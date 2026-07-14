package com.hbm.datagen.recipes.ingredient;

import com.google.gson.JsonObject;
import com.hbm.items.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

public class AnyAshIngredient extends Ingredient {

    public AnyAshIngredient() {
        super(Stream.empty());
    }

    @Override
    public boolean test(ItemStack stack) {
        if (Objects.requireNonNull(stack).isEmpty()) return false;
        return stack.getItem() == ModItems.POWDER_ASH.get();

    }

    @Override
    public @NotNull JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "hbm:any_ash");
        return json;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static @NotNull AnyAshIngredient of() {
        return new AnyAshIngredient();
    }

    public static class Serializer implements IIngredientSerializer<AnyAshIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull AnyAshIngredient parse(@NotNull JsonObject json) {
            return new AnyAshIngredient();
        }

        @Override
        public @NotNull AnyAshIngredient parse(@NotNull FriendlyByteBuf buffer) {
            return new AnyAshIngredient();
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull AnyAshIngredient ingredient) {}
    }
}