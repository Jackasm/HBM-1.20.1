package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineAmmoPress;
import com.hbm.inventory.recipes.AmmoPressRecipes;
import com.hbm.inventory.recipes.AmmoPressRecipes.AmmoPressRecipe;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.machine.TileEntityMachineAmmoPress;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIMachineAmmoPress extends AbstractContainerScreen<ContainerMachineAmmoPress> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_ammo_press.png");
    private final TileEntityMachineAmmoPress press;

    private final List<AmmoPressRecipe> recipes = new ArrayList<>();
    private int index;
    private int size;
    private int selection;
    private EditBox search;

    public GUIMachineAmmoPress(ContainerMachineAmmoPress container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        this.press = container.getAmmoPress();
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.selection = press.selectedRecipe;
        regenerateRecipes();
    }

    @Override
    protected void init() {
        super.init();
        this.search = new EditBox(this.font, leftPos + 10, topPos + 75, 66, 12, Component.literal(""));
        this.search.setTextColor(-1);
        this.search.setBordered(false);
        this.search.setMaxLength(25);
        this.search.setResponder(this::search);
        this.addWidget(this.search);
    }

    private void regenerateRecipes() {
        this.recipes.clear();
        this.recipes.addAll(AmmoPressRecipes.recipes);
        resetPaging();
    }

    private void search(String text) {
        String lower = text.toLowerCase(Locale.US);
        this.recipes.clear();

        if (lower.isEmpty()) {
            this.recipes.addAll(AmmoPressRecipes.recipes);
        } else {
            for (AmmoPressRecipe recipe : AmmoPressRecipes.recipes) {
                if (recipe.output.getHoverName().getString().toLowerCase(Locale.US).contains(lower)) {
                    this.recipes.add(recipe);
                }
            }
        }
        resetPaging();
    }

    private void resetPaging() {
        this.index = 0;
        this.size = Math.max(0, (int) Math.ceil((this.recipes.size() - 12) / 3D));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);

        // Тултипы для рецептов
        for (int i = index * 3; i < index * 3 + 12; i++) {
            if (i >= recipes.size()) break;
            int ind = i - index * 3;
            int ix = leftPos + 16 + 18 * (ind / 3);
            int iy = topPos + 17 + 18 * (ind % 3);
            if (mouseX >= ix && mouseX < ix + 18 && mouseY >= iy && mouseY < iy + 18) {
                AmmoPressRecipe recipe = recipes.get(i);
                graphics.renderTooltip(font, recipe.output, mouseX, mouseY);
                break;
            }
        }

        renderIngredientTooltips(graphics, mouseX, mouseY);

        this.search.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Проверяем, что мышь внутри области GUI
        if (mouseX >= leftPos && mouseX < leftPos + imageWidth &&
                mouseY >= topPos && mouseY < topPos + imageHeight) {
            if (delta > 0 && index > 0) {
                index--;
                return true;
            } else if (delta < 0 && index < size) {
                index++;
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Клик по полю поиска
        if (this.search.mouseClicked(mouseX, mouseY, button)) {
            this.search.setFocused(true);
            return true;
        }

        // Кнопка "вверх" (скролл влево)
        if (mouseX >= leftPos + 7 && mouseX < leftPos + 7 + 9 &&
                mouseY >= topPos + 17 && mouseY < topPos + 17 + 54) {
            clickSound();
            if (index > 0) index--;
            return true;
        }

        // Кнопка "вниз" (скролл вправо)
        if (mouseX >= leftPos + 88 && mouseX < leftPos + 88 + 9 &&
                mouseY >= topPos + 17 && mouseY < topPos + 17 + 54) {
            clickSound();
            if (index < size) index++;
            return true;
        }

        // Клик по рецепту
        for (int i = index * 3; i < index * 3 + 12; i++) {
            if (i >= recipes.size()) break;
            int ind = i - index * 3;
            int ix = leftPos + 16 + 18 * (ind / 3);
            int iy = topPos + 17 + 18 * (ind % 3);
            if (mouseX >= ix && mouseX < ix + 18 && mouseY >= iy && mouseY < iy + 18) {
                int newSelection = AmmoPressRecipes.recipes.indexOf(recipes.get(i));
                this.selection = (this.selection == newSelection) ? -1 : newSelection;

                CompoundTag data = new CompoundTag();
                data.putInt("selection", this.selection);
                PacketDispatcher.sendToServer(new NBTControlPacket(data, press.getBlockPos()));
                clickSound();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = Component.translatable(press.getBlockState().getBlock().getDescriptionId()).getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 0xffffff, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Кнопки скролла (подсветка при наведении)
        boolean overLeft = mouseX >= leftPos + 7 && mouseX < leftPos + 7 + 9 &&
                mouseY >= topPos + 17 && mouseY < topPos + 17 + 54;
        boolean overRight = mouseX >= leftPos + 88 && mouseX < leftPos + 88 + 9 &&
                mouseY >= topPos + 17 && mouseY < topPos + 17 + 54;

        if (overLeft) {
            graphics.blit(TEXTURE, leftPos + 7, topPos + 17, 176, 0, 9, 54);
        }
        if (overRight) {
            graphics.blit(TEXTURE, leftPos + 88, topPos + 17, 185, 0, 9, 54);
        }

        // Подсветка поля поиска при фокусе
        if (this.search.isFocused()) {
            graphics.blit(TEXTURE, leftPos + 8, topPos + 72, 176, 54, 70, 16);
        }

        // Отрисовка списка рецептов
        for (int i = index * 3; i < index * 3 + 12; i++) {
            if (i >= recipes.size()) break;
            int ind = i - index * 3;
            int x = leftPos + 17 + 18 * (ind / 3);
            int y = topPos + 18 + 18 * (ind % 3);

            AmmoPressRecipe recipe = recipes.get(i);
            graphics.renderItem(recipe.output, x, y);
            graphics.renderItemDecorations(font, recipe.output, x, y, String.valueOf(recipe.output.getCount()));

            // Рамка выделения
            int frameU = (selection == AmmoPressRecipes.recipes.indexOf(recipes.get(i))) ? 194 : 212;
            graphics.blit(TEXTURE, leftPos + 16 + 18 * (ind / 3), topPos + 17 + 18 * (ind % 3), frameU, 0, 18, 18);
        }

        // Отображение рецепта при выборе
        if (selection >= 0 && selection < AmmoPressRecipes.recipes.size()) {
            AmmoPressRecipe selected = AmmoPressRecipes.recipes.get(selection);
            for (int i = 0; i < 9; i++) {
                AStack stack = selected.input[i];
                if (stack == null) continue;
                if (!press.getInventory().getStackInSlot(i).isEmpty()) continue;

                List<ItemStack> inputs = stack.extractForNEI();
                if (inputs.isEmpty()) continue;
                int idx = (int) (System.currentTimeMillis() / 1000) % inputs.size();
                ItemStack displayStack = inputs.get(idx);

                int slotX = leftPos + 116 + 18 * (i % 3);
                int slotY = topPos + 18 + 18 * (i / 3);

                renderTransparentItem(graphics, displayStack, slotX, slotY);
            }
        }

        // Отрисовка поля поиска
        this.search.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void renderTransparentItem(GuiGraphics graphics, ItemStack stack, int x, int y) {
        // Временно отключаем затенение
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Устанавливаем прозрачность через цвет
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) 0.4);

        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack, x, y,
                stack.getCount() > 1 ? String.valueOf(stack.getCount()) : null);

        // Восстанавливаем
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private void renderIngredientTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        if (selection < 0 || selection >= AmmoPressRecipes.recipes.size()) return;

        AmmoPressRecipe selected = AmmoPressRecipes.recipes.get(selection);
        for (int i = 0; i < 9; i++) {
            AStack stack = selected.input[i];
            if (stack == null) continue;
            if (!press.getInventory().getStackInSlot(i).isEmpty()) continue;

            List<ItemStack> inputs = stack.extractForNEI();
            if (inputs.isEmpty()) continue;
            int idx = (int) (System.currentTimeMillis() / 1000) % inputs.size();
            ItemStack displayStack = inputs.get(idx);

            int slotX = leftPos + 116 + 18 * (i % 3);
            int slotY = topPos + 18 + 18 * (i / 3);

            if (mouseX >= slotX && mouseX < slotX + 16 &&
                    mouseY >= slotY && mouseY < slotY + 16) {
                graphics.renderTooltip(font, displayStack, mouseX, mouseY);
                return;
            }
        }
    }

    private void clickSound() {
        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Передаём ввод в поле поиска
        if (this.search.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char code, int modifiers) {
        if (this.search.charTyped(code, modifiers)) {
            return true;
        }
        return super.charTyped(code, modifiers);
    }

    @Override
    public void removed() {
        super.removed();
        // если нужно отключить повторение клавиш
    }
}