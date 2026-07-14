package com.hbm.datagen.recipes.ingredient;

import com.google.gson.JsonObject;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.fluid.ItemFluidBucket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.stream.Stream;

public class FluidBucketIngredient extends Ingredient {

    private final FluidTypeHBM fluid;

    public FluidBucketIngredient(FluidTypeHBM fluid) {
        super(Stream.of(new ItemValue(ItemFluidBucket.createForFluid(fluid))));
        this.fluid = fluid;
    }

    @Override
    public @NotNull JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "hbm:fluid_bucket");
        json.addProperty("fluid", fluid.getName().toLowerCase(Locale.ROOT));
        return json;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static FluidBucketIngredient of(FluidTypeHBM fluid) {
        return new FluidBucketIngredient(fluid);
    }

    public static class Serializer implements IIngredientSerializer<FluidBucketIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull FluidBucketIngredient parse(@NotNull JsonObject json) {
            String fluidName = json.get("fluid").getAsString();
            FluidTypeHBM fluid = Fluids.fromName(fluidName);
            return new FluidBucketIngredient(fluid);
        }

        @Override
        public @NotNull FluidBucketIngredient parse(@NotNull FriendlyByteBuf buffer) {
            String fluidName = buffer.readUtf();
            FluidTypeHBM fluid = Fluids.fromName(fluidName);
            return new FluidBucketIngredient(fluid);
        }

        @Override
        public void write(@NotNull FriendlyByteBuf buffer, @NotNull FluidBucketIngredient ingredient) {
            buffer.writeUtf(ingredient.fluid.getName());
        }
    }
}