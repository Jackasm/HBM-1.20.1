package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockCoke extends Block {

    public enum CokeType  implements StringRepresentable {
        COAL("coal"),
        LIGNITE("lignite"),
        PETROLEUM("petroleum");

        public final String name;

        CokeType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public static CokeType byOrdinal(int ordinal) {
            CokeType[] values = values();
            return values[Math.abs(ordinal) % values.length];
        }
    }

    public static final EnumProperty<CokeType> TYPE = EnumProperty.create("type", CokeType.class);

    public BlockCoke() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, CokeType.COAL));
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
        return this.defaultBlockState().setValue(TYPE, CokeType.byOrdinal(type));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("CustomModelData", state.getValue(TYPE).ordinal());
        return stack;
    }

    public Component getTypeName(int type) {
        return switch (type) {
            case 0 -> Component.translatable("block.hbm.block_coke.coal");
            case 1 -> Component.translatable("block.hbm.block_coke.lignite");
            case 2 -> Component.translatable("block.hbm.block_coke.petroleum");
            default -> Component.translatable("block.hbm.block_coke");
        };
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull net.minecraft.world.item.TooltipFlag flag) {
        // Убираем tooltip, так как имя уже будет кастомным
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 10;
    }
}