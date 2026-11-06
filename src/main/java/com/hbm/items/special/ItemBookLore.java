package com.hbm.items.special;

import com.hbm.inventory.container.ContainerBookLore;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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

import java.util.List;

public class ItemBookLore extends Item {

    public ItemBookLore(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;
        String key = tag.getString("k");
        if (key.isEmpty()) return;

        String authorKey = "book_lore." + key + ".author";
        Component author = Component.translatable(authorKey);
        if (!author.getString().equals(authorKey)) {
            tooltip.add(Component.translatable("book_lore.author", author)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.getString("k").isEmpty()) {
            return Component.translatable("book_lore.test");
        }
        return Component.translatable("book_lore." + tag.getString("k"));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable("container.book_lore");
                }

                @Override
                public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv,
                                                                 @NotNull Player p) {
                    return new ContainerBookLore(id, inv, stack);
                }
            }, buf -> buf.writeItem(stack));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    // ==== Фабрики ====

    public static ItemStack createBook(String key, int pages, int colorCov, int colorTit) {
        ItemStack book = new ItemStack(ModItems.BOOK_LORE.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("k", key);
        tag.putShort("p", (short) pages);
        tag.putInt("cov_col", colorCov);
        tag.putInt("tit_col", colorTit);
        book.setTag(tag);
        return book;
    }

    public static void addArgs(ItemStack book, int page, String... args) {
        CompoundTag parent = book.getTag();
        if (parent == null) return;
        CompoundTag data = new CompoundTag();
        for (int i = 0; i < args.length; i++) {
            data.putString("a" + (i + 1), args[i]);
        }
        parent.put("p" + page, data);
    }
}