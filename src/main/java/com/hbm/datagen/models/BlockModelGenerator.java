package com.hbm.datagen.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbm.util.EnumUtil;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockModelGenerator {

    private final PackOutput packOutput;
    private final List<JsonEntry> entries = new ArrayList<>();

    public record JsonEntry(JsonObject json, Path path) {}

    public BlockModelGenerator(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    public List<JsonEntry> getEntries() {
        return entries;
    }

    public void generateObjBlocksFullDirections(Object[][] blocks) {
        String[] directions = {"north", "east", "south", "west", "up", "down"};
        int[] yRot = {0, 90, 180, 270, 0, 0};
        int[] xRot = {0, 0, 0, 0, 270, 90};

        for (Object[] pair : blocks) {
            Block block = (Block) pair[0];
            String objName = (String) pair[1];
            String name = getRegistryName(block);
            // Третий параметр - текстура для particle (если есть, иначе используем имя блока)
            String textureName = pair.length > 2 ? (String) pair[2] : name;

            // Blockstate с учётом facing
            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();

            for (int i = 0; i < directions.length; i++) {
                String dir = directions[i];
                JsonObject variant = new JsonObject();
                variant.addProperty("model", "hbm:block/" + name);
                if (yRot[i] != 0) variant.addProperty("y", yRot[i]);
                if (xRot[i] != 0) variant.addProperty("x", xRot[i]);
                variants.add("facing=" + dir, variant);
            }
            blockstate.add("variants", variants);
            saveBlockstate(block, blockstate);

            // Block model (заглушка с particle)
            JsonObject blockModel = new JsonObject();
            blockModel.addProperty("parent", "block/cube_all");
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", "hbm:block/" + textureName); // используем textureName
            blockModel.add("textures", textures);
            saveBlockModel(block, blockModel);

            // Item model (OBJ)
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("loader", "hbm:hfr_obj");
            itemModel.addProperty("model", "models/block/" + objName + ".obj");
            saveItemModel(name, itemModel);
        }
    }

    public void generateGrate(Block block, String objPath) {
        String name = getRegistryName(block);

        // Particle модель
        JsonObject particleModel = new JsonObject();
        particleModel.addProperty("parent", "block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:block/" + objPath);
        particleModel.add("textures", textures);
        saveBlockModel(block, particleModel);

        // OBJ модель
        JsonObject objModel = new JsonObject();
        objModel.addProperty("loader", "hbm:hfr_obj");
        objModel.addProperty("model", "models/block/" + objPath + ".obj");
        saveBlockModel(block, objModel, "_obj");

        // Blockstate с layer от 0 до 7
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        for (int layer = 0; layer <= 7; layer++) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name);
            variants.add("layer=" + layer, variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Item модель
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("loader", "hbm:hfr_obj");
        itemModel.addProperty("model", "models/block/" + objPath + ".obj");
        saveItemModel(name, itemModel);
    }

    public void generateObjBlocksHorizontalDirections(Object[][] blocks) {
        String[] directions = {"north", "south", "west", "east"};  // только горизонтальные
        int[] yRot = {0, 180, 270, 90};
        int[] xRot = {0, 0, 0, 0};

        for (Object[] pair : blocks) {
            Block block = (Block) pair[0];
            String objName = (String) pair[1];
            String name = getRegistryName(block);
            String textureName = pair.length > 2 ? (String) pair[2] : name;

            // Blockstate с учётом facing (только горизонтальные направления)
            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();

            for (int i = 0; i < directions.length; i++) {
                String dir = directions[i];
                JsonObject variant = new JsonObject();
                variant.addProperty("model", "hbm:block/" + name);
                if (yRot[i] != 0) variant.addProperty("y", yRot[i]);
                if (xRot[i] != 0) variant.addProperty("x", xRot[i]);
                variants.add("facing=" + dir, variant);
            }
            blockstate.add("variants", variants);
            saveBlockstate(block, blockstate);

            // Block model (заглушка)
            JsonObject blockModel = new JsonObject();
            blockModel.addProperty("parent", "block/cube_all");
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", "hbm:block/" + textureName);
            blockModel.add("textures", textures);
            saveBlockModel(block, blockModel);

            // Item model (OBJ)
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", "builtin/entity");
            saveItemModel(name, itemModel);
        }
    }

    public void generateStaticObjBlock(Block block, String objName) {
        String name = getRegistryName(block);

        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject variant = new JsonObject();
        variant.addProperty("model", "hbm:block/" + name);
        variants.add("", variant);
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Block model (заглушка)
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:block/" + name);
        blockModel.add("textures", textures);
        saveBlockModel(block, blockModel);

        // Item model (OBJ)
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("loader", "hbm:hfr_obj");
        itemModel.addProperty("model", "models/block/" + objName + ".obj");
        saveItemModel(name, itemModel);
    }

    public void generateEnumObj(Block block, String objPrefix, Class<? extends Enum<?>> enumClass) {
        String name = getRegistryName(block);
        String[] types = EnumUtil.getEnumNames(enumClass);

        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        for (String type : types) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name + "_" + type);
            variants.add("type=" + type, variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Для каждого типа генерируем OBJ модель
        for (String type : types) {
            // Block model (заглушка с particle)
            JsonObject blockModel = new JsonObject();
            blockModel.addProperty("parent", "block/cube_all");
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", "hbm:block/" + objPrefix + "_" + type);
            blockModel.add("textures", textures);
            saveBlockModel(block, blockModel, "_" + type);

            // Item model (OBJ)
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("loader", "hbm:hfr_obj");
            itemModel.addProperty("model", "models/block/" + objPrefix + "_" + type + ".obj");
            saveItemModel(name + "_" + type, itemModel);
        }

        // Главный item model с overrides
        JsonObject mainItemModel = new JsonObject();
        mainItemModel.addProperty("parent", "item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:block/" + objPrefix + "_" + types[0]);
        mainItemModel.add("textures", textures);

        JsonArray overrides = new JsonArray();
        for (int i = 0; i < types.length; i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", i);
            override.add("predicate", predicate);
            override.addProperty("model", "hbm:item/" + name + "_" + types[i]);
            overrides.add(override);
        }
        mainItemModel.add("overrides", overrides);
        saveItemModel(name, mainItemModel);
    }

    // Перегрузка для сохранения произвольной item-модели
    public void saveItemModel(String itemName, JsonObject model) {
        Path path = packOutput.getOutputFolder()
                .resolve("assets/hbm/models/item/" + itemName + ".json");
        saveJson(path, model);
    }

    public void generateCubeAll(Object[][] blocks) {
        for (Object[] pair : blocks) {
            Block block = (Block) pair[0];
            String textureName = (String) pair[1];

            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:block/cube_all");

            JsonObject textures = new JsonObject();
            textures.addProperty("all", "hbm:block/" + textureName);
            model.add("textures", textures);

            saveBlockModel(block, model);
            saveItemModel(getRegistryName(block), "hbm:block/" + getRegistryName(block));
            saveSimpleBlockstate(block);
        }
    }

    public void generateCubeFrontSide(Block block, String frontTex, String sideTex, String topTex, String bottomTex) {
        String name = getRegistryName(block);

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube");

        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:block/" + frontTex);
        textures.addProperty("down", "hbm:block/" + bottomTex);
        textures.addProperty("up", "hbm:block/" + topTex);
        textures.addProperty("north", "hbm:block/" + frontTex);
        textures.addProperty("south", "hbm:block/" + sideTex);  // задняя сторона
        textures.addProperty("east", "hbm:block/" + sideTex);
        textures.addProperty("west", "hbm:block/" + sideTex);
        model.add("textures", textures);

        saveBlockModel(block, model);
        saveItemModel(name, "hbm:block/" + name);
        saveBlockstateFacing(block);
    }

    private void saveBlockstateFacing(Block block) {
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        // Все 6 направлений
        String[] directions = {"north", "south", "west", "east", "up", "down"};
        int[] yRot = {0, 180, 270, 90, 0, 0};
        int[] xRot = {0, 0, 0, 0, 270, 90};

        for (int i = 0; i < directions.length; i++) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + getRegistryName(block));
            if (yRot[i] != 0) variant.addProperty("y", yRot[i]);
            if (xRot[i] != 0) variant.addProperty("x", xRot[i]);
            variants.add("facing=" + directions[i], variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);
    }

    public void generateCubeAllTranslucent(Object[][] blocks) {
        for (Object[] pair : blocks) {
            Block block = (Block) pair[0];
            String textureName = (String) pair[1];

            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:block/cube_all");
            model.addProperty("render_type", "translucent"); // Добавляем для стекла

            JsonObject textures = new JsonObject();
            textures.addProperty("all", "hbm:block/" + textureName);
            model.add("textures", textures);

            saveBlockModel(block, model);
            saveItemModel(getRegistryName(block), "hbm:block/" + getRegistryName(block));
            saveSimpleBlockstate(block);
        }
    }

    public void generateCubeAllSwitch(Block block, String offTexture, String onTexture) {
        String name = getRegistryName(block);

        // Модель для выключенного состояния
        JsonObject offModel = new JsonObject();
        offModel.addProperty("parent", "minecraft:block/cube_all");
        JsonObject offTextures = new JsonObject();
        offTextures.addProperty("all", "hbm:block/" + offTexture);
        offModel.add("textures", offTextures);
        saveBlockModel(block, offModel, "_off");

        // Модель для включённого состояния
        JsonObject onModel = new JsonObject();
        onModel.addProperty("parent", "minecraft:block/cube_all");
        JsonObject onTextures = new JsonObject();
        onTextures.addProperty("all", "hbm:block/" + onTexture);
        onModel.add("textures", onTextures);
        saveBlockModel(block, onModel, "_on");

        // Blockstate с вариантами
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        JsonObject offVariant = new JsonObject();
        offVariant.addProperty("model", "hbm:block/" + name + "_off");
        variants.add("powered=false", offVariant);

        JsonObject onVariant = new JsonObject();
        onVariant.addProperty("model", "hbm:block/" + name + "_on");
        variants.add("powered=true", onVariant);

        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Item модель (используем off)
        saveItemModel(name, "hbm:block/" + name + "_off");
    }

    public void generateBlastFurnace(Block block, String sideTex, String topOffTex, String frontOffTex, String topOnTex, String frontOnTex) {
        String name = getRegistryName(block);
        String bottomTex = sideTex;
        String backTex = sideTex;
        // Модель для lit=false
        JsonObject offModel = cubeModel(sideTex, topOffTex, frontOffTex, bottomTex, backTex);
        saveBlockModel(block, offModel, "");
        // Модель для lit=true
        JsonObject onModel = cubeModel(sideTex, topOnTex, frontOnTex, bottomTex, backTex);
        saveBlockModel(block, onModel, "_lit");

        // Blockstate с facing и lit
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            int yRot = getYRotation(dir);
            for (boolean lit : new boolean[]{false, true}) {
                String key = "facing=" + dir.getSerializedName() + ",lit=" + lit;
                JsonObject variant = new JsonObject();
                variant.addProperty("model", "hbm:block/" + name + (lit ? "_lit" : ""));
                if (yRot != 0) variant.addProperty("y", yRot);
                variants.add(key, variant);
            }
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);
        // Item модель
        saveItemModel(name, "hbm:block/" + name);
    }

    private int getYRotation(Direction dir) {
        return switch (dir) {
            case SOUTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
            default -> 0;
        };
    }

    private JsonObject cubeModel(String side, String top, String front, String bottom, String back) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "block/cube");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:block/" + side);
        textures.addProperty("down", "hbm:block/" + bottom);
        textures.addProperty("up", "hbm:block/" + top);
        textures.addProperty("north", "hbm:block/" + front);
        textures.addProperty("south", "hbm:block/" + back);
        textures.addProperty("east", "hbm:block/" + side);
        textures.addProperty("west", "hbm:block/" + side);
        model.add("textures", textures);
        return model;
    }

    /**
     * Генерирует модель для блока со слоями (LAYERS property).
     * Создаёт:
     * - blockstate с вариантами layers=1..8, каждый ведёт на отдельную модель
     * - модели для каждого слоя (block/{name}_layer{1..8}.json)
     * - item модель (ссылается на модель слоя 1)
     * @param block блок (должен иметь LAYERS свойство)
     * @param textureName имя текстуры (без расширения), будет использована для всех слоёв
     */
    public void generateLayeredBlock(Block block, String textureName) {
        String name = getRegistryName(block);

        // 1. Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        for (int layer = 1; layer <= 8; layer++) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name + "_layer" + layer);
            variants.add("layers=" + layer, variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // 2. Модели для каждого слоя
        for (int layer = 1; layer <= 8; layer++) {
            float height = layer / 16.0f; // высота в блоках (от 1/16 до 1)
            int pixelHeight = layer * 2;   // высота в пикселях (1 слой = 2 пикселя? но у нас в BlockLayering SHAPES = i/16.0, а i от 1 до 8, т.е. 1/16 блока, 2/16... 8/16. Однако оригинальный fallout использовал от 2 до 16 пикселей, но у нас в BlockLayering SHAPES: i/16.0, где i = layers. Значит для layers=1 высота 1/16 блока = 1 пиксель. Это очень тонко. Лучше использовать такую же логику, как в fallout: height = layers * 2 / 16 = layers / 8. Но у нас SHAPES = i/16.0, поэтому нужно либо изменить SHAPES в BlockLayering на i*2/16, либо модели делать соответственно. Проще использовать SHAPES как есть (1/16..8/16). Тогда модель должна иметь высоту = layers/16 блока. Для 8 слоёв - полный блок.
            // Для простоты делаем модель с высотой = layers / 16 блока (т.е. layers пикселей).
            // В JSON элементы задаются в 1/16 единицах, поэтому высота = layers.
            int topY = layer; // высота в 1/16 блока

            JsonObject model = new JsonObject();
            model.addProperty("parent", "block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("all", "hbm:block/" + textureName);
            textures.addProperty("particle", "hbm:block/" + textureName);
            model.add("textures", textures);

            JsonArray elements = new JsonArray();
            JsonObject element = new JsonObject();
            element.add("from", jsonArray(0, 0, 0));
            element.add("to", jsonArray(16, topY, 16));
            JsonObject faces = new JsonObject();
            for (String dir : new String[]{"down", "up", "north", "south", "west", "east"}) {
                JsonObject face = new JsonObject();
                face.addProperty("texture", "#all");
                if (dir.equals("up")) {
                    // верхняя грань на всю площадь
                    face.add("uv", jsonArray(0, 0, 16, 16));
                } else if (dir.equals("down")) {
                    // нижняя грань (невидима) – можно тоже на всю площадь
                    face.add("uv", jsonArray(0, 0, 16, 16));
                } else {
                    // боковые грани – только видимая часть по высоте
                    // UV: текстура растягивается по высоте от 0 до 16, но мы берём только нижнюю часть
                    // Для простоты используем UV от 0 до 16 по Y, но при рендеринге текстура сожмётся? Лучше использовать UV от 0 до topY*16/16? Не критично, можно оставить 0..16.
                    face.add("uv", jsonArray(0, 0, 16, 16));
                }
                faces.add(dir, face);
            }
            element.add("faces", faces);
            elements.add(element);
            model.add("elements", elements);

            saveBlockModel(block, model, "_layer" + layer);
        }

        // 3. Item модель – используем модель для первого слоя
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "hbm:block/" + name + "_layer1");
        saveItemModel(name, itemModel);
    }

    public void generateCubeTopBottom(Block block, String sideTexture, String topTexture, String bottomTexture) {
        String name = getRegistryName(block);

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_bottom_top");

        JsonObject textures = new JsonObject();
        textures.addProperty("side", "hbm:block/" + sideTexture);
        textures.addProperty("top", "hbm:block/" + topTexture);
        textures.addProperty("bottom", "hbm:block/" + bottomTexture);
        model.add("textures", textures);

        saveBlockModel(block, model);
        saveItemModel(name, "hbm:block/" + name);
        saveSimpleBlockstate(block);
    }

    public void generateOre(Block block, String oreTexture, String baseTexture) {
        String name = getRegistryName(block);

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        model.addProperty("render_type", "cutout");

        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:block/" + oreTexture);
        if (baseTexture.contains(":")) {
            textures.addProperty("base", baseTexture);
        } else if (baseTexture.equals("stone") || baseTexture.equals("deepslate") || baseTexture.equals("netherrack")) {
            textures.addProperty("base", "minecraft:block/" + baseTexture);
        } else {
            textures.addProperty("base", "hbm:block/" + baseTexture);
        }
        textures.addProperty("overlay", "hbm:block/" + oreTexture);
        model.add("textures", textures);

        JsonArray elements = new JsonArray();

        // Базовый слой (камень)
        JsonObject baseElement = new JsonObject();
        baseElement.add("from", jsonArray(0, 0, 0));
        baseElement.add("to", jsonArray(16, 16, 16));
        JsonObject baseFaces = new JsonObject();
        for (String dir : new String[]{"down", "up", "north", "south", "west", "east"}) {
            JsonObject face = new JsonObject();
            face.addProperty("texture", "#base");
            face.addProperty("cullface", dir);
            baseFaces.add(dir, face);
        }
        baseElement.add("faces", baseFaces);
        elements.add(baseElement);

        // Оверлей руды
        JsonObject overlayElement = new JsonObject();
        overlayElement.add("from", jsonArray(0, 0, 0));
        overlayElement.add("to", jsonArray(16, 16, 16));
        JsonObject overlayFaces = new JsonObject();
        for (String dir : new String[]{"down", "up", "north", "south", "west", "east"}) {
            JsonObject face = new JsonObject();
            face.addProperty("texture", "#overlay");
            overlayFaces.add(dir, face);
        }
        overlayElement.add("faces", overlayFaces);
        elements.add(overlayElement);

        model.add("elements", elements);

        saveBlockModel(block, model);
        saveItemModel(name, "hbm:block/" + name);
        saveSimpleBlockstate(block);
    }

    public void generateModelItem(Block block, String particleTexture) {
        String name = getRegistryName(block);

        // Блок модель (пустая, только particle)
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/block");

        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:item/" + particleTexture);
        blockModel.add("textures", textures);
        blockModel.add("elements", new JsonArray());

        saveBlockModel(block, blockModel);

        // Blockstate с facing
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        String[] directions = {"north", "south", "west", "east"};
        int[] yRot = {0, 180, 270, 90};

        for (int i = 0; i < directions.length; i++) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name);
            if (yRot[i] != 0) variant.addProperty("y", yRot[i]);
            variants.add("facing=" + directions[i], variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Item модель (generated)
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");

        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("layer0", "hbm:item/" + particleTexture);
        itemModel.add("textures", itemTextures);

        saveItemModel(name, itemModel);
    }

    public void generateEntityBlockAnimated(Block block) {
        String name = getRegistryName(block);

        // Создаём пустую модель для блока
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        model.add("elements", new JsonArray());
        saveBlockModel(block, model); // сохраняем как блок/имя_блока.json

        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject variant = new JsonObject();
        variant.addProperty("model", "hbm:block/" + name); // ссылаемся на свою модель
        variants.add("", variant);
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);
    }

    public void generateModelItemStatic(Block block, String particleTexture) {
        String name = getRegistryName(block);

        // Блок модель (пустая, только particle)
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/block");

        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:item/" + particleTexture);
        blockModel.add("textures", textures);
        blockModel.add("elements", new JsonArray());

        saveBlockModel(block, blockModel);

        // Blockstate (простой, без facing)
        saveSimpleBlockstate(block);

        // Item модель (generated)
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");

        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("layer0", "hbm:item/" + particleTexture);
        itemModel.add("textures", itemTextures);

        saveItemModel(name, itemModel);
    }

    public void generateAir(Block block) {
        // Модель блока
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        model.add("elements", new JsonArray());
        saveBlockModel(block, model);

        // Blockstate с одним вариантом
        saveSimpleBlockstate(block);
    }

    public void generateMultiblock(Block block, String objPath, String texturePath) {
        generateMultiblock(block, objPath, texturePath, false);
    }

    public void generateMultiblock(Block block, String objPath, String texturePath, boolean noItem) {
        String name = getRegistryName(block);

        // Блок модель (заглушка для particle)
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", "hbm:block/" + texturePath);
        blockModel.add("textures", textures);
        saveBlockModel(block, blockModel);

        // Blockstate с dummy_state и facing
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        String[] directions = {"down", "up", "north", "south", "west", "east"};
        int[] yRot = {0, 0, 90, 270, 180, 0};

        for (int state = 0; state <= 2; state++) {
            for (int i = 0; i < directions.length; i++) {
                String dir = directions[i];
                JsonObject variant = new JsonObject();
                variant.addProperty("model", "hbm:block/" + name);
                if (yRot[i] != 0) variant.addProperty("y", yRot[i]);
                variants.add("dummy_state=" + state + ",facing=" + dir, variant);
            }
        }

        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Item модель — генерируем только если noItem == false
        if (!noItem) {
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("loader", "hbm:hfr_obj");
            itemModel.addProperty("model", "models/block/" + objPath + ".obj");
            saveItemModel(name, itemModel);
        }
    }

    private JsonArray jsonArray(int... values) {
        JsonArray array = new JsonArray();
        for (int v : values) array.add(v);
        return array;
    }

    private JsonArray jsonArray(double... values) {
        JsonArray array = new JsonArray();
        for (double v : values) array.add(v);
        return array;
    }

    public void generateCross(Block block, String textureName) {
        String name = getRegistryName(block);

        // Модель блока
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "minecraft:block/cross");
        blockModel.addProperty("render_type", "cutout");

        JsonObject textures = new JsonObject();
        textures.addProperty("cross", "hbm:block/" + textureName);
        blockModel.add("textures", textures);

        saveBlockModel(block, blockModel);

        // Модель предмета (для инвентаря) - используем generated
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");

        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("layer0", "hbm:block/" + textureName);
        itemModel.add("textures", itemTextures);

        saveItemModel(name, itemModel);

        saveSimpleBlockstate(block);
    }

    public void generateDecoCT(Block block, String textureName) {
        String name = getRegistryName(block);

        // Блок модель с кастомным лоадером
        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("loader", "hbm:deco_ct_geometry");

        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "hbm:block/" + textureName);
        textures.addProperty("texture", "hbm:block/" + textureName);
        blockModel.add("textures", textures);

        saveBlockModel(block, blockModel);

        // Item модель
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "hbm:block/" + name);

        JsonObject display = new JsonObject();

        JsonObject ground = new JsonObject();
        ground.add("rotation", jsonArray(0, 0, 0));
        ground.add("translation", jsonArray(0, 3, 0));
        ground.add("scale", jsonArray(0.25, 0.25, 0.25));
        display.add("ground", ground);

        JsonObject thirdpersonR = new JsonObject();
        thirdpersonR.add("rotation", jsonArray(-15, 0, 45));
        thirdpersonR.add("translation", jsonArray(0, 2, 0));
        thirdpersonR.add("scale", jsonArray(0.4, 0.4, 0.4));
        display.add("thirdperson_righthand", thirdpersonR);

        JsonObject thirdpersonL = new JsonObject();
        thirdpersonL.add("rotation", jsonArray(-15, 0, 45));
        thirdpersonL.add("translation", jsonArray(0, 2, 0));
        thirdpersonL.add("scale", jsonArray(0.4, 0.4, 0.4));
        display.add("thirdperson_lefthand", thirdpersonL);

        JsonObject firstpersonR = new JsonObject();
        firstpersonR.add("rotation", jsonArray(0, 45, 0));
        firstpersonR.add("translation", jsonArray(0, 0, 0));
        firstpersonR.add("scale", jsonArray(0.4, 0.4, 0.4));
        display.add("firstperson_righthand", firstpersonR);

        JsonObject firstpersonL = new JsonObject();
        firstpersonL.add("rotation", jsonArray(0, 45, 0));
        firstpersonL.add("translation", jsonArray(0, 0, 0));
        firstpersonL.add("scale", jsonArray(0.4, 0.4, 0.4));
        display.add("firstperson_lefthand", firstpersonL);

        JsonObject gui = new JsonObject();
        gui.add("rotation", jsonArray(30, 225, 0));
        gui.add("translation", jsonArray(0, 0, 0));
        gui.add("scale", jsonArray(0.65, 0.65, 0.65));
        display.add("gui", gui);

        itemModel.add("display", display);

        saveItemModel(name, itemModel);

        saveSimpleBlockstate(block);
    }

    public void generateDoor(Block block, String textureName) {
        String name = getRegistryName(block);

        String[] variants = {
                "lower_left", "lower_left_open", "lower_right", "lower_right_open",
                "upper_left", "upper_left_open", "upper_right", "upper_right_open"
        };

        for (String variant : variants) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:block/door_" + variant.replace("lower", "bottom").replace("upper", "top"));

            JsonObject textures = new JsonObject();
            textures.addProperty("bottom", "hbm:block/doors/" + textureName + "_lower");
            textures.addProperty("top", "hbm:block/doors/" + textureName + "_upper");
            model.add("textures", textures);

            Path path = packOutput.getOutputFolder()
                    .resolve("assets/hbm/models/block/" + name + "_" + variant + ".json");
            saveJson(path, model);
        }

        // Blockstate (без изменений)
        JsonObject blockstate = new JsonObject();
        JsonObject variantsObj = new JsonObject();

        String[] directions = {"east", "north", "south", "west"};
        for (String facing : directions) {
            for (String half : new String[]{"lower", "upper"}) {
                for (String hinge : new String[]{"left", "right"}) {
                    for (String open : new String[]{"false", "true"}) {
                        String key = "facing=" + facing + ",half=" + half + ",hinge=" + hinge + ",open=" + open;
                        String modelName = name + "_" + half + "_" + hinge + (open.equals("true") ? "_open" : "");

                        JsonObject variant = new JsonObject();
                        variant.addProperty("model", "hbm:block/" + modelName);

                        int yOpen = switch (facing) {
                            case "east" -> 90;
                            case "south" -> 180;
                            case "west" -> 270;
                            default -> 0;
                        };

                        int y = open.equals("true") ? yOpen : (yOpen + 270) % 360;
                        if (y > 0) variant.addProperty("y", y);

                        variantsObj.add(key, variant);
                    }
                }
            }
        }
        blockstate.add("variants", variantsObj);
        saveBlockstate(block, blockstate);

        // Item-модель
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");
        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("layer0", "hbm:item/doors/" + textureName);
        itemModel.add("textures", itemTextures);

        Path itemPath = packOutput.getOutputFolder()
                .resolve("assets/hbm/models/item/" + name + ".json");
        saveJson(itemPath, itemModel);
    }

    public void generateEnumBlockWithItems(Block block, String texturePrefix, Class<? extends Enum<?>> enumClass) {
        String name = getRegistryName(block);
        String[] types = EnumUtil.getEnumNames(enumClass);

        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        for (String type : types) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name + "_" + type);
            variants.add("type=" + type, variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Block models и item models для каждого типа
        for (String type : types) {
            // Block model (cube_all)
            JsonObject blockModel = new JsonObject();
            blockModel.addProperty("parent", "minecraft:block/cube_all");
            JsonObject textures = new JsonObject();
            textures.addProperty("all", "hbm:block/" + texturePrefix + "_" + type);
            blockModel.add("textures", textures);
            saveBlockModel(block, blockModel, "_" + type);

            // Item model (ссылается на блок модель)
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", "hbm:block/" + name + "_" + type);
            saveItemModel(name + "_" + type, itemModel);
        }

        // Главный item model с overrides
        JsonObject mainItemModel = new JsonObject();
        mainItemModel.addProperty("parent", "item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:block/" + name + "_" + types[0]);
        mainItemModel.add("textures", textures);

        JsonArray overrides = new JsonArray();
        for (int i = 0; i < types.length; i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", i);
            override.add("predicate", predicate);
            override.addProperty("model", "hbm:item/" + name + "_" + types[i]);
            overrides.add(override);
        }
        mainItemModel.add("overrides", overrides);

        saveItemModel(name, mainItemModel);
    }

    /**
     * Генерирует модели для растений с крестовыми моделями
     * - blockstate с вариантами по enum
     * - cross-модели для каждого типа (блок)
     * - item модели с CustomModelData overrides
     */
    public void generatePlantEnum(Block block, String texturePrefix, Class<? extends Enum<?>> enumClass) {
        String name = getRegistryName(block);
        String[] typeNames = EnumUtil.getEnumNames(enumClass);

        // 1. Blockstate со ВСЕМИ значениями meta
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        for (int i = 0; i < typeNames.length; i++) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name + "_" + typeNames[i]);
            variants.add("meta=" + i, variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // 2. Блок модели (cross) для каждого типа
        for (int i = 0; i < typeNames.length; i++) {
            String type = typeNames[i];

            JsonObject blockModel = new JsonObject();
            blockModel.addProperty("parent", "minecraft:block/cross");
            blockModel.addProperty("render_type", "cutout");
            JsonObject textures = new JsonObject();
            textures.addProperty("cross", "hbm:block/" + texturePrefix + "_" + type);
            blockModel.add("textures", textures);
            saveBlockModel(block, blockModel, "_" + type);

            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", "minecraft:item/generated");
            JsonObject itemTextures = new JsonObject();
            itemTextures.addProperty("layer0", "hbm:block/" + texturePrefix + "_" + type);
            itemModel.add("textures", itemTextures);
            saveItemModel(name + "_" + type, itemModel);
        }

        // 3. Главный item model с overrides
        JsonObject mainItemModel = new JsonObject();
        mainItemModel.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:block/" + texturePrefix + "_" + typeNames[0]);
        mainItemModel.add("textures", textures);

        JsonArray overrides = new JsonArray();
        for (int i = 0; i < typeNames.length; i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", i);
            override.add("predicate", predicate);
            override.addProperty("model", "hbm:item/" + name + "_" + typeNames[i]);
            overrides.add(override);
        }
        mainItemModel.add("overrides", overrides);
        saveItemModel(name, mainItemModel);
    }

    /**
     * Генерирует модели для высоких растений (BlockTallPlant)
     * с отдельными текстурами для нижней и верхней части
     */
    public void generateTallPlant(Block block, String texturePrefix, Class<? extends Enum<?>> enumClass) {
        String name = getRegistryName(block);
        String[] types = EnumUtil.getEnumNames(enumClass);

        // 1. Blockstate с type
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        for (String type : types) {
            // Для нижней части (LOWER)
            JsonObject lowerVariant = new JsonObject();
            lowerVariant.addProperty("model", "hbm:block/" + name + "_" + type + "_lower");
            variants.add("half=lower,type=" + type, lowerVariant);

            // Для верхней части (UPPER)
            JsonObject upperVariant = new JsonObject();
            upperVariant.addProperty("model", "hbm:block/" + name + "_" + type + "_upper");
            variants.add("half=upper,type=" + type, upperVariant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // 2. Блок модели для каждого типа (нижняя и верхняя)
        for (String type : types) {
            // Нижняя часть (cross)
            JsonObject lowerModel = new JsonObject();
            lowerModel.addProperty("parent", "minecraft:block/cross");
            lowerModel.addProperty("render_type", "cutout");
            JsonObject lowerTextures = new JsonObject();
            lowerTextures.addProperty("cross", "hbm:block/" + texturePrefix + "_" + type + "_lower");
            lowerModel.add("textures", lowerTextures);
            saveBlockModel(block, lowerModel, "_" + type + "_lower");

            // Верхняя часть (cross)
            JsonObject upperModel = new JsonObject();
            upperModel.addProperty("parent", "minecraft:block/cross");
            upperModel.addProperty("render_type", "cutout");
            JsonObject upperTextures = new JsonObject();
            upperTextures.addProperty("cross", "hbm:block/" + texturePrefix + "_" + type + "_upper");
            upperModel.add("textures", upperTextures);
            saveBlockModel(block, upperModel, "_" + type + "_upper");

            // Item модель (используем нижнюю часть)
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", "minecraft:item/generated");
            JsonObject itemTextures = new JsonObject();
            itemTextures.addProperty("layer0", "hbm:block/" + texturePrefix + "_" + type + "_lower");
            itemModel.add("textures", itemTextures);
            saveItemModel(name + "_" + type, itemModel);
        }

        // 3. Главный item model с overrides
        JsonObject mainItemModel = new JsonObject();
        mainItemModel.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:block/" + texturePrefix + "_" + types[0] + "_lower");
        mainItemModel.add("textures", textures);

        JsonArray overrides = new JsonArray();
        for (int i = 0; i < types.length; i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", i);
            override.add("predicate", predicate);
            override.addProperty("model", "hbm:item/" + name + "_" + types[i]);
            overrides.add(override);
        }
        mainItemModel.add("overrides", overrides);
        saveItemModel(name, mainItemModel);
    }

    public void generateEnumColumnWithItems(Block block, String texturePrefix, String[] types) {
        String name = getRegistryName(block);

        // Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        for (String type : types) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name + "_" + type);
            variants.add("type=" + type, variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // Block models (pillar) и item models для каждого типа
        for (String type : types) {
            // Block model (cube_column)
            JsonObject blockModel = new JsonObject();
            blockModel.addProperty("parent", "minecraft:block/cube_column");
            JsonObject textures = new JsonObject();
            textures.addProperty("side", "hbm:block/" + texturePrefix + "_" + type);
            textures.addProperty("end", "hbm:block/" + texturePrefix + "_" + type + "_top");
            blockModel.add("textures", textures);
            saveBlockModel(block, blockModel, "_" + type);

            // Горизонтальная модель (для pillar)
            JsonObject blockModelHorizontal = new JsonObject();
            blockModelHorizontal.addProperty("parent", "minecraft:block/cube_column_horizontal");
            blockModelHorizontal.add("textures", textures);
            saveBlockModel(block, blockModelHorizontal, "_" + type + "_horizontal");

            // Item model (ссылается на блок модель)
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", "hbm:block/" + name + "_" + type);
            saveItemModel(name + "_" + type, itemModel);
        }

        // Главный item model с overrides
        JsonObject mainItemModel = new JsonObject();
        mainItemModel.addProperty("parent", "item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:block/" + name + "_" + types[0]);
        mainItemModel.add("textures", textures);

        JsonArray overrides = new JsonArray();
        for (int i = 0; i < types.length; i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", i);
            override.add("predicate", predicate);
            override.addProperty("model", "hbm:item/" + name + "_" + types[i]);
            overrides.add(override);
        }
        mainItemModel.add("overrides", overrides);

        saveItemModel(name, mainItemModel);
    }

    public void generateBedrockOre(Block block, String baseTexture, String overlayPrefix) {
        String name = getRegistryName(block);

        // 1. Blockstate
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        for (int shape = 0; shape <= 9; shape++) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + name + "_shape" + shape);
            variants.add("shape=" + shape, variant);
        }
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        // 2. Модели для каждого shape
        for (int shape = 0; shape <= 9; shape++) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:block/block");
            model.addProperty("render_type", "cutout"); // для прозрачности оверлея

            JsonObject textures = new JsonObject();
            // базовая текстура (bedrock)
            if (baseTexture.contains(":")) {
                textures.addProperty("base", baseTexture);
            } else {
                textures.addProperty("base", "hbm:block/" + baseTexture);
            }
            // оверлей (случайная текстура из набора)
            textures.addProperty("overlay", "hbm:block/" + overlayPrefix + (shape+1));
            model.add("textures", textures);

            JsonArray elements = new JsonArray();

            // Базовый слой (bedrock)
            JsonObject baseElement = new JsonObject();
            baseElement.add("from", jsonArray(0, 0, 0));
            baseElement.add("to", jsonArray(16, 16, 16));
            JsonObject baseFaces = new JsonObject();
            for (String dir : new String[]{"down", "up", "north", "south", "west", "east"}) {
                JsonObject face = new JsonObject();
                face.addProperty("texture", "#base");
                face.addProperty("cullface", dir);
                baseFaces.add(dir, face);
            }
            baseElement.add("faces", baseFaces);
            elements.add(baseElement);

            // Оверлей (окрашиваемый)
            JsonObject overlayElement = new JsonObject();
            overlayElement.add("from", jsonArray(0, 0, 0));
            overlayElement.add("to", jsonArray(16, 16, 16));
            JsonObject overlayFaces = new JsonObject();
            for (String dir : new String[]{"down", "up", "north", "south", "west", "east"}) {
                JsonObject face = new JsonObject();
                face.addProperty("texture", "#overlay");
                face.addProperty("tintindex", 0);
                face.addProperty("cullface", dir);
                overlayFaces.add(dir, face);
            }
            overlayElement.add("faces", overlayFaces);
            elements.add(overlayElement);

            model.add("elements", elements);
            saveBlockModel(block, model, "_shape" + shape);
        }

        // 3. Item модель (простая, используем первый оверлей как иконку)
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:block/" + overlayPrefix + "1");
        itemModel.add("textures", textures);
        saveItemModel(name, itemModel);
    }

    public void generateCubeColumn(Block block, String sideTexture, String endTexture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_column");

        JsonObject textures = new JsonObject();
        textures.addProperty("side", "hbm:block/" + sideTexture);
        textures.addProperty("end", "hbm:block/" + endTexture);
        model.add("textures", textures);

        saveBlockModel(block, model);
        saveBlockModel(block, model, "_horizontal");
        saveItemModel(getRegistryName(block), "hbm:block/" + getRegistryName(block));
        saveBlockstatePillar(block);
    }

    public void generateConcreteSuper(Block block) {
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        for (int i = 0; i <= 9; i++) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", "hbm:block/" + getRegistryName(block));
            variants.add("damage=" + i, variant);
        }

        variants.add("damage=10", createVariant("hbm:block/" + getRegistryName(block) + "_m0"));
        variants.add("damage=11", createVariant("hbm:block/" + getRegistryName(block) + "_m0"));
        variants.add("damage=12", createVariant("hbm:block/" + getRegistryName(block) + "_m1"));
        variants.add("damage=13", createVariant("hbm:block/" + getRegistryName(block) + "_m1"));
        variants.add("damage=14", createVariant("hbm:block/" + getRegistryName(block) + "_m2"));
        variants.add("damage=15", createVariant("hbm:block/" + getRegistryName(block) + "_m3"));

        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);

        for (int i = 0; i <= 3; i++) {
            JsonObject model = new JsonObject();
            model.addProperty("parent", "minecraft:block/cube_all");
            JsonObject textures = new JsonObject();
            textures.addProperty("all", "hbm:block/" + getRegistryName(block) + "_m" + i);
            model.add("textures", textures);
            saveBlockModel(block, model, "_m" + i);
        }

        JsonObject mainModel = new JsonObject();
        mainModel.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", "hbm:block/" + getRegistryName(block));
        mainModel.add("textures", textures);
        saveBlockModel(block, mainModel, "");

        saveItemModel(getRegistryName(block), "hbm:block/" + getRegistryName(block));
    }

    private JsonObject createVariant(String model) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", model);
        return variant;
    }

    private void saveBlockModel(Block block, JsonObject model) {
        saveBlockModel(block, model, "");
    }

    private void saveBlockModel(Block block, JsonObject model, String suffix) {
        String name = getRegistryName(block);
        Path path = packOutput.getOutputFolder()
                .resolve("assets/hbm/models/block/" + name + suffix + ".json");
        saveJson(path, model);
    }

    public void saveItemModel(String itemName, String parentModel) {
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", parentModel);
        Path path = packOutput.getOutputFolder()
                .resolve("assets/hbm/models/item/" + itemName + ".json");
        saveJson(path, itemModel);
    }

    public void saveSimpleBlockstate(Block block) {
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject variant = new JsonObject();
        variant.addProperty("model", "hbm:block/" + getRegistryName(block));
        variants.add("", variant);
        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);
    }

    private void saveBlockstatePillar(Block block) {
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();

        String modelName = "hbm:block/" + getRegistryName(block);
        String modelNameHorizontal = modelName + "_horizontal";

        JsonObject axisY = new JsonObject();
        axisY.addProperty("model", modelName);
        variants.add("axis=y", axisY);

        JsonObject axisX = new JsonObject();
        axisX.addProperty("model", modelNameHorizontal);
        axisX.addProperty("x", 90);
        axisX.addProperty("y", 90);
        variants.add("axis=x", axisX);

        JsonObject axisZ = new JsonObject();
        axisZ.addProperty("model", modelNameHorizontal);
        axisZ.addProperty("x", 90);
        variants.add("axis=z", axisZ);

        blockstate.add("variants", variants);
        saveBlockstate(block, blockstate);
    }

    private void saveBlockstate(Block block, JsonObject blockstate) {
        String name = getRegistryName(block);
        Path path = packOutput.getOutputFolder()
                .resolve("assets/hbm/blockstates/" + name + ".json");
        saveJson(path, blockstate);
    }

    private void saveJson(Path path, JsonObject json) {
        entries.add(new JsonEntry(json, path));
    }

    public String getRegistryName(Block block) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
    }
}