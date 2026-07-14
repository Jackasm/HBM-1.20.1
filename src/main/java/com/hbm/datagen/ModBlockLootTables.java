package com.hbm.datagen;

import com.hbm.blocks.ModBlocks;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        for (Map.Entry<RegistryObject<Block>, LootInfo> entry : ModBlocks.BLOCK_LOOT.entrySet()) {
            Block block = entry.getKey().get();
            LootInfo info = entry.getValue();

            switch (info.getType()) {
                case SELF -> dropSelf(block);
                case ITEM -> add(block, createItemDrops(info.getDropItem(), info.getMinCount(), info.getMaxCount()));
                case ITEM_SILK -> add(block, createSilkTouchItemDrops(block, info.getDropItem(), info.getMinCount(), info.getMaxCount()));
                case OTHER_BLOCK -> add(block, createOtherBlockDrops(info.getSelfDropBlock()));
                case CUSTOM -> {}
            }
        }

    }

    protected LootTable.Builder createItemDrops(Supplier<Item> itemSupplier, int min, int max) {
        Item item = itemSupplier.get();
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                .when(hasNoSilkTouch())));
    }

    protected LootTable.Builder createSilkTouchItemDrops(Block block, Supplier<Item> itemSupplier, int min, int max) {
        Item item = itemSupplier.get();
        return LootTable.lootTable()
                // Пул для шёлкового касания (дропает блок)
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(block)
                                .apply(ApplyExplosionDecay.explosionDecay())
                                .when(hasSilkTouch())))
                // Пул для обычного дропа (предмет)
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                .apply(ApplyExplosionDecay.explosionDecay())
                                .when(hasNoSilkTouch())));
    }

    protected LootTable.Builder createOtherBlockDrops(Block dropBlock) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(dropBlock)
                                .apply(ApplyExplosionDecay.explosionDecay())));
    }

    // Условие: нет шелкового касания
    protected LootItemCondition.Builder hasNoSilkTouch() {
        return MatchTool.toolMatches(ItemPredicate.Builder.item()
                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)))).invert();
    }

    protected LootItemCondition.Builder hasSilkTouch() {
        return MatchTool.toolMatches(ItemPredicate.Builder.item()
                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }

}