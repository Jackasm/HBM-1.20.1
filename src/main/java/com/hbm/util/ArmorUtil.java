package com.hbm.util;

import com.hbm.api.item.IGasMask;
import com.hbm.handler.ArmorModHandler;
import com.hbm.handler.HazmatRegistry;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;

import com.hbm.items.ModToolItems;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry.HazardClass;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArmorUtil {



    public static boolean checkForDigamma(Player player) {

        // Проверка полного набора брони FAU
        if(checkArmor(player,
                ModArmorItems.FAU_HELMET.get(),
                ModArmorItems.FAU_CHESTPLATE.get(),
                ModArmorItems.FAU_LEGGINGS.get(),
                ModArmorItems.FAU_BOOTS.get())) {
            return true;
        }

        // Проверка полного набора брони DNS
        if(checkArmor(player,
                ModArmorItems.DNT_HELMET.get(),
                ModArmorItems.DNT_CHESTPLATE.get(),
                ModArmorItems.DNT_LEGGINGS.get(),
                ModArmorItems.DNT_BOOTS.get())) {
            return true;
        }

        // Проверка наличия эффекта стабильности
        return player.hasEffect(HbmPotion.STABILITY.get());
    }

    public static boolean checkArmor(LivingEntity entity, Item... armor) {
        for(int i = 0; i < 4; i++) {
            if(!checkArmorPiece(entity, armor[i], 3 - i))
                return false;
        }
        return true;
    }

    public static boolean checkArmorPiece(LivingEntity entity, Item armor, int slot) {
        ItemStack stack = entity.getItemBySlot(getEquipmentSlot(slot));
        return !stack.isEmpty() && stack.getItem() == armor;
    }

    public static boolean checkArmorNull(LivingEntity player, int slot) {
        return player.getItemBySlot(getEquipmentSlot(slot)).isEmpty();
    }

    private static net.minecraft.world.entity.EquipmentSlot getEquipmentSlot(int slot) {
        return switch(slot) {
            case 0 -> net.minecraft.world.entity.EquipmentSlot.FEET;
            case 1 -> net.minecraft.world.entity.EquipmentSlot.LEGS;
            case 2 -> net.minecraft.world.entity.EquipmentSlot.CHEST;
            case 3 -> net.minecraft.world.entity.EquipmentSlot.HEAD;
            default -> throw new IllegalArgumentException("Invalid slot: " + slot);
        };
    }

    public static void damageSuit(LivingEntity entity, int slot, int amount) {
        ItemStack stack = entity.getItemBySlot(getEquipmentSlot(slot));
        if(stack.isEmpty()) return;

        stack.hurtAndBreak(amount, entity, (e) -> e.broadcastBreakEvent(getEquipmentSlot(slot)));

        if(stack.getDamageValue() >= stack.getMaxDamage()) {
            entity.setItemSlot(getEquipmentSlot(slot), ItemStack.EMPTY);
        }
    }

    public static void resetFlightTime(Player player) {
        if(player instanceof ServerPlayer serverPlayer) {
            // В 1.20.1 используется different система для полета
            serverPlayer.fallDistance = 0;
            // Для сброса времени полета в воздухе
            if(player.getAbilities().flying) {
                // Сбрасываем время полета
            }
        }
    }

    @Deprecated
    public static boolean checkForHazmat(LivingEntity player) {
        // Проверка хазмат костюмов
        if(     

                checkArmor(player,
                    ModArmorItems.HAZMAT_HELMET.get(),
                    ModArmorItems.HAZMAT_CHESTPLATE.get(),
                    ModArmorItems.HAZMAT_LEGGINGS.get(),
                    ModArmorItems.HAZMAT_BOOTS.get()) ||

                checkArmor(player,
                        ModArmorItems.HAZMAT_HELMET_RED.get(),
                        ModArmorItems.HAZMAT_CHESTPLATE_RED.get(),
                        ModArmorItems.HAZMAT_LEGGINGS_RED.get(),
                        ModArmorItems.HAZMAT_BOOTS_RED.get()) ||

                checkArmor(player,
                        ModArmorItems.HAZMAT_HELMET_GREY.get(),
                        ModArmorItems.HAZMAT_CHESTPLATE_GREY.get(),
                        ModArmorItems.HAZMAT_LEGGINGS_GREY.get(),
                        ModArmorItems.HAZMAT_BOOTS_GREY.get()) ||

                checkArmor(player,
                        ModArmorItems.SCHRABIDIUM_HELMET.get(),
                        ModArmorItems.SCHRABIDIUM_BOOTS.get(),
                        ModArmorItems.SCHRABIDIUM_LEGGINGS.get(),
                        ModArmorItems.SCHRABIDIUM_BOOTS.get()) ||

                checkForHaz2(player)) {
            return true;
        }

        return player.hasEffect(HbmPotion.MUTATION.get());
    }

    public static boolean checkForHaz2(LivingEntity player) {

                return checkArmor(player,
                ModArmorItems.HAZMAT_PAA_HELMET.get(),
                ModArmorItems.HAZMAT_PAA_CHESTPLATE.get(),
                ModArmorItems.HAZMAT_PAA_LEGGINGS.get(),
                ModArmorItems.HAZMAT_PAA_BOOTS.get()) ||

                checkArmor(player,
                        ModArmorItems.LIQUIDATOR_HELMET.get(),
                        ModArmorItems.LIQUIDATOR_CHESTPLATE.get(),
                        ModArmorItems.LIQUIDATOR_LEGGINGS.get(),
                        ModArmorItems.LIQUIDATOR_BOOTS.get()) ||

                checkArmor(player,
                        ModArmorItems.EUPHEMIUM_HELMET.get(),
                        ModArmorItems.EUPHEMIUM_CHESTPLATE.get(),
                        ModArmorItems.EUPHEMIUM_LEGGINGS.get(),
                        ModArmorItems.EUPHEMIUM_BOOTS.get()) ||

                checkArmor(player,
                        ModArmorItems.RPA_HELMET.get(),
                        ModArmorItems.RPA_CHESTPLATE.get(),
                        ModArmorItems.RPA_LEGGINGS.get(),
                        ModArmorItems.RPA_BOOTS.get()) ||

                checkArmor(player,
                        ModArmorItems.FAU_HELMET.get(),
                        ModArmorItems.FAU_CHESTPLATE.get(),
                        ModArmorItems.FAU_LEGGINGS.get(),
                        ModArmorItems.FAU_BOOTS.get()) ||

                checkArmor(player,
                        ModArmorItems.DNT_HELMET.get(),
                        ModArmorItems.DNT_CHESTPLATE.get(),
                        ModArmorItems.DNT_LEGGINGS.get(),
                        ModArmorItems.DNT_BOOTS.get());

    }

    public static boolean checkForAsbestos(LivingEntity player) {
        return checkArmor(player,
                ModArmorItems.ASBESTOS_HELMET.get(),
                ModArmorItems.ASBESTOS_CHESTPLATE.get(),
                ModArmorItems.ASBESTOS_LEGGINGS.get(),
                ModArmorItems.ASBESTOS_BOOTS.get());
    }


    public static boolean checkForDigamma2(Player player) {
        if(!checkArmor(player,
                ModArmorItems.ROBES_HELMET.get(),
                ModArmorItems.ROBES_CHESTPLATE.get(),
                ModArmorItems.ROBES_LEGGINGS.get(),
                ModArmorItems.ROBES_BOOTS.get()))
            return false;

        if(!player.hasEffect(HbmPotion.STABILITY.get()))
            return false;

        for(int i = 0; i < 4; i++) {
            ItemStack armor = player.getItemBySlot(getEquipmentSlot(i));

            if(!armor.isEmpty() && ArmorModHandler.hasMods(armor)) {
                ItemStack[] mods = ArmorModHandler.pryMods(armor);
                if(!(mods[ArmorModHandler.CLADDING] != null &&
                        mods[ArmorModHandler.CLADDING].getItem() == ModItems.CLADDING_IRON.get()))
                    return false;
            }
        }

        return player.getMaxHealth() < 3;
    }

    public static boolean checkForFaraday(Player player) {
        ItemStack head = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);

        if(head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty())
            return false;

        return isFaradayArmor(head) && isFaradayArmor(chest) &&
                isFaradayArmor(legs) && isFaradayArmor(feet);
    }

    public static final String[] metals = new String[] {
            "chainmail", "iron", "silver", "gold", "platinum", "tin", "lead",
            "liquidator", "schrabidium", "euphemium", "steel", "cmb", "titanium",
            "alloy", "copper", "bronze", "electrum", "t45", "t51", "bj",
            "starmetal", "hazmat", "rubber", "hev", "ajr", "rpa", "spacesuit"
    };

    public static boolean isFaradayArmor(ItemStack item) {
        if(item.isEmpty()) return false;

        String name = item.getDescriptionId().toLowerCase(Locale.US);

        for(String metal : metals) {
            if(name.contains(metal)) return true;
        }

        return HazmatRegistry.getCladding(item) > 0;
    }




    public static boolean checkForFiend(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack mainHand = player.getMainHandItem();
        return !chest.isEmpty() && chest.getItem() == ModArmorItems.JACKET.get() &&
                !mainHand.isEmpty() && mainHand.getItem() == ModToolItems.SHIMMER_SLEDGE.get();
    }

    public static boolean checkForFiend2(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack mainHand = player.getMainHandItem();
        return !chest.isEmpty() && chest.getItem() == ModArmorItems.JACKET_2.get() &&
                !mainHand.isEmpty() && mainHand.getItem() == ModToolItems.SHIMMER_AXE.get();
    }



    /*
     * Default implementations for IGasMask items
     */
    public static final String FILTERK_KEY = "hfrFilter";

    public static void installGasMaskFilter(ItemStack mask, ItemStack filter) {
        if(mask.isEmpty() || filter.isEmpty())
            return;

        CompoundTag tag = mask.getOrCreateTag();
        CompoundTag attach = new CompoundTag();
        filter.save(attach); // Изменено с writeToNBT

        tag.put(FILTERK_KEY, attach);
    }

    public static void removeFilter(ItemStack mask) {
        if(mask.isEmpty()) return;

        CompoundTag tag = mask.getTag();
        if(tag != null) {
            tag.remove(FILTERK_KEY);
        }
    }

    public static ItemStack getGasMaskFilterRecursively(ItemStack mask, LivingEntity entity) {
        ItemStack filter = getGasMaskFilter(mask);

        if(filter.isEmpty() && ArmorModHandler.hasMods(mask)) {
            ItemStack[] mods = ArmorModHandler.pryMods(mask);

            if(mods[ArmorModHandler.HELMET_ONLY] != null &&
                    !mods[ArmorModHandler.HELMET_ONLY].isEmpty() &&
                    mods[ArmorModHandler.HELMET_ONLY].getItem() instanceof IGasMask gasMask) {
                filter = gasMask.getFilter(mods[ArmorModHandler.HELMET_ONLY], entity);
            }
        }

        return filter;
    }

    public static ItemStack getGasMaskFilter(ItemStack mask) {
        if(mask.isEmpty()) return ItemStack.EMPTY;

        CompoundTag tag = mask.getTag();
        if(tag == null || !tag.contains(FILTERK_KEY))
            return ItemStack.EMPTY;

        CompoundTag attach = tag.getCompound(FILTERK_KEY);
        return ItemStack.of(attach); // Изменено с loadItemStackFromNBT
    }

    /**
     * Проверяет защиту фильтра от конкретного класса опасности
     */
    public static boolean hasFilterProtection(ItemStack filter, HazardClass hazard) {
        if (filter.isEmpty()) return false;

        // Проверка через ArmorRegistry
        return ArmorRegistry.hasProtection(filter, hazard);
    }

    public static void damageGasMaskFilter(LivingEntity entity, int damage) {
        ItemStack mask = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);

        if(mask.isEmpty()) return;

        if(!(mask.getItem() instanceof IGasMask)) {
            if(ArmorModHandler.hasMods(mask)) {
                ItemStack[] mods = ArmorModHandler.pryMods(mask);

                if(mods[ArmorModHandler.HELMET_ONLY] != null &&
                        !mods[ArmorModHandler.HELMET_ONLY].isEmpty() &&
                        mods[ArmorModHandler.HELMET_ONLY].getItem() instanceof IGasMask) {
                    mask = mods[ArmorModHandler.HELMET_ONLY];
                }
            }
        }

        if(!mask.isEmpty())
            damageGasMaskFilter(mask, damage);
    }

    public static void damageGasMaskFilter(ItemStack mask, int damage) {
        ItemStack filter = getGasMaskFilter(mask);

        if(filter.isEmpty()) {
            if(ArmorModHandler.hasMods(mask)) {
                ItemStack[] mods = ArmorModHandler.pryMods(mask);

                if(mods[ArmorModHandler.HELMET_ONLY] != null &&
                        !mods[ArmorModHandler.HELMET_ONLY].isEmpty() &&
                        mods[ArmorModHandler.HELMET_ONLY].getItem() instanceof IGasMask) {
                    filter = getGasMaskFilter(mods[ArmorModHandler.HELMET_ONLY]);
                }
            }
        }

        if(filter.isEmpty() || filter.getMaxDamage() == 0)
            return;

        filter.setDamageValue(filter.getDamageValue() + damage);

        if(filter.getDamageValue() > filter.getMaxDamage())
            removeFilter(mask);
        else
            installGasMaskFilter(mask, filter);
    }

    public static void addGasMaskTooltip(ItemStack mask, Player player, List<Component> list, boolean ext) {
        if(mask.isEmpty() || !(mask.getItem() instanceof IGasMask))
            return;

        ItemStack filter = ((IGasMask) mask.getItem()).getFilter(mask, player);

        if(filter.isEmpty()) {
            list.add(Component.literal("No filter installed!").withStyle(ChatFormatting.RED));
            return;
        }

        list.add(Component.literal("Installed filter:").withStyle(ChatFormatting.GOLD));

        int damage = filter.getDamageValue();
        int maxDamage = filter.getMaxDamage();

        String append = "";

        if(maxDamage > 0) {
            append = " (" + ((maxDamage - damage) * 100 / maxDamage) + "%)";
        }

        List<Component> lore = new ArrayList<>();
        list.add(Component.literal("  " + filter.getHoverName().getString() + append));

        // Добавление информации о предмете
        filter.getItem().appendHoverText(filter, player.level(), lore,
                player.isCreative() ? net.minecraft.world.item.TooltipFlag.ADVANCED :
                        net.minecraft.world.item.TooltipFlag.Default.NORMAL);

        ForgeEventFactory.onItemTooltip(filter, player, lore,
                player.isCreative() ? net.minecraft.world.item.TooltipFlag.ADVANCED :
                        net.minecraft.world.item.TooltipFlag.Default.NORMAL);

        for(Component line : lore) {
            list.add(Component.literal("  ").append(line).withStyle(ChatFormatting.YELLOW));
        }
    }

    public static boolean isWearingEmptyMask(Player player) {
        ItemStack mask = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);

        if(mask.isEmpty()) return false;

        if(mask.getItem() instanceof IGasMask) {
            return getGasMaskFilter(mask).isEmpty();
        }

        ItemStack mod = ArmorModHandler.pryMods(mask)[ArmorModHandler.HELMET_ONLY];

        if(mod != null && !mod.isEmpty() && mod.getItem() instanceof IGasMask) {
            return getGasMaskFilter(mod).isEmpty();
        }

        return false;
    }

}