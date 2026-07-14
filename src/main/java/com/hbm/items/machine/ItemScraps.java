package com.hbm.items.machine;

import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.material.NTMMaterial.SmeltingBehavior;
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

public class ItemScraps extends Item {

    public ItemScraps(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (isLiquid(stack)) {
            MaterialStack contents = getMats(stack);
            if (contents != null) {
                return Component.translatable(contents.material.getUnlocalizedName());
            }
        }
        MaterialStack contents = getMats(stack);
        if (contents != null) {
            return Component.translatable(contents.material.getUnlocalizedName());
        }
        return super.getName(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MaterialStack contents = getMats(stack);
        if (contents == null) return;

        boolean shiftDown = GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;

        if (isLiquid(stack)) {
            tooltip.add(Component.literal(Mats.formatAmount(contents.amount, shiftDown)));
            if (contents.material.smeltable == SmeltingBehavior.ADDITIVE) {
                tooltip.add(Component.literal("Additive, not castable!").withStyle(ChatFormatting.DARK_RED));
            }
        } else {
            tooltip.add(Component.literal(
                    Component.translatable(contents.material.getUnlocalizedName()).getString() + ", " + Mats.formatAmount(contents.amount, shiftDown)));
        }
    }

    public static boolean isLiquid(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean("liquid");
    }

    public static int getScrapColor(ItemStack stack) {
        MaterialStack contents = getMats(stack);
        if (contents != null && isLiquid(stack)) {
            return contents.material.moltenColor;
        }
        return 0xFFFFFF;
    }

    public static MaterialStack getMats(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemScraps)) return null;
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("matID")) return null;
        NTMMaterial mat = Mats.matById.get(tag.getInt("matID"));
        if (mat == null) return null;
        int amount = tag.getInt("amount");
        if (amount <= 0) amount = MaterialShapes.INGOT.q(1);
        return new MaterialStack(mat, amount);
    }

    public static ItemStack create(MaterialStack stack) {
        return create(stack, false);
    }

    public static ItemStack create(MaterialStack stack, boolean liquid) {
        if (stack.material == null) return new ItemStack(ModItems.NOTHING.get());
        ItemStack scrap = new ItemStack(ModItems.SCRAPS.get(), 1);
        CompoundTag tag = scrap.getOrCreateTag();
        tag.putInt("matID", stack.material.id);
        tag.putInt("amount", stack.amount);
        if (liquid) tag.putBoolean("liquid", true);
        return scrap;
    }
}