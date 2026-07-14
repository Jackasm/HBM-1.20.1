package com.hbm.tileentity.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.container.ContainerCrystallizer;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.recipes.CrystallizerRecipes;
import com.hbm.inventory.recipes.CrystallizerRecipes.CrystallizerRecipe;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.tileentity.IFluidCopiable;
import com.hbm.tileentity.IUpgradeInfoProvider;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
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
import java.util.Objects;

import static com.hbm.blocks.BlockDummyable.FACING;

public class TileEntityMachineCrystallizer extends TileEntityMachineBase implements MenuProvider, IEnergyReceiver, IFluidStandardReceiver, IControlReceiver, IUpgradeInfoProvider, IFluidCopiable {

    public boolean collisionPrevention = false;

    public long power;
    public static final long MAX_POWER = 1000000;
    public static final int DEMAND = 1000;
    public short progress;
    public short duration = 600;
    public boolean isOn;

    public float angle;
    public float prevAngle;

    public FluidTankHBM tank;

    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();

    private final ItemStackHandler inventory = new ItemStackHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return CrystallizerRecipes.getOutput(stack, tank.getTankType()) != null;
            }
            if (slot == 1) {
                return stack.getItem() instanceof IBatteryItem;
            }
            if (slot >= 5 && slot <= 6) {
                return stack.getItem() instanceof ItemMachineUpgrade;
            }
            if (slot == 7) {
                return stack.getItem() instanceof IItemFluidIdentifier;
            }
            return false;
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineCrystallizer(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_CRYSTALLIZER.get(), pos, state);
        tank = new FluidTankHBM(Fluids.PEROXIDE.get(), 8000);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            updateConnections();
            this.isOn = false;

            // Зарядка от батареи
            this.power = Library.chargeTEFromItems(inventory, 1, this.power, MAX_POWER);

            // Настройка типа жидкости из слота идентификатора
            this.tank.setType(7, 7, getSlots(), inventory);

            // Загрузка жидкости из слота 3 в бак
            this.tank.loadTank(3, 4, getSlots(), inventory);

            upgradeManager.checkSlots(this, inventory, 5, 6);

            for (int i = 0; i < getCycleCount(); i++) {
                if (canProcess()) {
                    progress++;
                    power -= getPowerRequired();
                    isOn = true;

                    if (progress > getDuration()) {
                        progress = 0;
                        processItem();
                        setChanged();
                    }
                } else {
                    progress = 0;
                }
            }

            setChanged();
            networkPackNT(25);
        } else {
            prevAngle = angle;

            if (isOn) {
                angle += 5F * this.getCycleCount();

                if (angle >= 360) {
                    angle -= 360;
                    prevAngle -= 360;
                }

                if (level.random.nextInt(20) == 0) {
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD,
                            worldPosition.getX() + level.random.nextDouble(),
                            worldPosition.getY() + 6.5D,
                            worldPosition.getZ() + level.random.nextDouble(),
                            0.0, 0.1, 0.0);
                }
            }
        }

        // Лестница для игроков
        Direction dir = getBlockState().getValue(FACING);
        Direction rot = dir.getClockWise();

        AABB box = new AABB(
                worldPosition.getX() + 0.25 + rot.getStepX() * 1.5,
                worldPosition.getY() + 1,
                worldPosition.getZ() + 0.25 + rot.getStepZ() * 1.5,
                worldPosition.getX() + 0.75 + rot.getStepX() * 1.5,
                worldPosition.getY() + 6,
                worldPosition.getZ() + 0.75 + rot.getStepZ() * 1.5
        );

        List<Player> players = level.getEntitiesOfClass(Player.class, box);
        for (Player player : players) {
            var props = HbmPlayerProps.getData(player);
            if (props != null) props.setIsOnLadder(true);
        }
    }

    private ItemStack[] getSlots() {
        ItemStack[] slots = new ItemStack[inventory.getSlots()];
        for (int i = 0; i < inventory.getSlots(); i++) {
            slots[i] = inventory.getStackInSlot(i);
        }
        return slots;
    }

    private void updateConnections() {
        for (PosDir pos : getConPos()) {
            this.trySubscribe(level, pos.pos(), pos.dir());
            this.trySubscribe(tank.getTankType(), level, pos.pos(), pos.dir());
        }
    }

    private List<PosDir> getConPos() {
        List<PosDir> list = new ArrayList<>();
        Direction facing = getBlockState().getValue(FACING);
        Direction front = facing;
        Direction back = facing.getOpposite();
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        list.add(new PosDir(worldPosition.relative(front, 2).relative(left, 1), front));
        list.add(new PosDir(worldPosition.relative(front, 2).relative(right, 1), front));
        list.add(new PosDir(worldPosition.relative(back, 2).relative(left, 1), back));
        list.add(new PosDir(worldPosition.relative(back, 2).relative(right, 1), back));
        list.add(new PosDir(worldPosition.relative(front, 1).relative(left, 2), left));
        list.add(new PosDir(worldPosition.relative(front, 1).relative(right, 2), right));
        list.add(new PosDir(worldPosition.relative(back, 1).relative(left, 2), left));
        list.add(new PosDir(worldPosition.relative(back, 1).relative(right, 2), right));

        return list;
    }

    private void processItem() {
        CrystallizerRecipe result = CrystallizerRecipes.getOutput(inventory.getStackInSlot(0), tank.getTankType());

        if (result == null) return;

        ItemStack stack = result.output.copy();

        ItemStack outputSlot = inventory.getStackInSlot(2);
        if (outputSlot.isEmpty()) {
            inventory.setStackInSlot(2, stack);
        } else if (outputSlot.getCount() + stack.getCount() <= outputSlot.getMaxStackSize()) {
            outputSlot.grow(stack.getCount());
        }

        tank.setFill(tank.getFill() - getRequiredAcid(result.acidAmount));

        float freeChance = this.getFreeChance(result);
        if (freeChance == 0 || freeChance < Objects.requireNonNull(level).random.nextFloat()) {
            inventory.extractItem(0, result.itemAmount, false);
        }
    }

    private boolean canProcess() {
        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return false;

        if (power < getPowerRequired()) return false;

        CrystallizerRecipe result = CrystallizerRecipes.getOutput(input, tank.getTankType());
        if (result == null) return false;

        if (input.getCount() < result.itemAmount) return false;
        if (tank.getFill() < getRequiredAcid(result.acidAmount)) return false;

        ItemStack output = result.output.copy();
        ItemStack outputSlot = inventory.getStackInSlot(2);

        if (!outputSlot.isEmpty()) {
            if (outputSlot.getItem() != output.getItem() || outputSlot.getDamageValue() != output.getDamageValue()) {
                return false;
            }
            return outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize();
        }

        return true;
    }

    public int getRequiredAcid(int base) {
        return base;
    }

    public float getFreeChance(CrystallizerRecipe recipe) {
        int efficiency = upgradeManager.getLevel(UpgradeType.EFFECT);
        if (efficiency > 0) {
            return Math.min(efficiency * recipe.productivity, 0.99F);
        }
        return 0;
    }

    public short getDuration() {
        CrystallizerRecipe result = CrystallizerRecipes.getOutput(inventory.getStackInSlot(0), tank.getTankType());
        int base = result != null ? result.duration : 600;
        int speed = upgradeManager.getLevel(UpgradeType.SPEED);
        if (speed > 0) {
            return (short) Math.ceil((base * Math.max(1F - 0.25F * speed, 0.25F)));
        }
        return (short) base;
    }

    public int getPowerRequired() {
        int speed = upgradeManager.getLevel(UpgradeType.SPEED);
        int effect = upgradeManager.getLevel(UpgradeType.EFFECT);
        return (int) (DEMAND + speed * DEMAND + effect * DEMAND * 2);
    }

    public float getCycleCount() {
        int overdrive = upgradeManager.getLevel(UpgradeType.OVERDRIVE);
        return Math.min(1 + overdrive * 2, 7);
    }

    public long getPowerScaled(int i) {
        return (power * i) / MAX_POWER;
    }

    public int getProgressScaled(int i) {
        return (progress * i) / duration;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        tank.readFromNBT(nbt, "tank");
        progress = nbt.getShort("progress");
        duration = nbt.getShort("duration");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        tank.writeToNBT(nbt, "tank");
        nbt.putShort("progress", progress);
        nbt.putShort("duration", duration);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeShort(progress);
        buf.writeShort(getDuration());
        buf.writeLong(power);
        buf.writeBoolean(isOn);
        tank.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        progress = buf.readShort();
        duration = buf.readShort();
        power = buf.readLong();
        isOn = buf.readBoolean();
        tank.deserialize(buf);
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

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1,
                worldPosition.getY(),
                worldPosition.getZ() - 1,
                worldPosition.getX() + 2,
                worldPosition.getY() + 10,
                worldPosition.getZ() + 2
        );
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerCrystallizer(windowId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crystallizer");
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public FluidTankHBM getTank() {
        return tank;
    }

    public long getPower() {
        return power;
    }

    public long getMaxPower() {
        return MAX_POWER;
    }

    @Override
    public boolean canProvideInfo(UpgradeType type, int level, boolean extendedInfo) {
        return type == UpgradeType.SPEED || type == UpgradeType.EFFECT || type == UpgradeType.OVERDRIVE;
    }

    @Override
    public void provideInfo(UpgradeType type, int level, List<Component> info, boolean extendedInfo) {
        info.add(IUpgradeInfoProvider.getStandardLabel(ModBlocks.MACHINE_CRYSTALLIZER.get()));

        if (type == UpgradeType.SPEED) {
            info.add(Component.literal("-" + (level * 25) + "%")
                    .withStyle(ChatFormatting.GREEN));
            info.add(Component.translatable(IUpgradeInfoProvider.KEY_CONSUMPTION, "+" + (level * 100) + "%")
                    .withStyle(ChatFormatting.RED));
        }
        if (type == UpgradeType.EFFECT) {
            info.add(Component.literal("x" + level)
                    .withStyle(ChatFormatting.GREEN));
            info.add(Component.translatable(IUpgradeInfoProvider.KEY_CONSUMPTION, "+" + (level * 200) + "%")
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
        upgrades.put(UpgradeType.EFFECT, 3);
        upgrades.put(UpgradeType.OVERDRIVE, 3);
        return upgrades;
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return tank;
    }

    @Override
    public void setPower(long power) {
        this.power = power;
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{tank};
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && (type == tank.getTankType());
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
}