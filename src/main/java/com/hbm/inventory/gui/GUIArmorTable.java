package com.hbm.inventory.gui;

import com.hbm.handler.ArmorModHandler;
import com.hbm.inventory.container.ContainerArmorTable;

import com.hbm.inventory.container.ContainerAshpit;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIArmorTable extends AbstractContainerScreen<ContainerArmorTable> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_armor_modifier.png");


    public GUIArmorTable(ContainerArmorTable container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.imageWidth = 176 + 22;
        this.imageHeight = 222;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        if (this.minecraft.player.getInventory().getSelected() == null) {
            String[] unloc = new String[]{
                    "armorMod.type.helmet",
                    "armorMod.type.chestplate",
                    "armorMod.type.leggings",
                    "armorMod.type.boots",
                    "armorMod.type.servo",
                    "armorMod.type.cladding",
                    "armorMod.type.insert",
                    "armorMod.type.special",
                    "armorMod.type.battery",
                    "armorMod.insertHere"
            };

            for (int i = 0; i < ArmorModHandler.MOD_SLOTS + 1; ++i) {
                Slot slot = this.menu.slots.get(i);
                if (isHovering(slot.x, slot.y, 16, 16, x, y) && !slot.hasItem()) {
                    Component text = (i < ArmorModHandler.MOD_SLOTS ?
                            Component.literal("").withStyle(ChatFormatting.LIGHT_PURPLE) :
                            Component.literal("").withStyle(ChatFormatting.YELLOW))
                            .append(Component.translatable(unloc[i]));
                    guiGraphics.renderTooltip(this.font, text, x, y);
                }
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String name = Component.translatable("container.armorTable").getString();
        guiGraphics.drawString(this.font, name, (this.imageWidth - 22) / 2 - this.font.width(name) / 2 + 22, 6, 0x404040);
        guiGraphics.drawString(this.font, I18n.get("container.inventory"), 8 + 22, this.imageHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos + 22, this.topPos, 0, 0, this.imageWidth - 22, this.imageHeight);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos + 31, 176, 96, 22, 100);

        ItemStack armor = this.menu.slots.get(ArmorModHandler.MOD_SLOTS).getItem();

        if (!armor.isEmpty()) {
            if (armor.getItem() instanceof ArmorItem) {
                guiGraphics.blit(TEXTURE, this.leftPos + 41 + 22, this.topPos + 60, 176, 74, 22, 22);
            } else {
                guiGraphics.blit(TEXTURE, this.leftPos + 41 + 22, this.topPos + 60, 176, 52, 22, 22);
            }
        } else {
            if (System.currentTimeMillis() % 1000 < 500) {
                guiGraphics.blit(TEXTURE, this.leftPos + 41 + 22, this.topPos + 60, 176, 52, 22, 22);
            }
        }

        for (int i = 0; i < ArmorModHandler.MOD_SLOTS; i++) {
            Slot slot = this.menu.slots.get(i);
            drawIndicator(guiGraphics, i, slot.x - 1, slot.y - 1);
        }
    }

    private void drawIndicator(GuiGraphics guiGraphics, int index, int x, int y) {
        ItemStack mod = this.menu.slots.get(index).getItem();
        ItemStack armor = this.menu.slots.get(ArmorModHandler.MOD_SLOTS).getItem();

        if (mod.isEmpty()) return;

        if (ArmorModHandler.isApplicable(armor, mod)) {
            guiGraphics.blit(TEXTURE, this.leftPos + x, this.topPos + y, 176, 34, 18, 18);
        } else {
            guiGraphics.blit(TEXTURE, this.leftPos + x, this.topPos + y, 176, 16, 18, 18);
        }
    }
}