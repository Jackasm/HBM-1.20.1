package com.hbm.blocks.machine;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityFan;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockMachineFan extends BaseEntityBlock implements ITooltipProvider, IToolable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public BlockMachineFan(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // getNearestLookingDirection — 6 направлений (вверх/вниз тоже)
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityFan(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.FAN.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityFan fan) fan.tick();
            };
        }
        return null;
    }

    @Override
    public boolean onScrew(Level world, Player player, BlockPos pos, Direction side,
                           float fX, float fY, float fZ, IToolable.ToolType tool) {
        if (tool == IToolable.ToolType.SCREWDRIVER) {
            Direction facing = world.getBlockState(pos).getValue(FACING);
            world.setBlock(pos, world.getBlockState(pos).setValue(FACING, facing.getOpposite()), 3);
            return true;
        }
        if (tool == IToolable.ToolType.HAND_DRILL) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileEntityFan fan) {
                fan.falloff = !fan.falloff;
                fan.setChanged();
                // сообщение игроку + звук
                world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5F, 0.5F);
            }
            return true;
        }
        if (tool == IToolable.ToolType.DEFUSER) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileEntityFan fan) {
                fan.suck = !fan.suck;
                fan.setChanged();
                world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5F, 0.5F);
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        return new String[]{"block.fan.desc"};
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK);
    }
}