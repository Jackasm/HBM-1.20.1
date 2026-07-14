package com.hbm.items.tool;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.sound.ModSounds;
import com.hbm.util.ContaminationUtil;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemDosimeter extends Item {

    private final Random rand = new Random();

    public ItemDosimeter(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                              int slot, boolean selected) {
        if (!(entity instanceof LivingEntity living) || level.isClientSide) return;

        float rad = HbmLivingProps.getRadBuf(living);

        if (level.getGameTime() % 5 == 0) {
            if (rad > 1E-5) {
                List<Integer> list = new ArrayList<>();

                if (rad < 0.5) list.add(0);
                if (rad < 1) list.add(1);
                if (rad >= 0.5 && rad < 2) list.add(2);
                if (rad >= 1 && rad >= 2) list.add(3);

                int r = list.get(rand.nextInt(list.size()));

                if (r > 0) {
                    // Используем звуки через ModSounds
                    var sound = switch (r) {
                        case 1 -> ModSounds.GEIGER_1.get();
                        case 2 -> ModSounds.GEIGER_2.get();
                        case 3 -> ModSounds.GEIGER_3.get();
                        default -> null;
                    };
                    if (sound != null) {
                        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                                sound, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }

            } else if (rand.nextInt(100) == 0) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        ModSounds.GEIGER_1.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BOOP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            ContaminationUtil.printDosimeterData(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}