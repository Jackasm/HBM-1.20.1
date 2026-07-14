package com.hbm.datagen.models;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModelProvider implements DataProvider {

    private final PackOutput packOutput;

    public ModelProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        BlockModelGenerator generator = new BlockModelGenerator(packOutput);
        ItemModelGenerator itemGen = new ItemModelGenerator(packOutput);

        for (var entry : ModBlocks.BLOCK_MODELS.entrySet()) {
            RegistryObject<Block> blockReg = entry.getKey();
            Block block = blockReg.get();
            BlockModel type = entry.getValue();
            Object[] data = ModBlocks.MODEL_DATA.getOrDefault(blockReg, new Object[0]);

            switch (type) {
                case AIR -> generator.generateAir(block);
                case MULTIBLOCK -> {
                    String objPath = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    String texturePath = data.length > 1 ? (String) data[1] : objPath;
                    generator.generateMultiblock(block, objPath, texturePath);
                }
                case MULTIBLOCK_NO_ITEM -> {
                    String objPath = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    String texturePath = data.length > 1 ? (String) data[1] : objPath;
                    generator.generateMultiblock(block, objPath, texturePath, true);
                }
                case MODEL_ITEM -> {
                    String particleTex = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateModelItem(block, particleTex);
                }
                case MODEL_ITEM_STATIC -> {
                    String particleTex = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateModelItemStatic(block, particleTex);
                }
                case ENTITYBLOCK_ANIMATED -> generator.generateEntityBlockAnimated(block);
                case CUBE_ALL -> {
                    String texture = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateCubeAll(new Object[][]{{block, texture}});
                }
                case CUBE_FRONT_SIDE -> {
                    String frontTex = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath() + "_front";
                    String sideTex = data.length > 1 ? (String) data[1] : Objects.requireNonNull(blockReg.getId()).getPath() + "_side";
                    String topTex = data.length > 2 ? (String) data[2] : Objects.requireNonNull(blockReg.getId()).getPath() + "_top";
                    String bottomTex = data.length > 3 ? (String) data[3] : sideTex;
                    generator.generateCubeFrontSide(block, frontTex, sideTex, topTex, bottomTex);
                }
                case CUBE_ALL_TRANSLUCENT -> {
                    String texture = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateCubeAllTranslucent(new Object[][]{{block, texture}});
                }
                case CUBE_ALL_SWITCH -> {
                    String offTex = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath() + "_off";
                    String onTex = data.length > 1 ? (String) data[1] : Objects.requireNonNull(blockReg.getId()).getPath() + "_on";
                    generator.generateCubeAllSwitch(block, offTex, onTex);
                }
                case BLAST_FURNACE -> {
                    String side = data.length > 0 ? (String) data[0] : "blast_furnace_side_alt";
                    String topOff = data.length > 1 ? (String) data[1] : "blast_furnace_top_off_alt";
                    String frontOff = data.length > 2 ? (String) data[2] : "blast_furnace_front_off_alt";
                    String topOn = data.length > 3 ? (String) data[3] : "blast_furnace_top_on_alt";
                    String frontOn = data.length > 4 ? (String) data[4] : "blast_furnace_front_on_alt";
                    generator.generateBlastFurnace(block, side, topOff, frontOff, topOn, frontOn);
                }
                case LAYERED -> {
                    String texture = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateLayeredBlock(block, texture);
                }
                case ORE -> {
                    String oreTex = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    String baseTex = data.length > 1 ? (String) data[1] : "stone";
                    generator.generateOre(block, oreTex, baseTex);
                }
                case CROSS -> {
                    String texture = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateCross(block, texture);
                }
                case DECO_CT -> {
                    String texture = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateDecoCT(block, texture);
                }
                case CUBE_TOP_BOTTOM -> {
                    String sideTex = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath() + "_side";
                    String topTex = data.length > 1 ? (String) data[1] : Objects.requireNonNull(blockReg.getId()).getPath() + "_top";
                    String bottomTex = data.length > 2 ? (String) data[2] : Objects.requireNonNull(blockReg.getId()).getPath() + "_bottom";
                    generator.generateCubeTopBottom(block, sideTex, topTex, bottomTex);
                }
                case OBJ_MODEL_FULL -> {
                    String objName = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    String textureName = data.length > 1 ? (String) data[1] : objName;
                    generator.generateObjBlocksFullDirections(new Object[][]{{block, objName, textureName}});
                }
                case OBJ_MODEL_HORIZONTAL -> {
                    String objName = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    String textureName = data.length > 1 ? (String) data[1] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateObjBlocksHorizontalDirections(new Object[][]{{block, objName, textureName}});
                }
                case ENUM_OBJ -> {
                    String objPrefix = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    Class<? extends Enum<?>> enumClass;
                    if (data.length > 1 && data[1] instanceof Class<?> clazz && clazz.isEnum()) {
                        enumClass = (Class<? extends Enum<?>>) clazz;
                    } else {
                        enumClass = (Class<? extends Enum<?>>) data[0];
                    }
                    generator.generateEnumObj(block, objPrefix, enumClass);
                }
                case STATIC_OBJ -> {
                    String objName = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateStaticObjBlock(block, objName);
                }
                case DOOR -> {
                    String texName = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateDoor(block, texName);
                }
                case GRATE -> {
                    String objPath = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    generator.generateGrate(block, objPath);
                }
                case PILLAR -> {
                    String sideTex = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath() + "_side";
                    String endTex = data.length > 1 ? (String) data[1] : Objects.requireNonNull(blockReg.getId()).getPath() + "_top";
                    generator.generateCubeColumn(block, sideTex, endTex);
                }
                case ENUM_BLOCK -> {
                    String texturePrefix = Objects.requireNonNull(blockReg.getId()).getPath();
                    Class<? extends Enum<?>> enumClass;

                    if (data.length >= 2 && data[0] instanceof String str && data[1] instanceof Class<?> clazz && clazz.isEnum()) {
                        texturePrefix = str;
                        enumClass = (Class<? extends Enum<?>>) clazz;
                    } else {
                        enumClass = (Class<? extends Enum<?>>) data[0];
                    }

                    generator.generateEnumBlockWithItems(block, texturePrefix, enumClass);
                }
                case ENUM_PILLAR -> {
                    String texturePrefix = Objects.requireNonNull(blockReg.getId()).getPath();
                    String[] types = new String[]{"default"};

                    if (data.length > 0) {
                        if (data[0] instanceof String) {
                            texturePrefix = (String) data[0];
                        }
                        if (data.length > 1 && data[1] instanceof String[]) {
                            types = (String[]) data[1];
                        }
                    }
                    generator.generateEnumColumnWithItems(block, texturePrefix, types);
                }
                case PLANT_ENUM -> {
                    String texturePrefix = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    Class<? extends Enum<?>> enumClass;
                    if (data.length > 1 && data[1] instanceof Class<?> clazz && clazz.isEnum()) {
                        enumClass = (Class<? extends Enum<?>>) clazz;
                    } else {
                        enumClass = (Class<? extends Enum<?>>) data[0];
                    }
                    generator.generatePlantEnum(block, texturePrefix, enumClass);
                }
                case TALL_PLANT_ENUM -> {
                    String texturePrefix = data.length > 0 ? (String) data[0] : Objects.requireNonNull(blockReg.getId()).getPath();
                    Class<? extends Enum<?>> enumClass = getEnumClassFromData(data);
                    generator.generateTallPlant(block, texturePrefix, enumClass);
                }
                case BEDROCK_ORE -> {
                    String baseTex = data.length > 0 ? (String) data[0] : "minecraft:block/bedrock";
                    String overlayPrefix = data.length > 1 ? (String) data[1] : "ore_random_";
                    generator.generateBedrockOre(block, baseTex, overlayPrefix);
                }
                case CONCRETE_SUPER -> generator.generateConcreteSuper(block);
                case NONE -> {}
            }
        }

        processItemModels(ModItems.ITEM_MODELS, ModItems.ITEM_MODEL_DATA, itemGen);
        processItemModels(ModAmmoItems.AMMO_MODELS, ModAmmoItems.AMMO_MODEL_DATA, itemGen);
        processItemModels(ModToolItems.TOOLS_MODELS, ModToolItems.TOOLS_MODEL_DATA, itemGen);
        processItemModels(ModArmorItems.ARMOR_MODELS, ModArmorItems.ARMOR_MODEL_DATA, itemGen);

        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (BlockModelGenerator.JsonEntry entry : generator.getEntries()) {
            futures.add(DataProvider.saveStable(cache, entry.json(), entry.path()));
        }
        for (ItemModelGenerator.JsonEntry entry : itemGen.getEntries()) {
            futures.add(DataProvider.saveStable(cache, entry.json(), entry.path()));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private Class<? extends Enum<?>> getEnumClassFromData(Object[] data) {
        for (Object obj : data) {
            if (obj instanceof Class<?> clazz && clazz.isEnum()) {
                return (Class<? extends Enum<?>>) clazz;
            }
        }
        return null;
    }

    private void processItemModels(Map<RegistryObject<Item>, ItemModelType> itemModels,
                                   Map<RegistryObject<Item>, Object[]> modelData,
                                   ItemModelGenerator itemGen) {
        for (var entry : itemModels.entrySet()) {
            RegistryObject<net.minecraft.world.item.Item> itemReg = entry.getKey();
            String itemName = Objects.requireNonNull(itemReg.getId()).getPath();
            ItemModelType type = entry.getValue();
            Object[] data = modelData.getOrDefault(itemReg, new Object[0]);

            String textureName = itemName;

            if (type == ItemModelType.GENERATED_TINY) {
                if (itemName.startsWith("powder_tiny_")) {
                    textureName = itemName.replace("powder_tiny_", "powder_");
                }
            }

            if (data.length > 0 && data[0] instanceof String) {
                textureName = (String) data[0];
            }

            switch (type) {
                case NONE -> {
                    textureName = itemName;
                    if (data.length > 0 && data[0] instanceof String) {
                        textureName = (String) data[0];
                    }
                    itemGen.generateNoneWithTexture(itemName, textureName);
                }
                case GENERATED -> itemGen.generateGenerated(itemName, textureName);
                case GENERATED_TINY -> itemGen.generateGeneratedTiny(itemName, textureName);
                case HANDHELD -> itemGen.generateHandheld(itemName, textureName);
                case HANDHELD_ROD -> itemGen.generateHandheldRod(itemName, textureName);
                case SPAWN_EGG -> itemGen.generateSpawnEgg(itemName);
                case BUILTIN_ENTITY -> itemGen.generateBuiltinEntity(itemName);
                case OBJ_ITEM -> {
                    String objPath = data.length > 0 ? data[0] + ".obj" : "models/item/missile_part" + itemName + ".obj";
                    itemGen.generateObjItem(itemName, objPath);
                }
                case ENUM_ITEM -> {
                    if (data.length > 0 && data[0] instanceof Class<?>) {
                        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) data[0];
                        itemGen.generateEnumItemFromEnum(itemName, enumClass);
                    } else if (data.length > 0 && data[0] instanceof String[] types) {
                        String baseTexture = data.length > 1 && data[1] instanceof String ? (String) data[1] : itemName;
                        itemGen.generateEnumItem(itemName, types, baseTexture);
                    }
                }
                case ENUM_ITEM_TINTED -> {
                    // data[0] - baseTexture (опционально), data[1] - overlayTexture (опционально)
                    String baseTexture = data.length > 0 && data[0] instanceof String ? (String) data[0] : itemName;
                    String overlayTexture = data.length > 1 && data[1] instanceof String ? (String) data[1] : "ore_overlay";
                    itemGen.generateEnumItemTinted(itemName, baseTexture, overlayTexture);
                }
                case BEDROCK_ORE_ITEM -> {
                    String baseTexture = data.length > 0 ? (String) data[0] : "minecraft:block/bedrock";
                    String overlayPrefix = data.length > 1 ? (String) data[1] : "hbm:block/ore_random_";
                    itemGen.generateBedrockOreItem(itemName, baseTexture, overlayPrefix);
                }
                case GENERATED_LAYERED -> {
                    String baseTexture = null;
                    if (data[0] instanceof String) {
                        baseTexture = data.length > 0 ? (String) data[0] : itemName + "_base";
                    }
                    String overlayTexture = data.length > 1 ? (String) data[1] : itemName + "_overlay";
                    itemGen.generateGeneratedLayered(itemName, baseTexture, overlayTexture);
                }
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "HBM Block Models";
    }
}