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
                    Component.literal("This lock is too elaborate for a counterfeit key to be made")
                            .withStyle(ChatFormatting.LIGHT_PURPLE), false);
            player.displayClientMessage(
                    Component.literal("Perhaps there is another way around here to unlock it")
                            .withStyle(ChatFormatting.LIGHT_PURPLE), false);

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Use on a locked container to create two counterfeit keys!"));
    }
}