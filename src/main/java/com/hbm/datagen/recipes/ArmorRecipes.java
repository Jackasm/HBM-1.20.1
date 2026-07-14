package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.ItemFluidTank;
import com.hbm.items.fluid.ItemGasTank;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

import static com.hbm.util.RefStrings.MODID;

public class ArmorRecipes extends ModRecipeProvider {

    private static Consumer<FinishedRecipe> writer;

    public static final String[] patternHelmet = new String[] {"XXX", "X X"};
    public static final String[] patternChestplate = new String[] {"X X", "XXX", "XXX"};
    public static final String[] patternLeggings = new String[] {"XXX", "X X", "X X"};
    public static final String[] patternBoots = new String[] {"X X", "X X"};

    public ArmorRecipes(PackOutput pOutput) {super(pOutput);}

    public static void generateArmorRecipes(Consumer<FinishedRecipe> pWriter)
    {
        writer = pWriter;

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MACHINE_ARMOR_TABLE.get(), 1)
                .pattern("PPP")
                .pattern("TCT")
                .pattern("TST")
                .define('P', ModItems.PLATE_STEEL.get())
                .define('T', ModItems.INGOT_TUNGSTEN.get())
                .define('C', Blocks.CRAFTING_TABLE)
                .define('S', ModBlocks.BLOCK_STEEL.get().asItem())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_tungsten_ingot", has(ModItems.INGOT_TUNGSTEN.get()))
                .save(pWriter, MODID + ":machine_armor_table");

        addHelmet(ModItems.INGOT_STEEL.get(), ModArmorItems.STEEL_HELMET.get());
        addChest(ModItems.INGOT_STEEL.get(), ModArmorItems.STEEL_CHESTPLATE.get());
        addLegs(ModItems.INGOT_STEEL.get(), ModArmorItems.STEEL_LEGGINGS.get());
        addBoots(ModItems.INGOT_STEEL.get(), ModArmorItems.STEEL_BOOTS.get());
        addHelmet(ModItems.INGOT_TITANIUM.get(), ModArmorItems.TITANIUM_HELMET.get());
        addChest(ModItems.INGOT_TITANIUM.get(), ModArmorItems.TITANIUM_CHESTPLATE.get());
        addLegs(ModItems.INGOT_TITANIUM.get(), ModArmorItems.TITANIUM_LEGGINGS.get());
        addBoots(ModItems.INGOT_TITANIUM.get(), ModArmorItems.TITANIUM_BOOTS.get());
        addHelmet(ModItems.INGOT_ADVANCED_ALLOY.get(), ModArmorItems.ADVANCED_ALLOY_HELMET.get());
        addChest(ModItems.INGOT_ADVANCED_ALLOY.get(), ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get());
        addLegs(ModItems.INGOT_ADVANCED_ALLOY.get(), ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get());
        addBoots(ModItems.INGOT_ADVANCED_ALLOY.get(), ModArmorItems.ADVANCED_ALLOY_BOOTS.get());
        addHelmet(ModItems.INGOT_CMB_STEEL.get(), ModArmorItems.CMB_HELMET.get());
        addChest(ModItems.INGOT_CMB_STEEL.get(), ModArmorItems.CMB_CHESTPLATE.get());
        addLegs(ModItems.INGOT_CMB_STEEL.get(), ModArmorItems.CMB_LEGGINGS.get());
        addBoots(ModItems.INGOT_CMB_STEEL.get(), ModArmorItems.CMB_BOOTS.get());
        addHelmet(ModItems.RAG.get(), ModArmorItems.ROBES_HELMET.get());
        addChest(ModItems.RAG.get(), ModArmorItems.ROBES_CHESTPLATE.get());
        addLegs(ModItems.RAG.get(), ModArmorItems.ROBES_LEGGINGS.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ROBES_BOOTS.get(), 1)
                .pattern("R R")
                .pattern("P P")
                .define('R', ModItems.RAG.get())
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_rag", has(ModItems.RAG.get()))
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(writer, MODID + ":robes_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.COBALT_HELMET.get(), 1)
                .pattern("ECE")
                .define('E', ModItems.BILLET_COBALT.get())
                .define('C', ModArmorItems.STEEL_HELMET.get())
                .unlockedBy("has_cobalt", has(ModItems.BILLET_COBALT.get()))
                .save(writer, MODID + ":cobalt_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.COBALT_CHESTPLATE.get(), 1)
                .pattern(" E ")
                .pattern("ECE")
                .pattern(" E ")
                .define('E', ModItems.BILLET_COBALT.get())
                .define('C', ModArmorItems.STEEL_CHESTPLATE.get())
                .unlockedBy("has_cobalt", has(ModItems.BILLET_COBALT.get()))
                .save(writer, MODID + ":cobalt_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.COBALT_LEGGINGS.get(), 1)
                .pattern("ECE")
                .pattern("E E")
                .define('E', ModItems.BILLET_COBALT.get())
                .define('C', ModArmorItems.STEEL_LEGGINGS.get())
                .unlockedBy("has_cobalt", has(ModItems.BILLET_COBALT.get()))
                .save(writer, MODID + ":cobalt_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.COBALT_BOOTS.get(), 1)
                .pattern("ECE")
                .define('E', ModItems.BILLET_COBALT.get())
                .define('C', ModArmorItems.STEEL_BOOTS.get())
                .unlockedBy("has_cobalt", has(ModItems.BILLET_COBALT.get()))
                .save(writer, MODID + ":cobalt_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SECURITY_HELMET.get(), 1)
                .pattern("SSS")
                .pattern("IGI")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(writer, MODID + ":security_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SECURITY_CHESTPLATE.get(), 1)
                .pattern("KWK")
                .pattern("IKI")
                .pattern("WKW")
                .define('K', ModItems.PLATE_KEVLAR.get())
                .define('I', ModItemTags.ANY_PLASTIC_INGOT)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_kevlar", has(ModItems.PLATE_KEVLAR.get()))
                .save(writer, MODID + ":security_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SECURITY_LEGGINGS.get(), 1)
                .pattern("IWI")
                .pattern("K K")
                .pattern("W W")
                .define('K', ModItems.PLATE_KEVLAR.get())
                .define('I', ModItemTags.ANY_PLASTIC_INGOT)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_kevlar", has(ModItems.PLATE_KEVLAR.get()))
                .save(writer, MODID + ":security_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SECURITY_BOOTS.get(), 1)
                .pattern("P P")
                .pattern("I I")
                .define('P', ModItems.PLATE_STEEL.get())
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(writer, MODID + ":security_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ZIRCONIUM_LEGGINGS.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.INGOT_ZIRCONIUM.get())
                .unlockedBy("has_zirconium", has(ModItems.INGOT_ZIRCONIUM.get()))
                .save(writer, MODID + ":zirconium_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.T51_HELMET.get(), 1)
                .pattern("PPC")
                .pattern("PBP")
                .pattern("IXI")
                .define('P', ModItems.PLATE_ARMOR_TITANIUM.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('X', ModArmorItems.GAS_MASK_M65.get())
                .define('B', ModArmorItems.TITANIUM_HELMET.get())
                .unlockedBy("has_titanium_plate", has(ModItems.PLATE_ARMOR_TITANIUM.get()))
                .save(writer, MODID + ":t51_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.T51_CHESTPLATE.get(), 1)
                .pattern("MPM")
                .pattern("TBT")
                .pattern("PPP")
                .define('M', ModItems.MOTOR.get())
                .define('P', ModItems.PLATE_ARMOR_TITANIUM.get())
                .define('T', Ingredient.of(ItemGasTank.createEmpty()))
                .define('B', ModArmorItems.TITANIUM_CHESTPLATE.get())
                .unlockedBy("has_titanium_plate", has(ModItems.PLATE_ARMOR_TITANIUM.get()))
                .save(writer, MODID + ":t51_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.T51_LEGGINGS.get(), 1)
                .pattern("MPM")
                .pattern("PBP")
                .pattern("P P")
                .define('M', ModItems.MOTOR.get())
                .define('P', ModItems.PLATE_ARMOR_TITANIUM.get())
                .define('B', ModArmorItems.TITANIUM_LEGGINGS.get())
                .unlockedBy("has_titanium_plate", has(ModItems.PLATE_ARMOR_TITANIUM.get()))
                .save(writer, MODID + ":t51_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.T51_BOOTS.get(), 1)
                .pattern("P P")
                .pattern("PBP")
                .define('P', ModItems.PLATE_ARMOR_TITANIUM.get())
                .define('B', ModArmorItems.TITANIUM_BOOTS.get())
                .unlockedBy("has_titanium_plate", has(ModItems.PLATE_ARMOR_TITANIUM.get()))
                .save(writer, MODID + ":t51_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.AJR_HELMET.get(), 1)
                .pattern("PPC")
                .pattern("PBP")
                .pattern("IXI")
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('I', ModItemTags.ANY_PLASTIC_INGOT)
                .define('X', ModArmorItems.GAS_MASK_M65.get())
                .define('B', ModArmorItems.ADVANCED_ALLOY_HELMET.get())
                .unlockedBy("has_ajr_plate", has(ModItems.PLATE_ARMOR_AJR.get()))
                .save(writer, MODID + ":ajr_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.AJR_CHESTPLATE.get(), 1)
                .pattern("MPM")
                .pattern("TBT")
                .pattern("PPP")
                .define('M', ModItems.MOTOR_DESH.get())
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .define('T', Ingredient.of(ItemGasTank.createEmpty()))
                .define('B', ModArmorItems.ADVANCED_ALLOY_CHESTPLATE.get())
                .unlockedBy("has_ajr_plate", has(ModItems.PLATE_ARMOR_AJR.get()))
                .save(writer, MODID + ":ajr_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.AJR_LEGGINGS.get(), 1)
                .pattern("MPM")
                .pattern("PBP")
                .pattern("P P")
                .define('M', ModItems.MOTOR_DESH.get())
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .define('B', ModArmorItems.ADVANCED_ALLOY_LEGGINGS.get())
                .unlockedBy("has_ajr_plate", has(ModItems.PLATE_ARMOR_AJR.get()))
                .save(writer, MODID + ":ajr_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.AJR_BOOTS.get(), 1)
                .pattern("P P")
                .pattern("PBP")
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .define('B', ModArmorItems.ADVANCED_ALLOY_BOOTS.get())
                .unlockedBy("has_ajr_plate", has(ModItems.PLATE_ARMOR_AJR.get()))
                .save(writer, MODID + ":ajr_boots");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModArmorItems.AJRO_HELMET.get(), 1)
                .requires(ModArmorItems.AJR_HELMET.get())
                .requires(Items.RED_DYE)
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_ajr_helmet", has(ModArmorItems.AJR_HELMET.get()))
                .save(writer, MODID + ":ajro_helmet");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModArmorItems.AJRO_CHESTPLATE.get(), 1)
                .requires(ModArmorItems.AJR_CHESTPLATE.get())
                .requires(Items.RED_DYE)
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_ajr_chestplate", has(ModArmorItems.AJR_CHESTPLATE.get()))
                .save(writer, MODID + ":ajro_chestplate");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModArmorItems.AJRO_LEGGINGS.get(), 1)
                .requires(ModArmorItems.AJR_LEGGINGS.get())
                .requires(Items.RED_DYE)
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_ajr_leggings", has(ModArmorItems.AJR_LEGGINGS.get()))
                .save(writer, MODID + ":ajro_leggings");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModArmorItems.AJRO_BOOTS.get(), 1)
                .requires(ModArmorItems.AJR_BOOTS.get())
                .requires(Items.RED_DYE)
                .requires(Items.BLACK_DYE)
                .unlockedBy("has_ajr_boots", has(ModArmorItems.AJR_BOOTS.get()))
                .save(writer, MODID + ":ajro_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BJ_HELMET.get(), 1)
                .pattern("SBS")
                .pattern(" C ")
                .pattern(" I ")
                .define('S', Items.STRING)
                .define('B', Blocks.BLACK_WOOL)
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('I', ModItems.INGOT_STARMETAL.get())
                .unlockedBy("has_string", has(Items.STRING))
                .save(writer, MODID + ":bj_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BJ_CHESTPLATE.get(), 1)
                .pattern("N N")
                .pattern("MSM")
                .pattern("NCN")
                .define('N', ModItems.PLATE_ARMOR_LUNAR.get())
                .define('M', ModItems.MOTOR_DESH.get())
                .define('S', ModArmorItems.STARMETAL_CHESTPLATE.get())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_lunar_plate", has(ModItems.PLATE_ARMOR_LUNAR.get()))
                .save(writer, MODID + ":bj_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BJ_CHESTPLATE_JETPACK.get(), 1)
                .pattern("NFN")
                .pattern("TPT")
                .pattern("ICI")
                .define('N', ModItems.PLATE_ARMOR_LUNAR.get())
                .define('F', ModItems.FINS_QUAD_TITANIUM.get())
                .define('T', Ingredient.of(ItemFluidTank.createForFluid(Fluids.XENON.get())))
                .define('P', ModArmorItems.BJ_CHESTPLATE.get())
                .define('I', ModItems.MP_THRUSTER_10_XENON.get())
                .define('C', ModItems.CRYSTAL_PHOSPHORUS.get())
                .unlockedBy("has_lunar_plate", has(ModItems.PLATE_ARMOR_LUNAR.get()))
                .save(writer, MODID + ":bj_chestplate_jetpack");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BJ_LEGGINGS.get(), 1)
                .pattern("MBM")
                .pattern("NSN")
                .pattern("N N")
                .define('N', ModItems.PLATE_ARMOR_LUNAR.get())
                .define('M', ModItems.MOTOR_DESH.get())
                .define('S', ModArmorItems.STARMETAL_LEGGINGS.get())
                .define('B', ModBlocks.BLOCK_STARMETAL.get().asItem())
                .unlockedBy("has_lunar_plate", has(ModItems.PLATE_ARMOR_LUNAR.get()))
                .save(writer, MODID + ":bj_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BJ_BOOTS.get(), 1)
                .pattern("N N")
                .pattern("BSB")
                .define('N', ModItems.PLATE_ARMOR_LUNAR.get())
                .define('S', ModArmorItems.STARMETAL_BOOTS.get())
                .define('B', ModBlocks.BLOCK_STARMETAL.get().asItem())
                .unlockedBy("has_lunar_plate", has(ModItems.PLATE_ARMOR_LUNAR.get()))
                .save(writer, MODID + ":bj_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HEV_HELMET.get(), 1)
                .pattern("PPC")
                .pattern("PBP")
                .pattern("IFI")
                .define('P', ModItems.PLATE_ARMOR_HEV.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('B', ModArmorItems.TITANIUM_HELMET.get())
                .define('I', ModItemTags.ANY_PLASTIC_INGOT)
                .define('F', ModItems.GAS_MASK_FILTER.get())
                .unlockedBy("has_hev_plate", has(ModItems.PLATE_ARMOR_HEV.get()))
                .save(writer, MODID + ":hev_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HEV_CHESTPLATE.get(), 1)
                .pattern("MPM")
                .pattern("IBI")
                .pattern("PPP")
                .define('P', ModItems.PLATE_ARMOR_HEV.get())
                .define('B', ModArmorItems.TITANIUM_CHESTPLATE.get())
                .define('I', ModItemTags.ANY_PLASTIC_INGOT)
                .define('M', ModItems.MOTOR_DESH.get())
                .unlockedBy("has_hev_plate", has(ModItems.PLATE_ARMOR_HEV.get()))
                .save(writer, MODID + ":hev_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HEV_LEGGINGS.get(), 1)
                .pattern("MPM")
                .pattern("IBI")
                .pattern("P P")
                .define('P', ModItems.PLATE_ARMOR_HEV.get())
                .define('B', ModArmorItems.TITANIUM_LEGGINGS.get())
                .define('I', ModItemTags.ANY_PLASTIC_INGOT)
                .define('M', ModItems.MOTOR_DESH.get())
                .unlockedBy("has_hev_plate", has(ModItems.PLATE_ARMOR_HEV.get()))
                .save(writer, MODID + ":hev_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HEV_BOOTS.get(), 1)
                .pattern("P P")
                .pattern("PBP")
                .define('P', ModItems.PLATE_ARMOR_HEV.get())
                .define('B', ModArmorItems.TITANIUM_BOOTS.get())
                .unlockedBy("has_hev_plate", has(ModItems.PLATE_ARMOR_HEV.get()))
                .save(writer, MODID + ":hev_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.FAU_HELMET.get(), 1)
                .pattern("PWP")
                .pattern("PBP")
                .pattern("FSF")
                .define('P', ModItems.PLATE_ARMOR_FAU.get())
                .define('W', Blocks.RED_WOOL)
                .define('B', ModArmorItems.STARMETAL_HELMET.get())
                .define('F', ModItems.GAS_MASK_FILTER.get())
                .define('S', ModItems.PIPE_STEEL.get())
                .unlockedBy("has_fau_plate", has(ModItems.PLATE_ARMOR_FAU.get()))
                .save(writer, MODID + ":fau_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.FAU_CHESTPLATE.get(), 1)
                .pattern("MCM")
                .pattern("PBP")
                .pattern("PSP")
                .define('M', ModItems.MOTOR_DESH.get())
                .define('C', ModItems.DEMON_CORE_CLOSED.get())
                .define('P', ModItems.PLATE_ARMOR_FAU.get())
                .define('B', ModArmorItems.STARMETAL_CHESTPLATE.get())
                .define('S', ModBlocks.ANCIENT_SCRAP.get().asItem())
                .unlockedBy("has_fau_plate", has(ModItems.PLATE_ARMOR_FAU.get()))
                .save(writer, MODID + ":fau_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.FAU_LEGGINGS.get(), 1)
                .pattern("MPM")
                .pattern("PBP")
                .pattern("PDP")
                .define('M', ModItems.MOTOR_DESH.get())
                .define('P', ModItems.PLATE_ARMOR_FAU.get())
                .define('B', ModArmorItems.STARMETAL_LEGGINGS.get())
                .define('D', ModItems.BILLET_POLONIUM.get())
                .unlockedBy("has_fau_plate", has(ModItems.PLATE_ARMOR_FAU.get()))
                .save(writer, MODID + ":fau_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.FAU_BOOTS.get(), 1)
                .pattern("PDP")
                .pattern("PBP")
                .define('P', ModItems.PLATE_ARMOR_FAU.get())
                .define('D', ModItems.BILLET_POLONIUM.get())
                .define('B', ModArmorItems.STARMETAL_BOOTS.get())
                .unlockedBy("has_fau_plate", has(ModItems.PLATE_ARMOR_FAU.get()))
                .save(writer, MODID + ":fau_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DNT_HELMET.get(), 1)
                .pattern("PCP")
                .pattern("PBP")
                .pattern("PSP")
                .define('P', ModItems.PLATE_ARMOR_DNT.get())
                .define('S', ModItems.INGOT_CHAINSTEEL.get())
                .define('B', ModArmorItems.BJ_HELMET.get())
                .define('C', ModItems.CIRCUIT_QUANTUM.get())
                .unlockedBy("has_dnt_plate", has(ModItems.PLATE_ARMOR_DNT.get()))
                .save(writer, MODID + ":dns_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DNT_CHESTPLATE.get(), 1)
                .pattern("PCP")
                .pattern("PBP")
                .pattern("PSP")
                .define('P', ModItems.PLATE_ARMOR_DNT.get())
                .define('S', ModItems.INGOT_CHAINSTEEL.get())
                .define('B', ModArmorItems.BJ_CHESTPLATE_JETPACK.get())
                .define('C', ModItems.SINGULARITY_SPARK.get())
                .unlockedBy("has_dnt_plate", has(ModItems.PLATE_ARMOR_DNT.get()))
                .save(writer, MODID + ":dns_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DNT_LEGGINGS.get(), 1)
                .pattern("PCP")
                .pattern("PBP")
                .pattern("PSP")
                .define('P', ModItems.PLATE_ARMOR_DNT.get())
                .define('S', ModItems.INGOT_CHAINSTEEL.get())
                .define('B', ModArmorItems.BJ_LEGGINGS.get())
                .define('C', ModItems.COIN_WORM.get())
                .unlockedBy("has_dnt_plate", has(ModItems.PLATE_ARMOR_DNT.get()))
                .save(writer, MODID + ":dns_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DNT_BOOTS.get(), 1)
                .pattern("PCP")
                .pattern("PBP")
                .pattern("PSP")
                .define('P', ModItems.PLATE_ARMOR_DNT.get())
                .define('S', ModItems.INGOT_CHAINSTEEL.get())
                .define('B', ModArmorItems.BJ_BOOTS.get())
                .define('C', ModItems.DEMON_CORE_CLOSED.get())
                .unlockedBy("has_dnt_plate", has(ModItems.PLATE_ARMOR_DNT.get()))
                .save(writer, MODID + ":dns_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.RPA_HELMET.get(), 1)
                .pattern("KPK")
                .pattern("PLP")
                .pattern(" F ")
                .define('L', ModItems.PARTS_LEGENDARY_T2.get())
                .define('K', ModItems.PLATE_KEVLAR.get())
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .define('F', ModItems.GAS_MASK_FILTER_COMBO.get())
                .unlockedBy("has_legendary_parts", has(ModItems.PARTS_LEGENDARY_T2.get()))
                .save(writer, MODID + ":rpa_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.RPA_CHESTPLATE.get(), 1)
                .pattern("P P")
                .pattern("MLM")
                .pattern("PKP")
                .define('L', ModItems.PARTS_LEGENDARY_T2.get())
                .define('K', ModItems.PLATE_KEVLAR.get())
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .define('M', ModItems.MOTOR_DESH.get())
                .unlockedBy("has_legendary_parts", has(ModItems.PARTS_LEGENDARY_T2.get()))
                .save(writer, MODID + ":rpa_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.RPA_LEGGINGS.get(), 1)
                .pattern("MPM")
                .pattern("KLK")
                .pattern("P P")
                .define('L', ModItems.PARTS_LEGENDARY_T2.get())
                .define('K', ModItems.PLATE_KEVLAR.get())
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .define('M', ModItems.MOTOR_DESH.get())
                .unlockedBy("has_legendary_parts", has(ModItems.PARTS_LEGENDARY_T2.get()))
                .save(writer, MODID + ":rpa_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.RPA_BOOTS.get(), 1)
                .pattern("KLK")
                .pattern("P P")
                .define('L', ModItems.PARTS_LEGENDARY_T2.get())
                .define('K', ModItems.PLATE_KEVLAR.get())
                .define('P', ModItems.PLATE_ARMOR_AJR.get())
                .unlockedBy("has_legendary_parts", has(ModItems.PARTS_LEGENDARY_T2.get()))
                .save(writer, MODID + ":rpa_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STEAMSUIT_HELMET.get(), 1)
                .pattern("DCD")
                .pattern("CXC")
                .pattern(" F ")
                .define('D', ModItems.INGOT_DESH.get())
                .define('C', ModItems.PLATE_COPPER.get())
                .define('X', ModArmorItems.STEEL_HELMET.get())
                .define('F', ModItems.GAS_MASK_FILTER.get())
                .unlockedBy("has_desh", has(ModItems.INGOT_DESH.get()))
                .save(writer, MODID + ":steamsuit_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STEAMSUIT_CHESTPLATE.get(), 1)
                .pattern("C C")
                .pattern("DXD")
                .pattern("CFC")
                .define('D', ModItems.INGOT_DESH.get())
                .define('C', ModItems.PLATE_COPPER.get())
                .define('X', ModArmorItems.STEEL_CHESTPLATE.get())
                .define('F', ModItems.TANK_STEEL.get())
                .unlockedBy("has_desh", has(ModItems.INGOT_DESH.get()))
                .save(writer, MODID + ":steamsuit_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STEAMSUIT_LEGGINGS.get(), 1)
                .pattern("CCC")
                .pattern("DXD")
                .pattern("C C")
                .define('D', ModItems.INGOT_DESH.get())
                .define('C', ModItems.PLATE_COPPER.get())
                .define('X', ModArmorItems.STEEL_LEGGINGS.get())
                .unlockedBy("has_desh", has(ModItems.INGOT_DESH.get()))
                .save(writer, MODID + ":steamsuit_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STEAMSUIT_BOOTS.get(), 1)
                .pattern("C C")
                .pattern("DXD")
                .define('D', ModItems.INGOT_DESH.get())
                .define('C', ModItems.PLATE_COPPER.get())
                .define('X', ModArmorItems.STEEL_BOOTS.get())
                .unlockedBy("has_desh", has(ModItems.INGOT_DESH.get()))
                .save(writer, MODID + ":steamsuit_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DIESELSUIT_HELMET.get(), 1)
                .pattern("W W")
                .pattern("W W")
                .pattern("SCS")
                .define('W', Blocks.RED_WOOL)
                .define('S', ModItems.INGOT_STEEL.get())
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(writer, MODID + ":dieselsuit_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DIESELSUIT_CHESTPLATE.get(), 1)
                .pattern("W W")
                .pattern("CDC")
                .pattern("SWS")
                .define('W', Blocks.RED_WOOL)
                .define('S', ModItems.INGOT_STEEL.get())
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .define('D', ModBlocks.MACHINE_DIESEL.get().asItem())
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(writer, MODID + ":dieselsuit_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DIESELSUIT_LEGGINGS.get(), 1)
                .pattern("M M")
                .pattern("S S")
                .pattern("W W")
                .define('W', Blocks.RED_WOOL)
                .define('S', ModItems.INGOT_STEEL.get())
                .define('M', ModItems.MOTOR.get())
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(writer, MODID + ":dieselsuit_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.DIESELSUIT_BOOTS.get(), 1)
                .pattern("W W")
                .pattern("S S")
                .define('W', Blocks.RED_WOOL)
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(writer, MODID + ":dieselsuit_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ENVSUIT_HELMET.get(), 1)
                .pattern("TCT")
                .pattern("TGT")
                .pattern("RRR")
                .define('T', ModItems.PLATE_TITANIUM.get())
                .define('C', ModItems.CIRCUIT_CHIP.get())
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('R', ModItems.INGOT_RUBBER.get())
                .unlockedBy("has_titanium", has(ModItems.PLATE_TITANIUM.get()))
                .save(writer, MODID + ":envsuit_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ENVSUIT_CHESTPLATE.get(), 1)
                .pattern("T T")
                .pattern("TCT")
                .pattern("RRR")
                .define('T', ModItems.PLATE_TITANIUM.get())
                .define('C', ModItems.PLATE_CAST_TITANIUM.get())
                .define('R', ModItems.INGOT_RUBBER.get())
                .unlockedBy("has_titanium", has(ModItems.PLATE_TITANIUM.get()))
                .save(writer, MODID + ":envsuit_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ENVSUIT_LEGGINGS.get(), 1)
                .pattern("TCT")
                .pattern("R R")
                .pattern("T T")
                .define('T', ModItems.PLATE_TITANIUM.get())
                .define('C', ModItems.PLATE_CAST_TITANIUM.get())
                .define('R', ModItems.INGOT_RUBBER.get())
                .unlockedBy("has_titanium", has(ModItems.PLATE_TITANIUM.get()))
                .save(writer, MODID + ":envsuit_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ENVSUIT_BOOTS.get(), 1)
                .pattern("R R")
                .pattern("T T")
                .define('T', ModItems.PLATE_TITANIUM.get())
                .define('R', ModItems.INGOT_RUBBER.get())
                .unlockedBy("has_titanium", has(ModItems.PLATE_TITANIUM.get()))
                .save(writer, MODID + ":envsuit_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BISMUTH_HELMET.get(), 1)
                .pattern("GPP")
                .pattern("P  ")
                .pattern("FPP")
                .define('G', Items.GOLD_INGOT)
                .define('P', ModItems.PLATE_BISMUTH.get())
                .define('F', ModItems.RAG.get())
                .unlockedBy("has_bismuth_plate", has(ModItems.PLATE_BISMUTH.get()))
                .save(writer, MODID + ":bismuth_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BISMUTH_CHESTPLATE.get(), 1)
                .pattern("RWR")
                .pattern("PCP")
                .pattern("SFS")
                .define('R', ModItems.CRYSTAL_RARE.get())
                .define('W', ModItems.WIRE_GOLD.get())
                .define('P', ModItems.PLATE_BISMUTH.get())
                .define('C', ModItems.LASER_CRYSTAL_BISMUTH.get())
                .define('S', ModItems.RING_STARMETAL.get())
                .define('F', ModItems.RAG.get())
                .unlockedBy("has_bismuth_plate", has(ModItems.PLATE_BISMUTH.get()))
                .save(writer, MODID + ":bismuth_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BISMUTH_LEGGINGS.get(), 1)
                .pattern("FSF")
                .pattern("   ")
                .pattern("FSF")
                .define('F', ModItems.RAG.get())
                .define('S', ModItems.RING_STARMETAL.get())
                .unlockedBy("has_bismuth_plate", has(ModItems.PLATE_BISMUTH.get()))
                .save(writer, MODID + ":bismuth_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BISMUTH_BOOTS.get(), 1)
                .pattern("W W")
                .pattern("P P")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('P', ModItems.PLATE_BISMUTH.get())
                .unlockedBy("has_bismuth_plate", has(ModItems.PLATE_BISMUTH.get()))
                .save(writer, MODID + ":bismuth_boots");

// ==================== EUPHEMIUM ARMOR ====================

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.EUPHEMIUM_HELMET.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .define('E', ModItems.PLATE_EUPHEMIUM.get())
                .unlockedBy("has_euphemium_plate", has(ModItems.PLATE_EUPHEMIUM.get()))
                .save(writer, MODID + ":euphemium_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.EUPHEMIUM_CHESTPLATE.get(), 1)
                .pattern("EWE")
                .pattern("EEE")
                .pattern("EEE")
                .define('E', ModItems.PLATE_EUPHEMIUM.get())
                .define('W', ModItems.WATCH.get())
                .unlockedBy("has_euphemium_plate", has(ModItems.PLATE_EUPHEMIUM.get()))
                .save(writer, MODID + ":euphemium_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.EUPHEMIUM_LEGGINGS.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.PLATE_EUPHEMIUM.get())
                .unlockedBy("has_euphemium_plate", has(ModItems.PLATE_EUPHEMIUM.get()))
                .save(writer, MODID + ":euphemium_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.EUPHEMIUM_BOOTS.get(), 1)
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.PLATE_EUPHEMIUM.get())
                .unlockedBy("has_euphemium_plate", has(ModItems.PLATE_EUPHEMIUM.get()))
                .save(writer, MODID + ":euphemium_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.JETPACK_FLY.get(), 1)
                .pattern("ACA")
                .pattern("TLT")
                .pattern("D D")
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('T', ModItems.TANK_STEEL.get())
                .define('L', Items.LEATHER)
                .define('D', ModItems.THRUSTER_SMALL.get())
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.JETPACK_BREAK.get(), 1)
                .pattern("ICI")
                .pattern("TJT")
                .pattern("I I")
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('T', ModItems.INGOT_DURA_STEEL.get())
                .define('J', ModArmorItems.JETPACK_FLY.get())
                .define('I', ModItems.PLATE_POLYMER.get())
                .unlockedBy("has_jetpack_fly", has(ModArmorItems.JETPACK_FLY.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.JETPACK_VECTOR.get(), 1)
                .pattern("TCT")
                .pattern("MJM")
                .pattern("B B")
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('T', ModItems.TANK_STEEL.get())
                .define('J', ModArmorItems.JETPACK_BREAK.get())
                .define('M', ModItems.MOTOR.get())
                .define('B', ModItems.BOLT_DURA_STEEL.get())
                .unlockedBy("has_jetpack_break", has(ModArmorItems.JETPACK_BREAK.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.JETPACK_BOOST.get(), 1)
                .pattern("PCP")
                .pattern("DJD")
                .pattern("PAP")
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('P', ModItems.PLATE_SATURNITE.get())
                .define('D', ModItems.INGOT_DESH.get())
                .define('J', ModArmorItems.JETPACK_VECTOR.get())
                .define('A', ModItems.PLATE_CAST_COPPER.get())
                .unlockedBy("has_jetpack_vector", has(ModArmorItems.JETPACK_VECTOR.get()))
                .save(writer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_HELMET.get(), 1)
                .pattern("EEE")
                .pattern("EIE")
                .pattern(" P ")
                .define('E', ModItems.HAZMAT_CLOTH.get())
                .define('I', ModItemTags.ANY_GLASS_PANES)
                .define('P', ModItems.PLATE_IRON.get())
                .unlockedBy("has_hazmat_cloth", has(ModItems.HAZMAT_CLOTH.get()))
                .save(writer, MODID + ":hazmat_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_CHESTPLATE.get(), 1)
                .pattern("E E")
                .pattern("EEE")
                .pattern("EEE")
                .define('E', ModItems.HAZMAT_CLOTH.get())
                .unlockedBy("has_hazmat_cloth", has(ModItems.HAZMAT_CLOTH.get()))
                .save(writer, MODID + ":hazmat_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_LEGGINGS.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.HAZMAT_CLOTH.get())
                .unlockedBy("has_hazmat_cloth", has(ModItems.HAZMAT_CLOTH.get()))
                .save(writer, MODID + ":hazmat_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_BOOTS.get(), 1)
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.HAZMAT_CLOTH.get())
                .unlockedBy("has_hazmat_cloth", has(ModItems.HAZMAT_CLOTH.get()))
                .save(writer, MODID + ":hazmat_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_HELMET_RED.get(), 1)
                .pattern("EEE")
                .pattern("IEI")
                .pattern("EFE")
                .define('E', ModItems.HAZMAT_CLOTH_RED.get())
                .define('I', ModItemTags.ANY_GLASS_PANES)
                .define('F', ModItems.PLATE_IRON.get())
                .unlockedBy("has_hazmat_cloth_red", has(ModItems.HAZMAT_CLOTH_RED.get()))
                .save(writer, MODID + ":hazmat_helmet_red");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_CHESTPLATE_RED.get(), 1)
                .pattern("E E")
                .pattern("EEE")
                .pattern("EEE")
                .define('E', ModItems.HAZMAT_CLOTH_RED.get())
                .unlockedBy("has_hazmat_cloth_red", has(ModItems.HAZMAT_CLOTH_RED.get()))
                .save(writer, MODID + ":hazmat_chestplate_red");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_LEGGINGS_RED.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.HAZMAT_CLOTH_RED.get())
                .unlockedBy("has_hazmat_cloth_red", has(ModItems.HAZMAT_CLOTH_RED.get()))
                .save(writer, MODID + ":hazmat_leggings_red");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_BOOTS_RED.get(), 1)
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.HAZMAT_CLOTH_RED.get())
                .unlockedBy("has_hazmat_cloth_red", has(ModItems.HAZMAT_CLOTH_RED.get()))
                .save(writer, MODID + ":hazmat_boots_red");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_HELMET_GREY.get(), 1)
                .pattern("EEE")
                .pattern("IEI")
                .pattern("EFE")
                .define('E', ModItems.HAZMAT_CLOTH_GREY.get())
                .define('I', ModItemTags.ANY_GLASS_PANES)
                .define('F', ModItems.PLATE_IRON.get())
                .unlockedBy("has_hazmat_cloth_grey", has(ModItems.HAZMAT_CLOTH_GREY.get()))
                .save(writer, MODID + ":hazmat_helmet_grey");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_CHESTPLATE_GREY.get(), 1)
                .pattern("E E")
                .pattern("EEE")
                .pattern("EEE")
                .define('E', ModItems.HAZMAT_CLOTH_GREY.get())
                .unlockedBy("has_hazmat_cloth_grey", has(ModItems.HAZMAT_CLOTH_GREY.get()))
                .save(writer, MODID + ":hazmat_chestplate_grey");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_LEGGINGS_GREY.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.HAZMAT_CLOTH_GREY.get())
                .unlockedBy("has_hazmat_cloth_grey", has(ModItems.HAZMAT_CLOTH_GREY.get()))
                .save(writer, MODID + ":hazmat_leggings_grey");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_BOOTS_GREY.get(), 1)
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.HAZMAT_CLOTH_GREY.get())
                .unlockedBy("has_hazmat_cloth_grey", has(ModItems.HAZMAT_CLOTH_GREY.get()))
                .save(writer, MODID + ":hazmat_boots_grey");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ASBESTOS_HELMET.get(), 1)
                .pattern("EEE")
                .pattern("EIE")
                .define('E', ModItems.ASBESTOS_CLOTH.get())
                .define('I', ModItems.PLATE_GOLD.get())
                .unlockedBy("has_asbestos_cloth", has(ModItems.ASBESTOS_CLOTH.get()))
                .save(writer, MODID + ":asbestos_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ASBESTOS_CHESTPLATE.get(), 1)
                .pattern("E E")
                .pattern("EEE")
                .pattern("EEE")
                .define('E', ModItems.ASBESTOS_CLOTH.get())
                .unlockedBy("has_asbestos_cloth", has(ModItems.ASBESTOS_CLOTH.get()))
                .save(writer, MODID + ":asbestos_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ASBESTOS_LEGGINGS.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.ASBESTOS_CLOTH.get())
                .unlockedBy("has_asbestos_cloth", has(ModItems.ASBESTOS_CLOTH.get()))
                .save(writer, MODID + ":asbestos_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ASBESTOS_BOOTS.get(), 1)
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.ASBESTOS_CLOTH.get())
                .unlockedBy("has_asbestos_cloth", has(ModItems.ASBESTOS_CLOTH.get()))
                .save(writer, MODID + ":asbestos_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_PAA_HELMET.get(), 1)
                .pattern("EEE")
                .pattern("IEI")
                .pattern(" P ")
                .define('E', ModItems.PLATE_PAA.get())
                .define('I', ModItemTags.ANY_GLASS_PANES)
                .define('P', ModItems.PLATE_IRON.get())
                .unlockedBy("has_plate_paa", has(ModItems.PLATE_PAA.get()))
                .save(writer, MODID + ":hazmat_paa_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_PAA_CHESTPLATE.get(), 1)
                .pattern("E E")
                .pattern("EEE")
                .pattern("EEE")
                .define('E', ModItems.PLATE_PAA.get())
                .unlockedBy("has_plate_paa", has(ModItems.PLATE_PAA.get()))
                .save(writer, MODID + ":hazmat_paa_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_PAA_LEGGINGS.get(), 1)
                .pattern("EEE")
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.PLATE_PAA.get())
                .unlockedBy("has_plate_paa", has(ModItems.PLATE_PAA.get()))
                .save(writer, MODID + ":hazmat_paa_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.HAZMAT_PAA_BOOTS.get(), 1)
                .pattern("E E")
                .pattern("E E")
                .define('E', ModItems.PLATE_PAA.get())
                .unlockedBy("has_plate_paa", has(ModItems.PLATE_PAA.get()))
                .save(writer, MODID + ":hazmat_paa_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.PAA_CHESTPLATE.get(), 1)
                .pattern("E E")
                .pattern("NEN")
                .pattern("ENE")
                .define('E', ModItems.PLATE_PAA.get())
                .define('N', ModItems.NEUTRON_REFLECTOR.get())
                .unlockedBy("has_plate_paa", has(ModItems.PLATE_PAA.get()))
                .save(writer, MODID + ":paa_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.PAA_LEGGINGS.get(), 1)
                .pattern("EEE")
                .pattern("N N")
                .pattern("E E")
                .define('E', ModItems.PLATE_PAA.get())
                .define('N', ModItems.NEUTRON_REFLECTOR.get())
                .unlockedBy("has_plate_paa", has(ModItems.PLATE_PAA.get()))
                .save(writer, MODID + ":paa_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.PAA_BOOTS.get(), 1)
                .pattern("E E")
                .pattern("N N")
                .define('E', ModItems.PLATE_PAA.get())
                .define('N', ModItems.NEUTRON_REFLECTOR.get())
                .unlockedBy("has_plate_paa", has(ModItems.PLATE_PAA.get()))
                .save(writer, MODID + ":paa_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.LIQUIDATOR_HELMET.get(), 1)
                .pattern("III")
                .pattern("CBC")
                .pattern("III")
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CLADDING_IRON.get())
                .define('B', ModArmorItems.HAZMAT_HELMET_GREY.get())
                .unlockedBy("has_cladding", has(ModItems.CLADDING_IRON.get()))
                .save(writer, MODID + ":liquidator_helmet");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.LIQUIDATOR_CHESTPLATE.get(), 1)
                .pattern("ICI")
                .pattern("TBT")
                .pattern("ICI")
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CLADDING_IRON.get())
                .define('B', ModArmorItems.HAZMAT_CHESTPLATE_GREY.get())
                .define('T', ModItems.GAS_TANK.get())
                .unlockedBy("has_cladding", has(ModItems.CLADDING_IRON.get()))
                .save(writer, MODID + ":liquidator_chestplate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.LIQUIDATOR_LEGGINGS.get(), 1)
                .pattern("III")
                .pattern("CBC")
                .pattern("I I")
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CLADDING_IRON.get())
                .define('B', ModArmorItems.HAZMAT_LEGGINGS_GREY.get())
                .unlockedBy("has_cladding", has(ModItems.CLADDING_IRON.get()))
                .save(writer, MODID + ":liquidator_leggings");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.LIQUIDATOR_BOOTS.get(), 1)
                .pattern("ICI")
                .pattern("IBI")
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CLADDING_IRON.get())
                .define('B', ModArmorItems.HAZMAT_BOOTS_GREY.get())
                .unlockedBy("has_cladding", has(ModItems.CLADDING_IRON.get()))
                .save(writer, MODID + ":liquidator_boots");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.GOGGLES.get(), 1)
                .pattern("P P")
                .pattern("GPG")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('P', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(writer, MODID + ":goggles");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.GAS_MASK.get(), 1)
                .pattern("PPP")
                .pattern("GPG")
                .pattern(" F ")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('P', ModItems.PLATE_STEEL.get())
                .define('F', ModItems.PLATE_IRON.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(writer, MODID + ":gas_mask");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.GAS_MASK_M65.get(), 1)
                .pattern("PPP")
                .pattern("GPG")
                .pattern(" F ")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('F', ModItems.PLATE_IRON.get())
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(writer, MODID + ":gas_mask_m65");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.GAS_MASK_OLDE.get(), 1)
                .pattern("PPP")
                .pattern("GPG")
                .pattern(" F ")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('P', Items.LEATHER)
                .define('F', Items.IRON_INGOT)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(writer, MODID + ":gas_mask_olde");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.GAS_MASK_MONO.get(), 1)
                .pattern(" P ")
                .pattern("PPP")
                .pattern(" F ")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('F', ModItems.PLATE_IRON.get())
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(writer, MODID + ":gas_mask_mono");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.MASK_OF_INFAMY.get(), 1)
                .pattern("III")
                .pattern("III")
                .pattern(" I ")
                .define('I', ModItems.PLATE_IRON.get())
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .save(writer, MODID + ":mask_of_infamy");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ASHGLASSES.get(), 1)
                .pattern("I I")
                .pattern("GPG")
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('G', ModBlocks.GLASS_ASH.get().asItem())
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(writer, MODID + ":ashglasses");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.MASK_DRY.get(), 1)
                .pattern("RRR")
                .define('R', ModItems.RAG.get())
                .unlockedBy("has_rag", has(ModItems.RAG.get()))
                .save(writer, MODID + ":mask_dry");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.CAPE_RADIATION.get(), 1)
                .pattern("W W")
                .pattern("WIW")
                .pattern("WDW")
                .define('W', Blocks.YELLOW_WOOL)
                .define('D', Items.YELLOW_DYE)
                .define('I', ModItems.NUCLEAR_WASTE.get())
                .unlockedBy("has_nuclear_waste", has(ModItems.NUCLEAR_WASTE.get()))
                .save(writer, MODID + ":cape_radiation");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.CAPE_GASMASK.get(), 1)
                .pattern("W W")
                .pattern("WIW")
                .pattern("WDW")
                .define('W', Blocks.YELLOW_WOOL)
                .define('D', Items.BLACK_DYE)
                .define('I', ModArmorItems.GAS_MASK.get())
                .unlockedBy("has_gas_mask", has(ModArmorItems.GAS_MASK.get()))
                .save(writer, MODID + ":cape_gasmask");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.CAPE_SCHRABIDIUM.get(), 1)
                .pattern("W W")
                .pattern("WIW")
                .pattern("WDW")
                .define('W', ModItems.INGOT_SCHRABIDIUM.get())
                .define('D', Items.BLACK_DYE)
                .define('I', ModItems.CIRCUIT_CHIP.get())
                .unlockedBy("has_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                .save(writer, MODID + ":cape_schrabidium");

        if(GeneralConfig.ENABLE_LBSM && GeneralConfig.ENABLE_LBSM_SIMPLE_ARMOR_RECIPES) {
            addHelmet(ModItems.INGOT_STARMETAL.get(), ModArmorItems.STARMETAL_HELMET.get());
            addChest(ModItems.INGOT_STARMETAL.get(), ModArmorItems.STARMETAL_CHESTPLATE.get());
            addLegs(ModItems.INGOT_STARMETAL.get(), ModArmorItems.STARMETAL_LEGGINGS.get());
            addBoots(ModItems.INGOT_STARMETAL.get(), ModArmorItems.STARMETAL_BOOTS.get());
            addHelmet(ModItems.INGOT_SCHRABIDIUM.get(), ModArmorItems.SCHRABIDIUM_HELMET.get());
            addChest(ModItems.INGOT_SCHRABIDIUM.get(), ModArmorItems.SCHRABIDIUM_CHESTPLATE.get());
            addLegs(ModItems.INGOT_SCHRABIDIUM.get(), ModArmorItems.SCHRABIDIUM_LEGGINGS.get());
            addBoots(ModItems.INGOT_SCHRABIDIUM.get(), ModArmorItems.SCHRABIDIUM_BOOTS.get());
        } else {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STARMETAL_HELMET.get(), 1)
                    .pattern("EEE")
                    .pattern("ECE")
                    .define('E', ModItems.INGOT_STARMETAL.get())
                    .define('C', ModArmorItems.COBALT_HELMET.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(writer, MODID + ":starmetal_helmet");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STARMETAL_CHESTPLATE.get(), 1)
                    .pattern("ECE")
                    .pattern("EEE")
                    .pattern("EEE")
                    .define('E', ModItems.INGOT_STARMETAL.get())
                    .define('C', ModArmorItems.COBALT_CHESTPLATE.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(writer, MODID + ":starmetal_chestplate");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STARMETAL_LEGGINGS.get(), 1)
                    .pattern("EEE")
                    .pattern("ECE")
                    .pattern("E E")
                    .define('E', ModItems.INGOT_STARMETAL.get())
                    .define('C', ModArmorItems.COBALT_LEGGINGS.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(writer, MODID + ":starmetal_leggings");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.STARMETAL_BOOTS.get(), 1)
                    .pattern("E E")
                    .pattern("ECE")
                    .define('E', ModItems.INGOT_STARMETAL.get())
                    .define('C', ModArmorItems.COBALT_BOOTS.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(writer, MODID + ":starmetal_boots");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SCHRABIDIUM_HELMET.get(), 1)
                    .pattern("EEE")
                    .pattern("ESE")
                    .pattern(" P ")
                    .define('E', ModItems.INGOT_SCHRABIDIUM.get())
                    .define('S', ModArmorItems.STARMETAL_HELMET.get())
                    .define('P', ModItems.PELLET_CHARGED.get())
                    .unlockedBy("has_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                    .save(writer, MODID + ":schrabidium_helmet");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SCHRABIDIUM_CHESTPLATE.get(), 1)
                    .pattern("ESE")
                    .pattern("EPE")
                    .pattern("EEE")
                    .define('E', ModItems.INGOT_SCHRABIDIUM.get())
                    .define('S', ModArmorItems.STARMETAL_CHESTPLATE.get())
                    .define('P', ModItems.PELLET_CHARGED.get())
                    .unlockedBy("has_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                    .save(writer, MODID + ":schrabidium_chestplate");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SCHRABIDIUM_LEGGINGS.get(), 1)
                    .pattern("EEE")
                    .pattern("ESE")
                    .pattern("EPE")
                    .define('E', ModItems.INGOT_SCHRABIDIUM.get())
                    .define('S', ModArmorItems.STARMETAL_LEGGINGS.get())
                    .define('P', ModItems.PELLET_CHARGED.get())
                    .unlockedBy("has_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                    .save(writer, MODID + ":schrabidium_leggings");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.SCHRABIDIUM_BOOTS.get(), 1)
                    .pattern("EPE")
                    .pattern("ESE")
                    .define('E', ModItems.INGOT_SCHRABIDIUM.get())
                    .define('S', ModArmorItems.STARMETAL_BOOTS.get())
                    .define('P', ModItems.PELLET_CHARGED.get())
                    .unlockedBy("has_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                    .save(writer, MODID + ":schrabidium_boots");
        }

    }

    public static void addHelmet(Item ingot, Item helmet) {
        addArmor(ingot, helmet, patternHelmet);
    }
    public static void addChest(Item ingot, Item chestplate) {
        addArmor(ingot, chestplate, patternChestplate);
    }
    public static void addLegs(Item ingot, Item leggings) {
        addArmor(ingot, leggings, patternLeggings);
    }
    public static void addBoots(Item ingot, Item boots) {
        addArmor(ingot, boots, patternBoots);
    }

    private static void addArmor(Item material, Item armor, String[] pattern) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, armor, 1)
                .pattern(pattern[0])
                .pattern(pattern[1])
                .pattern(pattern.length > 2 ? pattern[2] : "   ")
                .define('X', material)
                .unlockedBy("has_material", has(material))
                .save(writer, MODID + ":" + armor.getDescriptionId().replace("item.hbm.", ""));
    }




}
