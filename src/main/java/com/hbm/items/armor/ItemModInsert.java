package com.hbm.items.armor;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemModInsert extends ItemArmorMod {

    private static final UUID SPEED_MOD_UUID = UUID.fromString("7a2f3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");

    private final float damageMod;
    private final float projectileMod;
    private final float explosionMod;
    private final float speed;
    private final boolean isPolonium;

    public ItemModInsert(Properties properties, float damageMod, float projectileMod, float explosionMod, float speed, boolean isPolonium) {
        super(ArmorModHandler.KEVLAR, false, true, false, false, properties);
        this.damageMod = damageMod;
        this.projectileMod = projectileMod;
        this.explosionMod = explosionMod;
        this.speed = speed;
        this.isPolonium = isPolonium;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (damageMod != 1F) {
            tooltip.add(Component.literal(ChatFormatting.RED + (damageMod < 1 ? "-" : "+") + Math.abs(Math.round((1F - damageMod) * 100)) + "% damage"));
        }
        if (projectileMod != 1F) {
            tooltip.add(Component.literal(ChatFormatting.YELLOW + "-" + Math.round((1F - projectileMod) * 100) + "% projectile damage"));
        }
        if (explosionMod != 1F) {
            tooltip.add(Component.literal(ChatFormatting.YELLOW + "-" + Math.round((1F - explosionMod) * 100) + "% explosion damage"));
        }
        if (speed != 1F) {
            tooltip.add(Component.literal(ChatFormatting.BLUE + "-" + Math.round((1F - speed) * 100) + "% speed"));
        }

        if (isPolonium) {
            tooltip.add(Component.literal(ChatFormatting.DARK_RED + "+100 RAD/s"));
        }

        tooltip.add(Component.literal((stack.getMaxDamage() - stack.getDamageValue()) + "/" + stack.getMaxDamage() + "HP"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void onLivingHurt(LivingHurtEvent event, ItemStack armor) {
        DamageSource source = event.getSource();

        event.setAmount(event.getAmount() * damageMod);

        if (source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            event.setAmount(event.getAmount() * projectileMod);
        }

        if (source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
            event.setAmount(event.getAmount() * explosionMod);
        }

        if (armor != null) {
            armor.setDamageValue(armor.getDamageValue() + 1);

            if (!event.getEntity().level().isClientSide && this == ModItems.INSERT_ERA.get()) {
                event.getEntity().level().explode(event.getEntity(),
                        event.getEntity().getX(),
                        event.getEntity().getY() + event.getEntity().getEyeHeight() * 0.5,
                        event.getEntity().getZ(),
                        0.05F, false, Level.ExplosionInteraction.NONE);
            }

            if (armor.getDamageValue() >= armor.getMaxDamage()) {
                // Удаляем мод из брони — это требует дополнительной логики в ArmorModHandler
                // Пока просто сбрасываем предмет
                armor.shrink(1);
            }
        }
    }

    public void onArmorTick(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide && isPolonium) {
            HbmLivingProps.incrementRadiation(entity, 100F);
        }
    }

    public AttributeModifier getSpeedModifier() {
        if (speed == 1F) return null;
        return new AttributeModifier(SPEED_MOD_UUID, "NTM Armor Mod Speed", -1F + speed, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public float getDamageMod() { return damageMod; }
    public float getProjectileMod() { return projectileMod; }
    public float getExplosionMod() { return explosionMod; }
    public float getSpeed() { return speed; }
}