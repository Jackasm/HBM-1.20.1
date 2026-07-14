package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.api.tile.IHeatSource;
import com.hbm.blocks.BlockDummyable;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorStandard;
import com.hbm.explosion.vanillant.standard.ExplosionEffectStandard;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Heatable;

import com.hbm.sound.AudioWrapper;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.IFluidCopiable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;

import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class TileEntityHeatBoiler extends TileEntityLoadedBase implements IBufPacketReceiver, IFluidStandardTransceiver, IConfigurableMachine, IFluidCopiable {

    public int heat;
    public FluidTankHBM[] tanks;
    public boolean isOn;
    public boolean hasExploded = false;

    private AudioWrapper audio;
    private int audioTime;

    /* CONFIGURABLE */
    public static int maxHeat = 3_200_000;
    public static double diffusion = 0.1D;
    public static boolean canExplode = true;

    private ByteBuf buf;

    public TileEntityHeatBoiler(BlockPos pos, BlockState state) {
        super(ModTileEntity.HEAT_BOILER.get(), pos, state);
        this.tanks = new FluidTankHBM[2];
        this.tanks[0] = new FluidTankHBM(Fluids.WATER.get(), 16_000);
        this.tanks[1] = new FluidTankHBM(Fluids.STEAM.get(), 16_000 * 100);
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && (type == this.tanks[0].getTankType() || type == this.tanks[1].getTankType());
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            ByteBuf tempBuf = Unpooled.buffer();

            tempBuf.writeBoolean(this.hasExploded);

            if (!this.hasExploded) {
                this.setupTanks();
                this.updateConnections();
                this.tryPullHeat();

                tempBuf.writeInt(this.heat);

                this.tanks[0].serialize(tempBuf);
                this.isOn = false;
                this.tryConvert();
                this.tanks[1].serialize(tempBuf);

                if (this.tanks[1].getFill() > 0) {
                    this.sendFluid();
                }
            }

            tempBuf.writeBoolean(this.muffled);
            tempBuf.writeBoolean(this.isOn);

            if (this.buf != null) {
                this.buf.release();
            }
            this.buf = tempBuf;

            this.networkPackNT(25);
        } else {
            if (this.hasExploded) {
                if (audio != null) {
                    audio.stopSound();
                    audio = null;
                }
                audioTime = 0;
                return;
            }
            if (this.isOn) audioTime = 20;

            if (audioTime > 0) {
                audioTime--;

                if (audio == null) {
                    audio = this.createAudioLoop();
                    audio.startSound();
                } else if (!audio.isPlaying()) {
                    audio = this.rebootAudio(audio);
                }

                audio.updateVolume(this.getVolume(1.0F));
                audio.keepAlive();

            } else {
                if (audio != null) {
                    audio.stopSound();
                    audio = null;
                }
            }
        }
    }

    public AudioWrapper createAudioLoop() {
        return SoundHelper.createLoopedSound(ModSounds.BLOCK_BOILER.get(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 0.125F, 10F, 1.0F, 20);
    }

    @Override
    public void onChunkUnloaded() {
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(this.hasExploded);
        if (!this.hasExploded) {
            buf.writeInt(heat);
            tanks[0].serialize(buf);
            tanks[1].serialize(buf);
            buf.writeBoolean(muffled);
            buf.writeBoolean(isOn);
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.hasExploded = buf.readBoolean();
        if (!this.hasExploded) {
            this.heat = buf.readInt();
            this.tanks[0].deserialize(buf);
            this.tanks[1].deserialize(buf);
            this.muffled = buf.readBoolean();
            this.isOn = buf.readBoolean();
        }
    }

    protected void tryPullHeat() {
        if (level == null) return;

        // Находим потенциальный источник тепла
        IHeatSource source = null;
        BlockEntity con = level.getBlockEntity(worldPosition.below());

        if (con != null) {
            // Если это мультиблок, берём его ядро
            if (con.getBlockState().getBlock() instanceof BlockDummyable dummy) {
                BlockPos corePos = dummy.findCore(level, con.getBlockPos());
                if (corePos != null) {
                    BlockEntity core = level.getBlockEntity(corePos);
                    if (core instanceof IHeatSource heatSource) {
                        source = heatSource;
                    }
                }
            }
            // Если это не мультиблок или ядро не нашлось, проверяем сам блок
            if (source == null && con instanceof IHeatSource heatSource) {
                source = heatSource;
            }
        }

        // Если нашли источник, работаем с ним
        if (source != null) {
            int diff = source.getHeatStored() - this.heat;
            if (diff > 0) {
                diff = (int) Math.ceil(diff * diffusion);
                diff = Math.min(diff, maxHeat - this.heat);
                source.useUpHeat(diff);
                this.heat += diff;
                if (this.heat > maxHeat) this.heat = maxHeat;
                return;
            }
        }

        // Остывание
        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    protected void setupTanks() {
        if (tanks[0].getTankType().hasTrait(FT_Heatable.class)) {
            FT_Heatable trait = tanks[0].getTankType().getTrait(FT_Heatable.class);
            if (trait.getEfficiency(FT_Heatable.HeatingType.BOILER) > 0) {
                FT_Heatable.HeatingStep entry = trait.getFirstStep();
                tanks[1].setType(entry.typeProduced);
                tanks[1].changeTankSize(tanks[0].getMaxFill() * entry.amountProduced / entry.amountReq);
                return;
            }
        }

        tanks[0].setType(Fluids.NONE.get());
        tanks[1].setType(Fluids.NONE.get());
    }

    protected void tryConvert() {
        if (tanks[0].getTankType().hasTrait(FT_Heatable.class)) {
            FT_Heatable trait = tanks[0].getTankType().getTrait(FT_Heatable.class);
            if (trait.getEfficiency(FT_Heatable.HeatingType.BOILER) > 0) {

                FT_Heatable.HeatingStep entry = trait.getFirstStep();
                int inputOps = this.tanks[0].getFill() / entry.amountReq;
                int outputOps = (this.tanks[1].getMaxFill() - this.tanks[1].getFill()) / entry.amountProduced;
                int heatOps = this.heat / entry.heatReq;

                int ops = Math.min(inputOps, Math.min(outputOps, heatOps));

                this.tanks[0].setFill(this.tanks[0].getFill() - entry.amountReq * ops);
                this.tanks[1].setFill(this.tanks[1].getFill() + entry.amountProduced * ops);
                this.heat -= entry.heatReq * ops;

                if (ops > 0 && Objects.requireNonNull(level).random.nextInt(400) == 0) {
                    level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 2, worldPosition.getZ() + 0.5,
                            ModSounds.BOILER_GROAN.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
                }

                if (ops > 0) {
                    this.isOn = true;
                }

                if (outputOps == 0 && canExplode) {
                    this.hasExploded = true;
                    BlockDummyable.safeRem = true;
                    for (int x = worldPosition.getX() - 1; x <= worldPosition.getX() + 1; x++) {
                        for (int y = worldPosition.getY() + 2; y <= worldPosition.getY() + 3; y++) {
                            for (int z = worldPosition.getZ() - 1; z <= worldPosition.getZ() + 1; z++) {
                                Objects.requireNonNull(level).setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                            }
                        }
                    }
                    Objects.requireNonNull(level).setBlock(worldPosition.above(), Blocks.AIR.defaultBlockState(), 3);

                    ExplosionVNT xnt = new ExplosionVNT(level, worldPosition.getX() + 0.5, worldPosition.getY() + 2, worldPosition.getZ() + 0.5, 5F);
                    xnt.setEntityProcessor(new EntityProcessorStandard().withRangeMod(3F));
                    xnt.setPlayerProcessor(new PlayerProcessorStandard());
                    xnt.setSFX(new ExplosionEffectStandard());
                    xnt.explode();

                    BlockDummyable.safeRem = false;
                }
            }
        }
    }

    private void updateConnections() {
        for (Library.PosDir pos : getConPos()) {
            this.trySubscribe(tanks[0].getTankType(), level, pos.pos(), pos.dir());
        }
    }

    private void sendFluid() {
        for (Library.PosDir pos : getConPos()) {
            this.sendFluid(tanks[1], level, pos.pos(), pos.dir().getOpposite());
        }
    }

    private Library.PosDir[] getConPos() {
        Direction dir = this.getBlockState().getValue(BlockDummyable.FACING).getClockWise();
        return new Library.PosDir[]{
                new Library.PosDir(worldPosition.offset(dir.getStepX() * 2, 0, dir.getStepZ() * 2), dir),
                new Library.PosDir(worldPosition.offset(-dir.getStepX() * 2, 0, -dir.getStepZ() * 2), dir.getOpposite()),
                new Library.PosDir(worldPosition.offset(0, 4, 0), Direction.UP),
        };
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        tanks[0].readFromNBT(nbt, "water");
        tanks[1].readFromNBT(nbt, "steam");
        heat = nbt.getInt("heat");
        hasExploded = nbt.getBoolean("exploded");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        tanks[0].writeToNBT(nbt, "water");
        tanks[1].writeToNBT(nbt, "steam");
        nbt.putInt("heat", heat);
        nbt.putBoolean("exploded", hasExploded);
    }


    public FluidTankHBM[] getAllTanks() {
        return tanks;
    }

    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[]{tanks[1]};
    }

    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tanks[0]};
    }

    private AABB renderBB = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBB == null) {
            renderBB = new AABB(
                    worldPosition.getX() - 1,
                    worldPosition.getY(),
                    worldPosition.getZ() - 1,
                    worldPosition.getX() + 2,
                    worldPosition.getY() + 4,
                    worldPosition.getZ() + 2
            );
        }
        return renderBB;
    }

    @Override
    public String getConfigName() {
        return "boiler";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        maxHeat = IConfigurableMachine.grab(obj, "I:maxHeat", maxHeat);
        diffusion = IConfigurableMachine.grab(obj, "D:diffusion", diffusion);
        canExplode = IConfigurableMachine.grab(obj, "B:canExplode", canExplode);
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:maxHeat").value(maxHeat);
        writer.name("D:diffusion").value(diffusion);
        writer.name("B:canExplode").value(canExplode);
    }
}