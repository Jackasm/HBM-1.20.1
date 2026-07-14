package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerCargoContainer;
import com.hbm.inventory.CargoItemHandler;
import com.hbm.network.PacketHandler;
import com.hbm.network.server.CargoContainerExtractPacket;
import com.hbm.network.server.CargoContainerInsertPacket;
import com.hbm.tileentity.machine.TileEntityCargoContainer;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class CargoContainerGUI extends AbstractContainerScreen<ContainerCargoContainer> {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/storage/cargo_container.png");

    private final TileEntityCargoContainer tile;
    private final CargoItemHandler handler;

    // Размеры сетки контейнера
    private static final int CONTAINER_COLS = 13;
    private static final int CONTAINER_ROWS = 4;
    private static final int SLOT_SIZE = 18;
    private static final int OFFSET_X = 4;
    private static final int OFFSET_Y = -30;

    private boolean handlingCargoClick = false;

    // Координаты области контейнера
    private int containerX;
    private int containerY;

    public CargoContainerGUI(ContainerCargoContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tile = container.getTile();
        this.handler = tile.getItemHandler();
        this.imageWidth = 256;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // Позиция области контейнера (сдвиг относительно левого верхнего угла GUI)
        this.containerX = this.leftPos + 8;
        this.containerY = this.topPos + 18;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        // Рисуем фон GUI
        renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        // Рисуем предметы контейнера
        renderContainerItems(guiGraphics);
        // Рисуем слоты инвентаря игрока
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        // Тултипы
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderSlotTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderContainerItems(GuiGraphics guiGraphics) {
        for (int row = 0; row < CONTAINER_ROWS; row++) {
            for (int col = 0; col < CONTAINER_COLS; col++) {
                int slotIndex = row * CONTAINER_COLS + col;
                if (slotIndex >= handler.getSlots()) break;
                ItemStack stack = handler.getStackInSlot(slotIndex);
                if (!stack.isEmpty()) {
                    int x = containerX + col * SLOT_SIZE + OFFSET_X;
                    int y = containerY + row * SLOT_SIZE + OFFSET_Y;
                    // Рисуем иконку (количество всегда 1)
                    guiGraphics.renderItem(stack, x, y);
                    // Рисуем реальное количество
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0, 0, 300);
                    String text = formatLargeNumber(handler.getRealCount(slotIndex));
                    guiGraphics.drawString(font, text, x - 1 + SLOT_SIZE - font.width(text), y - 1 + SLOT_SIZE - 8, 0xFFFFFF, true);
                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    private String formatLargeNumber(long count) {
        if (count >= 1_000_000_000) return String.format("%.1fB", count / 1_000_000_000.0);
        if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000.0);
        if (count >= 1000) return String.format("%.1fK", count / 1000.0);
        return String.format("%,d", count);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2 - 20;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Заголовки не нужны
    }

    private void renderSlotTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (int row = 0; row < CONTAINER_ROWS; row++) {
            for (int col = 0; col < CONTAINER_COLS; col++) {
                int slotIndex = row * CONTAINER_COLS + col;
                if (slotIndex >= handler.getSlots()) break;
                ItemStack stack = handler.getStackInSlot(slotIndex);
                if (!stack.isEmpty()) {
                    int x = containerX + col * SLOT_SIZE + OFFSET_X;
                    int y = containerY + row * SLOT_SIZE + OFFSET_Y;
                    if (mouseX >= x && mouseX <= x + SLOT_SIZE && mouseY >= y && mouseY <= y + SLOT_SIZE) {
                        // Получаем название предмета
                        String itemName = stack.getHoverName().getString();
                        // Получаем полное количество
                        long count = handler.getRealCount(slotIndex);
                        String countStr = String.format("%,d", count);

                        // Создаём список компонентов для тултипа
                        java.util.List<Component> tooltip = new java.util.ArrayList<>();
                        tooltip.add(Component.literal(itemName));
                        tooltip.add(Component.literal("× " + countStr).withStyle(net.minecraft.ChatFormatting.GRAY));

                        guiGraphics.renderTooltip(font, tooltip, java.util.Optional.empty(), stack, mouseX, mouseY);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Проверяем клик по предметам контейнера
        for (int row = 0; row < CONTAINER_ROWS; row++) {
            for (int col = 0; col < CONTAINER_COLS; col++) {
                int slotIndex = row * CONTAINER_COLS + col;
                if (slotIndex >= handler.getSlots()) break;
                int x = containerX + col * SLOT_SIZE + OFFSET_X;
                int y = containerY + row * SLOT_SIZE + OFFSET_Y;
                if (mouseX >= x && mouseX <= x + SLOT_SIZE && mouseY >= y && mouseY <= y + SLOT_SIZE) {
                    handlingCargoClick = true;
                    ItemStack carried = getMenu().getCarried();
                    boolean shift = hasShiftDown();

                    if (button == 0) { // Левая кнопка
                        ItemStack slotStack = handler.getStackInSlot(slotIndex);
                        boolean canMerge = canMergeStacks(carried, slotStack);

                        if (carried.isEmpty() || canMerge) {
                            PacketHandler.sendToServer(new CargoContainerExtractPacket(tile.getBlockPos(), slotIndex, shift));
                        } else {
                            PacketHandler.sendToServer(new CargoContainerInsertPacket(tile.getBlockPos(), 0));
                        }
                    } else if (button == 1) { // Правая кнопка
                        if (carried.isEmpty()) {
                            // Рука пуста - берём 1 предмет
                            PacketHandler.sendToServer(new CargoContainerExtractPacket(tile.getBlockPos(), slotIndex, false));
                        } else {
                            // В руке есть предмет - кладём 1 предмет
                            PacketHandler.sendToServer(new CargoContainerInsertPacket(tile.getBlockPos(), 1));
                        }
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean canMergeStacks(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty()) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getDamageValue() != b.getDamageValue()) return false;

        // Получаем NBT теги
        CompoundTag aTag = a.getTag();
        CompoundTag bTag = b.getTag();

        // Очищаем теги от Damage:0
        CompoundTag aClean = getCleanTag(aTag);
        CompoundTag bClean = getCleanTag(bTag);

        // Сравниваем очищенные теги
        if (aClean == null && bClean == null) return true;
        if (aClean == null || bClean == null) return false;
        return aClean.equals(bClean);
    }

    private CompoundTag getCleanTag(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return null;

        CompoundTag clean = tag.copy();
        // Удаляем Damage если он равен 0
        if (clean.contains("Damage") && clean.getInt("Damage") == 0) {
            clean.remove("Damage");
        }
        // Если после удаления тег стал пустым - возвращаем null
        return clean.isEmpty() ? null : clean;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (handlingCargoClick) {
            handlingCargoClick = false; // Сбрасываем флаг
            return true; // Блокируем стандартную обработку
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}