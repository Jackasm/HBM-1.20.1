package com.hbm.blocks.generic;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.deco.TileEntityGeysir;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGeysir extends Block implements EntityBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

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
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        boolean active = state.getValue(ACTIVE);

        if (type == GeyserType.VAPOR && active) {
            level.addParticle(ParticleTypes.CLOUD,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    0, 0.1, 0);
        }

        if (type == GeyserType.NETHER) {
            level.addParticle(ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 1.0625, pos.getZ() + 0.5,
                    0, 0, 0);
            if (random.nextFloat() < 0.3F) {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3,
                        pos.getY() + 1.1 + random.nextDouble() * 0.2,
                        pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3,
                        0, 0.02 + random.nextDouble() * 0.03, 0);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityGeysir(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> beType) {
        if (beType == ModTileEntity.GEYSIR.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityGeysir geysir) geysir.tick();
            };
        }
        return null;
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