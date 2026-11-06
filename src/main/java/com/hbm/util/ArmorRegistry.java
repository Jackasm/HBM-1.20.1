package com.hbm.util;

import com.hbm.api.item.IGasMask;
import com.hbm.handler.ArmorModHandler;

import com.hbm.items.ModArmorItems;
import com.hbm.items.armor.MaskItem;
import com.hbm.items.armor.RespiratorItem;
import com.hbm.items.ModItems;
import com.hbm.handler.HazmatRegistry;
import com.hbm.potion.HbmPotion;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ArmorRegistry {

    public enum HazardClass {
        PARTICLE_COARSE,    // Крупная пыль (уголь)
        PARTICLE_FINE,      // Мелкая пыль
        GAS_LUNG,           // Газы, поражающие легкие
        GAS_BLISTERING,     // Кожно-нарывные газы
        GAS_MONOXIDE,       // Угарный газ
        GAS,                // Общий класс газов (устаревший, но для совместимости)
        BACTERIA,           // Бактериологические угрозы
        SAND,               // Песок, пыльные бури
        LIGHT,              // Яркий свет, вспышки
        RADIATION,          // Радиация
        HEAT,               // Высокая температура
        COLD,                // Низкая температура
        CHEMICAL
    }

    /**
     * Комплекты, которые игра проверяет целиком: старые {@code ArmorUtil.checkFor*} (карточка #130).
     *
     * <p>Классы {@link HazardClass} отвечают на вопрос «от чего защищает эта деталь», а комплект —
     * «надет ли на существо полный набор». Это разные вопросы, и свести их в один мешок нельзя:
     * один и тот же класс (например {@code SAND}) висит на десятке разных предметов.
     * Поэтому в реестре два раздела: классы по предметам и комплекты по слотам.</p>
     */
    public enum SuitClass {
        HAZMAT,      // жёлтый, красный, серый, шраббидий + всё из HAZMAT2, плюс эффект мутации
        HAZMAT2,     // PAA, ликвидатор, эвфемий, RPA, FAU, DNT
        ASBESTOS,    // только асбестовый комплект
        DIGAMMA,     // FAU или DNT, плюс эффект стабильности
        DIGAMMA2,    // робы + стабильность + железный кладинг + здоровье меньше трёх
        FARADAY      // не набор, а металл в каждом из четырёх слотов
    }

    /** Полный комплект: по предмету на каждый слот брони. */
    private record SuitSet(Item helmet, Item chest, Item legs, Item boots) {
    }

    private static final Map<SuitClass, List<SuitSet>> SUIT_REGISTRY = new EnumMap<>(SuitClass.class);

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /**
     * Металлы для фарадеевой защиты: проверка по подстроке в имени предмета, как в эталоне.
     * Так было в 1.7.10, и менять это поведение — отдельная задача, не #130.
     */
    private static final String[] FARADAY_METALS = new String[]{
            "chainmail", "iron", "silver", "gold", "platinum", "tin", "lead",
            "liquidator", "schrabidium", "euphemium", "steel", "cmb", "titanium",
            "alloy", "copper", "bronze", "electrum", "t45", "t51", "bj",
            "starmetal", "hazmat", "rubber", "hev", "ajr", "rpa", "spacesuit"
    };

    // Регистр защиты предметов
    public static final Map<Item, Set<HazardClass>> PROTECTION_REGISTRY = new HashMap<>();

    // Регистр защиты по модификаторам
    private static final Map<Item, Map<HazardClass, Float>> MODIFIER_REGISTRY = new HashMap<>();

    // Инициализация регистра
    public static void registerHazard(Item item, HazardClass... classes) {
        Set<HazardClass> set = PROTECTION_REGISTRY.getOrDefault(item, new HashSet<>());
        Collections.addAll(set, classes);
        PROTECTION_REGISTRY.put(item, set);
    }

    public static void registerHazardWithModifier(Item item, HazardClass hazard, float modifier) {
        Map<HazardClass, Float> modifiers = MODIFIER_REGISTRY.getOrDefault(item, new HashMap<>());
        modifiers.put(hazard, modifier);
        MODIFIER_REGISTRY.put(item, modifiers);

        // Также добавляем в основной регистр
        registerHazard(item, hazard);
    }

    // Проверка защиты
    public static boolean hasProtection(LivingEntity entity, int slot, HazardClass hazard) {
        if (slot == 3) { // Слот головы
            ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
            return hasProtection(helmet, hazard);
        } else if (slot == 2) { // Слот груди
            ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
            return hasProtection(chestplate, hazard);
        } else if (slot == 1) { // Слот ног
            ItemStack leggings = entity.getItemBySlot(EquipmentSlot.LEGS);
            return hasProtection(leggings, hazard);
        } else if (slot == 0) { // Слот ног
            ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
            return hasProtection(boots, hazard);
        }
        return false;
    }
    public static boolean hasAnyProtection(LivingEntity entity, int slot, HazardClass... clazz) {
        // Получаем предмет в указанном слоте брони
        ItemStack stack = getArmorStack(entity, slot);

        if (stack.isEmpty()) return false;

        List<HazardClass> list = getProtectionFromItem(stack, entity);

        if (list == null) return false;

        for (HazardClass haz : clazz) {
            if (list.contains(haz)) return true;
        }

        return false;
    }

    private static ItemStack getArmorStack(LivingEntity entity, int slot) {
        return switch (slot) {
            case 0 -> entity.getItemBySlot(EquipmentSlot.FEET);
            case 1 -> entity.getItemBySlot(EquipmentSlot.LEGS);
            case 2 -> entity.getItemBySlot(EquipmentSlot.CHEST);
            case 3 -> entity.getItemBySlot(EquipmentSlot.HEAD);
            default -> ItemStack.EMPTY;
        };
    }

    public static List<HazardClass> getProtectionFromItem(ItemStack stack, LivingEntity entity) {
        List<HazardClass> prot = new ArrayList<>();

        Item item = stack.getItem();

        // if the item has HazardClasses assigned to it, add those
        if (PROTECTION_REGISTRY.containsKey(item)) {
            prot.addAll(PROTECTION_REGISTRY.get(item));
        }

        if (item instanceof IGasMask mask) {
            ItemStack filter = mask.getFilter(stack, entity);

            if (!filter.isEmpty()) {
                // add the HazardClasses from the filter, then remove the ones blacklisted by the mask
                List<HazardClass> filProt = new ArrayList<>(PROTECTION_REGISTRY.get(filter.getItem()));

                for (HazardClass c : mask.getBlacklist(stack, entity)) {
                    filProt.remove(c);
                }

                prot.addAll(filProt);
            }
        }

        if (ArmorModHandler.hasMods(stack)) {
            ItemStack[] mods = ArmorModHandler.pryMods(stack);

            for (ItemStack mod : mods) {
                // recursion! run the exact same procedure on every mod, in case future mods will have filter support
                if (!mod.isEmpty()) {
                    prot.addAll(getProtectionFromItem(mod, entity));
                }
            }
        }

        return prot;
    }

    public static boolean hasProtection(ItemStack stack, HazardClass hazard) {
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();

        // Проверка в регистре
        if (PROTECTION_REGISTRY.containsKey(item)) {
            return PROTECTION_REGISTRY.get(item).contains(hazard);
        }

        // Проверка специальных предметов
        if (item instanceof MaskItem mask) {
            return mask.protectsFrom(hazard);
        }

        if (item instanceof RespiratorItem respirator) {
            return respirator.protectsFrom(hazard);
        }

        return false;
    }

    // Получение модификатора защиты
    public static float getProtectionModifier(ItemStack stack, HazardClass hazard) {
        if (stack.isEmpty()) return 0.0f;

        Item item = stack.getItem();

        if (MODIFIER_REGISTRY.containsKey(item)) {
            Map<HazardClass, Float> modifiers = MODIFIER_REGISTRY.get(item);
            return modifiers.getOrDefault(hazard, 1.0f);
        }

        // Значения по умолчанию
        return hasProtection(stack, hazard) ? 1.0f : 0.0f;
    }

    // Полная защита от всех угроз в слоте
    public static boolean hasFullBodyProtection(LivingEntity entity, HazardClass hazard) {
        for (int i = 0; i < 4; i++) {
            if (!hasProtection(entity, i, hazard)) {
                return false;
            }
        }
        return true;
    }

    // Получение эффективности защиты (сумма модификаторов)
    public static float getTotalProtection(LivingEntity entity, HazardClass hazard) {
        float total = 0.0f;
        for (int i = 0; i < 4; i++) {
            ItemStack stack = getArmorInSlot(entity, i);
            total += getProtectionModifier(stack, hazard);
        }
        return Math.min(total, 1.0f); // Максимум 100% защиты
    }

    private static ItemStack getArmorInSlot(LivingEntity entity, int slot) {
        return switch (slot) {
            case 0 -> entity.getItemBySlot(EquipmentSlot.FEET);
            case 1 -> entity.getItemBySlot(EquipmentSlot.LEGS);
            case 2 -> entity.getItemBySlot(EquipmentSlot.CHEST);
            case 3 -> entity.getItemBySlot(EquipmentSlot.HEAD);
            default -> ItemStack.EMPTY;
        };
    }

    // Получение всех классов опасности, от которых защищает предмет
    public static Set<HazardClass> getProtectedHazards(ItemStack stack) {
        if (stack.isEmpty()) return Collections.emptySet();

        Item item = stack.getItem();
        if (PROTECTION_REGISTRY.containsKey(item)) {
            return new HashSet<>(PROTECTION_REGISTRY.get(item));
        }

        // Для специальных предметов
        Set<HazardClass> hazards = new HashSet<>();
        if (item instanceof MaskItem mask) {
            for (HazardClass hazard : HazardClass.values()) {
                if (mask.protectsFrom(hazard)) {
                    hazards.add(hazard);
                }
            }
        }

        if (item instanceof RespiratorItem respirator) {
            for (HazardClass hazard : HazardClass.values()) {
                if (respirator.protectsFrom(hazard)) {
                    hazards.add(hazard);
                }
            }
        }

        return hazards;
    }

    /**
     * Заполняет таблицу комплектов. Вызывается из {@link #registerAll()} — там, где предметы мода
     * уже зарегистрированы, как и регистрация классов по предметам.
     */
    public static void registerSuits() {
        SUIT_REGISTRY.clear();

        SUIT_REGISTRY.put(SuitClass.HAZMAT, List.of(
                suit(ModArmorItems.HAZMAT_HELMET.get(), ModArmorItems.HAZMAT_CHESTPLATE.get(),
                        ModArmorItems.HAZMAT_LEGGINGS.get(), ModArmorItems.HAZMAT_BOOTS.get()),
                suit(ModArmorItems.HAZMAT_HELMET_RED.get(), ModArmorItems.HAZMAT_CHESTPLATE_RED.get(),
                        ModArmorItems.HAZMAT_LEGGINGS_RED.get(), ModArmorItems.HAZMAT_BOOTS_RED.get()),
                suit(ModArmorItems.HAZMAT_HELMET_GREY.get(), ModArmorItems.HAZMAT_CHESTPLATE_GREY.get(),
                        ModArmorItems.HAZMAT_LEGGINGS_GREY.get(), ModArmorItems.HAZMAT_BOOTS_GREY.get()),
                suit(ModArmorItems.SCHRABIDIUM_HELMET.get(), ModArmorItems.SCHRABIDIUM_CHESTPLATE.get(),
                        ModArmorItems.SCHRABIDIUM_LEGGINGS.get(), ModArmorItems.SCHRABIDIUM_BOOTS.get())));

        SUIT_REGISTRY.put(SuitClass.HAZMAT2, List.of(
                suit(ModArmorItems.HAZMAT_PAA_HELMET.get(), ModArmorItems.HAZMAT_PAA_CHESTPLATE.get(),
                        ModArmorItems.HAZMAT_PAA_LEGGINGS.get(), ModArmorItems.HAZMAT_PAA_BOOTS.get()),
                suit(ModArmorItems.LIQUIDATOR_HELMET.get(), ModArmorItems.LIQUIDATOR_CHESTPLATE.get(),
                        ModArmorItems.LIQUIDATOR_LEGGINGS.get(), ModArmorItems.LIQUIDATOR_BOOTS.get()),
                suit(ModArmorItems.EUPHEMIUM_HELMET.get(), ModArmorItems.EUPHEMIUM_CHESTPLATE.get(),
                        ModArmorItems.EUPHEMIUM_LEGGINGS.get(), ModArmorItems.EUPHEMIUM_BOOTS.get()),
                suit(ModArmorItems.RPA_HELMET.get(), ModArmorItems.RPA_CHESTPLATE.get(),
                        ModArmorItems.RPA_LEGGINGS.get(), ModArmorItems.RPA_BOOTS.get()),
                suit(ModArmorItems.FAU_HELMET.get(), ModArmorItems.FAU_CHESTPLATE.get(),
                        ModArmorItems.FAU_LEGGINGS.get(), ModArmorItems.FAU_BOOTS.get()),
                suit(ModArmorItems.DNT_HELMET.get(), ModArmorItems.DNT_CHESTPLATE.get(),
                        ModArmorItems.DNT_LEGGINGS.get(), ModArmorItems.DNT_BOOTS.get())));

        SUIT_REGISTRY.put(SuitClass.ASBESTOS, List.of(
                suit(ModArmorItems.ASBESTOS_HELMET.get(), ModArmorItems.ASBESTOS_CHESTPLATE.get(),
                        ModArmorItems.ASBESTOS_LEGGINGS.get(), ModArmorItems.ASBESTOS_BOOTS.get())));

        SUIT_REGISTRY.put(SuitClass.DIGAMMA, List.of(
                suit(ModArmorItems.FAU_HELMET.get(), ModArmorItems.FAU_CHESTPLATE.get(),
                        ModArmorItems.FAU_LEGGINGS.get(), ModArmorItems.FAU_BOOTS.get()),
                suit(ModArmorItems.DNT_HELMET.get(), ModArmorItems.DNT_CHESTPLATE.get(),
                        ModArmorItems.DNT_LEGGINGS.get(), ModArmorItems.DNT_BOOTS.get())));

        SUIT_REGISTRY.put(SuitClass.DIGAMMA2, List.of(
                suit(ModArmorItems.ROBES_HELMET.get(), ModArmorItems.ROBES_CHESTPLATE.get(),
                        ModArmorItems.ROBES_LEGGINGS.get(), ModArmorItems.ROBES_BOOTS.get())));
    }

    private static SuitSet suit(Item helmet, Item chest, Item legs, Item boots) {
        return new SuitSet(helmet, chest, legs, boots);
    }

    /**
     * Единая точка старых проверок брони: надет ли комплект. Условия, которые в эталоне жили
     * рядом с наборами (зелья, кладинг, максимальное здоровье), переехали сюда же, чтобы таблица
     * и её правила лежали в одном месте.
     */
    public static boolean isWearing(LivingEntity entity, SuitClass suit) {
        return switch (suit) {
            // checkForHazmat в эталоне включал в себя checkForHaz2 — поведение сохранено.
            case HAZMAT -> wears(entity, SuitClass.HAZMAT)
                    || wears(entity, SuitClass.HAZMAT2)
                    || entity.hasEffect(HbmPotion.MUTATION.get());
            case HAZMAT2 -> wears(entity, SuitClass.HAZMAT2);
            case ASBESTOS -> wears(entity, SuitClass.ASBESTOS);
            case DIGAMMA -> wears(entity, SuitClass.DIGAMMA)
                    || entity.hasEffect(HbmPotion.STABILITY.get());
            case DIGAMMA2 -> wearsRobes(entity);
            case FARADAY -> wearsFaraday(entity);
        };
    }

    private static boolean wears(LivingEntity entity, SuitClass suit) {
        List<SuitSet> sets = SUIT_REGISTRY.get(suit);
        if (sets == null) return false;

        for (SuitSet set : sets) {
            if (wearsExactSet(entity, set)) return true;
        }
        return false;
    }

    private static boolean wearsExactSet(LivingEntity entity, SuitSet set) {
        return wearsPiece(entity, EquipmentSlot.HEAD, set.helmet())
                && wearsPiece(entity, EquipmentSlot.CHEST, set.chest())
                && wearsPiece(entity, EquipmentSlot.LEGS, set.legs())
                && wearsPiece(entity, EquipmentSlot.FEET, set.boots());
    }

    private static boolean wearsPiece(LivingEntity entity, EquipmentSlot slot, Item item) {
        ItemStack stack = entity.getItemBySlot(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }

    /** Робы: полный набор, эффект стабильности, железный кладинг и здоровье меньше трёх. */
    private static boolean wearsRobes(LivingEntity entity) {
        List<SuitSet> sets = SUIT_REGISTRY.get(SuitClass.DIGAMMA2);
        if (sets == null || sets.isEmpty() || !wearsExactSet(entity, sets.get(0))) return false;

        if (!entity.hasEffect(HbmPotion.STABILITY.get())) return false;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armor = entity.getItemBySlot(slot);

            if (!armor.isEmpty() && ArmorModHandler.hasMods(armor)) {
                ItemStack[] mods = ArmorModHandler.pryMods(armor);
                ItemStack cladding = mods[ArmorModHandler.CLADDING];

                if (!(cladding != null && cladding.getItem() == ModItems.CLADDING_IRON.get())) return false;
            }
        }

        return entity.getMaxHealth() < 3.0F;
    }

    /** Фарадей: все четыре слота заняты «металлом», набор как таковой не нужен. */
    private static boolean wearsFaraday(LivingEntity entity) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (!isFaradayArmor(entity.getItemBySlot(slot))) return false;
        }
        return true;
    }

    public static boolean isFaradayArmor(ItemStack item) {
        if (item.isEmpty()) return false;

        String name = item.getDescriptionId().toLowerCase(Locale.US);

        for (String metal : FARADAY_METALS) {
            if (name.contains(metal)) return true;
        }

        return HazmatRegistry.getCladding(item) > 0;
    }

    public static void registerAll() {
        registerSuits();
        
        registerHazard(ModItems.GAS_MASK_FILTER.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.GAS_BLISTERING, HazardClass.BACTERIA);

        registerHazard(ModItems.GAS_MASK_FILTER_MONO.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.GAS_MONOXIDE);

        registerHazard(ModItems.GAS_MASK_FILTER_COMBO.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.GAS_BLISTERING,
                HazardClass.BACTERIA, HazardClass.GAS_MONOXIDE);

        registerHazard(ModItems.GAS_MASK_FILTER_RAG.get(),
                HazardClass.PARTICLE_COARSE);

        registerHazard(ModItems.GAS_MASK_FILTER_PISS.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.GAS_LUNG);

        // Маски и респираторы
        registerHazard(ModArmorItems.GAS_MASK.get(),
                HazardClass.SAND, HazardClass.LIGHT);

        registerHazard(ModArmorItems.GAS_MASK_M65.get(),
                HazardClass.SAND);
        
        registerHazard(ModArmorItems.MASK_WET.get(),
                HazardClass.PARTICLE_COARSE);

        registerHazard(ModArmorItems.MASK_PISS.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.GAS_LUNG);
        
        registerHazard(ModArmorItems.GAS_MASK.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.ASHGLASSES.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        // Аттачменты
        registerHazard(ModArmorItems.ATTACHMENT_MASK.get(),
                HazardClass.SAND);

        // Шлемы и броня
        registerHazard(ModArmorItems.ASBESTOS_HELMET.get(),
                HazardClass.SAND, HazardClass.LIGHT);

        registerHazard(ModArmorItems.HAZMAT_HELMET.get(),
                HazardClass.SAND);

        registerHazard(ModArmorItems.HAZMAT_HELMET_RED.get(),
                HazardClass.SAND);

        registerHazard(ModArmorItems.HAZMAT_HELMET_GREY.get(),
                HazardClass.SAND);

        registerHazard(ModArmorItems.HAZMAT_PAA_HELMET.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.LIQUIDATOR_HELMET.get(),
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.T45_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE, HazardClass.SAND);

        registerHazard(ModArmorItems.T51_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE, HazardClass.SAND);

        registerHazard(ModArmorItems.AJR_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.AJRO_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.STEAMSUIT_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.HEV_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.FAU_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.DNT_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.SCHRABIDIUM_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.EUPHEMIUM_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.RPA_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.ENVSUIT_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);

        registerHazard(ModArmorItems.TRENCHMASTER_HELMET.get(),
                HazardClass.PARTICLE_COARSE, HazardClass.PARTICLE_FINE,
                HazardClass.GAS_LUNG, HazardClass.BACTERIA,
                HazardClass.GAS_BLISTERING, HazardClass.GAS_MONOXIDE,
                HazardClass.LIGHT, HazardClass.SAND);


    }

    // Регистрация предметов из других модов
    public static void registerIfExists(String domain, String name, HazardClass... classes) {
        ResourceLocation key = ResLocation.ResLocation(domain, name);
        Item item = ForgeRegistries.ITEMS.getValue(key);
        if (item != null) {
            registerHazard(item, classes);
        }
    }

    // Утилиты для совместимости
    public static boolean checkForFullHazmat(LivingEntity entity) {
        return hasFullBodyProtection(entity, HazardClass.RADIATION) &&
                hasFullBodyProtection(entity, HazardClass.GAS) &&
                hasFullBodyProtection(entity, HazardClass.PARTICLE_FINE);
    }

    public static boolean checkForFullAsbestos(LivingEntity entity) {
        return hasFullBodyProtection(entity, HazardClass.HEAT) &&
                hasFullBodyProtection(entity, HazardClass.PARTICLE_COARSE);
    }

    public static boolean hasAllProtection(LivingEntity entity, int slotMask, HazardClass hazard) {
        // Проверяем каждый слот в зависимости от маски
        if ((slotMask & 1) != 0 && !hasProtection(entity, 3, hazard)) { // Голова (3)
            return false;
        }
        if ((slotMask & 2) != 0 && !hasProtection(entity, 2, hazard)) { // Грудь (2)
            return false;
        }
        if ((slotMask & 4) != 0 && !hasProtection(entity, 1, hazard)) { // Ноги (1)
            return false;
        }
        if ((slotMask & 8) != 0 && !hasProtection(entity, 0, hazard)) { // Ботинки (0)
            return false;
        }
        return true;
    }
}