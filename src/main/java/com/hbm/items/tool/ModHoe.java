// ModHoe.java - базовая мотыга
package com.hbm.items.tool;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;

public class ModHoe extends HoeItem {
    public ModHoe(Tier tier, int attackDamageModifier, float attackSpeed, Properties properties) {
        super(tier, attackDamageModifier, attackSpeed, properties);
    }
}