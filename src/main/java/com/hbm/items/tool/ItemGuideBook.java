package com.hbm.items.tool;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.gui.GUIScreenGuide;
import com.hbm.inventory.recipes.AnvilRecipes;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ItemGuideBook extends Item{

    public ItemGuideBook() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 200;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide) {
            int damage = stack.getDamageValue();
            BookType type = BookType.getType(damage);
            Minecraft.getInstance().setScreen(new GUIScreenGuide(type));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int damage = stack.getDamageValue();
        BookType type = BookType.getType(damage);
        tooltip.add(Component.translatable(type.title).withStyle(ChatFormatting.GRAY));
    }


    public enum BookType {
        TEST("book.test.cover", 2F, BookContent::addTestPages),
        RBMK("book.rbmk.cover", 1.5F, BookContent::addRBMKPages),
        HADRON("book.error.cover", 1.5F, BookContent::addHadronPages),
        STARTER("book.starter.cover", 1.5F, BookContent::addStarterPages);

        public final String title;
        public final float titleScale;
        public final List<GuidePage> pages = new ArrayList<>();

        BookType(String title, float titleScale, Consumer<List<GuidePage>> pageGenerator) {
            this.title = title;
            this.titleScale = titleScale;
            pageGenerator.accept(pages);
        }

        public static BookType getType(int damage) {
            BookType[] values = values();
            return values[Math.abs(damage) % values.length];
        }

    }

    public static class BookContent {
        public static void addTestPages(List<GuidePage> pages) {
            // Страница 0: автоматический квест "Руководство по выживанию"
            pages.add(new GuidePage()
                    .addTitle("book.survival.title", 0x800000, 1F)
                    .addText("book.survival.page1", 2F)
                    .setQuest("hbm.quest.survival_guide", true, false)
                    .setCompleteOnLastPage(true));
            pages.add(new GuidePage()
                    .addText("book.survival.page2", 2F));

            // Страница 1: квест на крафт железной печи
            pages.add(new GuidePage()
                    .addTitle("book.craft_furnace.title", 0x800000, 1F)
                    .addText("book.craft_furnace.page1", 1.75F));
            pages.add(new GuidePage()
                    .setCraftingRecipe(new ItemStack(ModBlocks.FURNACE_IRON.get()))
                    .setQuest("hbm.quest.craft_iron_furnace", false, true));

            // Страница с квестом на крафт наковальни
            pages.add(new GuidePage()
                    .addTitle("hbm.quest.craft_anvil.desc", 0x800000, 0.5F)
                    .addText("book.craft_anvil.page1", 1.75F));
            pages.add(new GuidePage()
                    .setCraftingRecipe(new ItemStack(ModBlocks.ANVIL_IRON.get()))
                    .setQuest("hbm.quest.craft_anvil", false, true));

            // Страница с квестом на крафт доменной печи (в наковальне)
            pages.add(new GuidePage()
                    .addTitle("book.blast_furnace.title", 0x800000, 0.5F)
                    .addText("book.blast_furnace.page1", 1.75F));
            pages.add(new GuidePage()
                    .setAnvilRecipe(new ItemStack(ModBlocks.MACHINE_BLAST_FURNACE.get()))
                    .setAnvilPosition(20, 80)
                    .setQuest("hbm.quest.craft_blast_furnace", false, true));
        }

        public static void addRBMKPages(List<GuidePage> pages) {
            for (int i = 1; i <= 16; i++) {
                pages.add(new GuidePage()
                        .addTitle("book.rbmk.title" + i, 0x800000, 1F)
                        .addText("book.rbmk.page" + i, 2F)
                        .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/rbmk" + i + ".png"), 95, 80, 60));
            }
        }

        public static void addHadronPages(List<GuidePage> pages) {
            for (int i = 1; i <= 9; i++) {
                pages.add(new GuidePage()
                        .addTitle("book.error.title" + i, 0x800000, 1F)
                        .addText("book.error.page" + i, 2F));
            }
        }

        public static void addStarterPages(List<GuidePage> pages) {
            pages.add(new GuidePage()
                    .addTitle("book.starter.title1", 0x800000, 1F)
                    .addText("book.starter.page1", 2F)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter1.png"), 96, 101, 56));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title2", 0x800000, 1F)
                    .addText("book.starter.page2", 2F)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/mask_piss.png"), 85, 64, 64));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title3", 0x800000, 1F)
                    .addText("book.starter.page3", 2F)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter3.png"), 89, 100, 64));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title4", 0x800000, 1F)
                    .addText("book.starter.page4", 1.4F, 0, 6, 72)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/template_folder.png"), 72, 30, 24, 24)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/stamp_iron_flat.png"), 72, 60, 24, 24)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/assembly_template.png"), 72, 90, 24, 24)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/chemistry_template.png"), 72, 120, 24, 24));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title5", 0x800000, 1F)
                    .addText("book.starter.page5", 2F));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title6", 0x800000, 1F)
                    .addText("book.starter.page6a", 2F)
                    .addText("book.starter.page6b", 2F, 0, 96, 100)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter6.png"), 9, 89, 84, 36));
            pages.add(new GuidePage()
                    .addText("book.starter.page7a", 2F)
                    .addText("book.starter.page7b", 2F, 0, 95, 100)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter7.png"), 9, 67, 84, 58));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title8", 0x800000, 1F)
                    .addText("book.starter.page8a", 2F, 0, -1, 50)
                    .addText("book.starter.page8b", 2F, 50, 70, 50)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter8a.png"), 53, 36, 47, 61)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter8b.png"), 0, 102, 47, 61));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title9", 0x800000, 1F)
                    .addText("book.starter.page9", 2F)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/ingot_polymer.png"), 4, 106, 24, 24)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/ingot_desh.png"), 28, 130, 24, 24)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/solid_fuel_presto_triplet.png"), 52, 106, 24, 24)
                    .addImage(ResLocation(RefStrings.MODID, "textures/items/canister_gasoline.png"), 76, 130, 24, 24));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title10", 0x800000, 1F)
                    .addText("book.starter.page10", 2F)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter10.png"), 0, 115, 100, 39));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title11", 0x800000, 1F)
                    .addText("book.starter.page11", 2F, 0, -1, 60)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter11a.png"), 61, 36, 45, 57)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter11b.png"), 61, 97, 45, 57));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title12", 0xfece00, 1F)
                    .addText("book.starter.page12a", 3F)
                    .addText("book.starter.page12b", 2F, 0, 20, 100));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title13", 0x800000, 1F)
                    .addText("book.starter.page13", 2F)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter13.png"), 110, 84, 42));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title14", 0x800000, 1F)
                    .addText("book.starter.page14", 2F, 0, 54, 100)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter14.png"), 34, 100, 46));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title15", 0x800000, 1F)
                    .addText("book.starter.page15", 2F));
            pages.add(new GuidePage()
                    .addTitle("book.starter.title16", 0x800000, 1F)
                    .addText("book.starter.page16", 2F));
            pages.add(new GuidePage());
            pages.add(new GuidePage()
                    .addTitle("book.starter.title18", 0x800000, 1F)
                    .addText("book.starter.page18", 2F)
                    .addImage(ResLocation(RefStrings.MODID, "textures/gui/book/starter18.png"), 10, 69, 100, 100));
        }
    }

    public static class GuidePage {
        public String title;
        public int titleColor;
        public float titleScale;
        public final List<GuideText> texts = new ArrayList<>();
        public final List<GuideImage> images = new ArrayList<>();

        public String questId;
        public boolean autoAccept;
        public boolean showAcceptButton;
        public boolean completeOnLastPage;

        public List<ItemStack> craftingGrid = new ArrayList<>(); // 9 элементов
        public ItemStack craftingResult = ItemStack.EMPTY;
        public int craftingX = 0;
        public int cellSize = 18;

        public List<ItemStack> anvilRequirements = new ArrayList<>();
        public ItemStack anvilResult = ItemStack.EMPTY;
        public int anvilX = 0, anvilY = 0;

        public GuidePage setCraftingRecipe(ItemStack result) {
            this.craftingGrid = getCraftingRecipe(result);
            this.craftingResult = result.copy();
            return this;
        }

        public GuidePage setAnvilRecipe(ItemStack result) {
            List<ItemStack> requirements = getAnvilRecipe(result);
            this.anvilRequirements = new ArrayList<>(requirements);
            this.anvilResult = result.copy();
            return this;
        }

        public GuidePage setAnvilPosition(int x, int y) {
            this.anvilX = x;
            this.anvilY = y;
            return this;
        }

        public GuidePage setQuest(String questId, boolean autoAccept, boolean showButton) {
            this.questId = questId;
            this.autoAccept = autoAccept;
            this.showAcceptButton = showButton;
            return this;
        }

        public GuidePage setCompleteOnLastPage(boolean complete) {
            this.completeOnLastPage = complete;
            return this;
        }

        public GuidePage addTitle(String title, int color, float scale) {
            this.title = title;
            this.titleColor = color;
            this.titleScale = scale;
            return this;
        }

        public GuidePage addText(String text) {
            texts.add(new GuideText(text));
            return this;
        }

        public GuidePage addText(String text, float scale) {
            texts.add(new GuideText(text).setScale(scale));
            return this;
        }

        public GuidePage addText(String text, int xOffset, int yOffset, int width) {
            texts.add(new GuideText(text).setSize(xOffset, yOffset, width));
            return this;
        }

        public GuidePage addText(String text, float scale, int xOffset, int yOffset, int width) {
            texts.add(new GuideText(text).setSize(xOffset, yOffset, width).setScale(scale));
            return this;
        }

        public GuidePage addImage(ResourceLocation image, int xOffset, int yOffset, int sizeX, int sizeY) {
            images.add(new GuideImage(image, xOffset, yOffset, sizeX, sizeY));
            return this;
        }

        public GuidePage addImage(ResourceLocation image, int yOffset, int sizeX, int sizeY) {
            images.add(new GuideImage(image, -1, yOffset, sizeX, sizeY));
            return this;
        }
    }

    public static class GuideText {
        public String text;
        public float scale = 1F;
        public int xOffset = 0;
        public int yOffset = -1;
        public int width = 100;

        public GuideText(String text) {
            this.text = text;
        }

        public GuideText setScale(float scale) {
            this.scale = scale;
            return this;
        }

        public GuideText setSize(int xOffset, int yOffset, int width) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.width = width;
            return this;
        }
    }

    public record GuideImage(ResourceLocation image, int x, int y, int sizeX, int sizeY) {
    }



    public static List<ItemStack> getCraftingRecipe(ItemStack result) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        for (Recipe<?> recipe : recipeManager.getRecipes()) {
            if (recipe.getType() == RecipeType.CRAFTING &&
                    recipe.getResultItem(RegistryAccess.EMPTY).getItem() == result.getItem()) {
                if (recipe instanceof ShapedRecipe shaped) {
                    List<ItemStack> grid = new ArrayList<>();
                    for (Ingredient ingredient : shaped.getIngredients()) {
                        if (ingredient.isEmpty()) {
                            grid.add(ItemStack.EMPTY);
                        } else {
                            ItemStack[] stacks = ingredient.getItems();
                            grid.add(stacks.length > 0 ? stacks[0].copy() : ItemStack.EMPTY);
                        }
                    }
                    while (grid.size() < 9) grid.add(ItemStack.EMPTY);
                    return grid;
                }
            }
        }
        return new ArrayList<>();
    }

    public static List<ItemStack> getAnvilRecipe(ItemStack result) {
        for (AnvilRecipes.AnvilConstructionRecipe recipe : AnvilRecipes.constructionRecipes) {
            for (AnvilRecipes.AnvilOutput output : recipe.output) {
                if (ItemStack.isSameItem(output.stack, result)) {
                    List<ItemStack> requirements = new ArrayList<>();
                    for (AStack astack : recipe.input) {
                        if (astack instanceof ComparableStack cs) {
                            ItemStack stack = cs.toStack();
                            requirements.add(stack.copy());
                        } else if (astack instanceof TagStack ts) {
                            // Для тега берём первый предмет из списка (для отображения)
                            List<ItemStack> tagItems = ts.extractForNEI();
                            if (!tagItems.isEmpty()) {
                                requirements.add(tagItems.get(0).copy());
                            }
                        }
                    }
                    return requirements;
                }
            }
        }
        return new ArrayList<>();
    }

}