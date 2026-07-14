// EnchantmentUtil.java (адаптированный)
package com.hbm.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class EnchantmentUtil {

    public static void addEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(enchantment, level);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    public static void removeEnchantment(ItemStack stack, Enchantment enchantment) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.remove(enchantment);
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0;
    }

    /**
     * Identical to Player.addExperience but without increasing the player's score
     * @param player the player to add experience to
     * @param xp amount of experience to add
     * @param silent whether to add experience silently (without level-up sound)
     */
    public static void addExperience(Player player, int xp, boolean silent) {
        int maxXp = Integer.MAX_VALUE - player.totalExperience;

        if (xp > maxXp) {
            xp = maxXp;
        }

        float cap = player.getXpNeededForNextLevel();
        player.experienceProgress += (float) xp / cap;

        player.totalExperience += xp;

        while (player.experienceProgress >= 1.0F) {
            player.experienceProgress = (player.experienceProgress - 1.0F) * cap;

            if (silent) {
                addExperienceLevelSilent(player, 1);
            } else {
                player.giveExperienceLevels(1);
            }

            cap = player.getXpNeededForNextLevel();
        }
    }

    /**
     * Adds experience levels silently (without level-up sound)
     */
    public static void addExperienceLevelSilent(Player player, int levels) {
        int currentLevel = player.experienceLevel;
        player.experienceLevel += levels;

        // Сброс прогресса при повышении уровня
        if (player.experienceLevel > currentLevel) {
            player.experienceProgress = 0.0F;
        }

        // Обновление опыта для синхронизации
        player.onUpdateAbilities();
    }
}