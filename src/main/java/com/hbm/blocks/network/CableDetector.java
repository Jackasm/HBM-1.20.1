package com.hbm.blocks.network;

import com.hbm.tileentity.network.TileEntityCableSwitch;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CableDetector extends BaseEntityBlock {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public CableDetector(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityCableSwitch(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        // В 1.7.10 детектор — клон рубильника: сущность та же (TileEntityCableSwitch), узел
        // живёт, только пока блок под сигналом (`shouldCreateNode` смотрит POWERED). Без тикера
        // tick() не вызывался вовсе, и узел не появлялся даже под питанием (#180).
        if (type == com.hbm.tileentity.ModTileEntity.CABLE_SWITCH.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityCableSwitch sw) {
                    sw.tick();
                }
            };
        }
        return null;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (level.isClientSide) return;

        boolean on = level.hasNeighborSignal(pos);
        boolean powered = state.getValue(POWERED);

        if (on != powered) {
            BlockState newState = state.setValue(POWERED, on);
            level.setBlock(pos, newState, 2);

            // Звук из мода — ModSounds.REACTOR_START
            level.playSound(null, pos,
                    com.hbm.sound.ModSounds.REACTOR_START.get(),
                    SoundSource.BLOCKS, 1.0F, on ? 1.0F : 0.85F);

            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityCableSwitch sw) {
                sw.updateState();
            }
        }
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }
}