package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockGraphiteDrilled extends BlockGraphiteDrilledBase implements IToolable {

    public BlockGraphiteDrilled(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        Direction side = hit.getDirection();

        if (!held.isEmpty()) {
            int meta = getMeta(state);
            int cfg = meta & 3;

            if (side.get3DDataValue() == cfg * 2 || side.get3DDataValue() == cfg * 2 + 1) {
                if (checkInteraction(level, pos, meta, player, ModItems.PILE_ROD_URANIUM.get(), ModBlocks.BLOCK_GRAPHITE_FUEL.get())) return InteractionResult.SUCCESS;
                if (checkInteraction(level, pos, meta | 8, player, ModItems.PILE_ROD_PU239.get(), ModBlocks.BLOCK_GRAPHITE_FUEL.get())) return InteractionResult.SUCCESS;
                if (checkInteraction(level, pos, meta, player, ModItems.PILE_ROD_PLUTONIUM.get(), ModBlocks.BLOCK_GRAPHITE_PLUTONIUM.get())) return InteractionResult.SUCCESS;
                if (checkInteraction(level, pos, meta, player, ModItems.PILE_ROD_SOURCE.get(), ModBlocks.BLOCK_GRAPHITE_SOURCE.get())) return InteractionResult.SUCCESS;
                if (checkInteraction(level, pos, meta, player, ModItems.PILE_ROD_BORON.get(), ModBlocks.BLOCK_GRAPHITE_ROD.get())) return InteractionResult.SUCCESS;
                if (checkInteraction(level, pos, meta, player, ModItems.PILE_ROD_LITHIUM.get(), ModBlocks.BLOCK_GRAPHITE_LITHIUM.get())) return InteractionResult.SUCCESS;
                if (checkInteraction(level, pos, meta, player, ModItems.CELL_TRITIUM.get(), ModBlocks.BLOCK_GRAPHITE_TRITIUM.get())) return InteractionResult.SUCCESS;
                if (checkInteraction(level, pos, meta, player, ModItems.PILE_ROD_DETECTOR.get(), ModBlocks.BLOCK_GRAPHITE_DETECTOR.get())) return InteractionResult.SUCCESS;

                if ((meta >> 2) != 1) {
                    if (checkInteraction(level, pos, meta | 4, player, ModItems.SHELL_ALUMINIUM.get(), ModBlocks.BLOCK_GRAPHITE_DRILLED.get())) return InteractionResult.SUCCESS;
                    if (checkInteraction(level, pos, 0, player, ModItems.INGOT_GRAPHITE.get(), ModBlocks.BLOCK_GRAPHITE.get())) return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    private boolean checkInteraction(Level level, BlockPos pos, int meta, Player player, Item item, Block block) {
        ItemStack held = player.getMainHandItem();

        if (held.getItem() == item) {
            // Специальная проверка для оболочки
            if (item == ModItems.SHELL_ALUMINIUM.get()) {
                return false;
            }

            held.shrink(1);

            // Устанавливаем блок с нужной метаданными
            BlockState newState = block.defaultBlockState();
            // В 1.20.1 метаданные хранятся в BlockState, нужно установить соответствующие свойства
            // Если блок имеет свойства для метаданных, установите их
            // Здесь упрощённо - если блок имеет кастомное свойство
            level.setBlock(pos, newState, 3);

            level.playSound(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                    SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            return true;
        }

        return false;
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, ToolType tool) {
        if (tool != ToolType.SCREWDRIVER) return false;

        BlockState state = level.getBlockState(pos);
        int meta = getMeta(state);
        int cfg = meta & 3;

        // Проверяем сторону и наличие алюминиевой заглушки
        if (!level.isClientSide && (side.get3DDataValue() == cfg * 2 || side.get3DDataValue() == cfg * 2 + 1) && (meta >> 2) == 1) {
            // Возвращаем в обычное просверленное состояние
            BlockState newState = ModBlocks.BLOCK_GRAPHITE_DRILLED.get().defaultBlockState();
            level.setBlock(pos, newState, 3);

            level.playSound(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                    SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.BLOCKS, 1.0F, 0.85F);

            // Выбрасываем алюминиевую оболочку
            ejectItem(level, pos, side, new ItemStack(ModItems.SHELL_ALUMINIUM.get(), 1));
        }

        return true;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }
}