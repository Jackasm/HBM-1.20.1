package com.hbm.render.overlay;

import com.hbm.blocks.ILookOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class BlockOverlayProvider implements IOverlayProvider {

    @Override
    public boolean shouldRender(OverlayContext context) {
        return context.mc().level != null && context.mc().hitResult != null &&
                context.mc().hitResult.getType() == HitResult.Type.BLOCK;
    }

    @Override
    public List<OverlaySection> getSections(OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockHitResult blockHit = (BlockHitResult) context.mc().hitResult;
        assert blockHit != null;
        BlockPos pos = blockHit.getBlockPos();
        Level level = context.mc().level;

        if (level == null) return sections;

        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block instanceof ILookOverlay overlay) {
            sections.addAll(overlay.getSections(context));
        } else {
            OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);

            ItemStack stack = getDisplayStack(block, blockState);

            mainSection.setIcon(stack);

            mainSection.addLine(Component.translatable(block.getDescriptionId())
                    .withStyle(style -> style.withColor(0xffff00)));

            addMiningInfo(mainSection, context, blockState, pos);

            sections.add(mainSection);
        }
        return sections;
    }

    private ItemStack getDisplayStack(Block block, BlockState state) {
        ItemStack stack = new ItemStack(block.asItem());

        for (var prop : state.getProperties()) {
            if (prop instanceof EnumProperty<?> enumProp && prop.getName().equals("type")) {
                var value = state.getValue(enumProp);
                stack.getOrCreateTag().putInt("CustomModelData", value.ordinal());
                break;
            }
        }

        return stack;
    }

    private void addMiningInfo(OverlaySection section, OverlayContext context, BlockState state, BlockPos pos) {

        Player player = context.mc().player;
        if (player == null) return;

        float hardness = state.getBlock().defaultDestroyTime();

        if (hardness >= 0) {
            // Иконка и информация об инструментах
            ItemStack toolIcon = getRequiredToolIcon(state);
            if (toolIcon != null) {
                boolean canHarvest = state.getBlock().canHarvestBlock(state, context.mc().level, pos, player);
                section.setToolIcon(toolIcon, canHarvest);
            }
        } else {
            // Нерушимые блоки
            section.addLine(Component.literal("⛏ Нерушимый")
                    .withStyle(style -> style.withColor(0xFF0000)));
        }
    }

    private ItemStack getRequiredToolIcon(BlockState state) {
        // Сначала проверяем тип инструмента для добычи
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            // Кирки
            if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) {
                return new ItemStack(Items.NETHERITE_PICKAXE);
            } else if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return new ItemStack(Items.DIAMOND_PICKAXE);
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return new ItemStack(Items.IRON_PICKAXE);
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return new ItemStack(Items.STONE_PICKAXE);
            } else {
                // Для блоков, которые можно добыть киркой, но без требований к материалу
                return new ItemStack(Items.WOODEN_PICKAXE);
            }
        }
        else if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
            // Топоры
            if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) {
                return new ItemStack(Items.NETHERITE_AXE);
            } else if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return new ItemStack(Items.DIAMOND_AXE);
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return new ItemStack(Items.IRON_AXE);
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return new ItemStack(Items.STONE_AXE);
            } else {
                return new ItemStack(Items.WOODEN_AXE);
            }
        }
        else if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            // Лопаты
            if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) {
                return new ItemStack(Items.NETHERITE_SHOVEL);
            } else if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return new ItemStack(Items.DIAMOND_SHOVEL);
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return new ItemStack(Items.IRON_SHOVEL);
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return new ItemStack(Items.STONE_SHOVEL);
            } else {
                return new ItemStack(Items.WOODEN_SHOVEL);
            }
        }
        else if (state.is(BlockTags.MINEABLE_WITH_HOE)) {
            // Мотыги
            if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) {
                return new ItemStack(Items.NETHERITE_HOE);
            } else if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return new ItemStack(Items.DIAMOND_HOE);
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return new ItemStack(Items.IRON_HOE);
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return new ItemStack(Items.STONE_HOE);
            } else {
                return new ItemStack(Items.WOODEN_HOE);
            }
        }
        else if (state.requiresCorrectToolForDrops()) {
            // Если требуется инструмент, но не указан тип, показываем общий инструмент
            // Проверяем требования к материалу
            if (state.is(Tags.Blocks.NEEDS_NETHERITE_TOOL)) {
                return new ItemStack(Items.NETHERITE_PICKAXE); // или любой незеритовый
            } else if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return new ItemStack(Items.DIAMOND_PICKAXE);
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return new ItemStack(Items.IRON_PICKAXE);
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return new ItemStack(Items.STONE_PICKAXE);
            } else {
                return new ItemStack(Items.WOODEN_PICKAXE);
            }
        }

        return null;
    }
}