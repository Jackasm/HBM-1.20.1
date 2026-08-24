package com.hbm.datagen.recipes.ingredient;

import com.google.gson.JsonObject;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemPlasticScrap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

public class ScrapPlasticIngredient extends Ingredient {

    private static ItemStack[] scrapVariants = null;

    private static ItemStack[] getScrapVariants() {
        if (scrapVariants == null) {
            ItemPlasticScrap.ScrapType[] types = ItemPlasticScrap.ScrapType.values();
            scrapVariants = new ItemStack[types.length];
            for (int i = 0; i < types.length; i++) {
                ItemStack stack = new ItemStack(ModItems.SCRAP_PLASTIC.get());
                stack.getOrCreateTag().putInt("CustomModelData", i);
                scrapVariants[i] = stack;
            }
        }
        return scrapVariants;
    }

    public ScrapPlasticIngredient() {
        super(Stream.empty());
    }

    @Override
    public boolean test(ItemStack stack) {
        if (Objects.requireNonNull(stack).isEmpty()) return false;
        return stack.getItem() == ModItems.SCRAP_PLASTIC.get();
    }

    @Override
    public @NotNull ItemStack @NotNull [] getItems() {
        return getScrapVariants();
    }

    @Override
    public @NotNull JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "hbm:scrap_plastic");
        return json;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static @NotNull ScrapPlasticIngredient of() {
        return new ScrapPlasticIngredient();
    }

    public static class Serializer implements IIngredientSerializer<ScrapPlasticIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull ScrapPlasticIngredient parse(@NotNull JsonObject json) {
            return new ScrapPlasticIngredient();
        }

        @Override
        public @NotNull ScrapPlasticIngredient parse(@NotNull FriendlyByteBuf buffer) {
            return new ScrapPlasticIngredient();
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull ScrapPlasticIngredient ingredient) {}
    }
}