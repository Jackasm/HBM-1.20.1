package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityPigeon;
import com.hbm.render.model.ModelPigeon;
import com.hbm.util.RefStrings;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Рендер голубя. Тень и текстура — как в 1.7.10; взмах крыла считает модель,
 * потому что {@code handleRotationFloat}, которым это делал оригинальный рендер,
 * в 1.20.1 у {@link MobRenderer} нет.
 */
@OnlyIn(Dist.CLIENT)
public class RenderPigeon extends MobRenderer<EntityPigeon, ModelPigeon> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/pigeon.png");

    public RenderPigeon(EntityRendererProvider.Context context) {
        super(context, new ModelPigeon(context.bakeLayer(ModelPigeon.LAYER)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityPigeon entity) {
        return TEXTURE;
    }
}
