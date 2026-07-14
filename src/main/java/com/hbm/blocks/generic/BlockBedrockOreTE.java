
package com.hbm.blocks.generic;

import com.hbm.api.fluid.IFillableItem;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.machine.ItemDrillbit;
import com.hbm.items.machine.ItemDrillbit.EnumDrillType;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.block.TileEntityBedrockOre;
import com.hbm.util.EnumUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockBedrockOreTE extends Block implements EntityBlock, ILookOverlay {

    public static final IntegerProperty SHAPE = IntegerProperty.create("shape", 0, 9);

    public BlockBedrockOreTE(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityBedrockOre(pos, state);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        level.sendBlockUpdated(pos, state, state, 3);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return InteractionResult.PASS;
        if (!player.isCreative()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = level.getBlockEntity(pos);

        if (te instanceof TileEntityBedrockOre ore) {

            if (stack.getItem() instanceof ItemDrillbit) {
                EnumDrillType type = EnumUtil.grabEnumSafely(EnumDrillType.class, stack.getDamageValue());
                ore.tier = type.tier;
            } else if (FluidContainerRegistry.getFluidType(stack) != Fluids.NONE.get()) {
                FluidTypeHBM type = FluidContainerRegistry.getFluidType(stack);
                int amount = FluidContainerRegistry.getFluidContent(stack, type);
                ore.acidRequirement = new FluidStackHBM(type, amount);
            } else if (stack.getItem() instanceof IFillableItem item) {
                FluidTypeHBM type = item.getFirstFluidType(stack);
                if (type != null) {
                    ore.acidRequirement = new FluidStackHBM(type, item.getFill(stack));
                }
            } else {
                ore.resource = stack.copy();
                ore.shape = level.random.nextInt(10);
            }

            BlockState newState = state.setValue(SHAPE, ore.shape);
            level.setBlock(pos, newState, 3);

            ore.setChanged();
        }

        level.sendBlockUpdated(pos, state, state, 3);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public int getPasses() {
        return 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        if (context.mc().level == null || context.mc().hitResult == null ||
                context.mc().hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return sections;
        }

        BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) context.mc().hitResult).getBlockPos();
        Level level = context.mc().level;

        if (level == null) return sections;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityBedrockOre ore)) return sections;

        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);

        if (ore.resource != null && !ore.resource.isEmpty()) {
            mainSection.setIcon(ore.resource);
        } else {
            mainSection.setIcon(new ItemStack(this.asItem()));
        }

        mainSection.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(style -> style.withColor(0xffff00)));

        if (ore.resource != null && !ore.resource.isEmpty()) {
            mainSection.addLine(Component.literal("↓ ")
                    .append(ore.resource.getHoverName())
                    .withStyle(style -> style.withColor(0xffff55)));
        }

        mainSection.addLine(Component.literal("⛏ Требуется уровень бура: " + ore.tier)
                .withStyle(style -> style.withColor(0xffaa00)));

        if (ore.acidRequirement != null) {
            mainSection.addLine(Component.literal("🧪 Требуется: " + ore.acidRequirement.fill + "mB " +
                            ore.acidRequirement.type.getLocalizedName())
                    .withStyle(style -> style.withColor(0xff5555)));
        }

        sections.add(mainSection);
        return sections;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 3600000.0F) // неразрушимый
                .noLootTable()
                .pushReaction(PushReaction.BLOCK);
    }


}
