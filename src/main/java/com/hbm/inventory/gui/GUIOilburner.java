package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerOilburner;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.network.PacketDispatcher;
import com.hbm.tileentity.machine.TileEntityHeaterOilburner;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIOilburner extends AbstractContainerScreen<ContainerOilburner> {

    private static final ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_oilburner.png");
    private final TileEntityHeaterOilburner oilburner;

    public GUIOilburner(ContainerOilburner container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.oilburner = container.getHeater();

        this.imageWidth = 176;
        this.imageHeight = 203;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Информация о тепле
        drawCustomInfoStat(graphics,
                mouseX,
                mouseY,
                this.leftPos + 116,
                this.topPos + 17,
                16,
                52,
                new String[]{String.format(Locale.US, "%,d", Math.min(oilburner.heatEnergy, oilburner.maxHeatEnergy)) +
                        " / " + String.format(Locale.US, "%,d", oilburner.maxHeatEnergy) + " TU"});

        // Информация о горючести
        if (oilburner.tank.getTankType().hasTrait(FT_Flammable.class)) {
            int heatPerTick = (int) (oilburner.tank.getTankType().getTrait(FT_Flammable.class).getHeatEnergy() / 1000) * oilburner.setting;
            drawCustomInfoStat(graphics, mouseX, mouseY, this.leftPos + 79, this.topPos + 34, 18, 18,
                    new String[]{oilburner.setting + " mB/t", String.format(Locale.US, "%,d", heatPerTick) + " TU/t"});
        }

        // Информация о баке
        oilburner.tank.renderTankInfo(graphics, mouseX, mouseY, this.leftPos + 44, this.topPos + 17, 16, 52);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.leftPos + 80 <= mouseX && this.leftPos + 80 + 16 > mouseX &&
                this.topPos + 54 < mouseY && this.topPos + 54 + 14 >= mouseY) {

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            // Отправляем пакет на сервер (всегда, потому что мы на клиенте)
            CompoundTag data = new CompoundTag();
            data.putBoolean("toggle", true);
            PacketDispatcher.sendAuxButtonPacket(oilburner.getBlockPos(), 0, 1);

            return true; // событие обработано
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(oilburner.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(this.font, name, this.imageWidth / 2 - this.font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Прогресс тепла
        int heatProgress = oilburner.heatEnergy * 52 / oilburner.maxHeatEnergy;
        graphics.blit(texture, this.leftPos + 116, this.topPos + 69 - heatProgress, 194, 52 - heatProgress, 16, heatProgress);

        if (oilburner.isOn) {
            graphics.blit(texture, this.leftPos + 70, this.topPos + 54, 210, 0, 35, 14);

            if (oilburner.tank.getFill() > 0 && oilburner.tank.getTankType().hasTrait(FT_Flammable.class)) {
                graphics.blit(texture, this.leftPos + 79, this.topPos + 34, 176, 0, 18, 18);
            }
        }

        // Рендер бака
        oilburner.tank.renderTank(graphics, this.leftPos + 44, this.topPos + 69,  16, 52);
    }

    protected void drawCustomInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, int tPosX, int tPosY, String[] text) {
        if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
            List<Component> components = new java.util.ArrayList<>();
            for (String s : text) {
                components.add(Component.literal(s));
            }
            graphics.renderTooltip(this.font, components, java.util.Optional.empty(), tPosX, tPosY);
        }
    }

    public void drawCustomInfoStat(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, String[] text) {
        drawCustomInfoStat(graphics, mouseX, mouseY, x, y, width, height, mouseX, mouseY, text);
    }

    private List<Component> createComponentList(List<String> strings) {
        List<Component> list = new ArrayList<>();
        for (String s : strings) {
            list.add(Component.literal(s));
        }
        return list;
    }
}