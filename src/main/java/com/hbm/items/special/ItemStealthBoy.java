package com.hbm.items.special;

import com.hbm.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemStealthBoy extends Item {

    public ItemStealthBoy(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30 * 20, 1, false, true));

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.UNPACK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            stack.shrink(1);
        }

        return InteractionResultHolder.success(stack);
    }
}