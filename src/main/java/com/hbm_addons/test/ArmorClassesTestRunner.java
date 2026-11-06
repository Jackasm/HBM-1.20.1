package com.hbm_addons.test;

import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.ArmorUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.FakePlayer;

import java.util.*;

/**
 * Стенд классов защиты брони: снимок поведения старых проверок {@code ArmorUtil.checkFor*}
 * и реестра {@link ArmorRegistry} до сведения двух систем в одну (карточка #130).
 *
 * <p>Матрица «набор × проверка» снимается на нетронутом коде, ожидаемые значения вписаны
 * в файл по факту этого прогона. После рефактора стенд обязан дать ту же матрицу: это и есть
 * критерий приёмки «поведение совпадает с текущим».</p>
 *
 * <p>Наборы заданы явно, а не через {@code ArmorFSB}: проверяется публичный API, поэтому
 * рефактор внутренностей реестра стенд не ломает. Игрока на сервере без соединения нет,
 * поэтому используется {@link FakePlayer}: эффекты, атрибуты и слоты брони у него настоящие.</p>
 *
 * <p>Проверки классов идут по шлему — так же, как в игре: {@code hasProtection(entity, 3, class)}.</p>
 */
public class ArmorClassesTestRunner extends TestRunner {

    /** Короткие коды классов — чтобы ожидания читались без простыней. */
    private static final Map<String, HazardClass> CODES = new LinkedHashMap<>();
    static {
        CODES.put("PC", HazardClass.PARTICLE_COARSE);
        CODES.put("PF", HazardClass.PARTICLE_FINE);
        CODES.put("GL", HazardClass.GAS_LUNG);
        CODES.put("GB", HazardClass.GAS_BLISTERING);
        CODES.put("GM", HazardClass.GAS_MONOXIDE);
        CODES.put("BA", HazardClass.BACTERIA);
        CODES.put("SA", HazardClass.SAND);
        CODES.put("LI", HazardClass.LIGHT);
        CODES.put("GA", HazardClass.GAS);
        CODES.put("RA", HazardClass.RADIATION);
        CODES.put("HE", HazardClass.HEAT);
        CODES.put("CO", HazardClass.COLD);
        CODES.put("CH", HazardClass.CHEMICAL);
    }

    /** Полные респираторные шлемы: восемь классов. */
    private static final String FULL_RESPIRATOR = "PC,PF,GL,GB,GM,BA,SA,LI";
    /** Шлемы T45/T51: те же восемь без защиты от света. */
    private static final String COMBAT_HELMET = "PC,PF,GL,GB,GM,BA,SA";

    private record Suit(String name, Item head, Item chest, Item legs, Item feet, String classes) {
    }

    private static Suit suit(String name, Item head, Item chest, Item legs, Item feet, String classes) {
        return new Suit(name, head, chest, legs, feet, classes);
    }

    /**
     * Снимок «до»: какие классы реестра даёт шлем набора.
     * Получено на коде до рефактора (#130), реестр {@code ArmorRegistry.registerAll()}.
     */
    private static List<Suit> suits() {
        List<Suit> list = new ArrayList<>();
        list.add(suit("hazmat_yellow",
                ModArmorItems.HAZMAT_HELMET.get(), ModArmorItems.HAZMAT_CHESTPLATE.get(),
                ModArmorItems.HAZMAT_LEGGINGS.get(), ModArmorItems.HAZMAT_BOOTS.get(), "SA"));
        list.add(suit("hazmat_red",
                ModArmorItems.HAZMAT_HELMET_RED.get(), ModArmorItems.HAZMAT_CHESTPLATE_RED.get(),
                ModArmorItems.HAZMAT_LEGGINGS_RED.get(), ModArmorItems.HAZMAT_BOOTS_RED.get(), "SA"));
        list.add(suit("hazmat_grey",
                ModArmorItems.HAZMAT_HELMET_GREY.get(), ModArmorItems.HAZMAT_CHESTPLATE_GREY.get(),
                ModArmorItems.HAZMAT_LEGGINGS_GREY.get(), ModArmorItems.HAZMAT_BOOTS_GREY.get(), "SA"));
        list.add(suit("hazmat_paa",
                ModArmorItems.HAZMAT_PAA_HELMET.get(), ModArmorItems.HAZMAT_PAA_CHESTPLATE.get(),
                ModArmorItems.HAZMAT_PAA_LEGGINGS.get(), ModArmorItems.HAZMAT_PAA_BOOTS.get(), "SA,LI"));
        list.add(suit("liquidator",
                ModArmorItems.LIQUIDATOR_HELMET.get(), ModArmorItems.LIQUIDATOR_CHESTPLATE.get(),
                ModArmorItems.LIQUIDATOR_LEGGINGS.get(), ModArmorItems.LIQUIDATOR_BOOTS.get(), "SA,LI"));
        list.add(suit("euphemium",
                ModArmorItems.EUPHEMIUM_HELMET.get(), ModArmorItems.EUPHEMIUM_CHESTPLATE.get(),
                ModArmorItems.EUPHEMIUM_LEGGINGS.get(), ModArmorItems.EUPHEMIUM_BOOTS.get(), FULL_RESPIRATOR));
        list.add(suit("rpa",
                ModArmorItems.RPA_HELMET.get(), ModArmorItems.RPA_CHESTPLATE.get(),
                ModArmorItems.RPA_LEGGINGS.get(), ModArmorItems.RPA_BOOTS.get(), FULL_RESPIRATOR));
        list.add(suit("fau",
                ModArmorItems.FAU_HELMET.get(), ModArmorItems.FAU_CHESTPLATE.get(),
                ModArmorItems.FAU_LEGGINGS.get(), ModArmorItems.FAU_BOOTS.get(), FULL_RESPIRATOR));
        list.add(suit("dnt",
                ModArmorItems.DNT_HELMET.get(), ModArmorItems.DNT_CHESTPLATE.get(),
                ModArmorItems.DNT_LEGGINGS.get(), ModArmorItems.DNT_BOOTS.get(), FULL_RESPIRATOR));
        list.add(suit("schrabidium",
                ModArmorItems.SCHRABIDIUM_HELMET.get(), ModArmorItems.SCHRABIDIUM_CHESTPLATE.get(),
                ModArmorItems.SCHRABIDIUM_LEGGINGS.get(), ModArmorItems.SCHRABIDIUM_BOOTS.get(), FULL_RESPIRATOR));
        list.add(suit("asbestos",
                ModArmorItems.ASBESTOS_HELMET.get(), ModArmorItems.ASBESTOS_CHESTPLATE.get(),
                ModArmorItems.ASBESTOS_LEGGINGS.get(), ModArmorItems.ASBESTOS_BOOTS.get(), "SA,LI"));
        list.add(suit("robes",
                ModArmorItems.ROBES_HELMET.get(), ModArmorItems.ROBES_CHESTPLATE.get(),
                ModArmorItems.ROBES_LEGGINGS.get(), ModArmorItems.ROBES_BOOTS.get(), ""));
        list.add(suit("t45",
                ModArmorItems.T45_HELMET.get(), ModArmorItems.T45_CHESTPLATE.get(),
                ModArmorItems.T45_LEGGINGS.get(), ModArmorItems.T45_BOOTS.get(), COMBAT_HELMET));
        list.add(suit("t51",
                ModArmorItems.T51_HELMET.get(), ModArmorItems.T51_CHESTPLATE.get(),
                ModArmorItems.T51_LEGGINGS.get(), ModArmorItems.T51_BOOTS.get(), COMBAT_HELMET));
        list.add(suit("iron",
                Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, ""));
        list.add(suit("steel",
                ModArmorItems.STEEL_HELMET.get(), ModArmorItems.STEEL_CHESTPLATE.get(),
                ModArmorItems.STEEL_LEGGINGS.get(), ModArmorItems.STEEL_BOOTS.get(), ""));
        list.add(suit("titanium",
                ModArmorItems.TITANIUM_HELMET.get(), ModArmorItems.TITANIUM_CHESTPLATE.get(),
                ModArmorItems.TITANIUM_LEGGINGS.get(), ModArmorItems.TITANIUM_BOOTS.get(), ""));
        list.add(suit("leather",
                Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, ""));
        return list;
    }

    /** Полный набор, который старый {@code checkForHazmat} признаёт хазматом. */
    private static final Set<String> EXPECTED_HAZMAT = Set.of(
            "hazmat_yellow", "hazmat_red", "hazmat_grey", "schrabidium",
            "hazmat_paa", "liquidator", "euphemium", "rpa", "fau", "dnt");
    /** Секция {@code checkForHaz2}: «продвинутые» костюмы. */
    private static final Set<String> EXPECTED_HAZ2 = Set.of(
            "hazmat_paa", "liquidator", "euphemium", "rpa", "fau", "dnt");
    private static final Set<String> EXPECTED_ASBESTOS = Set.of("asbestos");
    private static final Set<String> EXPECTED_DIGAMMA = Set.of("fau", "dnt");
    /** Без условий (эффект, кладинг, здоровье) второй дигамма-набор не срабатывает никогда. */
    private static final Set<String> EXPECTED_DIGAMMA2 = Set.of();
    /**
     * Фарадей — не набор, а совпадение имени предмета с металлом в каждом из четырёх слотов
     * ({@code ArmorUtil.isFaradayArmor}). Поэтому в списке только наборы с металлом в имени.
     */
    private static final Set<String> EXPECTED_FARADAY = Set.of(
            "hazmat_yellow", "hazmat_red", "hazmat_grey", "hazmat_paa", "liquidator",
            "euphemium", "rpa", "schrabidium", "t45", "t51", "iron", "steel", "titanium");

    private boolean done = false;

    public ArmorClassesTestRunner(net.minecraft.server.level.ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (done) return true;
        done = true;

        progress("scanning_classes");
        scanHazardClasses();

        progress("scanning_suits");
        scanSuits();

        progress("scanning_conditions");
        scanConditions();

        finish(checks.stream().allMatch(c -> c.pass));
        return true;
    }

    /** Что даёт шлем набора по реестру классов: снимок тринадцати классов. */
    private void scanHazardClasses() {
        boolean allMatch = true;
        StringBuilder mismatches = new StringBuilder();

        for (Suit s : suits()) {
            FakePlayer player = player();
            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(s.head));

            Set<HazardClass> observed = EnumSet.noneOf(HazardClass.class);
            for (HazardClass haz : HazardClass.values()) {
                if (ArmorRegistry.hasProtection(player, 3, haz)) observed.add(haz);
            }

            String observedCodes = codesOf(observed);
            metric("classes_" + s.name, observedCodes);
            info("classes_" + s.name, observedCodes);

            if (!observedCodes.equals(s.classes)) {
                allMatch = false;
                mismatches.append(s.name).append(": expected [").append(s.classes)
                        .append("] got [").append(observedCodes).append("] ");
            }
        }

        if (!allMatch) info("classes_mismatches", mismatches.toString().trim());
        check("hazard_classes_match_snapshot", allMatch);
    }

    /** Матрица старых проверок по всем наборам. */
    private void scanSuits() {
        boolean hazmatOk = true, haz2Ok = true, asbestosOk = true;
        boolean digammaOk = true, digamma2Ok = true, faradayOk = true;
        StringBuilder mismatches = new StringBuilder();

        for (Suit s : suits()) {
            FakePlayer player = player();
            equip(player, s);
            boolean[] observed = evaluate(player);

            String flags = flags(observed);
            metric("suite_" + s.name, flags);
            info("suite_" + s.name, flags);

            boolean[] expected = {
                    EXPECTED_HAZMAT.contains(s.name),
                    EXPECTED_HAZ2.contains(s.name),
                    EXPECTED_ASBESTOS.contains(s.name),
                    EXPECTED_DIGAMMA.contains(s.name),
                    EXPECTED_DIGAMMA2.contains(s.name),
                    EXPECTED_FARADAY.contains(s.name)
            };

            StringBuilder diff = new StringBuilder();
            for (int i = 0; i < expected.length; i++) {
                if (observed[i] != expected[i]) {
                    diff.append(NAMES[i]).append(" expected ").append(expected[i])
                            .append(" got ").append(observed[i]).append("; ");
                }
            }

            if (diff.length() > 0) {
                mismatches.append(s.name).append(": ").append(diff);
                hazmatOk &= observed[0] == expected[0];
                haz2Ok &= observed[1] == expected[1];
                asbestosOk &= observed[2] == expected[2];
                digammaOk &= observed[3] == expected[3];
                digamma2Ok &= observed[4] == expected[4];
                faradayOk &= observed[5] == expected[5];
            }
        }

        if (mismatches.length() > 0) info("suite_mismatches", mismatches.toString().trim());
        check("hazmat_match", hazmatOk);
        check("haz2_match", haz2Ok);
        check("asbestos_match", asbestosOk);
        check("digamma_match", digammaOk);
        check("digamma2_match", digamma2Ok);
        check("faraday_match", faradayOk);
    }

    private static final String[] NAMES = {"hazmat", "haz2", "asbestos", "digamma", "digamma2", "faraday"};

    /** Краевые случаи: неполный набор, эффекты, условия второго дигамма-набора. */
    private void scanConditions() {
        // Три предмета из четырёх: набор развалился, хазмата нет.
        FakePlayer partial = player();
        partial.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModArmorItems.HAZMAT_HELMET.get()));
        partial.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModArmorItems.HAZMAT_CHESTPLATE.get()));
        partial.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModArmorItems.HAZMAT_LEGGINGS.get()));
        partial.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
        boolean partialFlags[] = evaluate(partial);
        info("partial_set", flags(partialFlags));
        check("partial_set_no_hazmat", !partialFlags[0]);
        // Но фарадей по-прежнему даёт: он смотрит на каждый предмет отдельно.
        check("partial_set_faraday", partialFlags[5]);

        // Эффект мутации заменяет хазмат, эффект стабильности — дигамма-защиту.
        FakePlayer mutation = player();
        mutation.addEffect(new MobEffectInstance(HbmPotion.MUTATION.get(), 200));
        check("mutation_grants_hazmat", ArmorUtil.checkForHazmat(mutation));

        FakePlayer stability = player();
        stability.addEffect(new MobEffectInstance(HbmPotion.STABILITY.get(), 200));
        check("stability_grants_digamma", ArmorUtil.checkForDigamma(stability));

        // Второй дигамма-набор: робы + стабильность + железный кладинг на всех четырёх +
        // максимальное здоровье меньше трёх.
        check("digamma2_full_kit", evaluate(robesKit(true, null, 2.0F))[4]);
        check("digamma2_no_stability", !evaluate(robesKit(false, null, 2.0F))[4]);
        // Особенность эталона: цикл смотрит только на детали, у которых вообще есть моды.
        // Комплект без модов проходит проверку кладинга и даёт защиту — поведение сохранено.
        check("digamma2_no_mods_passes", evaluate(robesKit(true, null, 2.0F))[4]);
        // А вот чужой кладинг защиту отнимает.
        check("digamma2_foreign_cladding", !evaluate(robesKit(true, ModItems.CLADDING_LEAD.get(), 2.0F))[4]);
        check("digamma2_healthy", !evaluate(robesKit(true, null, 10.0F))[4]);
    }

    /** Игрок в робах с управляемыми условиями второго дигамма-набора. */
    private FakePlayer robesKit(boolean stability, Item cladding, float maxHealth) {
        FakePlayer player = player();
        if (stability) player.addEffect(new MobEffectInstance(HbmPotion.STABILITY.get(), 200));

        Item[] robes = {
                ModArmorItems.ROBES_HELMET.get(), ModArmorItems.ROBES_CHESTPLATE.get(),
                ModArmorItems.ROBES_LEGGINGS.get(), ModArmorItems.ROBES_BOOTS.get()
        };
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        for (int i = 0; i < robes.length; i++) {
            ItemStack piece = new ItemStack(robes[i]);
            if (cladding != null) ArmorModHandler.applyMod(piece, new ItemStack(cladding));
            player.setItemSlot(slots[i], piece);
        }

        var health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.setBaseValue(maxHealth);
        return player;
    }

    /**
     * Шесть старых проверок разом: хазмат, хазмат-2, асбест, дигамма, дигамма-2, фарадей.
     * Порядок флагов совпадает с {@link #NAMES}.
     */
    private static boolean[] evaluate(FakePlayer player) {
        return new boolean[]{
                ArmorUtil.checkForHazmat(player),
                ArmorUtil.checkForHaz2(player),
                ArmorUtil.checkForAsbestos(player),
                ArmorUtil.checkForDigamma(player),
                ArmorUtil.checkForDigamma2(player),
                ArmorUtil.checkForFaraday(player)
        };
    }

    private FakePlayer player() {
        return new FakePlayer(level, new GameProfile(UUID.randomUUID(), "armor_classes_test"));
    }

    private static void equip(FakePlayer player, Suit s) {
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(s.head));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(s.chest));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(s.legs));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(s.feet));
    }

    private static String codesOf(Set<HazardClass> set) {
        List<String> codes = new ArrayList<>();
        for (Map.Entry<String, HazardClass> e : CODES.entrySet()) {
            if (set.contains(e.getValue())) codes.add(e.getKey());
        }
        return String.join(",", codes);
    }

    private static String flags(boolean[] values) {
        StringBuilder sb = new StringBuilder();
        for (boolean v : values) sb.append(v ? '1' : '0');
        return sb.toString();
    }
}
