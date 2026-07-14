package com.hbm.inventory.recipes.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.FluidStackHBM;

import com.hbm.inventory.recipes.common.AStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fully genericized recipes.
 * Features:
 * * Fluid in and output
 * * AStack intput
 * * Chance-based outputs, for selecting items and for selecting items are produced in the first place
 * * Duration
 * * Tags for identification
 *
 * @author hbm
 */
public abstract class GenericRecipes<T extends GenericRecipe> extends SerializableRecipe {

    public static final RandomSource RNG = RandomSource.create();

    /** Alternate recipes, i.e. obtainable otherwise */
    public static final String POOL_PREFIX_ALT = "alt.";
    /** Discoverable recipes, i.e. not obtainable otherwise */
    public static final String POOL_PREFIX_DISCOVER = "discover.";
    /** Secret recipes, self-explanatory. Why even have this comment? */
    public static final String POOL_PREFIX_SECRET = "secret.";

    public List<T> recipeOrderedList = new ArrayList<>();
    public HashMap<String, T> recipeNameMap = new HashMap<>();

    /** Blueprint pool name to list of recipe names that are part of this pool */
    public static HashMap<String, List<String>> blueprintPools = new HashMap<>();
    /** Name to recipe map for all recipes that are part of pools for lookup */
    public static HashMap<String, GenericRecipe> pooledBlueprints = new HashMap<>();

    /** Groups for auto switch functionality (changes recipe automatically based on first solid input) */
    public HashMap<String, List<GenericRecipe>> autoSwitchGroups = new HashMap<>();

    public abstract int inputItemLimit();
    public abstract int inputFluidLimit();
    public abstract int outputItemLimit();
    public abstract int outputFluidLimit();
    public boolean hasDuration() { return true; }
    public boolean hasPower() { return true; }

    /** Adds a recipe to a blueprint pool (i.e. a blueprint item's recipe list) */
    public static void addToPool(String pool, GenericRecipe recipe) {
        List<String> list = blueprintPools.computeIfAbsent(pool, k -> new ArrayList<>());
        list.add(recipe.name);
        pooledBlueprints.put(recipe.name, recipe);
    }

    /** Adds a recipe to an auto switch group (recipe can switch based on first solid input) */
    public void addToGroup(String group, GenericRecipe recipe) {
        List<GenericRecipe> list = autoSwitchGroups.get(group);
        if(list == null) list = new ArrayList<>();
        list.add(recipe);
        autoSwitchGroups.put(group, list);
    }

    public static void clearPools() {
        blueprintPools.clear();
        pooledBlueprints.clear();
    }

    @Override
    public Object getRecipeObject() {
        return this.recipeOrderedList;
    }

    @Override
    public void deleteRecipes() {
        this.recipeOrderedList.clear();
        this.recipeNameMap.clear();
        this.autoSwitchGroups.clear();
    }

    public void register(T recipe) {
        this.recipeOrderedList.add(recipe);
        if(recipeNameMap.containsKey(recipe.name)) throw new IllegalStateException("Recipe " + recipe.name + " has been registered with a duplicate ID!");
        this.recipeNameMap.put(recipe.name, recipe);
    }

    @Override
    public void readRecipe(JsonElement element) {
        JsonObject obj = (JsonObject) element;
        T recipe = instantiateRecipe(obj.get("name").getAsString());

        if(this.inputItemLimit() > 0 && obj.has("inputItem")) recipe.inputItem = readAStackArray(obj.get("inputItem").getAsJsonArray());
        if(this.inputFluidLimit() > 0 && obj.has("inputFluid")) recipe.inputFluid = readFluidArray(obj.get("inputFluid").getAsJsonArray());

        if(this.outputItemLimit() > 0 && obj.has("outputItem")) recipe.outputItem = this.readOutputArray(obj.get("outputItem").getAsJsonArray());
        if(this.outputFluidLimit() > 0 && obj.has("outputFluid")) recipe.outputFluid = readFluidArray(obj.get("outputFluid").getAsJsonArray());

        if(this.hasDuration()) recipe.setDuration(obj.get("duration").getAsInt());
        if(this.hasPower()) recipe.setPower(obj.get("power").getAsLong());

        if(obj.has("icon")) recipe.setIcon(readItemStack(obj.get("icon").getAsJsonArray()));
        if(obj.has("named") && obj.get("named").getAsBoolean()) recipe.setNamed();
        if(obj.has("blueprintpool")) recipe.setPools(obj.get("blueprintpool").getAsString().split(":"));
        if(obj.has("nameWrapper")) recipe.setNameWrapper(obj.get("nameWrapper").getAsString());
        if(obj.has("autoSwitchGroup")) recipe.setGroup(obj.get("autoSwitchGroup").getAsString(), this);

        readExtraData(element, recipe);

        register(recipe);
    }

    public abstract T instantiateRecipe(String name);
    public void readExtraData(JsonElement element, T recipe) { }

    @Override
    public void writeRecipe(Object recipeObject, JsonWriter writer) throws IOException {
        T recipe = (T) recipeObject;

        writer.name("name").value(recipe.name);

        if(this.inputItemLimit() > 0 && recipe.inputItem != null) {
            writer.name("inputItem").beginArray();
            for(AStack stack : recipe.inputItem) writeAStack(stack, writer);
            writer.endArray();
        }

        if(this.inputFluidLimit() > 0 && recipe.inputFluid != null) {
            writer.name("inputFluid").beginArray();
            for(FluidStackHBM stack : recipe.inputFluid) writeFluidStackHBM(stack, writer);
            writer.endArray();
        }

        if(this.outputItemLimit() > 0 && recipe.outputItem != null) {
            writer.name("outputItem").beginArray();
            for(IOutput stack : recipe.outputItem) stack.serialize(writer);
            writer.endArray();
        }

        if(this.outputFluidLimit() > 0 && recipe.outputFluid != null) {
            writer.name("outputFluid").beginArray();
            for(FluidStackHBM stack : recipe.outputFluid) writeFluidStackHBM(stack, writer);
            writer.endArray();
        }

        if(this.hasDuration()) writer.name("duration").value(recipe.duration);
        if(this.hasPower()) writer.name("power").value(recipe.power);

        if(recipe.writeIcon) {
            writer.name("icon");
            writeItemStack(recipe.icon, writer);
        }

        if(recipe.customLocalization) writer.name("named").value(true);
        if(recipe.nameWrapper != null) writer.name("nameWrapper").value(recipe.nameWrapper);
        if(recipe.blueprintPools != null && recipe.blueprintPools.length > 0) writer.name("blueprintpool").value(String.join(":", recipe.blueprintPools));
        if(recipe.autoSwitchGroup != null) writer.name("autoSwitchGroup").value(recipe.autoSwitchGroup);

        writeExtraData(recipe, writer);
    }

    public void writeExtraData(T recipe, JsonWriter writer) { }

    public IOutput[] readOutputArray(JsonArray array) {
        IOutput[] output = new IOutput[array.size()];
        int index = 0;

        for(JsonElement element : array) {
            JsonArray arrayElement = element.getAsJsonArray();
            String type = arrayElement.get(0).getAsString();
            if("single".equals(type)) {
                ChanceOutput co = new ChanceOutput();
                co.deserialize(arrayElement);
                output[index] = co;
            } else if("multi".equals(type)) {
                ChanceOutputMulti com = new ChanceOutputMulti();
                com.deserialize(arrayElement);
                output[index] = com;
            } else {
                throw new IllegalArgumentException("Invalid IOutput type '" + type + "', expected 'single' or 'multi' for recipe " + array);
            }
            index++;
        }

        return output;
    }

    ///////////////
    /// CLASSES ///
    ///////////////
    public interface IOutput {
        /** true for ChanceOutputMulti with a poolsize >1 */
        boolean possibleMultiOutput();
        /** Decides an output, returns a copy of the held result */
        ItemStack collapse();
        /** Returns an itemstack only if possibleMultiOutput is false, null otherwise */
        ItemStack getSingle();
        ItemStack[] getAllPossibilities();
        void serialize(JsonWriter writer) throws IOException;
        void deserialize(JsonArray array);
        List<Component> getLabel();
    }

    /** A chance output, produces either an ItemStack or null */
    public static class ChanceOutput extends WeightedRandom.Item implements IOutput {

        // a weight of 0 means this output is not part of a weighted output

        public ItemStack stack;
        public float chance = 1F;

        public ChanceOutput() { super(0); } // for deserialization
        public ChanceOutput(ItemStack stack) { this(stack, 1F, 0); }
        public ChanceOutput(ItemStack stack, int weight) { this(stack, 1F, weight); }
        public ChanceOutput(ItemStack stack, float chance) { this(stack, chance, 0); }
        public ChanceOutput(ItemStack stack, float chance, int weight) {
            super(weight);
            this.stack = stack;
            this.chance = chance;
        }

        @Override
        public ItemStack collapse() {
            if(this.chance >= 1F) return getSingle().copy();
            return RNG.nextFloat() <= chance ? getSingle().copy() : null;
        }

        @Override public ItemStack getSingle() { return this.stack; }
        @Override public boolean possibleMultiOutput() { return false; }
        @Override public ItemStack[] getAllPossibilities() { return new ItemStack[] {getSingle()}; }

        @Override
        public void serialize(JsonWriter writer) throws IOException {
            boolean standardStack = chance >= 1 && itemWeight == 0;

            writer.beginArray();
            writer.setIndent("");

            if(itemWeight == 0) writer.value("single");
            SerializableRecipe.writeItemStack(stack, writer);
            writer.setIndent("");

            if(!standardStack) {
                writer.value(chance);
                if(itemWeight > 0) writer.value(itemWeight);
            }

            writer.endArray();
            writer.setIndent("  ");
        }

        @Override
        public void deserialize(JsonArray array) {

            if(array.get(0).isJsonPrimitive()) { // "single" tag, don't apply weight
                this.stack = SerializableRecipe.readItemStack(array.get(1).getAsJsonArray());
                if(array.size() > 2) this.chance = array.get(2).getAsFloat();
            } else { // hopefully an array, therefore a weighted result
                this.stack = SerializableRecipe.readItemStack(array.get(0).getAsJsonArray());
                if(array.size() > 1) this.chance = array.get(1).getAsFloat();
                if(array.size() > 2) this.itemWeight = array.get(2).getAsInt();
            }
        }

        @Override
        public List<Component> getLabel() {
            String chanceStr = this.chance >= 1 ? "" : " (" + (int)(this.chance * 1000) / 10F + "%)";
            return List.of(
                    Component.literal("" + this.stack.getCount() + "x " + this.stack.getHoverName().getString() + chanceStr)
                            .withStyle(ChatFormatting.GRAY)
            );
        }
    }

    /** Multiple choice chance output, produces a ChanceOutput chosen randomly by weight */
    public static class ChanceOutputMulti implements IOutput {

        public List<ChanceOutput> pool = new ArrayList<>();

        @Override public ItemStack collapse() {
            int totalWeight = 0;
            for(ChanceOutput co : pool) totalWeight += co.itemWeight;
            int random = RNG.nextInt(totalWeight);
            int cumulative = 0;
            for(ChanceOutput co : pool) {
                cumulative += co.itemWeight;
                if(random < cumulative) return co.collapse();
            }
            return pool.get(0).collapse();
        }
        @Override public boolean possibleMultiOutput() { return pool.size() > 1; }
        @Override public ItemStack getSingle() { return possibleMultiOutput() ? null : pool.get(0).getSingle(); }

        @Override public ItemStack[] getAllPossibilities() {
            ItemStack[] outputs = new ItemStack[pool.size()];
            for(int i = 0; i < outputs.length; i++) outputs[i] = pool.get(i).getAllPossibilities()[0];
            return outputs;
        }

        @Override
        public void serialize(JsonWriter writer) throws IOException {
            writer.beginArray();
            writer.value("multi");
            for(ChanceOutput output : pool) output.serialize(writer);
            writer.endArray();
        }

        @Override
        public void deserialize(JsonArray array) {
            for(JsonElement element : array) {
                ChanceOutput output = new ChanceOutput();
                output.deserialize(element.getAsJsonArray());
                pool.add(output);
            }
        }

        @Override
        public List<Component> getLabel() {
            List<Component> label = new ArrayList<>();
            label.add(Component.literal("One of:"));

            int totalWeight = 0;
            for(ChanceOutput co : pool) totalWeight += co.itemWeight;

            for(ChanceOutput output : pool) {
                float chance = (float) output.itemWeight / (float) totalWeight * output.chance;
                String chanceStr = " (" + (int)(chance * 1000F) / 10F + "%)";
                label.add(Component.literal("  " + output.stack.getCount() + "x " + output.stack.getHoverName().getString() + chanceStr)
                        .withStyle(ChatFormatting.GRAY));
            }
            return label;
        }
    }
}