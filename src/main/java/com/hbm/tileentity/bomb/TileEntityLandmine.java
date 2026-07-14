package com.hbm.tileentity.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.Landmine;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class TileEntityLandmine extends BlockEntity {

    private boolean isPrimed = false;
    public boolean waitingForPlayer = false;

    public TileEntityLandmine(BlockPos pos, BlockState state) {
        super(ModTileEntity.LANDMINE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        Block block = level.getBlockState(worldPosition).getBlock();
        if (!(block instanceof Landmine landmine)) return;

        double range = landmine.range;
        double height = landmine.height;

        if (waitingForPlayer) {
            range = 25;
            height = 25;
        } else if (!isPrimed) {
            range *= 2;
            height *= 2;
        }

        if (block == ModBlocks.MINE_NAVAL.get()) {
            range += 1;
            height += 1;
        }

        AABB aabb = new AABB(
                worldPosition.getX() - range, worldPosition.getY() - height, worldPosition.getZ() - range,
                worldPosition.getX() + range + 1, worldPosition.getY() + height, worldPosition.getZ() + range + 1
        );

        for (Entity entity : level.getEntitiesOfClass(Entity.class, aabb)) {

            if (entity.getType().getCategory() == MobCategory.WATER_CREATURE) continue;
            if (entity.getType().getCategory() == MobCategory.AMBIENT) continue;

            if (waitingForPlayer) {
                if (entity instanceof Player) {
                    waitingForPlayer = false;
                    return;
                }
            } else {
                if (entity instanceof LivingEntity) {
                    if (isPrimed) {
                        landmine.explode(level, worldPosition);
                        if (entity instanceof Player) {
                            // addStat - нужно будет добавить в MainRegistry
                            // player.awardStat(MainRegistry.statMines, 1);
                        }
                    }
                    return;
                }
            }
        }

        if (!isPrimed && !waitingForPlayer) {
            level.playSound(null, worldPosition,
                    ModSounds.FSTBMB_START.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    3.0F, 1.0F);
            isPrimed = true;
        }
    }

    @Override
    public void load(net.minecraft.nbt.@NotNull CompoundTag nbt) {
        super.load(nbt);
        isPrimed = nbt.getBoolean("primed");
        waitingForPlayer = nbt.getBoolean("waiting");
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("primed", isPrimed);
        nbt.putBoolean("waiting", waitingForPlayer);
    }

}