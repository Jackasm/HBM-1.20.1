package com.hbm.tileentity.machine;

import com.hbm.api.fluid.IFluidStandardSender;
import com.hbm.api.fluid.IFluidCopiable;
import com.hbm.api.tile.IHeatSource;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.container.ContainerFurnaceCombination;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.recipes.CombinationRecipes;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachinePolluting;
import com.hbm.util.Tuple.Pair;
import com.hbm.util.HBMEnums;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import java.util.Objects;

public class TileEntityFurnaceCombination extends TileEntityMachinePolluting implements
        IFluidStandardSender, IFluidCopiable, IPersistentNBT, MenuProvider {

    public boolean wasOn;
    public int progress;
    public static final int processTime = 20_000;

    public int heat;
    public static final int maxHeat = 100_000;
    public static final double diffusion = 0.25D;

    public FluidTankHBM tank;

    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> CombinationRecipes.getOutput(stack) != null; // input
                case 1, 2, 3 -> false; // output и баки только для вывода
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> inventory);
    private final LazyOptional<FluidTankHBM> fluidHandlerCap;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> processTime;
                case 2 -> heat;
                case 3 -> maxHeat;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // только для чтения
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public IItemHandler getItemHandler() {
        return inventory;
    }

    public TileEntityFurnaceCombination(BlockPos pos, BlockState state) {
        super(ModTileEntity.FURNACE_COMBINATION.get(), pos, state, 50);
        this.tank = new FluidTankHBM(Fluids.NONE.get(), 24_000);
        this.fluidHandlerCap = LazyOptional.of(() -> tank);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.furnaceCombination");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerFurnaceCombination(windowId, playerInventory, player, this);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            this.tryPullHeat();

            if (this.level.getGameTime() % 20 == 0) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    Direction rot = dir.getClockWise();

                    for (int y = worldPosition.getY(); y <= worldPosition.getY() + 1; y++) {
                        for (int j = -1; j <= 1; j++) {
                            BlockPos fluidPos = new BlockPos(
                                    worldPosition.getX() + dir.getStepX() * 2 + rot.getStepX() * j,
                                    y,
                                    worldPosition.getZ() + dir.getStepZ() * 2 + rot.getStepZ() * j
                            );

                            if (tank.getFill() > 0) {
                                this.sendFluid(tank, level, fluidPos, dir);
                            }
                            this.sendSmoke(fluidPos, dir);
                        }
                    }
                }

                for (int x = worldPosition.getX() - 1; x <= worldPosition.getX() + 1; x++) {
                    for (int z = worldPosition.getZ() - 1; z <= worldPosition.getZ() + 1; z++) {
                        BlockPos upPos = new BlockPos(x, worldPosition.getY() + 2, z);
                        if (tank.getFill() > 0) {
                            this.sendFluid(tank, level, upPos, Direction.UP);
                        }
                        this.sendSmoke(upPos, Direction.UP);
                    }
                }
            }

            this.wasOn = false;

            tank.unloadTank(2, 3, toArray(inventory), inventory);

            if (canSmelt()) {
                int burn = heat / 100;

                if (burn > 0) {
                    this.wasOn = true;
                    this.progress += burn;
                    this.heat -= burn;

                    if (progress >= processTime) {
                        this.setChanged();
                        progress -= processTime;

                        ItemStack input = inventory.getStackInSlot(0);
                        Pair<ItemStack, FluidStackHBM> pair = CombinationRecipes.getOutput(input);
                        ItemStack out = pair.key();
                        FluidStackHBM fluid = pair.value();

                        if (out != null && !out.isEmpty()) {
                            ItemStack output = inventory.getStackInSlot(1);
                            if (output.isEmpty()) {
                                inventory.setStackInSlot(1, out.copy());
                            } else {
                                output.grow(out.getCount());
                            }
                        }

                        if (fluid != null && fluid.fill > 0) {
                            if (tank.getTankType() != fluid.type) {
                                tank.setType(fluid.type);
                            }

                            tank.setFill(tank.getFill() + fluid.fill);
                        }

                        input.shrink(1);
                    }

                    AABB aabb = new AABB(
                            worldPosition.getX() - 0.5, worldPosition.getY() + 2, worldPosition.getZ() - 0.5,
                            worldPosition.getX() + 1.5, worldPosition.getY() + 4, worldPosition.getZ() + 1.5
                    );
                    List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb);

                    for (Entity e : entities) e.setRemainingFireTicks(100);

                    if (level.getGameTime() % 10 == 0) {
                        level.playSound(null, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(),
                                net.minecraft.sounds.SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.25F, 0.5F);
                    }
                    if (level.getGameTime() % 20 == 0) {
                        this.pollute(PollutionType.SOOT, PollutionHandler.SOOT_PER_SECOND * 3);
                    }
                }
            } else {
                this.progress = 0;
            }

            this.networkPackNT(50);
        } else {
            if (this.wasOn && level.random.nextInt(15) == 0) {
                level.addParticle(ParticleTypes.LAVA,
                        worldPosition.getX() + 0.5 + level.random.nextGaussian() * 0.5,
                        worldPosition.getY() + 2,
                        worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 0.5,
                        0, 0, 0);
            }
        }
    }

    private ItemStack[] toArray(ItemStackHandler handler) {
        ItemStack[] array = new ItemStack[handler.getSlots()];
        for (int i = 0; i < handler.getSlots(); i++) {
            array[i] = handler.getStackInSlot(i);
        }
        return array;
    }

    protected void tryPullHeat() {
        if (this.heat >= maxHeat) return;

        BlockEntity con = Objects.requireNonNull(level).getBlockEntity(worldPosition.below());

        if (con instanceof IHeatSource source) {
            int diff = source.getHeatStored() - this.heat;

            if (diff == 0) return;

            if (diff > 0) {
                diff = (int) Math.ceil(diff * diffusion);
                source.useUpHeat(diff);
                this.heat += diff;
                if (this.heat > maxHeat)
                    this.heat = maxHeat;
                return;
            }
        }

        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    public boolean canSmelt() {
        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return false;

        Pair<ItemStack, FluidStackHBM> pair = CombinationRecipes.getOutput(input);
        if (pair == null) return false;

        ItemStack out = pair.key();
        FluidStackHBM fluid = pair.value();

        if (out != null && !out.isEmpty()) {
            ItemStack currentOut = inventory.getStackInSlot(1);
            if (!currentOut.isEmpty()) {
                if (!ItemStack.isSameItemSameTags(currentOut, out)) return false;
                if (currentOut.getCount() + out.getCount() > currentOut.getMaxStackSize()) return false;
            }
        }

        if (fluid != null && fluid.type != null && fluid.fill > 0) {
            if (tank.getTankType() != fluid.type && tank.getFill() > 0) return false;
            return tank.getTankType() != fluid.type || tank.getFill() + fluid.fill <= tank.getMaxFill();
        }

        return true;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(wasOn);
        buf.writeInt(heat);
        buf.writeInt(progress);
        tank.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        wasOn = buf.readBoolean();
        heat = buf.readInt();
        progress = buf.readInt();
        tank.deserialize(buf);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        tank.readFromNBT(nbt, "tank");
        progress = nbt.getInt("progress");
        heat = nbt.getInt("heat");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        tank.writeToNBT(nbt, "tank");
        nbt.putInt("progress", progress);
        nbt.putInt("heat", heat);
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        if (progress == 0 && heat == 0 && tank.getFill() == 0) return;
        CompoundTag data = new CompoundTag();
        data.putInt("progress", progress);
        data.putInt("heat", heat);
        tank.writeToNBT(data, "tank");
        nbt.put(NBT_PERSISTENT_KEY, data);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        CompoundTag data = nbt.getCompound(NBT_PERSISTENT_KEY);
        progress = data.getInt("progress");
        heat = data.getInt("heat");
        tank.readFromNBT(data, "tank");
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[]{tank, smoke, smokeLeaded, smokePoison};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && dir.getAxis() != Direction.Axis.Y;
    }

    public HBMEnums.ConnectionPriority getFluidPriority() {
        return HBMEnums.ConnectionPriority.NORMAL;
    }

    public long transferFluid(FluidTypeHBM type, int pressure, long fluid) {
        if (type != tank.getTankType()) return fluid;
        if (pressure != 0) return fluid;

        long toTransfer = Math.min(getDemand(type, pressure), fluid);
        tank.setFill(tank.getFill() + (int) toTransfer);
        return fluid - toTransfer;
    }

    public long getDemand(FluidTypeHBM type, int pressure) {
        if (type != tank.getTankType()) return 0;
        if (pressure != 0) return 0;
        return tank.getMaxFill() - tank.getFill();
    }

    @Override
    public int[] getFluidIDToCopy() {
        return new int[]{Fluids.getID(tank.getTankType())};
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return tank;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCap.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
        fluidHandlerCap.invalidate();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1,
                worldPosition.getY(),
                worldPosition.getZ() - 1,
                worldPosition.getX() + 2,
                worldPosition.getY() + 2.125,
                worldPosition.getZ() + 2
        );
    }
}