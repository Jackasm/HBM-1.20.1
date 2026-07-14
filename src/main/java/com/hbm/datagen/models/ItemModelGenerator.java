package com.hbm.datagen.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemModelGenerator {

    private final PackOutput packOutput;
    private final List<JsonEntry> entries = new ArrayList<>();

    public record JsonEntry(JsonObject json, Path path) {}

    public ItemModelGenerator(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    public List<JsonEntry> getEntries() {
        return entries;
    }

    /**
     * Генерирует простую модель (parent = item/generated) с одной текстурой layer0.
     * @param itemName имя предмета (registry path)
     * @param textureName имя текстуры (обычно совпадает с itemName)
     */
    public void generateGenerated(String itemName, String textureName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:item/" + textureName);
        model.add("textures", textures);
        save(itemName, model);
    }

    public void generateNone(String itemName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        // Используем текстуру блока вместо item текстуры
        textures.addProperty("all", "hbm:block/" + itemName);
        model.add("textures", textures);
        save(itemName, model);
    }
    public void generateNoneWithTexture(String itemName, String textureName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/cube_all");
        JsonObject textures = new JsonObject();
        textures.addProperty("all", "hbm:block/" + textureName);
        model.add("textures", textures);
        save(itemName, model);
    }


    /**
     * Генерирует модель с масштабированием (scale 0.35) для маленьких предметов.
     * @param itemName имя предмета (registry path)
     * @param textureName имя текстуры
     */
    public void generateGeneratedTiny(String itemName, String textureName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:item/" + textureName);
        model.add("textures", textures);

        JsonObject display = new JsonObject();

        String[] transforms = {"thirdperson_righthand", "thirdperson_lefthand",
                "firstperson_righthand", "firstperson_lefthand",
                "gui", "ground", "fixed"};

        for (String transform : transforms) {
            JsonObject transformObj = new JsonObject();
            JsonArray scale = new JsonArray();
            scale.add(0.35);
            scale.add(0.35);
            scale.add(0.35);
            transformObj.add("scale", scale);
            display.add(transform, transformObj);
        }

        model.add("display", display);
        save(itemName, model);
    }

    public void generateBedrockOreItem(String itemName, String baseTexture, String overlayPrefix) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        model.addProperty("render_type", "cutout");

        JsonObject textures = new JsonObject();
        if (baseTexture.contains(":")) {
            textures.addProperty("base", baseTexture);
        } else {
            textures.addProperty("base", "hbm:block/" + baseTexture);
        }
        textures.addProperty("overlay", overlayPrefix + "1"); // используем первый оверлей по умолчанию
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

        // Оверлей с tintindex для окрашивания
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

        // Добавляем display трансформации для инвентаря
        JsonObject display = new JsonObject();

        // GUI дисплей
        JsonObject gui = new JsonObject();
        gui.add("rotation", jsonArray(30, 225, 0));
        gui.add("translation", jsonArray(0, 0, 0));
        gui.add("scale", jsonArray(0.625, 0.625, 0.625));
        display.add("gui", gui);

        // Дисплей на земле
        JsonObject ground = new JsonObject();
        ground.add("rotation", jsonArray(0, 0, 0));
        ground.add("translation", jsonArray(0, 3, 0));
        ground.add("scale", jsonArray(0.25, 0.25, 0.25));
        display.add("ground", ground);

        // От первого лица
        JsonObject firstperson = new JsonObject();
        firstperson.add("rotation", jsonArray(0, 45, 0));
        firstperson.add("translation", jsonArray(0, 0, 0));
        firstperson.add("scale", jsonArray(0.4, 0.4, 0.4));
        display.add("firstperson", firstperson);

        // От третьего лица
        JsonObject thirdperson = new JsonObject();
        thirdperson.add("rotation", jsonArray(-15, 0, 45));
        thirdperson.add("translation", jsonArray(0, 2, 0));
        thirdperson.add("scale", jsonArray(0.375, 0.375, 0.375));
        display.add("thirdperson", thirdperson);

        model.add("display", display);

        save(itemName, model);
    }

    public void generateEnumItem(String itemName, String[] types, String baseTexture) {
        // Основной item с overrides
        JsonObject mainModel = new JsonObject();
        mainModel.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:item/" + baseTexture);
        mainModel.add("textures", textures);

        JsonArray overrides = new JsonArray();
        for (int i = 0; i < types.length; i++) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", i);
            override.add("predicate", predicate);
            override.addProperty("model", "hbm:item/" + types[i]);
            overrides.add(override);
        }
        mainModel.add("overrides", overrides);
        save(itemName, mainModel);

        // Отдельные item файлы для каждого типа
        for (String type : types) {
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", "minecraft:item/generated");

            JsonObject typeTextures = new JsonObject();
            typeTextures.addProperty("layer0", "hbm:item/" + type);
            itemModel.add("textures", typeTextures);

            save(type, itemModel);
        }
    }

    public void generateObjItem(String itemName, String objPath) {
        JsonObject model = new JsonObject();
        model.addProperty("loader", "hbm:hfr_obj");
        model.addProperty("model", objPath);

        save(itemName, model);
    }

    /**
     * Генерирует enum item с tint (окрашиванием) для всех типов.
     * Использует одну базовую текстуру, которая окрашивается в цвет материала.
     */
    public void generateEnumItemTinted(String itemName, String baseTexture, String overlayTexture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:item/" + baseTexture);
        textures.addProperty("layer1", "hbm:item/" + overlayTexture);
        model.add("textures", textures);

        save(itemName, model);
    }

    public void generateEnumItemFromEnum(String itemName, Class<? extends Enum<?>> enumClass) {
        Enum<?>[] constants = enumClass.getEnumConstants();
        String[] types = new String[constants.length];

        // Первый тип используем как baseTexture
        String firstTypeName = constants[0].name().toLowerCase(Locale.US);
        String baseTexture = itemName + "_" + firstTypeName;

        for (int i = 0; i < constants.length; i++) {
            String name = constants[i].name().toLowerCase(Locale.US);
            types[i] = itemName + "_" + name;
        }

        generateEnumItem(itemName, types, baseTexture);
    }

    public void generateGeneratedLayered(String itemName, String baseTexture, String overlayTexture) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:item/" + baseTexture);
        textures.addProperty("layer1", "hbm:item/" + overlayTexture);
        model.add("textures", textures);

        save(itemName, model);
    }

    /**
     * Генерирует модель handheld (parent = item/handheld).
     */
    public void generateHandheld(String itemName, String textureName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/handheld");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:item/" + textureName);
        model.add("textures", textures);
        save(itemName, model);
    }

    /**
     * Генерирует модель handheld_rod (parent = item/handheld_rod).
     */
    public void generateHandheldRod(String itemName, String textureName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/handheld_rod");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "hbm:item/" + textureName);
        model.add("textures", textures);
        save(itemName, model);
    }

    /**
     * Генерирует модель яйца призыва (parent = item/template_spawn_egg).
     */
    public void generateSpawnEgg(String itemName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/template_spawn_egg");
        save(itemName, model);
    }

    public void generateBuiltinEntity(String itemName) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "builtin/entity");
        save(itemName, model);
    }

    private void save(String itemName, JsonObject model) {
        Path path = packOutput.getOutputFolder()
                .resolve("assets/hbm/models/item/" + itemName + ".json");
        entries.add(new JsonEntry(model, path));
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
}