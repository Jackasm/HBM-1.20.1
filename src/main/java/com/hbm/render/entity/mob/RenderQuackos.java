package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityQuackos;
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
public class RenderQuackos extends MobRenderer<EntityQuackos, ChickenModel<EntityQuackos>> {

    private static final ResourceLocation DUCK_TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/duck.png");

    public RenderQuackos(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 7.5F);
    }

    @Override
    protected void scale(@NotNull EntityQuackos entity, PoseStack poseStack, float partialTick) {
        // Огромный размер - 25x
        poseStack.scale(25.0F, 25.0F, 25.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityQuackos entity) {
        return DUCK_TEXTURE;
    }
}