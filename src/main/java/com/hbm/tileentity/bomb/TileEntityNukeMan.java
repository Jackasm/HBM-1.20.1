package com.hbm.tileentity.bomb;

import com.hbm.inventory.container.ContainerNukeMan;
import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class TileEntityNukeMan extends BlockEntity implements net.minecraft.world.Container, net.minecraft.world.MenuProvider {

    private final NonNullList<ItemStack> slots = NonNullList.withSize(6, ItemStack.EMPTY);
    private String customName;

    public TileEntityNukeMan(BlockPos pos, BlockState state) {
        super(ModTileEntity.NUKE_MAN.get(), pos, state);
    }

    // === Логика ===

    public boolean exp1() { return isLens(slots.get(1)); }
    public boolean exp2() { return isLens(slots.get(2)); }
    public boolean exp3() { return isLens(slots.get(3)); }
    public boolean exp4() { return isLens(slots.get(4)); }

    private boolean isLens(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.EARLY_EXPLOSIVE_LENSES.get();
    }

    public boolean isReady() {
        if (!(exp1() && exp2() && exp3() && exp4())) return false;
        ItemStack s0 = slots.get(0);
        ItemStack s5 = slots.get(5);
        return !s0.isEmpty() && s0.getItem() == ModItems.MAN_IGNITER.get()
                && !s5.isEmpty() && s5.getItem() == ModItems.MAN_CORE.get();
    }

    public void clearSlots() {
        Collections.fill(slots, ItemStack.EMPTY);
        setChanged();
    }

    // === MenuProvider ===

    @Override
    public @NotNull Component getDisplayName() {
        return customName != null && !customName.isEmpty()
                ? Component.literal(customName)
                : Component.translatable("container.nukeMan");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerNukeMan(id, inv, this);
    }

    // === Container ===

    @Override public int getContainerSize() { return slots.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : slots) if (!s.isEmpty()) return false;
        return true;
    }

    @Override public @NotNull ItemStack getItem(int slot) { return slots.get(slot); }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack s = ContainerHelper.removeItem(slots, slot, amount);
        if (!s.isEmpty()) setChanged();
        return s;
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

    @Override public int getMaxStackSize() { return 1; }
    @Override public boolean canPlaceItem(int slot, @NotNull ItemStack stack) { return false; }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override public void clearContent() { clearSlots(); }

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

    // === Рендер ===

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1, worldPosition.getY() - 1, worldPosition.getZ() - 1,
                worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2
        );
    }
}