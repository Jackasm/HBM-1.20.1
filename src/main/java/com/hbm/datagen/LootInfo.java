package com.hbm.datagen;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class LootInfo {
    private final LootType type;
    private final Supplier<Item> dropItem;
    private final int minCount;
    private final int maxCount;
    private final Block selfDropBlock; // если дропается не сам блок, а другой

    // Типы лута
    public enum LootType {
        SELF,          // дропает сам блок (dropSelf)
        ITEM,          // дропает предмет (с возможным разбросом)
        ITEM_SILK,     // дропает сам блок только с шёлком, иначе предмет
        OTHER_BLOCK,   // дропает другой блок (например, руда → блок руды с шёлком)
        CUSTOM         // кастомная таблица (будет задана отдельно)
    }

    private LootInfo(LootType type, Supplier<Item> dropItem, int minCount, int maxCount, Block selfDropBlock) {
        this.type = type;
        this.dropItem = dropItem;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.selfDropBlock = selfDropBlock;
    }

    public static LootInfo self() {
        return new LootInfo(LootType.SELF, null, 0, 0, null);
    }

    public static LootInfo item(Supplier<Item> item, int min, int max) {
        return new LootInfo(LootType.ITEM, item, min, max, null);
    }

    public static LootInfo itemSilk(Supplier<Item> item, int min, int max) {
        return new LootInfo(LootType.ITEM_SILK, item, min, max, null);
    }

    public static LootInfo otherBlock(Block block) {
        return new LootInfo(LootType.OTHER_BLOCK, null, 0, 0, block);
    }

    public LootType getType() { return type; }
    public Supplier<Item> getDropItem() { return dropItem; }
    public int getMinCount() { return minCount; }
    public int getMaxCount() { return maxCount; }
    public Block getSelfDropBlock() { return selfDropBlock; }
}