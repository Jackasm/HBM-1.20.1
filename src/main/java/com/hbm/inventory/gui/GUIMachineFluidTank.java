package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineFluidTank;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.network.PacketDispatcher;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIMachineFluidTank extends AbstractContainerScreen<ContainerMachineFluidTank> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/storage/gui_tank.png");

    private short lastMode = -1;
    private int lastFluidFill = -1;
    private int lastFluidType = -1;

    public GUIMachineFluidTank(ContainerMachineFluidTank container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void containerTick() {
        super.containerTick();

        // Проверяем изменения и принудительно обновляем GUI если нужно
        short currentMode = menu.getMode();
        int currentFluidFill = menu.getFluidFill();
        int currentFluidType = menu.getFluidType();

        if (currentMode != lastMode || currentFluidFill != lastFluidFill || currentFluidType != lastFluidType) {
            lastMode = currentMode;
            lastFluidFill = currentFluidFill;
            lastFluidType = currentFluidType;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Получаем данные из menu
        int fill = menu.getFluidFill();
        int capacity = menu.getFluidCapacity();
        FluidTypeHBM fluidType = Fluids.fromID(menu.getFluidType());
        short mode = menu.getMode();

        // Отрисовка уровня жидкости
        renderFluidTank(guiGraphics, fill, capacity, fluidType);

        // Отрисовка кнопки режима
        guiGraphics.blit(TEXTURE, leftPos + 151, topPos + 34, 176, mode * 18, 18, 18);
    }

    private void renderFluidTank(GuiGraphics guiGraphics, int fill, int capacity, FluidTypeHBM fluidType) {
        if (fill > 0 && capacity > 0 && fluidType != Fluids.NONE.get()) {
            int fluidHeight = (int) (52 * ((float) fill / (float) capacity));

            ResourceLocation fluidTexture = fluidType.getTexture();

            if (fluidTexture != null) {
                int tankX = leftPos + 71;
                int tankY = topPos + 69 - fluidHeight;

                IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(fluidType);
                int color = fluidExtensions.getTintColor();
                if (color == -1) color = fluidType.getColor();

                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;

                RenderSystem.setShaderColor(r, g, b, 1.0F);

                renderFluidSegment(guiGraphics, fluidTexture, tankX, tankY, 16, fluidHeight);
                renderFluidSegment(guiGraphics, fluidTexture, tankX + 16, tankY, 16, fluidHeight);
                renderFluidSegment(guiGraphics, fluidTexture, tankX + 32, tankY, 2, fluidHeight);

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                // Fallback - цветной прямоугольник
                int color = fluidType.getColor();
                guiGraphics.fill(leftPos + 71, topPos + 69 - fluidHeight,
                        leftPos + 71 + 34, topPos + 69, color | 0xFF000000);
            }
        }
    }

    private void renderFluidSegment(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height) {
        if (height <= 0) return;
        guiGraphics.blit(texture, x, y, 0, 0, width, height, width, height);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String title = Component.translatable("container.fluidtank").getString();
        guiGraphics.drawString(this.font, title, this.imageWidth / 2 - this.font.width(title) / 2, 6, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Тултип для бака с жидкостью
        if (isHovering(71, 17, 34, 52, mouseX, mouseY)) {
            int fill = menu.getFluidFill();
            int capacity = menu.getFluidCapacity();
            FluidTypeHBM fluidType = Fluids.fromID(menu.getFluidType());

            List<Component> tooltip = new ArrayList<>();

            if (fluidType != Fluids.NONE.get()) {
                tooltip.add(Component.literal(fluidType.getLocalizedName()));
            } else {
                tooltip.add(Component.literal("Empty"));
            }

            tooltip.add(Component.literal("§7" + fill + "/" + capacity + "mB"));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }

        // Тултип для кнопки режима
        if (isHovering(151, 34, 18, 18, mouseX, mouseY)) {
            short mode = menu.getMode();
            List<Component> tooltip = new ArrayList<>();

            switch (mode) {
                case 0 -> {
                    tooltip.add(Component.literal("§aMode: Input Only"));
                    tooltip.add(Component.literal("§7Accepts fluid from network"));
                    tooltip.add(Component.literal("§7Does not provide fluid"));
                }
                case 1 -> {
                    tooltip.add(Component.literal("§eMode: Input & Output"));
                    tooltip.add(Component.literal("§7Both accepts and provides fluid"));
                }
                case 2 -> {
                    tooltip.add(Component.literal("§6Mode: Output Only"));
                    tooltip.add(Component.literal("§7Provides fluid to network"));
                    tooltip.add(Component.literal("§7Does not accept fluid"));
                }
            }

            tooltip.add(Component.literal("§7Click to cycle mode"));
            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovering(151, 34, 18, 18, mouseX, mouseY) && button == 0) {
            PacketDispatcher.sendAuxButtonPacket(menu.getTank().getBlockPos(), 0, 0);
            playButtonClickSound();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playButtonClickSound() {
        assert minecraft != null;
        minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)
        );
    }
}