package com.hbm.render.item;

import com.hbm.items.machine.ItemBatteryPack;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class BatteryPackRenderer extends BlockEntityWithoutLevelRenderer {

    public static final BatteryPackRenderer INSTANCE = new BatteryPackRenderer();

    public BatteryPackRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext ctx, @NotNull PoseStack pose,
                             @NotNull MultiBufferSource buffer, int light, int overlay) {

        if (!(stack.getItem() instanceof ItemBatteryPack packItem)) return;
        ItemBatteryPack.EnumBatteryPack type = packItem.type;

        pose.pushPose();

        // ---- трансформации в зависимости от контекста ----
        switch (ctx) {
            case GUI -> {
                pose.translate(0.5D, 0.3125D, 0.5D);
                pose.scale(0.3125F, 0.3125F, 0.3125F);
                pose.mulPose(Axis.XP.rotationDegrees(15F));
                pose.mulPose(Axis.YP.rotationDegrees(-45F));
            }
            case GROUND -> {
                pose.translate(0.5D, 0.25D, 0.5D);
                pose.scale(0.25F, 0.25F, 0.25F);
            }
            case FIXED -> pose.translate(0.5D, 0.5D, 0.5D);
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                pose.translate(0.5D, 0.5D, 0.5D);
                pose.scale(0.25F, 0.25F, 0.25F);
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                pose.translate(0.5D, 0.5D, 0.5D);
                pose.scale(0.25F, 0.25F, 0.25F);
            }
            default -> pose.translate(0.5D, 0.5D, 0.5D);
        }

        // ---- рендер OBJ ----
        ResourceLocation texture = type.getTexture();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));

        String part = type.isCapacitor() ? "Capacitor" : "Battery";
        HBMResourceManager.battery_socket.renderPart(pose, consumer, part, light, OverlayTexture.NO_OVERLAY);

        pose.popPose();
    }

    /** Хелпер для IClientItemExtensions */
    public static IClientItemExtensions clientExtensions() {
        return new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return INSTANCE;
            }
        };
    }
}