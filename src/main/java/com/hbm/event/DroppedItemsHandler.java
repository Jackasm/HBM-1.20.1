package com.hbm.event;

import com.hbm.items.ModArmorItems;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DroppedItemsHandler {

    private static final Map<UUID, MaskInfo> activeMasks = new HashMap<>();
    private static final Map<UUID, Timer> maskTimers = new HashMap<>();

    private static class MaskInfo {
        public final ItemEntity itemEntity;
        public Timer transformationTimer;
        public boolean isTransforming;
        public boolean hasTransformed;
        public long waterContactTime;

        public MaskInfo(ItemEntity entity) {
            this.itemEntity = entity;
            this.isTransforming = false;
            this.hasTransformed = false;
            this.waterContactTime = 0;
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (!stack.is(ModArmorItems.MASK_DRY.get())) {
            return;
        }

        Level level = event.getEntity().level();
        if (!level.isClientSide()) {
            UUID maskId = event.getEntity().getUUID();

            cancelMaskTimer(maskId);

            MaskInfo maskInfo = new MaskInfo(event.getEntity());
            activeMasks.put(maskId, maskInfo);

            startWaterCheck(maskId);
        }
    }

    private static void startWaterCheck(UUID maskId) {
        Timer timer = new Timer();
        maskTimers.put(maskId, timer);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MaskInfo maskInfo = activeMasks.get(maskId);
                if (maskInfo == null || maskInfo.itemEntity == null ||
                        maskInfo.itemEntity.isRemoved() || !maskInfo.itemEntity.isAlive()) {
                    cleanupMask(maskId);
                    return;
                }

                Level level = maskInfo.itemEntity.level();
                if (level.isClientSide()) return;

                ItemStack currentStack = maskInfo.itemEntity.getItem();

                if (!currentStack.is(ModArmorItems.MASK_DRY.get())) {
                    cleanupMask(maskId);
                    return;
                }

                if (isTouchingWater(maskInfo.itemEntity, level)) {
                    if (maskInfo.waterContactTime == 0) {
                        maskInfo.waterContactTime = System.currentTimeMillis();

                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MaskInfo info = activeMasks.get(maskId);
                                if (info == null || info.hasTransformed || !info.itemEntity.isAlive()) {
                                    return;
                                }

                                if (isTouchingWater(info.itemEntity, level) &&
                                        info.itemEntity.getItem().is(ModArmorItems.MASK_DRY.get())) {
                                    startTransformation(maskId);
                                } else {
                                    info.waterContactTime = 0;
                                }
                            }
                        }, 1000);
                    }

                } else {
                    maskInfo.waterContactTime = 0;
                    maskInfo.isTransforming = false;
                }
            }
        }, 0, 500);
    }

    private static void startTransformation(UUID maskId) {
        MaskInfo maskInfo = activeMasks.get(maskId);
        if (maskInfo == null || maskInfo.hasTransformed || maskInfo.isTransforming) {
            return;
        }

        maskInfo.isTransforming = true;
        Level level = maskInfo.itemEntity.level();

        level.playSound(null, maskInfo.itemEntity.getX(), maskInfo.itemEntity.getY(), maskInfo.itemEntity.getZ(),
                SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS,
                0.7F, 0.8F + level.random.nextFloat() * 0.4F);

        spawnBubbles(maskInfo.itemEntity, level, 10);

        Timer transformTimer = new Timer();
        maskInfo.transformationTimer = transformTimer;

        final int[] ticks = {0};
        transformTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (maskInfo.itemEntity.isRemoved() ||
                        !maskInfo.itemEntity.isAlive() || maskInfo.hasTransformed) {
                    this.cancel();
                    return;
                }

                if (ticks[0] % 5 == 0) {
                    spawnBubbles(maskInfo.itemEntity, level, 3);
                }

                ticks[0]++;

                if (ticks[0] >= 40) {
                    completeTransformation(maskId);
                    this.cancel();
                }
            }
        }, 0, 50);
    }

    private static void completeTransformation(UUID maskId) {
        MaskInfo maskInfo = activeMasks.get(maskId);
        if (maskInfo == null || maskInfo.hasTransformed) {
            return;
        }

        maskInfo.hasTransformed = true;
        maskInfo.isTransforming = false;
        Level level = maskInfo.itemEntity.level();

        ItemStack wetMask = new ItemStack(ModArmorItems.MASK_WET.get());
        ItemStack currentStack = maskInfo.itemEntity.getItem();

        if (currentStack.hasTag()) {
            assert currentStack.getTag() != null;
            wetMask.setTag(currentStack.getTag().copy());
        }
        wetMask.setDamageValue(currentStack.getDamageValue());
        wetMask.getOrCreateTag().putInt("remainingTime", 6000);

        maskInfo.itemEntity.setItem(wetMask);
        maskInfo.itemEntity.setDefaultPickUpDelay();

        level.playSound(null, maskInfo.itemEntity.getX(), maskInfo.itemEntity.getY(), maskInfo.itemEntity.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);

        Timer cleanupTimer = new Timer();
        cleanupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanupMask(maskId);
            }
        }, 5000);
    }

    private static void cleanupMask(UUID maskId) {
        cancelMaskTimer(maskId);

        MaskInfo maskInfo = activeMasks.get(maskId);
        if (maskInfo != null && maskInfo.transformationTimer != null) {
            maskInfo.transformationTimer.cancel();
        }

        activeMasks.remove(maskId);
        maskTimers.remove(maskId);
    }

    private static void cancelMaskTimer(UUID maskId) {
        Timer timer = maskTimers.get(maskId);
        if (timer != null) {
            timer.cancel();
            maskTimers.remove(maskId);
        }
    }

    private static void spawnBubbles(ItemEntity maskEntity, Level level, int count) {
        if (level.isClientSide()) return;

        if (level instanceof ServerLevel serverLevel) {
            double x = maskEntity.getX();
            double y = maskEntity.getY();
            double z = maskEntity.getZ();

            serverLevel.sendParticles(
                    ParticleTypes.BUBBLE_COLUMN_UP,
                    x, y + 0.3, z,
                    count,
                    0.25, 0.1, 0.25,
                    0.1
            );
        }
    }

    private static boolean isTouchingWater(ItemEntity entity, Level level) {
        BlockPos pos = entity.blockPosition();
        return isWater(level.getBlockState(pos.below())) || isWater(level.getBlockState(pos));
    }

    private static boolean isWater(BlockState state) {
        return state.getFluidState().is(net.minecraft.tags.FluidTags.WATER);
    }
}