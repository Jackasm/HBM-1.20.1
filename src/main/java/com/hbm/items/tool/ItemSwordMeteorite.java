package com.hbm.items.tool;

import com.hbm.items.ModToolItems;
import com.hbm.render.item.MeteorSwordRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemSwordMeteorite extends ItemSwordAbility {

    public static final List<ItemSwordMeteorite> swords = new ArrayList<>();

    public ItemSwordMeteorite(Properties properties, Tier tier, float attackDamage, float attackSpeed) {
        super(properties, tier, attackDamage, attackSpeed);
        swords.add(this);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Возвращаем ваш кастомный рендерер с нужными цветами
                return new MeteorSwordRenderer();
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (this == ModToolItems.METEORITE_SWORD.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_SEARED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.seared.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_REFORGED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.reforged.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_HARDENED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.hardened.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_ALLOYED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.alloyed.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_MACHINED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.machined.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_TREATED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.treated.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_ETCHED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.etched.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_BRED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.bred.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_IRRADIATED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.irradiated.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_FUSED.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.fused.desc").withStyle(ChatFormatting.ITALIC));
        }

        if (this == ModToolItems.METEORITE_SWORD_BALEFUL.get()) {
            tooltip.add(Component.translatable("item.hbm.meteorite_sword.baleful.desc").withStyle(ChatFormatting.ITALIC));
        }
    }
}