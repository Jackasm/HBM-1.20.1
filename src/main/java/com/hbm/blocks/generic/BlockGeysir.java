package com.hbm.blocks.generic;

import com.hbm.tileentity.deco.TileEntityGeysir;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGeysir extends Block implements EntityBlock {

    private final GeyserType type;

    public enum GeyserType {
        WATER,
        CHLORINE,
        VAPOR,
        NETHER
    }

    public BlockGeysir(Properties properties, GeyserType type) {
        super(properties);
        this.type = type;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        int meta = 0; // метаданные, если нужны

        // Водяной гейзер (пар)
        if (this.type == GeyserType.VAPOR && meta == 1) {
            float x = pos.getX() + 0.5F;
            float y = pos.getY() + 1.0F;
            float z = pos.getZ() + 0.5F;
            level.addParticle(ParticleTypes.CLOUD, x, y, z, 0.0D, 0.1D, 0.0D);
        }

        // Гейзер в аду — искры
        if (this.type == GeyserType.NETHER) {
            float x = pos.getX() + 0.5F;
            float y = pos.getY() + 1.0625F;
            float z = pos.getZ() + 0.5F;
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
            // Добавим немного дыма для эффектности
            if (random.nextFloat() < 0.3F) {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        x + (random.nextDouble() - 0.5) * 0.3,
                        y + 0.1 + random.nextDouble() * 0.2,
                        z + (random.nextDouble() - 0.5) * 0.3,
                        0.0D, 0.02D + random.nextDouble() * 0.03, 0.0D);
            }
        }
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityGeysir(pos, state);
    }

    public static Properties createProperties(GeyserType type) {
        Properties props = Properties.of()
                .mapColor(type == GeyserType.NETHER ? MapColor.NETHER : MapColor.STONE)
                .strength(5.0F)
                .sound(SoundType.STONE)
                .noLootTable()
                .pushReaction(PushReaction.BLOCK)
                .noOcclusion();

        if (type == GeyserType.NETHER) {
            props.lightLevel(state -> 10); // 1.0F = 10
            props.strength(2.0F);
        }

        return props;
    }
}