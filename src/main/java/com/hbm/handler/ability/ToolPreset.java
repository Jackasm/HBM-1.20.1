package com.hbm.handler.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class ToolPreset {
    public IToolAreaAbility areaAbility = IToolAreaAbility.NONE;
    public int areaAbilityLevel = 0;
    public IToolHarvestAbility harvestAbility = IToolHarvestAbility.NONE;
    public int harvestAbilityLevel = 0;

    public ToolPreset() {
    }

    public ToolPreset(IToolAreaAbility areaAbility, IToolHarvestAbility harvestAbility) {
        this.areaAbility = areaAbility;
        this.harvestAbility = harvestAbility;
    }

    public ToolPreset(IToolAreaAbility areaAbility, int areaAbilityLevel,
                      IToolHarvestAbility harvestAbility, int harvestAbilityLevel) {
        this.areaAbility = areaAbility;
        this.areaAbilityLevel = areaAbilityLevel;
        this.harvestAbility = harvestAbility;
        this.harvestAbilityLevel = harvestAbilityLevel;
    }

    public Component getMessage() {
        if(isNone()) {
            return Component.literal("Ability deactivated")
                    .withStyle(ChatFormatting.GOLD); // Золотой цвет
        }

        boolean hasArea = areaAbility != IToolAreaAbility.NONE;
        boolean hasHarvest = harvestAbility != IToolHarvestAbility.NONE;

        // Используем MutableComponent вместо Component
        MutableComponent message = Component.literal("Enabled ");

        if(hasArea) {
            // Добавляем название способности области
            message.append(areaAbility.getFullName(areaAbilityLevel));
        }

        if(hasArea && hasHarvest) {
            // Добавляем разделитель
            message.append(" + ");
        }

        if(hasHarvest) {
            // Добавляем название способности сбора
            message.append(harvestAbility.getFullName(harvestAbilityLevel));
        }

        // Применяем стиль ко всему компоненту
        return message.withStyle(ChatFormatting.YELLOW);
    }

    public boolean isNone() {
        return areaAbility == IToolAreaAbility.NONE && harvestAbility == IToolHarvestAbility.NONE;
    }

    public void writeToNBT(CompoundTag nbt) {
        nbt.putString("area", areaAbility.getName());
        nbt.putInt("areaLevel", areaAbilityLevel);
        nbt.putString("harvest", harvestAbility.getName());
        nbt.putInt("harvestLevel", harvestAbilityLevel);
    }

    public void readFromNBT(CompoundTag nbt) {
        areaAbility = IToolAreaAbility.getByName(nbt.getString("area"));
        areaAbilityLevel = nbt.getInt("areaLevel");
        harvestAbility = IToolHarvestAbility.getByName(nbt.getString("harvest"));
        harvestAbilityLevel = nbt.getInt("harvestLevel");

        areaAbilityLevel = Math.min(areaAbilityLevel, areaAbility.levels() - 1);
        harvestAbilityLevel = Math.min(harvestAbilityLevel, harvestAbility.levels() - 1);
    }
}