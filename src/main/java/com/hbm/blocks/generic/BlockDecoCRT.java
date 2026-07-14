package com.hbm.blocks.generic;

import com.hbm.blocks.BlockMulti;

import com.hbm.datagen.worldgen.nbt.INBTBlockTransformable;
import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockDecoCRT extends BlockMulti implements INBTBlockTransformable, EntityBlock {

    public static final IntegerProperty META = IntegerProperty.create("meta", 0, 15);

    public BlockDecoCRT() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any().setValue(META, 0));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(META);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int rotation = Math.floorDiv((int) Objects.requireNonNull(context.getPlayer()).getYRot() * 4, 360) & 3;
        return this.defaultBlockState().setValue(META, rotation);
    }

    @Override
    public void setPlacedBy(Level level, @NotNull BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        int meta = stack.getDamageValue(); // или из NBT
        int rotation = Math.floorDiv((int) Objects.requireNonNull(placer).getYRot() * 4, 360) & 3;
        level.setBlock(pos, state.setValue(META, meta * 4 + rotation), 2);
    }

    @Override
    public int getSubCount() {
        return 4;
    }

    @Override
    public BlockState transformState(BlockState state, int rotation) {
        if (rotation == 0 || !state.hasProperty(META)) return state;

        int meta = state.getValue(META);
        int newMeta = transformMeta(meta, rotation);
        return state.setValue(META, newMeta);
    }

    public int transformMeta(int meta, int coordBaseMode) {
        if (coordBaseMode == 0) return meta;
        int rot = (meta + coordBaseMode) % 4;
        int type = (meta / 4) * 4;
        return rot | type;
    }
}