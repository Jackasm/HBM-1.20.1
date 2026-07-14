package com.hbm.render.entity.effect;


import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderFireLingering extends EntityRenderer<EntityFireLingering> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/models/thegadget3.png");

    public RenderFireLingering(EntityRendererProvider.Context context) {
        super(context);

    }

    @Override
    public void render(@NotNull EntityFireLingering entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityFireLingering entity) {
        return TEXTURE;
    }
}