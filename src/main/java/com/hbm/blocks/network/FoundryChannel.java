package com.hbm.blocks.network;

import com.hbm.inventory.material.Mats;
import com.hbm.items.machine.ItemScraps;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityFoundryChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoundryChannel extends FoundryCastingBase {

    public FoundryChannel(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityFoundryChannel(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.FOUNDRY_CHANNEL.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityFoundryChannel channel) {
                    TileEntityFoundryChannel.serverTick(lvl, pos, st, channel);
                }
            };
        }
        return null;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityFoundryChannel channel) {
                // Выбрасываем расплавленный металл из канала
                if (channel.amount > 0 && channel.type != null) {
                    ItemStack scrap = ItemScraps.create(new Mats.MaterialStack(channel.type, channel.amount));
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, scrap));
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        // Базовая форма для X
        double minX = 0.3125;
        double maxX = 0.6875;

        // Базовая форма для Z
        double minZ = 0.3125;
        double maxZ = 0.6875;

        double height = 0.625;

        // Проверяем соседей для изменения формы
        boolean posX = canConnect(level, pos, Direction.EAST);
        boolean negX = canConnect(level, pos, Direction.WEST);
        boolean posZ = canConnect(level, pos, Direction.SOUTH);
        boolean negZ = canConnect(level, pos, Direction.NORTH);

        // Расширяем форму в сторону соединения
        if (posX) maxX = 1.0;
        if (negX) minX = 0.0;
        if (posZ) maxZ = 1.0;
        if (negZ) minZ = 0.0;

        return Shapes.box(minX, 0, minZ, maxX, height, maxZ);
    }

    private boolean canConnect(BlockGetter level, BlockPos pos, Direction dir) {
        BlockPos neighborPos = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighborPos);
        Block neighborBlock = neighborState.getBlock();
        // Проверяем, может ли соседний блок соединяться с формой
        return neighborBlock instanceof FoundryChannel ||
                neighborBlock instanceof FoundryOutlet ||
                neighborBlock instanceof FoundryMold; // если форма может соединяться с другой формой
    }
}