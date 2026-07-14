package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class BlockOreBasalt extends Block {

    public static final EnumProperty<EnumBasaltOreType> TYPE = EnumProperty.create("type", EnumBasaltOreType.class);

    public BlockOreBasalt() {
        super(Properties.of()
                .mapColor(MapColor.STONE)
                .strength(5.0F, 10.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, EnumBasaltOreType.SULFUR));
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
        EnumBasaltOreType[] values = EnumBasaltOreType.values();
        return this.defaultBlockState().setValue(TYPE, values[Math.min(type, values.length - 1)]);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTag().putInt("CustomModelData", state.getValue(TYPE).ordinal());
        return stack;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        EnumBasaltOreType type = state.getValue(TYPE);

        if (!drops.isEmpty() && drops.get(0).getItem() == this.asItem()) {
            drops.clear();
            drops.add(new ItemStack(type.getDrop(), 1));
        }
        return drops;
    }

    @Override
    public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ItemStack stack, boolean dropXp) {
        super.spawnAfterBreak(state, level, pos, stack, dropXp);

        EnumBasaltOreType type = state.getValue(TYPE);
        if (type == EnumBasaltOreType.ASBESTOS) {
            level.setBlock(pos, ModBlocks.GAS_ASBESTOS.get().defaultBlockState(), 3);
        }
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        EnumBasaltOreType type = state.getValue(TYPE);

        if (type == EnumBasaltOreType.ASBESTOS && level.getBlockState(pos.above()).isAir()) {
            if (level.random.nextInt(10) == 0) {
                level.setBlock(pos.above(), ModBlocks.GAS_ASBESTOS.get().defaultBlockState(), 3);
            }
            for (int i = 0; i < 5; i++) {
                level.addParticle(ParticleTypes.MYCELIUM,
                        pos.getX() + level.random.nextFloat(),
                        pos.getY() + 1.1,
                        pos.getZ() + level.random.nextFloat(),
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        // No more BUD outgassing
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 0;
    }

    public enum EnumBasaltOreType implements StringRepresentable {
        SULFUR(ModItems.SULFUR),
        FLUORITE(ModItems.FLUORITE),
        ASBESTOS(ModItems.INGOT_ASBESTOS),
        GEM(ModItems.GEM_VOLCANIC),
        MOLYSITE(ModItems.POWDER_MOLYSITE);

        private final Supplier<Item> dropSupplier;
        private Item dropCache;

        EnumBasaltOreType(Supplier<Item> dropSupplier) {
            this.dropSupplier = dropSupplier;
        }

        public Item getDrop() {
            if (dropCache == null) {
                dropCache = dropSupplier.get();
            }
            return dropCache;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}