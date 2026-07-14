package com.hbm.items.tool;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.items.armor.ArmorFSB;
import com.hbm.items.armor.ArmorFSBPowered;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemFusionCore extends Item {

    private final int charge;

    public ItemFusionCore(Properties properties, int charge) {
        super(properties);
        this.charge = charge;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (ArmorFSB.hasFSBArmorIgnoreCharge(player) &&
                player.getInventory().armor.get(2).getItem() instanceof ArmorFSBPowered) {

            for (ItemStack armorStack : player.getInventory().armor) {
                if (armorStack.isEmpty()) continue;

                if (armorStack.getItem() instanceof IBatteryItem battery) {
                    long maxCharge = battery.getMaxCharge(armorStack);
                    long currentCharge = battery.getCharge(armorStack);
                    long newCharge = Math.min(currentCharge + this.charge, maxCharge);

                    battery.setCharge(armorStack, newCharge);
                }
            }

            stack.shrink(1);

            if (!level.isClientSide) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ARMOR_EQUIP_LEATHER,
                        net.minecraft.sounds.SoundSource.PLAYERS,
                        1.0F, 1.0F);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("item.fusion_core.desc",
                        BobMathUtil.getShortNumber(this.charge))
                .withStyle(ChatFormatting.YELLOW));
        list.add(Component.translatable("item.fusion_core.desc2")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, list, flag);
    }

    public int getCharge() {
        return this.charge;
    }
}