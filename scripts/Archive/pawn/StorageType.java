package com.hbm.pawn;

public enum StorageType {
    CHEST,      // обычный сундук
    BARREL,     // бочка
    TANK,       // цистерна (жидкости)
    CRATE,      // ящик
    CABINET,    // шкаф
    DRAWER,     // ящик комода
    OTHER;      // любой другой склад

    public boolean canStoreItems() {
        return this != TANK; // танки только для жидкостей, остальные для предметов
    }

    public boolean canStoreFluids() {
        return this == TANK;
    }
}