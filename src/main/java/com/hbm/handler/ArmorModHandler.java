package com.hbm.handler;

import com.hbm.items.armor.ItemArmorMod;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("JavadocDeclaration")
public class ArmorModHandler {

    public static final int HELMET_ONLY = 0;
    public static final int PLATE_ONLY = 1;
    public static final int LEGS_ONLY = 2;
    public static final int BOOTS_ONLY = 3;
    public static final int SERVOS = 4;
    public static final int CLADDING = 5;
    public static final int KEVLAR = 6;
    public static final int EXTRA = 7;
    public static final int BATTERY = 8;

    public static final int MOD_SLOTS = 9;

    public static final UUID[] fixedUUIDs = new UUID[] {
            UUID.fromString("e572caf4-3e65-4152-bc79-c4d4048cbd29"),
            UUID.fromString("bed30902-8a6a-4769-9f65-2a9b67469fff"),
            UUID.fromString("baebf7b3-1eda-4a14-b233-068e2493e9a2"),
            UUID.fromString("28016c1b-d992-4324-9409-a9f9f0ffb85c")
    };

    public static final String MOD_COMPOUND_KEY = "ntm_armor_mods";
    public static final String MOD_SLOT_KEY = "mod_slot_";

    /**
     * Checks if a mod can be applied to an armor piece
     * Needs to be used to prevent people from inserting invalid items into the armor table
     * @param armor
     * @param mod
     * @return
     */
    public static boolean isApplicable(ItemStack armor, ItemStack mod) {
        if (armor == null || mod == null)
            return false;

        if (!(armor.getItem() instanceof ArmorItem armorItem))
            return false;

        if (!(mod.getItem() instanceof ItemArmorMod aMod))
            return false;

        net.minecraft.world.entity.EquipmentSlot slot = armorItem.getEquipmentSlot();

        return (slot == net.minecraft.world.entity.EquipmentSlot.HEAD && aMod.helmet) ||
                (slot == net.minecraft.world.entity.EquipmentSlot.CHEST && aMod.chestplate) ||
                (slot == net.minecraft.world.entity.EquipmentSlot.LEGS && aMod.leggings) ||
                (slot == net.minecraft.world.entity.EquipmentSlot.FEET && aMod.boots);
    }

    /**
     * Applies an mod to the given armor piece
     * Make sure to check for applicability first
     * Will override present mods so make sure to only use unmodded armor pieces
     * @param armor
     * @param mod
     */
    public static void applyMod(ItemStack armor, ItemStack mod) {
        if (armor.isEmpty() || mod.isEmpty()) return;
        if (!(mod.getItem() instanceof ItemArmorMod armorMod)) return;

        CompoundTag nbt = armor.getOrCreateTag();

        if (!nbt.contains(MOD_COMPOUND_KEY)) {
            nbt.put(MOD_COMPOUND_KEY, new CompoundTag());
        }
        CompoundTag mods = nbt.getCompound(MOD_COMPOUND_KEY);

        int slot = armorMod.type;

        CompoundTag cmp = new CompoundTag();
        mod.save(cmp);
        mods.put(MOD_SLOT_KEY + slot, cmp);
    }

    /**
     * Removes the mod from the given slot
     * @param armor
     * @param slot
     */
    public static void removeMod(ItemStack armor, int slot) {
        if (armor.isEmpty()) return;

        CompoundTag tag = armor.getOrCreateTag();

        if (!tag.contains(MOD_COMPOUND_KEY))
            tag.put(MOD_COMPOUND_KEY, new CompoundTag());

        CompoundTag mods = tag.getCompound(MOD_COMPOUND_KEY);
        mods.remove(MOD_SLOT_KEY + slot);

        if (mods.isEmpty())
            clearMods(armor);
    }

    /**
     * Removes ALL mods
     * Should be used when the armor piece is put in the armor table slot AFTER the armor pieces have been separated
     * @param armor
     */
    public static void clearMods(ItemStack armor) {
        if (armor.isEmpty()) return;

        CompoundTag tag = armor.getTag();
        if (tag != null) {
            tag.remove(MOD_COMPOUND_KEY);
        }
    }

    /**
     * Does what the name implies. Returns true if the stack has NBT and that NBT has the MOD_COMPOUND_KEY tag.
     * @param armor
     * @return
     */
    public static boolean hasMods(ItemStack armor) {
        if (armor.isEmpty()) return false;

        CompoundTag tag = armor.getTag();
        return tag != null && tag.contains(MOD_COMPOUND_KEY);
    }

    /**
     * Gets all the modifications in the provided armor
     * @param armor
     * @return
     */
    public static ItemStack[] pryMods(ItemStack armor) {
        ItemStack[] slots = new ItemStack[MOD_SLOTS];

        if (!hasMods(armor))
            return slots;

        CompoundTag tag = armor.getTag();
        CompoundTag mods = Objects.requireNonNull(tag).getCompound(MOD_COMPOUND_KEY);

        for (int i = 0; i < MOD_SLOTS; i++) {
            if (mods.contains(MOD_SLOT_KEY + i)) {
                CompoundTag cmp = mods.getCompound(MOD_SLOT_KEY + i);
                ItemStack stack = ItemStack.of(cmp);

                if (!stack.isEmpty()) {
                    slots[i] = stack;
                } else {
                    removeMod(armor, i);
                }
            }
        }

        return slots;
    }

    /**
     * Gets the mod from the specific slot
     * @param armor
     * @param slot
     * @return
     */
    public static ItemStack pryMod(ItemStack armor, int slot) {
        if (!hasMods(armor))
            return ItemStack.EMPTY;

        CompoundTag tag = armor.getTag();
        CompoundTag mods = Objects.requireNonNull(tag).getCompound(MOD_COMPOUND_KEY);

        if (!mods.contains(MOD_SLOT_KEY + slot))
            return ItemStack.EMPTY;

        CompoundTag cmp = mods.getCompound(MOD_SLOT_KEY + slot);
        ItemStack stack = ItemStack.of(cmp);

        if (!stack.isEmpty())
            return stack;

        removeMod(armor, slot);
        return ItemStack.EMPTY;
    }
}