package com.hbm.render.entity.item;

import com.hbm.entity.item.EntityFlamePonyPainting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Painting;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class FlamePonyPaintingRenderer extends PaintingRenderer {

    private static final ResourceLocation FLAME_PONY_TEXTURE = ResLocation(MODID, "textures/painting/flame_pony.png");

    public FlamePonyPaintingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Painting entity) {
        if (entity instanceof EntityFlamePonyPainting) {
            return FLAME_PONY_TEXTURE;
        }
        return super.getTextureLocation(entity);
    }
}
