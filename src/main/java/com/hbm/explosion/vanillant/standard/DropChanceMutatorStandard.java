package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IDropChanceMutator;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DropChanceMutatorStandard implements IDropChanceMutator {

    protected final float chance;

    public DropChanceMutatorStandard(float chance)  {
        this.chance = chance;
    }

    @Override
    public float mutateDropChance(ExplosionVNT explosion, Block block, int x, int y, int z, float chance) {
        return this.chance;
    }

    @Override
    public float mutateDropChance(ExplosionVNT explosion, BlockState state, int x, int y, int z, float chance) {
        return this.chance;
    }

    // Статические фабричные методы для удобства

    public static DropChanceMutatorStandard of(float chance) {
        return new DropChanceMutatorStandard(chance);
    }

    public static DropChanceMutatorStandard noDrop() {
        return new DropChanceMutatorStandard(0.0f);
    }

    public static DropChanceMutatorStandard allDrop() {
        return new DropChanceMutatorStandard(1.0f);
    }

    public static DropChanceMutatorStandard halfDrop() {
        return new DropChanceMutatorStandard(0.5f);
    }

    public static DropChanceMutatorStandard quarterDrop() {
        return new DropChanceMutatorStandard(0.25f);
    }

    // Методы для настройки на основе характеристик взрыва

    public static DropChanceMutatorStandard sizeBased(float baseChance) {
        return new DropChanceMutatorStandard(baseChance) {
            @Override
            public float mutateDropChance(ExplosionVNT explosion, BlockState state, int x, int y, int z, float chance) {
                // Уменьшаем шанс для больших взрывов
                float sizeFactor = Math.max(0.1f, 1.0f - explosion.size * 0.05f);
                return this.chance * sizeFactor;
            }
        };
    }

    public static DropChanceMutatorStandard distanceBased(float baseChance) {
        return new DropChanceMutatorStandard(baseChance) {
            @Override
            public float mutateDropChance(ExplosionVNT explosion, BlockState state, int x, int y, int z, float chance) {
                // Уменьшаем шанс для дальних блоков
                double distance = Math.sqrt(
                        Math.pow(x - explosion.posX, 2) +
                                Math.pow(y - explosion.posY, 2) +
                                Math.pow(z - explosion.posZ, 2)
                );
                float distanceFactor = (float) Math.max(0.0, 1.0 - distance / explosion.size);
                return this.chance * distanceFactor;
            }
        };
    }

    public static DropChanceMutatorStandard blockTypeBased() {
        return new DropChanceMutatorStandard(1.0f) {
            @Override
            public float mutateDropChance(ExplosionVNT explosion, BlockState state, int x, int y, int z, float chance) {
                // Разный шанс для разных типов блоков
                Block block = state.getBlock();

                // Руды имеют высокий шанс выпадения
                if(block.defaultBlockState().requiresCorrectToolForDrops()) {
                    return 0.8f;
                }

                // Дерево и подобные материалы
                if(state.getDestroySpeed(explosion.world, new net.minecraft.core.BlockPos(x, y, z)) < 2.0f) {
                    return 0.6f;
                }

                // Камень и твердые материалы
                return 0.3f;
            }
        };
    }
}