package com.hbm.render.overlay;

import com.hbm.extprop.HbmLivingProps;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EntityOverlayProvider implements IOverlayProvider {

    @Override
    public boolean shouldRender(OverlayContext context) {
        return context.mc().hitResult != null &&
                context.mc().hitResult.getType() == HitResult.Type.ENTITY;
    }

    @Override
    public List<OverlaySection> getSections(OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        EntityHitResult entityHit = (EntityHitResult) context.mc().hitResult;
        assert entityHit != null;
        var entity = entityHit.getEntity();

        // Основная информация
        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.ENTITY_MAIN);
        mainSection.addLine(Component.literal(entity.getDisplayName().getString())
                .withStyle(style -> style.withColor(0xFFAA00)));

        // Здоровье для живых существ
        if (entity instanceof LivingEntity living) {
            float health = living.getHealth();
            float maxHealth = living.getMaxHealth();
            float percentage = (health / maxHealth) * 100;

            // Health bar
            String healthBar = createHealthBar(percentage);
            mainSection.addLine(Component.literal("HP: " + String.format("%.1f/%.1f", health, maxHealth))
                    .withStyle(style -> style.withColor(getHealthColor(percentage))));
            mainSection.addLine(Component.literal(healthBar)
                    .withStyle(style -> style.withColor(getHealthColor(percentage))));

            // Броня для игроков
            if (living instanceof Player player) {
                int armor = player.getArmorValue();
                if (armor > 0) {
                    mainSection.addLine(Component.literal("Броня: " + armor)
                            .withStyle(style -> style.withColor(0x87CEEB)));
                }
            }

            // Дополнительная информация для животных
            if (living instanceof Animal animal) {
                // Кулдаун размножения
                if (animal.getAge() < 0) {
                    int cooldown = -animal.getAge() / 20; // в секундах
                    mainSection.addLine(Component.literal("Размножение: " + cooldown + " сек")
                            .withStyle(style -> style.withColor(0xFF69B4)));
                } else if (animal.isInLove()) {
                    mainSection.addLine(Component.literal("♥ Влюблён")
                            .withStyle(style -> style.withColor(0xFF1493)));
                }
            }
        }

        sections.add(mainSection);

        // Информация о снаряжении (для мобов с броней)
        if (entity instanceof LivingEntity living) {
            var equipmentSection = createEquipmentSection(living);
            if (equipmentSection != null) {
                sections.add(equipmentSection);
            }
        }

        if (entity instanceof LivingEntity living) {
            OverlaySection debuffSection = createHbmDebuffsSection(living);
            if (debuffSection != null) {
                sections.add(debuffSection);
            }
        }

        // Стандартные эффекты Minecraft
        if (entity instanceof LivingEntity living) {
            OverlaySection effectsSection = createMinecraftEffectsSection(living);
            if (effectsSection != null) {
                sections.add(effectsSection);
            }
        }

        return sections;
    }

    private String createHealthBar(float percentage) {
        int bars = 10;
        int filled = (int) (percentage / 100 * bars);
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        return bar.toString();
    }

    private int getHealthColor(float percentage) {
        if (percentage > 75) return 0x00FF00; // Зеленый
        if (percentage > 50) return 0xFFFF00; // Желтый
        if (percentage > 25) return 0xFFA500; // Оранжевый
        return 0xFF0000; // Красный
    }

    private OverlaySection createEquipmentSection(LivingEntity entity) {
        // Реализация отображения снаряжения
        return null;
    }

    private OverlaySection createHbmDebuffsSection(LivingEntity entity) {
        HbmLivingProps props = HbmLivingProps.getData(entity);
        if (props == null) return null;

        List<Component> debuffLines = new ArrayList<>();

        // Фосфор (горение фосфором)
        int phosphorus = HbmLivingProps.getPhosphorus(entity);
        if (phosphorus > 0) {
            String timeLeft = formatTicksToTime(phosphorus);
            debuffLines.add(Component.literal("☠ Фосфор: " + timeLeft)
                    .withStyle(ChatFormatting.RED));
        }

        // Обычный огонь
        int fire = HbmLivingProps.getFire(entity);
        if (fire > 0) {
            String timeLeft = formatTicksToTime(fire);
            debuffLines.add(Component.literal("🔥 Огонь: " + timeLeft)
                    .withStyle(ChatFormatting.GOLD));
        }

        // Balefire
        int balefire = HbmLivingProps.getBalefire(entity);
        if (balefire > 0) {
            String timeLeft = formatTicksToTime(balefire);
            debuffLines.add(Component.literal("☢ Balefire: " + timeLeft)
                    .withStyle(ChatFormatting.DARK_GREEN));
        }

        // Black fire
        int blackFire = HbmLivingProps.getBlackFire(entity);
        if (blackFire > 0) {
            String timeLeft = formatTicksToTime(blackFire);
            debuffLines.add(Component.literal("☠ Черный огонь: " + timeLeft)
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }

        // Черная болезнь легких (Black Lung)
        int blackLung = HbmLivingProps.getBlackLung(entity);
        if (blackLung > 0) {
            String severity = String.format("%.1f%%", (blackLung / (float)HbmLivingProps.MAX_BLACK_LUNG) * 100);
            debuffLines.add(Component.literal("🫁 Черные легкие: " + severity)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        // Асбестоз
        int asbestos = HbmLivingProps.getAsbestos(entity);
        if (asbestos > 0) {
            String severity = String.format("%.1f%%", (asbestos / (float)HbmLivingProps.MAX_ASBESTOS) * 100);
            debuffLines.add(Component.literal("🪨 Асбестоз: " + severity)
                    .withStyle(ChatFormatting.GRAY));
        }

        // Радиация
        float radiation = HbmLivingProps.getRadiation(entity);
        if (radiation > 0) {
            String radLevel = String.format("%.1f RAD", radiation);
            debuffLines.add(Component.literal("☢ Радиация: " + radLevel)
                    .withStyle(getRadiationColor(radiation)));
        }

        // Digamma
        float digamma = HbmLivingProps.getDigamma(entity);
        if (digamma > 0) {
            String digammaLevel = String.format("%.2f ψ", digamma);
            debuffLines.add(Component.literal("ψ Digamma: " + digammaLevel)
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }

        // Нефтяное пятно (Oil)
        int oil = HbmLivingProps.getOil(entity);
        if (oil > 0) {
            String timeLeft = formatTicksToTime(oil);
            debuffLines.add(Component.literal("🛢️ Нефть: " + timeLeft)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        // Контаминация (Contagion)
        int contagion = HbmLivingProps.getContagion(entity);
        if (contagion > 0) {
            String timeLeft = formatTicksToTime(contagion);
            debuffLines.add(Component.literal("🦠 Заражение: " + timeLeft)
                    .withStyle(ChatFormatting.DARK_GREEN));
        }

        // Таймер (если есть)
        int timer = HbmLivingProps.getTimer(entity);
        if (timer > 0) {
            String timeLeft = formatTicksToTime(timer);
            debuffLines.add(Component.literal("⏱️ Таймер: " + timeLeft)
                    .withStyle(ChatFormatting.RED));
        }

        if (debuffLines.isEmpty()) {
            return null;
        }

        OverlaySection debuffSection = new OverlaySection(OverlaySection.Type.DEBUFFS);
        debuffSection.addLine(Component.literal("Дебаффы:")
                .withStyle(ChatFormatting.DARK_RED));

        for (Component line : debuffLines) {
            debuffSection.addLine(line);
        }

        return debuffSection;
    }

    private OverlaySection createMinecraftEffectsSection(LivingEntity entity) {
        Map<MobEffect, MobEffectInstance> activeEffects = entity.getActiveEffectsMap();
        if (activeEffects.isEmpty()) {
            return null;
        }

        // Фильтруем только негативные эффекты
        List<MobEffectInstance> debuffs = activeEffects.values().stream()
                .filter(effect -> !effect.getEffect().isBeneficial())
                .sorted(Comparator.comparing(e -> e.getEffect().getDisplayName().getString()))
                .toList();

        if (debuffs.isEmpty()) {
            return null;
        }

        OverlaySection effectsSection = new OverlaySection(OverlaySection.Type.EFFECTS);
        effectsSection.addLine(Component.literal("Эффекты Minecraft:")
                .withStyle(ChatFormatting.DARK_PURPLE));

        for (MobEffectInstance effect : debuffs) {
            String effectName = effect.getEffect().getDisplayName().getString();
            String amplifier = getAmplifierText(effect.getAmplifier());
            String duration = formatTicksToTime(effect.getDuration());

            MutableComponent line = Component.literal("• " + effectName + amplifier + ": " + duration);
            line.withStyle(effect.getEffect().getCategory().getTooltipFormatting());

            effectsSection.addLine(line);
        }

        return effectsSection;
    }

    private String getAmplifierText(int amplifier) {
        if (amplifier == 0) return "";
        if (amplifier == 1) return " II";
        if (amplifier == 2) return " III";
        if (amplifier == 3) return " IV";
        if (amplifier == 4) return " V";
        return " " + (amplifier + 1);
    }

    private String formatTicksToTime(int ticks) {
        if (ticks >= 1200) { // 1 минута и больше
            int minutes = ticks / 1200;
            int seconds = (ticks % 1200) / 20;
            return String.format("%d:%02d", minutes, seconds);
        } else { // Меньше минуты
            int seconds = ticks / 20;
            return seconds + " сек";
        }
    }

    private ChatFormatting getRadiationColor(float radiation) {
        if (radiation < 100) return ChatFormatting.YELLOW;
        if (radiation < 500) return ChatFormatting.GOLD;
        if (radiation < 1000) return ChatFormatting.RED;
        return ChatFormatting.DARK_RED;
    }
}