package com.hbm.blocks.generic;

import com.hbm.tileentity.deco.TileEntityWoodStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockWoodStructure extends BaseEntityBlock {

    public enum EnumWoodStructure {
        ROOF, SCAFFOLD, CEILING
    }

    // Формы по типу (из 1.7.10 setBlockBoundsBasedOnState)
    private static final VoxelShape SHAPE_ROOF = Shapes.box(0, 0, 0, 1, 0.1875, 1);
    private static final VoxelShape SHAPE_SCAFFOLD = Shapes.box(0.0625, 0, 0.0625, 0.9375, 1, 0.9375);
    private static final VoxelShape SHAPE_CEILING = Shapes.box(0, 0.875, 0, 1, 1, 1);

    private final EnumWoodStructure type;

    public BlockWoodStructure(Properties properties, EnumWoodStructure type) {
        super(properties);
        this.type = type;
    }

    public EnumWoodStructure getStructureType() {
        return type;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (type) {
            case ROOF -> SHAPE_ROOF;
            case SCAFFOLD -> SHAPE_SCAFFOLD;
            case CEILING -> SHAPE_CEILING;
        };
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    /** В 1.7.10 `onEntityCollidedWithBlock` для SCAFFOLD вешал игрока на лестницу. */
    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                             @NotNull Entity entity) {
        if (type == EnumWoodStructure.SCAFFOLD && entity instanceof Player player) {
            var props = com.hbm.extprop.HbmPlayerProps.getData(player);
            if (props != null) props.setIsOnLadder(true);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityWoodStructure(pos, state);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(5.0F, 15.0F)
                .sound(SoundType.WOOD)
                .noOcclusion();
    }
}