package com.hbm.render.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;

import static com.hbm.util.ResLocation.ResLocation;

public class HFRWavefrontObject implements IUnbakedGeometry<HFRWavefrontObject> {

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Vertex> normals = new ArrayList<>();
    private final List<TextureCoordinate> texCoords = new ArrayList<>();
    private final List<GroupObject> groups = new ArrayList<>();
    public final Map<String, GroupObject> groupMap = new HashMap<>();
    private GroupObject currentGroup;
    private final ResourceLocation modelLocation;
    private final boolean smoothing;

    public HFRWavefrontObject(ResourceLocation location) {
        this(location, true);
    }

    public HFRWavefrontObject(ResourceLocation location, boolean smoothing) {
        this.modelLocation = location;
        this.smoothing = smoothing;
        loadModel();
    }

    // Загрузка модели
    private void loadModel() {
        try {
            InputStream stream = Minecraft.getInstance().getResourceManager()
                    .getResource(modelLocation).orElseThrow().open();
            loadModel(stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load OBJ model: " + modelLocation, e);
        }
    }

    private void loadModel(InputStream stream) throws IOException {
        vertices.clear();
        normals.clear();
        texCoords.clear();
        groups.clear();
        groupMap.clear();
        currentGroup = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        int lineNum = 0;

        while ((line = reader.readLine()) != null) {
            lineNum++;
            line = line.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            try {
                if (line.startsWith("v ")) { // Вершина
                    parseVertex(line);
                } else if (line.startsWith("vn ")) { // Нормаль
                    parseNormal(line);
                } else if (line.startsWith("vt ")) { // Текстурная координата
                    parseTexCoord(line);
                } else if (line.startsWith("f ")) { // Грань
                    parseFace(line);
                } else if (line.startsWith("g ") || line.startsWith("o ")) { // Группа/Объект
                    parseGroup(line);
                }
            } catch (Exception e) {
                throw new IOException("Error parsing line " + lineNum + ": " + line, e);
            }
        }

        reader.close();

        // Добавляем последнюю группу, если она есть
        if (currentGroup != null) {
            groups.add(currentGroup);
            groupMap.put(currentGroup.name, currentGroup);
        }
    }

    private void parseVertex(String line) {
        float[] coords = parseFloats(line, 3); // x, y, z
        vertices.add(new Vertex(coords[0], coords[1], coords[2]));
    }

    private void parseNormal(String line) {
        float[] coords = parseFloats(line, 3); // x, y, z
        normals.add(new Vertex(coords[0], coords[1], coords[2]));
    }

    private void parseTexCoord(String line) {
        float[] coords = parseFloats(line, 2); // u, v (w необязателен)
        float u = coords[0];
        float v = coords.length > 1 ? coords[1] : 0;
        float w = coords.length > 2 ? coords[2] : 0;
        texCoords.add(new TextureCoordinate(u, 1.0f - v, w)); // Инвертируем V
    }

    private float[] parseFloats(String line, int minCount) {
        String[] parts = line.split("\\s+");
        int actualCount = parts.length - 1; // -1 чтобы пропустить префикс
        int count = Math.min(actualCount, minCount);

        float[] result = new float[count];
        for (int i = 0; i < count; i++) {
            result[i] = Float.parseFloat(parts[i + 1]);
        }

        return result;
    }

    private void parseFace(String line) {
        if (currentGroup == null) {
            currentGroup = new GroupObject("default");
        }

        String[] vertexData = line.substring(2).trim().split("\\s+");
        Face face = new Face(smoothing);

        // Определяем формат грани
        String[] firstVertex = vertexData[0].split("/");
        boolean hasTexCoords = firstVertex.length > 1 && !firstVertex[1].isEmpty();
        boolean hasNormals = firstVertex.length > 2 && !firstVertex[2].isEmpty();

        // Определяем режим рисования
        if (currentGroup.glDrawingMode == -1) {
            currentGroup.glDrawingMode = vertexData.length == 3 ?
                    org.lwjgl.opengl.GL11.GL_TRIANGLES :
                    org.lwjgl.opengl.GL11.GL_QUADS;
        }

        // Разбираем вершины грани
        face.vertices = new Vertex[vertexData.length];
        if (hasTexCoords) face.textureCoordinates = new TextureCoordinate[vertexData.length];
        if (hasNormals) face.vertexNormals = new Vertex[vertexData.length];

        for (int i = 0; i < vertexData.length; i++) {
            String[] indices = vertexData[i].split("/");

            // Индексы в OBJ начинаются с 1
            int vIdx = Integer.parseInt(indices[0]) - 1;
            face.vertices[i] = vertices.get(vIdx);

            if (hasTexCoords && indices.length > 1 && !indices[1].isEmpty()) {
                int tIdx = Integer.parseInt(indices[1]) - 1;
                face.textureCoordinates[i] = texCoords.get(tIdx);
            }

            if (hasNormals && indices.length > 2 && !indices[2].isEmpty()) {
                int nIdx = Integer.parseInt(indices[2]) - 1;
                face.vertexNormals[i] = normals.get(nIdx);
            }
        }

        currentGroup.faces.add(face);
    }

    private void parseGroup(String line) {
        if (currentGroup != null) {
            groups.add(currentGroup);
            groupMap.put(currentGroup.name, currentGroup);
        }

        // Создаем новую группу
        String name = line.substring(2).trim();
        if (name.isEmpty()) {
            name = "unnamed_" + groups.size();
        }

        currentGroup = new GroupObject(name);
    }

    public void renderAll(PoseStack poseStack, MultiBufferSource buffer,
                          ResourceLocation texture, int packedLight, int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
        renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    public void renderAll(PoseStack poseStack, VertexConsumer builder,
                          int packedLight, int packedOverlay) {
        for (GroupObject group : groups) {
            group.render(builder, poseStack.last(), packedLight, packedOverlay);
        }
    }

    public void renderAllColored(PoseStack poseStack, VertexConsumer builder,
                                 int packedLight, int packedOverlay,
                                 float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        for (GroupObject group : groups) {
            group.renderColored(builder, pose, packedLight, packedOverlay, r, g, b, a);
        }
    }

    public void renderAllColored(PoseStack poseStack, MultiBufferSource buffer,
                                 ResourceLocation texture, int packedLight, int packedOverlay,
                                 float r, float g, float b, float a) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
        renderAllColored(poseStack, builder, packedLight, packedOverlay, r, g, b, a);
    }

    public void renderPart(PoseStack poseStack, MultiBufferSource buffer,
                           String partName, ResourceLocation texture,
                           int packedLight, int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
        renderPart(poseStack, builder, partName, packedLight, packedOverlay);
    }

    public void renderPart(PoseStack poseStack, VertexConsumer builder,
                           String partName, int packedLight, int packedOverlay) {
        GroupObject group = groupMap.get(partName);
        if (group != null) {
            group.render(builder, poseStack.last(), packedLight, packedOverlay);
        }
    }

    public void renderPartColored(PoseStack poseStack, VertexConsumer builder,
                                  String partName, int packedLight, int packedOverlay,
                                  float r, float g, float b, float a) {
        GroupObject group = groupMap.get(partName);
        if (group != null) {
            group.renderColored(builder, poseStack.last(), packedLight, packedOverlay, r, g, b, a);
        }
    }

    public void renderPartColored(PoseStack poseStack, MultiBufferSource buffer,
                                  String partName, ResourceLocation texture,
                                  int packedLight, int packedOverlay,
                                  float r, float g, float b, float a) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
        renderPartColored(poseStack, builder, partName, packedLight, packedOverlay, r, g, b, a);
    }

    public void renderAllExcept(PoseStack poseStack, MultiBufferSource buffer,
                                String excludedPartName, ResourceLocation texture,
                                int packedLight, int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
        renderAllExcept(poseStack, builder, excludedPartName, packedLight, packedOverlay);
    }

    public void renderAllExcept(PoseStack poseStack, VertexConsumer builder,
                                String excludedPartName, int packedLight, int packedOverlay) {
        PoseStack.Pose pose = poseStack.last();

        for (GroupObject group : groups) {
            if (!group.name.equals(excludedPartName)) {
                group.render(builder, pose, packedLight, packedOverlay);
            }
        }
    }

    public void renderAllExcept(PoseStack poseStack, MultiBufferSource buffer,
                                Collection<String> excludedParts, ResourceLocation texture,
                                int packedLight, int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
        renderAllExcept(poseStack, builder, excludedParts, packedLight, packedOverlay);
    }

    public void renderAllExcept(PoseStack poseStack, VertexConsumer builder,
                                Collection<String> excludedParts, int packedLight, int packedOverlay) {
        PoseStack.Pose pose = poseStack.last();

        for (GroupObject group : groups) {
            if (!excludedParts.contains(group.name)) {
                group.render(builder, pose, packedLight, packedOverlay);
            }
        }
    }

    public List<String> getPartNames() {
        List<String> names = new ArrayList<>();
        for (GroupObject group : groups) {
            names.add(group.name);
        }
        return names;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides,
                           ResourceLocation modelLocation) {
        return new BakedHFRModel(this, context, spriteGetter, modelState, overrides);
    }

    // Загрузчик для Forge
    public static class Loader implements IGeometryLoader<HFRWavefrontObject> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public HFRWavefrontObject read(JsonObject json, JsonDeserializationContext context) {
            ResourceLocation modelLocation = ResLocation(RefStrings.MODID,
                    GsonHelper.getAsString(json, "model")
            );
            boolean smoothing = GsonHelper.getAsBoolean(json, "smoothing", true);
            return new HFRWavefrontObject(modelLocation, smoothing);
        }
    }
}

// BakedModel обертка для интеграции с системой моделей Minecraft
class BakedHFRModel implements IDynamicBakedModel {
    private final HFRWavefrontObject model;
    private final TextureAtlasSprite particleTexture;

    public BakedHFRModel(HFRWavefrontObject model, IGeometryBakingContext context,
                         Function<Material, TextureAtlasSprite> spriteGetter,
                         ModelState ignoredModelState, ItemOverrides ignoredOverrides) {
        this.model = model;
        this.particleTexture = spriteGetter.apply(context.getMaterial("particle"));
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state,
                                             @Nullable net.minecraft.core.Direction side,
                                             @NotNull RandomSource rand,
                                             @NotNull ModelData extraData,
                                             @Nullable RenderType renderType) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return particleTexture;
    }

    public HFRWavefrontObject getModel() {
        return model;
    }
}