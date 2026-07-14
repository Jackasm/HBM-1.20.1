package com.hbm.blocks.machine;

import com.hbm.blocks.BlockFallingNT;

import com.hbm.inventory.container.ModContainers;
import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NTMAnvil extends BlockFallingNT implements EntityBlock {

    public static final int TIER_IRON = 1;
    public static final int TIER_STEEL = 2;
    public static final int TIER_OIL = 3;
    public static final int TIER_NUCLEAR = 4;
    public static final int TIER_RBMK = 5;
    public static final int TIER_FUSION = 6;
    public static final int TIER_PARTICLE = 7;
    public static final int TIER_GERALD = 8;

    public final int tier;
    public static final HashMap<Integer, List<NTMAnvil>> tierMap = new HashMap<>();

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_X = Block.box(4.0D, 0.0D, 2.0D, 12.0D, 10.0D, 14.0D);
    private static final VoxelShape SHAPE_Z = Block.box(2.0D, 0.0D, 4.0D, 14.0D, 10.0D, 12.0D);



    public NTMAnvil(int tier) {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 100.0F)
                .sound(SoundType.ANVIL)
                .requiresCorrectToolForDrops());

        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));

        List<NTMAnvil> anvils = tierMap.get(tier);
        if(anvils == null)
            anvils = new ArrayList<>();
        anvils.add(this);
        tierMap.put(tier, anvils);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return true; // Для правильного освещения
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if(!player.isShiftKeyDown()) {
            FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
            extraData.writeInt(this.tier); // Пишем tier в buffer

            NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) ->
                            ModContainers.ANVIL.get().create(windowId, playerInventory, extraData),
                    Component.translatable("container.anvil.tier." + this.tier)
            ), buf -> buf.writeInt(this.tier));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if(placer != null) {
            Direction direction = placer.getDirection().getOpposite();
            world.setBlock(pos, state.setValue(FACING, direction), 2);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Tier " + tier + " Anvil").withStyle(net.minecraft.ChatFormatting.GOLD));
    }
}