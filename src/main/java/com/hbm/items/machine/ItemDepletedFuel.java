package com.hbm.items.machine;

import com.hbm.items.special.ItemNuclearWaste;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDepletedFuel extends ItemNuclearWaste {

    public ItemDepletedFuel(Properties properties) {
        super(properties);
    }

    public ItemDepletedFuel() {
        super(new Properties().stacksTo(64));
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        // Если есть повреждение (meta) > 0 - оранжевый оттенок
        return stack.getDamageValue() > 0 ? 0xFFBFA5 : 0xFFFFFF;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if (stack.getDamageValue() > 0) {
            list.add(Component.translatable("desc.item.wasteCooling").withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(stack, level, list, flag);
    }
}