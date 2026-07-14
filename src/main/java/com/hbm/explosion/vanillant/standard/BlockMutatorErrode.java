package com.hbm.explosion.vanillant.standard;

import java.util.HashMap;
import java.util.Map;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMutatorErrode implements IBlockMutator {

    private final Map<Block, Block> erosionMap;
    private final float chance;

    public BlockMutatorErrode() {
        this(0.6F); // 60% шанс как в оригинале
    }

    public BlockMutatorErrode(float chance) {
        this.erosionMap = createErosionMap();
        this.chance = chance;
    }

    public BlockMutatorErrode(Map<Block, Block> erosionMap, float chance) {
        this.erosionMap = erosionMap;
        this.chance = chance;
    }

    private Map<Block, Block> createErosionMap() {
        Map<Block, Block> map = new HashMap<>();

        // Бетон → гравий
        map.put(ModBlocks.CONCRETE.get(), Blocks.GRAVEL);
        map.put(ModBlocks.CONCRETE_SMOOTH.get(), Blocks.GRAVEL);

        // Бетонный кирпич → битый бетонный кирпич → гравий
        map.put(ModBlocks.BRICK_CONCRETE.get(), ModBlocks.BRICK_CONCRETE_BROKEN.get());
        map.put(ModBlocks.BRICK_CONCRETE_BROKEN.get(), Blocks.GRAVEL);

        return map;
    }

    @Override
    public void mutatePre(ExplosionVNT explosion, Block block, int meta, BlockPos pos) {
        Level world = explosion.world;

        // Проверяем, есть ли блок в карте эрозии и сработал ли шанс
        if (erosionMap.containsKey(block) && world.random.nextFloat() < chance) {
            Block replacement = erosionMap.get(block);
            world.setBlock(pos, replacement.defaultBlockState(), 3);
        }
    }

    @Override
    public void mutatePost(ExplosionVNT explosion, BlockPos pos) {
        // Ничего не делаем после взрыва
    }

    // Фабричный метод
    public static BlockMutatorErrode standard() {
        return new BlockMutatorErrode();
    }

    public static BlockMutatorErrode withChance(float chance) {
        return new BlockMutatorErrode(chance);
    }

    public static BlockMutatorErrode custom(Map<Block, Block> map, float chance) {
        return new BlockMutatorErrode(map, chance);
    }
}