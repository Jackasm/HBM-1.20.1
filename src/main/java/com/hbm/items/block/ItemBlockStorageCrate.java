package com.hbm.items.block;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemBlockStorageCrate extends BlockItem {

    public ItemBlockStorageCrate(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (!stack.hasTag()) return;
        CompoundTag nbt = stack.getTag();

        // === ПАУКИ ===
        boolean hasSpiders = nbt.getBoolean("crate_spiders");
        boolean isLocked = nbt.contains("crate_lock");

        // === ЗАМОК ===
        if (isLocked) {
            tooltip.add(Component.translatable("hbm.text.this_container_is_locked")
                    .withStyle(ChatFormatting.RED));

            // Есть ли содержимое?
            CompoundTag items = nbt.getCompound("crate_items");
            if (!items.isEmpty()) {
                tooltip.add(Component.translatable("hbm.text.it_feels_heavy")
                        .withStyle(ChatFormatting.YELLOW));
            } else {
                tooltip.add(Component.translatable("hbm.text.it_feels_empty")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }

        // === ПАУКИ (отдельно от замка) ===
        if (hasSpiders) {
            tooltip.add(Component.translatable("hbm.text.skittering_emanates_from_within")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }

        // === СОДЕРЖИМОЕ (только если НЕ заперт) ===
        if (!isLocked && nbt.contains("crate_items")) {
            CompoundTag items = nbt.getCompound("crate_items");
            List<String> contents = new ArrayList<>();
            int amount = 0;

            for (int i = 0; i < 104; i++) {
                String key = "slot" + i;
                if (!items.contains(key)) continue;

                ItemStack content = ItemStack.of(items.getCompound(key));
                if (content.isEmpty()) continue;

                amount++;
                if (contents.size() < 10) {
                    String line = " - " + content.getHoverName().getString()
                            + (content.getCount() > 1 ? " x" + content.getCount() : "");
                    contents.add(line);
                }
            }

            if (!contents.isEmpty()) {
                tooltip.add(Component.translatable("hbm.text.contains").withStyle(ChatFormatting.AQUA));
                for (String line : contents) {
                    tooltip.add(Component.literal(line).withStyle(ChatFormatting.AQUA));
                }
                int more = amount - contents.size();
                if (more > 0) {
                    tooltip.add(Component.translatable("hbm.text.and", more)
                            .withStyle(ChatFormatting.AQUA));
                }
            }
        }
    }
}