package com.hbm.render.projectile;

import com.hbm.entity.projectile.EntityRubble;

import com.hbm.render.model.ModelRubble;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderRubble extends EntityRenderer<EntityRubble> {

    private final ModelRubble model;

    public RenderRubble(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ModelRubble();
    }

    @Override
    public void render(@NotNull EntityRubble entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        poseStack.translate(0, 0, 0);

        poseStack.scale(1.0F, 1.0F, 1.0F);

        poseStack.mulPose(new Quaternionf().rotateX((float) Math.PI));

        float rotation = (entity.tickCount + partialTicks) * 10;
        poseStack.mulPose(new Quaternionf().rotateAxis(
                (float) Math.toRadians(rotation),
                new Vector3f(1, 1, 1).normalize()
        ));

        try {
            int blockId = entity.getEntityData().get(EntityRubble.BLOCK_ID);

            Block block = Block.stateById(blockId).getBlock();
            if (block == Blocks.AIR) {
                block = Blocks.STONE;
            }

            ResourceLocation texture = getBlockTexture(block);

            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));
            model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        } catch (Exception e) {
            // Игнорируем ошибки как в оригинале
        }

        poseStack.popPose();
    }

    private ResourceLocation getBlockTexture(Block block) {
        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
        if (registryName == null) {
            return ResLocation("minecraft", "textures/block/dirt.png");
        }

        ResourceLocation texture = ResLocation(
                registryName.getNamespace(),
                "textures/block/" + registryName.getPath() + ".png"
        );

        if (doesTextureExist(texture)) {
            return texture;
        }

        return ResLocation("minecraft", "textures/block/dirt.png");
    }

    private boolean doesTextureExist(ResourceLocation texture) {
        return Minecraft.getInstance().getResourceManager()
                .getResource(texture).isPresent();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityRubble entity) {
        return ResLocation(RefStrings.MODID, "textures/models/ModelRubbleScrap.png");
    }
}