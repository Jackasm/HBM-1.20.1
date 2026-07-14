package com.hbm.handler.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AvailableAbilities {
    private final HashMap<IBaseAbility, Integer> abilities = new HashMap<>();

    public AvailableAbilities() { }

    public void addAbility(IBaseAbility ability, int level) {
        if(ability.isAllowed()) {
            abilities.put(ability, level);
        }
    }

    public AvailableAbilities addToolAbilities() {
        addAbility(IToolAreaAbility.NONE, 0);
        addAbility(IToolHarvestAbility.NONE, 0);
        return this;
    }

    public int maxLevel(IBaseAbility ability) {
        return abilities.getOrDefault(ability, -1);
    }

    public Map<IBaseAbility, Integer> get() {
        return Collections.unmodifiableMap(abilities);
    }

    public Map<IWeaponAbility, Integer> getWeaponAbilities() {
        return abilities.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof IWeaponAbility)
                .collect(Collectors.toMap(
                        entry -> (IWeaponAbility) entry.getKey(),
                        Map.Entry::getValue
                ));
    }


    public Map<IToolAreaAbility, Integer> getToolAreaAbilities() {
        return abilities.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof IToolAreaAbility)
                .collect(Collectors.toMap(
                        entry -> (IToolAreaAbility) entry.getKey(),
                        Map.Entry::getValue
                ));
    }

    public Map<IToolHarvestAbility, Integer> getToolHarvestAbilities() {
        return abilities.entrySet().stream()
                .filter(entry -> entry.getKey() instanceof IToolHarvestAbility)
                .collect(Collectors.toMap(
                        entry -> (IToolHarvestAbility) entry.getKey(),
                        Map.Entry::getValue
                ));
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(List<Component> tooltip, @Nullable ToolPreset activePreset) {
        List<Map.Entry<IBaseAbility, Integer>> toolAbilities = abilities.entrySet().stream()
                .filter(entry -> (entry.getKey() instanceof IToolAreaAbility && entry.getKey() != IToolAreaAbility.NONE) ||
                        (entry.getKey() instanceof IToolHarvestAbility && entry.getKey() != IToolHarvestAbility.NONE))
                .sorted(Map.Entry.<IBaseAbility, Integer>comparingByKey()
                        .thenComparing(Map.Entry.comparingByValue()))
                .toList();

        if(!toolAbilities.isEmpty()) {
            tooltip.add(Component.literal("Abilities:").withStyle(ChatFormatting.GRAY));

            Component noneText = Component.translatable("item.hbm.ability.none");
            if (activePreset != null && activePreset.isNone()) {
                tooltip.add(Component.literal("  > ").append(noneText)
                        .withStyle(ChatFormatting.DARK_GREEN));
            } else {
                tooltip.add(Component.literal("    ").append(noneText)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }

            toolAbilities.forEach(entry -> {
                IBaseAbility ability = entry.getKey();
                int level = entry.getValue();
                Component abilityName = ability.getFullName(level);

                // Проверяем, активна ли эта способность в текущем пресете
                boolean isActive = activePreset != null && isAbilityActive(ability, activePreset);

                // Добавляем стрелочку если эта способность активна
                if (isActive) {
                    tooltip.add(Component.literal("  > ").append(abilityName.copy())
                            .withStyle(ChatFormatting.DARK_GREEN));
                } else {
                    tooltip.add(Component.literal("    ").append(abilityName.copy())
                            .withStyle(ChatFormatting.GOLD));
                }
            });

            // Обновляем описание
            tooltip.add(Component.literal("Right click in air to cycle abilities").withStyle(ChatFormatting.BLUE));
            tooltip.add(Component.literal("Shift + Right click in air to disable").withStyle(ChatFormatting.BLUE));
        }

        List<Map.Entry<IBaseAbility, Integer>> weaponAbilities = abilities.entrySet().stream()
                .filter(entry -> (entry.getKey() instanceof IWeaponAbility && entry.getKey() != IWeaponAbility.NONE))
                .sorted(Map.Entry.<IBaseAbility, Integer>comparingByKey()
                        .thenComparing(Map.Entry.comparingByValue()))
                .toList();

        if(!weaponAbilities.isEmpty()) {
            tooltip.add(Component.literal("Weapon modifiers:").withStyle(ChatFormatting.GRAY));

            weaponAbilities.forEach(entry -> {
                IBaseAbility ability = entry.getKey();
                int level = entry.getValue();
                tooltip.add(Component.literal("  ").append(ability.getFullName(level).copy())
                        .withStyle(ChatFormatting.RED));
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(List<Component> tooltip) {
        addInformation(tooltip, null);
    }

    private boolean isAbilityActive(IBaseAbility ability, ToolPreset activePreset) {
        return (ability instanceof IToolAreaAbility && ability == activePreset.areaAbility) ||
                (ability instanceof IToolHarvestAbility && ability == activePreset.harvestAbility);
    }
}