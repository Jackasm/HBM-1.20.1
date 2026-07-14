package com.hbm.world.teleporter;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class TeleportationLoadingScreen extends Screen {
    private static final ResourceLocation LOADING_BACKGROUND = ResLocation("hbm", "textures/gui/teleport_loading.png");

    public TeleportationLoadingScreen() {
        super(Component.literal(""));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Рендерим текстуру на весь экран
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(LOADING_BACKGROUND, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        // Текст в нижней части экрана, крупный, фиолетовый
        float scale = 1.5F;
        int textOffsetFromBottom = 18; // расстояние от нижнего края экрана (пиксели)
        int y = this.height - textOffsetFromBottom;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.drawCenteredString(this.font, Component.translatable("hbm.loading.teleport"),
                (int)(this.width / 2 / scale), (int)(y / scale), 0xAA66FF);
        guiGraphics.pose().popPose();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}