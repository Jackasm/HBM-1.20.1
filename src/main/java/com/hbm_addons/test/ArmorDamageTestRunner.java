package com.hbm_addons.test;

import com.hbm.items.ModArmorItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Стенд брони: сколько урона реально доходит до здоровья через слои защиты.
 *
 * <p>Карточка #93: DT/DR и ванильные броневые очки. В 1.20.1 {@code LivingHurtEvent}
 * срабатывает до того, как движок применит броневые очки, поэтому снижение может
 * примениться дважды.</p>
 *
 * <p>Игрока без соединения на стенде нет, поэтому удары принимает зомби: броневые
 * очки и их снижение считаются в {@code LivingEntity#getArmorValue()} и
 * {@code CombatRules.getDamageAfterAbsorb} одинаково для всех живых существ.
 * Урон наносится источником {@code generic} — это PHYS-категория без DT/DR,
 * значит у железа и титана остаются только броневые очки, а у шраббидия — порог DT.</p>
 *
 * <p>Комплект надевается на одном тике, а меряется через два: атрибут {@code armor}
 * пересчитывается из надетых предметов в {@code LivingEntity.tick}, а не в
 * {@code setItemSlot}. Ночь выставляется, чтобы зомби не горел на солнце и не
 * подмешивал огненный урон в замер.</p>
 */
public class ArmorDamageTestRunner extends TestRunner {

    /** Урон одного удара: проходит сквозь очки, но не убивает зомби (20 HP). */
    private static final float HIT = 10.0F;
    private static final int HITS = 3;
    private static final float TOLERANCE = 0.05F;
    private static final BlockPos SPAWN = new BlockPos(320, 120, 324);

    private static final Item[] IRON_SET = {
            Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS
    };
    private static final Item[] TITANIUM_SET = {
            ModArmorItems.TITANIUM_HELMET.get(), ModArmorItems.TITANIUM_CHESTPLATE.get(),
            ModArmorItems.TITANIUM_LEGGINGS.get(), ModArmorItems.TITANIUM_BOOTS.get()
    };
    /** Набор с DT 10 / DR 0.65: удар в 10 единиц должен упереться в порог. */
    private static final Item[] SCHRABIDIUM_SET = {
            ModArmorItems.SCHRABIDIUM_HELMET.get(), ModArmorItems.SCHRABIDIUM_CHESTPLATE.get(),
            ModArmorItems.SCHRABIDIUM_LEGGINGS.get(), ModArmorItems.SCHRABIDIUM_BOOTS.get()
    };

    private record Trial(float damage, int armorPoints) {
    }

    private Zombie subject;
    private int stage = 0;
    private int waitTicks = 0;
    private long previousDayTime;
    private boolean ok = true;

    private Trial bare;
    private Trial iron;
    private Trial titanium;
    private Trial schrabidium;

    public ArmorDamageTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            TestChunks.force(level, 20, 20);
            previousDayTime = level.getDayTime();
            level.setDayTime(18000L);

            subject = EntityType.ZOMBIE.create(level);
            if (subject == null) {
                check("zombie_created", false);
                finish(false);
                return true;
            }
            subject.moveTo(SPAWN.getX() + 0.5D, SPAWN.getY(), SPAWN.getZ() + 0.5D, 0F, 0F);
            subject.setNoAi(true);
            subject.setNoGravity(true);
            subject.setPersistenceRequired();
            // У зомби есть природные 2 очка брони; для замера слоёв они лишние,
            // у игрока их нет.
            var naturalArmor = subject.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR);
            if (naturalArmor != null) naturalArmor.setBaseValue(0.0D);
            subject.setHealth(subject.getMaxHealth());
            level.addFreshEntity(subject);

            equip(null);
            waitTicks = 20;
            stage = 1;
            return false;
        }

        if (--waitTicks > 0) return false;

        try {
            switch (stage) {
                case 1 -> {
                    bare = measure("bare");
                    equip(IRON_SET);
                    waitTicks = 5;
                    stage = 2;
                }
                case 2 -> {
                    iron = measure("iron");
                    equip(TITANIUM_SET);
                    waitTicks = 5;
                    stage = 3;
                }
                case 3 -> {
                    titanium = measure("titanium");
                    equip(SCHRABIDIUM_SET);
                    waitTicks = 5;
                    stage = 4;
                }
                case 4 -> {
                    schrabidium = measure("schrabidium");
                    stage = 5;
                    report();
                    finish(ok);
                    return true;
                }
                default -> {
                    return true;
                }
            }
        } finally {
            if (stage == 5) {
                level.setDayTime(previousDayTime);
                subject.discard();
            }
        }

        return false;
    }

    private void report() {
        progress("reporting");

        float ironSingle = vanillaArmor(HIT, iron.armorPoints());
        float ironDouble = vanillaArmor(HIT * portArmorFactor(iron.armorPoints()), iron.armorPoints());
        float titaniumSingle = vanillaArmor(HIT, titanium.armorPoints());
        float titaniumDouble = vanillaArmor(HIT * portArmorFactor(titanium.armorPoints()), titanium.armorPoints());

        String layer = layerOf(iron.damage(), ironSingle, ironDouble);

        info("hit", HIT);
        info("no_armor_damage", bare.damage());
        info("iron_damage", iron.damage());
        info("iron_armor_points", iron.armorPoints());
        info("iron_expected_single", ironSingle);
        info("iron_expected_double", ironDouble);
        info("titanium_damage", titanium.damage());
        info("titanium_armor_points", titanium.armorPoints());
        info("titanium_expected_single", titaniumSingle);
        info("titanium_expected_double", titaniumDouble);
        info("schrabidium_damage", schrabidium.damage());
        info("armor_layer", layer);

        ok &= expect("baseline_matches_hit", Math.abs(bare.damage() - HIT) < 0.01F);
        ok &= expect("iron_mitigates", iron.damage() < bare.damage() - TOLERANCE);
        ok &= expect("titanium_mitigates_more", titanium.damage() < iron.damage() - TOLERANCE);
        ok &= expect("iron_armor_applied_once", matches(iron.damage(), ironSingle));
        ok &= expect("titanium_armor_applied_once", matches(titanium.damage(), titaniumSingle));
        ok &= expect("dt_threshold_blocks", schrabidium.damage() < TOLERANCE);

        metric("no_armor", bare.damage());
        metric("iron", iron.damage());
        metric("titanium", titanium.damage());
        metric("schrabidium", schrabidium.damage());
        metric("layer", layer);
    }

    private Trial measure(String name) {
        int armorPoints = subject.getArmorValue();
        float total = 0F;

        for (int hit = 0; hit < HITS; hit++) {
            subject.setHealth(subject.getMaxHealth());
            subject.invulnerableTime = 0;
            subject.hurtTime = 0;

            float before = subject.getHealth();
            subject.hurt(level.damageSources().cactus(), HIT);
            float damage = before - subject.getHealth();
            total += damage;
            info("damage_" + name + "_" + (hit + 1), damage);
        }

        info("armor_points_" + name, armorPoints);
        return new Trial(total / HITS, armorPoints);
    }

    private void equip(Item[] set) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
            subject.setItemSlot(slot, ItemStack.EMPTY);
        }
        if (set == null) return;

        subject.setItemSlot(EquipmentSlot.HEAD, new ItemStack(set[0]));
        subject.setItemSlot(EquipmentSlot.CHEST, new ItemStack(set[1]));
        subject.setItemSlot(EquipmentSlot.LEGS, new ItemStack(set[2]));
        subject.setItemSlot(EquipmentSlot.FEET, new ItemStack(set[3]));
    }

    /** Ванильная формула снижения очками: CombatRules.getDamageAfterAbsorb. */
    private static float vanillaArmor(float damage, int armorPoints) {
        float toughness = 0F;
        float f = 2.0F + toughness / 4.0F;
        float reduced = Mth.clamp(armorPoints - damage / f, armorPoints * 0.2F, 20.0F);
        return damage * (1.0F - reduced / 25.0F);
    }

    /** Множитель порта: 1 − min(очки × 0.04, 0.8). */
    private static float portArmorFactor(int armorPoints) {
        return 1.0F - Math.min(armorPoints * 0.04F, 0.8F);
    }

    private static boolean matches(float observed, float expected) {
        return Math.abs(observed - expected) < TOLERANCE;
    }

    private static String layerOf(float observed, float single, float dual) {
        if (matches(observed, dual)) return "double";
        if (matches(observed, single)) return "single";
        return "unknown";
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}

