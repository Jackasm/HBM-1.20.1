package com.hbm.items.machine;

import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.inventory.recipes.CrucibleRecipes.CrucibleRecipe;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ItemCrucibleTemplate extends Item {

    public ItemCrucibleTemplate(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        CrucibleRecipe recipe = getRecipe(stack);
        if (recipe == null) return super.getName(stack);

        String baseName = Component.translatable(this.getDescriptionId(stack)).getString();
        String recipeName = Component.translatable(recipe.name).getString();

        return Component.literal(baseName + " " + recipeName);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        CrucibleRecipe recipe = getRecipe(stack);
        if (recipe == null) return;

        boolean shiftDown = GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;

        tooltip.add(Component.translatable("info.template_out_p").withStyle(ChatFormatting.BOLD));
        for (MaterialStack out : recipe.output) {
            tooltip.add(Component.literal(
                    Component.translatable(out.material.getUnlocalizedName()).getString() + ": " + Mats.formatAmount(out.amount, shiftDown)));
        }

        tooltip.add(Component.translatable("info.template_in_p").withStyle(ChatFormatting.BOLD));
        for (MaterialStack in : recipe.input) {
            tooltip.add(Component.literal(
                    Component.translatable(in.material.getUnlocalizedName()).getString() + ": " + Mats.formatAmount(in.amount, shiftDown)));
        }
    }

    public static CrucibleRecipe getRecipe(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("recipeIndex")) {
            int index = tag.getInt("recipeIndex");
            if (index >= 0 && index < CrucibleRecipes.recipes.size()) {
                return CrucibleRecipes.recipes.get(index);
            }
        }
        return null;
    }

    public static ItemStack forRecipe(int index) {
        ItemStack stack = new ItemStack(ModItems.CRUCIBLE_TEMPLATE.get());
        stack.getOrCreateTag().putInt("recipeIndex", index);
        return stack;
    }
}