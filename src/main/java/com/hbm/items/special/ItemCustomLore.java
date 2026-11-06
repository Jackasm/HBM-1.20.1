package com.hbm.items.special;

import com.hbm.blocks.ITooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ItemCustomLore extends Item implements ITooltipProvider {

    protected Rarity rarity;
    protected boolean hasEffect = false;
    protected boolean scramblingName = false;

    public ItemCustomLore(Properties properties) {
        super(properties);
    }

    public ItemCustomLore setRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public ItemCustomLore setEffect() {
        this.hasEffect = true;
        return this;
    }

    /** Помечает предмет как «undefined» — имя будет браться из ScramblingName. */
    public ItemCustomLore setScramblingName() {
        this.scramblingName = true;
        return this;
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return this.rarity != null ? rarity : super.getRarity(stack);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (scramblingName) {
            return Component.literal(ScramblingNameHolder.NAME.getResult());
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {

        if (scramblingName) {
            // undefined-специфичный тултип
            if (level != null && level.random.nextInt(10) == 0) {
                tooltip.add(Component.literal("UNDEFINED").withStyle(ChatFormatting.DARK_RED));
            } else {
                try {
                    Random rand = new Random(System.currentTimeMillis() / 500);
                    int setSize = BuiltInRegistries.ITEM.size();
                    int r = rand.nextInt(setSize);
                    Item item = BuiltInRegistries.ITEM.byId(r);

                    if (item != null && item != net.minecraft.world.item.Items.AIR) {
                        tooltip.add(new ItemStack(item).getHoverName());
                    } else {
                        tooltip.add(Component.literal("ERROR #" + r).withStyle(ChatFormatting.RED));
                    }
                } catch (Exception ex) {
                    tooltip.add(Component.literal("UNDEFINED").withStyle(ChatFormatting.DARK_RED));
                }
            }
            return;
        }

        // Обычный тултип из `<desc>`, разбитый по `$`
        String unloc = this.getDescriptionId() + ".desc";
        String loc = Component.translatable(unloc).getString();

        if (!unloc.equals(loc)) {
            for (String s : loc.split("\\$")) {
                tooltip.add(Component.literal(s));
            }
        }
    }
}