package com.hbm.blocks.generic;

import com.hbm.items.block.ItemBlockConcreteColoredExt;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
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

public class BlockConcreteColoredExt extends Block {

    public static final EnumProperty<EnumConcreteType> TYPE =
            EnumProperty.create("type", EnumConcreteType.class);

    public BlockConcreteColoredExt(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, EnumConcreteType.MACHINE));
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
        int type = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            type = tag.getInt("CustomModelData");
        }
        return this.defaultBlockState().setValue(TYPE, EnumConcreteType.values()[type]);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return ItemBlockConcreteColoredExt.withType(state.getValue(TYPE).ordinal());
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        return List.of(ItemBlockConcreteColoredExt.withType(state.getValue(TYPE).ordinal()));
    }

    public enum EnumConcreteType implements StringRepresentable {
        MACHINE("machine"),
        MACHINE_STRIPE("machine_stripe"),
        INDIGO("indigo"),
        PURPLE("purple"),
        PINK("pink"),
        HAZARD("hazard"),
        SAND("sand"),
        BRONZE("bronze");

        private final String name;

        EnumConcreteType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}