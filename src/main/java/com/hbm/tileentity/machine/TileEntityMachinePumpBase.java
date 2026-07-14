package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.fluid.IFluidCopiable;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class TileEntityMachinePumpBase extends TileEntityLoadedBase implements IFluidStandardTransceiver, IBufPacketReceiver, IConfigurableMachine, IFluidCopiable {

    public static final Set<Block> validBlocks = new HashSet<>();

    static {
        validBlocks.add(Blocks.GRASS_BLOCK);
        validBlocks.add(Blocks.DIRT);
        validBlocks.add(Blocks.SAND);
        validBlocks.add(Blocks.MYCELIUM);
        validBlocks.add(ModBlocks.WASTE_EARTH.get());
        validBlocks.add(ModBlocks.DIRT_DEAD.get());
        validBlocks.add(ModBlocks.DIRT_OILY.get());
        validBlocks.add(ModBlocks.SAND_DIRTY.get());
        validBlocks.add(ModBlocks.SAND_DIRTY_RED.get());
    }

    public FluidTankHBM water;
    public boolean isOn = false;
    public float rotor = 0;
    public float lastRotor = 0;
    public boolean onGround = false;
    public int groundCheckDelay = 0;

    public static int groundHeight = 70;
    public static int groundDepth = 4;
    public static int steamSpeed = 1_000;
    public static int electricSpeed = 10_000;

    public TileEntityMachinePumpBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public String getConfigName() {
        return "waterpump";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        groundHeight = IConfigurableMachine.grab(obj, "I:groundHeight", groundHeight);
        groundDepth = IConfigurableMachine.grab(obj, "I:groundDepth", groundDepth);
        steamSpeed = IConfigurableMachine.grab(obj, "I:steamSpeed", steamSpeed);
        electricSpeed = IConfigurableMachine.grab(obj, "I:electricSpeed", electricSpeed);
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:groundHeight").value(groundHeight);
        writer.name("I:groundDepth").value(groundDepth);
        writer.name("I:steamSpeed").value(steamSpeed);
        writer.name("I:electricSpeed").value(electricSpeed);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            for (PosDir pos : getConPos()) {
                if (water.getFill() > 0) {
                    this.sendFluid(water, level, pos.pos(), pos.dir());
                }
            }

            if (groundCheckDelay > 0) {
                groundCheckDelay--;
            } else {
                onGround = checkGround();
                groundCheckDelay = 20;
            }

            isOn = false;
            if (canOperate() && worldPosition.getY() <= groundHeight && onGround) {
                isOn = true;
                operate();
            }

            this.networkPackNT(150);
        } else {
            lastRotor = rotor;
            if (isOn) {
                rotor += 10F;
                if (rotor >= 360F) {
                    rotor -= 360F;
                    lastRotor -= 360F;
                    playClientSound();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void playClientSound() {
        // будет переопределено в наследниках
    }

    protected boolean checkGround() {
        if (level == null) return false;
        if (level.dimension() == Level.NETHER || level.dimension() == Level.END) return false;

        int valid = 0;
        int invalid = 0;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y >= -groundDepth; y--) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = worldPosition.offset(x, y, z);
                    Block b = level.getBlockState(checkPos).getBlock();

                    if (y == -1 && !level.getBlockState(checkPos).isSolid()) return false;

                    if (validBlocks.contains(b)) valid++;
                    else invalid++;
                }
            }
        }
        return valid >= invalid;
    }

    protected abstract boolean canOperate();
    protected abstract void operate();

    protected PosDir[] getConPos() {
        return new PosDir[]{
                new PosDir(worldPosition.offset(2, 0, 0), Direction.EAST),
                new PosDir(worldPosition.offset(-2, 0, 0), Direction.WEST),
                new PosDir(worldPosition.offset(0, 0, 2), Direction.SOUTH),
                new PosDir(worldPosition.offset(0, 0, -2), Direction.NORTH)
        };
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeBoolean(isOn);
        buf.writeBoolean(onGround);
        water.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        isOn = buf.readBoolean();
        onGround = buf.readBoolean();
        water.deserialize(buf);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        water = new FluidTankHBM(water == null ? null : water.getTankType(), water == null ? 0 : water.getMaxFill());
        water.readFromNBT(nbt, "water");
        isOn = nbt.getBoolean("isOn");
        onGround = nbt.getBoolean("onGround");
        groundCheckDelay = nbt.getInt("groundCheckDelay");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        water.writeToNBT(nbt, "water");
        nbt.putBoolean("isOn", isOn);
        nbt.putBoolean("onGround", onGround);
        nbt.putInt("groundCheckDelay", groundCheckDelay);
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{water};
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[]{water};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[0];
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1,
                worldPosition.getY(),
                worldPosition.getZ() - 1,
                worldPosition.getX() + 2,
                worldPosition.getY() + 5,
                worldPosition.getZ() + 2
        );
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && type == water.getTankType();
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return null;
    }
}