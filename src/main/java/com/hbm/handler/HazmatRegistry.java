package com.hbm.handler;

import com.google.gson.Gson;
import com.hbm.items.ModArmorItems;
import com.hbm.items.armor.ItemModCladding;
import com.hbm.potion.HbmPotion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public class HazmatRegistry {

    private static final HashMap<Item, Double> entries = new HashMap<>();
    private static final Gson gson = new Gson();

    static {
        // Инициализация по умолчанию
        initDefault();
    }

    public static void initDefault() {
        // Коэффициенты защиты (значения из оригинала)
        double helmet = 0.2D;
        double chest = 0.4D;
        double legs = 0.3D;
        double boots = 0.1D;

        double iron = 0.0225D;
        double gold = 0.0225D;
        double steel = 0.045D;
        double titanium = 0.045D;
        double alloy = 0.07D;
        double cobalt = 0.125D;

        double hazYellow = 0.6D;
        double hazRed = 1.0D;
        double hazGray = 2D;
        double paa = 1.7D;
        double liquidator = 2.4D;

        double t51 = 1D;
        double ajr = 1.3D;
        double bj = 1D;
        double env = 1.0D;
        double hev = 2.3D;
        double rpa = 2D;
        double trench = 1D;
        double fau = 4D;
        double dns = 5D;
        double security = 0.825D;
        double star = 1D;
        double cmb = 1.3D;
        double schrab = 3D;
        double euph = 10D;
        
        registerHazmat(ModArmorItems.HAZMAT_HELMET.get(), hazYellow * helmet);
        registerHazmat(ModArmorItems.HAZMAT_CHESTPLATE.get(), hazYellow * chest);
        registerHazmat(ModArmorItems.HAZMAT_LEGGINGS.get(), hazYellow * legs);
        registerHazmat(ModArmorItems.HAZMAT_BOOTS.get(), hazYellow * boots);

        registerHazmat(ModArmorItems.HAZMAT_HELMET_RED.get(), hazRed * helmet);
        registerHazmat(ModArmorItems.HAZMAT_CHESTPLATE_RED.get(), hazRed * chest);
        registerHazmat(ModArmorItems.HAZMAT_LEGGINGS_RED.get(), hazRed * legs);
        registerHazmat(ModArmorItems.HAZMAT_BOOTS_RED.get(), hazRed * boots);

        registerHazmat(ModArmorItems.HAZMAT_HELMET_GREY.get(), hazGray * helmet);
        registerHazmat(ModArmorItems.HAZMAT_CHESTPLATE_GREY.get(), hazGray * chest);
        registerHazmat(ModArmorItems.HAZMAT_LEGGINGS_GREY.get(), hazGray * legs);
        registerHazmat(ModArmorItems.HAZMAT_BOOTS_GREY.get(), hazGray * boots);

        registerHazmat(ModArmorItems.LIQUIDATOR_HELMET.get(), liquidator * helmet);
        registerHazmat(ModArmorItems.LIQUIDATOR_CHESTPLATE.get(), liquidator * chest);
        registerHazmat(ModArmorItems.LIQUIDATOR_LEGGINGS.get(), liquidator * legs);
        registerHazmat(ModArmorItems.LIQUIDATOR_BOOTS.get(), liquidator * boots);

        registerHazmat(ModArmorItems.T45_HELMET.get(), t51 * helmet);
        registerHazmat(ModArmorItems.T45_CHESTPLATE.get(), t51 * chest);
        registerHazmat(ModArmorItems.T45_LEGGINGS.get(), t51 * legs);
        registerHazmat(ModArmorItems.T45_BOOTS.get(), t51 * boots);

        registerHazmat(ModArmorItems.T51_HELMET.get(), t51 * helmet);
        registerHazmat(ModArmorItems.T51_CHESTPLATE.get(), t51 * chest);
        registerHazmat(ModArmorItems.T51_LEGGINGS.get(), t51 * legs);
        registerHazmat(ModArmorItems.T51_BOOTS.get(), t51 * boots);

        registerHazmat(ModArmorItems.AJR_HELMET.get(), ajr * helmet);
        registerHazmat(ModArmorItems.AJR_CHESTPLATE.get(), ajr * chest);
        registerHazmat(ModArmorItems.AJR_LEGGINGS.get(), ajr * legs);
        registerHazmat(ModArmorItems.AJR_BOOTS.get(), ajr * boots);

        registerHazmat(ModArmorItems.AJRO_HELMET.get(), ajr * helmet);
        registerHazmat(ModArmorItems.AJRO_CHESTPLATE.get(), ajr * chest);
        registerHazmat(ModArmorItems.AJRO_LEGGINGS.get(), ajr * legs);
        registerHazmat(ModArmorItems.AJRO_BOOTS.get(), ajr * boots);

        registerHazmat(ModArmorItems.BJ_HELMET.get(), bj * helmet);
        registerHazmat(ModArmorItems.BJ_CHESTPLATE.get(), bj * chest);
        registerHazmat(ModArmorItems.BJ_CHESTPLATE_JETPACK.get(), bj * chest);
        registerHazmat(ModArmorItems.BJ_LEGGINGS.get(), bj * legs);
        registerHazmat(ModArmorItems.BJ_BOOTS.get(), bj * boots);

        registerHazmat(ModArmorItems.STEAMSUIT_HELMET.get(), 1.3 * helmet);
        registerHazmat(ModArmorItems.STEAMSUIT_CHESTPLATE.get(), 1.3 * chest);
        registerHazmat(ModArmorItems.STEAMSUIT_LEGGINGS.get(), 1.3 * legs);
        registerHazmat(ModArmorItems.STEAMSUIT_BOOTS.get(), 1.3 * boots);

        registerHazmat(ModArmorItems.ENVSUIT_HELMET.get(), env * helmet);
        registerHazmat(ModArmorItems.ENVSUIT_CHESTPLATE.get(), env * chest);
        registerHazmat(ModArmorItems.ENVSUIT_LEGGINGS.get(), env * legs);
        registerHazmat(ModArmorItems.ENVSUIT_BOOTS.get(), env * boots);

        registerHazmat(ModArmorItems.HEV_HELMET.get(), hev * helmet);
        registerHazmat(ModArmorItems.HEV_CHESTPLATE.get(), hev * chest);
        registerHazmat(ModArmorItems.HEV_LEGGINGS.get(), hev * legs);
        registerHazmat(ModArmorItems.HEV_BOOTS.get(), hev * boots);

        registerHazmat(ModArmorItems.RPA_HELMET.get(), rpa * helmet);
        registerHazmat(ModArmorItems.RPA_CHESTPLATE.get(), rpa * chest);
        registerHazmat(ModArmorItems.RPA_LEGGINGS.get(), rpa * legs);
        registerHazmat(ModArmorItems.RPA_BOOTS.get(), rpa * boots);

        registerHazmat(ModArmorItems.TRENCHMASTER_HELMET.get(), trench * helmet);
        registerHazmat(ModArmorItems.TRENCHMASTER_CHESTPLATE.get(), trench * chest);
        registerHazmat(ModArmorItems.TRENCHMASTER_LEGGINGS.get(), trench * legs);
        registerHazmat(ModArmorItems.TRENCHMASTER_BOOTS.get(), trench * boots);

        registerHazmat(ModArmorItems.FAU_HELMET.get(), fau * helmet);
        registerHazmat(ModArmorItems.FAU_CHESTPLATE.get(), fau * chest);
        registerHazmat(ModArmorItems.FAU_LEGGINGS.get(), fau * legs);
        registerHazmat(ModArmorItems.FAU_BOOTS.get(), fau * boots);

        registerHazmat(ModArmorItems.DNT_HELMET.get(), dns * helmet);
        registerHazmat(ModArmorItems.DNT_CHESTPLATE.get(), dns * chest);
        registerHazmat(ModArmorItems.DNT_LEGGINGS.get(), dns * legs);
        registerHazmat(ModArmorItems.DNT_BOOTS.get(), dns * boots);
        

        registerHazmat(ModArmorItems.PAA_CHESTPLATE.get(), paa * chest);
        registerHazmat(ModArmorItems.PAA_LEGGINGS.get(), paa * legs);
        registerHazmat(ModArmorItems.PAA_BOOTS.get(), paa * boots);
        

        registerHazmat(ModArmorItems.HAZMAT_PAA_HELMET.get(), paa * helmet);
        registerHazmat(ModArmorItems.HAZMAT_PAA_CHESTPLATE.get(), paa * chest);
        registerHazmat(ModArmorItems.HAZMAT_PAA_LEGGINGS.get(), paa * legs);
        registerHazmat(ModArmorItems.HAZMAT_PAA_BOOTS.get(), paa * boots);        
        
        registerHazmat(ModArmorItems.SECURITY_HELMET.get(), security * helmet);
        registerHazmat(ModArmorItems.SECURITY_CHESTPLATE.get(), security * chest);
        registerHazmat(ModArmorItems.SECURITY_LEGGINGS.get(), security * legs);
        registerHazmat(ModArmorItems.SECURITY_BOOTS.get(), security * boots);

        registerHazmat(ModArmorItems.STARMETAL_HELMET.get(), star * helmet);
        registerHazmat(ModArmorItems.STARMETAL_CHESTPLATE.get(), star * chest);
        registerHazmat(ModArmorItems.STARMETAL_LEGGINGS.get(), star * legs);
        registerHazmat(ModArmorItems.STARMETAL_BOOTS.get(), star * boots);       

        registerHazmat(ModArmorItems.JACKET.get(), 0.1);
        registerHazmat(ModArmorItems.JACKET_2.get(), 0.1);

        registerHazmat(ModArmorItems.GAS_MASK.get(), 0.07);
        registerHazmat(ModArmorItems.GAS_MASK_M65.get(), 0.095);
        
        registerHazmat(ModArmorItems.STEEL_HELMET.get(), steel * helmet);
        registerHazmat(ModArmorItems.STEEL_CHESTPLATE.get(), steel * chest);
        registerHazmat(ModArmorItems.STEEL_LEGGINGS.get(), steel * legs);
        registerHazmat(ModArmorItems.STEEL_BOOTS.get(), steel * boots);

        registerHazmat(ModArmorItems.TITANIUM_HELMET.get(), titanium * helmet);
        registerHazmat(ModArmorItems.TITANIUM_CHESTPLATE.get(), titanium * chest);
        registerHazmat(ModArmorItems.TITANIUM_LEGGINGS.get(), titanium * legs);
        registerHazmat(ModArmorItems.TITANIUM_BOOTS.get(), titanium * boots);

        registerHazmat(ModArmorItems.COBALT_HELMET.get(), cobalt * helmet);
        registerHazmat(ModArmorItems.COBALT_CHESTPLATE.get(), cobalt * chest);
        registerHazmat(ModArmorItems.COBALT_LEGGINGS.get(), cobalt * legs);
        registerHazmat(ModArmorItems.COBALT_BOOTS.get(), cobalt * boots);

        // Ванильные предметы
        registerHazmat(Items.IRON_HELMET, iron * helmet);
        registerHazmat(Items.IRON_CHESTPLATE, iron * chest);
        registerHazmat(Items.IRON_LEGGINGS, iron * legs);
        registerHazmat(Items.IRON_BOOTS, iron * boots);

        registerHazmat(Items.GOLDEN_HELMET, gold * helmet);
        registerHazmat(Items.GOLDEN_CHESTPLATE, gold * chest);
        registerHazmat(Items.GOLDEN_LEGGINGS, gold * legs);
        registerHazmat(Items.GOLDEN_BOOTS, gold * boots);

        registerHazmat(ModArmorItems.ADVANCED_ALLOY_HELMET.get(), alloy * helmet);
        registerHazmat(ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get(), alloy * chest);
        registerHazmat(ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get(), alloy * legs);
        registerHazmat(ModArmorItems.ADVANCED_ALLOY_BOOTS.get(), alloy * boots);

        registerHazmat(ModArmorItems.CMB_HELMET.get(), cmb * helmet);
        registerHazmat(ModArmorItems.CMB_CHESTPLATE.get(), cmb * chest);
        registerHazmat(ModArmorItems.CMB_LEGGINGS.get(), cmb * legs);
        registerHazmat(ModArmorItems.CMB_BOOTS.get(), cmb * boots);

        registerHazmat(ModArmorItems.SCHRABIDIUM_HELMET.get(), schrab * helmet);
        registerHazmat(ModArmorItems.SCHRABIDIUM_CHESTPLATE.get(), schrab * chest);
        registerHazmat(ModArmorItems.SCHRABIDIUM_LEGGINGS.get(), schrab * legs);
        registerHazmat(ModArmorItems.SCHRABIDIUM_BOOTS.get(), schrab * boots);
        
        registerHazmat(ModArmorItems.EUPHEMIUM_HELMET.get(), euph * helmet);
        registerHazmat(ModArmorItems.EUPHEMIUM_CHESTPLATE.get(), euph * chest);
        registerHazmat(ModArmorItems.EUPHEMIUM_LEGGINGS.get(), euph * legs);
        registerHazmat(ModArmorItems.EUPHEMIUM_BOOTS.get(), euph * boots);

    }

    public static void registerHazmat(Item item, double resistance) {
        entries.put(item, resistance);
    }

    public static double getResistance(ItemStack stack) {
        if(stack == null || stack.isEmpty())
            return 0;

        double cladding = getCladding(stack);
        Double resistance = entries.get(stack.getItem());

        if(resistance != null)
            return resistance + cladding;

        return cladding;
    }

    public static double getCladding(ItemStack stack) {
        // Проверка NBT тега
        if(stack.hasTag() && stack.getTag() != null && stack.getTag().contains("hfr_cladding")) {
            return stack.getTag().getDouble("hfr_cladding");
        }

        // Проверка модификаций брони
        if(ArmorModHandler.hasMods(stack)) {
            ItemStack[] mods = ArmorModHandler.pryMods(stack);
            if(ArmorModHandler.CLADDING < mods.length) {
                ItemStack cladding = mods[ArmorModHandler.CLADDING];
                if(cladding != null && !cladding.isEmpty() && cladding.getItem() instanceof ItemModCladding) {
                    return ((ItemModCladding) cladding.getItem()).getRadResistance();
                }
            }
        }

        return 0;
    }

    public static float getResistance(Player player) {
        float res = 0.0F;

        // Суммируем защиту со всей брони
        for(ItemStack armor : player.getInventory().armor) {
            if(!armor.isEmpty()) {
                res += getResistance(armor);
            }
        }

        // Эффект зелья radx
        if(player.hasEffect(HbmPotion.RADX.get())) {
            res += 0.2F;
        }

        return res;
    }


    // Вспомогательные методы
    public static HashMap<Item, Double> getEntries() {
        return new HashMap<>(entries);
    }

    public static boolean hasResistance(Item item) {
        return entries.containsKey(item);
    }

    public static double getBaseResistance(Item item) {
        return entries.getOrDefault(item, 0.0);
    }

    public static double coefficientToPercentage(double coefficient) {
        // В оригинале: real_coefficient = 5, формула: percentage = 1 - 1/(1 + coefficient*5)
        return 1.0 - 1.0 / (1.0 + coefficient * 5.0);
    }
}