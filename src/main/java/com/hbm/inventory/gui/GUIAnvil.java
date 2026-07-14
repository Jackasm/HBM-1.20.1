package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerAnvil;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.AnvilRecipes;
import com.hbm.inventory.recipes.AnvilRecipes.AnvilConstructionRecipe;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.fluid.ItemFluidID;
import com.hbm.util.RefStrings;
import com.hbm.network.PacketDispatcher;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class GUIAnvil extends AbstractContainerScreen<ContainerAnvil> {

    private static final ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_anvil.png");

    private final int tier;
    private final List<AnvilConstructionRecipe> allRecipes = new ArrayList<>();
    private final List<AnvilConstructionRecipe> filteredRecipes = new ArrayList<>();
    private final List<AnvilConstructionRecipe> displayRecipes = new ArrayList<>();
    private int selection;
    int index;
    int size;
    private final Inventory playerInventory;
    private EditBox search;
    private int lastSize = 1;

    private static final int TAB_WIDTH = 24;
    private static final int TAB_HEIGHT = 24;
    private static final int TAB_OFFSET_X = -24;
    private static final int TAB_START_Y = 4;

    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 256;

    private enum TabType {
        ALL,
        CONSTRUCTION,
        RECYCLING,
        SMITHING,
        FLUID_ID
    }

    private TabType currentTab = TabType.ALL;
    private final List<TabType> tabs = List.of(TabType.ALL, TabType.CONSTRUCTION, TabType.RECYCLING, TabType.SMITHING, TabType.FLUID_ID);

    private long lastTagUpdate = 0;
    private final Map<Integer, Integer> tagAnimationIndices = new HashMap<>();

    public GUIAnvil(ContainerAnvil container, Inventory playerInv, Component title) {
        super(container, playerInv, title);

        this.tier = container.tier;
        this.playerInventory = playerInv;

        this.imageWidth = 176;
        this.imageHeight = 222;

        this.allRecipes.addAll(AnvilRecipes.getConstruction());

        updateFilteredRecipes();

        regenerateRecipes();
    }

    private void updateFilteredRecipes() {
        this.filteredRecipes.clear();
        if (currentTab == TabType.FLUID_ID) {
            // Получаем жидкостные рецепты из AnvilRecipes
            this.filteredRecipes.addAll(AnvilRecipes.fluidRecipes);
            return;
        }
        for (AnvilConstructionRecipe recipe : this.allRecipes) {
            if (recipe.tierLower > this.tier) continue;
            boolean matchesTab;
            if (currentTab == TabType.ALL) {
                matchesTab = true;
            } else if (currentTab == TabType.CONSTRUCTION) {
                matchesTab = recipe.getOverlay() == AnvilRecipes.OverlayType.CONSTRUCTION;
            } else if (currentTab == TabType.RECYCLING) {
                matchesTab = recipe.getOverlay() == AnvilRecipes.OverlayType.RECYCLING;
            } else if (currentTab == TabType.SMITHING) {
                matchesTab = recipe.getOverlay() == AnvilRecipes.OverlayType.SMITHING ||
                        recipe.getOverlay() == AnvilRecipes.OverlayType.TOOLS;
            } else {
                matchesTab = true;
            }
            if (matchesTab) {
                this.filteredRecipes.add(recipe);
            }
        }
    }

    private void regenerateRecipes() {
        this.displayRecipes.clear();
        this.displayRecipes.addAll(this.filteredRecipes);
        resetPaging();
    }

    private void search(String searchText) {
        this.displayRecipes.clear();
        if (searchText == null || searchText.isEmpty()) {
            this.displayRecipes.addAll(this.filteredRecipes);
        } else {
            String lower = searchText.toLowerCase(Locale.US);
            for (AnvilConstructionRecipe recipe : this.filteredRecipes) {
                List<String> list = recipeToSearchList(recipe);
                for (String s : list) {
                    if (s.contains(lower)) {
                        this.displayRecipes.add(recipe);
                        break;
                    }
                }
            }
        }
        resetPaging();
    }

    private void resetPaging() {
        this.index = 0;
        this.selection = -1;
        this.size = Math.max(0, (int) Math.ceil((this.displayRecipes.size() - 10) / 2D));
    }

    @Override
    protected void init() {
        super.init();

        int searchX = this.leftPos + 10;
        int searchY = this.topPos + 111;
        this.search = new EditBox(this.font, searchX, searchY, 84, 12, Component.literal(""));
        this.search.setTextColor(-1);
        this.search.setBordered(false);
        this.search.setMaxLength(25);
        this.search.setResponder(this::search);
        this.addRenderableWidget(this.search);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        // Обработка кликов по вкладкам
        int tabX = this.leftPos + TAB_OFFSET_X;
        for (int i = 0; i < tabs.size(); i++) {
            int tabY = this.topPos + TAB_START_Y + i * TAB_HEIGHT;
            if (mouseX >= tabX && mouseX < tabX + TAB_WIDTH && mouseY >= tabY && mouseY < tabY + TAB_HEIGHT) {
                TabType newTab = tabs.get(i);
                if (newTab != currentTab) {
                    currentTab = newTab;
                    updateFilteredRecipes();
                    if (this.search != null) {
                        this.search.setValue("");
                    }
                    regenerateRecipes();
                    this.selection = -1;
                    Minecraft.getInstance().getSoundManager().play(
                            net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                return true;
            }
        }

        if (this.search != null) {
            boolean searchClicked = this.search.mouseClicked(mouseX, mouseY, button);
            if (!searchClicked && this.search.isFocused()) this.search.setFocused(false);
        }

        int scrollLeftX = this.leftPos + 7;
        if (mouseX >= scrollLeftX && mouseX < scrollLeftX + 9 && mouseY >= this.topPos + 71 && mouseY < this.topPos + 71 + 36) {
            Minecraft.getInstance().getSoundManager().play(
                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (this.index > 0) this.index--;
            return true;
        }

        int scrollRightX = this.leftPos + 106;
        if (mouseX >= scrollRightX && mouseX < scrollRightX + 9 && mouseY >= this.topPos + 71 && mouseY < this.topPos + 71 + 36) {
            Minecraft.getInstance().getSoundManager().play(
                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (this.index < this.size) this.index++;
            return true;
        }

        int craftX = this.leftPos + 52;
        if (mouseX >= craftX && mouseX < craftX + 18 && mouseY >= this.topPos + 53 && mouseY < this.topPos + 53 + 18) {
            if (this.selection == -1) return true;

            Minecraft.getInstance().getSoundManager().play(
                    net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            boolean isShiftDown = hasShiftDown();
            AnvilConstructionRecipe selectedRecipe = this.displayRecipes.get(this.selection);
            int actualIndex = this.allRecipes.indexOf(selectedRecipe);
            PacketDispatcher.sendAnvilCraft(actualIndex, isShiftDown);
            return true;
        }

        for (int i = index * 2; i < index * 2 + 10; i++) {
            if (i >= this.displayRecipes.size()) break;
            int ind = i - index * 2;
            int ix = this.leftPos + 16 + 18 * (ind / 2);
            int iy = this.topPos + 71 + 18 * (ind % 2);
            if (mouseX >= ix && mouseX < ix + 18 && mouseY >= iy && mouseY < iy + 18) {
                Minecraft.getInstance().getSoundManager().play(
                        net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.selection = (this.selection != i) ? i : -1;

                if (this.selection >= 0 && this.selection < displayRecipes.size()) {
                    tagAnimationIndices.clear();
                    AnvilConstructionRecipe selectedRecipe = displayRecipes.get(this.selection);
                    for (int slot = 0; slot < selectedRecipe.input.size(); slot++) {
                        if (selectedRecipe.input.get(slot) instanceof TagStack) {
                            tagAnimationIndices.put(slot, 0);
                        }
                    }
                    lastTagUpdate = System.currentTimeMillis(); // сброс таймера
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void updateTagAnimation() {
        if (selection < 0 || selection >= displayRecipes.size()) return;

        AnvilConstructionRecipe recipe = displayRecipes.get(selection);
        if (recipe == null) return;

        boolean changed = false;
        for (int i = 0; i < recipe.input.size(); i++) {
            AStack input = recipe.input.get(i);
            if (input instanceof TagStack tagStack) {
                List<String> names = tagStack.getAllDisplayNames();
                if (!names.isEmpty()) {
                    int current = tagAnimationIndices.getOrDefault(i, 0);
                    int next = (current + 1) % names.size();
                    tagAnimationIndices.put(i, next);
                    changed = true;
                }
            }
        }

        if (changed) {
            Objects.requireNonNull(this.minecraft).screen = this;
        }
    }

    private void renderTabs(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int tabX = this.leftPos + TAB_OFFSET_X;
        for (int i = 0; i < tabs.size(); i++) {
            int tabY = this.topPos + TAB_START_Y + i * TAB_HEIGHT;
            TabType tab = tabs.get(i);
            boolean isSelected = (tab == currentTab);

            // Фон вкладки
            if (isSelected) {
                guiGraphics.blit(texture, tabX + 1, tabY, 26, TAB_HEIGHT, 194, 186, 26, TAB_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
            } else {
                guiGraphics.blit(texture, tabX + 1, tabY, 23, TAB_HEIGHT, 194, 186, 26, TAB_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
            }

            // Иконка или текст вкладки
            int iconX = tabX + (TAB_WIDTH - 16) / 2 - 2;
            int iconY = tabY;

            if (tab == TabType.ALL) {
                // Для ALL пишем букву A текстом
                guiGraphics.drawString(this.font, "All", iconX + 8, iconY + 8, 0xFFFFFF, false);
            } else if (tab == TabType.FLUID_ID) {
                // Иконка воды
                ItemStack fluidIcon = ItemFluidID.createForFluid(Fluids.WATER.get());
                guiGraphics.renderItem(fluidIcon, iconX + 4, iconY + 4);
            } else {
                // Для остальных используем оверлей из текстуры
                int u = switch (tab) {
                    case CONSTRUCTION -> 36;
                    case RECYCLING -> 54;
                    case SMITHING -> 72;
                    default -> 18;
                };
                guiGraphics.blit(texture, iconX, iconY, u + 1, 222 + 1, 16, 16);
            }
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component name = Component.translatable("container.anvil", tier);

        guiGraphics.drawString(this.font, name, 61 - this.font.width(name) / 2, 8, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventory.getDisplayName(), 8, this.imageHeight - 96 + 2, 4210752, false);

        if (this.selection >= 0 && this.selection < this.displayRecipes.size()) {
            AnvilConstructionRecipe recipe = displayRecipes.get(this.selection);
            renderRecipeIcons(guiGraphics, recipe);
        }
    }

    private void renderRecipeIcons(GuiGraphics guiGraphics, AnvilConstructionRecipe recipe) {
        int startX = this.width / 2 - 110;
        int startY = 35;
        int slotSize = 18;
        int columns = 3;
        int padding = 2;
        int neededWidth = columns * (slotSize + padding) - padding;
        this.lastSize = Math.min(neededWidth, 150);

        guiGraphics.drawString(this.font, Component.translatable("container.anvil.inputs"), startX, startY - 12, 0xFFFF00, false);

        for (int i = 0; i < recipe.input.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            int x = startX + col * (slotSize + padding);
            int y = startY + row * (slotSize + padding);
            AStack input = recipe.input.get(i);

            ItemStack displayStack = getDisplayStackForInput(input, i);
            if (!displayStack.isEmpty()) {
                guiGraphics.renderItem(displayStack, x, y);

                if (displayStack.getCount() > 1) {
                    guiGraphics.renderItemDecorations(this.font, displayStack, x, y);
                }

                boolean hasItems = checkPlayerHasItem(input, this.playerInventory);
                if (!hasItems) {
                    guiGraphics.renderOutline(x - 1, y - 1, slotSize, slotSize, 0xFFFF0000);
                } else {
                    guiGraphics.renderOutline(x - 1, y - 1, slotSize, slotSize, 0xFF00FF00);
                }
            }
        }

        int inputRows = (int) Math.ceil((double) recipe.input.size() / columns);
        int outputsStartY = startY + inputRows * (slotSize + padding) + 10;

        guiGraphics.drawString(this.font, Component.translatable("container.anvil.outputs"), startX, outputsStartY - 12, 0xFFFF00, false);

        for (int i = 0; i < recipe.output.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            int x = startX + col * (slotSize + padding);
            int y = outputsStartY + row * (slotSize + padding);
            AnvilRecipes.AnvilOutput output = recipe.output.get(i);
            ItemStack outStack = output.stack.copy();
            if (!outStack.isEmpty()) {
                guiGraphics.renderItem(outStack, x, y);
                if (outStack.getCount() > 1) {
                    guiGraphics.renderItemDecorations(this.font, outStack, x, y);
                }
            }
        }
    }

    private ItemStack getDisplayStackForInput(AStack input, int slotIndex) {
        if (input instanceof ComparableStack compStack) {
            return compStack.toStack();
        } else if (input instanceof TagStack tagStack) {
            return getCurrentStackForTag(tagStack, slotIndex);
        }
        return ItemStack.EMPTY;
    }

    private void handleRecipeTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, AnvilConstructionRecipe recipe, int startX, int startY, int slotSize, int columns, int padding) {
        for (int i = 0; i < recipe.input.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            int x = startX + col * (slotSize + padding);
            int y = startY + row * (slotSize + padding);

            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                AStack input = recipe.input.get(i);
                ItemStack displayStack = getDisplayStackForInput(input, i);
                if (!displayStack.isEmpty()) {
                    guiGraphics.renderTooltip(this.font, displayStack, mouseX, mouseY);
                    return;
                }
            }
        }

        int inputRows = (int) Math.ceil((double) recipe.input.size() / columns);
        int outputsStartY = startY + inputRows * (slotSize + padding) + 10;

        for (int i = 0; i < recipe.output.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            int x = startX + col * (slotSize + padding);
            int y = outputsStartY + row * (slotSize + padding);

            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                AnvilRecipes.AnvilOutput output = recipe.output.get(i);
                ItemStack outStack = output.stack.copy();
                if (!outStack.isEmpty()) {
                    if (output.chance != 1.0F) {
                        List<Component> tooltipLines = new ArrayList<>();
                        tooltipLines.add(outStack.getHoverName());
                        tooltipLines.add(Component.literal(String.format("Chance: %.0f%%", output.chance * 100)).withStyle(ChatFormatting.GOLD));
                        guiGraphics.renderComponentTooltip(this.font, tooltipLines, mouseX, mouseY);
                    } else {
                        guiGraphics.renderTooltip(this.font, outStack, mouseX, mouseY);
                    }
                    return;
                }
            }
        }
    }

    private ItemStack getCurrentStackForTag(TagStack tagStack, int slotIndex) {
        List<ItemStack> variants = tagStack.extractForNEI();
        if (variants.isEmpty()) return ItemStack.EMPTY;
        int index = tagAnimationIndices.getOrDefault(slotIndex, 0);
        ItemStack stack = variants.get(index % variants.size()).copy();
        stack.setCount(tagStack.getStackSize());
        return stack;
    }

    public List<String> recipeToSearchList(AnvilConstructionRecipe recipe) {
        List<String> list = new ArrayList<>();

        for (AnvilRecipes.AnvilOutput output : recipe.output) {
            list.add(output.stack.getHoverName().getString().toLowerCase(Locale.US));
        }

        for (AStack input : recipe.input) {
            if (input instanceof ComparableStack compStack) {
                String name = compStack.toStack().getHoverName().getString();
                list.add(name.toLowerCase(Locale.US));
            } else if (input instanceof TagStack tagStack) {
                List<ItemStack> items = tagStack.extractForNEI();
                for (ItemStack item : items) {
                    list.add(item.getHoverName().getString().toLowerCase(Locale.US));
                }
            }
        }

        return list;
    }

    private boolean checkPlayerHasItem(AStack stack, Inventory inventory) {
        if (stack instanceof ComparableStack compStack) {
            return hasEnoughItems(inventory, compStack.toStack());
        } else if (stack instanceof TagStack tagStack) {
            return hasEnoughItemsForTag(inventory, tagStack);
        }
        return false;
    }

    private boolean hasEnoughItems(Inventory inventory, ItemStack required) {
        int countNeeded = required.getCount();
        int countFound = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stackInSlot = inventory.getItem(i);
            if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameTags(stackInSlot, required)) {
                countFound += stackInSlot.getCount();
                if (countFound >= countNeeded) return true;
            }
        }
        return false;
    }

    private boolean hasEnoughItemsForTag(Inventory inventory, TagStack tagStack) {
        int countNeeded = tagStack.getStackSize();
        int countFound = 0;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stackInSlot = inventory.getItem(i);
            if (!stackInSlot.isEmpty() && stackInSlot.is(tagStack.tag)) {
                countFound += stackInSlot.getCount();
                if (countFound >= countNeeded) return true;
            }
        }
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        for (int i = index * 2; i < index * 2 + 10; i++) {
            if (i >= this.displayRecipes.size()) break;

            int ind = i - index * 2;
            int x = this.leftPos + 16 + 18 * (ind / 2);
            int y = this.topPos + 71 + 18 * (ind % 2);

            if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                AnvilConstructionRecipe recipe = displayRecipes.get(i);
                ItemStack display = recipe.getDisplay();
                if (!display.isEmpty()) {
                    guiGraphics.renderTooltip(this.font, display, mouseX, mouseY);
                }
                break;
            }
        }

        if (this.selection >= 0 && this.selection < this.displayRecipes.size()) {
            AnvilConstructionRecipe recipe = displayRecipes.get(this.selection);

            int startX = this.width / 2 + 40;
            int startY = 35 + 15;
            int slotSize = 18;
            int columns = 3;
            int padding = 2;

            handleRecipeTooltips(guiGraphics, mouseX, mouseY, recipe, startX, startY, slotSize, columns, padding);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, 176, this.imageHeight);

        // Отрисовка вкладок слева
        renderTabs(guiGraphics, mouseX, mouseY);

        guiGraphics.blit(texture, this.leftPos + 140, this.topPos + 17, 125, 17, 54, 108);

        if (mouseX >= this.leftPos + 7 && mouseX < this.leftPos + 7 + 9 && mouseY >= this.topPos + 71 && mouseY < this.topPos + 71 + 36) {
            guiGraphics.blit(texture, this.leftPos + 7, this.topPos + 71, 176, 186, 9, 36);
        }
        if (mouseX >= this.leftPos + 106 && mouseX < this.leftPos + 106 + 9 && mouseY >= this.topPos + 71 && mouseY < this.topPos + 71 + 36) {
            guiGraphics.blit(texture, this.leftPos + 106, this.topPos + 71, 185, 186, 9, 36);
        }

        if (mouseX >= this.leftPos + 52 && mouseX < this.leftPos + 52 + 18 && mouseY >= this.topPos + 53 && mouseY < this.topPos + 53 + 18) {
            guiGraphics.blit(texture, this.leftPos + 52, this.topPos + 53, 176, 150, 18, 18);
        } else {
            guiGraphics.blit(texture, this.leftPos + 52, this.topPos + 53, 176, 132, 18, 18);
        }

        if (this.search != null && this.search.isFocused()) {
            guiGraphics.blit(texture, this.leftPos + 8, this.topPos + 108, 168, 222, 88, 16);
        }

        // Отрисовка иконок рецептов
        for (int i = index * 2; i < index * 2 + 10; i++) {
            if (i >= displayRecipes.size()) break;

            int ind = i - index * 2;
            int x = this.leftPos + 16 + 18 * (ind / 2);
            int y = this.topPos + 71 + 18 * (ind % 2);

            AnvilConstructionRecipe recipe = displayRecipes.get(i);
            ItemStack display = recipe.getDisplay();

            if (!display.isEmpty()) {
                guiGraphics.renderItem(display, x + 1, y + 1);
            }

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 300);

            switch (recipe.getOverlay()) {
                case CONSTRUCTION:
                    guiGraphics.blit(texture, x, y, 36, 222, 18, 18);
                    break;
                case RECYCLING:
                    guiGraphics.blit(texture, x, y, 54, 222, 18, 18);
                    break;
                case SMITHING, TOOLS:
                    guiGraphics.blit(texture, x, y, 72, 222, 18, 18);
                    break;
                default:
                    guiGraphics.blit(texture, x, y, 18, 222, 18, 18);
                    break;
            }

            poseStack.popPose();

            if (selection == i) {
                poseStack.pushPose();
                poseStack.translate(0, 0, 400);
                guiGraphics.blit(texture, x, y, 0, 222, 18, 18);
                poseStack.popPose();
            }
        }

        long now = System.currentTimeMillis();
        if (now - lastTagUpdate > 2000) { // 2 секунды
            lastTagUpdate = now;
            updateTagAnimation();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        boolean isOverCarousel = mouseX >= this.leftPos + 16 &&
                mouseX < this.leftPos + 16 + (18 * 5) &&
                mouseY >= this.topPos + 71 &&
                mouseY < this.topPos + 71 + 90;

        if (isOverCarousel) {
            if (delta > 0) {
                if (this.index > 0) {
                    this.index--;
                    return true;
                }
            } else if (delta < 0) {
                if (this.index < this.size) {
                    this.index++;
                    return true;
                }
            }
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.search != null && this.search.isFocused()) {
            if (keyCode == 69) return true;
            return this.search.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char code, int modifiers) {
        if (this.search != null && this.search.isFocused()) {
            return this.search.charTyped(code, modifiers);
        }
        return super.charTyped(code, modifiers);
    }
}