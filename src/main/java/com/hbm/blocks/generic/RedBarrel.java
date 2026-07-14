package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockDetonatable;

import com.hbm.explosion.ExplosionThermo;
import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

public class RedBarrel extends BlockDetonatable implements EntityBlock {

    private static final VoxelShape SHAPE = Shapes.box(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    public RedBarrel(Properties properties, boolean flammable) {
        super(properties, flammable ? 2 : 0, false, true);
    }

    @javax.annotation.Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void explodeEntity(Level world, BlockPos pos, LivingEntity entity) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        int ix = pos.getX();
        int iy = pos.getY();
        int iz = pos.getZ();

        if (this == ModBlocks.RED_BARREL.get() || this == ModBlocks.PINK_BARREL.get()) {
            world.explode(entity, x, y, z, 2.5F, Level.ExplosionInteraction.TNT);
        } else if (this == ModBlocks.LOX_BARREL.get()) {
            world.explode(entity, x, y, z, 1.0F, Level.ExplosionInteraction.NONE);
            ExplosionThermo.freeze(world, pos, 7);
        } else if (this == ModBlocks.TAINT_BARREL.get()) {
            world.explode(entity, x, y, z, 1.0F, Level.ExplosionInteraction.NONE);

            RandomSource rand = world.random;
            for (int i = 0; i < 100; i++) {
                int a = rand.nextInt(9) - 4 + ix;
                int b = rand.nextInt(9) - 4 + iy;
                int c = rand.nextInt(9) - 4 + iz;
                BlockPos targetPos = new BlockPos(a, b, c);
                BlockState targetState = world.getBlockState(targetPos);
                if (targetState.isSolid() && !targetState.isAir()) {
                    world.setBlock(targetPos, ModBlocks.TAINT.get().defaultBlockState(), 2);
                }
            }
        }
    }

    public static RedBarrel createFlammable() {
        Properties props = BlockDetonatable.createProperties(MapColor.METAL, 2.0F, 5.0F, SoundType.METAL, 0);
        return new RedBarrel(props, true);
    }

    public static RedBarrel createNonFlammable() {
        Properties props = BlockDetonatable.createProperties(MapColor.METAL, 2.0F, 5.0F, SoundType.METAL, 0);
        return new RedBarrel(props, false);
    }
}