package com.hbm.integration.jei.rotaryfurnace;

import com.hbm.integration.jei.FluidColorIngredient;
import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.material.Mats;
import com.hbm.util.ResLocation;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public class RotaryFurnaceRecipeWrapper implements HBMRecipeWrapper {

    private static final ResourceLocation LAVA = ResLocation.ResLocation("hbm", "textures/block/network/lava_gray.png");

    private final List<List<ItemStack>> inputVariants;
    private final FluidColorIngredient inputFluid;
    private final Mats.MaterialStack outputMaterial;
    private final int duration;
    private final int steam;

    // Маппинг позиций для входных слотов (x, y)
    private static final int[][] INPUT_SLOT_POSITIONS = {
            {8, 18},   // слот 0
            {26, 18},   // слот 1
            {44, 18}    // слот 2
    };

    public RotaryFurnaceRecipeWrapper(List<List<ItemStack>> inputVariants, FluidColorIngredient inputFluid,
                                      Mats.MaterialStack outputMaterial, int duration, int steam) {
        this.inputVariants = inputVariants;
        this.inputFluid = inputFluid;
        this.outputMaterial = outputMaterial;
        this.duration = duration;
        this.steam = steam;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // Входные предметы: каждый ингредиент в своём слоте со всеми вариантами
        for (int i = 0; i < inputVariants.size() && i < INPUT_SLOT_POSITIONS.length; i++) {
            List<ItemStack> variants = inputVariants.get(i);
            if (!variants.isEmpty()) {
                int[] pos = INPUT_SLOT_POSITIONS[i];
                builder.addSlot(RecipeIngredientRole.INPUT, pos[0], pos[1])
                        .addItemStacks(variants);  // все альтернативы в одном слоте
            }
        }

        // Входная жидкость
        if (inputFluid != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 36)
                    .addIngredient(FluidColorIngredient.TYPE, inputFluid);
        }

        // Выходная жидкость (сплав)
        if (outputMaterial != null && outputMaterial.material != null) {
            String registryName = outputMaterial.material.names[0].toLowerCase(Locale.ROOT);
            String localizedName = Component.translatable(outputMaterial.material.getUnlocalizedName()).getString();
            FluidColorIngredient outputFluid = new FluidColorIngredient(
                    localizedName,
                    registryName,
                    outputMaterial.amount,
                    outputMaterial.material.moltenColor,
                    LAVA
            );
            builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 54)
                    .addIngredient(FluidColorIngredient.TYPE, outputFluid);
        }
    }

    public int getDuration() { return duration; }
    public int getSteam() { return steam; }
}