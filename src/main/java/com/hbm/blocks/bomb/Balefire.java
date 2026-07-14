package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.potion.HbmPotion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class Balefire extends Block {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public Balefire(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    // Проверка, может ли огонь стоять на этом месте (заменяет FireBlock.canSurvive)
    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP) || this.isValidFireLocation(level, pos);
    }

    private boolean isValidFireLocation(LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.canCatchFire(level, pos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) return;

        if (!this.canSurvive(state, level, pos)) {
            level.removeBlock(pos, false);
            return;
        }

        int age = state.getValue(AGE);
        if (age < 15) {
            level.scheduleTick(pos, this, 30 + random.nextInt(10));
        }

        if (!this.canNeighborBurn(level, pos) && !level.getBlockState(pos.below()).isSolid()) {
            level.removeBlock(pos, false);
            return;
        }

        if (age < 15) {
            // Попытка поджечь соседние блоки
            this.tryCatchFire(level, pos.east(), 500, random, age, Direction.WEST);
            this.tryCatchFire(level, pos.west(), 500, random, age, Direction.EAST);
            this.tryCatchFire(level, pos.below(), 300, random, age, Direction.UP);
            this.tryCatchFire(level, pos.above(), 300, random, age, Direction.DOWN);
            this.tryCatchFire(level, pos.north(), 500, random, age, Direction.SOUTH);
            this.tryCatchFire(level, pos.south(), 500, random, age, Direction.NORTH);

            int range = 3;
            for (int ix = -range; ix <= range; ix++) {
                for (int iz = -range; iz <= range; iz++) {
                    for (int iy = -1; iy <= 4; iy++) {
                        if (ix == 0 && iy == 0 && iz == 0) continue;

                        BlockPos targetPos = pos.offset(ix, iy, iz);
                        int fireLimit = 100;
                        if (iy > 1) {
                            fireLimit += (iy - 1) * 100;
                        }

                        BlockState targetState = level.getBlockState(targetPos);
                        if (targetState.getBlock() == ModBlocks.BALEFIRE.get() &&
                                targetState.getValue(AGE) > age + 1) {
                            level.setBlock(targetPos, this.defaultBlockState().setValue(AGE, age + 1), 3);
                            continue;
                        }

                        int neighborFireChance = this.getChanceOfNeighborsEncouragingFire(level, targetPos);
                        if (neighborFireChance > 0) {
                            int adjustedFireChance = (neighborFireChance + 40 + level.getDifficulty().getId() * 7) / (age + 30);
                            if (adjustedFireChance > 0 && random.nextInt(fireLimit) <= adjustedFireChance) {
                                level.setBlock(targetPos, this.defaultBlockState().setValue(AGE, age + 1), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    private void tryCatchFire(Level level, BlockPos pos, int chance, RandomSource random, int fireMetadata, Direction face) {
        BlockState state = level.getBlockState(pos);
        int flammability = state.getFlammability(level, pos, face);
        if (random.nextInt(chance) < flammability) {
            boolean isTnt = state.getBlock() == Blocks.TNT;
            level.setBlock(pos, this.defaultBlockState().setValue(AGE, fireMetadata + 1), 3);
            if (isTnt) {
                Blocks.TNT.onCaughtFire(state, level, pos, face, null);
            }
        }
    }

    private boolean canNeighborBurn(Level level, BlockPos pos) {
        return this.canCatchFire(level, pos.east(), Direction.WEST)
                || this.canCatchFire(level, pos.west(), Direction.EAST)
                || this.canCatchFire(level, pos.below(), Direction.UP)
                || this.canCatchFire(level, pos.above(), Direction.DOWN)
                || this.canCatchFire(level, pos.north(), Direction.SOUTH)
                || this.canCatchFire(level, pos.south(), Direction.NORTH);
    }

    private int getChanceOfNeighborsEncouragingFire(Level level, BlockPos pos) {
        if (!level.getBlockState(pos).isAir()) return 0;
        int spread = 0;
        spread = this.getChanceToEncourageFire(level, pos.east(), spread, Direction.WEST);
        spread = this.getChanceToEncourageFire(level, pos.west(), spread, Direction.EAST);
        spread = this.getChanceToEncourageFire(level, pos.below(), spread, Direction.UP);
        spread = this.getChanceToEncourageFire(level, pos.above(), spread, Direction.DOWN);
        spread = this.getChanceToEncourageFire(level, pos.north(), spread, Direction.SOUTH);
        spread = this.getChanceToEncourageFire(level, pos.south(), spread, Direction.NORTH);
        return spread;
    }

    private int getChanceToEncourageFire(Level level, BlockPos pos, int currentSpread, Direction face) {
        BlockState state = level.getBlockState(pos);
        if (!state.isAir()) {
            currentSpread += state.getFlammability(level, pos, face);
        }
        return currentSpread;
    }

    public boolean canCatchFire(BlockGetter level, BlockPos pos, Direction face) {
        BlockState state = level.getBlockState(pos);
        return state.isFlammable(level, pos, face);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        entity.setRemainingFireTicks(200);
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 5 * 20, 9));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (random.nextInt(24) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    net.minecraft.sounds.SoundEvents.FIRE_AMBIENT,
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0F + random.nextFloat(),
                    random.nextFloat() * 0.7F + 0.3F, false);
        }
        for (int i = 0; i < 2; i++) {
            double d0 = pos.getX() + random.nextDouble();
            double d1 = pos.getY() + random.nextDouble();
            double d2 = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AGE, 0);
    }

    @Override
    public boolean isBurning(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(0, 0, 0, 16, 1, 16); // маленькая высота, как у огня
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    public static BlockBehaviour.Properties createBalefireProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.FIRE)
                .instabreak()
                .lightLevel(state -> 15)
                .noOcclusion()
                .noCollission()
                .noLootTable()
                .pushReaction(PushReaction.DESTROY)
                .randomTicks();
    }
}