package com.hbm.items.weapon;

import com.hbm.config.WeaponConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeaponizedCell extends Item {

    private static final int DETONATION_TIME = 50 * 20; // 50 секунд

    public WeaponizedCell(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();

        if (entity.tickCount > DETONATION_TIME || entity.isOnFire()) {
            if (!level.isClientSide) {
                if (WeaponConfig.dropStar.get()) {
                    double x = entity.getX();
                    double y = entity.getY();
                    double z = entity.getZ();

                    if (level instanceof ServerLevel serverLevel) {
                        EntityNukeExplosionMK3 ex = EntityNukeExplosionMK3.statFacFleija(serverLevel, x, y, z, 100);
                        if (!ex.isRemoved()) {
                            level.playSound(null, BlockPos.containing(x, y, z),
                                    net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                                    net.minecraft.sounds.SoundSource.BLOCKS,
                                    100.0F, level.random.nextFloat() * 0.1F + 0.9F);
                            level.addFreshEntity(ex);

                            EntityCloudFleijaRainbow cloud = new EntityCloudFleijaRainbow(ModEntities.CLOUD_FLEIJA.get(), level, 100);
                            cloud.setPos(x, y, z);
                            level.addFreshEntity(cloud);
                        }
                    }
                }
                entity.discard();
            }
            return false;
        }

        int randy = DETONATION_TIME - entity.tickCount;
        if (randy < 1) randy = 1;

        // Частицы для эффекта
        if (level.random.nextInt(DETONATION_TIME) >= randy) {
            level.addParticle(ParticleTypes.SMOKE,
                    entity.getX() + level.random.nextGaussian() * entity.getBbWidth() / 2,
                    entity.getY() + level.random.nextGaussian() * entity.getBbHeight(),
                    entity.getZ() + level.random.nextGaussian() * entity.getBbWidth() / 2,
                    0.0, 0.0, 0.0);
        } else {
            level.addParticle(ParticleTypes.CLOUD,
                    entity.getX() + level.random.nextGaussian() * entity.getBbWidth() / 2,
                    entity.getY() + level.random.nextGaussian() * entity.getBbHeight(),
                    entity.getZ() + level.random.nextGaussian() * entity.getBbWidth() / 2,
                    0.0, 0.0, 0.0);
        }

        if (randy < 100) {
            level.addParticle(ParticleTypes.LAVA,
                    entity.getX() + level.random.nextGaussian() * entity.getBbWidth() / 2,
                    entity.getY() + level.random.nextGaussian() * entity.getBbHeight(),
                    entity.getZ() + level.random.nextGaussian() * entity.getBbWidth() / 2,
                    0.0, 0.0, 0.0);
        }

        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("A charged energy cell, rigged to explode")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("when left on the floor for too long.")
                .withStyle(ChatFormatting.GRAY));
    }
}