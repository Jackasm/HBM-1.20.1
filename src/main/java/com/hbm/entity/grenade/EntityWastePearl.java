package com.hbm.entity.grenade;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockFallout;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityWastePearl extends EntityGrenadeBase {

    public EntityWastePearl(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityWastePearl(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityWastePearl(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            this.discard();

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());

            for (int ix = -3; ix <= 3; ix++) {
                for (int iy = -3; iy <= 3; iy++) {
                    for (int iz = -3; iz <= 3; iz++) {
                        BlockPos targetPos = pos.offset(ix, iy, iz);
                        BlockState targetState = this.level().getBlockState(targetPos);

                        if (this.random.nextInt(3) == 0 && targetState.canBeReplaced() &&
                                ModBlocks.FALLOUT.get().defaultBlockState().canSurvive(this.level(), targetPos)) {
                            this.level().setBlock(targetPos, ModBlocks.FALLOUT.get().defaultBlockState().setValue(BlockFallout.LAYERS, 1), 3);
                        } else if (targetState.isAir()) {
                            if (random.nextBoolean()) {
                                this.level().setBlock(targetPos, ModBlocks.GAS_RADON.get().defaultBlockState(), 3);
                            } else {
                                this.level().setBlock(targetPos, ModBlocks.GAS_RADON_DENSE.get().defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }
}