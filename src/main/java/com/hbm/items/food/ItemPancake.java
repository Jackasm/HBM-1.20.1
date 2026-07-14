package com.hbm.items.food;

import com.hbm.items.ModArmorItems;
import com.hbm.items.armor.ArmorFSB;
import com.hbm.items.machine.ItemBattery;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPancake extends Item {

    public ItemPancake(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (ArmorFSB.hasFSBArmorIgnoreCharge(player) && player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModArmorItems.BJ_HELMET.get()) {
            return super.use(level, player, hand);
        }

        if (!level.isClientSide) {
            player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW + "Your teeth are too soft to eat this."));
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity living) {
        if (living instanceof Player player) {
            for (ItemStack armorStack : player.getInventory().armor) {
                if (armorStack.isEmpty()) continue;
                if (armorStack.getItem() instanceof ItemBattery battery) {
                    battery.setCharge(armorStack, battery.getMaxCharge(armorStack));
                }
            }
        }
        return super.finishUsingItem(stack, level, living);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Can be eaten to recharge lunar cybernetic armor"));
        tooltip.add(Component.literal("Not for people with weak molars"));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Half burnt and smells horrible"));
    }
}