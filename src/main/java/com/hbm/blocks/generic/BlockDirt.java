package com.hbm.blocks.generic;

import com.hbm.saveddata.TomSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlockDirt extends Block {

    public BlockDirt(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(Items.DIRT);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos checkPos = pos.offset(i, j, k);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (checkState.getBlock() instanceof GrassBlock) {
                        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                        return;
                    }
                }
            }
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
                           @NotNull BlockPos pos, @NotNull RandomSource random) {

        TomSaveData data = TomSaveData.forWorld(level);

        int blockLight = level.getBrightness(LightLayer.BLOCK, pos.above());
        int skyLight = level.getBrightness(LightLayer.SKY, pos.above());
        int light = Math.max(blockLight, (int) (skyLight * (1.0 - Objects.requireNonNull(data).dust)));

        if (light >= 9 && data.fire == 0) {
            level.setBlock(pos, Blocks.GRASS.defaultBlockState(), 3);
        }
    }

    // Фабричный метод для создания свойств блока
    public static BlockBehaviour.Properties createProperties(boolean tick) {
        Properties props = BlockBehaviour.Properties.of()
                .mapColor(MapColor.DIRT)
                .strength(0.5F)
                .pushReaction(PushReaction.DESTROY);

        if (tick) {
            props.randomTicks();
        }

        return props;
    }

    // Конкретные варианты для регистрации
    public static BlockBehaviour.Properties defaultDirtProps() {
        return createProperties(false);
    }

    public static BlockBehaviour.Properties tickingDirtProps() {
        return createProperties(true);
    }
}