package com.hbm.items.tool;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.tileentity.block.TileEntityBedrockOre;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ItemSurveyScanner extends Item {

    public ItemSurveyScanner(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            int x = (int) Math.floor(player.getX());
            int y = (int) Math.floor(player.getY());
            int z = (int) Math.floor(player.getZ());

            boolean hasOil = false;
            boolean hasColtan = false;
            boolean hasBedrockOil = false;
            boolean hasDepth = false;
            boolean hasSchist = false;
            boolean hasAussie = false;
            TileEntityBedrockOre tile = null;

            for (int a = -5; a <= 5; a++) {
                for (int b = -5; b <= 5; b++) {
                    for (int i = y + 15; i > 1; i -= 2) {
                        BlockPos pos = new BlockPos(x + a * 5, i, z + b * 5);
                        BlockState state = level.getBlockState(pos);
                        Block block = state.getBlock();

                        if (block == ModBlocks.ORE_OIL.get()) hasOil = true;
                        else if (block == ModBlocks.ORE_COLTAN.get()) hasColtan = true;
                        else if (block == ModBlocks.ORE_BEDROCK_OIL.get()) hasBedrockOil = true;
                        else if (block == ModBlocks.STONE_DEPTH.get()) hasDepth = true;
                        else if (block == ModBlocks.STONE_DEPTH_NETHER.get()) hasDepth = true;
                        else if (block == ModBlocks.STONE_GNEISS.get()) hasSchist = true;
                        else if (block == ModBlocks.ORE_AUSTRALIUM.get()) hasAussie = true;
                    }

                    BlockPos bedrockPos = new BlockPos(x + a * 2, 0, z + b * 2);
                    BlockState bedrockState = level.getBlockState(bedrockPos);
                    if (bedrockState.getBlock() == ModBlocks.ORE_BEDROCK.get()) {
                        BlockEntity te = level.getBlockEntity(bedrockPos);
                        if (te instanceof TileEntityBedrockOre bedrockOre) {
                            tile = bedrockOre;
                        }
                    }
                }
            }

            if (hasOil) player.displayClientMessage(Component.literal("Found OIL!").withStyle(style -> style.withColor(0x000000)), false);
            if (hasBedrockOil) player.displayClientMessage(Component.literal("Found BEDROCK OIL!").withStyle(style -> style.withColor(0x000000)), false);
            if (hasColtan) player.displayClientMessage(Component.literal("Found COLTAN!").withStyle(style -> style.withColor(0xFFA500)), false);
            if (hasDepth) player.displayClientMessage(Component.literal("Found DEPTH ROCK!").withStyle(style -> style.withColor(0x808080)), false);
            if (hasSchist) player.displayClientMessage(Component.literal("Found SCHIST!").withStyle(style -> style.withColor(0x00CED1)), false);
            if (hasAussie) player.displayClientMessage(Component.literal("Found AUSTRALIUM!").withStyle(style -> style.withColor(0xFFFF00)), false);
            if (tile != null && tile.resource != null) {
                player.displayClientMessage(Component.literal("Found BEDROCK ORE for " + tile.resource.getDisplayName() + "!")
                        .withStyle(style -> style.withColor(0xFF0000)), false);
            }
        }

        player.swing(hand);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == ModBlocks.BLOCK_BERYLLIUM.get() && player.getInventory().hasAnyMatching(
                s -> s.getItem() == ModItems.ENTANGLEMENT_KIT.get())) {
            if (!level.isClientSide) {
                // Телепортация в измерения - используем новый метод
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    net.minecraft.server.level.ServerLevel targetWorld = serverPlayer.server.getLevel(
                            net.minecraft.world.level.Level.END
                    );
                    if (targetWorld != null) {
                        serverPlayer.teleportTo(targetWorld, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                                serverPlayer.getYRot(), serverPlayer.getXRot());
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}