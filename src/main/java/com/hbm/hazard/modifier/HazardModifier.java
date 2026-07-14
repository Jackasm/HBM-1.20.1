package com.hbm.hazard.modifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class HazardModifier {

    /**
     * Modifies the hazard level for the given stack and holder.
     *
     * @param stack  the item stack
     * @param holder the entity holding the item, can be null
     * @param level  the current hazard level
     * @return the modified hazard level
     */
    public abstract float modify(@NotNull ItemStack stack, @Nullable LivingEntity holder, float level);

    /**
     * Returns the level after applying all modifiers in order.
     *
     * @param stack  the item stack
     * @param entity the entity holding the item, can be null
     * @param level  the initial hazard level
     * @param mods   the list of modifiers to apply
     * @return the final hazard level after all modifiers
     */
    public static float evalAllModifiers(@NotNull ItemStack stack,
                                         @Nullable LivingEntity entity,
                                         float level,
                                         @NotNull List<HazardModifier> mods) {
        float result = level;
        for (HazardModifier mod : mods) {
            result = mod.modify(stack, entity, result);
        }
        return result;
    }
}