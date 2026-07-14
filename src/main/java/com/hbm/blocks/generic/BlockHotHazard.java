package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockHotHazard extends BlockHazard {

    public static final float HEAT_LEVEL = 5.0F;

    public BlockHotHazard(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .strength(5.0F, 50.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK)
                .lightLevel(state -> 3);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (level.canSeeSky(pos.above())) {
            float ox = random.nextFloat();
            float oz = random.nextFloat();
            level.addParticle(ParticleTypes.CLOUD,
                    pos.getX() + ox,
                    pos.getY() + 1,
                    pos.getZ() + oz,
                    0.0D, 0.0D, 0.0D);
        }

        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN) continue;

            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.getFluidState().isSourceOfType(net.minecraft.world.level.material.Fluids.WATER)) {
                double ix = pos.getX() + 0.5F + dir.getStepX() + random.nextDouble() - 0.5D;
                double iy = pos.getY() + 0.5F + dir.getStepY() + random.nextDouble() - 0.5D;
                double iz = pos.getZ() + 0.5F + dir.getStepZ() + random.nextDouble() - 0.5D;

                if (dir.getStepX() != 0) {
                    ix = pos.getX() + 0.5F + dir.getStepX() * 0.5 + random.nextDouble() * 0.125 * dir.getStepX();
                }
                if (dir.getStepY() != 0) {
                    iy = pos.getY() + 0.5F + dir.getStepY() * 0.5 + random.nextDouble() * 0.125 * dir.getStepY();
                }
                if (dir.getStepZ() != 0) {
                    iz = pos.getZ() + 0.5F + dir.getStepZ() * 0.5 + random.nextDouble() * 0.125 * dir.getStepZ();
                }

                level.addParticle(ParticleTypes.CLOUD, ix, iy, iz, 0.0D, 0.0D, 0.0D);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
        }
    }
}