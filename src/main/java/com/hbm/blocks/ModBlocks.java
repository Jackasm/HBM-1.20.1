package com.hbm.blocks;

import com.hbm.blocks.bomb.*;
import com.hbm.blocks.conduit.ConduitBlock;
import com.hbm.blocks.deco.*;
import com.hbm.blocks.fluid.*;
import com.hbm.blocks.fluid.MudBlock;
import com.hbm.blocks.gas.*;
import com.hbm.blocks.generic.*;
import com.hbm.blocks.generic.BlockDynamicSlag;
import com.hbm.blocks.generic.DecoBlock;
import com.hbm.blocks.machine.*;
import com.hbm.blocks.machine.BlastFurnaceBlock;
import com.hbm.blocks.machine.pile.*;
import com.hbm.blocks.machine.rbmk.RBMKDebris;
import com.hbm.blocks.network.*;
import com.hbm.blocks.turret.TurretSentry;
import com.hbm.blocks.turret.TurretSentryDamaged;
import com.hbm.datagen.LootInfo;
import com.hbm.datagen.ToolInfo;
import com.hbm.datagen.models.BlockModel;
import com.hbm.entity.logic.EntityC130;
import com.hbm.items.ModItems;
import com.hbm.util.HBMEnums;
import com.hbm.util.RefStrings;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.hbm.util.ResLocation.ResLocation;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RefStrings.MODID);

    public static final Map<RegistryObject<Block>, BlockModel> BLOCK_MODELS = new LinkedHashMap<>();
    public static final Map<RegistryObject<Block>, Object[]> MODEL_DATA = new LinkedHashMap<>();
    public static final Map<RegistryObject<Block>, LootInfo> BLOCK_LOOT = new LinkedHashMap<>();
    public static final Map<RegistryObject<Block>, ToolInfo> BLOCK_TOOL = new LinkedHashMap<>();

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier,
                                                      BlockModel blockModel, LootInfo loot, ToolInfo tool,
                                                      Object... modelData) {
        RegistryObject<Block> block = registerBlock(name, supplier, blockModel, modelData);
        BLOCK_LOOT.put(block, loot);
        BLOCK_TOOL.put(block, tool);
        return block;
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier,
                                                      BlockModel blockModel, ToolInfo tool,
                                                      Object... modelData) {
        RegistryObject<Block> block = registerBlock(name, supplier, blockModel, modelData);
        BLOCK_TOOL.put(block, tool);
        return block;
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier, BlockModel type, Object... data) {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        BLOCK_MODELS.put(block, type);
        if (data.length > 0) {
            MODEL_DATA.put(block, data);
        }
        return block;
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier,
                                                      BlockModel type, LootInfo loot, ToolInfo tool,
                                                      String texturePrefix, Class<? extends Enum<?>> enumClass) {
        return registerBlock(name, supplier, type, loot, tool, texturePrefix, (Object) enumClass);
    }

    // Регистрация блоков
    public static final RegistryObject<Block> ORE_ALUMINIUM = registerBlock("ore_aluminium",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_ALUMINIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_aluminium");

    public static final RegistryObject<Block> ORE_ALUMINIUM_DEEPSLATE = registerBlock("ore_aluminium_deepslate",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_ALUMINIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_aluminium", "deepslate");

    public static final RegistryObject<Block> ORE_TITANIUM = registerBlock("ore_titanium",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_TITANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_titanium");

    public static final RegistryObject<Block> ORE_TITANIUM_DEEPSLATE = registerBlock("ore_titanium_deepslate",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_TITANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_titanium", "deepslate");

    public static final RegistryObject<Block> ORE_SULFUR = registerBlock("ore_sulfur",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.SULFUR, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_sulfur");

    public static final RegistryObject<Block> ORE_SULFUR_DEEPSLATE = registerBlock("ore_sulfur_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.SULFUR, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_sulfur", "deepslate");

    public static final RegistryObject<Block> ORE_ASBESTOS = registerBlock("ore_asbestos",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 5F, 15F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(ModItems.POWDER_ASBESTOS, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_asbestos");

    public static final RegistryObject<Block> ORE_ASBESTOS_DEEPSLATE = registerBlock("ore_asbestos_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.POWDER_ASBESTOS, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_asbestos", "deepslate");

    public static final RegistryObject<Block> ORE_GNEISS_ASBESTOS = registerBlock("ore_gneiss_asbestos",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(ModItems.POWDER_ASBESTOS, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_gneiss_asbestos");

    public static final RegistryObject<Block> ORE_METEOR_IRON = registerBlock("ore_meteor_iron",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(() -> Items.RAW_IRON, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_meteor_iron");

    public static final RegistryObject<Block> ORE_METEOR_COPPER = registerBlock("ore_meteor_copper",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(() -> Items.RAW_COPPER, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_meteor_copper");

    public static final RegistryObject<Block> ORE_METEOR_ALUMINIUM = registerBlock("ore_meteor_aluminium",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_ALUMINIUM, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_meteor_aluminium");

    public static final RegistryObject<Block> ORE_METEOR_RARE_EARTH = registerBlock("ore_meteor_rare_earth",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(ModItems.RARE_EARTH_CHUNK, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_meteor_rare_earth");

    public static final RegistryObject<Block> ORE_METEOR_COBALT = registerBlock("ore_meteor_cobalt",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_COBALT, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_meteor_cobalt");

    public static final RegistryObject<Block> ORE_TIKITE = registerBlock("ore_tikite",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.ORE_TIKITE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_tikite");

    public static final RegistryObject<Block> ORE_AUSTRALIUM = registerBlock("ore_australium",
            () -> new BlockGeneric(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()),
            BlockModel.ORE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_australium");

    public static final RegistryObject<Block> ANCIENT_SCRAP = registerBlock("ancient_scrap",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.RAW_IRON, 100F, 6000.0F, true), 1, true, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ancient_scrap");

    public static final RegistryObject<Block> BLOCK_CORIUM_COBBLE = registerBlock("block_corium_cobble",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.RAW_IRON, 100F, 6000.0F, true), 1, true, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_corium_cobble");

    public static final RegistryObject<Block> BLOCK_ASBESTOS = registerBlock("block_asbestos",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.SNOW, 5F, 15.0F, true), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_asbestos");

    public static final RegistryObject<Block> BLOCK_LITHIUM = registerBlock("block_lithium",
            BlockLithium::new,
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_lithium");

    public static final RegistryObject<Block> DECO_ASBESTOS = registerBlock("deco/deco_asbestos",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.SNOW, 5F, 10.0F, true), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco_asbestos");

    public static final RegistryObject<Block> BRICK_ASBESTOS = registerBlock("brick_asbestos",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.SNOW, 5F, 1000.0F, true), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "brick_asbestos");

    public static final RegistryObject<Block> TILE_LAB = registerBlock("tile_lab",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1F, 20.0F, false), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "tile_lab");

    public static final RegistryObject<Block> TILE_LAB_CRACKED = registerBlock("tile_lab_cracked",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1F, 20.0F, false), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "tile_lab_cracked");

    public static final RegistryObject<Block> TILE_LAB_BROKEN = registerBlock("tile_lab_broken",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1F, 20.0F, true), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "tile_lab_broken");

    public static final RegistryObject<Block> ORE_COAL_OIL_BURNING = registerBlock("ore_coal_oil_burning",
            () -> new BlockCoalBurning(BlockOutgas.createProperties(MapColor.STONE, 5F, 15.0F)
                    .lightLevel(state -> 10)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_coal_oil_burning");

    public static final RegistryObject<Block> ORE_NETHER_COAL = registerBlock("ore_nether_coal",
            () -> new BlockNetherCoal(BlockOutgas.createProperties(MapColor.NETHER, 0.4F, 10.0F, false).lightLevel(state -> 10), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.COAL_INFERNAL, 1, 3),  // адский уголь
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_nether_coal");

    public static final RegistryObject<Block> ORE_COLTAN = registerBlock("ore_coltan",
            () -> new BlockOre(BlockOre.createOreProperties(MapColor.STONE, 15.0F, 10.0F).sound(SoundType.STONE), 0.0F),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_coltan");

    public static final RegistryObject<Block> BRICK_JUNGLE_OOZE = registerBlock("brick_jungle_ooze",
            () -> new BlockOre(BlockOre.createOreProperties(MapColor.STONE, 15.0F, 10.0F).sound(SoundType.STONE), 0.0F),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_coltan");

    public static final RegistryObject<Block> BRICK_JUNGLE_MYSTIC = registerBlock("brick_jungle_mystic",
            () -> new BlockOre(BlockOre.createOreProperties(MapColor.STONE, 15.0F, 10.0F).sound(SoundType.STONE), 0.0F),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_coltan");

    public static final RegistryObject<Block> ORE_NETHER_COBALT = registerBlock("ore_nether_cobalt",
            () -> new BlockOre(BlockOre.createOreProperties(MapColor.NETHER, 0.4F, 10.0F).sound(SoundType.NETHER_GOLD_ORE), 0.0F),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.RAW_COBALT, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),  // кобальт требует алмазную кирку
            "ore_nether_cobalt");

    public static final RegistryObject<Block> BLOCK_METEOR_MOLTEN = registerBlock("block_meteor_molten",
            () -> new BlockOre(BlockOre.createOreProperties(MapColor.COLOR_GRAY, 15.0F, 360.0F).sound(SoundType.STONE).lightLevel(state -> 12).randomTicks(), 0.0F).noFortune(),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_meteor_molten");

    public static final RegistryObject<Block> ORE_FLUORITE = registerBlock("ore_fluorite",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.FLUORITE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_fluorite", "stone");

    public static final RegistryObject<Block> ORE_FLUORITE_DEEPSLATE = registerBlock("ore_fluorite_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.FLUORITE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_fluorite", "deepslate");

    public static final RegistryObject<Block> ORE_NITER = registerBlock("ore_niter",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.NITER, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_niter", "stone");

    public static final RegistryObject<Block> ORE_NITER_DEEPSLATE = registerBlock("ore_niter_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.NITER, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_niter", "deepslate");

    public static final RegistryObject<Block> ORE_TUNGSTEN = registerBlock("ore_tungsten",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_TUNGSTEN, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_tungsten", "stone");

    public static final RegistryObject<Block> ORE_TUNGSTEN_DEEPSLATE = registerBlock("ore_tungsten_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_TUNGSTEN, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_tungsten", "deepslate");

    public static final RegistryObject<Block> ORE_LEAD = registerBlock("ore_lead",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_LEAD, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_lead", "stone");

    public static final RegistryObject<Block> ORE_LEAD_DEEPSLATE = registerBlock("ore_lead_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_LEAD, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_lead", "deepslate");

    public static final RegistryObject<Block> ORE_LIGNITE = registerBlock("ore_lignite",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.LIGNITE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_lignite", "stone");

    public static final RegistryObject<Block> ORE_LIGNITE_DEEPSLATE = registerBlock("ore_lignite_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.LIGNITE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_lignite", "deepslate");

    public static final RegistryObject<Block> ORE_BERYLLIUM = registerBlock("ore_beryllium",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_BERYLLIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_beryllium", "stone");

    public static final RegistryObject<Block> ORE_BERYLLIUM_DEEPSLATE = registerBlock("ore_beryllium_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_BERYLLIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_beryllium", "deepslate");

    public static final RegistryObject<Block> ORE_RARE = registerBlock("ore_rare",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RARE_EARTH_CHUNK, 1, 2),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_rare", "stone");

    public static final RegistryObject<Block> ORE_RARE_DEEPSLATE = registerBlock("ore_rare_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RARE_EARTH_CHUNK, 1, 2),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_rare", "deepslate");

    public static final RegistryObject<Block> ORE_ZINC = registerBlock("ore_zinc",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_ZINC, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_zinc", "stone");

    public static final RegistryObject<Block> ORE_ZINC_DEEPSLATE = registerBlock("ore_zinc_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_ZINC, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_zinc", "deepslate");

    public static final RegistryObject<Block> ORE_CINNABAR = registerBlock("ore_cinnabar",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.CINNABAR, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_cinnabar", "stone");

    public static final RegistryObject<Block> ORE_CINNABAR_DEEPSLATE = registerBlock("ore_cinnabar_deepslate",
            () -> new Block(ModBlockProperties.DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.CINNABAR, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_cinnabar", "deepslate");

    public static final RegistryObject<Block> ORE_COBALT = registerBlock("ore_cobalt",
            () -> new Block(ModBlockProperties.HARD_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_COBALT, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_cobalt", "stone");

    public static final RegistryObject<Block> ORE_COBALT_DEEPSLATE = registerBlock("ore_cobalt_deepslate",
            () -> new Block(ModBlockProperties.HARD_DEEPSLATE_ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_COBALT, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_cobalt", "deepslate");

    public static final RegistryObject<Block> ORE_URANIUM = registerBlock("ore_uranium",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.itemSilk(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_uranium", "stone");

    public static final RegistryObject<Block> ORE_URANIUM_DEEPSLATE = registerBlock("ore_uranium_deepslate",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 5F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.itemSilk(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_uranium", "deepslate");

    public static final RegistryObject<Block> ORE_NETHER_PLUTONIUM = registerBlock("ore_plutonium",
            () -> new BlockOre(BlockOre.createProperties(MapColor.NETHER, 0.4F, 10F)),
            BlockModel.ORE,
            LootInfo.itemSilk(ModItems.RAW_PLUTONIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_plutonium", "netherrack");

    public static final RegistryObject<Block> ORE_NETHER_TUNGSTEN = registerBlock("ore_nether_tungsten",
            () -> new BlockOre(BlockOre.createProperties(MapColor.NETHER, 0.4F, 10F)),
            BlockModel.ORE,
            LootInfo.itemSilk(ModItems.RAW_TUNGSTEN, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_tungsten", "netherrack");

    public static final RegistryObject<Block> ORE_NETHER_SMOLDERING = registerBlock("ore_nether_smoldering",
            () -> new BlockSmolder(BlockSmolder.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.POWDER_FIRE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_nether_smoldering");

    public static final RegistryObject<Block> ORE_THORIUM = registerBlock("ore_thorium",
            () -> new BlockOre(BlockOre.createProperties(MapColor.STONE, 5.0F, 10F)),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_THORIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_thorium", "stone");

    public static final RegistryObject<Block> ORE_THORIUM_DEEPSLATE = registerBlock("ore_thorium_deepslate",
            () -> new BlockOre(BlockOre.createProperties(MapColor.STONE, 5.0F, 10F)),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_THORIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_thorium", "deepslate");

    public static final RegistryObject<Block> STONE_RESOURCE_LIMESTONE = registerBlock("stone_resource_limestone",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "stone_resource_limestone");

    public static final RegistryObject<Block> STONE_GNEISS = registerBlock("stone_gneiss",
            () -> new BlockGeneric(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5F, 10.0F)
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.WOOD));

    public static final RegistryObject<Block> WASTE_EARTH = registerBlock("waste_earth",
            () -> new WasteEarth(WasteEarth.wasteEarthProps()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "waste_earth_side", "waste_earth_top", "waste_earth_bottom");

    public static final RegistryObject<Block> WASTE_MYCELIUM = registerBlock("waste_mycelium",
            () -> new WasteEarth(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "waste_mycelium_side", "waste_mycelium_top", "waste_mycelium_bottom");

    public static final RegistryObject<Block> FROZEN_GRASS = registerBlock("frozen_grass",
            () -> new WasteEarth(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "frozen_grass_side", "frozen_grass_top", "frozen_grass_bottom");

    public static final RegistryObject<Block> BURNING_EARTH = registerBlock("burning_earth",
            () -> new WasteEarth(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "burning_grass_side", "burning_grass_top", "burning_grass_bottom");

    public static final RegistryObject<Block> IMPACT_DIRT = registerBlock("impact_dirt",
            () -> new BlockDirt(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5F)
                    .sound(SoundType.GRAVEL)
                    .randomTicks()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "impact_dirt");

    public static final RegistryObject<Block> MUSH = registerBlock("mush",
            () -> new BlockMush(BlockMush.createProperties()),
            BlockModel.CROSS,
            LootInfo.self(),
            ToolInfo.none(),
            "mush");

    public static final RegistryObject<Block> WASTE_LEAVES = registerBlock("waste_leaves",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.none(),
            "waste_leaves");

    public static final RegistryObject<Block> SAND_DIRTY = registerBlock("sand_dirty",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5F)
                    .sound(SoundType.SAND)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "sand_dirty");

    public static final RegistryObject<Block> SAND_DIRTY_RED = registerBlock("sand_dirty_red",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5F)
                    .sound(SoundType.SAND)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "sand_dirty_red");

    public static final RegistryObject<Block> ANVIL_IRON = registerBlock("machines/anvil_iron",
            () -> new NTMAnvil(NTMAnvil.TIER_IRON),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_LEAD = registerBlock("machines/anvil_lead",
            () -> new NTMAnvil(NTMAnvil.TIER_IRON),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_STEEL = registerBlock("machines/anvil_steel",
            () -> new NTMAnvil(NTMAnvil.TIER_STEEL),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_DESH = registerBlock("machines/anvil_desh",
            () -> new NTMAnvil(NTMAnvil.TIER_OIL),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_SATURNITE = registerBlock("machines/anvil_saturnite",
            () -> new NTMAnvil(NTMAnvil.TIER_NUCLEAR),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_FERROURANIUM = registerBlock("machines/anvil_ferrouranium",
            () -> new NTMAnvil(NTMAnvil.TIER_RBMK),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_BISMUTH_BRONZE = registerBlock("machines/anvil_bismuth_bronze",
            () -> new NTMAnvil(NTMAnvil.TIER_RBMK),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_ARSENIC_BRONZE = registerBlock("machines/anvil_arsenic_bronze",
            () -> new NTMAnvil(NTMAnvil.TIER_RBMK),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_SCHRABIDATE = registerBlock("machines/anvil_schrabidate",
            () -> new NTMAnvil(NTMAnvil.TIER_FUSION),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_DNT = registerBlock("machines/anvil_dnt",
            () -> new NTMAnvil(NTMAnvil.TIER_PARTICLE),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_OSMIRIDIUM = registerBlock("machines/anvil_osmiridium",
            () -> new NTMAnvil(NTMAnvil.TIER_GERALD),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> ANVIL_MURKY = registerBlock("machines/anvil_murky",
            () -> new NTMAnvil(1916169),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/anvil");

    public static final RegistryObject<Block> DECO_TITANIUM = registerBlock("deco/deco_titanium",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_titanium");

    public static final RegistryObject<Block> DECO_RED_COPPER = registerBlock("deco/deco_red_copper",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_red_copper");

    public static final RegistryObject<Block> DECO_TUNGSTEN = registerBlock("deco/deco_tungsten",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_tungsten");

    public static final RegistryObject<Block> DECO_ALUMINIUM = registerBlock("deco/deco_aluminium",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_aluminium");

    public static final RegistryObject<Block> DECO_STEEL = registerBlock("deco/deco_steel",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_steel");

    public static final RegistryObject<Block> DECO_LEAD = registerBlock("deco/deco_lead",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_lead");

    public static final RegistryObject<Block> DECO_BERYLLIUM = registerBlock("deco/deco_beryllium",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_beryllium");

    public static final RegistryObject<Block> DECO_STAINLESS = registerBlock("deco/deco_stainless",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_stainless");

    public static final RegistryObject<Block> DECO_RUSTY_STEEL = registerBlock("deco/deco_rusty_steel",
            BlockDecoCT::new,
            BlockModel.DECO_CT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/deco_rusty_steel");

    public static final RegistryObject<Block> BLOCK_STEEL = registerBlock("block_steel",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_steel");

    public static final RegistryObject<Block> BLOCK_DESH = registerBlock("block_desh",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_desh");

    public static final RegistryObject<Block> BLOCK_ALUMINIUM = registerBlock("block_aluminium",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_aluminium");

    public static final RegistryObject<Block> BLOCK_TITANIUM = registerBlock("block_titanium",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_titanium");

    public static final RegistryObject<Block> BLOCK_TUNGSTEN = registerBlock("block_tungsten",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_tungsten");

    public static final RegistryObject<Block> BLOCK_MAGNETIZED_TUNGSTEN = registerBlock("block_magnetized_tungsten",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_magnetized_tungsten");

    public static final RegistryObject<Block> BLOCK_LEAD = registerBlock("block_lead",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_lead");

    public static final RegistryObject<Block> BLOCK_BERYLLIUM = registerBlock("block_beryllium",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_beryllium");

    public static final RegistryObject<Block> BLOCK_BORON = registerBlock("block_boron",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_boron");

    public static final RegistryObject<Block> BLOCK_COBALT = registerBlock("block_cobalt",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_cobalt");

    public static final RegistryObject<Block> BLOCK_URANIUM = registerBlock("block_uranium",
            () -> new BlockHazard(BlockHazard.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_uranium");

    public static final RegistryObject<Block> BLOCK_U233 = registerBlock("block_u233",
            () -> new BlockHazard(BlockHazard.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_uranium");

    public static final RegistryObject<Block> BLOCK_U235 = registerBlock("block_u235",
            () -> new BlockHazard(BlockHazard.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_uranium");

    public static final RegistryObject<Block> BLOCK_U238 = registerBlock("block_u238",
            () -> new BlockHazard(BlockHazard.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_uranium");

    public static final RegistryObject<Block> BLOCK_PLUTONIUM = registerBlock("block_plutonium",
            () -> new BlockHazard(BlockHazard.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_plutonium");

    public static final RegistryObject<Block> BLOCK_THORIUM = registerBlock("block_thorium",
            () -> new BlockHazard(BlockHazard.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_thorium");

    public static final RegistryObject<Block> BLOCK_POLONIUM = registerBlock("block_polonium",
            () -> new BlockHotHazard(BlockHazard.createProperties()
                    .strength(5.0F, 50.0F)
                    .sound(SoundType.METAL)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_polonium");

    public static final RegistryObject<Block> BLOCK_TRINITITE = registerBlock("block_trinitite",
            () -> new BlockHazard(BlockHazard.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_trinitite");

    public static final RegistryObject<Block> BLOCK_STARMETAL = registerBlock("block_starmetal",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_starmetal");

    public static final RegistryObject<Block> STEEL_GRATE = registerBlock("deco/steel_grate",
            BlockSteelGrate::new,
            BlockModel.GRATE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/steel_grate");

    public static final RegistryObject<Block> STEEL_GRATE_WIDE = registerBlock("deco/steel_grate_wide",
            BlockSteelGrateWide::new,
            BlockModel.GRATE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/steel_grate_wide");

    public static final RegistryObject<Block> STEEL_BEAM = registerBlock("deco/steel_beam",
            BlockSteelBeam::new,
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/steel_beam");

    public static final RegistryObject<Block> STEEL_SCAFFOLD = registerBlock("deco/steel_scaffold",
            BlockSteelScaffold::new,
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/steel_scaffold");

    public static final RegistryObject<Block> FILING_CABINET = registerBlock("storage/filing_cabinet",
            BlockFilingCabinet::new,
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/filing_cabinet");

    public static final RegistryObject<Block> FILING_CABINET_STEEL = registerBlock("storage/filing_cabinet_steel",
            BlockFilingCabinetSteel::new,
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/filing_cabinet");



    public static final RegistryObject<Block> MACHINE_PRESS = registerBlock("machines/press",
            () -> new MachinePressBlock(MachinePressBlock.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/press");

    public static final RegistryObject<Block> MACHINE_BLAST_FURNACE = registerBlock("machines/machine_blast_furnace",
            () -> new BlastFurnaceBlock(BlastFurnaceBlock.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_blast_furnace");

    public static final RegistryObject<Block> MACHINE_FLUIDTANK = registerBlock("storage/fluid_tank",
            () -> new MachineFluidTank(MachineFluidTank.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_tank");

    public static final RegistryObject<Block> MACHINE_STIRLING = registerBlock("machines/machine_stirling",
            () -> new MachineStirling(MachineStirling.createProperties(), MachineStirling.StirlingType.NORMAL),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/stirling");

    public static final RegistryObject<Block> MACHINE_STIRLING_STEEL = registerBlock("machines/machine_stirling_steel",
            () -> new MachineStirling(MachineStirling.createProperties(), MachineStirling.StirlingType.STEEL),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/stirling");

    public static final RegistryObject<Block> MACHINE_STIRLING_CREATIVE = registerBlock("machines/machine_stirling_creative",
            () -> new MachineStirling(MachineStirling.createProperties(), MachineStirling.StirlingType.CREATIVE),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/stirling");

    public static final RegistryObject<Block> MACHINE_STEAM_ENGINE = registerBlock("machines/machine_steam_engine",
            () -> new MachineSteamEngine(MachineSteamEngine.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_steam_engine");

    public static final RegistryObject<Block> MACHINE_SOLDERING_STATION = registerBlock("machines/machine_soldering_station",
            () -> new MachineSolderingStation(MachineSolderingStation.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_soldering_station");

    public static final RegistryObject<Block> MACHINE_ARC_WELDER = registerBlock("machines/machine_arc_welder",
            () -> new MachineArcWelder(BlockDummyable.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_arc_welder");

    public static final RegistryObject<Block> MACHINE_CRYSTALLIZER = registerBlock("machines/machine_crystallizer",
            () -> new MachineCrystallizer(MachineCrystallizer.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_crystallizer");

    public static final RegistryObject<Block> FLUID_DUCT = registerBlock("storage/fluid_duct",
            () -> new FluidDuctStandard(BlockBehaviour.Properties.of(), FluidDuctStandard.FluidDuctType.FLUID_DUCT),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_duct");

    public static final RegistryObject<Block> FLUID_DUCT_SILVER = registerBlock("storage/fluid_duct_silver",
            () -> new FluidDuctStandard(BlockBehaviour.Properties.of(), FluidDuctStandard.FluidDuctType.FLUID_DUCT_SILVER),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_duct");

    public static final RegistryObject<Block> FLUID_DUCT_COLORED = registerBlock("storage/fluid_duct_colored",
            () -> new FluidDuctStandard(BlockBehaviour.Properties.of(), FluidDuctStandard.FluidDuctType.FLUID_DUCT_COLORED),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_duct");


    public static final RegistryObject<Block> FLUID_DUCT_BOX_SILVER = registerBlock("storage/fluid_duct_box_silver",
            () -> new FluidDuctBox(Block.Properties.of(), FluidDuctBox.FluidDuctBoxMaterial.SILVER),
            BlockModel.ENTITYBLOCK_ANIMATED,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_duct_box_silver");

    public static final RegistryObject<Block> FLUID_DUCT_BOX_COPPER = registerBlock("storage/fluid_duct_box_copper",
            () -> new FluidDuctBox(Block.Properties.of(), FluidDuctBox.FluidDuctBoxMaterial.COPPER),
            BlockModel.ENTITYBLOCK_ANIMATED,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_duct_box_copper");

    public static final RegistryObject<Block> FLUID_DUCT_BOX_WHITE = registerBlock("storage/fluid_duct_box_white",
            () -> new FluidDuctBox(Block.Properties.of(), FluidDuctBox.FluidDuctBoxMaterial.WHITE),
            BlockModel.ENTITYBLOCK_ANIMATED,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_duct_box_white");

    public static final RegistryObject<Block> FLUID_DUCT_EXHAUST = registerBlock("storage/fluid_duct_exhaust",
            () -> new FluidDuctExhaust(Block.Properties.of(), FluidDuctExhaust.FluidDuctBoxMaterial.WHITE),
            BlockModel.ENTITYBLOCK_ANIMATED,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/fluid_duct_exhaust");

    public static final RegistryObject<Block> BARREL_PLASTIC = registerBlock("storage/barrel_plastic",
            () -> new BlockBarrel(
                    BlockBarrel.createProperties(MapColor.METAL, SoundType.STONE),
                    12000,
                    BlockBarrel.BarrelType.PLASTIC
            ),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> BARREL_CORRODED = registerBlock("storage/barrel_corroded",
            () -> new BlockBarrel(
                    BlockBarrel.createProperties(MapColor.METAL, SoundType.METAL),
                    6000,
                    BlockBarrel.BarrelType.CORRODED
            ),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> BARREL_IRON = registerBlock("storage/barrel_iron",
            () -> new BlockBarrel(
                    BlockBarrel.createProperties(MapColor.METAL, SoundType.METAL),
                    8000,
                    BlockBarrel.BarrelType.IRON
            ),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> BARREL_STEEL = registerBlock("storage/barrel_steel",
            () -> new BlockBarrel(
                    BlockBarrel.createProperties(MapColor.METAL, SoundType.METAL),
                    16000,
                    BlockBarrel.BarrelType.STEEL
            ),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> BARREL_TCALLOY = registerBlock("storage/barrel_tcalloy",
            () -> new BlockBarrel(
                    BlockBarrel.createProperties(MapColor.METAL, SoundType.METAL),
                    24000,
                    BlockBarrel.BarrelType.TCALLOY
            ),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> BARREL_ANTIMATTER = registerBlock("storage/barrel_antimatter",
            () -> new BlockBarrel(
                    BlockBarrel.createProperties(MapColor.METAL, SoundType.METAL),
                    16000,
                    BlockBarrel.BarrelType.ANTIMATTER
            ),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> CRATE_SUPPLIES = registerBlock("block_supply_crate",
            () -> new BlockSupplyCrate(EntityC130.C130PayloadType.SUPPLIES),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "conserve_crate");

    public static final RegistryObject<Block> CRATE_WEAPONS = registerBlock("block_weapon_crate",
            () -> new BlockSupplyCrate(EntityC130.C130PayloadType.WEAPONS),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "conserve_crate");

    public static final RegistryObject<Block> MACHINE_DIESEL = registerBlock("machines/machine_diesel",
            () -> new MachineDiesel(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_diesel");

    public static final RegistryObject<Block> TAINT = registerBlock("taint",
            () -> new BlockTaint(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "taint");

    public static final RegistryObject<Block> WASTE_LOG = registerBlock("waste_log",
            () -> new WasteLog(WasteLog.createPropertiesWasteLog(),
                    ResLocation(RefStrings.MODID, "block/waste_log_top"),
                    ResLocation(RefStrings.MODID, "block/waste_log_side")),
            BlockModel.PILLAR,
            LootInfo.self(),
            ToolInfo.axe(ToolInfo.ToolLevel.IRON),
            "waste_log_side", "waste_log_top");

    public static final RegistryObject<Block> WASTE_PLANKS = registerBlock("waste_planks",
            () -> new BlockOre(BlockOre.createProperties(MapColor.WOOD, 0.5F, 2.5F)
                    .sound(SoundType.WOOD)
                    .requiresCorrectToolForDrops(), 0.0F),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.axe(ToolInfo.ToolLevel.IRON),
            "waste_planks");

    public static final RegistryObject<Block> GLYPHID_BASE = registerBlock("glyphid_base",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glyphid_base");

    public static final RegistryObject<Block> GLYPHID_SPAWNER = registerBlock("glyphid_spawner",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glyphid_spawner");

    public static final RegistryObject<Block> ORE_NETHER_URANIUM = registerBlock("ore_nether_uranium",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.NETHER, 0.4F, 10.0F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_nether_uranium", "netherrack");

    public static final RegistryObject<Block> ORE_NETHER_SCHRABIDIUM = registerBlock("ore_nether_schrabidium",
            () -> new BlockGeneric(BlockGeneric.createOreProperties(MapColor.NETHER, 15.0F, 600.0F).sound(SoundType.STONE)),
            BlockModel.ORE,
            LootInfo.itemSilk(ModItems.ORE_SCHRABIDIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_nether_schrabidium", "netherrack");

    public static final RegistryObject<Block> ORE_NETHER_SULFUR = registerBlock("ore_nether_sulfur",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .strength(0.4F, 10.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), 0.0F),
            BlockModel.ORE,
            LootInfo.item(ModItems.SULFUR, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.STONE),
            "ore_nether_sulfur", "netherrack");

    public static final RegistryObject<Block> ORE_SELLAFIELD_DIAMOND = registerBlock("ore_sellafield_diamond",
            () -> new BlockSellafieldOre(BlockSellafieldOre.createProperties()),
            BlockModel.ORE,
            LootInfo.item(ModItems.ORE_SELLAFIELD_DIAMOND, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_overlay_diamond", "sellafield_base");

    public static final RegistryObject<Block> ORE_SELLAFIELD_EMERALD = registerBlock("ore_sellafield_emerald",
            () -> new BlockSellafieldOre(BlockSellafieldOre.createProperties()),
            BlockModel.ORE,
            LootInfo.item(ModItems.ORE_SELLAFIELD_EMERALD, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_overlay_emerald", "sellafield_base");

    public static final RegistryObject<Block> ORE_SELLAFIELD_SCHRABIDIUM = registerBlock("ore_sellafield_schrabidium",
            () -> new BlockSellafieldOre(BlockSellafieldOre.createProperties()),
            BlockModel.ORE,
            LootInfo.item(ModItems.ORE_SELLAFIELD_SCHRABIDIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_overlay_schrabidium", "sellafield_base");

    public static final RegistryObject<Block> ORE_SELLAFIELD_RADGEM = registerBlock("ore_sellafield_radgem",
            () -> new BlockSellafieldOre(BlockSellafieldOre.createProperties()),
            BlockModel.ORE,
            LootInfo.item(ModItems.ORE_SELLAFIELD_RADGEM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_overlay_radgem", "sellafield_base");

    public static final RegistryObject<Block> ORE_NETHER_URANIUM_SCORCHED = registerBlock("ore_nether_uranium_scorched",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.NETHER, 0.4F, 10F, true), 5, true),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_nether_uranium_scorched", "netherrack");

    public static final RegistryObject<Block> ORE_SELLAFIELD_URANIUM_SCORCHED = registerBlock("ore_sellafield_uranium_scorched",
            () -> new BlockSellafieldOre(BlockSellafieldOre.createProperties()),
            BlockModel.ORE,
            LootInfo.item(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_overlay_uranium_scorched", "sellafield_base");

    public static final RegistryObject<Block> ORE_BEDROCK = registerBlock("ore_bedrock",
            () -> new BlockBedrockOreTE(BlockBedrockOreTE.createProperties()),
            BlockModel.BEDROCK_ORE,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.NONE),
            "minecraft:block/bedrock", "ore_random_");

    public static final RegistryObject<Block> ORE_BEDROCK_OIL = registerBlock("ore_bedrock_oil",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(-1.0F, 3600000.0F)
                    .pushReaction(PushReaction.BLOCK)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_bedrock_oil");

    public static final RegistryObject<Block> ORE_GNEISS_URANIUM = registerBlock("ore_gneiss_uranium",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10F, true), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_gneiss_uranium");

    public static final RegistryObject<Block> ORE_GNEISS_GAS = registerBlock("ore_gneiss_gas",
            () -> new BlockOre(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_gneiss_gas");

    public static final RegistryObject<Block> ORE_NETHER_FIRE = registerBlock("ore_nether_fire",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .strength(0.4F, 10.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), 0.0F),
            BlockModel.ORE,
            LootInfo.item(ModItems.POWDER_FIRE, 1, 3),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_nether_fire", "netherrack");

    public static final RegistryObject<Block> BLOCK_METEOR_COBBLE = registerBlock("block_meteor_cobble",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 360.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), 0.0F).noFortune(),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_meteor_cobble");

    public static final RegistryObject<Block> BLOCK_METEOR_BROKEN = registerBlock("block_meteor_broken",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 360.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), 0.0F).noFortune(),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_meteor_broken");

    public static final RegistryObject<Block> ORE_GNEISS_RARE = registerBlock("ore_gneiss_rare",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5F, 10.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), 0.0F),
            BlockModel.ORE,
            LootInfo.item(ModItems.RARE_EARTH_CHUNK, 1, 2),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_gneiss_rare", "stone");

    public static final RegistryObject<Block> WASTE_TRINITITE = registerBlock("waste_trinitite",
            () -> new BlockOre(BlockOre.createRadioactiveProperties(MapColor.COLOR_GREEN, 0.5F, 2.5F)
                    .sound(SoundType.SAND), 0.0F).noFortune(),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "waste_trinitite");

    public static final RegistryObject<Block> WASTE_TRINITITE_RED = registerBlock("waste_trinitite_red",
            () -> new BlockOre(BlockOre.createRadioactiveProperties(MapColor.COLOR_RED, 0.5F, 2.5F)
                    .sound(SoundType.SAND), 0.0F).noFortune(),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "waste_trinitite_red");

    public static final RegistryObject<Block> FALLOUT = registerBlock("fallout",
            () -> new BlockFallout(BlockFallout.createProperties()),
            BlockModel.LAYERED, LootInfo.self(), ToolInfo.shovel(ToolInfo.ToolLevel.STONE), "fallout");

    public static final RegistryObject<Block> VOLCANO_CORE = registerBlock("volcano_core",
            () -> new BlockVolcano(BlockVolcano.createProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "volcano_core");

    public static final RegistryObject<Block> VOLCANO_RAD_CORE = registerBlock("volcano_rad_core",
            () -> new BlockVolcano(BlockVolcano.createProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "volcano_rad_core");

    public static final RegistryObject<Block> STATUE_ELB_F = registerBlock("deco/statue_elb_f",
            () -> new DecoBlockAlt(DecoBlockAlt.createProperties()),
            BlockModel.ENTITYBLOCK_ANIMATED,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/statue_elb_f");

    public static final RegistryObject<Block> PLASMA = registerBlock("plasma",
            () -> new BlockPlasma(BlockPlasma.createProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "plasma");

    public static final RegistryObject<Block> RADIOREC = registerBlock("radiorec",
            () -> new RadioRec(RadioRec.createProperties()),
            BlockModel.MODEL_ITEM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "radiorec");

    public static final RegistryObject<Block> RADIOBOX = registerBlock("radiobox",
            () -> new BlockRadiobox(BlockRadiobox.createProperties()),
            BlockModel.MODEL_ITEM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "radiobox");

    public static final RegistryObject<Block> BROADCASTER_PC = registerBlock("broadcaster_pc",
            () -> new PinkCloudBroadcaster(PinkCloudBroadcaster.createProperties()),
            BlockModel.MODEL_ITEM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "broadcaster_pc");

    public static final RegistryObject<Block> FROZEN_DIRT = registerBlock("frozen_dirt",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5F, 2.5F)
                    .sound(SoundType.GLASS)
                    .requiresCorrectToolForDrops(), 0.0F),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "frozen_dirt");

    public static final RegistryObject<Block> FROZEN_LOG = registerBlock("frozen_log",
            () -> new WasteLog(WasteLog.createPropertiesWasteLog(),
                    ResLocation(RefStrings.MODID, "block/frozen_log_top"),
                    ResLocation(RefStrings.MODID, "block/frozen_log")),
            BlockModel.PILLAR,
            LootInfo.self(),
            ToolInfo.axe(ToolInfo.ToolLevel.IRON),
            "frozen_log_side", "frozen_log_top");

    public static final RegistryObject<Block> FROZEN_PLANKS = registerBlock("frozen_planks",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.5F, 2.5F)
                    .sound(SoundType.GLASS)
                    .requiresCorrectToolForDrops(), 0.0F),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.axe(ToolInfo.ToolLevel.IRON),
            "frozen_planks");

    public static final RegistryObject<Block> GRAVEL_OBSIDIAN = registerBlock("gravel_obsidian",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(5.0F, 240.0F)
                    .sound(SoundType.GRAVEL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "gravel_obsidian");

    public static final RegistryObject<Block> GRAVEL_DIAMOND = registerBlock("gravel_diamond",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(5.0F, 240.0F)
                    .sound(SoundType.GRAVEL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "gravel_diamond");

    public static final RegistryObject<Block> MOON_TURF = registerBlock("moon_turf",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(5.0F, 240.0F)
                    .sound(SoundType.GRAVEL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "moon_turf");

    public static final RegistryObject<Block> GAS_RADON = registerBlock("gas_radon",
            () -> new BlockGasRadon(BlockGasRadon.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_radon");

    public static final RegistryObject<Block> GAS_RADON_DENSE = registerBlock("gas_radon_dense",
            () -> new BlockGasRadonDense(BlockGasRadonDense.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_radon_dense");

    public static final RegistryObject<Block> GAS_RADON_TOMB = registerBlock("gas_radon_tomb",
            () -> new BlockGasRadonTomb(BlockGasRadon.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_radon_tomb");

    public static final RegistryObject<Block> GAS_MONOXIDE = registerBlock("gas_monoxide",
            () -> new BlockGasMonoxide(BlockGasBase.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_monoxide");

    public static final RegistryObject<Block> GAS_COAL = registerBlock("gas_coal",
            () -> new BlockGasCoal(BlockGasBase.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_coal");

    public static final RegistryObject<Block> GAS_ASBESTOS = registerBlock("gas_asbestos",
            () -> new BlockGasAsbestos(BlockGasAsbestos.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_asbestos");

    public static final RegistryObject<Block> GAS_FLAMMABLE = registerBlock("gas_flammable",
            () -> new BlockGasFlammable(BlockGasBase.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_flammable");

    public static final RegistryObject<Block> GAS_EXPLOSIVE = registerBlock("gas_explosive",
            () -> new BlockGasExplosive(BlockGasBase.createGasProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_explosive");

    public static final RegistryObject<Block> GAS_MELTDOWN = registerBlock("gas_meltdown",
            () -> new BlockGasMeltdown(BlockGasMeltdown.createProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "gas_meltdown");

    public static final RegistryObject<Block> CHLORINE_GAS = registerBlock("chlorine_gas",
            () -> new BlockGasChlorine(BlockGasChlorine.createProperties()),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "chlorine_gas");

    public static final RegistryObject<Block> BRICK_CONCRETE = registerBlock("brick_concrete",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 160.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "brick_concrete");


    public static final RegistryObject<Block> REINFORCED_STONE = registerBlock("reinforced_stone",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 100.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "reinforced_stone");

    public static final RegistryObject<Block> CONCRETE = registerBlock("concrete",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 140.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "concrete");

    public static final RegistryObject<Block> CONCRETE_SMOOTH = registerBlock("concrete_smooth",
            () -> new BlockRadResistant(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 140.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "concrete_smooth");

    public static final RegistryObject<Block> BRICK_CONCRETE_BROKEN = registerBlock("brick_concrete_broken",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 45.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "brick_concrete_broken");

    public static final RegistryObject<Block> BRICK_CONCRETE_CRACKED = registerBlock("brick_concrete_cracked",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 160.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "brick_concrete_cracked");

    public static final RegistryObject<Block> BALEFIRE = registerBlock("balefire",
            () -> new Balefire(Balefire.createBalefireProperties()),
            BlockModel.CROSS,
            ToolInfo.none(),
            "balefire");

    public static final RegistryObject<Block> BRICK_LIGHT = registerBlock("brick_light",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(5.0F, 20.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "brick_light");

    public static final RegistryObject<Block> BLOCK_SCRAP = registerBlock("block_scrap",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(2.5F, 5.0F)
                    .sound(SoundType.GRAVEL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "block_scrap");

    public static final RegistryObject<Block> BRICK_OBSIDIAN = registerBlock("brick_obsidian",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 120.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 0)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "brick_obsidian");

    public static final RegistryObject<Block> RED_CABLE = registerBlock("red_cable",
            () -> new BlockCable(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cable_neo");

    public static final RegistryObject<Block> ORE_SCHRABIDIUM = registerBlock("ore_schrabidium",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 600.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .randomTicks(), 0.1F),
            BlockModel.ORE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_schrabidium", "stone");

    public static final RegistryObject<Block> ORE_URANIUM_SCORCHED = registerBlock("ore_uranium_scorched",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 5.0F, 10.0F, true), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_uranium_scorched");

    public static final RegistryObject<Block> ORE_GNEISS_SCHRABIDIUM = registerBlock("ore_gneiss_schrabidium",
            () -> new BlockOre(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5F, 10.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE), 0.0F),
            BlockModel.ORE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "ore_schrabidium", "stone");

    public static final RegistryObject<Block> ORE_GNEISS_URANIUM_SCORCHED = registerBlock("ore_gneiss_uranium_scorched",
            () -> new BlockOutgas(BlockOutgas.createProperties(MapColor.STONE, 1.5F, 10F, true), 5, true),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.RAW_URANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_gneiss_uranium_scorched");

    public static final RegistryObject<Block> BLOCK_ELECTRICAL_SCRAP = registerBlock("block_electrical_scrap",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.5F, 5.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_electrical_scrap");

    public static final RegistryObject<Block> FUSION_CONDUCTOR = registerBlock("fusion_conductor",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "fusion_conductor_side", "fusion_conductor_top");

    public static final RegistryObject<Block> FUSION_MOTOR = registerBlock("fusion_motor",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "fusion_motor_side", "fusion_motor_top");

    public static final RegistryObject<Block> FUSION_HEATER = registerBlock("fusion_heater",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "fusion_heater_side", "fusion_heater_top");

    public static final RegistryObject<Block> BLOCK_WASTE = registerBlock("block_waste",
            () -> new BlockNuclearWaste(BlockNuclearWaste.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_waste");

    public static final RegistryObject<Block> BLOCK_WASTE_PAINTED = registerBlock("block_waste_painted",
            () -> new BlockNuclearWaste(BlockNuclearWaste.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_waste_painted");

    public static final RegistryObject<Block> BLOCK_WASTE_VITRIFIED = registerBlock("block_waste_vitrified",
            () -> new BlockNuclearWaste(BlockNuclearWaste.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_waste_vitrified");

    public static final RegistryObject<Block> BLOCK_SCHRARANIUM = registerBlock("block_schraranium",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 250.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.SCHRAB),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_schraranium");

    public static final RegistryObject<Block> BLOCK_SCHRABIDIUM = registerBlock("block_schrabidium",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 600.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.SCHRAB),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_schrabidium");

    public static final RegistryObject<Block> BLOCK_SCHRABIDATE = registerBlock("block_schrabidate",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 600.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.SCHRAB),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_schrabidate");

    public static final RegistryObject<Block> BLOCK_LANTHANIUM = registerBlock("block_lanthanium",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_lanthanium");

    public static final RegistryObject<Block> BLOCK_RA226 = registerBlock("block_ra226",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_ra226");

    public static final RegistryObject<Block> BLOCK_ACTINIUM = registerBlock("block_actinium",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.SCHRAB),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_actinium");

    public static final RegistryObject<Block> BLOCK_COLTAN = registerBlock("block_coltan",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_coltan");

    public static final RegistryObject<Block> BLOCK_SMORE = registerBlock("block_smore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_smore_side", "block_smore","block_smore");

    public static final RegistryObject<Block> BLOCK_SEMTEX = registerBlock("block_semtex",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_FRONT_SIDE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_semtex_front", "block_semtex", "block_semtex");

    public static final RegistryObject<Block> BLOCK_C4 = registerBlock("block_c4",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_c4");

    public static final RegistryObject<Block> BLOCK_POLYMER = registerBlock("block_polymer",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_polymer");

    public static final RegistryObject<Block> BLOCK_BAKELITE = registerBlock("block_bakelite",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_bakelite");

    public static final RegistryObject<Block> BLOCK_RUBBER = registerBlock("block_rubber",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_rubber");

    public static final RegistryObject<Block> BLOCK_CADMIUM = registerBlock("block_cadmium",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_cadmium");

    public static final RegistryObject<Block> BLOCK_TCALLOY = registerBlock("block_tcalloy",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_tcalloy");

    public static final RegistryObject<Block> BLOCK_CDALLOY = registerBlock("block_cdalloy",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_cdalloy");

    // ==================== БЛОКИ ДЛЯ MINERAL SET ====================

    public static final RegistryObject<Block> BLOCK_NIOBIUM = registerBlock("block_niobium",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_niobium");

    public static final RegistryObject<Block> BLOCK_BISMUTH = registerBlock("block_bismuth",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_bismuth");

    public static final RegistryObject<Block> BLOCK_TANTALIUM = registerBlock("block_tantalium",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_tantalium");

    public static final RegistryObject<Block> BLOCK_ZIRCONIUM = registerBlock("block_zirconium",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_zirconium");

    public static final RegistryObject<Block> BLOCK_DINEUTRONIUM = registerBlock("block_dineutronium",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_dineutronium");

    public static final RegistryObject<Block> BLOCK_PU_MIX = registerBlock("block_pu_mix",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_pu_mix");
    public static final RegistryObject<Block> BLOCK_FLUORITE = registerBlock("block_fluorite",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_fluorite");

    public static final RegistryObject<Block> BLOCK_NITER = registerBlock("block_niter",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_niter");

    public static final RegistryObject<Block> BLOCK_RED_COPPER = registerBlock("block_red_copper",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_red_copper");

    public static final RegistryObject<Block> BLOCK_SULFUR = registerBlock("block_sulfur",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_sulfur");

    public static final RegistryObject<Block> BLOCK_EUPHEMIUM = registerBlock("block_euphemium",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 600.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.SCHRAB),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_euphemium");

    public static final RegistryObject<Block> BLOCK_ADVANCED_ALLOY = registerBlock("block_advanced_alloy",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_advanced_alloy");

    public static final RegistryObject<Block> BLOCK_COMBINE_STEEL = registerBlock("block_combine_steel",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_combine_steel");

    public static final RegistryObject<Block> BLOCK_AUSTRALIUM = registerBlock("block_australium",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_australium");

    public static final RegistryObject<Block> BLOCK_URANIUM_FUEL = registerBlock("block_uranium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_uranium_fuel");

    public static final RegistryObject<Block> BLOCK_NEPTUNIUM = registerBlock("block_neptunium",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_neptunium");

    public static final RegistryObject<Block> BLOCK_PU238 = registerBlock("block_pu238",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_pu238");

    public static final RegistryObject<Block> BLOCK_PU239 = registerBlock("block_pu239",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_pu239");

    public static final RegistryObject<Block> BLOCK_PU240 = registerBlock("block_pu240",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_pu240");

    public static final RegistryObject<Block> BLOCK_MOX_FUEL = registerBlock("block_mox_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_mox_fuel");

    public static final RegistryObject<Block> BLOCK_PLUTONIUM_FUEL = registerBlock("block_plutonium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_plutonium_fuel");

    public static final RegistryObject<Block> BLOCK_THORIUM_FUEL = registerBlock("block_thorium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_thorium_fuel");

    public static final RegistryObject<Block> BLOCK_WHITE_PHOSPHORUS = registerBlock("block_white_phosphorus",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.FLAMES),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_white_phosphorus");

    public static final RegistryObject<Block> BLOCK_RED_PHOSPHORUS = registerBlock("block_red_phosphorus",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_red_phosphorus");

    public static final RegistryObject<Block> BLOCK_INSULATOR = registerBlock("block_insulator",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_insulator_side", "block_insulator_top", "block_insulator_top");

    public static final RegistryObject<Block> BLOCK_FIBERGLASS = registerBlock("block_fiberglass",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_WHITE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_fiberglass_side", "block_fiberglass_top","block_fiberglass_top");

    public static final RegistryObject<Block> BLOCK_DURA_STEEL = registerBlock("block_dura_steel",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_dura_steel");

    public static final RegistryObject<Block> BLOCK_YELLOWCAKE = registerBlock("block_yellowcake",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_yellowcake");

    public static final RegistryObject<Block> BLOCK_SOLINIUM = registerBlock("block_solinium",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 600.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.SCHRAB),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_solinium");

    public static final RegistryObject<Block> BLOCK_SCHRABIDIUM_FUEL = registerBlock("block_schrabidium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 600.0F)
                    .sound(SoundType.METAL)
                    .randomTicks())
                    .makeBeaconable()
                    .setDisplayEffect(BlockHazard.ExtDisplayEffect.SCHRAB),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "block_schrabidium_fuel");

    public static final RegistryObject<Block> BASALT = registerBlock("basalt",
            () -> new BlockPillar(BlockPillar.createProperties()),
            BlockModel.PILLAR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> ORE_BASALT = registerBlock("ore_basalt",
            BlockOreBasalt::new,
            BlockModel.ENUM_PILLAR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_basalt", new String[]{"asbestos", "fluorite", "gem", "molysite", "sulfur"});

    public static final RegistryObject<Block> VOLCANIC_LAVA_BLOCK = registerBlock("volcanic_lava_block",
            () -> new VolcanicBlock(ModFluids.VOLCANIC_LAVA_FLUID.get(), VolcanicBlock.createProperties()),
            BlockModel.CUBE_ALL,
            "volcanic_lava_still");

    public static final RegistryObject<Block> RAD_LAVA_BLOCK = registerBlock("rad_lava_block",
            () -> new RadBlock(ModFluids.RAD_LAVA_FLUID.get(), RadBlock.createProperties()),
            BlockModel.CUBE_ALL,
            "rad_lava_still");

    public static final RegistryObject<Block> MUD_BLOCK = registerBlock("mud_block",
            () -> new MudBlock(ModFluids.MUD_FLUID.get(), MudBlock.createProperties()),
            BlockModel.CUBE_ALL,
            "mud_still");

    public static final RegistryObject<Block> TOXIC_BLOCK = registerBlock("toxic_block",
            () -> new ToxicBlock(ModFluids.TOXIC_FLUID.get(), ToxicBlock.createProperties()),
            BlockModel.CUBE_ALL,
            "toxic_still");

    public static final RegistryObject<Block> SELLAFIELD_BEDROCK = registerBlock("sellafield_bedrock",
            () -> new BlockSellafieldSlaked(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(-1.0F, 3600000.0F)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "sellafield_bedrock");

    public static final RegistryObject<Block> SELLAFIELD_SLAKED = registerBlock("sellafield_slaked",
            () -> new BlockSellafieldSlaked(BlockSellafieldSlaked.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sellafield_slaked");

    public static final RegistryObject<Block> PWR_CASING = registerBlock("pwr_casing",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()), // new BlockGenericPWR(Material.iron).setBlockName("pwr_casing").setHardness(5.0F).setResistance(10.0F).setCreativeTab(MainRegistry.machineTab).setBlockTextureName(RefStrings.MODID + ":pwr_casing");
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "pwr_casing");

    public static final RegistryObject<Block> DUMMY_BLOCK = registerBlock("dummy_block",
            () -> new Block(Block.Properties.of()
                    .mapColor(MapColor.NONE)
                    .strength(-1.0F, 3600000.0F)
                    .noOcclusion()
                    .noLootTable()
                    .noCollission()
                    .air()),
            BlockModel.AIR,
            "dummy_block");

    public static final RegistryObject<Block> BLOCK_COKE = registerBlock("block_coke",
            BlockCoke::new,
            BlockModel.ENUM_BLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_coke",
            BlockCoke.CokeType.class);

    public static final RegistryObject<Block> HEATER_FIREBOX = registerBlock("machines/heater_firebox",
            () -> new HeaterFirebox(HeaterFirebox.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/heater_firebox");

    public static final RegistryObject<Block> MACHINE_ASHPIT = registerBlock("machines/machine_ashpit",
            () -> new MachineAshpit(MachineAshpit.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/heater_oven");

    public static final RegistryObject<Block> HEATER_OVEN = registerBlock("machines/heater_oven",
            () -> new HeaterOven(HeaterOven.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/heater_oven");

    public static final RegistryObject<Block> HEATER_OILBURNER = registerBlock("machines/heater_oilburner",
            () -> new HeaterOilburner(HeaterOilburner.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/heater_oilburner");

    public static final RegistryObject<Block> FURNACE_STEEL = registerBlock("machines/furnace_steel",
            () -> new FurnaceSteel(FurnaceSteel.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/furnace_steel");

    public static final RegistryObject<Block> FURNACE_IRON = registerBlock("machines/furnace_iron",
            () -> new FurnaceIron(FurnaceIron.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/furnace_iron");

    public static final RegistryObject<Block> FURNACE_COMBINATION = registerBlock("machines/furnace_combination",
            () -> new FurnaceCombination(FurnaceCombination.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/furnace_combination");

    public static final RegistryObject<Block> MACHINE_SAWMILL = registerBlock("machines/sawmill",
            () -> new MachineSawmill(MachineSawmill.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/sawmill");

    public static final RegistryObject<Block> MACHINE_HEAT_BOILER = registerBlock("machines/heat_boiler",
            () -> new MachineHeatBoiler(MachineHeatBoiler.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/heat_boiler");

    public static final RegistryObject<Block> MACHINE_AUTOSAW = registerBlock("machines/autosaw",
            () -> new MachineAutosaw(MachineAutosaw.createProperties()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/autosaw");

    public static final RegistryObject<Block> MACHINE_CRUCIBLE = registerBlock("machines/machine_crucible",
            () -> new MachineCrucible(MachineCrucible.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_crucible");

    public static final RegistryObject<Block> PLANT_TALL = registerBlock("plant_tall",
            () -> new BlockTallPlant(BlockTallPlant.createProperties()),
            BlockModel.TALL_PLANT_ENUM,
            LootInfo.self(),
            ToolInfo.none(),
            "plant_tall", BlockTallPlant.EnumTallFlower.class
    );

    public static final RegistryObject<Block> DIRT_DEAD = registerBlock("dirt_dead",
            () -> new FallingBlock(Block.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5F)
                    .sound(SoundType.GRAVEL)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "dirt_dead");

    public static final RegistryObject<Block> DIRT_OILY = registerBlock("dirt_oily",
            () -> new FallingBlock(Block.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5F)
                    .sound(SoundType.GRAVEL)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "dirt_oily");

    public static final RegistryObject<Block> ORE_ALEXANDRITE = registerBlock("ore_alexandrite",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_alexandrite");

    public static final RegistryObject<Block> ORE_OIL = registerBlock("ore_oil",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_oil", "stone");

    public static final RegistryObject<Block> ORE_OIL_EMPTY = registerBlock("ore_oil_empty",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.ORE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_oil_empty", "stone");

    public static final RegistryObject<Block> CLUSTER_IRON = registerBlock("cluster_iron",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.CRYSTAL_IRON, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cluster_iron");

    public static final RegistryObject<Block> CLUSTER_TITANIUM = registerBlock("cluster_titanium",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.CRYSTAL_TITANIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cluster_titanium");

    public static final RegistryObject<Block> CLUSTER_ALUMINIUM = registerBlock("cluster_aluminium",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.CRYSTAL_ALUMINIUM, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cluster_aluminium");

    public static final RegistryObject<Block> CLUSTER_COPPER = registerBlock("cluster_copper",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.item(ModItems.CRYSTAL_COPPER, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cluster_copper");

    public static final RegistryObject<Block> MINE_AP = registerBlock("bomb/mine/mine_ap",
            () -> new Landmine(Landmine.createProperties(), 1.5D, 1D),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "bomb/mine/mine_ap");

    public static final RegistryObject<Block> MINE_HE = registerBlock("bomb/mine/mine_he",
            () -> new Landmine(Landmine.createProperties(), 2D, 5D),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "bomb/mine/mine_he");

    public static final RegistryObject<Block> MINE_SHRAP = registerBlock("bomb/mine/mine_shrap",
            () -> new Landmine(Landmine.createProperties(), 1.5D, 1D),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "bomb/mine/mine_ap");

    public static final RegistryObject<Block> MINE_FAT = registerBlock("bomb/mine/mine_fat",
            () -> new Landmine(Landmine.createProperties(), 2.5D, 1D),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "bomb/mine/mine_fat");

    public static final RegistryObject<Block> MINE_NAVAL = registerBlock("bomb/mine/mine_naval",
            () -> new Landmine(Landmine.createProperties(), 2.5D, 1D),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "bomb/mine/mine_naval");

    public static final RegistryObject<Block> BLOCK_SCHRABIDIUM_CLUSTER = registerBlock("block_schrabidium_cluster",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND));

    public static final RegistryObject<Block> BLOCK_EUPHEMIUM_CLUSTER = registerBlock("block_euphemium_cluster",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND));

    public static final RegistryObject<Block> DET_CORD = registerBlock("det_cord",
            () -> new DetCord(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cable_neo");

    public static final RegistryObject<Block> RED_BARREL = registerBlock("red_barrel",
            RedBarrel::createFlammable,
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> PINK_BARREL = registerBlock("pink_barrel",
            RedBarrel::createFlammable,
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> YELLOW_BARREL = registerBlock("yellow_barrel",
            YellowBarrel::create,
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> LOX_BARREL = registerBlock("lox_barrel",
            RedBarrel::createNonFlammable,
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> TAINT_BARREL = registerBlock("taint_barrel",
            RedBarrel::createNonFlammable,
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/barrel");

    public static final RegistryObject<Block> NUKE_CUSTOM = registerBlock("bomb/nuke_custom",
            () -> new NukeCustom(NukeCustom.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "bomb/nuke_custom");

    public static final RegistryObject<Block> STEEL_POLES = registerBlock("deco/steel_poles",
            () -> new DecoSteelPoles(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/steel_beam");

    public static final RegistryObject<Block> TAPE_RECORDER = registerBlock("deco/tape_recorder",
            () -> new DecoTapeRecorder(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco/tape_recorder");

    public static final RegistryObject<Block> POLE_TOP = registerBlock("deco/pole_top",
            () -> new DecoPoleTop(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.MODEL_ITEM_STATIC,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "pole_top");

    public static final RegistryObject<Block> POLE_SATELLITE_RECEIVER = registerBlock("deco/pole_satellite_receiver",
            () -> new DecoPoleSatelliteReceiver(ModBlockProperties.ORE_PROPERTIES()),
            BlockModel.MODEL_ITEM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "pole_satellite_receiver");

    public static final RegistryObject<Block> BLOCK_SLAG = registerBlock("block_slag",
            () -> new BlockSlag(BlockSlag.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_slag");

    public static final RegistryObject<Block> BOBBLEHEAD = registerBlock("bobble",
            BlockBobble::new,
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "bobble");

    public static final RegistryObject<Block> MACHINE_ARMOR_TABLE = registerBlock("machines/machine_armor_table",
            () -> new BlockArmorTable(BlockBehaviour.Properties.of().strength(5.0F).noOcclusion()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/armor_table_side", "machines/armor_table_top", "machines/armor_table_bottom");

    public static final RegistryObject<Block> MACHINE_WEAPON_TABLE = registerBlock("machines/machine_weapon_table",
            () -> new BlockWeaponTable(BlockBehaviour.Properties.of().strength(5.0F).noOcclusion()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/weapon_table_side", "machines/weapon_table_top", "machines/weapon_table_bottom");

    public static final RegistryObject<Block> CONCRETE_COLORED = registerBlock("concrete_colored",
            () -> new BlockConcreteColored(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 140.0F)
                    .sound(SoundType.STONE)),
            BlockModel.ENUM_BLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "concrete_colored",
            DyeColor.class);

    public static final RegistryObject<Block> CONCRETE_COLORED_EXT = registerBlock("concrete_colored_ext",
            () -> new BlockConcreteColoredExt(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 140.0F)
                    .sound(SoundType.STONE)),
            BlockModel.ENUM_BLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "concrete_colored_ext",
            BlockConcreteColoredExt.EnumConcreteType.class);

    public static final RegistryObject<Block> CONCRETE_ASBESTOS = registerBlock("concrete_asbestos",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 150.0F)
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "concrete_asbestos");

    public static final RegistryObject<Block> CONCRETE_REBAR = registerBlock("concrete_rebar",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(50.0F, 240.0F)
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "concrete_rebar");

    public static final RegistryObject<Block> CONCRETE_SUPER = registerBlock("concrete_super",
            () -> new BlockUberConcrete(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(150.0F, 1000.0F)
                    .sound(SoundType.STONE)
                    .randomTicks()),
            BlockModel.CONCRETE_SUPER,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.DIAMOND),
            "concrete_super");

    public static final RegistryObject<Block> CONCRETE_SUPER_BROKEN = registerBlock("concrete_super_broken",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(10.0F, 20.0F)
                    .sound(SoundType.STONE)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "concrete_super_broken");

    public static final RegistryObject<Block> CONCRETE_PILLAR = registerBlock("concrete_pillar",
            () -> new BlockRotatablePillar(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 180.0F)
                    .sound(SoundType.STONE)),
            BlockModel.PILLAR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> SANDBAG = registerBlock("sandbag",
            () -> new SandbagBlock(Block.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5f)
                    .sound(SoundType.SAND)
                    .noOcclusion()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sandbag");

    public static final RegistryObject<Block> DOOR_METAL = registerBlock("door_metal",
            () -> new BlockModDoor(BlockModDoor.createDoorProperties(5.0F, 5.0F)),
            BlockModel.DOOR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "door_metal");

    public static final RegistryObject<Block> DOOR_OFFICE = registerBlock("door_office",
            () -> new BlockModDoor(BlockModDoor.createDoorProperties(10.0F, 10.0F)),
            BlockModel.DOOR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "door_office");

    public static final RegistryObject<Block> DOOR_BUNKER = registerBlock("door_bunker",
            () -> new BlockModDoor(BlockModDoor.createDoorProperties(10.0F, 100.0F)),
            BlockModel.DOOR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "door_bunker");

    public static final RegistryObject<Block> DOOR_RED = registerBlock("door_red",
            () -> new BlockModDoor(BlockModDoor.createDoorProperties(10.0F, 100.0F)),
            BlockModel.DOOR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "door_red");

    public static final RegistryObject<Block> SPOTLIGHT_INCANDESCENT = registerBlock("spotlight_incandescent",
            () -> new BlockSpotlight(2, HBMEnums.LightType.INCANDESCENT, true),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cage_lamp", "spotlight_incandescent"); // OBJ имя, текстура для particle

    public static final RegistryObject<Block> SPOTLIGHT_INCANDESCENT_OFF = registerBlock("spotlight_incandescent_off",
            () -> new BlockSpotlight(2, HBMEnums.LightType.INCANDESCENT, false),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cage_lamp", "spotlight_incandescent_off");

    public static final RegistryObject<Block> SPOTLIGHT_FLUORO = registerBlock("spotlight_fluoro",
            () -> new BlockSpotlight(8, HBMEnums.LightType.FLUORESCENT, true),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "fluorescent_lamp", "spotlight_fluoro");

    public static final RegistryObject<Block> SPOTLIGHT_FLUORO_OFF = registerBlock("spotlight_fluoro_off",
            () -> new BlockSpotlight(8, HBMEnums.LightType.FLUORESCENT, false),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "fluorescent_lamp", "spotlight_fluoro_off");

    public static final RegistryObject<Block> SPOTLIGHT_HALOGEN = registerBlock("spotlight_halogen",
            () -> new BlockSpotlight(32, HBMEnums.LightType.HALOGEN, true),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "flood_lamp", "spotlight_halogen");

    public static final RegistryObject<Block> SPOTLIGHT_HALOGEN_OFF = registerBlock("spotlight_halogen_off",
            () -> new BlockSpotlight(32, HBMEnums.LightType.HALOGEN, false),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "flood_lamp", "spotlight_halogen_off");

    public static final RegistryObject<Block> SPOTLIGHT_BEAM = registerBlock("spotlight_beam",
            BlockSpotlightBeam::new,
            BlockModel.AIR,
            "spotlight_beam");

    public static final RegistryObject<Block> FLOODLIGHT = registerBlock("floodlight",
            BlockFloodlight::new,
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "floodlight");

    public static final RegistryObject<Block> FLOODLIGHT_BEAM = registerBlock("machines/floodlight_beam",
            BlockFloodlightBeam::new,
            BlockModel.AIR,
            "machines/floodlight_beam");

    public static final RegistryObject<Block> CARGO_CONTAINER = registerBlock("storage/cargo_container",
            () -> new CargoContainer(CargoContainer.createProperties()),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/cargo_container");

    public static final RegistryObject<Block> MACHINE_GAS_CENT = registerBlock("machines/machine_gas_cent",
            () -> new MachineGasCent(MachineGasCent.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_gas_cent");

    public static final RegistryObject<Block> MACHINE_CENTRIFUGE = registerBlock("machines/machine_centrifuge",
            () -> new MachineCentrifuge(MachineCentrifuge.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_centrifuge");

    public static final RegistryObject<Block> PUMP_STEAM = registerBlock("machines/pump_steam",
            () -> new MachinePump(MachinePump.createProperties(), false),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/pump", "machines/pump_steam");

    public static final RegistryObject<Block> PUMP_ELECTRIC = registerBlock("machines/pump_electric",
            () -> new MachinePump(MachinePump.createProperties(), true),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/pump", "machines/pump_electric");

    public static final RegistryObject<Block> CONDUIT = registerBlock("storage/conduit",
            () -> new ConduitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(2f, 6f)
                    .noOcclusion()
                    .noLootTable()),
            BlockModel.STATIC_OBJ,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/conduit");

    public static final RegistryObject<Block> RED_CONNECTOR =
            registerBlock("network/red_connector",
                    () -> new ConnectorRedWire(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(5.0F, 10.0F)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/red_connector");

    public static final RegistryObject<Block> MACHINE_ROTARY_FURNACE = registerBlock("machines/rotary_furnace",
            () -> new MachineRotaryFurnace(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/rotary_furnace");

    public static final RegistryObject<Block> STONE_RESOURCE = registerBlock("stone_resource",
            () -> new BlockResourceStone(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(5.0F, 10.0F)
                    .requiresCorrectToolForDrops()),
            BlockModel.ENUM_BLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "stone_resource",
            BlockResourceStone.EnumStoneType.class);

    public static final RegistryObject<Block> FOUNDRY_MOLD = registerBlock("network/foundry_mold",
            () -> new FoundryMold(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/foundry_mold");

    public static final RegistryObject<Block> FOUNDRY_BASIN = registerBlock("network/foundry_basin",
            () -> new FoundryBasin(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/foundry_basin");

    public static final RegistryObject<Block> FOUNDRY_CHANNEL = registerBlock("network/foundry_channel",
            () -> new FoundryChannel(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/foundry_channel");

    public static final RegistryObject<Block> FOUNDRY_TANK = registerBlock("network/foundry_tank",
            () -> new FoundryTank(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/foundry_basin");

    public static final RegistryObject<Block> FOUNDRY_OUTLET = registerBlock("network/foundry_outlet",
            () -> new FoundryOutlet(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/foundry_outlet");

    public static final RegistryObject<Block> FOUNDRY_SLAGTAP = registerBlock("network/foundry_slagtap",
            () -> new FoundrySlagtap(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/foundry_outlet");

    public static final RegistryObject<Block> SLAG = registerBlock("network/slag",
            () -> new BlockDynamicSlag(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "network/slag");

    public static final RegistryObject<Block> EMP_BOMB = registerBlock("emp_bomb",
            () -> new BombFloat(BombFloat.createEMPProperties()),
            BlockModel.CUBE_TOP_BOTTOM, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "emp_bomb_side","emp_bomb_top", "emp_bomb_top");

    public static final RegistryObject<Block> FLOAT_BOMB = registerBlock("float_bomb",
            () -> new BombFloat(BombFloat.createFloatProperties()),
            BlockModel.CUBE_TOP_BOTTOM, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "float_bomb_side", "float_bomb_top", "float_bomb_top");

    public static final RegistryObject<Block> NUKE_N2 = registerBlock("bomb/n2",
            () -> new NukeN2(NukeN2.createProperties()),
            BlockModel.STATIC_OBJ, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "bomb/n2");

    public static final RegistryObject<Block> LAMP_DEMON = registerBlock("machines/lamp_demon",
            () -> new DemonLamp(DemonLamp.createProperties()),
            BlockModel.STATIC_OBJ, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "machines/lamp_demon");

    public static final RegistryObject<Block> DET_CHARGE = registerBlock("det_charge",
            () -> new ExplosiveCharge(ExplosiveCharge.createChargeProperties()),
            BlockModel.CUBE_ALL, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "det_charge");

    public static final RegistryObject<Block> DET_NUKE = registerBlock("det_nuke",
            () -> new ExplosiveCharge(ExplosiveCharge.createNukeProperties()),
            BlockModel.CUBE_TOP_BOTTOM, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "det_nuke_side", "det_nuke_top", "det_nuke_top");

    public static final RegistryObject<Block> BOXCAR = registerBlock("boxcar",
            () -> new DecoBlock(DecoBlock.createProperties()),
            BlockModel.STATIC_OBJ, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "boxcar");

    public static final RegistryObject<Block> BOAT = registerBlock("boat",
            () -> new DecoBlock(DecoBlock.createProperties()),
            BlockModel.STATIC_OBJ, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "boat");

    public static final RegistryObject<Block> STEEL_WALL = registerBlock("steel_wall",
            () -> new DecoBlock(DecoBlock.createProperties()),
            BlockModel.MODEL_ITEM_STATIC, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "steel_wall");

    public static final RegistryObject<Block> STEEL_CORNER = registerBlock("steel_corner",
            () -> new DecoBlock(DecoBlock.createProperties()),
            BlockModel.MODEL_ITEM_STATIC, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "steel_corner");

    public static final RegistryObject<Block> STEEL_ROOF = registerBlock("steel_roof",
            () -> new DecoBlock(DecoBlock.createProperties()),
            BlockModel.MODEL_ITEM_STATIC, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "steel_roof");

    public static final RegistryObject<Block> FOAM_LAYER = registerBlock("foam_layer",
            () -> new BlockLayering(BlockLayering.createFoamProperties()),
            BlockModel.LAYERED, LootInfo.self(), ToolInfo.shovel(ToolInfo.ToolLevel.WOOD), "foam");

    public static final RegistryObject<Block> SAND_BORON_LAYER = registerBlock("sand_boron_layer",
            () -> new BlockLayering(BlockLayering.createSandProperties()),
            BlockModel.LAYERED, LootInfo.self(), ToolInfo.shovel(ToolInfo.ToolLevel.WOOD), "sand_boron");

    public static final RegistryObject<Block> BLOCK_FOAM = registerBlock("block_foam",
            () -> new Block(ModBlockProperties.ORE_PROPERTIES().noLootTable()),
            BlockModel.CUBE_ALL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "ore_aluminium");

    public static final RegistryObject<Block> SAND_BORON = registerBlock("sand_boron",
            () -> new FallingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5F)
                    .sound(SoundType.SAND)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "sand_boron");

    public static final RegistryObject<Block> TURRET_SENTRY = registerBlock("turrets/turret_sentry",
            () -> new TurretSentry(TurretSentry.createProperties()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "turrets/turret_sentry");

    public static final RegistryObject<Block> TURRET_SENTRY_DAMAGED = registerBlock("turrets/turret_sentry_damaged",
            () -> new TurretSentryDamaged(TurretSentryDamaged.createProperties()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "turrets/turret_sentry");

    public static final RegistryObject<Block> MACHINE_TRANSFORMER = registerBlock("machine_transformer",
            () -> new MachineTransformer(MachineTransformer.createProperties(), 10000L, 1),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "machine_transformer", "machine_transformer_top", "machine_transformer_top");

    public static final RegistryObject<Block> MACHINE_TRANSFORMER_DNT = registerBlock("machine_transformer_dnt",
            () -> new MachineTransformer(MachineTransformer.createProperties(), 1000000000000000L, 1),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.IRON),
            "machine_transformer_iron", "machine_transformer_iron_top", "machine_transformer_iron_top");

    public static final RegistryObject<Block> STONE_DEPTH = registerBlock("stone_depth",
            () -> new BlockDepth(BlockDepth.createProperties()),
            BlockModel.CUBE_ALL, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.NONE), "stone_depth");

    public static final RegistryObject<Block> STONE_DEPTH_NETHER = registerBlock("stone_depth_nether",
            () -> new BlockDepth(BlockDepth.createProperties()),
            BlockModel.CUBE_ALL, LootInfo.self(), ToolInfo.pickaxe(ToolInfo.ToolLevel.NONE), "stone_depth_nether");

    public static final RegistryObject<Block> CABLE_SWITCH = registerBlock("cable_switch",
            () -> new CableSwitch(CableSwitch.createProperties()),
            BlockModel.CUBE_ALL_SWITCH,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "cable_switch_off", "cable_switch_on");

    public static final RegistryObject<Block> CRASHED_BALEFIRE = registerBlock("bomb/crashed_balefire",
            () -> new BlockCrashedBomb(BlockCrashedBomb.createProperties()),
            BlockModel.ENUM_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.NONE),
            "bomb/dud",
            BlockCrashedBomb.EnumDudType.class);

    public static final RegistryObject<Block> MACHINE_SHREDDER = registerBlock("machines/machine_shredder",
            () -> new MachineShredder(MachineShredder.createProperties()),
            BlockModel.CUBE_TOP_BOTTOM, LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON), "machines/machine_shredder_side", "machines/machine_shredder_top", "machines/machine_shredder_bottom");

    public static final RegistryObject<Block> BLOCK_METEOR = registerBlock("block_meteor",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_meteor");

    public static final RegistryObject<Block> METEOR_POLISHED = registerBlock("meteor_polished",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "meteor_polished");

    public static final RegistryObject<Block> METEOR_BRICK = registerBlock("meteor_brick",
            () -> new Block(ModBlockProperties.METAL_BLOCK_PROPERTIES()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "meteor_brick");

    public static final RegistryObject<Block> STEEL_CHAIN = registerBlock("steel_chain",
            () -> new ChainBlock(Block.Properties.of().strength(0.5F, 3.0F).noOcclusion()),
            BlockModel.CROSS,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "steel_chain");

    public static final RegistryObject<Block> CRATE = registerBlock("storage/crate",
            () -> new BlockCrateSimple(BlockCrateSimple.createPropertiesWood(), BlockCrateSimple.CrateType.SUPPLY),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> CRATE_WEAPON = registerBlock("storage/crate_weapon",
            () -> new BlockCrateSimple(BlockCrateSimple.createPropertiesWood(), BlockCrateSimple.CrateType.WEAPON),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> CRATE_CAN = registerBlock("storage/crate_can",
            BlockCanCrate::new,
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/crate_can_side", "storage/crate_can_top", "storage/crate_can_bottom");

    public static final RegistryObject<Block> CRATE_AMMO = registerBlock("storage/crate_ammo",
            () -> new BlockAmmoCrate(BlockAmmoCrate.createProperties()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.WOOD),
            "storage/crate_ammo_side", "storage/crate_ammo_top", "storage/crate_ammo_bottom");

    public static final RegistryObject<Block> CRATE_LEAD = registerBlock("storage/crate_lead",
            () -> new BlockCrateSimple(BlockCrateSimple.createPropertiesMetal(), BlockCrateSimple.CrateType.LEAD),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> CRATE_METAL = registerBlock("storage/crate_metal",
            () -> new BlockCrateSimple(BlockCrateSimple.createPropertiesMetal(), BlockCrateSimple.CrateType.METAL),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> CRATE_RED = registerBlock("storage/crate_red",
            () -> new BlockCrateSimple(BlockCrateSimple.createPropertiesMetal(), BlockCrateSimple.CrateType.RED),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> CRATE_JUNGLE = registerBlock("storage/crate_jungle",
            () -> new BlockCrateSimple(BlockCrateSimple.createPropertiesWood(), BlockCrateSimple.CrateType.JUNGLE),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    // Ящики-хранилища (с TE)
    public static final RegistryObject<Block> CRATE_IRON = registerBlock("storage/crate_iron",
            () -> new BlockStorageCrate(BlockStorageCrate.createPropertiesMetal()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/crate_iron_side", "storage/crate_iron_top", "storage/crate_iron_top");

    public static final RegistryObject<Block> CRATE_STEEL = registerBlock("storage/crate_steel",
            () -> new BlockStorageCrate(BlockStorageCrate.createPropertiesMetal()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/crate_steel_side", "storage/crate_steel_top", "storage/crate_steel_top");

    public static final RegistryObject<Block> CRATE_DESH = registerBlock("storage/crate_desh",
            () -> new BlockStorageCrate(BlockStorageCrate.createPropertiesMetal()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/crate_desh_side", "storage/crate_desh_top", "storage/crate_desh_top");

    public static final RegistryObject<Block> CRATE_TUNGSTEN = registerBlock("storage/crate_tungsten",
            () -> new BlockStorageCrateTungsten(BlockStorageCrate.createPropertiesTungsten()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/crate_tungsten_side", "storage/crate_tungsten_top", "storage/crate_tungsten_top");

    public static final RegistryObject<Block> CRATE_TEMPLATE = registerBlock("storage/crate_template",
            () -> new BlockStorageCrate(BlockStorageCrate.createPropertiesMetal()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> SAFE = registerBlock("storage/safe",
            () -> new BlockStorageCrate(BlockStorageCrate.createPropertiesSafe()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "storage/safe_side", "storage/safe_front", "storage/safe_front");

    public static final RegistryObject<Block> MUSH_BLOCK = registerBlock("mush_block",
            () -> new BlockStorageCrate(BlockStorageCrate.createPropertiesMetal()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> MUSH_BLOCK_STEM = registerBlock("mush_block_stem",
            () -> new BlockStorageCrate(BlockStorageCrate.createPropertiesMetal()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON));

    public static final RegistryObject<Block> BLOCK_FALLOUT = registerBlock("block_fallout",
            () -> new BlockHazardFalling(BlockHazardFalling.createProperties(MapColor.SAND)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.STONE),
            "ash");

    public static final RegistryObject<Block> GLASS_BORON = registerBlock("glass_boron",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.COLOR_CYAN, 0.3F), false),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_boron");

    public static final RegistryObject<Block> GLASS_LEAD = registerBlock("glass_lead",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.COLOR_GRAY, 0.3F), false),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_lead");

    public static final RegistryObject<Block> GLASS_URANIUM = registerBlock("glass_uranium",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.COLOR_GREEN, 0.3F), false, 5),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_uranium");

    public static final RegistryObject<Block> GLASS_TRINITITE = registerBlock("glass_trinitite",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.COLOR_LIGHT_GREEN, 0.3F), false, 5),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_trinitite");

    public static final RegistryObject<Block> GLASS_POLONIUM = registerBlock("glass_polonium",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.COLOR_PURPLE, 0.3F), false, 5),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_polonium");

    public static final RegistryObject<Block> GLASS_ASH = registerBlock("glass_ash",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.COLOR_GRAY, 3.0F), false),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_ash");

    public static final RegistryObject<Block> GLASS_QUARTZ = registerBlock("glass_quartz",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.QUARTZ, 1.0F, 40.0F, SoundType.GLASS), false),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_quartz");

    public static final RegistryObject<Block> GLASS_POLARIZED = registerBlock("glass_polarized",
            () -> new BlockNTMGlass(BlockNTMGlass.createProperties(MapColor.COLOR_BLACK, 0.3F), false),
            BlockModel.CUBE_ALL_TRANSLUCENT,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "glass_polarized");

    public static final RegistryObject<Block> BLOCK_METEOR_TREASURE = registerBlock("block_meteor_treasure",
            () -> new BlockMeteoriteTreasure(BlockBehaviour.Properties.of().noLootTable()),
            BlockModel.CUBE_ALL,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_meteor_treasure");

    public static final RegistryObject<Block> BLOCK_GRAPHITE = registerBlock("block_graphite",
            () -> new BlockGraphite(BlockGraphite.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite");

    public static final RegistryObject<Block> BLOCK_GRAPHITE_DRILLED = registerBlock("block_graphite_drilled",
            () -> new BlockGraphiteDrilled(BlockGraphiteDrilled.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_drilled");

    // Графитовые блоки для сваи
    public static final RegistryObject<Block> BLOCK_GRAPHITE_FUEL = registerBlock("block_graphite_fuel",
            () -> new BlockGraphiteFuel(BlockGraphiteFuel.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_fuel");

    public static final RegistryObject<Block> BLOCK_GRAPHITE_PLUTONIUM = registerBlock("block_graphite_plutonium",
            () -> new BlockGraphiteSource(BlockGraphiteSource.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_plutonium");

    public static final RegistryObject<Block> BLOCK_GRAPHITE_ROD = registerBlock("block_graphite_rod",
            () -> new BlockGraphiteRod(BlockGraphiteRod.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_rod");

    public static final RegistryObject<Block> BLOCK_GRAPHITE_SOURCE = registerBlock("block_graphite_source",
            () -> new BlockGraphiteSource(BlockGraphiteSource.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_source");

    public static final RegistryObject<Block> BLOCK_GRAPHITE_LITHIUM = registerBlock("block_graphite_lithium",
            () -> new BlockGraphiteBreedingFuel(BlockGraphiteBreedingFuel.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_lithium");

    public static final RegistryObject<Block> BLOCK_GRAPHITE_TRITIUM = registerBlock("block_graphite_tritium",
            () -> new BlockGraphiteBreedingProduct(BlockGraphiteBreedingProduct.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_tritium");

    public static final RegistryObject<Block> BLOCK_GRAPHITE_DETECTOR = registerBlock("block_graphite_detector",
            () -> new BlockGraphiteNeutronDetector(BlockGraphiteNeutronDetector.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "block_graphite_detector");

    public static final RegistryObject<Block> DECO_CRT = registerBlock("deco_crt",
            BlockDecoCRT::new,
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "deco_crt");

    public static final RegistryObject<Block> SAT_MAPPER = registerBlock("sat/sat_mapper",
            () -> new DecoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sat/sat_mapper");

    public static final RegistryObject<Block> SAT_RADAR = registerBlock("sat/sat_radar",
            () -> new DecoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sat/sat_radar");

    public static final RegistryObject<Block> SAT_SCANNER = registerBlock("sat/sat_scanner",
            () -> new DecoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sat/sat_scanner");

    public static final RegistryObject<Block> SAT_LASER = registerBlock("sat/sat_laser",
            () -> new DecoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sat/sat_laser");

    public static final RegistryObject<Block> SAT_FOEQ = registerBlock("sat/sat_foeq",
            () -> new DecoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sat/sat_foeq");

    public static final RegistryObject<Block> SAT_RESONATOR = registerBlock("sat/sat_resonator",
            () -> new DecoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sat/sat_resonator");

    public static final RegistryObject<Block> SAT_DOCK = registerBlock("sat/sat_dock",
            () -> new MachineSatDock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "sat/sat_dock");

    public static final RegistryObject<Block> TEKTITE = registerBlock("tektite",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5F)
                    .sound(SoundType.SAND)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.WOOD),
            "tektite");

    public static final RegistryObject<Block> ORE_TEKTITE_OSMIRIDIUM = registerBlock("ore_tektite_osmiridium",
            () -> new BlockGeneric(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.5F)
                    .sound(SoundType.SAND)
                    .requiresCorrectToolForDrops()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.WOOD),
            "ore_tektite_osmiridium");

    public static final RegistryObject<Block> NTM_DIRT = registerBlock("ntm_dirt",
            BlockNTMDirt::new,
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.shovel(ToolInfo.ToolLevel.WOOD),
            "ntm_dirt");

    public static final RegistryObject<Block> PRIBRIS = registerBlock("rbmk/rbmk_debris",
            RBMKDebris::new,
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "rbmk/rbmk_debris");

    public static final RegistryObject<Block> ZIRNOX_DESTROYED = registerBlock("zirnox_destroyed",
            () -> new ZirnoxDestroyed(ZirnoxDestroyed.createProperties()),
            BlockModel.MULTIBLOCK,
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "zirnox_destroyed");

    public static final RegistryObject<Block> CAPACITOR_BUS = registerBlock("capacitor_bus",
            () -> new MachineCapacitorBus(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "capacitor_bus_side", "capacitor_bus", "capacitor_bus");

    public static final RegistryObject<Block> CAPACITOR_COPPER = registerBlock("capacitor_copper",
            () -> new MachineCapacitor(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL), 1_000_000L),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "capacitor", "capacitor_copper_side");

    public static final RegistryObject<Block> CAPACITOR_GOLD = registerBlock("capacitor_gold",
            () -> new MachineCapacitor(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL), 5_000_000L),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "capacitor", "capacitor_gold_side");

    public static final RegistryObject<Block> CAPACITOR_NIOBIUM = registerBlock("capacitor_niobium",
            () -> new MachineCapacitor(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL), 25_000_000L),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "capacitor", "capacitor_niobium_side");

    public static final RegistryObject<Block> CAPACITOR_TANTALIUM = registerBlock("capacitor_tantalium",
            () -> new MachineCapacitor(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL), 150_000_000L),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "capacitor", "capacitor_tantalium_side");

    public static final RegistryObject<Block> CAPACITOR_SCHRABIDATE = registerBlock("capacitor_schrabidate",
            () -> new MachineCapacitor(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL), 50_000_000_000L),
            BlockModel.OBJ_MODEL_FULL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "capacitor", "capacitor_schrabidate_side");

    public static final RegistryObject<Block> TESLA = registerBlock("tesla",
            () -> new MachineTesla(MachineTesla.createProperties()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "tesla");

    public static final RegistryObject<Block> METEOR_BATTERY = registerBlock("meteor_battery",
            () -> new BlockPillar(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(15.0F, 360.0F)
                    .sound(SoundType.STONE)),
            BlockModel.PILLAR,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "meteor_spawner_side", "meteor_power");

    public static final RegistryObject<Block> PINK_LOG = registerBlock("pink_log",
            () -> new BlockPinkLog(BlockPinkLog.createProperties()),
            BlockModel.PILLAR,
            LootInfo.self(),
            ToolInfo.axe(ToolInfo.ToolLevel.WOOD),
            "oak_log", "pink_log");

    public static final RegistryObject<Block> DECO_LOOT = registerBlock("deco_loot",
            () -> new BlockLoot(BlockLoot.createProperties()),
            BlockModel.AIR,
            ToolInfo.none());

    public static final RegistryObject<Block> LAUNCH_PAD = registerBlock("launch_pad",
            () -> new LaunchPad(Block.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "launch_pad");

    public static final RegistryObject<Block> MACHINE_RADAR = registerBlock("machines/machine_radar",
            () -> new MachineRadar(MachineRadar.createProperties()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_radar");

    public static final RegistryObject<Block> RADAR_SCREEN = registerBlock("machines/radar_screen",
            () -> new MachineRadarScreen(MachineRadarScreen.createProperties()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/radar_screen");

    public static final RegistryObject<Block> MACHINE_TELEPORTER = registerBlock("machines/machine_teleporter",
            () -> new MachineTeleporter(MachineTeleporter.createProperties()),
            BlockModel.CUBE_TOP_BOTTOM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_teleporter_side", "machines/machine_teleporter_top", "machines/machine_teleporter_bottom");

    public static final RegistryObject<Block> GEIGER = registerBlock("geiger",
            () -> new GeigerCounter(GeigerCounter.createProperties()),
            BlockModel.MODEL_ITEM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "geiger");

    public static final RegistryObject<Block> SOLAR_MIRROR = registerBlock("machines/solar_mirror",
            () -> new SolarMirror(SolarMirror.createProperties()),
            BlockModel.STATIC_OBJ,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/solar_mirror");

    public static final RegistryObject<Block> MACHINE_SOLAR_BOILER = registerBlock("machines/machine_solar_boiler",
            () -> new MachineSolarBoiler(MachineSolarBoiler.createProperties()),
            BlockModel.MULTIBLOCK,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/machine_solar_boiler");

    public static final RegistryObject<Block> REBAR = registerBlock("rebar",
            () -> new BlockRebar(BlockRebar.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "rebar");

    public static final RegistryObject<Block> PLANT_FLOWER = registerBlock("plant_flower",
            () -> new BlockNTMFlower(BlockNTMFlower.createProperties().noLootTable()),
            BlockModel.PLANT_ENUM,
            ToolInfo.none(),
            "plant_flower", BlockNTMFlower.EnumFlowerType.class);

    public static final RegistryObject<Block> PLANT_DEAD = registerBlock("plant_dead",
            () -> new BlockDeadPlant(BlockDeadPlant.createProperties()),
            BlockModel.PLANT_ENUM,
            LootInfo.self(),
            ToolInfo.none(),
            "plant_dead", BlockDeadPlant.EnumDeadPlantType.class);

    public static final RegistryObject<Block> PLUSHIE = registerBlock("plushie",
            () -> new BlockPlushie(BlockPlushie.createProperties()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.none(),
            "plushie");

    public static final RegistryObject<Block> SNOWGLOBE = registerBlock("snowglobe",
            () -> new BlockSnowglobe(BlockSnowglobe.createProperties()),
            BlockModel.OBJ_MODEL_HORIZONTAL,
            LootInfo.self(),
            ToolInfo.none(),
            "snowglobe");

    public static final RegistryObject<Block> STRUCT_LAUNCHER = registerBlock("struct_launcher",
            () -> new BlockGeneric(BlockGeneric.createMetalProperties(MapColor.METAL, 5.0F, 10.0F)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "struct_launcher");

    public static final RegistryObject<Block> STRUCT_SCAFFOLD = registerBlock("struct_scaffold",
            () -> new BlockGeneric(BlockGeneric.createMetalProperties(MapColor.METAL, 5.0F, 10.0F)),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "struct_scaffold");

    public static final RegistryObject<Block> STRUCT_LAUNCHER_CORE = registerBlock("struct_launcher_core",
            () -> new BlockStruct(BlockStruct.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "struct_launcher_core");

    public static final RegistryObject<Block> STRUCT_LAUNCHER_CORE_LARGE = registerBlock("struct_launcher_core_large",
            () -> new BlockStruct(BlockStruct.createProperties()),
            BlockModel.CUBE_ALL,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "struct_launcher_core_large");

    public static final RegistryObject<Block> COMPACT_LAUNCHER = registerBlock("machines/compact_launcher",
            () -> new CompactLauncher(BlockGeneric.createMetalProperties(MapColor.METAL, 5.0F, 10.0F)),
            BlockModel.MULTIBLOCK,
            LootInfo.item(ModItems.STRUCT_LAUNCHER_CORE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/compact_launcher");

    public static final RegistryObject<Block> LAUNCH_TABLE = registerBlock("machines/launch_table",
            () -> new LaunchTable(BlockGeneric.createMetalProperties(MapColor.METAL, 5.0F, 10.0F)),
            BlockModel.MULTIBLOCK_NO_ITEM,
            LootInfo.item(ModItems.STRUCT_LAUNCHER_CORE_LARGE, 1, 1),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/launch_table");

    public static final RegistryObject<Block> LAUNCH_PAD_RUSTED = registerBlock("machines/launch_pad_rusted",
            () -> new LaunchPadRusted(BlockGeneric.createMetalProperties(MapColor.METAL, 5.0F, 10.0F)),
            BlockModel.MULTIBLOCK_NO_ITEM,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "machines/launch_pad_rusted");

    public static final RegistryObject<Block> GEYSIR_WATER = registerBlock("geysir_water",
            () -> new BlockGeysir(BlockGeysir.createProperties(BlockGeysir.GeyserType.WATER), BlockGeysir.GeyserType.WATER),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "geysir_stone");

    public static final RegistryObject<Block> GEYSIR_CHLORINE = registerBlock("geysir_chlorine",
            () -> new BlockGeysir(BlockGeysir.createProperties(BlockGeysir.GeyserType.CHLORINE), BlockGeysir.GeyserType.CHLORINE),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "geysir_stone");

    public static final RegistryObject<Block> GEYSIR_VAPOR = registerBlock("geysir_vapor",
            () -> new BlockGeysir(BlockGeysir.createProperties(BlockGeysir.GeyserType.VAPOR), BlockGeysir.GeyserType.VAPOR),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "geysir_stone");

    public static final RegistryObject<Block> GEYSIR_NETHER = registerBlock("geysir_nether",
            () -> new BlockGeysir(BlockGeysir.createProperties(BlockGeysir.GeyserType.NETHER), BlockGeysir.GeyserType.NETHER),
            BlockModel.CUBE_ALL,
            ToolInfo.none(),
            "geysir_nether");

    public static final RegistryObject<Block> MACHINE_BATTERY = registerBlock("machine_battery",
            () -> new MachineBattery(MachineBattery.createProperties(), 1_000_000L),
            BlockModel.CUBE_FRONT_SIDE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "battery_front", "battery_side", "battery_top");

    public static final RegistryObject<Block> MACHINE_BATTERY_POTATO = registerBlock("machine_battery_potato",
            () -> new MachineBattery(MachineBattery.createProperties(), 10_000L),
            BlockModel.CUBE_FRONT_SIDE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "battery_potato_front", "battery_potato_side", "battery_potato_top");

    public static final RegistryObject<Block> MACHINE_LITHIUM_BATTERY = registerBlock("machine_lithium_battery",
            () -> new MachineBattery(MachineBattery.createProperties(), 50_000_000L),
            BlockModel.CUBE_FRONT_SIDE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "battery_lithium_front", "battery_lithium_side", "battery_lithium_top");

    public static final RegistryObject<Block> MACHINE_SCHRABIDIUM_BATTERY = registerBlock("machine_schrabidium_battery",
            () -> new MachineBattery(MachineBattery.createProperties(), 25_000_000_000L),
            BlockModel.CUBE_FRONT_SIDE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "battery_schrabidium_front", "battery_schrabidium_side", "battery_schrabidium_top");

    public static final RegistryObject<Block> MACHINE_DINEUTRONIUM_BATTERY = registerBlock("machine_dineutronium_battery",
            () -> new MachineBattery(MachineBattery.createProperties(), 1_000_000_000_000L),
            BlockModel.CUBE_FRONT_SIDE,
            LootInfo.self(),
            ToolInfo.pickaxe(ToolInfo.ToolLevel.IRON),
            "battery_dineutronium_front", "battery_dineutronium_side", "battery_dineutronium_top");

}