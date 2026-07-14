package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.effect.EntityEMPBlast;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.interfaces.IBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BombFloat extends Block implements IBomb {

    @OnlyIn(Dist.CLIENT)
    private net.minecraft.client.renderer.texture.TextureAtlasSprite iconTop;

    public BombFloat(Properties properties) {
        super(properties);
    }

    public static Properties createEMPProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 200.0F)
                .requiresCorrectToolForDrops();
    }

    public static Properties createFloatProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 200.0F)
                .requiresCorrectToolForDrops();
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (level.hasNeighborSignal(pos)) {
            explode(level, pos);
        }
    }

    @Override
    public IBomb.BombReturnCode explode(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 5.0F, level.random.nextFloat() * 0.2F + 0.9F);

        if (!level.isClientSide) {
            level.removeBlock(pos, false);

            if (this == ModBlocks.FLOAT_BOMB.get()) {
                ExplosionChaos.floater(level, pos.getX(), pos.getY(), pos.getZ(), 15, 50);
                ExplosionChaos.move(level, pos.getX(), pos.getY(), pos.getZ(), 15, 0, 50, 0);
            }

            if (this == ModBlocks.EMP_BOMB.get()) {
                ExplosionNukeGeneric.empBlast(level, pos.getX(), pos.getY(), pos.getZ(), 50);
                EntityEMPBlast wave = new EntityEMPBlast(level, 50);
                wave.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                level.addFreshEntity(wave);
            }
        }

        return BombReturnCode.DETONATED;
    }
}