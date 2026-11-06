package com.hbm_addons.render.entity.mob;

import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm.render.model.ModelFBI;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class EncounterVillagerRenderer extends HumanoidMobRenderer<EntityEncounterVillager, ModelFBI<EntityEncounterVillager>> {

    private static final ResourceLocation TEXTURE_FRIENDLY =
            ResLocation("minecraft", "textures/entity/player/wide/steve.png");
    private static final ResourceLocation TEXTURE_HOSTILE =
            ResLocation("hbm", "textures/entity/fbi.png");

    public EncounterVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelFBI<>(context.bakeLayer(ModelFBI.LAYER_LOCATION)), 0.5F);

        // Броня — ванильный слой, работает с HumanoidModel
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));

        // Оружие в руке
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityEncounterVillager entity) {
        return entity.isHostile() ? TEXTURE_HOSTILE : TEXTURE_FRIENDLY;
    }
}
