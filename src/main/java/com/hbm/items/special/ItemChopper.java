package com.hbm.items.special;

import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.EntityDuck;
import com.hbm.entity.mob.EntityHunterChopper;
import com.hbm.entity.mob.EntityUFO;
import com.hbm.entity.mob.botprime.EntityBOTPrimeHead;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemChopper extends Item {

    public ItemChopper(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        Block block = level.getBlockState(pos).getBlock();
        BlockState state = level.getBlockState(pos);
        BlockPos spawnPos = pos.relative(side);
        double offset = 0.0D;

        if (side == Direction.UP && block.getRenderShape(state) != null) {
            offset = 0.5D;
        }

        Entity entity = spawnCreature(level, stack.getDamageValue(), spawnPos.getX() + 0.5D, spawnPos.getY() + offset, spawnPos.getZ() + 0.5D);

        if (entity != null) {
            if (entity instanceof LivingEntity living && stack.hasCustomHoverName()) {
                living.setCustomName(stack.getHoverName());
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        HitResult hitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos pos = blockHit.getBlockPos();

        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, blockHit.getDirection(), stack)) {
            return InteractionResultHolder.pass(stack);
        }

        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof LiquidBlock) {
            Entity entity = spawnCreature(level, stack.getDamageValue(), pos.getX(), pos.getY(), pos.getZ());

            if (entity != null) {
                if (entity instanceof LivingEntity living && stack.hasCustomHoverName()) {
                    living.setCustomName(stack.getHoverName());
                }

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
        }

        return InteractionResultHolder.success(stack);
    }

    public Entity spawnCreature(Level level, int damage, double x, double y, double z) {
        Entity entity = null;

        if (this == ModItems.SPAWN_CHOPPER.get()) {
            entity = new EntityHunterChopper(ModEntities.CHOPPER.get(), level);
        } else if (this == ModItems.SPAWN_WORM.get()) {
            entity = new EntityBOTPrimeHead(ModEntities.BOT_PRIME_HEAD.get(),level);
        } else if (this == ModItems.SPAWN_UFO.get()) {
            EntityUFO ufo = new EntityUFO(level);
            ufo.scanCooldown = 100;
            entity = ufo;
            y += 35;
        } else if (this == ModItems.SPAWN_DUCK.get()) {
            entity = new EntityDuck(level);
        }

        if (entity instanceof Mob mob) {
            mob.moveTo(x, y, z, level.random.nextFloat() * 360.0F, 0.0F);
            mob.yHeadRot = mob.getYRot();
            mob.yBodyRot = mob.getYRot();
            if (level instanceof ServerLevelAccessor accessor) {
                mob.finalizeSpawn(accessor, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.NATURAL, null, null);
            }
            level.addFreshEntity(mob);
        }

        return entity;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.SPAWN_WORM.get()) {
            tooltip.add(Component.literal("Without a player in survival mode"));
            tooltip.add(Component.literal("to target, he struggles around a lot."));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("He's doing his best so please show him"));
            tooltip.add(Component.literal("some consideration."));
        }
    }
}