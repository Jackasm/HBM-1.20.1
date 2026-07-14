package com.hbm.inventory.recipes.loader;

import com.hbm.config.GeneralConfig;
import com.hbm.inventory.FluidStackHBM;

import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.loader.GenericRecipes.ChanceOutput;
import com.hbm.inventory.recipes.loader.GenericRecipes.ChanceOutputMulti;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GenericRecipe {

    protected final String name;
    public String nameWrapper;
    public AStack[] inputItem;
    public FluidStackHBM[] inputFluid;
    public IOutput[] outputItem;
    public FluidStackHBM[] outputFluid;
    public int duration;
    public long power;
    protected ItemStack icon;
    public boolean writeIcon = false;
    public boolean customLocalization = false;
    protected String[] blueprintPools = null;
    public String autoSwitchGroup = null;

    public GenericRecipe(String name) {
        this.name = name;
    }

    public boolean isPooled() { return blueprintPools != null; }
    public String[] getPools() { return this.blueprintPools; }

    public boolean isPartOfPool(String lookingFor) {
        if(!isPooled()) return false;
        for(String pool : blueprintPools) if (pool.equals(lookingFor)) return true;
        return false;
    }

    public GenericRecipe setDuration(int duration) { this.duration = duration; return this; }
    public GenericRecipe setPower(long power) { this.power = power; return this; }
    public GenericRecipe setup(int duration, long power) { return this.setDuration(duration).setPower(power); }
    public GenericRecipe setupNamed(int duration, long power) { return this.setDuration(duration).setPower(power).setNamed(); }
    public GenericRecipe setNameWrapper(String wrapper) { this.nameWrapper = wrapper; return this; }
    public GenericRecipe setIcon(ItemStack icon) { this.icon = icon; this.writeIcon = true; return this; }

    public GenericRecipe setIcon(Item item, int meta) {
        ItemStack stack = new ItemStack(item, 1);
        stack.getOrCreateTag().putInt("CustomModelData", meta);
        return this.setIcon(stack);
    }
    public GenericRecipe setIcon(Item item) { return this.setIcon(new ItemStack(item)); }
    public GenericRecipe setIcon(Block block) { return this.setIcon(new ItemStack(block)); }
    public GenericRecipe setNamed() { this.customLocalization = true; return this; }
    public GenericRecipe setPools(String... pools) { this.blueprintPools = pools; for(String pool : pools) GenericRecipes.addToPool(pool, this); return this; }
    public GenericRecipe setGroup(String autoSwitch, GenericRecipes set) { this.autoSwitchGroup = autoSwitch; set.addToGroup(autoSwitch, this); return this; }

    public GenericRecipe inputItems(AStack... input) { this.inputItem = input; for(AStack stack : this.inputItem) if(stack.stacksize > 64) throw new IllegalArgumentException("AStack in " + this.name + " exceeds stack limit!"); return this; }
    public GenericRecipe inputItemsEx(AStack... input) { if(!GeneralConfig.enableExpensiveMode.get()) return this; this.inputItem = input; for(AStack stack : this.inputItem) if(stack.stacksize > 64) throw new IllegalArgumentException("AStack in " + this.name + " exceeds stack limit!"); return this; }
    public GenericRecipe inputFluids(FluidStackHBM... input) { this.inputFluid = input; return this; }
    public GenericRecipe inputFluidsEx(FluidStackHBM... input) { if(!GeneralConfig.enableExpensiveMode.get()) return this; this.inputFluid = input; return this; }
    public GenericRecipe outputItems(IOutput... output) { this.outputItem = output; return this; }
    public GenericRecipe outputFluids(FluidStackHBM... output) { this.outputFluid = output; return this; }

    public GenericRecipe outputItems(ItemStack... output) {
        this.outputItem = new IOutput[output.length];
        for(int i = 0; i < outputItem.length; i++) this.outputItem[i] = new ChanceOutput(output[i]);
        return this;
    }

    public GenericRecipe setIconToFirstIngredient() {
        if(this.inputItem != null) {
            List<ItemStack> stacks = this.inputItem[0].extractForNEI();
            if(!stacks.isEmpty()) this.icon = stacks.get(0);
        }
        return this;
    }

    public ItemStack getIcon() {
        if(icon == null) {
            if(outputItem != null) {
                if(outputItem[0] instanceof ChanceOutput) icon = ((ChanceOutput) outputItem[0]).stack.copy();
                if(outputItem[0] instanceof ChanceOutputMulti) icon = ((ChanceOutputMulti) outputItem[0]).pool.get(0).stack.copy();
                return icon;
            }
            if(outputFluid != null) {
                icon = ItemFluidIcon.make(outputFluid[0]);
            }
        }

        if(icon == null) icon = new ItemStack(ModItems.NOTHING.get());
        return icon;
    }

    public String getInternalName() {
        return this.name;
    }

    public String getLocalizedName() {
        String name = null;
        if(customLocalization) name = Component.translatable(this.name).getString();
        if(name == null) name = this.getIcon().getHoverName().getString();
        if(this.nameWrapper != null) name = Component.translatable(this.nameWrapper, name).getString();
        return name;
    }

    public List<Component> print() {
        List<Component> list = new ArrayList<>();
        list.add(Component.literal(this.getLocalizedName()).withStyle(ChatFormatting.YELLOW));

        // autoswitch group
        if(this.autoSwitchGroup != null) {
            String[] lines = Component.translatable("autoswitch", Component.translatable(this.autoSwitchGroup)).getString().split("\\$");
            for(String line : lines) {
                list.add(Component.literal(line).withStyle(ChatFormatting.GOLD));
            }
        }

        // duration (seconds)
        if(duration > 0) {
            double seconds = this.duration / 20D;
            list.add(Component.translatable("gui.recipe.duration")
                    .append(": " + seconds + "s")
                    .withStyle(ChatFormatting.RED));
        }

        // power / consumption
        if(power > 0) {
            list.add(Component.translatable("gui.recipe.consumption")
                    .append(": " + BobMathUtil.getShortNumber(power) + "HE/t")
                    .withStyle(ChatFormatting.RED));
        }

        // input label + items
        list.add(Component.translatable("gui.recipe.input").withStyle(ChatFormatting.BOLD));
        if(inputItem != null) {
            for(AStack stack : inputItem) {
                ItemStack display = stack.extractForCyclingDisplay(20);
                list.add(Component.literal("  " + display.getCount() + "x " + display.getHoverName().getString())
                        .withStyle(ChatFormatting.GRAY));
            }
        }
        if(inputFluid != null) {
            for(FluidStackHBM fluid : inputFluid) {
                String pressurePart = fluid.pressure == 0 ? "" : " " + Component.translatable("gui.recipe.atPressure").getString() + " " + ChatFormatting.RED + fluid.pressure + " PU";
                list.add(Component.literal("  " + fluid.fill + "mB " + fluid.type.getLocalizedName() + pressurePart)
                        .withStyle(ChatFormatting.BLUE));
            }
        }

        // output label + items
        list.add(Component.translatable("gui.recipe.output").withStyle(ChatFormatting.BOLD));
        if(outputItem != null) {
            for(IOutput output : outputItem) {
                for(Component line : output.getLabel()) {
                    list.add(Component.literal("  ").append(line));
                }
            }
        }
        if(outputFluid != null) {
            for(FluidStackHBM fluid : outputFluid) {
                String pressurePart = fluid.pressure == 0 ? "" : " " + Component.translatable("gui.recipe.atPressure").getString() + " " + ChatFormatting.RED + fluid.pressure + " PU";
                list.add(Component.literal("  " + fluid.fill + "mB " + fluid.type.getLocalizedName() + pressurePart)
                        .withStyle(ChatFormatting.BLUE));
            }
        }

        return list;
    }

    /** Default impl only matches localized name substring, can be extended to include ingredient as well */
    public boolean matchesSearch(String substring) {
        return getLocalizedName().toLowerCase(Locale.US).contains(substring.toLowerCase(Locale.US));
    }
}