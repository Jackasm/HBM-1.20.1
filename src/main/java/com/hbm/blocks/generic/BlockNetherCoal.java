package com.hbm.blocks.generic;

import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class BlockNetherCoal extends BlockOutgas {

    public BlockNetherCoal(Properties properties,  int rate, boolean onBreak) {
        // Предполагаем, что конструктор BlockOutgas теперь принимает (Properties, int rate, boolean onBreak, boolean randomTicks)
        super(properties, rate, onBreak);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(ModItems.COAL_INFERNAL.get());
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        entity.setRemainingFireTicks(60); // 3 секунды огня
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        super.animateTick(state, level, pos, rand);

        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN) continue;

            BlockPos neighborPos = pos.relative(dir);
            if (level.getBlockState(neighborPos).isAir()) {

                double ix = pos.getX() + 0.5F + dir.getStepX() + rand.nextDouble() - 0.5D;
                double iy = pos.getY() + 0.5F + dir.getStepY() + rand.nextDouble() - 0.5D;
                double iz = pos.getZ() + 0.5F + dir.getStepZ() + rand.nextDouble() - 0.5D;

                if (dir.getStepX() != 0)
                    ix = pos.getX() + 0.5F + dir.getStepX() * 0.5 + rand.nextDouble() * 0.125 * dir.getStepX();
                if (dir.getStepY() != 0)
                    iy = pos.getY() + 0.5F + dir.getStepY() * 0.5 + rand.nextDouble() * 0.125 * dir.getStepY();
                if (dir.getStepZ() != 0)
                    iz = pos.getZ() + 0.5F + dir.getStepZ() * 0.5 + rand.nextDouble() * 0.125 * dir.getStepZ();

                level.addParticle(ParticleTypes.FLAME, ix, iy, iz, 0.0, 0.0, 0.0);
                level.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.0, 0.0);
                level.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.1, 0.0);
            }
        }
    }

    // Фабричный метод для создания свойств
    public static Block.Properties createProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.NETHER)
                .strength(3.0F, 15.0F)
                .lightLevel(state -> 10) // Свет 10
                .randomTicks()
                .requiresCorrectToolForDrops();
    }
}