package com.hbm.render.anim;

import com.hbm.items.weapon.sedna.GunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.HashMap;
import java.util.Map;

public class HbmAnimations {

    // Массивы анимаций для каждого слота и каждой "дорожки"
    public static final Animation[][] hotbar = new Animation[9][8];

    // Кэш для трансформаций (чтобы не вычислять каждый кадр)
    private static final Map<String, double[]> transformationCache = new HashMap<>();
    private static long lastCacheUpdate = 0;

    public static class Animation {
        public String key;
        public long startMillis;
        public BusAnimation animation;
        public boolean holdLastFrame = false;

        public Animation(String key, long startMillis, BusAnimation animation) {
            this.key = key;
            this.startMillis = startMillis;
            this.animation = animation;
        }

        public Animation(String key, long startMillis, BusAnimation animation, boolean holdLastFrame) {
            this(key, startMillis, animation);
            this.holdLastFrame = holdLastFrame;
        }
    }

    // Получение актуальной анимации
    public static Animation getRelevantAnim(int index) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return null;

        // Определяем, в какой руке предмет
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean isMainHand = mainHand.getItem() instanceof GunItem;
        boolean isOffHand = offHand.getItem() instanceof GunItem;

        if (!isMainHand && !isOffHand) return null;

        // Определяем слот (для совместимости со старой системой)
        int slot = player.getInventory().selected;
        if (slot < 0 || slot > 8) {
            slot = Math.abs(slot) % 9;
        }

        if (hotbar[slot][index] == null) return null;

        // Проверяем, соответствует ли анимация текущему предмету
        Animation anim = hotbar[slot][index];
        ItemStack currentStack = isMainHand ? mainHand : offHand;

        if (anim.key.equals(currentStack.getItem().getDescriptionId())) {
            return anim;
        }

        return null;
    }

    /**
     * Очищает все старые анимации которые уже закончились
     */
    public static void cleanupExpiredAnimations() {
        long currentTime = System.currentTimeMillis();

        for (int slot = 0; slot < 9; slot++) {
            for (int i = 0; i < 8; i++) {
                Animation anim = hotbar[slot][i];
                if (anim != null) {
                    long elapsed = currentTime - anim.startMillis;
                    // Очищаем если анимация закончилась больше 100мс назад
                    if (elapsed > anim.animation.getDuration() + 100) {
                        hotbar[slot][i] = null;
                        System.out.println("[ANIM] Cleaned up expired animation at slot " + slot + "," + i);
                    }
                }
            }
        }

        // Также очищаем кэш от устаревших записей
        transformationCache.entrySet().removeIf(entry -> {
            // Удаляем записи старше 1 секунды
            return currentTime - lastCacheUpdate > 1000;
        });
    }

    // Получение трансформации для конкретной "шины"
    public static double[] getRelevantTransformation(String bus, int index) {
        long currentTime = System.currentTimeMillis();

        // Получаем анимацию
        Animation anim = getRelevantAnim(index);
        if (anim == null) {
            return DEFAULT_TRANSFORM;
        }

        // Проверяем время анимации
        long elapsed = currentTime - anim.startMillis;
        int duration = anim.animation.getDuration();

        // Если анимация закончилась
        if (elapsed > duration) {
            if (!anim.holdLastFrame) {
                clearAnimation(anim, index); // Очищаем
                return DEFAULT_TRANSFORM;
            } else {
                // Используем последний кадр
                elapsed = duration;
            }
        }

        // Вычисляем трансформацию (без кэша для простоты)
        BusAnimationSequence seq = anim.animation.getBus(bus);
        if (seq != null) {
            return seq.getTransformation((int) elapsed);
        }

        return DEFAULT_TRANSFORM;
    }

    private static final double[] DEFAULT_TRANSFORM =
            new double[] {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 2};

    private static void clearAnimation(Animation anim, int index) {
        for (int slot = 0; slot < 9; slot++) {
            if (hotbar[slot][index] == anim) {
                hotbar[slot][index] = null;
            }
        }
        transformationCache.clear(); // Полная очистка кэша
    }

    // Применение трансформации к PoseStack
    public static void applyRelevantTransformation(PoseStack poseStack, String bus, int index) {
        double[] transform = getRelevantTransformation(bus, index);

        // Порядок вращения
        int[] rotOrder = new int[] { (int)transform[12], (int)transform[13], (int)transform[14] };

        // Применяем трансформации
        poseStack.translate(transform[0], transform[1], transform[2]);

        // Вращение в правильном порядке
        for (int axis : rotOrder) {
            switch (axis) {
                case 0: // X
                    poseStack.mulPose(Axis.XP.rotationDegrees((float)transform[3]));
                    break;
                case 1: // Y
                    poseStack.mulPose(Axis.YP.rotationDegrees((float)transform[4]));
                    break;
                case 2: // Z
                    poseStack.mulPose(Axis.ZP.rotationDegrees((float)transform[5]));
                    break;
            }
        }

        // Смещение центра
        poseStack.translate(-transform[9], -transform[10], -transform[11]);

        // Масштаб
        poseStack.scale((float)transform[6], (float)transform[7], (float)transform[8]);
    }

    // Упрощенная версия для 1-го индекса
    public static double[] getRelevantTransformation(String bus) {
        return getRelevantTransformation(bus, 0);
    }

    public static void applyRelevantTransformation(PoseStack poseStack, String bus) {
        applyRelevantTransformation(poseStack, bus, 0);
    }

    // Запуск анимации
    public static void startAnimation(ItemStack stack, BusAnimation animation, int index) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        int slot = player.getInventory().selected;
        if (slot < 0 || slot > 8) {
            slot = Math.abs(slot) % 9;
        }

        String key = stack.getItem().getDescriptionId();
        hotbar[slot][index] = new Animation(key, System.currentTimeMillis(), animation);

        // Очищаем кэш
        transformationCache.clear();
    }

    // Очистка анимаций для предмета
    public static void clearAnimations(ItemStack stack) {
        for (int slot = 0; slot < 9; slot++) {
            for (int i = 0; i < 8; i++) {
                if (hotbar[slot][i] != null &&
                        hotbar[slot][i].key.equals(stack.getItem().getDescriptionId())) {
                    hotbar[slot][i] = null;
                }
            }
        }
        transformationCache.clear();
    }

    // Проверка, играет ли анимация
    public static boolean isAnimationPlaying(ItemStack stack, int index) {
        Animation anim = getRelevantAnim(index);
        return anim != null && anim.key.equals(stack.getItem().getDescriptionId());
    }
}