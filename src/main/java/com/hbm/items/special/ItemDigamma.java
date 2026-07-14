package com.hbm.items.special;

import com.hbm.config.WeaponConfig;
import com.hbm.entity.effect.EntityQuasar;
import com.hbm.util.ContaminationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDigamma extends Item {

    private final int digamma;

    public ItemDigamma(Properties properties, int digamma) {
        super(properties);
        this.digamma = digamma;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (entity instanceof Player player) {
            // Применяем дигамма-эффект к игроку
            ContaminationUtil.applyDigammaData(player, 1.0F / ((float) digamma));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Half-life: 1.67*10³⁴ years"));
        tooltip.add(Component.literal(ChatFormatting.RED + "Player decay: " + (digamma / 20F) + "s"));

        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);

        float d = ((int) ((1000F / digamma) * 200F)) / 10F;

        tooltip.add(Component.literal(ChatFormatting.RED + "[Digamma]"));
        tooltip.add(Component.literal(ChatFormatting.DARK_RED + "" + d + "mDRX/s"));

        tooltip.add(Component.literal(ChatFormatting.RED + "[Drop to release]"));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity != null) {
            if (entity.onGround() && !entity.level().isClientSide) {
                if (WeaponConfig.dropSing.get()) {
                    // Создаём квазар при падении на землю
                    EntityQuasar quasar = new EntityQuasar(entity.level(), 5.0F);
                    quasar.setPos(entity.getX(), entity.getY(), entity.getZ());
                    entity.level().addFreshEntity(quasar);
                }

                entity.discard();
                return true;
            }
        }
        return false;
    }

    // Фабричный метод для создания свойств
    public static Properties createProperties() {
        return new Properties()
                .stacksTo(1);
    }
}