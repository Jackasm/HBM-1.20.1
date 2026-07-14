package com.hbm.integration.jei;

import com.hbm.integration.jei.BlastFurnace.BlastFurnaceRecipeCategory;
import com.hbm.integration.jei.BlastFurnace.BlastFurnaceRecipeHandler;
import com.hbm.integration.jei.anvil.AnvilConstructionRecipeCategory;
import com.hbm.integration.jei.anvil.AnvilConstructionRecipeHandler;
import com.hbm.integration.jei.anvil.AnvilRecipeCategory;
import com.hbm.integration.jei.anvil.AnvilRecipeHandler;
import com.hbm.integration.jei.arcwelder.ArcWelderRecipeCategory;
import com.hbm.integration.jei.arcwelder.ArcWelderRecipeHandler;
import com.hbm.integration.jei.centrifuge.CentrifugeRecipeCategory;
import com.hbm.integration.jei.centrifuge.CentrifugeRecipeHandler;
import com.hbm.integration.jei.combinationfurnace.CombinationFurnaceRecipeCategory;
import com.hbm.integration.jei.combinationfurnace.CombinationFurnaceRecipeHandler;
import com.hbm.integration.jei.crucible.CrucibleRecipeCategory;
import com.hbm.integration.jei.crucible.CrucibleRecipeHandler;
import com.hbm.integration.jei.crystallizer.CrystallizerRecipeCategory;
import com.hbm.integration.jei.crystallizer.CrystallizerRecipeHandler;
import com.hbm.integration.jei.gascentrifuge.GasCentrifugeRecipeCategory;
import com.hbm.integration.jei.gascentrifuge.GasCentrifugeRecipeHandler;
import com.hbm.integration.jei.press.PressRecipeCategory;
import com.hbm.integration.jei.press.PressRecipeHandler;
import com.hbm.integration.jei.rotaryfurnace.RotaryFurnaceRecipeCategory;
import com.hbm.integration.jei.rotaryfurnace.RotaryFurnaceRecipeHandler;
import com.hbm.integration.jei.solderingstation.SolderingStationRecipeCategory;
import com.hbm.integration.jei.solderingstation.SolderingStationRecipeHandler;
import com.hbm.inventory.gui.*;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.hbm.blocks.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

@JeiPlugin
public class HBMJeiPlugin implements IModPlugin {

    private static final ResourceLocation ID = ResLocation(MODID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AnvilRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AnvilConstructionRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PressRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new BlastFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CombinationFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RotaryFurnaceRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CrucibleRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SolderingStationRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ArcWelderRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new GasCentrifugeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CrystallizerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(HBMJeiRecipeTypes.ANVIL, AnvilRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.ANVIL_CONSTRUCTION, AnvilConstructionRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.PRESS, PressRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.BLAST_FURNACE, BlastFurnaceRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.COMBINATION_FURNACE, CombinationFurnaceRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.ROTARY_FURNACE, RotaryFurnaceRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.CRUCIBLE, CrucibleRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.SOLDERING_STATION, SolderingStationRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.ARC_WELDER, ArcWelderRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.GAS_CENTRIFUGE, GasCentrifugeRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.CRYSTALLIZER, CrystallizerRecipeHandler.getRecipes());
        registration.addRecipes(HBMJeiRecipeTypes.CENTRIFUGE, CentrifugeRecipeHandler.getRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ANVIL_IRON.get()), HBMJeiRecipeTypes.ANVIL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ANVIL_LEAD.get()), HBMJeiRecipeTypes.ANVIL);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_PRESS.get()), HBMJeiRecipeTypes.PRESS);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_BLAST_FURNACE.get()), HBMJeiRecipeTypes.BLAST_FURNACE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FURNACE_COMBINATION.get()), HBMJeiRecipeTypes.COMBINATION_FURNACE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_ROTARY_FURNACE.get()), HBMJeiRecipeTypes.ROTARY_FURNACE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_CRUCIBLE.get()), HBMJeiRecipeTypes.CRUCIBLE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_SOLDERING_STATION.get()), HBMJeiRecipeTypes.SOLDERING_STATION);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_ARC_WELDER.get()), HBMJeiRecipeTypes.ARC_WELDER);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_GAS_CENT.get()), HBMJeiRecipeTypes.GAS_CENTRIFUGE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_CRYSTALLIZER.get()), HBMJeiRecipeTypes.CRYSTALLIZER);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MACHINE_CENTRIFUGE.get()), HBMJeiRecipeTypes.CENTRIFUGE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(GUIAnvil.class, 35, 26, 18, 18, HBMJeiRecipeTypes.ANVIL);
        registration.addRecipeClickArea(GUIAnvil.class, 71, 26, 18, 18, HBMJeiRecipeTypes.ANVIL);
        registration.addRecipeClickArea(GUIMachinePress.class, 107, 35, 18, 18, HBMJeiRecipeTypes.PRESS);
        registration.addRecipeClickArea(GUIBlastFurnace.class, 110, 36, 18, 18, HBMJeiRecipeTypes.BLAST_FURNACE);
        registration.addRecipeClickArea(GUIFurnaceCombination.class, 54, 55, 18, 18, HBMJeiRecipeTypes.COMBINATION_FURNACE);
        registration.addRecipeClickArea(GUIMachineRotaryFurnace.class, 63, 26, 54, 18, HBMJeiRecipeTypes.ROTARY_FURNACE);
        registration.addRecipeClickArea(GUICrucible.class, 63, 0, 54, 16, HBMJeiRecipeTypes.CRUCIBLE);
        registration.addRecipeClickArea(GUIMachineSolderingStation.class, 72, 28, 33, 14, HBMJeiRecipeTypes.SOLDERING_STATION);
        registration.addRecipeClickArea(GUIMachineArcWelder.class, 72, 37, 33, 14, HBMJeiRecipeTypes.ARC_WELDER);
        registration.addRecipeClickArea(GUIMachineGasCent.class, 72, 37, 33, 14, HBMJeiRecipeTypes.GAS_CENTRIFUGE);
        registration.addRecipeClickArea(GUICrystallizer.class, 80, 46, 26, 14, HBMJeiRecipeTypes.CRYSTALLIZER);
        registration.addRecipeClickArea(GUIMachineCentrifuge.class, 28, 20, 36, 26, HBMJeiRecipeTypes.CENTRIFUGE);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        Collection<FluidColorIngredient> fluidIngredients = HBMFluidIngredients.getAllFluids();

        registration.register(
                FluidColorIngredient.TYPE,
                fluidIngredients,
                new FluidColorIngredient.Helper(),
                new FluidColorIngredient.Renderer()
        );

        Collection<MaterialIngredient> materialIngredients = new ArrayList<>();
        for (NTMMaterial material : Mats.orderedList) {
            materialIngredients.add(new MaterialIngredient(material, 144));
        }
        registration.register(
                MaterialIngredient.TYPE,
                materialIngredients,
                new MaterialIngredient.Helper(),
                new MaterialIngredient.Renderer()
        );
    }
}