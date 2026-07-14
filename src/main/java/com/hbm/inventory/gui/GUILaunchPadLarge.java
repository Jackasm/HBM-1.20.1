package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerLaunchPadLarge;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemMissile;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.tileentity.bomb.TileEntityLaunchPadBase;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUILaunchPadLarge extends AbstractContainerScreen<ContainerLaunchPadLarge> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/weapon/gui_launch_pad_large.png");
    private final TileEntityLaunchPadBase launchpad;

    public GUILaunchPadLarge(ContainerLaunchPadLarge container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.launchpad = container.getLaunchPad();
        this.imageWidth = 176;
        this.imageHeight = 236;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Power tooltip
        if (isHovering(107, 36, 16, 52, mouseX, mouseY)) {
            String text = String.format("%,d / %,d HE", launchpad.getPower(), launchpad.getMaxPower());
            graphics.renderTooltip(font, Component.literal(text), mouseX, mouseY);
        }

        // Tank tooltips
        launchpad.tanks[0].renderTankInfo(graphics, mouseX, mouseY, leftPos + 125, topPos + 36, 16, 52);
        launchpad.tanks[1].renderTankInfo(graphics, mouseX, mouseY, leftPos + 143, topPos + 36, 16, 52);

        // Slot tooltip for designators
        if (this.minecraft != null && this.minecraft.player.containerMenu.getCarried().isEmpty() &&
                this.isHovering(26, 72, 18, 18, mouseX, mouseY) &&
                !this.menu.getSlot(1).hasItem()) {

            ItemStack[] list = new ItemStack[] {
                    new ItemStack(ModItems.DESIGNATOR.get()),
                    new ItemStack(ModItems.DESIGNATOR_RANGE.get()),
                    new ItemStack(ModItems.DESIGNATOR_MANUAL.get())
            };

            ItemStack selected = list[(int) ((System.currentTimeMillis() % (1000 * list.length)) / 1000)];
            selected.setCount(0);

            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal(selected.getHoverName().getString()));

            graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        Component name = this.launchpad.getBlockState().getBlock().getName();

        graphics.drawString(font, name.getString(), this.imageWidth / 2 - font.width(name.getString()) / 2, 4, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int fuel = launchpad.getFuelState();
        int oxidizer = launchpad.getOxidizerState();

        if (fuel == 1) graphics.blit(TEXTURE, leftPos + 130, topPos + 23, 192, 0, 6, 8);
        if (fuel == -1) graphics.blit(TEXTURE, leftPos + 130, topPos + 23, 198, 0, 6, 8);
        if (oxidizer == 1) graphics.blit(TEXTURE, leftPos + 148, topPos + 23, 192, 0, 6, 8);
        if (oxidizer == -1) graphics.blit(TEXTURE, leftPos + 148, topPos + 23, 198, 0, 6, 8);
        if (launchpad.isMissileValid()) {
            graphics.blit(TEXTURE, leftPos + 112, topPos + 23, launchpad.getPower() >= 75_000 ? 192 : 198, 0, 6, 8);
        }

        // Power bar
        int power = (int) (launchpad.getPower() * 52 / launchpad.getMaxPower());
        if (power > 0) {
            graphics.blit(TEXTURE, leftPos + 107, topPos + 88 - power, 176, 52 - power, 16, power);
        }

        // Tanks
        launchpad.tanks[0].renderTank(graphics, leftPos + 125, topPos + 88, 16, 52, 0);
        launchpad.tanks[1].renderTank(graphics, leftPos + 143, topPos + 88, 16, 52, 0);

        ItemStack missileStack = launchpad.getInventory().getStackInSlot(0);
        if (!missileStack.isEmpty()) {
            double scale = getMissileScale(missileStack);
            renderMissile(graphics, missileStack, leftPos + 70, topPos + 120, scale);
        }

        // Status text
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos + 34, topPos + 107, 0);
        String text = "";
        int color = 0xFFFFFF;

        if (launchpad.state == TileEntityLaunchPadBase.STATE_MISSING) {
            graphics.pose().scale(0.5F, 0.5F, 1);
            text = "Not ready";
            color = 0xFF0000;
        } else if (launchpad.state == TileEntityLaunchPadBase.STATE_LOADING) {
            graphics.pose().scale(0.6F, 0.6F, 1);
            text = "Loading...";
            color = 0xFF8000;
        } else if (launchpad.state == TileEntityLaunchPadBase.STATE_READY) {
            graphics.pose().scale(0.8F, 0.8F, 1);
            text = "Ready";
            color = 0x00FF00;
        }

        if (!text.isEmpty()) {
            graphics.drawString(font, text, -font.width(text) / 2, -font.lineHeight / 2, color, false);
        }
        graphics.pose().popPose();
    }

    private void renderMissile(GuiGraphics graphics, ItemStack missileStack, int x, int y, double scale) {
        if (missileStack == null || missileStack.isEmpty()) return;

        var renderer = ItemRenderMissileGeneric.renderers.get(
                new ComparableStack(missileStack).makeSingular()
        );

        if (renderer == null) return;

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 100);
        graphics.pose().mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
        graphics.pose().scale((float) scale, (float) scale, (float) scale);
        graphics.pose().scale(-8, -8, -8);

        graphics.pose().pushPose();
        graphics.pose().mulPose(com.mojang.math.Axis.YP.rotationDegrees(75));
        graphics.pose().popPose();

        var buffer = graphics.bufferSource();
        var context = new ItemRenderMissileGeneric.RenderContext(
                graphics.pose(),
                buffer,
                0xF000F0,
                OverlayTexture.NO_OVERLAY
        );
        renderer.accept(context);

        graphics.flush();

        graphics.pose().popPose();
    }

    private double getMissileScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1.0D;

        if (stack.getItem() instanceof ItemMissile missile) {
            return switch (missile.formFactor) {
                case ABM -> 1.45D;
                case MICRO -> 2.5D;
                case V2 -> 1.75D;
                case STRONG -> 1.375D;
                case HUGE -> 0.925D;
                case ATLAS -> 0.875D;
                default -> 1.0D;
            };
        }
        return 1.0D;
    }

    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w &&
                mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}