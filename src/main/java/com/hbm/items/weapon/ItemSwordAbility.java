package com.hbm.items.weapon;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ability.AvailableAbilities;
import com.hbm.handler.ability.IWeaponAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ItemSwordAbility extends SwordItem {

    private final AvailableAbilities abilities;
    private final Rarity rarity;
    private final float attackDamage;
    private final float attackSpeed;

    // UUID для атрибутов (используем стандартные из SwordItem)
    private static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ItemSwordAbility(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
        this.rarity = Rarity.COMMON;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.abilities = new AvailableAbilities();

        // Создаем модификаторы атрибутов
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                this.attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    // Строитель для удобного создания
    public static Builder builder(Tier tier) {
        return new Builder(tier);
    }

    public static class Builder {
        private final Tier tier;
        private int attackDamage = 3;
        private float attackSpeed = -2.4F;
        private Rarity rarity = Rarity.COMMON;
        private AvailableAbilities abilities = new AvailableAbilities();

        public Builder(Tier tier) {
            this.tier = tier;
        }

        public Builder attackDamage(int damage) {
            this.attackDamage = damage;
            return this;
        }

        public Builder attackSpeed(float speed) {
            this.attackSpeed = speed;
            return this;
        }

        public Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder addAbility(IWeaponAbility ability, int level) {
            this.abilities.addAbility(ability, level);
            return this;
        }

        public ItemSwordAbility build() {
            Properties props = new Properties()
                    .durability(tier.getUses())
                    .rarity(rarity);

            ItemSwordAbility sword = new ItemSwordAbility(
                    tier, attackDamage, attackSpeed, props);

            // Копируем способности
            this.abilities.getWeaponAbilities().forEach((ability, level) -> {
                sword.addAbility(ability, level);
            });

            return sword;
        }
    }

    public ItemSwordAbility addAbility(IWeaponAbility weaponAbility, int level) {
        this.abilities.addAbility(weaponAbility, level);
        return this;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player && canOperate(stack)) {

            // Специальный звук для определенных мечей
            /*
            if (this == ModItems.MESE_GAVEL.get()) {
                attacker.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                        net.minecraft.sounds.SoundEvents.ANVIL_LAND,
                        net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            }

             */

            // Активация способностей оружия
            this.abilities.getWeaponAbilities().forEach((ability, level) -> {
                ability.onHit(level, attacker.level(), (Player) attacker, target, this);
            });
        }

        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // Добавляем информацию о способностях
        abilities.addInformation(tooltip);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return this.rarity != Rarity.COMMON ? this.rarity : super.getRarity(stack);
    }

    protected boolean canOperate(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.isEnchanted() || !this.abilities.getWeaponAbilities().isEmpty();
    }

    // Геттеры для доступа к данным меча
    public float getAttackDamage() {
        return this.attackDamage;
    }

    public float getAttackSpeed() {
        return this.attackSpeed;
    }

    public AvailableAbilities getAbilities() {
        return this.abilities;
    }
}