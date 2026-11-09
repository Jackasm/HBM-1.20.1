package com.hbm.items.tool;

import com.hbm.items.ModItems;
import com.hbm.tileentity.TileEntityLockableBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCounterfeitKeys extends Item {

    public ItemCounterfeitKeys(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(context.getClickedPos());
        if (!(te instanceof TileEntityLockableBase locked)) return InteractionResult.PASS;

        if (locked.isLocked() && locked.isCheesable()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            // Создаём поддельный ключ с теми же пинами
            ItemStack fakeKey = new ItemStack(ModItems.KEY_FAKE.get());
            ItemKeyPin.setPins(fakeKey, locked.getPins());

            // Заменяем key_kit в руке на поддельный ключ
            ItemStack held = context.getItemInHand();
            ItemStack replacement = fakeKey.copy();

            // Если стак key_kit — 1 (maxStackSize = 1), просто заменяем слот
            int slot = player.getInventory().selected;
            player.getInventory().setItem(slot, replacement);

            // Дополнительно пытаемся положить ещё один фейк в инвентарь (как в оригинале — второй ключ)
            ItemStack extra = fakeKey.copy();
            if (!player.getInventory().add(extra)) {
                player.drop(extra, false);
            }

            // Анимация взмаха
            player.swing(context.getHand());

            return InteractionResult.CONSUME;
        }

        if (!locked.isCheesable()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            player.displayClientMessage(
                    Component.translatable("hbm.text.this_lock_is_too_elaborate_for_a_counterfeit_key")
                            .withStyle(ChatFormatting.LIGHT_PURPLE), false);
            player.displayClientMessage(
                    Component.translatable("hbm.text.perhaps_there_is_another_way_around_here_to_unlo")
                            .withStyle(ChatFormatting.LIGHT_PURPLE), false);

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("hbm.text.use_on_a_locked_container_to_create_two_counterf"));
    }
}