package com.hbm.blocks.generic;

import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockSmolder extends Block {

    public BlockSmolder(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (level.getBlockState(pos.above()).isAir()) {
            // Частицы лавы
            double x = pos.getX() + 0.25 + random.nextDouble() * 0.5;
            double y = pos.getY() + 1.1;
            double z = pos.getZ() + 0.25 + random.nextDouble() * 0.5;
            level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(ModItems.POWDER_FIRE.get());
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, Entity entity) {
        entity.setSecondsOnFire(3);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.NETHER)
                .strength(0.4F, 10.0F)
                .lightLevel(state -> 10) // 1.0F в 1.7.10 = 10 в 1.20.1
                .sound(SoundType.NETHERRACK)
                .requiresCorrectToolForDrops()
                .pushReaction(PushReaction.BLOCK);
    }
}