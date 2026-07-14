package com.hbm.items.armor;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.ArmorModHandler;
import com.hbm.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModSensor extends ItemArmorMod {

    public ItemModSensor(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Beeps near hazardous gasses"));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Works in the inventory or when applied to armor"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity living) {
            modUpdate(living, null);
        }
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide) return;
        if (entity.tickCount % 20 != 0) return;

        Level level = entity.level();
        BlockPos pos = entity.blockPosition();

        int x = pos.getX();
        int y = pos.getY() + (int) Math.ceil(entity.getEyeHeight() - 0.5);
        int z = pos.getZ();

        boolean poison = false;
        boolean explosive = false;

        for (int i = -3; i <= 3; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -3; k <= 3; k++) {
                    BlockPos checkPos = new BlockPos(
                            x + i * 2,
                            y + j * 2,
                            z + k * 2
                    );
                    Block b = level.getBlockState(checkPos).getBlock();

                    if (b == ModBlocks.GAS_ASBESTOS.get() ||
                            b == ModBlocks.GAS_COAL.get() ||
                            b == ModBlocks.GAS_RADON.get() ||
                            b == ModBlocks.GAS_MONOXIDE.get() ||
                            b == ModBlocks.GAS_RADON_DENSE.get() ||
                            b == ModBlocks.CHLORINE_GAS.get()) {
                        poison = true;
                    }
                    if (b == ModBlocks.GAS_FLAMMABLE.get() ||
                            b == ModBlocks.GAS_EXPLOSIVE.get()) {
                        explosive = true;
                    }
                }
            }
        }

        if (explosive) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.FOLLY_AQUIRED.get(), net.minecraft.sounds.SoundSource.PLAYERS, 0.5F, 1.0F);
        } else if (poison) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.TECH_BOOP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 1.5F);
        }
    }
}