package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerDesignator;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.ItemDesignatorPacket;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIScreenDesignator extends AbstractContainerScreen<ContainerDesignator> {

    protected static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/weapon/gui_designator.png");

    int shownX;
    int shownZ;
    List<FolderButton> buttons = new ArrayList<>();
    private final Player player;

    public GUIScreenDesignator(ContainerDesignator menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 178;
        this.player = Minecraft.getInstance().player;
        this.inventoryLabelY = 10000; // скрыть надпись "Inventory"
        this.titleLabelY = 10000;     // скрыть заголовок
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        shownX = 0;
        shownZ = 0;

        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() == ModItems.DESIGNATOR_MANUAL.get() && stack.hasTag()) {
                shownX = Objects.requireNonNull(stack.getTag()).getInt("xCoord");
                shownZ = stack.getTag().getInt("zCoord");
            }
        }

        updateButtons();
    }

    private void updateButtons() {
        buttons.clear();

        // X координаты — верхняя часть
        buttons.add(new FolderButton(leftPos + 25, topPos + 26, 0, 0, 0, 1, null));
        buttons.add(new FolderButton(leftPos + 52, topPos + 26, 1, 0, 0, 5, null));
        buttons.add(new FolderButton(leftPos + 79, topPos + 26, 2, 0, 0, 10, null));
        buttons.add(new FolderButton(leftPos + 106, topPos + 26, 3, 0, 0, 50, null));
        buttons.add(new FolderButton(leftPos + 133, topPos + 26, 4, 0, 0, 100, null));

        buttons.add(new FolderButton(leftPos + 25, topPos + 62, 5, 1, 0, 1, null));
        buttons.add(new FolderButton(leftPos + 52, topPos + 62, 6, 1, 0, 5, null));
        buttons.add(new FolderButton(leftPos + 79, topPos + 62, 7, 1, 0, 10, null));
        buttons.add(new FolderButton(leftPos + 106, topPos + 62, 8, 1, 0, 50, null));
        buttons.add(new FolderButton(leftPos + 133, topPos + 62, 9, 1, 0, 100, null));

        buttons.add(new FolderButton(leftPos + 133, topPos + 44, 10, 2, 0, 0, "Set coord to current X position..."));

        // Z координаты — нижняя часть
        buttons.add(new FolderButton(leftPos + 25, topPos + 26 + 72, 0, 0, 1, 1, null));
        buttons.add(new FolderButton(leftPos + 52, topPos + 26 + 72, 1, 0, 1, 5, null));
        buttons.add(new FolderButton(leftPos + 79, topPos + 26 + 72, 2, 0, 1, 10, null));
        buttons.add(new FolderButton(leftPos + 106, topPos + 26 + 72, 3, 0, 1, 50, null));
        buttons.add(new FolderButton(leftPos + 133, topPos + 26 + 72, 4, 0, 1, 100, null));

        buttons.add(new FolderButton(leftPos + 25, topPos + 62 + 72, 5, 1, 1, 1, null));
        buttons.add(new FolderButton(leftPos + 52, topPos + 62 + 72, 6, 1, 1, 5, null));
        buttons.add(new FolderButton(leftPos + 79, topPos + 62 + 72, 7, 1, 1, 10, null));
        buttons.add(new FolderButton(leftPos + 106, topPos + 62 + 72, 8, 1, 1, 50, null));
        buttons.add(new FolderButton(leftPos + 133, topPos + 62 + 72, 9, 1, 1, 100, null));

        buttons.add(new FolderButton(leftPos + 133, topPos + 44 + 72, 10, 2, 1, 0, "Set coord to current Z position..."));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        for (FolderButton b : buttons) {
            b.drawButton(graphics, b.isMouseOnButton(mouseX, mouseY));
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        // Tooltips для кнопок
        for (FolderButton b : buttons) {
            if (b.isMouseOnButton(mouseX - leftPos, mouseY - topPos)) {
                b.drawString(graphics, mouseX, mouseY);
            }
        }

        // Отображение координат (в относительных координатах GUI)
        String x = String.valueOf(shownX);
        String z = String.valueOf(shownZ);
        graphics.drawString(font, Component.translatable("hbm.text.designator_x", x),
                this.imageWidth / 2 - font.width(Component.translatable("hbm.text.designator_x", x)) / 2, 50, 0x404040, false);
        graphics.drawString(font, Component.translatable("hbm.text.designator_z", z),
                this.imageWidth / 2 - font.width(Component.translatable("hbm.text.designator_z", z)) / 2, 50 + 18 * 4, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        try {
            // Координаты мыши относительно GUI
            int relX = (int) mouseX - leftPos;
            int relY = (int) mouseY - topPos;

            for (FolderButton b : buttons) {
                if (b.isMouseOnButton(relX, relY)) {
                    b.executeAction();
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == Objects.requireNonNull(this.minecraft).options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (player == null || player.getMainHandItem().getItem() != ModItems.DESIGNATOR_MANUAL.get()) {
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // Внутренний класс FolderButton
    private class FolderButton {
        int xPos;
        int yPos;
        int type;
        int operator;
        int value;
        int reference;
        String info;

        public FolderButton(int x, int y, int t, int o, int r, int v, String i) {
            xPos = x;
            yPos = y;
            type = t;
            operator = o;
            value = v;
            reference = r;
            info = i;
        }

        public boolean isMouseOnButton(int mouseX, int mouseY) {
            return xPos <= mouseX && xPos + 18 > mouseX && yPos <= mouseY && yPos + 18 > mouseY;
        }

        public void drawButton(GuiGraphics graphics, boolean hovered) {
            graphics.blit(TEXTURE, xPos, yPos, hovered ? 176 + 18 : 176, type * 18, 18, 18);
        }

        public void drawString(GuiGraphics graphics, int x, int y) {
            if (info == null || info.isEmpty()) return;
            graphics.renderTooltip(font, Component.literal(info), x, y);
        }

        public void executeAction() {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)
            );
            PacketDispatcher.sendToServer(new ItemDesignatorPacket(this.operator, this.value, this.reference));

            int result = 0;

            if (operator == 0) result += value;
            if (operator == 1) result -= value;
            if (operator == 2) {
                if (reference == 0) {
                    shownX = (int) Math.round(player.getX());
                } else {
                    shownZ = (int) Math.round(player.getZ());
                }
                return;
            }

            if (reference == 0) shownX += result;
            else shownZ += result;
        }
    }
}