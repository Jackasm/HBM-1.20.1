// ModToolMaterials.java
package com.hbm.items.tool;

import com.hbm.items.ModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class ModToolMaterials {

    // ============ ОСНОВНЫЕ МАТЕРИАЛЫ ============

    public static final Tier SCHRABIDIUM = new Tier() {
        @Override
        public int getUses() {
            return 10000;
        }

        @Override
        public float getSpeed() {
            return 50.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 4; // Нетерайт
        }

        @Override
        public int getEnchantmentValue() {
            return 200;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_SCHRABIDIUM.get());
        }
    };

    public static final Tier SCHRABIDIUM_HAMMER = new Tier() {
        @Override
        public int getUses() {
            return 0; // Бесконечная прочность
        }

        @Override
        public float getSpeed() {
            return 50.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 4;
        }

        @Override
        public int getEnchantmentValue() {
            return 200;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_SCHRABIDIUM.get());
        }
    };

    public static final Tier CHAINSAW = new Tier() {
        @Override
        public int getUses() {
            return 1500;
        }

        @Override
        public float getSpeed() {
            return 50.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public static final Tier STEEL = new Tier() {
        @Override
        public int getUses() {
            return 500;
        }

        @Override
        public float getSpeed() {
            return 7.5F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 2;
        }

        @Override
        public int getEnchantmentValue() {
            return 10;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_STEEL.get());
        }
    };

    public static final Tier TITANIUM = new Tier() {
        @Override
        public int getUses() {
            return 750;
        }

        @Override
        public float getSpeed() {
            return 9.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_TITANIUM.get());
        }
    };

    public static final Tier ALLOY = new Tier() {
        @Override
        public int getUses() {
            return 2000;
        }

        @Override
        public float getSpeed() {
            return 15.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 5;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_ADVANCED_ALLOY.get());
        }
    };

    public static final Tier CMB = new Tier() {
        @Override
        public int getUses() {
            return 8500;
        }

        @Override
        public float getSpeed() {
            return 40.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 100;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_CMB_STEEL.get());
        }
    };

    public static final Tier ELECTRIC = new Tier() {
        @Override
        public int getUses() {
            return 0; // Использует энергию
        }

        @Override
        public float getSpeed() {
            return 30.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 2;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public static final Tier DESH = new Tier() {
        @Override
        public int getUses() {
            return 0; // Бесконечная прочность
        }

        @Override
        public float getSpeed() {
            return 7.5F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 2;
        }

        @Override
        public int getEnchantmentValue() {
            return 10;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_DESH.get());
        }
    };

    public static final Tier COBALT = new Tier() {
        @Override
        public int getUses() {
            return 750;
        }

        @Override
        public float getSpeed() {
            return 9.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 60;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_COBALT.get());
        }
    };

    // ============ ДОПОЛНИТЕЛЬНЫЕ МАТЕРИАЛЫ ============

    public static final Tier COBALT_DECORATED = new Tier() {
        @Override
        public int getUses() {
            return 2500;
        }

        @Override
        public float getSpeed() {
            return 15.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 75;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_COBALT.get());
        }
    };

    public static final Tier STARMETAL = new Tier() {
        @Override
        public int getUses() {
            return 3000;
        }

        @Override
        public float getSpeed() {
            return 20.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 100;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_STARMETAL.get());
        }
    };

    public static final Tier PIPE_LEAD = new Tier() {
        @Override
        public int getUses() {
            return 250; // durability
        }

        @Override
        public float getSpeed() {
            return 1.5F; // mining speed
        }

        @Override
        public float getAttackDamageBonus() {
            return 3.0F; // damage
        }

        @Override
        public int getLevel() {
            return 1; // harvest level (iron)
        }

        @Override
        public int getEnchantmentValue() {
            return 25; // enchantability
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_LEAD.get());
        }
    };

    public static final Tier SAW = new Tier() {
        @Override
        public int getUses() {
            return 750; // durability
        }

        @Override
        public float getSpeed() {
            return 2.0F; // mining speed
        }

        @Override
        public float getAttackDamageBonus() {
            return 3.5F; // damage
        }

        @Override
        public int getLevel() {
            return 2; // harvest level (iron)
        }

        @Override
        public int getEnchantmentValue() {
            return 25; // enchantability
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_STEEL.get()); // ремонтируется сталью
        }
    };

    public static final Tier BAT = new Tier() {
        @Override
        public int getUses() {
            return 500; // durability
        }

        @Override
        public float getSpeed() {
            return 1.5F; // mining speed
        }

        @Override
        public float getAttackDamageBonus() {
            return 3.0F; // damage
        }

        @Override
        public int getLevel() {
            return 0; // harvest level (wood)
        }

        @Override
        public int getEnchantmentValue() {
            return 25; // enchantability
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.OAK_PLANKS); // ремонтируется деревом
        }
    };

    public static final Tier BAT_NAIL = new Tier() {
        @Override
        public int getUses() {
            return 450; // durability
        }

        @Override
        public float getSpeed() {
            return 1.0F; // mining speed
        }

        @Override
        public float getAttackDamageBonus() {
            return 4.0F; // damage
        }

        @Override
        public int getLevel() {
            return 0; // harvest level (wood)
        }

        @Override
        public int getEnchantmentValue() {
            return 25; // enchantability
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.OAK_PLANKS); // ремонтируется деревом
        }
    };

    public static final Tier GOLF_CLUB = new Tier() {
        @Override
        public int getUses() {
            return 1000; // durability
        }

        @Override
        public float getSpeed() {
            return 2.0F; // mining speed
        }

        @Override
        public float getAttackDamageBonus() {
            return 5.0F; // damage
        }

        @Override
        public int getLevel() {
            return 1; // harvest level (stone)
        }

        @Override
        public int getEnchantmentValue() {
            return 25; // enchantability
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.IRON_INGOT); // ремонтируется железом
        }
    };

    public static final Tier PIPE_RUSTY = new Tier() {
        @Override
        public int getUses() {
            return 350; // durability
        }

        @Override
        public float getSpeed() {
            return 1.5F; // mining speed
        }

        @Override
        public float getAttackDamageBonus() {
            return 4.5F; // damage
        }

        @Override
        public int getLevel() {
            return 1; // harvest level (stone)
        }

        @Override
        public int getEnchantmentValue() {
            return 25; // enchantability
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.IRON_NUGGET); // ремонтируется железными самородками
        }
    };

    public static final Tier BOTTLE_OPENER = new Tier() {
        @Override
        public int getUses() {
            return 250;
        }

        @Override
        public float getSpeed() {
            return 1.5F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.5F;
        }

        @Override
        public int getLevel() {
            return 1;
        }

        @Override
        public int getEnchantmentValue() {
            return 200;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.IRON_INGOT);
        }
    };

    public static final Tier SHIMMER = new Tier() {
        @Override
        public int getUses() {
            return 0;
        }

        @Override
        public float getSpeed() {
            return 25.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 26.0F;
        }

        @Override
        public int getLevel() {
            return 1;
        }

        @Override
        public int getEnchantmentValue() {
            return 200;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.DIAMOND);
        }
    };

    // ============ МАТЕРИАЛЫ ДЛЯ МЕЧЕЙ ============

    public static final Tier CRUCIBLE = new Tier() {
        @Override
        public int getUses() {
            return 3;  // Прочность 3 (4 состояния: 0-3)
        }

        @Override
        public float getSpeed() {
            return 50.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 100.0F;  // Урон 100
        }

        @Override
        public int getLevel() {
            return 10;  // Уровень добычи 10
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_STEEL.get());  // Ремонт сталью
        }
    };

    public static final Tier ELEC = new Tier() {
        @Override
        public int getUses() {
            return 0;
        }

        @Override
        public float getSpeed() {
            return 30.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 12.0F;
        }

        @Override
        public int getLevel() {
            return 3;
        }

        @Override
        public int getEnchantmentValue() {
            return 2;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_DURA_STEEL.get());
        }
    };

    public static final Tier METEORITE = new Tier() {
        @Override
        public int getUses() {
            return 0;
        }

        @Override
        public float getSpeed() {
            return 50.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return 4;
        }

        @Override
        public int getEnchantmentValue() {
            return 200;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_PAA.get());
        }
    };

    public static final Tier MESE_GAVEL = new Tier() {
        @Override
        public int getUses() {
            return 0;
        }

        @Override
        public float getSpeed() {
            return 50.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return 4;
        }

        @Override
        public int getEnchantmentValue() {
            return 200;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_PAA.get());
        }
    };

    public static final Tier DWARVEN = new Tier() {
        @Override
        public int getUses() {
            return 250;
        }

        @Override
        public float getSpeed() {
            return 4.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return 2;
        }

        @Override
        public int getEnchantmentValue() {
            return 10;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_RED_COPPER.get());
        }
    };

    public static final Tier BISMUTH = new Tier() {
        @Override
        public int getUses() { return 0; }
        @Override
        public float getSpeed() { return 50.0F; }
        @Override
        public float getAttackDamageBonus() { return 0.0F; }
        @Override
        public int getLevel() { return 4; }
        @Override
        public int getEnchantmentValue() { return 200; }
        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_BISMUTH.get());
        }
    };

    public static final Tier VOLCANIC = new Tier() {
        @Override
        public int getUses() { return 0; }
        @Override
        public float getSpeed() { return 50.0F; }
        @Override
        public float getAttackDamageBonus() { return 0.0F; }
        @Override
        public int getLevel() { return 4; }
        @Override
        public int getEnchantmentValue() { return 200; }
        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.INGOT_BISMUTH.get());
        }
    };

    public static final Tier CHLOROPHYTE = new Tier() {
        @Override
        public int getUses() { return 0; }
        @Override
        public float getSpeed() { return 75.0F; }
        @Override
        public float getAttackDamageBonus() { return 0.0F; }
        @Override
        public int getLevel() { return 4; }
        @Override
        public int getEnchantmentValue() { return 200; }
        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.POWDER_CHLOROPHYTE.get());
        }
    };

    public static final Tier MESE = new Tier() {
        @Override
        public int getUses() { return 0; }
        @Override
        public float getSpeed() { return 100.0F; }
        @Override
        public float getAttackDamageBonus() { return 0.0F; }
        @Override
        public int getLevel() { return 4; }
        @Override
        public int getEnchantmentValue() { return 200; }
        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(ModItems.PLATE_PAA.get());
        }
    };


    // ============ РЕГИСТРАЦИЯ В TIER SORTING REGISTRY ============

    public static void registerTiers() {
        // Регистрируем порядок материалов для совместимости
        // (опционально, но рекомендуется для правильной работы с модами)

        // Пример регистрации (если нужно):
        // TierSortingRegistry.registerTier(
        //     SCHRABIDIUM,
        //     ResourceLocation.tryParse(RefStrings.MODID + ":schrabidium"),
        //     List.of(Tiers.NETHERITE),
        //     List.of()
        // );
    }
}