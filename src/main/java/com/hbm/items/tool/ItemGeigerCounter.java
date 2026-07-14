package com.hbm.items.tool;

import com.hbm.extprop.HbmLivingProps;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.sound.ModSounds;
import com.hbm.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemGeigerCounter extends Item {

    private final Random rand = new Random();

    public ItemGeigerCounter(Properties properties) {
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

                if (rad < 1) list.add(0);
                if (rad < 5) list.add(0);
                if (rad < 10) list.add(1);
                if (rad > 5 && rad < 15) list.add(2);
                if (rad > 10 && rad < 20) list.add(3);
                if (rad > 15 && rad < 25) list.add(4);
                if (rad > 20 && rad < 30) list.add(5);
                if (rad > 25) list.add(6);

                int r = list.get(rand.nextInt(list.size()));

                if (r > 0) {
                    SoundEvent sound = switch (r) {
                        case 1 -> ModSounds.GEIGER_1.get();
                        case 2 -> ModSounds.GEIGER_2.get();
                        case 3 -> ModSounds.GEIGER_3.get();
                        case 4 -> ModSounds.GEIGER_4.get();
                        case 5 -> ModSounds.GEIGER_5.get();
                        default -> ModSounds.GEIGER_6.get();
                    };
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            sound, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            } else if (rand.nextInt(50) == 0) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        ModSounds.GEIGER_1.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    public static void setFloat(ItemStack stack, float value, String name) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(name, value);
    }

    public static float getFloat(ItemStack stack, String name) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(name)) {
            return tag.getFloat(name);
        }
        return 0;
    }

    public static int check(Level level, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return (int) Math.ceil(RadiationEvents.getRadiation(level, pos));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BOOP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            ContaminationUtil.printGeigerData(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
    }
}