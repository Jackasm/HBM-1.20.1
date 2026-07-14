package com.hbm.inventory.fluid.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.ArmorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class FT_Toxin extends FluidTrait {

    public List<ToxinEntry> entries = new ArrayList<>();

    public FT_Toxin addEntry(ToxinEntry entry) {
        entries.add(entry);
        return this;
    }

    @Override
    public void addInfoHidden(List<Component> info) {
        info.add(Component.literal("[Toxin]").withStyle(ChatFormatting.LIGHT_PURPLE));

        for (ToxinEntry entry : entries) {
            entry.addInfo(info);
        }
    }

    public void affect(LivingEntity entity, double intensity) {
        for (ToxinEntry entry : entries) {
            entry.poison(entity, intensity);
        }
    }

    public static abstract class ToxinEntry {

        public HazardClass clazz;
        public boolean fullBody = false;

        public ToxinEntry(HazardClass clazz, boolean fullBody) {
            this.clazz = clazz;
            this.fullBody = fullBody;
        }

        public boolean isProtected(LivingEntity entity) {

            boolean hasMask = clazz == null;
            boolean hasSuit = !fullBody;

            if (clazz != null && ArmorRegistry.hasAllProtection(entity, 3, clazz)) {
                ArmorUtil.damageGasMaskFilter(entity, 1);
                hasMask = true;
            }

            if (fullBody && ArmorUtil.checkForHazmat(entity)) {
                hasSuit = true;
            }

            return hasMask && hasSuit;
        }

        public abstract void poison(LivingEntity entity, double intensity);

        public abstract void addInfo(List<Component> info);
    }

    public static class ToxinDirectDamage extends ToxinEntry {

        public ResourceKey<DamageType> damageType;
        public float amount;
        public int delay;

        public ToxinDirectDamage(ResourceKey<DamageType> damageType, float amount, int delay, HazardClass clazz, boolean fullBody) {
            super(clazz, fullBody);
            this.damageType = damageType;
            this.amount = amount;
            this.delay = delay;
        }

        @Override
        public void poison(LivingEntity entity, double intensity) {

            if (isProtected(entity)) return;

            if (delay == 0 || entity.level().getGameTime() % delay == 0) {
                // Получаем Holder для DamageType из реестра
                var registry = entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
                var holder = registry.getHolderOrThrow(damageType);
                DamageSource source = new DamageSource(holder);
                entity.hurt(source, (float) (amount * intensity));
            }
        }

        @Override
        public void addInfo(List<Component> info) {
            MutableComponent line = Component.literal("- ")
                    .append(Component.translatable(clazz.name().toLowerCase()))
                    .withStyle(ChatFormatting.YELLOW);

            if (fullBody) {
                line.append(Component.literal(" (requires hazmat suit)").withStyle(ChatFormatting.RED));
            }

            line.append(Component.literal(": ").withStyle(ChatFormatting.YELLOW));
            line.append(Component.literal(String.format("%.1f DPS", amount * 20 / delay)).withStyle(ChatFormatting.YELLOW));

            info.add(line);
        }
    }

    public static class ToxinEffects extends ToxinEntry {

        public List<MobEffectInstance> effects = new ArrayList<>();

        public ToxinEffects(HazardClass clazz, boolean fullBody) {
            super(clazz, fullBody);
        }

        public ToxinEffects add(MobEffectInstance... effs) {
            Collections.addAll(this.effects, effs);
            return this;
        }

        @Override
        public void poison(LivingEntity entity, double intensity) {

            if (isProtected(entity)) return;

            for (MobEffectInstance eff : effects) {
                entity.addEffect(new MobEffectInstance(
                        eff.getEffect(),
                        (int) (eff.getDuration() * intensity),
                        eff.getAmplifier(),
                        eff.isAmbient(),
                        eff.isVisible(),
                        eff.showIcon()
                ));
            }
        }

        @Override
        public void addInfo(List<Component> info) {
            MutableComponent line = Component.literal("- ")
                    .append(Component.translatable(clazz.name().toLowerCase()))
                    .withStyle(ChatFormatting.YELLOW);

            if (fullBody) {
                line.append(Component.literal(" (requires hazmat suit)").withStyle(ChatFormatting.RED));
            }

            info.add(line);

            for (MobEffectInstance eff : effects) {
                MutableComponent effectLine = Component.literal("   - ")
                        .append(Component.translatable(eff.getEffect().getDescriptionId()))
                        .withStyle(ChatFormatting.YELLOW);

                if (eff.getAmplifier() > 0) {
                    effectLine.append(Component.literal(" " + getAmplifierString(eff.getAmplifier())));
                }

                effectLine.append(Component.literal(" " + ticksToElapsedTime(eff.getDuration())));

                info.add(effectLine);
            }
        }

        private String getAmplifierString(int amplifier) {
            return switch (amplifier) {
                case 1 -> "II";
                case 2 -> "III";
                case 3 -> "IV";
                case 4 -> "V";
                default -> "I";
            };
        }

        private String ticksToElapsedTime(int ticks) {
            int seconds = ticks / 20;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        }
    }

    @Override
    public void serializeJSON(JsonWriter writer) throws IOException {

        writer.name("entries").beginArray();

        for (ToxinEntry entry : entries) {
            writer.beginObject();

            if (entry instanceof ToxinDirectDamage e) {
                writer.name("type").value("directdamage");
                writer.name("amount").value(e.amount);
                writer.name("source").value(e.damageType.location().toString());
                writer.name("delay").value(e.delay);
                writer.name("hazmat").value(e.fullBody);
                writer.name("masktype").value(e.clazz.name());
            }
            if (entry instanceof ToxinEffects e) {
                writer.name("type").value("effects");
                writer.name("effects").beginArray();
                writer.setIndent("");
                for (MobEffectInstance effect : e.effects) {
                    writer.beginArray();
                    writer.value(Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect())).toString());
                    writer.value(effect.getDuration());
                    writer.value(effect.getAmplifier());
                    writer.value(effect.isAmbient());
                    writer.value(effect.isVisible());
                    writer.value(effect.showIcon());
                    writer.endArray();
                }
                writer.endArray();
                writer.setIndent("  ");
                writer.name("hazmat").value(e.fullBody);
                writer.name("masktype").value(e.clazz.name());
            }

            writer.endObject();
        }

        writer.endArray();
    }

    @Override
    public void deserializeJSON(JsonObject obj) {
        JsonArray array = obj.get("entries").getAsJsonArray();

        for (int i = 0; i < array.size(); i++) {
            JsonObject entry = array.get(i).getAsJsonObject();
            String name = entry.get("type").getAsString();

            if (name.equals("directdamage")) {
                // Получаем тип урона из строки
                ResourceKey<DamageType> damageTypeKey = ResourceKey.create(
                        Registries.DAMAGE_TYPE,
                        ResLocation(entry.get("source").getAsString())
                );

                ToxinDirectDamage e = new ToxinDirectDamage(
                        damageTypeKey,
                        entry.get("amount").getAsFloat(),
                        entry.get("delay").getAsInt(),
                        HazardClass.valueOf(entry.get("masktype").getAsString()),
                        entry.get("hazmat").getAsBoolean()
                );
                this.entries.add(e);
            }

            if (name.equals("effects")) {
                ToxinEffects e = new ToxinEffects(
                        HazardClass.valueOf(entry.get("masktype").getAsString()),
                        entry.get("hazmat").getAsBoolean()
                );
                JsonArray effects = entry.get("effects").getAsJsonArray();
                for (int j = 0; j < effects.size(); j++) {
                    JsonArray effect = effects.get(j).getAsJsonArray();
                    MobEffectInstance mobEffect = new MobEffectInstance(
                            Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.get(
                                    ResLocation(effect.get(0).getAsString()))),
                            effect.get(1).getAsInt(),
                            effect.get(2).getAsInt(),
                            effect.get(3).getAsBoolean(),
                            effect.get(4).getAsBoolean(),
                            effect.get(5).getAsBoolean()
                    );
                    e.effects.add(mobEffect);
                }
                this.entries.add(e);
            }
        }
    }
}