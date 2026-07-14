package com.hbm.datagen.models;

public enum ItemModelType {
    NONE,
    GENERATED,      // parent: item/generated
    GENERATED_TINY, // parent: item/generated с scale 0.35
    GENERATED_LAYERED,
    OBJ_ITEM,        // для предметов с OBJ моделью (loader: hbm:hfr_obj)
    ENUM_ITEM,      // для ItemEnumMulti с overrides
    ENUM_ITEM_TINTED,      // для одноцветного окрашивания
    BEDROCK_ORE_ITEM,  // специальная модель для бедроковой руды (куб с оверлеем)
    HANDHELD,       // parent: item/handheld
    HANDHELD_ROD,   // parent: item/handheld_rod
    SPAWN_EGG,       // parent: item/template_spawn_egg
    BUILTIN_ENTITY
}