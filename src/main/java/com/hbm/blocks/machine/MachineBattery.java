package com.hbm.blocks.machine;

import com.hbm.tileentity.ModTileEntity;

import com.hbm.tileentity.storage.TileEntityMachineBattery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MachineBattery extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    private final long maxPower; // ёмкость батареи

    public MachineBattery(Properties properties, long maxPower) {
        super(properties);
        this.maxPower = maxPower;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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
    public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TileEntityMachineBattery battery) {
                if (stack.hasCustomHoverName()) {
                    battery.setCustomName(stack.getHoverName().getString());
                }
                // Восстановление NBT (если есть в стеке)
                if (stack.getTag() != null && stack.getTag().contains("BlockEntityTag")) {
                    battery.load(stack.getTag().getCompound("BlockEntityTag"));
                }
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityMachineBattery(pos, state, maxPower);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TileEntityMachineBattery battery) {
            NetworkHooks.openScreen((ServerPlayer) player, battery, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.MACHINE_BATTERY.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityMachineBattery battery) {
                    battery.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Stores up to " + formatNumber(maxPower) + " HE"));
        tooltip.add(Component.literal("Charge speed: " + formatNumber(maxPower / 200) + " HE/t"));
        tooltip.add(Component.literal("Discharge speed: " + formatNumber(maxPower / 600) + " HE/t"));
        if (stack.getTag() != null && stack.getTag().contains("BlockEntityTag")) {
            long power = stack.getTag().getCompound("BlockEntityTag").getLong("power");
            tooltip.add(Component.literal(formatNumber(power) + " / " + formatNumber(maxPower) + " HE"));
        }
    }

    // Утилита для форматирования чисел (можно вынести в отдельный класс)
    private static String formatNumber(long value) {
        if (value >= 1_000_000_000_000L) return (value / 1_000_000_000_000L) + "T";
        if (value >= 1_000_000_000L) return (value / 1_000_000_000L) + "B";
        if (value >= 1_000_000L) return (value / 1_000_000L) + "M";
        if (value >= 1_000L) return (value / 1_000L) + "k";
        return String.valueOf(value);
    }

    // Статический фабричный метод для свойств блока
    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .noOcclusion();
    }

    // Для получения ёмкости из блока (используется в TileEntity)
    public long getMaxPower() {
        return maxPower;
    }
}