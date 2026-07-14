package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityDuck;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderDuck extends MobRenderer<EntityDuck, ChickenModel<EntityDuck>> {

    private static final ResourceLocation DUCK_TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/duck.png");

    public RenderDuck(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    @Override
    protected void scale(@NotNull EntityDuck entity, @NotNull PoseStack poseStack, float partialTick) {
        // Утки меньше куриц (или такого же размера)
        super.scale(entity, poseStack, partialTick);
        // Можно добавить кастомный скейл если нужно
        // poseStack.scale(1.0F, 1.0F, 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityDuck entity) {
        return DUCK_TEXTURE;
    }
}