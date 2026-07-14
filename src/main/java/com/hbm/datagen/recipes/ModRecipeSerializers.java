package com.hbm.datagen.recipes;

import com.hbm.datagen.recipes.ingredient.AnyAshIngredient;
import com.hbm.datagen.recipes.ingredient.FluidBucketIngredient;
import com.hbm.datagen.recipes.ingredient.FluidTankIngredient;
import com.hbm.inventory.recipes.RecipeCache;
import com.hbm.inventory.recipes.RecipeType;
import com.hbm.util.RefStrings;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.hbm.util.ResLocation.ResLocation;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipeSerializers {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CraftingHelper.register(ResLocation(RefStrings.MODID, "fluid_bucket"),
                    FluidBucketIngredient.Serializer.INSTANCE);

            CraftingHelper.register(ResLocation(RefStrings.MODID, "fluid_tank"),
                    FluidTankIngredient.Serializer.INSTANCE);

            CraftingHelper.register(ResLocation(RefStrings.MODID, "any_ash"),
                    AnyAshIngredient.Serializer.INSTANCE);


            // Инициализируем кэш рецептов
            for (RecipeType type : RecipeType.values()) {
                RecipeCache.getDisplayRecipes(type);

            }
        });
    }
}
