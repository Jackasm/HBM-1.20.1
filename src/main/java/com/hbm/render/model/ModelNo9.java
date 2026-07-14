package com.hbm.render.model;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.ModelRendererObj;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ModelNo9 extends ModelArmorBase {

    public static final ModelLayerLocation NO9 = new ModelLayerLocation(
            ResLocation(RefStrings.MODID, "no9"), "main");

    private ModelRendererObj lamp;
    private ModelRendererObj insignia;

    public ModelNo9(ModelPart root) {
        super(0, root);

        this.head = new ModelRendererObj(HBMResourceManager.armor_no9, "Helmet");
        this.insignia = new ModelRendererObj(HBMResourceManager.armor_no9, "Insignia");
        this.lamp = new ModelRendererObj(HBMResourceManager.armor_no9, "Flame");
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {

        if (this.type != 0) return;
        float scale = 0.0625f;
        poseStack.pushPose();

        // Копируем повороты головы
        this.head.copyTo(this.insignia);
        this.head.copyTo(this.lamp);

        // Получаем сущность из контекста (нужно передавать через поле)
        if (this.entity != null) {
            this.setupAnim(this.entity, 0, 0, 0, 0, 0);
        }

        // Рендерим основную модель шлема
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer headConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.no9_tex));
        this.head.render(poseStack, headConsumer, packedLight, packedOverlay, scale);

        // Рендерим инсигнию
        VertexConsumer insigniaConsumer = buffer.getBuffer(RenderType.entityCutout(HBMResourceManager.no9_insignia_tex));
        this.insignia.render(poseStack, insigniaConsumer, packedLight, packedOverlay, scale);

        // Проверяем, включена ли подсветка
        if (entity instanceof Player player) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!helmet.isEmpty() && helmet.hasTag() && Objects.requireNonNull(helmet.getTag()).getBoolean("isOn")) {
                // Рендерим лампу с ярким светом
                int light = 0xF000F0; // Максимальная яркость

                VertexConsumer lampConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(HBMResourceManager.no9_tex));
                poseStack.pushPose();

                // Применяем жёлтый цвет
                this.lamp.render(poseStack, lampConsumer, light, OverlayTexture.NO_OVERLAY, scale);

                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }
}