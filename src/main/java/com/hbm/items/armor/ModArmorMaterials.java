package com.hbm.items.armor;

import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class ModArmorMaterials {

    public static final ArmorMaterial TITANIUM = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 25;
        private final int ENCHANTABILITY = 9;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_TITANIUM.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":titanium";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial STEEL = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{2, 6, 5, 2};
        private final int DURABILITY_MULT = 20;
        private final int ENCHANTABILITY = 5;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_STEEL.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":steel";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial ADVANCED_ALLOY = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 40;
        private final int ENCHANTABILITY = 12;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_ADVANCED_ALLOY.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":alloy";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    // Новые материалы
    public static final ArmorMaterial SCHRABIDIUM = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 100;
        private final int ENCHANTABILITY = 50;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_SCHRABIDIUM.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":schrabidium";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial EUPHEMIUM = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 15000000;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_EUPHEMIUM.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":euphemium";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial HAZMAT = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{2, 5, 4, 1};
        private final int DURABILITY_MULT = 60;
        private final int ENCHANTABILITY = 5;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.HAZMAT_CLOTH.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":hazmat";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial HAZMAT2 = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{2, 5, 4, 1};
        private final int DURABILITY_MULT = 60;
        private final int ENCHANTABILITY = 5;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.HAZMAT_CLOTH_RED.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":hazmat2";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial HAZMAT3 = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{2, 5, 4, 1};
        private final int DURABILITY_MULT = 60;
        private final int ENCHANTABILITY = 5;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.HAZMAT_CLOTH_GREY.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":hazmat3";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial ASBESTOS = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{1, 4, 3, 1};
        private final int DURABILITY_MULT = 20;
        private final int ENCHANTABILITY = 5;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.ASBESTOS_CLOTH.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":asbestos";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial PAA = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 75;
        private final int ENCHANTABILITY = 25;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_PAA.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":paa";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial CMB = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 60;
        private final int ENCHANTABILITY = 50;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_CMB_STEEL.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":cmb";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial AUSIII = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{2, 6, 5, 2};
        private final int DURABILITY_MULT = 375;
        private final int ENCHANTABILITY = 0;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_AUSTRALIUM.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":ausiii";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial SECURITY = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 100;
        private final int ENCHANTABILITY = 15;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_KEVLAR.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":security";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial COBALT = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 70;
        private final int ENCHANTABILITY = 60;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_COBALT.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":cobalt";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial STARMETAL = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_STARMETAL.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":starmetal";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial BISMUTH = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 100;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_BISMUTH.get()); // Нужно проверить правильность названия
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":bismuth";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial ZIRCONIUM = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{2, 5, 3, 1};
        private final int DURABILITY_MULT = 1000;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_ZIRCONIUM.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":zirconium";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial LIQUIDATOR = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3}; // [HELMET, CHESTPLATE, LEGGINGS, BOOTS]
        private final int DURABILITY_MULT = 40; // из оригинального aMatLiquidator
        private final int ENCHANTABILITY = 12;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_STEEL.get()); // или другой материал
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":liquidator";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial T45 = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3}; // [HELMET, CHESTPLATE, LEGGINGS, BOOTS]
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 0;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_TITANIUM.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":t45";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial T51 = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3}; // [HELMET, CHESTPLATE, LEGGINGS, BOOTS]
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 0;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_TITANIUM.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":t51";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial AJR = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_AJR.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":ajr";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial DESH = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 0;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_DESH.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":desh";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial DIESEL = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 0;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_COPPER.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":diesel";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial HEV = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_HEV.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":hev";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial DIGAMMA = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_FAU.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":digamma";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial DNT = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_DNT.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":dnt";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial ENV = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_HEV.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":env";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial TRENCH = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3};
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.IRON_INGOT);
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":trench";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial ROBES = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{1, 4, 5, 2}; // [HELMET, CHESTPLATE, LEGGINGS, BOOTS]
        private final int DURABILITY_MULT = 10;
        private final int ENCHANTABILITY = 12;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.STRING);
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":robes";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };

    public static final ArmorMaterial BJ = new ArmorMaterial() {
        private final int[] PROTECTION = new int[]{3, 8, 6, 3}; // [HELMET, CHESTPLATE, LEGGINGS, BOOTS]
        private final int DURABILITY_MULT = 150;
        private final int ENCHANTABILITY = 100;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> DURABILITY_MULT * 13;
                case LEGGINGS -> DURABILITY_MULT * 15;
                case CHESTPLATE -> DURABILITY_MULT * 16;
                case HELMET -> DURABILITY_MULT * 11;
            };
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return switch (type) {
                case BOOTS -> PROTECTION[3];
                case LEGGINGS -> PROTECTION[2];
                case CHESTPLATE -> PROTECTION[1];
                case HELMET -> PROTECTION[0];
            };
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_ARMOR_LUNAR.get());
        }

        @Override
        public @NotNull String getName() {
            return RefStrings.MODID + ":bj";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    };
}