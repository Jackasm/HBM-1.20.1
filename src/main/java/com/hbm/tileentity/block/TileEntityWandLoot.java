package com.hbm.tileentity.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockWandLoot;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.LootGenerator;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.ContainerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileEntityWandLoot extends BlockEntity {

    private boolean triggerReplace = false;
    private Block replaceBlock = ModBlocks.DECO_LOOT.get();
    private int replaceMeta = 0;
    private String poolName = LootGenerator.LOOT_SHIT;
    private int minItems = 0;
    private int maxItems = 1;
    private float placedRotation = 0;

    public TileEntityWandLoot(BlockPos pos, BlockState state) {
        super(ModTileEntity.WAND_LOOT.get(), pos, state);
    }

    // ==================== Setters ====================
    public void setReplaceBlock(Block block) { this.replaceBlock = block; setChanged(); }
    public void setReplaceMeta(int meta) { this.replaceMeta = meta; setChanged(); }
    public void setPlacedRotation(float rotation) { this.placedRotation = rotation; setChanged(); }
    public void setPoolName(String name) { this.poolName = name; setChanged(); }
    public void setMinItems(int min) { this.minItems = min; setChanged(); }
    public void setMaxItems(int max) { this.maxItems = max; setChanged(); }
    public void setTriggerReplace(boolean value) { this.triggerReplace = value; setChanged(); }

    // ==================== Getters ====================
    public Block getReplaceBlock() { return replaceBlock; }
    public int getReplaceMeta() { return replaceMeta; }
    public String getPoolName() { return poolName; }
    public int getMinItems() { return minItems; }
    public int getMaxItems() { return maxItems; }

    // ==================== Tick ====================
    public void tick() {
        if (level == null || level.isClientSide) return;
        if (!triggerReplace) return;

        triggerReplace = false;
        replace();
    }

    // ==================== Replace ====================
    private void replace() {
        if (level == null) return;

        BlockState current = level.getBlockState(worldPosition);
        if (!(current.getBlock() instanceof BlockWandLoot)) {
            System.err.println("[HBM] TileEntityWandLoot at " + worldPosition + " but block isn't BlockWandLoot, skipping");
            return;
        }

        // Определяем финальный блок с учётом meta
        Block finalBlock = resolveFinalBlock();
        BlockState newState = finalBlock.defaultBlockState();

        // Копируем FACING из wand_loot
        if (current.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                && newState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING,
                    current.getValue(BlockStateProperties.HORIZONTAL_FACING));
        } else if (current.hasProperty(BlockStateProperties.FACING)
                && newState.hasProperty(BlockStateProperties.FACING)) {
            newState = newState.setValue(BlockStateProperties.FACING,
                    current.getValue(BlockStateProperties.FACING));
        }

        // Ставим блок
        level.setBlock(worldPosition, newState, 3);

        // Заполняем контейнер
        if (finalBlock == ModBlocks.DECO_LOOT.get()) {
            LootGenerator.applyLoot(level, worldPosition, poolName);
        } else {
            ContainerHelper.fill(level, worldPosition, poolName, minItems, maxItems, level.random);
        }

        setChanged();
    }

    private Block resolveFinalBlock() {
        // Специальные случаи с meta-подтипами
        if (replaceBlock == ModBlocks.FILING_CABINET.get()) {
            if (replaceMeta == 1) return ModBlocks.FILING_CABINET_STEEL.get();
            return ModBlocks.FILING_CABINET.get();
        }
        // Остальные блоки — как есть
        return replaceBlock;
    }

    public List<String> getPoolNames(boolean isLoot) {
        if (isLoot) {
            return LootGenerator.getLootNames();
        }
        return new ArrayList<>(ItemPool.pools.keySet());
    }

    // ==================== NBT ====================
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(replaceBlock);
        tag.putString("block", blockId != null ? blockId.toString() : "minecraft:air");
        tag.putInt("meta", replaceMeta);
        tag.putInt("min", minItems);
        tag.putInt("max", maxItems);
        tag.putString("pool", poolName);
        tag.putFloat("rot", placedRotation);
        tag.putBoolean("trigger", triggerReplace);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        String blockName = tag.getString("block");
        Block loaded = blockName.isEmpty() ? null : ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
        replaceBlock = loaded != null ? loaded : ModBlocks.DECO_LOOT.get();

        replaceMeta = tag.getInt("meta");
        minItems = tag.getInt("min");
        maxItems = tag.getInt("max");
        poolName = tag.getString("pool");
        if (poolName.isEmpty()) poolName = LootGenerator.LOOT_SHIT;
        placedRotation = tag.getFloat("rot");
        triggerReplace = tag.getBoolean("trigger");
    }
}