package com.hbm.pawn;

public enum ConsumerType {
    FURNACE,    // обычная печь, плавильня
    SMELTER,    // продвинутая плавильня (например, электропечь)
    CRAFTER,    // верстак, автоматический крафтер
    PLANNER,    // блок-планировщик (ваш будущий аналог AE2)
    MACHINE,    // любая машина с инвентарём
    GENERIC    // всё остальное
}