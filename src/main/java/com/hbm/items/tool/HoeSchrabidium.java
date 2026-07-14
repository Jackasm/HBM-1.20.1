package com.hbm.items.tool;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

public class HoeSchrabidium extends HoeItem {

    public HoeSchrabidium(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.EPIC; // или Rarity.RARE в зависимости от нужной редкости
    }

    // Если нужны дополнительные способности как у других инструментов
    public static class Builder {
        private final Tier tier;
        private int attackDamage = -3; // Стандартное значение для мотыги
        private float attackSpeed = 0.0F; // Стандартное значение
        private Rarity rarity = Rarity.COMMON;

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

        public HoeSchrabidium build() {
            Properties props = new Properties()
                    .durability(tier.getUses())
                    .rarity(rarity);

            return new HoeSchrabidium(tier, attackDamage, attackSpeed, props);
        }
    }

    public static Builder builder(Tier tier) {
        return new Builder(tier);
    }
}