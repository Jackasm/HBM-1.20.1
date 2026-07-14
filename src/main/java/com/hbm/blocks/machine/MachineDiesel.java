package com.hbm.blocks.machine;

import com.hbm.datagen.worldgen.nbt.INBTBlockTransformable;
import com.hbm.inventory.fluid.trait.FT_Combustible.FuelGrade;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityMachineDiesel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MachineDiesel extends BaseEntityBlock implements INBTBlockTransformable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public MachineDiesel(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityMachineDiesel(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityMachineDiesel diesel) {
            NetworkHooks.openScreen((ServerPlayer) player, diesel, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.MACHINE_DIESEL.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityMachineDiesel diesel) {
                    diesel.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("§eFuel efficiency:"));
        for (FuelGrade grade : FuelGrade.values()) {
            Double efficiency = TileEntityMachineDiesel.fuelEfficiency.get(grade);
            if (efficiency != null) {
                int eff = (int) (efficiency * 100);
                tooltip.add(Component.literal("§e-" + grade.getGrade() + ": §c" + eff + "%"));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {


        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TileEntityMachineDiesel diesel) {
            if (diesel.hasAcceptableFuel() && diesel.tank.getFill() > 0) {
                Direction dir = state.getValue(FACING);
                Direction rot = dir.getClockWise(); // была раньше rotation
                double x = pos.getX() + 0.5 - dir.getStepX() * 0.6 + rot.getStepX() * 0.1875;
                double z = pos.getZ() + 0.5 - dir.getStepZ() * 0.6 + rot.getStepZ() * 0.1875;
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, pos.getY() + 0.3125, z, 0, 0, 0);
            }
        }
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .noOcclusion();
    }


    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    public BlockState transformState(BlockState state, int rotation) {
        // Поворачиваем FACING
        Direction newFacing = state.getValue(FACING);
        for (int i = 0; i < rotation; i++) {
            newFacing = newFacing.getClockWise();
        }
        return state.setValue(FACING, newFacing);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}