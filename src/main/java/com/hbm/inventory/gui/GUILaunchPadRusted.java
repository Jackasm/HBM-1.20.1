package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerLaunchPadRusted;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;

import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.tileentity.bomb.TileEntityLaunchPadRusted;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUILaunchPadRusted extends AbstractContainerScreen<ContainerLaunchPadRusted> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/weapon/gui_launch_pad_rusted.png");
    private final TileEntityLaunchPadRusted launchpad;

    public GUILaunchPadRusted(ContainerLaunchPadRusted container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.launchpad = container.getLaunchpad();
        this.imageWidth = 176;
        this.imageHeight = 236;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Release button tooltip
        if (isHovering(26, 36, 16, 16, mouseX, mouseY)) {
            graphics.renderTooltip(font,
                    Component.literal(ChatFormatting.YELLOW + "Release Missile\n" +
                            "Missile is locked in launch position,\n" +
                            "releasing may cause damage to the missile.\n" +
                            "Damaged missile can not be put back\n" +
                            "into launching position."),
                    mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(26, 36, 16, 16, mouseX, mouseY)) {
            CompoundTag data = new CompoundTag();
            data.putBoolean("release", true);
            PacketDispatcher.sendToServer(new NBTControlPacket(data, launchpad.getBlockPos()));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = launchpad.getDisplayName().getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 4, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        boolean hasCodes = !launchpad.getInventory().getStackInSlot(1).isEmpty() &&
                launchpad.getInventory().getStackInSlot(1).getItem() == ModItems.LAUNCH_CODE.get();
        boolean hasKey = !launchpad.getInventory().getStackInSlot(2).isEmpty() &&
                launchpad.getInventory().getStackInSlot(2).getItem() == ModItems.LAUNCH_KEY.get();

        if (hasCodes) graphics.blit(TEXTURE, leftPos + 121, topPos + 32, 192, 0, 6, 8);
        if (hasKey) graphics.blit(TEXTURE, leftPos + 139, topPos + 32, 192, 0, 6, 8);

        // Launch codes display
        if (hasCodes && hasKey && launchpad.missileLoaded) {
            Random rand = new Random(launchpad.getBlockPos().getX() * 131071 + launchpad.getBlockPos().getZ());
            int launchCodes = rand.nextInt(100_000_000);
            for (int i = 0; i < 8; i++) {
                int magnitude = (int) Math.pow(10, i);
                int digit = (launchCodes % (magnitude * 10)) / magnitude;
                graphics.blit(TEXTURE, leftPos + 109 + 6 * i, topPos + 85, 192 + 6 * digit, 8, 6, 8);
            }
        }

        // Missile render
        if (launchpad.missileLoaded) {
            var renderer = ItemRenderMissileGeneric.renderers.get(
                    new ComparableStack(ModItems.MISSILE_DOOMSDAY_RUSTED.get()).makeSingular()
            );
            if (renderer != null) {
                graphics.pose().pushPose();
                graphics.pose().translate(leftPos + 70, topPos + 120, 100);
                graphics.pose().scale(0.875f, 0.875f, 0.875f);
                graphics.pose().mulPose(com.mojang.math.Axis.YP.rotationDegrees(90));
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
        }
    }

    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w &&
                mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}