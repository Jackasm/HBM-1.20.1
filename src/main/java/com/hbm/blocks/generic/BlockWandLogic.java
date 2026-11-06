package com.hbm.blocks.generic;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.datagen.worldgen.structure.util.LogicBlockActions;
import com.hbm.datagen.worldgen.structure.util.LogicBlockConditions;
import com.hbm.datagen.worldgen.structure.util.LogicBlockInteractions;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.block.TileEntityWandLogic;
import com.hbm.util.MathHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockWandLogic extends BaseEntityBlock implements ILookOverlay, IToolable, ITooltipProvider {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockWandLogic(Properties properties) {
        super(properties);
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
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL; // блок с моделью (или BER)
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null) {
            Direction dir = placer.getDirection();
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityWandLogic logic) {
                logic.placedRotation = dir.get3DDataValue();
                logic.setChanged();
            }
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && !player.isShiftKeyDown()) {
            Block block = bi.getBlock();
            if (block == this) return InteractionResult.PASS;

            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityWandLogic logic) {
                logic.setDisguise(block, stack.getDamageValue() & 15);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityWandLogic(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == com.hbm.tileentity.ModTileEntity.WAND_LOGIC.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityWandLogic logic) logic.tick();
            };
        }
        return null;
    }

    // --- IToolable ---

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side,
                           float fX, float fY, float fZ, ToolType tool) {
        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityWandLogic logic)) return false;

        switch (tool) {
            case SCREWDRIVER: {
                List<String> names = LogicBlockActions.getActionNames();
                int idx = names.indexOf(logic.actionID);
                idx += player.isShiftKeyDown() ? -1 : 1;
                idx = MathHelper.clamp(idx, 0, names.size() - 1);
                logic.setAction(names.get(idx));
                return true;
            }
            case DEFUSER: {
                List<String> names = LogicBlockConditions.getConditionNames();
                int idx = names.indexOf(logic.conditionID);
                idx += player.isShiftKeyDown() ? -1 : 1;
                idx = MathHelper.clamp(idx, 0, names.size() - 1);
                logic.setCondition(names.get(idx));
                return true;
            }
            case HAND_DRILL: {
                List<String> names = LogicBlockInteractions.getInteractionNames();
                int idx = names.indexOf(logic.interactionID);
                idx += player.isShiftKeyDown() ? -1 : 1;
                idx = MathHelper.clamp(idx, 0, names.size() - 1);
                logic.setInteraction(names.get(idx));
                return true;
            }
            default:
                return false;
        }
    }

    // --- ILookOverlay ---

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityWandLogic logic)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);

        section.addLine(Component.literal("Action: " + logic.actionID));
        section.addLine(Component.literal("Condition: " + logic.conditionID));
        section.addLine(Component.literal("Interaction: " + (logic.interactionID != null ? logic.interactionID : "None")));

        String disguiseName = (logic.disguise != null)
                ? Component.translatable(logic.disguise.getDescriptionId()).getString()
                : "None";
        section.addLine(Component.literal("Disguise Block: " + disguiseName));

        sections.add(section);
        return sections;
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        return new String[0];
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion();
    }
}