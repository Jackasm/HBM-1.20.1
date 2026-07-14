package com.hbm.tileentity.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.IControlReceiver;

import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.container.ContainerMachineSolderingStation;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;

import com.hbm.inventory.recipes.SolderingRecipes;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.machine.ItemMachineUpgrade;
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

public class TileEntityMachineSolderingStation extends TileEntityMachineBase implements MenuProvider, IEnergyReceiver, IFluidStandardReceiver, IControlReceiver, IUpgradeInfoProvider, IFluidCopiable{

    public long power;
    public long maxPower = 2_000;
    public long consumption;
    public boolean collisionPrevention = false;

    public int progress;
    public int processTime = 1;

    public FluidTankHBM tank;
    public ItemStack display;

    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();

    private final ItemStackHandler itemHandler = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 7) {
                return stack.getItem() instanceof IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get();
            }

            if (slot == 8) {
                return stack.getItem() instanceof IItemFluidIdentifier;
            }

            if (slot == 9 || slot == 10) {
                return stack.getItem() instanceof ItemMachineUpgrade;
            }

            if (slot < 3) {
                for (int i = 0; i < 3; i++) {
                    if (i != slot && getStackInSlot(i) != null && ItemStack.isSameItemSameTags(getStackInSlot(i), stack)) return false;
                }
                for (AStack t : SolderingRecipes.toppings) {
                    if (t.matchesRecipe(stack, true)) return true;
                }
            } else if (slot < 5) {
                for (int i = 3; i < 5; i++) {
                    if (i != slot && getStackInSlot(i) != null && ItemStack.isSameItemSameTags(getStackInSlot(i), stack)) return false;
                }
                for (AStack t : SolderingRecipes.pcb) {
                    if (t.matchesRecipe(stack, true)) return true;
                }
            } else if (slot < 6) {
                for (int i = 5; i < 6; i++) {
                    if (i != slot && getStackInSlot(i) != null && ItemStack.isSameItemSameTags(getStackInSlot(i), stack)) return false;
                }
                for (AStack t : SolderingRecipes.solder) {
                    if (t.matchesRecipe(stack, true)) return true;
                }
            }
            return false;
        }
    };

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> itemHandler);

    private SolderingRecipes.SolderingRecipe recipe;

    public TileEntityMachineSolderingStation(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_SOLDERING_STATION.get(), pos, state);
        this.tank = new FluidTankHBM(Fluids.NONE.get(), 8_000);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            // Зарядка от предметов
            this.power = Library.chargeTEFromItems(itemHandler, 7, this.power, this.maxPower);
            this.tank.setType(8, 8, getSlots(),itemHandler);

            if (level.getGameTime() % 20 == 0) {
                for (PosDir pos : getConPos()) {
                    this.trySubscribe(level, pos);
                    if (tank.getTankType() != Fluids.NONE.get()) {
                        this.trySubscribe(tank.getTankType(), level, pos.pos(), pos.dir());
                    }
                }
            }

            recipe = SolderingRecipes.getRecipe(new ItemStack[]{
                    itemHandler.getStackInSlot(0), itemHandler.getStackInSlot(1), itemHandler.getStackInSlot(2),
                    itemHandler.getStackInSlot(3), itemHandler.getStackInSlot(4), itemHandler.getStackInSlot(5)
            });
            long intendedMaxPower;

            upgradeManager.checkSlots(this, itemHandler, 9, 10);
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

                        ItemStack outputSlot = itemHandler.getStackInSlot(6);
                        if (outputSlot.isEmpty()) {
                            itemHandler.setStackInSlot(6, recipe.output.copy());
                        } else {
                            outputSlot.grow(recipe.output.getCount());
                        }
                        setChanged();
                    }

                    if (level.getGameTime() % 20 == 0) {
                        Direction dir = getBlockState().getValue(FACING);
                        Direction rot = dir.getClockWise();
                        CompoundTag dPart = new CompoundTag();
                        dPart.putString("type", "tau");
                        dPart.putByte("count", (byte) 3);
                        PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(dPart,
                                        worldPosition.getX() + 0.5 - dir.getStepX() * 0.5 + rot.getStepX() * 0.5,
                                        worldPosition.getY() + 1.125,
                                        worldPosition.getZ() + 0.5 - dir.getStepZ() * 0.5 + rot.getStepZ() * 0.5),
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
        ItemStack[] slots = new ItemStack[11];
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            slots[i] = itemHandler.getStackInSlot(i);
        }
        return slots;
    }

    private boolean canProcess(SolderingRecipes.SolderingRecipe recipe) {
        if (this.power < this.consumption) return false;

        if (recipe.fluid != null) {
            if (this.tank.getTankType() != recipe.fluid.type) return false;
            if (this.tank.getFill() < recipe.fluid.fill) return false;
        }

        if (collisionPrevention && recipe.fluid == null && this.tank.getFill() > 0) return false;

        ItemStack outputSlot = itemHandler.getStackInSlot(6);
        if (!outputSlot.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(outputSlot, recipe.output)) return false;
            return outputSlot.getCount() + recipe.output.getCount() <= outputSlot.getMaxStackSize();
        }

        return true;
    }

    private void consumeItems(SolderingRecipes.SolderingRecipe recipe) {
        for (AStack aStack : recipe.toppings) {
            for (int i = 0; i < 3; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (aStack.matchesRecipe(stack, true) && stack.getCount() >= aStack.stacksize) {
                    itemHandler.extractItem(i, aStack.stacksize, false);
                    break;
                }
            }
        }

        for (AStack aStack : recipe.pcb) {
            for (int i = 3; i < 5; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (aStack.matchesRecipe(stack, true) && stack.getCount() >= aStack.stacksize) {
                    itemHandler.extractItem(i, aStack.stacksize, false);
                    break;
                }
            }
        }

        for (AStack aStack : recipe.solder) {
            for (int i = 5; i < 6; i++) {
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
        list.add(new PosDir(worldPosition.relative(dir, -2), dir.getOpposite()));
        list.add(new PosDir(worldPosition.relative(dir, -2).relative(rot, 1), dir.getOpposite()));
        list.add(new PosDir(worldPosition.relative(rot, -1), rot.getOpposite()));
        list.add(new PosDir(worldPosition.relative(dir, -1).relative(rot, -1), rot.getOpposite()));
        list.add(new PosDir(worldPosition.relative(rot, 2), rot));
        list.add(new PosDir(worldPosition.relative(dir, -1).relative(rot, 2), rot));
        return list;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(this.power);
        buf.writeLong(this.maxPower);
        buf.writeLong(this.consumption);
        buf.writeInt(this.progress);
        buf.writeInt(this.processTime);
        buf.writeBoolean(this.collisionPrevention);
        this.tank.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.power = buf.readLong();
        this.maxPower = buf.readLong();
        this.consumption = buf.readLong();
        this.progress = buf.readInt();
        this.processTime = buf.readInt();
        this.collisionPrevention = buf.readBoolean();
        this.display = null;
        this.tank.deserialize(buf);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putLong("power", power);
        nbt.putLong("maxPower", maxPower);
        nbt.putInt("progress", progress);
        nbt.putInt("processTime", processTime);
        nbt.putBoolean("collisionPrevention", collisionPrevention);
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
        this.collisionPrevention = nbt.getBoolean("collisionPrevention");
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
        return new ContainerMachineSolderingStation(windowId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.machineSolderingStation");
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
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_SOLDERING_STATION.get()));

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
    public HashMap<ItemMachineUpgrade.UpgradeType, Integer> getValidUpgrades() {
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

    @Override
    public boolean hasPermission(Player player) {
        return isUsableByPlayer(player);
    }

    @Override
    public void receiveControl(CompoundTag data) {
        this.collisionPrevention = !this.collisionPrevention;
        setChanged();
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && (type == tank.getTankType());
    }

    public ItemStack getCurrentDisplay() {
        if (recipe != null && recipe.output != null) {
            return recipe.output;
        }
        return ItemStack.EMPTY;
    }
}