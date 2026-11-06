package com.hbm.render.block.ct;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

/**
 * Загрузчик геометрии CT-блока: в JSON модели достаточно ссылки на текстуру и необязательного
 * {@code "render_type": "translucent"} для стёкол — всё остальное считает {@link CTBakedModel}.
 * Лоадер общий для всех семейств: deco, platemetal, катушки, стекло, провод.
 */
public class CTGeometry implements IUnbakedGeometry<CTGeometry> {

    private final boolean translucent;

    public CTGeometry(boolean translucent) {
        this.translucent = translucent;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides,
                           ResourceLocation modelLocation) {
        return new CTBakedModel(context, spriteGetter, translucent);
    }

    public static class Loader implements IGeometryLoader<CTGeometry> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public CTGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            boolean translucent = jsonObject.has("render_type")
                    && "translucent".equals(jsonObject.get("render_type").getAsString());
            return new CTGeometry(translucent);
        }
    }
}
