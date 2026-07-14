package com.hbm.inventory.gui;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.inventory.recipes.RecipeCache;
import com.hbm.inventory.recipes.RecipeType;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class CraftingOverviewScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResLocation(RefStrings.MODID, "textures/gui/crafting_overview.png");
    private static final ResourceLocation TAB_BAR = ResLocation(RefStrings.MODID, "textures/gui/crafting_tab_bar.png");

    private static final int GUI_WIDTH = 240;
    private static final int GUI_HEIGHT = 200;
    private static final int TAB_HEIGHT = 28;
    private static final int TAB_WIDTH = 36;
    private static final int RECIPES_PER_PAGE = 24; // 6x4
    private static final int RECIPES_PER_ROW = 6;
    private static final int RECIPE_SLOT_SIZE = 24;
    private static final int RECIPE_SPACING = 6;

    private final Player player;
    private final List<RecipeType> availableTypes = new ArrayList<>();
    private final Map<RecipeType, List<RecipeCache.DisplayRecipe>> recipesMap = new LinkedHashMap<>();
    private final Map<RecipeType, ItemStack> typeIcons = new HashMap<>();

    private int selectedTabIndex = 0;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int tabsPerRow = 0;

    private int guiLeft, guiTop;
    private int tabBarLeft, tabBarTop;
    private int tabBarWidth;

    // Кэш иконок для типов крафтеров
    private static final Map<RecipeType, ItemStack> ICON_CACHE = new HashMap<>();

    public CraftingOverviewScreen(Player player) {
        super(Component.translatable("hbm.crafter.title"));
        this.player = player;

        initIcons();

        // Получаем список типов для отображения
        Set<RecipeType> typesToShow;

        if (player.isCreative()) {
            // Креатив: все типы с рецептами
            typesToShow = new HashSet<>();
            for (RecipeType type : RecipeType.values()) {
                if (!RecipeCache.getDisplayRecipes(type).isEmpty()) {
                    typesToShow.add(type);
                }
            }
        } else {
            // Выживание: только разблокированные
            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
            typesToShow = props != null ? props.getUnlockedCrafters() : Collections.emptySet();
        }

        // Сортируем и добавляем
        typesToShow.stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .forEach(type -> {
                    List<RecipeCache.DisplayRecipe> recipes = RecipeCache.getDisplayRecipes(type);
                    if (!recipes.isEmpty()) {
                        availableTypes.add(type);
                        recipesMap.put(type, recipes);
                        typeIcons.put(type, getIconForType(type));
                    }
                });

        if (!availableTypes.isEmpty()) {
            updateScrollMax();
        }
    }

    private void initIcons() {
        // Инициализируем кэш иконок
        for (RecipeType type : RecipeType.values()) {
            getIconForType(type);
        }
    }

    private ItemStack getIconForType(RecipeType type) {
        if (ICON_CACHE.containsKey(type)) {
            return ICON_CACHE.get(type);
        }

        ItemStack icon;

        switch (type) {
            case ANVIL -> icon = new ItemStack(ModItems.ANVIL_IRON.get());
            case PRESS -> icon = new ItemStack(ModItems.MACHINE_PRESS_ITEM.get());
            case BLAST_FURNACE -> icon = new ItemStack(ModItems.MACHINE_BLAST_FURNACE.get());
            case COMBINATION_FURNACE -> icon = new ItemStack(ModItems.FURNACE_COMBINATION.get());
            case ROTARY_FURNACE -> icon = new ItemStack(ModItems.MACHINE_ROTARY_FURNACE.get());
            case CRUCIBLE -> icon = new ItemStack(ModItems.MACHINE_CRUCIBLE.get());
            case SOLDERING_STATION -> icon = new ItemStack(ModItems.MACHINE_SOLDERING_STATION.get());
            case ARC_WELDER -> icon = new ItemStack(ModItems.MACHINE_ARC_WELDER.get());
            case GAS_CENTRIFUGE -> icon = new ItemStack(ModItems.MACHINE_GAS_CENT.get());
            case CRYSTALLIZER -> icon = new ItemStack(ModItems.MACHINE_CRYSTALLIZER.get());
            case CENTRIFUGE -> icon = new ItemStack(ModItems.MACHINE_CENTRIFUGE.get());
            default -> icon = new ItemStack(ModItems.ANVIL_IRON.get());
        }

        // Сопоставляем тип крафтера с блоком-иконкой

        if (icon.isEmpty()) {
            // Fallback - используем первый выходной предмет из рецептов
            List<RecipeCache.DisplayRecipe> recipes = RecipeCache.getDisplayRecipes(type);
            if (!recipes.isEmpty() && !recipes.get(0).getOutputs().isEmpty()) {
                icon = recipes.get(0).getOutputs().get(0);
            }
        }

        ICON_CACHE.put(type, icon);
        return icon;
    }

    private ItemStack getBlockStack(String blockName) {
        Block block = ForgeRegistries.BLOCKS.getValue(ResLocation(RefStrings.MODID, blockName));
        if (block != null) {
            return new ItemStack(block);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void init() {
        super.init();

        // Рассчитываем позиции
        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2 + TAB_HEIGHT + 4;

        // Вкладки располагаются над основным окном
        this.tabBarLeft = this.guiLeft;
        this.tabBarTop = this.guiTop - TAB_HEIGHT - 4;
        this.tabBarWidth = GUI_WIDTH;

        // Вычисляем сколько вкладок помещается в ряд
        this.tabsPerRow = tabBarWidth / (TAB_WIDTH + 4);

        // Кнопки прокрутки
        this.addRenderableWidget(
                Button.builder(Component.literal("↑"), b -> scrollUp())
                        .bounds(guiLeft + GUI_WIDTH - 24, guiTop + 30, 18, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("↓"), b -> scrollDown())
                        .bounds(guiLeft + GUI_WIDTH - 24, guiTop + 60, 18, 20)
                        .build()
        );
    }

    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
            updateScrollMax();
        }
    }

    private void scrollDown() {
        if (scrollOffset < maxScroll) {
            scrollOffset++;
            updateScrollMax();
        }
    }

    private void updateScrollMax() {
        if (!availableTypes.isEmpty() && selectedTabIndex < availableTypes.size()) {
            RecipeType current = availableTypes.get(selectedTabIndex);
            List<RecipeCache.DisplayRecipe> recipes = recipesMap.get(current);
            maxScroll = Math.max(0, recipes.size() - RECIPES_PER_PAGE);
            scrollOffset = Math.min(scrollOffset, maxScroll);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.onClose();
            return true;
        }

        if (keyCode == 262) { // RIGHT
            if (selectedTabIndex < availableTypes.size() - 1) {
                selectedTabIndex++;
                scrollOffset = 0;
                updateScrollMax();
            }
            return true;
        }

        if (keyCode == 263) { // LEFT
            if (selectedTabIndex > 0) {
                selectedTabIndex--;
                scrollOffset = 0;
                updateScrollMax();
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Проверяем клик по вкладкам
            int tabX = tabBarLeft + 4;
            int tabY = tabBarTop + 2;
            int row = 0;
            int col = 0;

            for (int i = 0; i < availableTypes.size(); i++) {
                col = i % tabsPerRow;
                row = i / tabsPerRow;

                int x = tabX + col * (TAB_WIDTH + 4);
                int y = tabY + row * (TAB_HEIGHT + 4);

                if (mouseX >= x && mouseX <= x + TAB_WIDTH &&
                        mouseY >= y && mouseY <= y + TAB_HEIGHT) {
                    selectedTabIndex = i;
                    scrollOffset = 0;
                    updateScrollMax();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        // Рисуем основное окно
        graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        // Рисуем заголовок
        graphics.drawString(font, Component.translatable("hbm.crafter.title").getString(),
                guiLeft + 10, guiTop + 6, 0xFFFFFF, false);

        // Рисуем вкладки
        renderTabs(graphics, mouseX, mouseY);

        // Рисуем рецепты
        if (!availableTypes.isEmpty()) {
            renderRecipes(graphics, mouseX, mouseY);
        } else {
            graphics.drawString(font, "No crafters unlocked",
                    guiLeft + 20, guiTop + 60, 0xFF6666, false);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void renderTabs(GuiGraphics graphics, int mouseX, int mouseY) {
        if (availableTypes.isEmpty()) return;

        int tabX = tabBarLeft + 4;
        int tabY = tabBarTop + 2;

        // Фон для панели вкладок
        graphics.fill(tabBarLeft, tabBarTop, tabBarLeft + tabBarWidth,
                tabBarTop + TAB_HEIGHT + 4 + ((availableTypes.size() - 1) / tabsPerRow) * (TAB_HEIGHT + 4),
                0xCC222222);

        // Рамка панели вкладок
        graphics.fill(tabBarLeft, tabBarTop, tabBarLeft + tabBarWidth, tabBarTop + 1, 0xFF555555);
        graphics.fill(tabBarLeft, tabBarTop, tabBarLeft + 1,
                tabBarTop + TAB_HEIGHT + 4 + ((availableTypes.size() - 1) / tabsPerRow) * (TAB_HEIGHT + 4), 0xFF555555);
        graphics.fill(tabBarLeft + tabBarWidth - 1, tabBarTop,
                tabBarLeft + tabBarWidth,
                tabBarTop + TAB_HEIGHT + 4 + ((availableTypes.size() - 1) / tabsPerRow) * (TAB_HEIGHT + 4), 0xFF555555);

        for (int i = 0; i < availableTypes.size(); i++) {
            RecipeType type = availableTypes.get(i);
            boolean selected = i == selectedTabIndex;

            int col = i % tabsPerRow;
            int row = i / tabsPerRow;

            int x = tabX + col * (TAB_WIDTH + 4);
            int y = tabY + row * (TAB_HEIGHT + 4);

            // Фон вкладки
            int color = selected ? 0xFF888888 : 0xFF444444;
            graphics.fill(x, y, x + TAB_WIDTH, y + TAB_HEIGHT, color);

            // Рамка выделенной вкладки
            if (selected) {
                graphics.fill(x, y + TAB_HEIGHT - 2, x + TAB_WIDTH, y + TAB_HEIGHT, 0xFFFFAA00);
                graphics.fill(x, y, x + TAB_WIDTH, y + 1, 0xFFFFAA00);
                graphics.fill(x, y, x + 1, y + TAB_HEIGHT, 0xFFFFAA00);
                graphics.fill(x + TAB_WIDTH - 1, y, x + TAB_WIDTH, y + TAB_HEIGHT, 0xFFFFAA00);
            }

            // Иконка крафтера
            ItemStack icon = typeIcons.get(type);
            if (icon != null && !icon.isEmpty()) {
                graphics.renderItem(icon, x + (TAB_WIDTH - 16) / 2, y + (TAB_HEIGHT - 16) / 2);
                graphics.renderItemDecorations(font, icon, x + (TAB_WIDTH - 16) / 2, y + (TAB_HEIGHT - 16) / 2, "");
            } else {
                // Если иконки нет - рисуем название
                graphics.drawString(font, type.getDisplayName().substring(0, Math.min(2, type.getDisplayName().length())),
                        x + 8, y + 8, 0xFFFFFF, false);
            }

            // Тултип при наведении
            if (mouseX >= x && mouseX <= x + TAB_WIDTH && mouseY >= y && mouseY <= y + TAB_HEIGHT) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(type.getDisplayName()));
                if (!selected) {
                    tooltip.add(Component.literal("Click to view").withStyle(net.minecraft.ChatFormatting.GRAY));
                }
                graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    private void renderRecipes(GuiGraphics graphics, int mouseX, int mouseY) {
        RecipeType currentType = availableTypes.get(selectedTabIndex);
        List<RecipeCache.DisplayRecipe> recipes = recipesMap.get(currentType);

        if (recipes == null || recipes.isEmpty()) {
            graphics.drawString(font, Component.translatable("hbm.crafter.no_recipes").getString(),
                    guiLeft + 20, guiTop + 60, 0xFF6666, false);
            return;
        }

        int startX = guiLeft + 16;
        int startY = guiTop + 28;
        int slotSize = RECIPE_SLOT_SIZE;
        int spacing = RECIPE_SPACING;

        int visibleCount = Math.min(RECIPES_PER_PAGE, recipes.size() - scrollOffset);

        for (int i = 0; i < visibleCount; i++) {
            RecipeCache.DisplayRecipe displayRecipe = recipes.get(i + scrollOffset);
            List<ItemStack> outputs = displayRecipe.getOutputs();

            if (outputs.isEmpty()) continue;

            ItemStack result = outputs.get(0);

            int col = i % RECIPES_PER_ROW;
            int row = i / RECIPES_PER_ROW;

            int x = startX + col * (slotSize + spacing);
            int y = startY + row * (slotSize + spacing);

            // Рамка слота
            graphics.fill(x - 2, y - 2, x + slotSize - 2, y + slotSize - 2, 0xFF444444);
            graphics.fill(x - 1, y - 1, x + slotSize - 3, y + slotSize - 3, 0xFF222222);

            // Рендерим предмет
            graphics.renderItem(result, x, y);
            graphics.renderItemDecorations(font, result, x, y, "");

            // Тултип при наведении
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(result.getHoverName().copy().withStyle(net.minecraft.ChatFormatting.YELLOW));
                tooltip.add(Component.literal(" "));
                tooltip.add(Component.translatable("hbm.crafter.ingredients").withStyle(net.minecraft.ChatFormatting.GRAY));

                List<ItemStack> ingredients = displayRecipe.getIngredients();
                if (!ingredients.isEmpty()) {
                    for (ItemStack ingredient : ingredients) {
                        tooltip.add(Component.literal("• ").append(ingredient.getHoverName())
                                .append(Component.literal(" x" + ingredient.getCount())));
                    }
                } else {
                    tooltip.add(Component.literal("  No ingredients").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
                }

                graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}