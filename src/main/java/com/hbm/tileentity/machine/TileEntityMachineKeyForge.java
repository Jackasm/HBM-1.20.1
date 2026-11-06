package com.hbm.tileentity.machine;

import com.hbm.inventory.container.ContainerMachineKeyForge;
import com.hbm.items.tool.ItemKeyPin;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityMachineKeyForge extends BlockEntity implements WorldlyContainer, net.minecraft.world.MenuProvider {

    private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);
    private String customName;

    private static final int[] SLOTS_TOP    = {0};
    private static final int[] SLOTS_BOTTOM = {1};
    private static final int[] SLOTS_SIDE   = {2};

    public TileEntityMachineKeyForge(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_KEY_FORGE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack slot0 = slots.get(0);
        ItemStack slot1 = slots.get(1);

        if (!slot0.isEmpty() && !slot1.isEmpty()
                && slot0.getItem() instanceof ItemKeyPin p0
                && slot1.getItem() instanceof ItemKeyPin p1
                && p0.canTransfer() && p1.canTransfer()) {
            ItemKeyPin.setPins(slot1, ItemKeyPin.getPins(slot0));
            setChanged();
        }

        ItemStack slot2 = slots.get(2);
        if (!slot2.isEmpty() && slot2.getItem() instanceof ItemKeyPin p2 && p2.canTransfer()) {
            ItemKeyPin.setPins(slot2, level.random.nextInt(900) + 100);
            setChanged();
        }
    }

    // === MenuProvider ===

    @Override
    public @NotNull Component getDisplayName() {
        return customName != null && !customName.isEmpty()
                ? Component.literal(customName)
                : Component.translatable("container.keyForge");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerMachineKeyForge(id, inv, this);
    }

    // === Container ===

    @Override public int getContainerSize() { return slots.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : slots) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override public @NotNull ItemStack getItem(int slot) { return slots.get(slot); }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(slots, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(slots, slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        slots.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override public void clearContent() { slots.clear(); setChanged(); }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) { return false; }

    // === WorldlyContainer ===

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return switch (side) {
            case DOWN  -> SLOTS_BOTTOM;
            case UP    -> SLOTS_TOP;
            default    -> SLOTS_SIDE;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
        return true;
    }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, slots);
        customName = tag.contains("name") ? tag.getString("name") : null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, slots);
        if (customName != null) tag.putString("name", customName);
    }
}