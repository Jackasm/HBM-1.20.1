package com.hbm.quests;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface CustomQuestCondition {
    boolean test(Player player);
}