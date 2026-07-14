package com.hbm.items.tool;

import com.hbm.inventory.container.ContainerToolBox;
import com.hbm.items.ItemInventory;
import com.hbm.items.ModItems;
import com.hbm.util.ItemStackUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ItemToolBox extends Item {

    public ItemToolBox(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Click with the toolbox to swap hotbars in/out of the toolbox."));
        tooltip.add(Component.literal("Shift-click with the toolbox to open the toolbox."));
    }

    public List<Integer> getActiveRows(ItemStack box) {
        ItemStack[] stacks = ItemStackUtil.readStacksFromNBT(box, 24);
        if (stacks == null) return new ArrayList<>();
        List<Integer> activeRows = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            for (int slot = 0; slot < 8; slot++) {
                if (stacks[row * 8 + slot] != null && !stacks[row * 8 + slot].isEmpty()) {
                    activeRows.add(row);
                    break;
                }
            }
        }
        return activeRows;
    }

    public void moveRows(ItemStack box, Player player) {
        Inventory inventory = player.getInventory();

        ItemStack[] endingHotBar = new ItemStack[9];
        ItemStack[] stacksToTransferToBox = new ItemStack[8];

        boolean hasToolbox = false;
        int extraToolboxes = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack slot = inventory.getItem(i);

            if (slot != null && slot.getItem() == ModItems.TOOLBOX.get() && i != inventory.selected) {
                extraToolboxes++;
                if (!player.level().isClientSide) {
                    player.drop(slot, true);
                }
                inventory.setItem(i, ItemStack.EMPTY);
            } else if (i == inventory.selected) {
                hasToolbox = true;
                endingHotBar[i] = slot;
            } else {
                stacksToTransferToBox[i - (hasToolbox ? 1 : 0)] = slot;
            }
        }

        if (extraToolboxes > 0) {
            String msg = extraToolboxes == 1 ?
                    "You can't toolbox a toolbox... " :
                    "You can't toolbox a toolbox... (x" + extraToolboxes + ")";
            player.displayClientMessage(Component.literal(msg).withStyle(ChatFormatting.RED), false);
        }

        ItemStack[] stacks = ItemStackUtil.readStacksFromNBT(box, 24);
        ItemStack[] endingStacks = new ItemStack[24];

        int lowestActiveIndex = Integer.MAX_VALUE;
        int lowestInactiveIndex = Integer.MAX_VALUE;

        if (stacks != null) {
            List<Integer> activeRows = getActiveRows(box);

            for (int i = 0; i < 3; i++) {
                if (activeRows.contains(i))
                    lowestActiveIndex = Math.min(i, lowestActiveIndex);
                else
                    lowestInactiveIndex = Math.min(i, lowestInactiveIndex);
            }

            if (lowestInactiveIndex > 2)
                lowestInactiveIndex = 2;
            else
                lowestInactiveIndex = Math.max(0, lowestInactiveIndex - 1);

            for (Integer activeRowIndex : activeRows) {
                int activeIndex = 8 * activeRowIndex;

                if (activeRowIndex == lowestActiveIndex) {
                    hasToolbox = false;
                    for (int i = 0; i < 9; i++) {
                        if (i == inventory.selected) {
                            hasToolbox = true;
                            continue;
                        }
                        endingHotBar[i] = stacks[activeIndex + i - (hasToolbox ? 1 : 0)];
                    }
                    continue;
                }

                int targetIndex = 8 * (activeRowIndex - 1);
                System.arraycopy(stacks, activeIndex, endingStacks, targetIndex, 8);
            }
        }

        if (stacks == null)
            lowestInactiveIndex = 0;

        System.arraycopy(stacksToTransferToBox, 0, endingStacks, lowestInactiveIndex * 8, 8);

        for (int i = 0; i < endingHotBar.length; i++) {
            inventory.setItem(i, endingHotBar[i] == null ? ItemStack.EMPTY : endingHotBar[i]);
        }

        box.setTag(new CompoundTag());
        ItemStackUtil.addStacksToNBT(box, endingStacks);

        CompoundTag nbt = box.getTag();

        if (nbt != null && !nbt.isEmpty()) {
            Random random = new Random();

            try {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                NbtIo.writeCompressed(nbt, byteOutput);
                byte[] abyte = byteOutput.toByteArray();

                if (abyte.length > 6000) {
                    player.displayClientMessage(Component.literal("Warning: Container NBT exceeds 6kB, contents will be ejected!").withStyle(ChatFormatting.RED), false);
                    ItemStack[] stacks1 = ItemStackUtil.readStacksFromNBT(box, 24);
                    if (stacks1 == null) return;

                    for (ItemStack itemstack : stacks1) {
                        if (itemstack != null && !itemstack.isEmpty()) {
                            float f = random.nextFloat() * 0.8F + 0.1F;
                            float f1 = random.nextFloat() * 0.8F + 0.1F;
                            float f2 = random.nextFloat() * 0.8F + 0.1F;

                            while (itemstack.getCount() > 0) {
                                int j1 = random.nextInt(21) + 10;
                                if (j1 > itemstack.getCount()) {
                                    j1 = itemstack.getCount();
                                }

                                itemstack.shrink(j1);
                                ItemStack dropStack = new ItemStack(itemstack.getItem(), j1);
                                if (itemstack.getDamageValue() > 0) {
                                    dropStack.setDamageValue(itemstack.getDamageValue());
                                }
                                if (itemstack.hasTag()) {
                                    dropStack.setTag(Objects.requireNonNull(itemstack.getTag()).copy());
                                }
                                player.drop(dropStack, true);
                            }
                        }
                    }
                    box.setTag(new CompoundTag());
                }
            } catch (IOException ignored) {}
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (!player.isShiftKeyDown()) {
                moveRows(stack, player);
                player.containerMenu.broadcastChanges();
            } else {
                if (stack.getTag() == null)
                    stack.setTag(new CompoundTag());
                stack.getTag().putBoolean("isOpen", true);
                NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                    @Override
                    public @NotNull Component getDisplayName() {
                        return Component.translatable("container.toolBox");
                    }

                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
                        return new ContainerToolBox(id, inv, new InventoryToolBox(player, stack));
                    }
                });
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static class InventoryToolBox extends ItemInventory {

        public InventoryToolBox(Player player, ItemStack box) {
            this.player = player;
            this.target = box;
            this.slots = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

            if (!box.hasTag())
                box.setTag(new CompoundTag());

            ItemStack[] fromNBT = ItemStackUtil.readStacksFromNBT(box, getContainerSize());

            if (fromNBT != null) {
                for (int i = 0; i < fromNBT.length && i < slots.size(); i++) {
                    slots.set(i, fromNBT[i] == null ? ItemStack.EMPTY : fromNBT[i]);
                }
            }
        }

        @Override
        public int getContainerSize() {
            return 24;
        }

        public Component getDisplayName() {
            return Component.translatable("container.toolBox");
        }

        public boolean hasCustomName() {
            return target.hasCustomHoverName();
        }

        @Override
        public void stopOpen(@NotNull Player player) {
            super.stopOpen(player);

            if (target.getTag() != null) {
                target.getTag().remove("isOpen");
                target.getTag().putInt("rand", player.level().random.nextInt());
            }
            player.containerMenu.broadcastChanges();
        }

        @Override
        public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
            return stack.getItem() != ModItems.TOOLBOX.get();
        }
    }
}