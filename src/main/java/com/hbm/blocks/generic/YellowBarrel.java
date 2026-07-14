package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockDetonatable;
import com.hbm.explosion.ExplosionNukeGeneric;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class YellowBarrel extends BlockDetonatable implements EntityBlock {

    private static final VoxelShape SHAPE = Shapes.box(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    private final Random rand = new Random();

    // Исправленный конструктор
    public YellowBarrel(Properties properties) {
        super(properties, 0, true, false);
    }

    // Фабричный метод для создания
    public static YellowBarrel create() {
        Properties props = BlockDetonatable.createProperties(MapColor.METAL, 2.0F, 5.0F, SoundType.METAL, 0);
        return new YellowBarrel(props);
    }

    @javax.annotation.Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.Explosion explosion) {
        level.removeBlock(pos, false);
        if (this != ModBlocks.YELLOW_BARREL.get()) return;
        super.onBlockExploded(state, level, pos, explosion);
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, world, pos, random);

        world.addParticle(ParticleTypes.MYCELIUM,
                pos.getX() + random.nextFloat() * 0.5F + 0.25F,
                pos.getY() + 1.1F,
                pos.getZ() + random.nextFloat() * 0.5F + 0.25F,
                0.0D, 0.0D, 0.0D);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);

        if (this == ModBlocks.YELLOW_BARREL.get()) {
            RadiationEvents.incrementRadiation(level, pos, 5.0F);
        } else {
            RadiationEvents.incrementRadiation(level, pos, 0.5F);
        }

        level.scheduleTick(pos, this, getTickRate(level));
    }

    public int getTickRate(Level level) {
        return 20;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, getTickRate(level));
        }
    }

    @Override
    public void explodeEntity(Level world, BlockPos pos, LivingEntity entity) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        int ix = pos.getX();
        int iy = pos.getY();
        int iz = pos.getZ();

        if (rand.nextInt(3) == 0) {
            world.setBlock(pos, ModBlocks.TOXIC_BLOCK.get().defaultBlockState(), 3);
        } else {
            world.explode(entity, x, y, z, 12.0F, Level.ExplosionInteraction.TNT);
        }
        ExplosionNukeGeneric.waste(world, pos, 35);

        for (int i = -5; i <= 5; i++) {
            for (int j = -5; j <= 5; j++) {
                for (int k = -5; k <= 5; k++) {
                    BlockPos targetPos = new BlockPos(ix + i, iy + j, iz + k);
                    if (world.random.nextInt(5) == 0 && world.getBlockState(targetPos).isAir()) {
                        world.setBlock(targetPos, ModBlocks.GAS_RADON_DENSE.get().defaultBlockState(), 3);
                    }
                }
            }
        }
        RadiationEvents.incrementRadiation(world, pos, 35);
    }
}