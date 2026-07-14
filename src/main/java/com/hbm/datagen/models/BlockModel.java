package com.hbm.datagen.models;

public enum BlockModel {
    CUBE_ALL,             // обычный куб с одной текстурой
    CUBE_ALL_TRANSLUCENT,
    CUBE_ALL_SWITCH,
    BLAST_FURNACE,
    ORE,                  // руда
    CUBE_TOP_BOTTOM,      // верх, низ и сторона
    CROSS,                // крест для растений/крестовых моделей
    LAYERED,              // блок со слоями (например, foam_layer, sand_boron_layer)
    DECO_CT,
    AIR,
    MULTIBLOCK,
    MULTIBLOCK_NO_ITEM,
    ENTITYBLOCK_ANIMATED,  // блок с BlockEntityRenderer (кастомный рендер)
    MODEL_ITEM,
    MODEL_ITEM_STATIC,
    BEDROCK_ORE,          // специальная модель для BedrockOre с двумя слоями (основа + оверлей)
    GRATE,                // блоки со слоями (layer)
    OBJ_MODEL_FULL,       // OBJ с поддержкой поворота по всем направлениям
    OBJ_MODEL_HORIZONTAL, // OBJ с поддержкой поворота только по горизонтали
    STATIC_OBJ,           // OBJ без поворотов
    DOOR,                 // дверь
    PILLAR,               // колонна (cube_column)
    ENUM_BLOCK,           // блок с перечисляемыми типами (concrete_colored_ext)
    ENUM_OBJ,  // для блоков с enum типами и OBJ моделями
    ENUM_PILLAR,          // колонна с перечисляемыми типами
    CONCRETE_SUPER,       // специальный блок с повреждениями
    PLANT_ENUM,
    TALL_PLANT_ENUM,
    CUBE_FRONT_SIDE,
    NONE                  // без генерации (по умолчанию)
}