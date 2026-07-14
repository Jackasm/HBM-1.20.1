package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.main.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockHazard extends Block {

    protected float rad = 0.0F;
    protected boolean radChecked = false;
    private ExtDisplayEffect extEffect = null;
    private boolean beaconable = false;

    public BlockHazard(Properties properties) {
        super(properties);
    }

    public BlockHazard setDisplayEffect(ExtDisplayEffect extEffect) {
        this.extEffect = extEffect;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        super.animateTick(state, level, pos, rand);

        if (extEffect == null) return;

        switch (extEffect) {
            case RADFOG:
            case SCHRAB:
            case FLAMES:
                spawnParticles(level, pos, rand);
                break;
            case LAVAPOP:
                level.addParticle(ParticleTypes.LAVA,
                        pos.getX() + rand.nextFloat(),
                        pos.getY() + 1.1,
                        pos.getZ() + rand.nextFloat(),
                        0.0D, 0.0D, 0.0D);
                break;
            default:
                break;
        }
    }

    private void spawnParticles(Level level, BlockPos pos, RandomSource rand) {
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN && this.extEffect == ExtDisplayEffect.FLAMES)
                continue;

            BlockPos neighborPos = pos.relative(dir);
            if (level.getBlockState(neighborPos).isAir()) {

                double ix = pos.getX() + 0.5 + dir.getStepX() + rand.nextDouble() * 3 - 1.5;
                double iy = pos.getY() + 0.5 + dir.getStepY() + rand.nextDouble() * 3 - 1.5;
                double iz = pos.getZ() + 0.5 + dir.getStepZ() + rand.nextDouble() * 3 - 1.5;

                if (dir.getStepX() != 0)
                    ix = pos.getX() + 0.5 + dir.getStepX() * 0.5 + rand.nextDouble() * dir.getStepX();
                if (dir.getStepY() != 0)
                    iy = pos.getY() + 0.5 + dir.getStepY() * 0.5 + rand.nextDouble() * dir.getStepY();
                if (dir.getStepZ() != 0)
                    iz = pos.getZ() + 0.5 + dir.getStepZ() * 0.5 + rand.nextDouble() * dir.getStepZ();

                if (this.extEffect == ExtDisplayEffect.RADFOG) {
                    level.addParticle(ParticleTypes.MYCELIUM, ix, iy, iz, 0.0, 0.0, 0.0);
                }
                if (this.extEffect == ExtDisplayEffect.SCHRAB) {
                    CompoundTag data = new CompoundTag();
                    data.putString("type", "schrabfog");
                    data.putDouble("posX", ix);
                    data.putDouble("posY", iy);
                    data.putDouble("posZ", iz);
                    MainRegistry.proxy.effectNT(data);
                }
                if (this.extEffect == ExtDisplayEffect.FLAMES) {
                    level.addParticle(ParticleTypes.FLAME, ix, iy, iz, 0.0, 0.0, 0.0);
                    level.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.0, 0.0);
                    level.addParticle(ParticleTypes.SMOKE, ix, iy, iz, 0.0, 0.1, 0.0);
                }
            }
        }
    }

    public BlockHazard makeBeaconable() {
        this.beaconable = true;
        return this;
    }

    public boolean isBeaconBase(BlockState state, LevelReader level, BlockPos pos, BlockPos beacon) {
        return beaconable;
    }


    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!radChecked)
        {
            ItemStack stack = new ItemStack(this);
            this.rad = HazardSystem.getHazardLevelFromStack(stack, HazardRegistry.RADIATION) * 0.2F;
            radChecked = true;
        }

        if (this.rad > 0) {
            RadiationEvents.incrementRadiation(level, pos, rad);
            level.scheduleTick(pos, this, this.getTickRate(level));
        }
    }

    public int getTickRate(LevelReader level) {
        return this.rad > 0 ? 20 : 0;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 20);
    }

    public enum ExtDisplayEffect {
        RADFOG,
        SPARKS,
        SCHRAB,
        FLAMES,
        LAVAPOP
    }

    public net.minecraft.world.item.Rarity getRarity(ItemStack stack) {
        if (this == ModBlocks.BLOCK_SCHRARANIUM.get()
                || this == ModBlocks.BLOCK_SCHRABIDATE.get()
                || this == ModBlocks.BLOCK_SOLINIUM.get()
                || this == ModBlocks.BLOCK_SCHRABIDIUM_FUEL.get())
            return net.minecraft.world.item.Rarity.RARE;

        return net.minecraft.world.item.Rarity.COMMON;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .strength(5.0F, 50.0F)
                .randomTicks()
                .pushReaction(PushReaction.DESTROY);
    }
}