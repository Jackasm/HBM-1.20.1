package com.hbm.items.tool;

import com.hbm.inventory.container.ContainerCasingBag;
import com.hbm.inventory.gui.GUICasingBag;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.util.ItemStackUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import net.minecraft.world.SimpleContainer;

public class ItemCasingBag extends Item implements IGUIProvider, MenuProvider {

    public ItemCasingBag() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public AbstractContainerMenu provideContainer(int ID, Player player, Level world, net.minecraft.world.phys.BlockHitResult hitResult) {
        return new ContainerCasingBag(ID, player.getInventory(), new InventoryCasingBag(player.getMainHandItem()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object provideGUI(int ID, Player player, Level world, net.minecraft.world.phys.BlockHitResult hitResult) {
        return new GUICasingBag(new ContainerCasingBag(0, player.getInventory(),
                new InventoryCasingBag(player.getMainHandItem())), player.getInventory(),
                net.minecraft.network.chat.Component.translatable("container.casingBag"));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, this, buf -> {});
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.@NotNull Inventory playerInv, Player player) {
        return new ContainerCasingBag(id, playerInv, new InventoryCasingBag(player.getMainHandItem()));
    }

    @Override
    @NotNull
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.casingBag");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object provideGUI(int ID, Player player, Level world, int x, int y, int z) {
        return new GUICasingBag(new ContainerCasingBag(0, player.getInventory(),
                new InventoryCasingBag(player.getMainHandItem())), player.getInventory(),
                net.minecraft.network.chat.Component.translatable("container.casingBag"));
    }

    /**
     * Returns true if ammo was able to be added
     */
    public static boolean pushCasing(ItemStack bag, ItemStack casing, float amount) {
        if (bag.isEmpty()) return false;

        CompoundTag tag = bag.getOrCreateTag();
        String name = casing.getDescriptionId() + "@" + casing.getDamageValue();

        boolean ret = false;

        // Only add if the previous number did not exceed 1
        if (tag.getFloat(name) < 1) {
            ret = true;
            tag.putFloat(name, tag.getFloat(name) + amount);
        }

        if (tag.getFloat(name) >= 1) {
            InventoryCasingBag inv = new InventoryCasingBag(bag);
            ItemStack toAdd = casing.copy();

            while (tag.getFloat(name) >= 1) {
                boolean didSomething = false;

                // Try to add to existing stacks
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    if (toAdd.isEmpty()) break;

                    ItemStack slot = inv.getItem(i);
                    if (!slot.isEmpty() && ItemStack.isSameItemSameTags(slot, toAdd)) {
                        int am = Math.min(toAdd.getCount(), slot.getMaxStackSize() - slot.getCount());
                        toAdd.shrink(am);
                        slot.grow(am);
                        if (am > 0) didSomething = true;
                    }
                }

                // Try to add to empty slots
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    if (toAdd.isEmpty()) break;

                    ItemStack slot = inv.getItem(i);
                    if (slot.isEmpty()) {
                        inv.setItem(i, toAdd.copy());
                        toAdd.setCount(0);
                        didSomething = true;
                        break;
                    }
                }

                if (didSomething) {
                    tag.putFloat(name, tag.getFloat(name) - 1F);
                    ret = true;
                } else {
                    break;
                }
            }

            inv.setChanged();
        }

        return ret;
    }

    public static class InventoryCasingBag extends SimpleContainer {
        private final ItemStack bag;

        public InventoryCasingBag(ItemStack bag) {
            super(15);
            this.bag = bag;

            if (!bag.hasTag()) {
                bag.setTag(new CompoundTag());
            }

            ItemStack[] fromNBT = ItemStackUtil.readStacksFromNBT(bag, getContainerSize());

            if (fromNBT != null) {
                for (int i = 0; i < getContainerSize(); i++) {
                    setItem(i, fromNBT[i]);
                }
            }
        }

        @Override
        public void setChanged() {
            super.setChanged();

            // Clean up empty stacks
            for (int i = 0; i < getContainerSize(); i++) {
                ItemStack stack = getItem(i);
                if (!stack.isEmpty() && stack.getCount() == 0) {
                    setItem(i, ItemStack.EMPTY);
                }
            }

            ItemStack[] items = new ItemStack[getContainerSize()];
            for(int i = 0; i < getContainerSize(); i++) {
                items[i] = getItem(i);
            }
            ItemStackUtil.addStacksToNBT(bag, items);
        }

        @Override
        public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
            return false; // No manual insertion
        }
    }
}