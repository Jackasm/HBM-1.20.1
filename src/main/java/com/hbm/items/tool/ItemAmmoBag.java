package com.hbm.items.tool;

import com.hbm.inventory.container.ContainerAmmoBag;
import com.hbm.inventory.gui.GUIAmmoBag;
import com.hbm.items.ModItems;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import net.minecraft.world.SimpleContainer;

public class ItemAmmoBag extends Item implements IGUIProvider, MenuProvider {

    public ItemAmmoBag() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object provideGUI(int ID, Player player, Level world, BlockHitResult hitResult) {
        return new GUIAmmoBag(new ContainerAmmoBag(0, player.getInventory(),
                new InventoryAmmoBag(player.getMainHandItem())), player.getInventory(),
                net.minecraft.network.chat.Component.translatable("container.ammoBag"));
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
    public AbstractContainerMenu provideContainer(int ID, Player player, Level world, BlockHitResult hitResult) {
        return new ContainerAmmoBag(ID, player.getInventory(), new InventoryAmmoBag(player.getMainHandItem()));
    }

    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.@NotNull Inventory playerInv, Player player) {
        return new ContainerAmmoBag(id, playerInv, new InventoryAmmoBag(player.getMainHandItem()));
    }

    @Override
    @NotNull
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.ammoBag");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object provideGUI(int ID, Player player, Level world, int x, int y, int z) {
        return new GUIAmmoBag(new ContainerAmmoBag(0, player.getInventory(),
                new InventoryAmmoBag(player.getMainHandItem())), player.getInventory(),
                net.minecraft.network.chat.Component.translatable("container.ammoBag"));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        if (this == ModItems.AMMO_BAG_INFINITE.get()) return false;
        return !stack.hasTag() || getBarWidth(stack) != 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!stack.hasTag()) return 13;

        InventoryAmmoBag inv = new InventoryAmmoBag(stack);
        int capacity = 0;
        int bullets = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack slot = inv.getItem(i);
            if (slot.isEmpty()) {
                capacity += 64;
            } else {
                capacity += slot.getMaxStackSize();
                bullets += slot.getCount();
            }
        }

        if (capacity == 0) return 0;
        return (int) Math.max(1, (1.0 - (double) bullets / (double) capacity) * 13);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00FF00; // Green durability bar
    }

    public static class InventoryAmmoBag extends SimpleContainer {
        private final ItemStack bag;

        public InventoryAmmoBag(ItemStack bag) {
            super(8);
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
            // Only allow items without NBT (simple ammo items)
            return !stack.hasTag();
        }
    }
}