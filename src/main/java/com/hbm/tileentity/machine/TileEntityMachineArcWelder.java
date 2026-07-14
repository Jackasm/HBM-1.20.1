package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.container.ContainerMachineArcWelder;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.recipes.ArcWelderRecipes;
import com.hbm.inventory.recipes.ArcWelderRecipes.ArcWelderRecipe;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.*;
import com.hbm.util.BobMathUtil;
import com.hbm.util.Library;
import com.hbm.util.Library.PosDir;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hbm.blocks.BlockDummyable.FACING;

public class TileEntityMachineArcWelder extends TileEntityMachineBase implements IEnergyReceiver, IFluidStandardReceiver, IUpgradeInfoProvider, IFluidCopiable, MenuProvider {

    public long power;
    public long maxPower = 2_000;
    public long consumption;

    public int progress;
    public int processTime = 1;

    public FluidTankHBM tank;
    public ItemStack display;

    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();

    private final ItemStackHandler itemHandler = new ItemStackHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // Слоты 0-2 для ингредиентов
            if (slot < 3) {
                for (AStack t : ArcWelderRecipes.ingredients) {
                    if (t.matchesRecipe(stack, true)) return true;
                }
            }
            // Слот 3 - выход (нельзя класть)
            if (slot == 3) return false;
            // Слот 4 - батарея
            // Слот 5 - идентификатор жидкости
            // Слоты 6-7 - апгрейды
            return slot >= 4;
        }
    };

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> itemHandler);

    public TileEntityMachineArcWelder(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_ARC_WELDER.get(), pos, state);
        this.tank = new FluidTankHBM(Fluids.NONE.get(), 24_000);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            // Зарядка от предметов
            this.power = Library.chargeTEFromItems(itemHandler, 4, this.power, this.maxPower);
            this.tank.setType(5, 5,getSlots() ,itemHandler);

            if (level.getGameTime() % 20 == 0) {
                for (PosDir pos : getConPos()) {
                    this.trySubscribe(level, pos);
                    if (tank.getTankType() != Fluids.NONE.get()) {
                        this.trySubscribe(tank.getTankType(), level, pos.pos(), pos.dir());
                    }
                }
            }

            ArcWelderRecipe recipe = ArcWelderRecipes.getRecipe(
                    itemHandler.getStackInSlot(0),
                    itemHandler.getStackInSlot(1),
                    itemHandler.getStackInSlot(2)
            );
            long intendedMaxPower;

            upgradeManager.checkSlots(this, itemHandler, 6, 7);
            int redLevel = upgradeManager.getLevel(UpgradeType.SPEED);
            int blueLevel = upgradeManager.getLevel(UpgradeType.POWER);
            int blackLevel = upgradeManager.getLevel(UpgradeType.OVERDRIVE);

            if (recipe != null) {
                this.processTime = recipe.duration - (recipe.duration * redLevel / 6) + (recipe.duration * blueLevel / 3);
                this.consumption = recipe.consumption + (recipe.consumption * redLevel) - (recipe.consumption * blueLevel / 6);
                this.consumption *= (long) Math.pow(2, blackLevel);
                intendedMaxPower = consumption * 20;

                if (canProcess(recipe)) {
                    this.progress += (1 + blackLevel);
                    this.power -= this.consumption;

                    if (progress >= processTime) {
                        this.progress = 0;
                        this.consumeItems(recipe);

                        ItemStack outputSlot = itemHandler.getStackInSlot(3);
                        if (outputSlot.isEmpty()) {
                            itemHandler.setStackInSlot(3, recipe.output.copy());
                        } else {
                            outputSlot.grow(recipe.output.getCount());
                        }
                        setChanged();
                    }

                    if (level.getGameTime() % 2 == 0) {
                        Direction dir = getBlockState().getValue(FACING);
                        CompoundTag dPart = new CompoundTag();
                        dPart.putString("type", level.getGameTime() % 20 == 0 ? "tau" : "hadron");
                        dPart.putByte("count", (byte) 5);
                        PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(dPart,
                                        worldPosition.getX() + 0.5 - dir.getStepX() * 0.5,
                                        worldPosition.getY() + 1.25,
                                        worldPosition.getZ() + 0.5 - dir.getStepZ() * 0.5),
                                level, worldPosition, 25);
                    }

                } else {
                    this.progress = 0;
                }

            } else {
                this.progress = 0;
                this.consumption = 100;
                intendedMaxPower = 2000;
            }

            this.maxPower = Math.max(intendedMaxPower, power);

            this.networkPackNT(25);
        }
    }

    private ItemStack[] getSlots() {
        ItemStack[] slots = new ItemStack[itemHandler.getSlots()];
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            slots[i] = itemHandler.getStackInSlot(i);
        }
        return slots;
    }

    private boolean canProcess(ArcWelderRecipe recipe) {
        if (this.power < this.consumption) return false;

        if (recipe.fluid != null) {
            if (this.tank.getTankType() != recipe.fluid.type) return false;
            if (this.tank.getFill() < recipe.fluid.fill) return false;
        }

        ItemStack outputSlot = itemHandler.getStackInSlot(3);
        if (!outputSlot.isEmpty()) {
            if (outputSlot.getItem() != recipe.output.getItem()) return false;
            return outputSlot.getCount() + recipe.output.getCount() <= outputSlot.getMaxStackSize();
        }

        return true;
    }

    private void consumeItems(ArcWelderRecipe recipe) {
        for (AStack aStack : recipe.ingredients) {
            for (int i = 0; i < 3; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (aStack.matchesRecipe(stack, true) && stack.getCount() >= aStack.stacksize) {
                    itemHandler.extractItem(i, aStack.stacksize, false);
                    break;
                }
            }
        }

        if (recipe.fluid != null) {
            this.tank.setFill(tank.getFill() - recipe.fluid.fill);
        }
    }

    private List<PosDir> getConPos() {
        Direction dir = getBlockState().getValue(FACING);
        Direction rot = dir.getClockWise();

        List<PosDir> list = new ArrayList<>();
        list.add(new PosDir(worldPosition.relative(dir, 1), dir));
        list.add(new PosDir(worldPosition.relative(dir, 1).relative(rot, 1), dir));
        list.add(new PosDir(worldPosition.relative(dir, 1).relative(rot, -1), dir));
        list.add(new PosDir(worldPosition.relative(dir, -2), dir.getOpposite()));
        list.add(new PosDir(worldPosition.relative(dir, -2).relative(rot, 1), dir.getOpposite()));
        list.add(new PosDir(worldPosition.relative(dir, -2).relative(rot, -1), dir.getOpposite()));
        list.add(new PosDir(worldPosition.relative(rot, 2), rot));
        list.add(new PosDir(worldPosition.relative(dir, -1).relative(rot, 2), rot));
        list.add(new PosDir(worldPosition.relative(rot, -2), rot.getOpposite()));
        list.add(new PosDir(worldPosition.relative(dir, -1).relative(rot, -2), rot.getOpposite()));
        return list;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeLong(maxPower);
        buf.writeLong(consumption);
        buf.writeInt(progress);
        buf.writeInt(processTime);
        this.tank.serialize(buf);
        // display не сериализуем
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        maxPower = buf.readLong();
        consumption = buf.readLong();
        progress = buf.readInt();
        processTime = buf.readInt();
        this.tank.deserialize(buf);
        this.display = null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putLong("power", power);
        nbt.putLong("maxPower", maxPower);
        nbt.putInt("progress", progress);
        nbt.putInt("processTime", processTime);
        tank.writeToNBT(nbt, "t");
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        this.power = nbt.getLong("power");
        this.maxPower = nbt.getLong("maxPower");
        this.progress = nbt.getInt("progress");
        this.processTime = nbt.getInt("processTime");
        tank.readFromNBT(nbt, "t");
    }

    @Override
    public long getPower() {
        return Math.max(Math.min(power, maxPower), 0);
    }

    @Override
    public void setPower(long power) {
        this.power = power;
    }

    @Override
    public long getMaxPower() {
        return maxPower;
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && (type == tank.getTankType());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1,
                worldPosition.getX() + 2, worldPosition.getY() + 3, worldPosition.getZ() + 2);
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerMachineArcWelder(windowId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.machineArcWelder");
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean canProvideInfo(UpgradeType type, int level, boolean extendedInfo) {
        return type == UpgradeType.SPEED || type == UpgradeType.POWER || type == UpgradeType.OVERDRIVE;
    }

    @Override
    public void provideInfo(UpgradeType type, int level, List<Component> info, boolean extendedInfo) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_ARC_WELDER.get()));

        if (type == UpgradeType.SPEED) {
            info.add(Component.literal("-" + (level * 100 / 6) + "%")
                    .withStyle(ChatFormatting.GREEN));
            info.add(Component.translatable(KEY_CONSUMPTION, "+" + (level * 100) + "%")
                    .withStyle(ChatFormatting.RED));
        }
        if (type == UpgradeType.POWER) {
            info.add(Component.translatable(KEY_CONSUMPTION, "-" + (level * 100 / 6) + "%")
                    .withStyle(ChatFormatting.GREEN));
            info.add(Component.translatable(KEY_DELAY, "+" + (level * 100 / 3) + "%")
                    .withStyle(ChatFormatting.RED));
        }
        if (type == UpgradeType.OVERDRIVE) {
            info.add(Component.literal("YES")
                    .withStyle(BobMathUtil.getBlink() ? ChatFormatting.RED : ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public HashMap<UpgradeType, Integer> getValidUpgrades() {
        HashMap<UpgradeType, Integer> upgrades = new HashMap<>();
        upgrades.put(UpgradeType.SPEED, 3);
        upgrades.put(UpgradeType.POWER, 3);
        upgrades.put(UpgradeType.OVERDRIVE, 3);
        return upgrades;
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return tank;
    }

    public ItemStack getCurrentDisplay() {
        return display != null ? display : ItemStack.EMPTY;
    }
}