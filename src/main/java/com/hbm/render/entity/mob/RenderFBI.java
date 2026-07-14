package com.hbm.render.entity.mob;

import com.hbm.render.model.ModelFBI;
import com.hbm.util.RefStrings;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderFBI extends HumanoidMobRenderer<Mob, ModelFBI> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/fbi.png");

    public RenderFBI(EntityRendererProvider.Context context) {
        super(context, new ModelFBI(context.bakeLayer(ModelFBI.LAYER_LOCATION)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new ModelFBI(context.bakeLayer(ModelFBI.LAYER_LOCATION)),
                new ModelFBI(context.bakeLayer(ModelFBI.LAYER_LOCATION)),
                context.getModelManager()));
        // ItemInHandLayer уже добавляется в HumanoidMobRenderer, но если нужно добавить свой:
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Mob entity) {
        return TEXTURE;
    }
}