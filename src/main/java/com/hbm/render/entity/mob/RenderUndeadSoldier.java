package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityUndeadSoldier;
import com.hbm.render.model.ModelUndeadSoldier;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderUndeadSoldier extends MobRenderer<EntityUndeadSoldier, HumanoidModel<EntityUndeadSoldier>> {

    private static final ResourceLocation TEXTURE_ZOMBIE = ResLocation(RefStrings.MODID, "textures/entity/undead_soldier_zombie.png");
    private static final ResourceLocation TEXTURE_SKELETON = ResLocation(RefStrings.MODID, "textures/entity/undead_soldier_skeleton.png");

    private final HumanoidModel<EntityUndeadSoldier> modelZombie;
    private final HumanoidModel<EntityUndeadSoldier> modelSkeleton;

    public RenderUndeadSoldier(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);

        this.modelZombie = new ModelUndeadSoldier(context.bakeLayer(ModelLayers.ZOMBIE));
        this.modelSkeleton = new ModelUndeadSoldier(context.bakeLayer(ModelLayers.SKELETON));

        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)),
                context.getModelManager()
        ));

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public void render(@NotNull EntityUndeadSoldier entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        byte type = entity.getEntityData().get(EntityUndeadSoldier.DW_TYPE);
        if (type == EntityUndeadSoldier.TYPE_ZOMBIE) {
            this.model = modelZombie;
        } else if (type == EntityUndeadSoldier.TYPE_SKELETON) {
            this.model = modelSkeleton;
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityUndeadSoldier entity) {
        byte type = entity.getEntityData().get(EntityUndeadSoldier.DW_TYPE);
        if (type == EntityUndeadSoldier.TYPE_ZOMBIE) {
            return TEXTURE_ZOMBIE;
        }
        return TEXTURE_SKELETON;
    }
}