package com.hbm.render.entity.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderCreeperUniversal<T extends Creeper> extends MobRenderer<T, CreeperModel<T>> {

    private final ResourceLocation creeperTextures;
    private final ResourceLocation armoredCreeperTextures;
    private float swellMod = 1.0F;

    public RenderCreeperUniversal(EntityRendererProvider.Context context, String texture, String overlay) {
        super(context, new CreeperModel<>(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        this.creeperTextures = ResLocation(texture);
        this.armoredCreeperTextures = ResLocation(overlay);
    }

    public RenderCreeperUniversal<T> setSwellMod(float mod) {
        this.swellMod = mod;
        return this;
    }

    @Override
    protected void scale(T entity, @NotNull PoseStack poseStack, float partialTick) {
        float swell = entity.getSwelling(partialTick);
        float flash = 1.0F + Mth.sin(swell * 100.0F) * swell * 0.01F;

        if (swell < 0.0F) {
            swell = 0.0F;
        }

        if (swell > 1.0F) {
            swell = 1.0F;
        }

        swell *= swell;
        swell *= swell;
        swell *= swellMod;
        float scaleHorizontal = (1.0F + swell * 0.4F) * flash;
        float scaleVertical = (1.0F + swell * 0.1F) / flash;
        poseStack.scale(scaleHorizontal, scaleVertical, scaleHorizontal);
    }

    @Override
    protected float getWhiteOverlayProgress(T entity, float partialTick) {
        if (entity.isPowered()) {
            float swell = entity.getSwelling(partialTick);
            return (int) (swell * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swell, 0.5F, 1.0F);
        }
        return 0.0F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        return entity.isPowered() ? armoredCreeperTextures : creeperTextures;
    }
}