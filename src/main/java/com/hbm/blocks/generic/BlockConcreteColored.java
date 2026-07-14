package com.hbm.blocks.generic;

import com.hbm.items.block.ItemBlockConcreteColored;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockConcreteColored extends Block {

    public static final EnumProperty<DyeColor> TYPE = EnumProperty.create("type", DyeColor.class);

    public BlockConcreteColored(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, DyeColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        CompoundTag tag = stack.getTag();
        int colorOrdinal = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            colorOrdinal = tag.getInt("CustomModelData");
        }
        DyeColor color = DyeColor.byId(colorOrdinal);
        return this.defaultBlockState().setValue(TYPE, color);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return ItemBlockConcreteColored.withColor(state.getValue(TYPE));
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        return List.of(ItemBlockConcreteColored.withColor(state.getValue(TYPE)));
    }
}