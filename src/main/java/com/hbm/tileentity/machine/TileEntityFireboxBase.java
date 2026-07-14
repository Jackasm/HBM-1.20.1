package com.hbm.tileentity.machine;

import com.hbm.api.fluid.IFluidStandardSender;
import com.hbm.api.tile.IHeatSource;
import com.hbm.blocks.BlockDummyable;
import com.hbm.handler.pollution.PollutionHandler;

import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.container.ContainerFirebox;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.tank.FluidTankHBM;

import com.hbm.module.ModuleBurnTime;
import com.hbm.tileentity.TileEntityMachinePolluting;
import com.hbm.util.HBMEnums;
import com.hbm.util.ItemStackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class TileEntityFireboxBase extends TileEntityMachinePolluting
        implements IFluidStandardSender, IHeatSource, MenuProvider{

    public int maxBurnTime;
    public int burnTime;
    public int burnHeat;
    public boolean wasOn = false;
    private int playersUsing = 0;

    public float doorAngle = 0;
    public float prevDoorAngle = 0;

    public int heatEnergy;

    public TileEntityFireboxBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 50);
    }

    protected ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            // Отправка дыма
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Direction rot = dir.getClockWise();

                for (int j = -1; j <= 1; j++) {
                    BlockPos smokePos = worldPosition.offset(dir.getStepX() * 2 + rot.getStepX() * j, 0, dir.getStepZ() * 2 + rot.getStepZ() * j);
                    this.sendSmoke(smokePos, dir);
                }
            }

            wasOn = false;

            if (burnTime <= 0) {
                for (int i = 0; i < 2; i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty()) {

                        int baseTime = getModule().getBurnTime(stack);

                        if (baseTime > 0) {
                            int fuel = (int) (baseTime * getTimeMult());

                            BlockEntity below = level.getBlockEntity(worldPosition.below());

                            if (below instanceof TileEntityAshpit ashpit) {
                                HBMEnums.EnumAshType type = getAshFromFuel(stack);
                                if (type == HBMEnums.EnumAshType.WOOD) ashpit.ashLevelWood += baseTime;
                                if (type == HBMEnums.EnumAshType.COAL) ashpit.ashLevelCoal += baseTime;
                                if (type == HBMEnums.EnumAshType.MISC) ashpit.ashLevelMisc += baseTime;
                            }

                            this.maxBurnTime = this.burnTime = fuel;
                            this.burnHeat = getModule().getBurnHeat(getBaseHeat(), stack);
                            stack.shrink(1);

                            if (stack.isEmpty()) {
                                inventory.setStackInSlot(i, stack.getCraftingRemainingItem());
                            }

                            this.wasOn = true;
                            break;
                        }
                    }
                }
            } else {
                if (this.heatEnergy < getMaxHeat()) {
                    burnTime--;
                    if (level.getGameTime() % 20 == 0)
                        this.pollute(PollutionType.SOOT, PollutionHandler.SOOT_PER_SECOND * 3);
                }
                this.wasOn = true;

                if (level.random.nextInt(15) == 0 && !this.muffled) {
                    level.playSound(null, worldPosition, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.5F + level.random.nextFloat() * 0.5F);
                }
            }

            if (wasOn) {
                this.heatEnergy = Math.min(this.heatEnergy + this.burnHeat, getMaxHeat());
            } else {
                this.heatEnergy = Math.max(this.heatEnergy - Math.max(this.heatEnergy / 1000, 1), 0);
                this.burnHeat = 0;
            }

            this.networkPackNT(50);
        } else {
            this.prevDoorAngle = this.doorAngle;
            float swingSpeed = (doorAngle / 10F) + 3;

            if (this.playersUsing > 0) {
                this.doorAngle += swingSpeed;
            } else {
                this.doorAngle -= swingSpeed;
            }

            this.doorAngle = Mth.clamp(this.doorAngle, 0F, 135F);

            if (wasOn && level.getGameTime() % 5 == 0) {
                if (level instanceof ServerLevel serverLevel) {
                    Direction dir = getBlockState().getValue(BlockDummyable.FACING);
                    double x = worldPosition.getX() + 0.5 + dir.getStepX();
                    double y = worldPosition.getY() + 0.25;
                    double z = worldPosition.getZ() + 0.5 + dir.getStepZ();
                    serverLevel.sendParticles(ParticleTypes.FLAME,
                            x + level.random.nextDouble() * 0.5 - 0.25,
                            y + level.random.nextDouble() * 0.25,
                            z + level.random.nextDouble() * 0.5 - 0.25,
                            1, 0, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(maxBurnTime);
        buf.writeInt(burnTime);
        buf.writeInt(burnHeat);
        buf.writeInt(heatEnergy);
        buf.writeInt(playersUsing);
        buf.writeBoolean(wasOn);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        maxBurnTime = buf.readInt();
        burnTime = buf.readInt();
        burnHeat = buf.readInt();
        heatEnergy = buf.readInt();
        playersUsing = buf.readInt();
        wasOn = buf.readBoolean();
    }

    public static HBMEnums.EnumAshType getAshFromFuel(ItemStack stack) {
        List<String> names = ItemStackUtil.getTagNames(stack);

        for (String name : names) {
            if (name.contains("coke")) return HBMEnums.EnumAshType.COAL;
            if (name.contains("coal")) return HBMEnums.EnumAshType.COAL;
            if (name.contains("lignite")) return HBMEnums.EnumAshType.COAL;
            if (name.startsWith("log")) return HBMEnums.EnumAshType.WOOD;
            if (name.contains("wood")) return HBMEnums.EnumAshType.WOOD;
            if (name.contains("sapling")) return HBMEnums.EnumAshType.WOOD;
        }

        return HBMEnums.EnumAshType.MISC;
    }

    public abstract ModuleBurnTime getModule();

    public abstract int getBaseHeat();

    public abstract double getTimeMult();

    public abstract int getMaxHeat();


    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        this.maxBurnTime = nbt.getInt("maxBurnTime");
        this.burnTime = nbt.getInt("burnTime");
        this.burnHeat = nbt.getInt("burnHeat");
        this.heatEnergy = nbt.getInt("heatEnergy");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putInt("maxBurnTime", maxBurnTime);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("burnHeat", burnHeat);
        nbt.putInt("heatEnergy", heatEnergy);
    }

    @Override
    public int getHeatStored() {
        return heatEnergy;
    }

    @Override
    public void useUpHeat(int heat) {
        this.heatEnergy = Math.max(0, this.heatEnergy - heat);
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
                    worldPosition.getY() + 1,
                    worldPosition.getZ() + 2
            );
        }
        return renderBB;
    }


    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[0];
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return this.getSmokeTanks();
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != Direction.DOWN;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        openInventory();
        return new ContainerFirebox(windowId, inv, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    public void closeInventory() {
        this.playersUsing = Math.max(0, this.playersUsing - 1);
        this.setChanged();
    }

    public void openInventory() {
        this.playersUsing++;
        this.setChanged();
    }
}