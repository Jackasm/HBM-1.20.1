package com.hbm.items.special;

import com.hbm.items.machine.ItemBattery;
import com.hbm.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemPotatos extends ItemBattery {

    public ItemPotatos(Properties properties, long maxCharge, long chargeRate, long dischargeRate) {
        super(properties, maxCharge, chargeRate, dischargeRate);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (getCharge(stack) == 0) return;

        if (getTimer(stack) > 0) {
            setTimer(stack, getTimer(stack) - 1);
        } else {
            if (entity instanceof Player player) {
                if (player.getMainHandItem() == stack) {
                    float pitch = (float) getCharge(stack) / (float) this.getMaxCharge(stack) * 0.5F + 0.5F;

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.POTATOES_RANDOM.get(), SoundSource.PLAYERS,
                            1.0F, pitch);

                    setTimer(stack, 200 + level.random.nextInt(100));
                }
            }
        }
    }

    private static int getTimer(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt("timer");
    }

    private static void setTimer(ItemStack stack, int timer) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("timer", timer);
    }
}