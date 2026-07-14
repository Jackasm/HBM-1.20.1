package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerToolBox;
import com.hbm.items.tool.ItemToolBox.InventoryToolBox;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIToolBox extends AbstractContainerScreen<ContainerToolBox> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/gui_toolbox.png");
    private final InventoryToolBox inventory;
    private ItemStack firstHeld;

    public GUIToolBox(ContainerToolBox container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.inventory = container.getInventoryBox();
        this.imageWidth = 176;
        this.imageHeight = 211;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (firstHeld == null) {
            // *very* unlikely to be incorrect on the first frame after opening, so doing this is good enough
            firstHeld = Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).getMainHandItem();
            // if the open box has changed or disappeared, close the inventory
        } else if (Objects.requireNonNull(Objects.requireNonNull(this.minecraft).player).getMainHandItem() != firstHeld) {
            //this.minecraft.player.closeScreen();
            //return;
        }

        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Component name = inventory.hasCustomName() ?
                inventory.target.getHoverName() :
                inventory.getDisplayName();

        graphics.drawString(font, name.getString(), this.imageWidth / 2 - font.width(name.getString()) / 2, 37, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}