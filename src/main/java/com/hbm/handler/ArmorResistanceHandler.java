package com.hbm.handler;

import com.hbm.items.ModArmorItems;
import com.hbm.util.ModDamageSource;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class ArmorResistanceHandler {

    public static float currentPierceDT = 0F;
    public static float currentPierceDR = 0F;

    public static final String CATEGORY_PHYSICAL = "PHYS";
    public static final String CATEGORY_EXPLOSION = "EXPL";
    public static final String CATEGORY_FIRE = "FIRE";
    public static final String CATEGORY_ENERGY = "EN";
    public static final String CATEGORY_LASER = "LASER";
    public static final String CATEGORY_MICROWAVE = "MICROWAVE";
    public static final String CATEGORY_SUBATOMIC = "SUBATOMIC";
    public static final String CATEGORY_ELECTRIC = "ELECTRIC";
    public static final String CATEGORY_RADIATION = "RADIATION";
    public static final String CATEGORY_ACID = "ACID";
    public static final String CATEGORY_OTHER = "OTHER";

    public static final Map<Item, DamageResistanceStats> ITEM_STATS = new HashMap<>();
    public static final Map<ArmorSet, DamageResistanceStats> SET_STATS = new HashMap<>();
    private static final Map<Class<? extends Entity>, DamageResistanceStats> ENTITY_STATS = new HashMap<>();

    public static final Map<Item, List<ArmorSet>> ITEM_TO_SETS = new HashMap<>();

    public static class ResistanceStats {
        public final float threshold;
        public final float reduction;

        public ResistanceStats(float threshold, float reduction) {
            this.threshold = threshold;
            this.reduction = reduction;
        }

        public boolean hasResistance() {
            return threshold > 0 || reduction > 0;
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(ArmorResistanceHandler.class);
        initDefaults();
    }

    private static void initDefaults() {
        registerEntityResistance(Creeper.class, new DamageResistanceStats()
                .addCategory(CATEGORY_EXPLOSION, 2.0F, 0.25F));

        registerItemResistance(ModArmorItems.JACKET.get(), new DamageResistanceStats()
                .addCategory(CATEGORY_PHYSICAL, 5.0F, 0.5F));
        registerItemResistance(ModArmorItems.JACKET_2.get(), new DamageResistanceStats()
                .addCategory(CATEGORY_PHYSICAL, 5.0F, 0.5F));

        registerSet(
                ModArmorItems.STEEL_HELMET.get(),
                ModArmorItems.STEEL_CHESTPLATE.get(),
                ModArmorItems.STEEL_LEGGINGS.get(),
                ModArmorItems.STEEL_BOOTS.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.TITANIUM_HELMET.get(),
                ModArmorItems.TITANIUM_CHESTPLATE.get(),
                ModArmorItems.TITANIUM_LEGGINGS.get(),
                ModArmorItems.TITANIUM_BOOTS.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.ADVANCED_ALLOY_HELMET.get(),
                ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get(),
                ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get(),
                ModArmorItems.ADVANCED_ALLOY_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 2.0F, 0.10F)
        );

        registerSet(
                ModArmorItems.COBALT_HELMET.get(),
                ModArmorItems.COBALT_CHESTPLATE.get(),
                ModArmorItems.COBALT_LEGGINGS.get(),
                ModArmorItems.COBALT_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 2.0F, 0.10F)
        );

        registerSet(
                ModArmorItems.STARMETAL_HELMET.get(),
                ModArmorItems.STARMETAL_CHESTPLATE.get(),
                ModArmorItems.STARMETAL_LEGGINGS.get(),
                ModArmorItems.STARMETAL_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 3.0F, 0.25F)
                        .setOther(1.0F, 0.10F)
        );

        registerSet(
                ModArmorItems.ZIRCONIUM_LEGGINGS.get(),
                ModArmorItems.ZIRCONIUM_LEGGINGS.get(),
                ModArmorItems.ZIRCONIUM_LEGGINGS.get(),
                ModArmorItems.ZIRCONIUM_LEGGINGS.get(),
                new DamageResistanceStats()
                        .setOther(0.0F, 1.0F)
        );

        registerSet(
                ModArmorItems.DNT_HELMET.get(),
                ModArmorItems.DNT_CHESTPLATE.get(),
                ModArmorItems.DNT_LEGGINGS.get(),
                ModArmorItems.DNT_BOOTS.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.CMB_HELMET.get(),
                ModArmorItems.CMB_CHESTPLATE.get(),
                ModArmorItems.CMB_LEGGINGS.get(),
                ModArmorItems.CMB_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 5.0F, 0.50F)
                        .setOther(5.0F, 0.25F)
        );

        registerSet(
                ModArmorItems.SCHRABIDIUM_HELMET.get(),
                ModArmorItems.SCHRABIDIUM_CHESTPLATE.get(),
                ModArmorItems.SCHRABIDIUM_LEGGINGS.get(),
                ModArmorItems.SCHRABIDIUM_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 10.0F, 0.65F)
                        .setOther(5.0F, 0.50F)
        );

        registerSet(
                ModArmorItems.ROBES_HELMET.get(),
                ModArmorItems.ROBES_CHESTPLATE.get(),
                ModArmorItems.ROBES_LEGGINGS.get(),
                ModArmorItems.ROBES_BOOTS.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.SECURITY_HELMET.get(),
                ModArmorItems.SECURITY_CHESTPLATE.get(),
                ModArmorItems.SECURITY_LEGGINGS.get(),
                ModArmorItems.SECURITY_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 5.0F, 0.50F)
                        .addCategory(CATEGORY_EXPLOSION, 2.0F, 0.25F)
        );

        registerSet(
                ModArmorItems.STEAMSUIT_HELMET.get(),
                ModArmorItems.STEAMSUIT_CHESTPLATE.get(),
                ModArmorItems.STEAMSUIT_LEGGINGS.get(),
                ModArmorItems.STEAMSUIT_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 2.0F, 0.15F)
                        .addCategory(CATEGORY_FIRE, 0.5F, 0.25F)
                        .addExact("fall", 5.0F, 0.25F)
                        .setOther(0.0F, 0.10F)
        );

        registerSet(
                ModArmorItems.T45_HELMET.get(),
                ModArmorItems.T45_CHESTPLATE.get(),
                ModArmorItems.T45_LEGGINGS.get(),
                ModArmorItems.T45_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 2.0F, 0.15F)
                        .addCategory(CATEGORY_FIRE, 0.5F, 0.35F)
                        .addCategory(CATEGORY_EXPLOSION, 5.0F, 0.25F)
                        .addExact("fall", 0.0F, 1.0F)
                        .setOther(0.0F, 0.10F)
        );

        registerSet(
                ModArmorItems.T51_HELMET.get(),
                ModArmorItems.T51_CHESTPLATE.get(),
                ModArmorItems.T51_LEGGINGS.get(),
                ModArmorItems.T51_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 2.0F, 0.15F)
                        .addCategory(CATEGORY_FIRE, 0.5F, 0.35F)
                        .addCategory(CATEGORY_EXPLOSION, 5.0F, 0.25F)
                        .addExact("fall", 0.0F, 1.0F)
                        .setOther(0.0F, 0.10F)
        );

        registerSet(
                ModArmorItems.AJR_HELMET.get(),
                ModArmorItems.AJR_CHESTPLATE.get(),
                ModArmorItems.AJR_LEGGINGS.get(),
                ModArmorItems.AJR_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 4.0F, 0.15F)
                        .addCategory(CATEGORY_FIRE, 0.5F, 0.35F)
                        .addCategory(CATEGORY_EXPLOSION, 7.5F, 0.25F)
                        .addExact("fall", 0.0F, 1.0F)
                        .setOther(0.0F, 0.15F)
        );

        registerSet(
                ModArmorItems.AJRO_HELMET.get(),
                ModArmorItems.AJRO_CHESTPLATE.get(),
                ModArmorItems.AJRO_LEGGINGS.get(),
                ModArmorItems.AJRO_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 4.0F, 0.15F)
                        .addCategory(CATEGORY_FIRE, 0.5F, 0.35F)
                        .addCategory(CATEGORY_EXPLOSION, 7.5F, 0.25F)
                        .addExact("fall", 0.0F, 1.0F)
                        .setOther(0.0F, 0.15F)
        );

        registerSet(
                ModArmorItems.RPA_HELMET.get(),
                ModArmorItems.RPA_CHESTPLATE.get(),
                ModArmorItems.RPA_LEGGINGS.get(),
                ModArmorItems.RPA_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 25.0F, 0.65F)
                        .addCategory(CATEGORY_FIRE, 10.0F, 0.90F)
                        .addCategory(CATEGORY_EXPLOSION, 15.0F, 0.25F)
                        .addCategory(CATEGORY_ENERGY, 25.0F, 0.75F)
                        .addExact("fall", 0.0F, 1.0F)
                        .setOther(15.0F, 0.30F)
        );

        DamageResistanceStats bjStats = new DamageResistanceStats()
                .addCategory(CATEGORY_PHYSICAL, 5.0F, 0.50F)
                .addCategory(CATEGORY_FIRE, 2.5F, 0.50F)
                .addCategory(CATEGORY_EXPLOSION, 10.0F, 0.25F)
                .addExact("fall", 0.0F, 1.0F)
                .setOther(2.0F, 0.15F);

        registerSet(
                ModArmorItems.BJ_HELMET.get(),
                ModArmorItems.BJ_CHESTPLATE.get(),
                ModArmorItems.BJ_LEGGINGS.get(),
                ModArmorItems.BJ_BOOTS.get(),
                bjStats
        );

        registerSet(
                ModArmorItems.BJ_HELMET.get(),
                ModArmorItems.BJ_CHESTPLATE_JETPACK.get(),
                ModArmorItems.BJ_LEGGINGS.get(),
                ModArmorItems.BJ_BOOTS.get(),
                bjStats
        );

        registerSet(
                ModArmorItems.ENVSUIT_HELMET.get(),
                ModArmorItems.ENVSUIT_CHESTPLATE.get(),
                ModArmorItems.ENVSUIT_LEGGINGS.get(),
                ModArmorItems.ENVSUIT_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_FIRE, 2.0F, 0.75F)
                        .addExact("drowning", 0.0F, 1.0F)
                        .addExact("fall", 5.0F, 0.75F)
                        .setOther(0.0F, 0.10F)
        );

        registerSet(
                ModArmorItems.HEV_HELMET.get(),
                ModArmorItems.HEV_CHESTPLATE.get(),
                ModArmorItems.HEV_LEGGINGS.get(),
                ModArmorItems.HEV_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 2.0F, 0.25F)
                        .addCategory(CATEGORY_FIRE, 0.5F, 0.50F)
                        .addCategory(CATEGORY_EXPLOSION, 5.0F, 0.25F)
                        .addExact("inFire", 0.0F, 1.0F)
                        .addExact("fall", 10.0F, 0.0F)
                        .setOther(2.0F, 0.25F)
        );

        registerSet(
                ModArmorItems.BISMUTH_HELMET.get(),
                ModArmorItems.BISMUTH_CHESTPLATE.get(),
                ModArmorItems.BISMUTH_LEGGINGS.get(),
                ModArmorItems.BISMUTH_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 2.0F, 0.15F)
                        .addCategory(CATEGORY_FIRE, 5.0F, 0.50F)
                        .addCategory(CATEGORY_EXPLOSION, 5.0F, 0.25F)
                        .addExact("fall", 0.0F, 1.0F)
                        .setOther(2.0F, 0.25F)
        );

        registerSet(
                ModArmorItems.FAU_HELMET.get(),
                ModArmorItems.FAU_CHESTPLATE.get(),
                ModArmorItems.FAU_LEGGINGS.get(),
                ModArmorItems.FAU_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_EXPLOSION, 50.0F, 0.95F)
                        .addCategory(CATEGORY_FIRE, 0.0F, 1.0F)
                        .addExact("laser", 25.0F, 0.95F)
                        .addExact("fall", 0.0F, 1.0F)
                        .setOther(100.0F, 0.99F)
        );

        registerSet(
                ModArmorItems.TRENCHMASTER_HELMET.get(),
                ModArmorItems.TRENCHMASTER_CHESTPLATE.get(),
                ModArmorItems.TRENCHMASTER_LEGGINGS.get(),
                ModArmorItems.TRENCHMASTER_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_PHYSICAL, 5.0F, 0.50F)
                        .addCategory(CATEGORY_FIRE, 5.0F, 0.50F)
                        .addCategory(CATEGORY_EXPLOSION, 5.0F, 0.25F)
                        .addExact("fall", 10.0F, 0.50F)
                        .setOther(5.0F, 0.25F)
        );

        registerSet(
                ModArmorItems.EUPHEMIUM_HELMET.get(),
                ModArmorItems.EUPHEMIUM_CHESTPLATE.get(),
                ModArmorItems.EUPHEMIUM_LEGGINGS.get(),
                ModArmorItems.EUPHEMIUM_BOOTS.get(),
                new DamageResistanceStats()
                        .setOther(1000000.0F, 1.0F)
        );

        registerSet(
                ModArmorItems.HAZMAT_HELMET.get(),
                ModArmorItems.HAZMAT_CHESTPLATE.get(),
                ModArmorItems.HAZMAT_LEGGINGS.get(),
                ModArmorItems.HAZMAT_BOOTS.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.HAZMAT_HELMET_RED.get(),
                ModArmorItems.HAZMAT_CHESTPLATE_RED.get(),
                ModArmorItems.HAZMAT_LEGGINGS_RED.get(),
                ModArmorItems.HAZMAT_BOOTS_RED.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.HAZMAT_HELMET_GREY.get(),
                ModArmorItems.HAZMAT_CHESTPLATE_GREY.get(),
                ModArmorItems.HAZMAT_LEGGINGS_GREY.get(),
                ModArmorItems.HAZMAT_BOOTS_GREY.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.LIQUIDATOR_HELMET.get(),
                ModArmorItems.LIQUIDATOR_CHESTPLATE.get(),
                ModArmorItems.LIQUIDATOR_LEGGINGS.get(),
                ModArmorItems.LIQUIDATOR_BOOTS.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.HAZMAT_PAA_HELMET.get(),
                ModArmorItems.HAZMAT_PAA_CHESTPLATE.get(),
                ModArmorItems.HAZMAT_PAA_LEGGINGS.get(),
                ModArmorItems.HAZMAT_PAA_BOOTS.get(),
                new DamageResistanceStats()
        );

        registerSet(
                ModArmorItems.ASBESTOS_HELMET.get(),
                ModArmorItems.ASBESTOS_CHESTPLATE.get(),
                ModArmorItems.ASBESTOS_LEGGINGS.get(),
                ModArmorItems.ASBESTOS_BOOTS.get(),
                new DamageResistanceStats()
                        .addCategory(CATEGORY_FIRE, 10.0F, 0.90F)
        );
    }

    public static class ArmorSet {
        public final Item helmet;
        public final Item chestplate;
        public final Item leggings;
        public final Item boots;

        public ArmorSet(Item helmet, Item chestplate, Item leggings, Item boots) {
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArmorSet armorSet = (ArmorSet) o;
            return Objects.equals(helmet, armorSet.helmet) &&
                    Objects.equals(chestplate, armorSet.chestplate) &&
                    Objects.equals(leggings, armorSet.leggings) &&
                    Objects.equals(boots, armorSet.boots);
        }

        @Override
        public int hashCode() {
            return Objects.hash(helmet, chestplate, leggings, boots);
        }
    }

    public static class Resistance {
        public final float threshold;
        public final float reduction;

        public Resistance(float threshold, float reduction) {
            this.threshold = threshold;
            this.reduction = reduction;
        }
    }

    public static class DamageResistanceStats {
        public final Map<String, Resistance> exactResistances = new HashMap<>();
        public final Map<String, Resistance> categoryResistances = new HashMap<>();
        public Resistance otherResistance;

        public DamageResistanceStats addExact(String damageType, float dt, float dr) {
            exactResistances.put(damageType, new Resistance(dt, dr));
            return this;
        }

        public DamageResistanceStats addCategory(String category, float dt, float dr) {
            categoryResistances.put(category, new Resistance(dt, dr));
            return this;
        }

        public DamageResistanceStats setOther(float dt, float dr) {
            otherResistance = new Resistance(dt, dr);
            return this;
        }

        public Resistance getResistance(DamageSource source) {
            String msgId = source.getMsgId();

            for (Map.Entry<String, Resistance> entry : exactResistances.entrySet()) {
                if (msgId.equals(entry.getKey()) || source.is(getDamageTypeKey(entry.getKey()))) {
                    return entry.getValue();
                }
            }

            String category = getDamageCategory(source);
            Resistance categoryRes = categoryResistances.get(category);
            if (categoryRes != null) return categoryRes;

            return source.is(DamageTypeTags.BYPASSES_ARMOR) ? null : otherResistance;
        }

        public boolean hasResistance() {
            return !exactResistances.isEmpty() ||
                    !categoryResistances.isEmpty() ||
                    otherResistance != null;
        }
    }

    private static ResourceKey<DamageType> getDamageTypeKey(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResLocation(RefStrings.MODID, name));
    }

    public static ResistanceStats getItemResistance(ItemStack stack) {
        if (stack.isEmpty()) return new ResistanceStats(0, 0);

        Item item = stack.getItem();
        DamageResistanceStats stats = ITEM_STATS.get(item);

        if (stats != null && stats.hasResistance()) {
            float totalDT = 0;
            float totalDR = 0;

            for (Resistance res : stats.exactResistances.values()) {
                totalDT += res.threshold;
                totalDR += res.reduction;
            }

            for (Resistance res : stats.categoryResistances.values()) {
                totalDT += res.threshold;
                totalDR += res.reduction;
            }

            if (stats.otherResistance != null) {
                totalDT += stats.otherResistance.threshold;
                totalDR += stats.otherResistance.reduction;
            }

            totalDR = Math.min(totalDR, 0.95F);

            return new ResistanceStats(totalDT, totalDR);
        }

        return new ResistanceStats(0, 0);
    }

    public static ResistanceStats getSetResistance(ItemStack stack) {
        if (stack.isEmpty()) return new ResistanceStats(0, 0);

        Item item = stack.getItem();
        List<ArmorSet> sets = ITEM_TO_SETS.get(item);

        if (sets == null || sets.isEmpty()) {
            return new ResistanceStats(0, 0);
        }

        ArmorSet set = sets.get(0);
        DamageResistanceStats stats = SET_STATS.get(set);

        if (stats != null && stats.hasResistance()) {
            float totalDT = 0;
            float totalDR = 0;

            for (Resistance res : stats.exactResistances.values()) {
                totalDT += res.threshold;
                totalDR += res.reduction;
            }

            for (Resistance res : stats.categoryResistances.values()) {
                totalDT += res.threshold;
                totalDR += res.reduction;
            }

            if (stats.otherResistance != null) {
                totalDT += stats.otherResistance.threshold;
                totalDR += stats.otherResistance.reduction;
            }

            totalDR = Math.min(totalDR, 0.95F);

            return new ResistanceStats(totalDT, totalDR);
        }

        return new ResistanceStats(0, 0);
    }

    public static void registerSet(Item helmet, Item chestplate, Item leggings, Item boots, DamageResistanceStats stats) {
        ArmorSet set = new ArmorSet(helmet, chestplate, leggings, boots);
        SET_STATS.put(set, stats);

        if (helmet != null) ITEM_TO_SETS.computeIfAbsent(helmet, k -> new ArrayList<>()).add(set);
        if (chestplate != null) ITEM_TO_SETS.computeIfAbsent(chestplate, k -> new ArrayList<>()).add(set);
        if (leggings != null) ITEM_TO_SETS.computeIfAbsent(leggings, k -> new ArrayList<>()).add(set);
        if (boots != null) ITEM_TO_SETS.computeIfAbsent(boots, k -> new ArrayList<>()).add(set);
    }

    public static void registerItemResistance(Item item, DamageResistanceStats stats) {
        ITEM_STATS.put(item, stats);
    }

    public static void registerEntityResistance(Class<? extends Entity> entityClass, DamageResistanceStats stats) {
        ENTITY_STATS.put(entityClass, stats);
    }

    public static String getDamageCategory(DamageSource source) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return CATEGORY_EXPLOSION;
        if (source.is(DamageTypeTags.IS_FIRE)) return CATEGORY_FIRE;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return CATEGORY_PHYSICAL;
        if (source.is(DamageTypeTags.IS_FALL)) return CATEGORY_PHYSICAL;

        String msgId = source.getMsgId();

        if (msgId.equals("laser") || source.is(ModDamageSource.LASER)) return CATEGORY_LASER;
        if (msgId.equals("microwave") || source.is(ModDamageSource.MICROWAVE)) return CATEGORY_MICROWAVE;
        if (msgId.equals("subatomic") || source.is(ModDamageSource.SUBATOMIC)) return CATEGORY_SUBATOMIC;
        if (msgId.equals("electricity") || source.is(ModDamageSource.ELECTRICITY)) return CATEGORY_ELECTRIC;
        if (msgId.equals("radiation") || source.is(ModDamageSource.RADIATION)) return CATEGORY_RADIATION;
        if (msgId.equals("acid") || source.is(ModDamageSource.ACID)) return CATEGORY_ACID;

        if (source.is(DamageTypeTags.IS_LIGHTNING) ||
                source.is(DamageTypeTags.WITCH_RESISTANT_TO) ||
                msgId.contains("magic")) {
            return CATEGORY_ENERGY;
        }

        return CATEGORY_PHYSICAL;
    }

    public static float[] getDTDR(LivingEntity entity, DamageSource source, float amount) {
        float totalDT = 0;
        float totalDR = 0;

        ArmorSet wornSet = getWornArmorSet(entity);
        if (wornSet != null) {
            DamageResistanceStats setStats = SET_STATS.get(wornSet);
            if (setStats != null) {
                Resistance res = setStats.getResistance(source);
                if (res != null) {
                    totalDT += res.threshold;
                    totalDR += res.reduction;
                }
            }
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;

            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                DamageResistanceStats itemStats = ITEM_STATS.get(stack.getItem());
                if (itemStats != null) {
                    Resistance res = itemStats.getResistance(source);
                    if (res != null) {
                        totalDT += res.threshold;
                        totalDR += res.reduction;
                    }
                }
            }
        }

        DamageResistanceStats entityStats = ENTITY_STATS.get(entity.getClass());
        if (entityStats != null) {
            Resistance res = entityStats.getResistance(source);
            if (res != null) {
                totalDT += res.threshold;
                totalDR += res.reduction;
            }
        }

        return new float[]{totalDT, totalDR};
    }

    public static float calculateDamage(LivingEntity entity, DamageSource source, float amount) {
        if (shouldIgnoreProtection(source)) {
            return amount;
        }

        float originalDamage = amount;

        float[] dtDr = getDTDR(entity, source, amount);
        float dt = dtDr[0];
        float dr = dtDr[1];

        dt = Math.max(0F, dt - currentPierceDT);

        if (dt >= amount) {
            applyArmorDamage(entity, source, originalDamage, 0);
            return 0F;
        }

        amount -= dt;

        float pierceFactor = 1F - currentPierceDR;
        pierceFactor = Math.max(0F, Math.min(pierceFactor, 2F));
        dr *= pierceFactor;

        dr = Math.min(dr, 0.95F);
        amount *= (1F - dr);

        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            int armorValue = entity.getArmorValue();
            if (armorValue > 0) {
                float armorProtection = Math.min(armorValue * 0.04f, 0.8f);
                amount *= (1 - armorProtection);
            }
        }

        applyArmorDamage(entity, source, originalDamage, amount);

        return Math.max(amount, 0.001F);
    }

    private static boolean shouldIgnoreProtection(DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_ARMOR) ||
                source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ||
                source.is(DamageTypeTags.BYPASSES_EFFECTS) ||
                source.is(DamageTypeTags.BYPASSES_RESISTANCE) ||
                source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS) ||
                (source.getEntity() instanceof Player player && player.isCreative()) ||
                source.getMsgId().equals("out_of_world");
    }

    private static ArmorSet getWornArmorSet(LivingEntity entity) {
        Item helmet = entity.getItemBySlot(EquipmentSlot.HEAD).getItem();
        Item chestplate = entity.getItemBySlot(EquipmentSlot.CHEST).getItem();
        Item leggings = entity.getItemBySlot(EquipmentSlot.LEGS).getItem();
        Item boots = entity.getItemBySlot(EquipmentSlot.FEET).getItem();

        for (Item item : Arrays.asList(helmet, chestplate, leggings, boots)) {
            if (item != null) {
                List<ArmorSet> possibleSets = ITEM_TO_SETS.get(item);
                if (possibleSets != null) {
                    for (ArmorSet set : possibleSets) {
                        if (matchesSet(set, helmet, chestplate, leggings, boots)) {
                            return set;
                        }
                    }
                }
            }
        }

        return null;
    }

    private static boolean matchesSet(ArmorSet set, Item helmet, Item chestplate, Item leggings, Item boots) {
        return (set.helmet == null || set.helmet == helmet) &&
                (set.chestplate == null || set.chestplate == chestplate) &&
                (set.leggings == null || set.leggings == leggings) &&
                (set.boots == null || set.boots == boots);
    }

    private static void applyArmorDamage(LivingEntity entity, DamageSource source,
                                         float originalAmount, float finalAmount) {

        float damageBlocked = originalAmount - finalAmount;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;

            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem) {

                int damageMultiplier = getDamageMultiplierForSource(source);

                int baseDamage = (int) Math.max(1, originalAmount / 4.0f);
                int durabilityDamage = baseDamage * damageMultiplier;
                if (durabilityDamage > 0) {
                    stack.hurtAndBreak(durabilityDamage, entity,
                            e -> e.broadcastBreakEvent(slot));
                }
            }
        }
    }

    private static int getDamageMultiplierForSource(DamageSource source) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            return 2;
        }
        if (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_FREEZING)) {
            return 1;
        }
        if (source.is(DamageTypeTags.IS_FALL)) {
            return 1;
        }
        return 1;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        float original = event.getAmount();
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        setupPierceFromSource(source);

        float afterDTDR = calculateDamage(entity, source, original);

        event.setAmount(afterDTDR);
        resetPierce();
    }

    public static void setupPierce(float pierceDT, float pierceDR) {
        currentPierceDT = pierceDT;
        currentPierceDR = pierceDR;
    }

    public static void resetPierce() {
        currentPierceDT = 0;
        currentPierceDR = 0;
    }

    private static void setupPierceFromSource(DamageSource source) {
    }

    public static String getMaterialDisplayName(ItemStack stack) {
        if (stack.isEmpty()) return "";

        Item item = stack.getItem();
        List<ArmorSet> sets = ITEM_TO_SETS.get(item);

        if (sets != null && !sets.isEmpty()) {
            ArmorSet set = sets.get(0);
            DamageResistanceStats stats = SET_STATS.get(set);

            if (stats != null && stats.hasResistance()) {
                String itemName = item.getDescription().getString().toLowerCase();

                if (itemName.contains("сплав")) return "сплавного";
                if (itemName.contains("alloy")) return "сплавного";
                if (itemName.contains("титан")) return "титанового";
                if (itemName.contains("titanium")) return "титанового";
                if (itemName.contains("сталь")) return "стального";
                if (itemName.contains("steel")) return "стального";
                if (itemName.contains("шраббидий")) return "шраббидиевого";
                if (itemName.contains("schrabidium")) return "шраббидиевого";
                if (itemName.contains("cmb")) return "CMB";
                if (itemName.contains("rpa")) return "красной силовой";
                if (itemName.contains("red power")) return "красной силовой";
            }
        }

        return "";
    }

    public static void addInfo(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty()) return;

        if (ITEM_STATS.containsKey(stack.getItem())) {
            DamageResistanceStats stats = ITEM_STATS.get(stack.getItem());

            List<Component> toAdd = new ArrayList<>();

            for (Map.Entry<String, Resistance> entry : stats.categoryResistances.entrySet()) {
                String categoryKey = "damage.category." + entry.getKey();
                toAdd.add(Component.translatable(categoryKey + ".format",
                        String.format("%.1f", entry.getValue().threshold),
                        String.format("%d", (int)(entry.getValue().reduction * 100))));
            }
            for (Map.Entry<String, Resistance> entry : stats.exactResistances.entrySet()) {
                String displayName = getDamageTypeDisplayName(entry.getKey());
                toAdd.add(Component.literal(ChatFormatting.YELLOW.toString())
                        .append(displayName)
                        .append(ChatFormatting.RESET.toString())
                        .append(String.format(": %.1f DT ✓ %d%% DR", entry.getValue().threshold, (int)(entry.getValue().reduction * 100))));
            }
            if (stats.otherResistance != null) {
                toAdd.add(Component.literal(ChatFormatting.YELLOW.toString())
                        .append(Component.translatable("damage.other"))
                        .append(ChatFormatting.RESET.toString())
                        .append(String.format(": %.1f DT ✓ %d%% DR", stats.otherResistance.threshold, (int)(stats.otherResistance.reduction * 100))));
            }

            if (!toAdd.isEmpty()) {
                tooltip.add(Component.translatable("damage.item").withStyle(ChatFormatting.DARK_PURPLE));
                tooltip.addAll(toAdd);
            }
        }
    }

    public static void addSetInfo(ItemStack stack, List<Component> tooltip) {
        if (stack == null || stack.isEmpty()) return;

        if (ITEM_TO_SETS.containsKey(stack.getItem())) {
            List<ArmorSet> sets = ITEM_TO_SETS.get(stack.getItem());

            for (ArmorSet set : sets) {
                DamageResistanceStats stats = SET_STATS.get(set);
                if (stats == null) continue;

                List<Component> toAdd = new ArrayList<>();

                for (Map.Entry<String, Resistance> entry : stats.categoryResistances.entrySet()) {
                    String categoryKey = "damage.category." + entry.getKey();
                    toAdd.add(Component.translatable(categoryKey + ".format",
                            String.format("%.1f", entry.getValue().threshold),
                            String.format("%d", (int)(entry.getValue().reduction * 100))));
                }
                for (Map.Entry<String, Resistance> entry : stats.exactResistances.entrySet()) {
                    String displayName = getDamageTypeDisplayName(entry.getKey());
                    toAdd.add(Component.literal(ChatFormatting.YELLOW.toString())
                            .append(displayName)
                            .append(ChatFormatting.RESET.toString())
                            .append(String.format(": %.1f DT ✓ %d%% DR", entry.getValue().threshold, (int)(entry.getValue().reduction * 100))));
                }
                if (stats.otherResistance != null) {
                    toAdd.add(Component.literal(ChatFormatting.YELLOW.toString())
                            .append(Component.translatable("damage.other"))
                            .append(ChatFormatting.RESET.toString())
                            .append(String.format(": %.1f DT ✓ %d%% DR", stats.otherResistance.threshold, (int)(stats.otherResistance.reduction * 100))));
                }

                if (!toAdd.isEmpty()) {
                    tooltip.addAll(toAdd);
                }

                break;
            }
        }

    }

    private static String getDamageTypeDisplayName(String damageKey) {
        switch (damageKey) {
            case "fall": return Component.translatable("damage.exact.fall").getString();
            case "inFire": return Component.translatable("damage.exact.fire").getString();
            case "onFire": return Component.translatable("damage.exact.fire").getString();
            case "lava": return Component.translatable("damage.exact.lava").getString();
            case "drowning": return Component.translatable("damage.exact.drowning").getString();
            case "laser": return Component.translatable("damage.exact.laser").getString();
            case "microwave": return Component.translatable("damage.exact.microwave").getString();
            case "electricity": return Component.translatable("damage.exact.electric").getString();
            case "radiation": return Component.translatable("damage.exact.radiation").getString();
            case "acid": return Component.translatable("damage.exact.acid").getString();
            default: return damageKey;
        }
    }
}