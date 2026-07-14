package com.hbm.blocks.generic;

import com.hbm.blocks.IBlockMulti;
import com.hbm.inventory.gui.GUIScreenSnowglobe;
import com.hbm.items.block.ItemSnowglobe;
import com.hbm.tileentity.block.TileEntitySnowglobe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockSnowglobe extends BaseEntityBlock implements IBlockMulti {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Shapes.box(0.25D, 0.0D, 0.25D, 0.75D, 0.3125D, 0.75D);

    public BlockSnowglobe(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public enum SnowglobeType {
        NONE("NONE", null),
        RIVETCITY("Rivet City", "Welcome to Rivet City. Please wait while the bridge extends."),
        TENPENNYTOWER("Tenpenny Tower", "Tenpenny Tower is the brainchild of Allistair Tenpenny, a British refugee who came to the Capital Wasteland seeking his fortune."),
        LUCKY38("Lucky 38", "My guess? Leads to a big cashout at some casino - and if the \"38\" on it is any indication... well... Lucky 38 it is."),
        SIERRAMADRE("Sierra Madre", "It's the moment you've been waiting for, the reason we're all here - the Gala Event, the Grand Opening of the Sierra Madre Casino."),
        PRYDWEN("The Prydwen", "People of the Commonwealth. Do not interfere. Our intentions are peaceful. We are the Brotherhood of Steel.");

        public final String label;
        public final String inscription;

        SnowglobeType(String label, String inscription) {
            this.label = label;
            this.inscription = inscription;
        }
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
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntitySnowglobe snowglobe) {
            return ItemSnowglobe.withType(snowglobe.type);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        Direction facing = placer != null ? placer.getDirection().getOpposite() : Direction.NORTH;
        level.setBlock(pos, state.setValue(FACING, facing), 3);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntitySnowglobe snowglobe) {
            CompoundTag tag = stack.getTag();
            int customModelData = 0;
            if (tag != null && tag.contains("CustomModelData")) {
                customModelData = tag.getInt("CustomModelData");
            }
            snowglobe.type = SnowglobeType.values()[Math.abs(customModelData) % SnowglobeType.values().length];
            snowglobe.setChanged();
        }
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, Player player) {
        if (!player.isCreative() && !level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntitySnowglobe snowglobe) {
                ItemStack stack = ItemSnowglobe.withType(snowglobe.type);
                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                item.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(item);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntitySnowglobe snowglobe) {
                Minecraft.getInstance().setScreen(new GUIScreenSnowglobe(snowglobe));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySnowglobe(pos, state);
    }

    @Override
    public int getSubCount() {
        return SnowglobeType.values().length;
    }

    @Override
    public Component getOverrideDisplayName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int customModelData = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            customModelData = tag.getInt("CustomModelData");
        }
        SnowglobeType type = SnowglobeType.values()[Math.abs(customModelData) % SnowglobeType.values().length];
        String name = type == SnowglobeType.NONE ? "" : type.label;
        return Component.translatable(this.getDescriptionId() + ".name", name);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        int customModelData = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            customModelData = tag.getInt("CustomModelData");
        }
        SnowglobeType type = SnowglobeType.values()[Math.abs(customModelData) % SnowglobeType.values().length];
        if (type.inscription != null) {
            tooltip.add(Component.literal(type.inscription));
        }
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.ICE)
                .strength(0.5F, 0.5F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
}