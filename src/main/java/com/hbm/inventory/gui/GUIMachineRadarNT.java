package com.hbm.inventory.gui;

import com.hbm.api.entity.RadarEntry;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.machine.TileEntityMachineRadarNT;
import com.hbm.util.BobMathUtil;

import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIMachineRadarNT extends Screen {

    public static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_radar_nt.png");

    protected final TileEntityMachineRadarNT radar;
    protected int imageWidth = 216;
    protected int imageHeight = 234;
    protected int leftPos;
    protected int topPos;

    public int lastMouseX;
    public int lastMouseY;

    public GUIMachineRadarNT(TileEntityMachineRadarNT tile) {
        super(Component.translatable("container.radar"));
        this.radar = tile;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        String cmd = null;

        if (checkClick(mouseX, mouseY, -10, 88, 8, 8)) cmd = "missiles";
        if (checkClick(mouseX, mouseY, -10, 98, 8, 8)) cmd = "shells";
        if (checkClick(mouseX, mouseY, -10, 108, 8, 8)) cmd = "players";
        if (checkClick(mouseX, mouseY, -10, 118, 8, 8)) cmd = "smart";
        if (checkClick(mouseX, mouseY, -10, 128, 8, 8)) cmd = "red";
        if (checkClick(mouseX, mouseY, -10, 138, 8, 8)) cmd = "map";
        if (checkClick(mouseX, mouseY, -10, 158, 8, 8)) cmd = "gui1";
        if (checkClick(mouseX, mouseY, -10, 178, 8, 8)) cmd = "clear";

        if (cmd != null) {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F)
            );
            CompoundTag data = new CompoundTag();
            data.putBoolean(cmd, true);
            PacketDispatcher.sendToServer(new NBTControlPacket(data, radar.getBlockPos()));
        }
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderBg(graphics, partialTicks, mouseX, mouseY);
        this.renderLabels(graphics, mouseX, mouseY);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        if (checkClick(mouseX, mouseY, 8, 221, 200, 7)) {
            graphics.renderTooltip(font,
                    Component.literal(BobMathUtil.getShortNumber(radar.getPower()) + "/" + BobMathUtil.getShortNumber(radar.getMaxPower()) + "HE"),
                    mouseX, mouseY);
        }

        if (checkClick(mouseX, mouseY, -10, 88, 8, 8)) {
            graphics.renderTooltip(font, Component.translatable("radar.detectMissiles"), mouseX, mouseY);
        }
        if (checkClick(mouseX, mouseY, -10, 98, 8, 8)) {
            graphics.renderTooltip(font,Component.translatable("radar.detectShells"), mouseX, mouseY);
        }
        if (checkClick(mouseX, mouseY, -10, 108, 8, 8)) {
            graphics.renderTooltip(font, Component.translatable("radar.detectPlayers"), mouseX, mouseY);
        }
        if (checkClick(mouseX, mouseY, -10, 118, 8, 8)) {
            graphics.renderTooltip(font, Component.translatable("radar.smartMode"), mouseX, mouseY);
        }
        if (checkClick(mouseX, mouseY, -10, 128, 8, 8)) {
            graphics.renderTooltip(font, Component.translatable("radar.redMode"), mouseX, mouseY);
        }
        if (checkClick(mouseX, mouseY, -10, 138, 8, 8)) {
            graphics.renderTooltip(font, Component.translatable("radar.showMap"), mouseX, mouseY);
        }
        if (checkClick(mouseX, mouseY, -10, 158, 8, 8)) {
            graphics.renderTooltip(font, Component.translatable("radar.toggleGui"), mouseX, mouseY);
        }
        if (checkClick(mouseX, mouseY, -10, 178, 8, 8)) {
            graphics.renderTooltip(font, Component.translatable("radar.clearMap"), mouseX, mouseY);
        }

        if (!radar.entries.isEmpty()) {
            for (RadarEntry m : radar.entries) {
                int x = leftPos + (int) ((m.posX - radar.getBlockPos().getX()) / ((double) radar.getRange() * 2 + 1) * (200D - 8D)) + 108;
                int z = topPos + (int) ((m.posZ - radar.getBlockPos().getZ()) / ((double) radar.getRange() * 2 + 1) * (200D - 8D)) + 117;

                if (mouseX + 5 > x && mouseX - 4 <= x && mouseY + 5 > z && mouseY - 4 <= z) {
                    graphics.renderTooltip(font,
                            List.of(
                                    Component.translatable(m.unlocalizedName),
                                    Component.literal(m.posX + " / " + m.posZ),
                                    Component.literal("Alt.: " + m.posY)
                            ),
                            Optional.empty(),
                            x, z);
                    return;
                }
            }
        }

        if (checkClick(mouseX, mouseY, 8, 17, 200, 200)) {
            int tX = (int) ((lastMouseX - leftPos - 108) * ((double) radar.getRange() * 2 + 1) / 192D + radar.getBlockPos().getX());
            int tZ = (int) ((lastMouseY - topPos - 117) * ((double) radar.getRange() * 2 + 1) / 192D + radar.getBlockPos().getZ());
            graphics.renderTooltip(font, Component.literal(tX + " / " + tZ), lastMouseX, lastMouseY);
        }
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        graphics.blit(TEXTURE, leftPos - 14, topPos + 84, 224, 0, 14, 66);
        graphics.blit(TEXTURE, leftPos - 14, topPos + 154, 224, 66, 14, 36);

        if (radar.getPower() > 0) {
            int i = (int) (radar.getPower() * 200 / radar.getMaxPower());
            graphics.blit(TEXTURE, leftPos + 8, topPos + 221, 0, 234, i, 16);
        }

        // Кнопки настроек
        if (radar.scanMissiles ^ (radar.jammed && Objects.requireNonNull(radar.getLevel()).random.nextBoolean())) {
            graphics.blit(TEXTURE, leftPos - 10, topPos + 88, 238, 4, 8, 8);
        }
        if (radar.scanShells ^ (radar.jammed && Objects.requireNonNull(radar.getLevel()).random.nextBoolean())) {
            graphics.blit(TEXTURE, leftPos - 10, topPos + 98, 238, 14, 8, 8);
        }
        if (radar.scanPlayers ^ (radar.jammed && Objects.requireNonNull(radar.getLevel()).random.nextBoolean())) {
            graphics.blit(TEXTURE, leftPos - 10, topPos + 108, 238, 24, 8, 8);
        }
        if (radar.smartMode ^ (radar.jammed && Objects.requireNonNull(radar.getLevel()).random.nextBoolean())) {
            graphics.blit(TEXTURE, leftPos - 10, topPos + 118, 238, 34, 8, 8);
        }
        if (radar.redMode ^ (radar.jammed && Objects.requireNonNull(radar.getLevel()).random.nextBoolean())) {
            graphics.blit(TEXTURE, leftPos - 10, topPos + 128, 238, 44, 8, 8);
        }
        if (radar.showMap ^ (radar.jammed && Objects.requireNonNull(radar.getLevel()).random.nextBoolean())) {
            graphics.blit(TEXTURE, leftPos - 10, topPos + 138, 238, 54, 8, 8);
        }

        if (radar.getPower() < TileEntityMachineRadarNT.consumption) return;

        if (radar.jammed) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    graphics.blit(TEXTURE, leftPos + 8 + i * 40, topPos + 17 + j * 40,
                            216, 118 + Objects.requireNonNull(radar.getLevel()).random.nextInt(81), 40, 40);
                }
            }
            return;
        }

        if (radar.showMap) {
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            for (int i = 0; i < 40_000; i++) {
                int iX = i % 200;
                int iZ = i / 200;
                byte b = radar.map[i];
                if (b > 0) {
                    int color = ((b - 50) * 255 / 78) << 8;
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int blue = color & 0xFF;
                    float f = 0.0F;
                    buffer.vertex(leftPos + 8 + iX, topPos + 18 + iZ, f).color(r, g, blue, 255).endVertex();
                    buffer.vertex(leftPos + 9 + iX, topPos + 18 + iZ, f).color(r, g, blue, 255).endVertex();
                    buffer.vertex(leftPos + 9 + iX, topPos + 17 + iZ, f).color(r, g, blue, 255).endVertex();
                    buffer.vertex(leftPos + 8 + iX, topPos + 17 + iZ, f).color(r, g, blue, 255).endVertex();
                }
            }
            tessellator.end();
        }

        // Вектор направления радара
        Vec3 tr = new Vec3(100, 0, 0);
        Vec3 tl = new Vec3(100, 0, 0);
        Vec3 bl = new Vec3(0, -5, 0);
        float rot = (float) -Math.toRadians(radar.prevRotation + (radar.rotation - radar.prevRotation) * partialTicks + 180F);
        tr = tr.yRot(rot);
        tl = tl.yRot(rot + 0.25F);
        bl = bl.yRot(rot);

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(leftPos + 108, topPos + 117, 0).color(0, 255, 0, 0).endVertex();
        buffer.vertex(leftPos + 108 + tr.x, topPos + 117 + tr.z, 0).color(0, 255, 0, 255).endVertex();
        buffer.vertex(leftPos + 108 + tl.x, topPos + 117 + tl.z, 0).color(0, 255, 0, 0).endVertex();
        buffer.vertex(leftPos + 108 + bl.x, topPos + 117 + bl.z, 0).color(0, 255, 0, 0).endVertex();
        tessellator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        // Отображение целей
        if (!radar.entries.isEmpty()) {
            for (RadarEntry m : radar.entries) {
                double x = (m.posX - radar.getBlockPos().getX()) / ((double) radar.getRange() * 2 + 1) * (200D - 8D) - 4D;
                double z = (m.posZ - radar.getBlockPos().getZ()) / ((double) radar.getRange() * 2 + 1) * (200D - 8D) - 4D;
                int t = m.blipLevel;
                drawTexturedModalRectDouble(graphics, leftPos + 108 + x, topPos + 117 + z, 216, 8 * t, 8, 8);
            }
        }
    }

    public void drawTexturedModalRectDouble(GuiGraphics graphics, double x, double y, int sourceX, int sourceY, int sizeX, int sizeY) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        RenderSystem.setShaderTexture(0, TEXTURE);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x, y + sizeY, 0).uv((sourceX + 0) * f, (sourceY + sizeY) * f1).endVertex();
        buffer.vertex(x + sizeX, y + sizeY, 0).uv((sourceX + sizeX) * f, (sourceY + sizeY) * f1).endVertex();
        buffer.vertex(x + sizeX, y, 0).uv((sourceX + sizeX) * f, (sourceY + 0) * f1).endVertex();
        buffer.vertex(x, y, 0).uv((sourceX + 0) * f, (sourceY + 0) * f1).endVertex();
        tessellator.end();
    }

    protected boolean checkClick(double x, double y, int left, int top, int sizeX, int sizeY) {
        return leftPos + left <= x && leftPos + left + sizeX > x && topPos + top < y && topPos + top + sizeY >= y;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }

        if (checkClick(lastMouseX, lastMouseY, 8, 17, 200, 200) && keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_8) {
            int id = keyCode - GLFW.GLFW_KEY_1;

            if (!radar.entries.isEmpty()) {
                for (RadarEntry m : radar.entries) {
                    int x = leftPos + (int) ((m.posX - radar.getBlockPos().getX()) / ((double) radar.getRange() * 2 + 1) * (200D - 8D)) + 108;
                    int z = topPos + (int) ((m.posZ - radar.getBlockPos().getZ()) / ((double) radar.getRange() * 2 + 1) * (200D - 8D)) + 117;

                    if (lastMouseX + 5 > x && lastMouseX - 4 <= x && lastMouseY + 5 > z && lastMouseY - 4 <= z) {
                        CompoundTag data = new CompoundTag();
                        data.putInt("launchEntity", m.entityID);
                        data.putInt("link", id);
                        PacketDispatcher.sendToServer(new NBTControlPacket(data, radar.getBlockPos()));
                        return true;
                    }
                }
            }

            int tX = (int) ((lastMouseX - leftPos - 108) * ((double) radar.getRange() * 2 + 1) / 192D + radar.getBlockPos().getX());
            int tZ = (int) ((lastMouseY - topPos - 117) * ((double) radar.getRange() * 2 + 1) / 192D + radar.getBlockPos().getZ());
            CompoundTag data = new CompoundTag();
            data.putInt("launchPosX", tX);
            data.putInt("launchPosZ", tZ);
            data.putInt("link", id);
            PacketDispatcher.sendToServer(new NBTControlPacket(data, radar.getBlockPos()));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).isAlive() || this.minecraft.player.isRemoved()) {
            this.onClose();
        }
    }
}