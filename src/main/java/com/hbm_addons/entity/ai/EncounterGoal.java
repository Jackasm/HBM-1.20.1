package com.hbm_addons.entity.ai;

public enum EncounterGoal {
    TRADER,     // Friendly: идёт к игроку, ждёт, уходит
    TRAVELER,   // Friendly: транзит через зону видимости
    DESERTER,   // Friendly: бродит, агрессивен при приближении
    SCOUT,      // Hostile: подходит на 32, стоит, уходит
    RAIDER,     // Hostile: атакует до смерти/потери цели, detect 48
    AMBUSHER    // Hostile: сидит в укрытии, detect 16
}
