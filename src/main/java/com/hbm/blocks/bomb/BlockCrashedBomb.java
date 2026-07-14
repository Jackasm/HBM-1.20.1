package com.hbm.blocks.bomb;

import com.hbm.config.BombConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.logic.EntityBalefire;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorCross;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.interfaces.IBomb;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;

import com.hbm.network.PacketDispatcher;
import com.hbm.particle.helper.ExplosionCreator;
import com.hbm.tileentity.bomb.TileEntityCrashedBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCrashedBomb extends BaseEntityBlock implements IBomb {

    public static final EnumProperty<EnumDudType> TYPE = EnumProperty.create("type", EnumDudType.class);

    public enum EnumDudType implements StringRepresentable {
        BALEFIRE("balefire"),
        CONVENTIONAL("conventional"),
        NUKE("nuke"),
        SALTED("salted");

        private final String name;

        EnumDudType(String name) {
            this.name = name;
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return name;
        }
    }

    public BlockCrashedBomb(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, EnumDudType.BALEFIRE));
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(-1.0F, 6000.0F)
                .noOcclusion();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityCrashedBomb(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.DEFUSER.get()) {

            EnumDudType type = state.getValue(TYPE);

            if (type == EnumDudType.BALEFIRE) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.EGG_BALEFIRE_SHARD.get())));
            }
            if (type == EnumDudType.CONVENTIONAL) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.BALL_TNT.get(), 16)));
            }
            if (type == EnumDudType.NUKE) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.BALL_TNT.get(), 8)));
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.BILLET_PLUTONIUM.get(), 4)));
            }
            if (type == EnumDudType.SALTED) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.BALL_TNT.get(), 8)));
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.BILLET_PLUTONIUM.get(), 2)));
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ModItems.INGOT_COBALT.get(), 12)));
            }

            level.removeBlock(pos, false);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            EnumDudType type = state.getValue(TYPE);
            level.removeBlock(pos, false);

            if (type == EnumDudType.BALEFIRE) {
                EntityBalefire bf = new EntityBalefire(ModEntities.BALEFIRE.get(), level);
                bf.setPos(pos.getX(), pos.getY(), pos.getZ());
                bf.destructionRange = (int) (BombConfig.fatmanRadius.get() * 1.25);
                level.addFreshEntity(bf);
                spawnMush(level, pos, true);
            }

            if (type == EnumDudType.CONVENTIONAL) {
                ExplosionVNT xnt = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 35F);
                xnt.setBlockAllocator(new BlockAllocatorStandard(24));
                xnt.setBlockProcessor(new BlockProcessorStandard().setNoDrop());
                xnt.setEntityProcessor(new EntityProcessorCross(5D).withRangeMod(1.5F));
                xnt.setPlayerProcessor(new PlayerProcessorStandard());
                xnt.explode();
                ExplosionCreator.composeEffectLarge(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            }

            if (type == EnumDudType.NUKE) {
                level.addFreshEntity(EntityNukeExplosionMK5.statFac(level, 35, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
                spawnMush(level, pos, MainRegistry.polaroidID == 11 || level.random.nextInt(100) == 0);
            }

            if (type == EnumDudType.SALTED) {
                level.addFreshEntity(EntityNukeExplosionMK5.statFac(level, 25, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).moreFallout(25));
                spawnMush(level, pos, MainRegistry.polaroidID == 11 || level.random.nextInt(100) == 0);
            }
        }
        return IBomb.BombReturnCode.DETONATED;
    }

    private void spawnMush(Level level, BlockPos pos, boolean balefire) {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 15.0F, 1.0F);

        PacketDispatcher.sendAuxParticleNT(createMushData(balefire), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, level, pos);
    }

    private CompoundTag createMushData(boolean balefire) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "muke");
        data.putBoolean("balefire", balefire);
        return data;
    }
}