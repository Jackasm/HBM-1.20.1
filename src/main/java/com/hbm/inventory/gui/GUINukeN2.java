package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerNukeN2;
import com.hbm.items.ModItems;
import com.hbm.tileentity.bomb.TileEntityNukeN2;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class GUINukeN2 extends AbstractContainerScreen<ContainerNukeN2> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/weapon/n2Schematic.png");
    private final TileEntityNukeN2 nuke;

    public GUINukeN2(ContainerNukeN2 container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.nuke = container.getNuke();
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Component name = Component.translatable("container.nukeN2");
        graphics.drawString(this.font, name, this.imageWidth / 2 - this.font.width(name) / 2, 6, 4210752, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Подсчитываем количество зарядов N2
        IItemHandler handler = nuke.getInventory();
        int count = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty() && handler.getStackInSlot(i).getItem() == ModItems.N2_CHARGE.get()) {
                count++;
            }
        }

        // Рисуем индикатор заполнения
        if (count > 0) {
            int height = 6 * count;
            graphics.blit(TEXTURE, this.leftPos + 35, this.topPos + 120 - height, 176, 0, 34, height);
        }
    }
}