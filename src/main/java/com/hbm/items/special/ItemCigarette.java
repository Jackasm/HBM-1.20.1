package com.hbm.items.special;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.advancements.No9Trigger;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.ModArmorItems;
import com.hbm.network.PacketDispatcher;

import com.hbm.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCigarette extends Item {

    private final boolean isCrackpipe;

    public ItemCigarette(Properties properties, boolean isCrackpipe) {
        super(properties);
        this.isCrackpipe = isCrackpipe;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 30;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity living) {
        if (living instanceof Player player) {
            if (!level.isClientSide) {
                if (!isCrackpipe) {
                    HbmLivingProps.incrementBlackLung(player, 2000);
                    HbmLivingProps.incrementAsbestos(player, 2000);
                    HbmLivingProps.incrementRadiation(player, 100F);

                    ItemStack helmet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
                    if (helmet != null && helmet.getItem() == ModArmorItems.NO9.get()) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            No9Trigger trigger = ModCriteriaTriggers.NO9;
                            trigger.trigger(serverPlayer);
                        }
                    }
                } else {
                    HbmLivingProps.incrementBlackLung(player, 500);
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                    player.heal(10F);
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.PLAYER_COUGH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "vomit");
                nbt.putString("mode", "smoke");
                nbt.putInt("count", 30);
                nbt.putInt("entity", player.getId());
                PacketDispatcher.sendAuxParticleNT(nbt, player.getX(), player.getY(), player.getZ(), player);
            }

            stack.shrink(1);
        }
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!isCrackpipe) {
            tooltip.add(Component.literal(ChatFormatting.RED + "✓ Asbestos filter"));
            tooltip.add(Component.literal(ChatFormatting.RED + "✓ High in tar"));
            tooltip.add(Component.literal(ChatFormatting.RED + "✓ Tobacco contains 100% Polonium-210"));
            tooltip.add(Component.literal(ChatFormatting.RED + "✓ Yum"));
        } else {
            String[] colors = new String[]{
                    ChatFormatting.RED + "",
                    ChatFormatting.GOLD + "",
                    ChatFormatting.YELLOW + "",
                    ChatFormatting.GREEN + "",
                    ChatFormatting.AQUA + "",
                    ChatFormatting.BLUE + "",
                    ChatFormatting.DARK_PURPLE + "",
                    ChatFormatting.LIGHT_PURPLE + "",
            };
            int len = 2000;
            int index = (int) (System.currentTimeMillis() % len * colors.length / len);
            tooltip.add(Component.literal("This can't be good for me, but I feel " + colors[index] + "GREAT"));
        }
    }
}