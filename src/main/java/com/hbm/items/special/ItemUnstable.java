package com.hbm.items.special;

import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.sound.ModSounds;
import com.hbm.util.ModDamageSource;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemUnstable extends Item {

    private final int radius;
    private final int timer;

    public ItemUnstable(Properties properties, int radius, int timer) {
        super(properties);
        this.radius = radius;
        this.timer = timer;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.getDamageValue() != 0) return;

        int decay = getTimer(stack) * 100 / timer;
        tooltip.add(Component.literal("Decay: " + decay + "%").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (stack.getDamageValue() != 0) return;

        setTimer(stack, getTimer(stack) + 1);

        if (getTimer(stack) == timer && !level.isClientSide) {
            EntityNukeExplosionMK5.statFac(level, radius, entity.getX(), entity.getY(), entity.getZ());
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.OLD_EXPLOSION.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            entity.hurt(ModDamageSource.nuclearBlast(level), 10000);

            stack.shrink(1);
        }
    }

    private void setTimer(ItemStack stack, int time) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("timer", time);
    }

    private int getTimer(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getInt("timer");
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        String name = switch (stack.getDamageValue()) {
            case 1 -> "ELEMENTS";
            case 2 -> "ARSENIC";
            case 3 -> "VAULT";
            default -> super.getName(stack).getString();
        };
        return Component.literal(name);
    }
}