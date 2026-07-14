package com.hbm.hazard;

import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.hazard.type.HazardTypeBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HazardEntry {

    private final HazardTypeBase type;
    private float baseLevel;

    /**
     * Modifiers are evaluated in the order they're being applied to the entry.
     */
    private final List<HazardModifier> mods = new ArrayList<>();

    public HazardEntry(@NotNull HazardTypeBase type) {
        this(type, 1.0F);
    }

    public HazardEntry(@NotNull HazardTypeBase type, float level) {
        this.type = type;
        this.baseLevel = level;
    }

    public HazardEntry addMod(@NotNull HazardModifier mod) {
        this.mods.add(mod);
        return this;
    }

    public float getBaseLevel() {
        return baseLevel;
    }

    public List<HazardModifier> getMods() {
        return mods;
    }

    public void applyHazard(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        float level = HazardModifier.evalAllModifiers(stack, entity, baseLevel, mods);
        type.onUpdate(entity, level, stack);
    }

    @NotNull
    public HazardTypeBase getType() {
        return this.type;
    }

    @NotNull
    public HazardEntry clone() {
        return clone(1.0F);
    }

    @NotNull
    public HazardEntry clone(float mult) {
        HazardEntry clone = new HazardEntry(type, baseLevel * mult);
        clone.mods.addAll(this.mods);
        return clone;
    }
}