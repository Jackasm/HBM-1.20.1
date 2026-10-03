package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityEncounterVillager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class EncounterVillagerRenderer extends VillagerRenderer {

    private static final ResourceLocation TEXTURE_HOSTILE =
            ResLocation("minecraft", "textures/entity/zombie_villager/zombie_villager.png");

    public EncounterVillagerRenderer(EntityRendererProvider.Context context) {
        super(context);

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));

        // Inner (для леггинсов) и outer (для остального) — ДВЕ разные сетки,
        // сгенерированные ванилой. Пропорции = игрок. Текстуры layer_1/layer_2
        // нарисованы именно под них.
        HumanoidModel<EntityEncounterVillager> inner =
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        HumanoidModel<EntityEncounterVillager> outer =
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));

        this.addLayer(new EncounterVillagerArmorLayer(this, inner, outer));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityEncounterVillager entity) {
        if (entity.isHostile()) {
            return TEXTURE_HOSTILE;
        }
        return super.getTextureLocation(entity);
    }
}
