package com.hbm.items.tool;

import com.hbm.sound.ModSounds;
import com.hbm.util.ContaminationUtil;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemDigammaDiagnostic extends Item {

    public ItemDigammaDiagnostic(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BOOP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            ContaminationUtil.printDiagnosticData(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}