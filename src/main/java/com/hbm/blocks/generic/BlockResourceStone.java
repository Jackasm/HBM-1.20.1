package com.hbm.blocks.generic;

import com.hbm.items.block.ItemBlockResourceStone;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockResourceStone extends Block {

    public enum EnumStoneType implements StringRepresentable {
        SULFUR("sulfur"),
        ASBESTOS("asbestos"),
        HEMATITE("hematite"),
        MALACHITE("malachite"),
        LIMESTONE("limestone"),
        BAUXITE("bauxite");

        private final String name;

        EnumStoneType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public static final EnumProperty<EnumStoneType> TYPE = EnumProperty.create("type", EnumStoneType.class);

    public BlockResourceStone(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, EnumStoneType.SULFUR));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        int type = 0;
        if (stack.getTag() != null && stack.getTag().contains("CustomModelData")) {
            type = stack.getTag().getInt("CustomModelData");
        }
        return this.defaultBlockState().setValue(TYPE, EnumStoneType.values()[Math.abs(type) % EnumStoneType.values().length]);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return ItemBlockResourceStone.withType(state.getValue(TYPE).ordinal());
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        return List.of(ItemBlockResourceStone.withType(state.getValue(TYPE).ordinal()));
    }
}