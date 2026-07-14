package com.hbm.items.tool;

import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;
import org.jetbrains.annotations.NotNull;

public class ItemFertilizer extends Item {

    public ItemFertilizer(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        if (!player.mayUseItemAt(pos, context.getClickedFace(), stack)) {
            return InteractionResult.PASS;
        }

        boolean didSomething = false;

        for (int i = pos.getX() - 1; i <= pos.getX() + 1; i++) {
            for (int j = pos.getY() - 1; j <= pos.getY() + 1; j++) {
                for (int k = pos.getZ() - 1; k <= pos.getZ() + 1; k++) {
                    BlockPos checkPos = new BlockPos(i, j, k);
                    boolean success = fertilize(level, checkPos, player, i == pos.getX() && j == pos.getY() && k == pos.getZ());
                    didSomething = didSomething || success;
                    if (success && !level.isClientSide) {
                        level.playSound(null, checkPos, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.addParticle(ParticleTypes.HAPPY_VILLAGER, checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5, 0, 0, 0);
                    }
                }
            }
        }

        if (didSomething && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return didSomething ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public static boolean fertilize(Level level, BlockPos pos, Player player, boolean force) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        BonemealEvent event = new BonemealEvent(player, level, pos, state, new ItemStack(ModItems.POWDER_FERTILIZER.get()));
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }

        if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW) {
            return true;
        }

        if (state.getBlock() instanceof BonemealableBlock growable) {
            if (growable.isValidBonemealTarget(level, pos, state, level.isClientSide)) {
                if (!level.isClientSide) {
                    if (force || growable.isBonemealSuccess(level, level.random, pos, state)) {
                        growable.performBonemeal((ServerLevel) level, level.random, pos, state);
                    }
                }
                return true;
            }
        }

        return false;
    }
}