package com.hbm.blocks.bomb;

import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.EntityCreeperTainted;
import com.hbm.entity.mob.EntityTaintCrab;
import com.hbm.entity.mob.EntityTeslaCrab;
import com.hbm.potion.HbmPotion;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BlockTaint extends Block implements Fallable {

    public static final IntegerProperty META = IntegerProperty.create("meta", 0, 15);
    protected static final VoxelShape COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);

    public BlockTaint(Properties properties) {
        super(properties
                .mapColor(MapColor.COLOR_GRAY)
                .strength(0.5F)
                .randomTicks()
                .pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any().setValue(META, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(META);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int meta = state.getValue(META);
        if (meta >= 15) return;

        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                for (int k = -3; k <= 3; k++) {
                    if (Math.abs(i) + Math.abs(j) + Math.abs(k) > 4) continue;
                    if (random.nextFloat() > 0.25F) continue;

                    BlockPos targetPos = pos.offset(i, j, k);
                    BlockState targetState = level.getBlockState(targetPos);
                    Block targetBlock = targetState.getBlock();

                    if (targetState.isAir() || targetBlock == Blocks.BEDROCK) continue;

                    int targetMeta = meta + 1;
                    boolean hasAir = false;
                    for (Direction dir : Direction.values()) {
                        if (level.getBlockState(targetPos.relative(dir)).isAir()) {
                            hasAir = true;
                            break;
                        }
                    }
                    if (!hasAir) targetMeta = meta + 3;
                    if (targetMeta > 15) continue;

                    if (targetBlock == this && targetState.getValue(META) >= targetMeta) continue;

                    level.setBlock(targetPos, this.defaultBlockState().setValue(META, targetMeta), 3);

                    // Проверка, может ли блок упасть (аналог BlockFalling.canFallBelow)
                    if (random.nextFloat() < 0.25F && canFallThrough(level, targetPos.below())) {
                        // Проверяем, нет ли уже падающего блока
                        boolean alreadyFalling = level.getEntitiesOfClass(FallingBlockEntity.class,
                                        new net.minecraft.world.phys.AABB(targetPos)).stream()
                                .anyMatch(f -> f.blockPosition().equals(targetPos));

                        if (!alreadyFalling) {
                            FallingBlockEntity falling = FallingBlockEntity.fall(level, targetPos,
                                    this.defaultBlockState().setValue(META, targetMeta));
                            level.addFreshEntity(falling);
                        }
                    }
                }
            }
        }
    }

    private boolean canFallThrough(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        return state.isAir() ||
                state.is(Blocks.FIRE) ||
                state.liquid() ||
                state.canBeReplaced();
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        int meta = state.getValue(META);
        int levelValue = 15 - meta;

        entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.6, 1.0, 0.6));

        List<ItemStack> list = new ArrayList<>();
        MobEffectInstance effect = new MobEffectInstance(HbmPotion.TAINT.get(), 15 * 20, levelValue, false, false);
        effect.setCurativeItems(list);

        if (entity instanceof LivingEntity living) {
            if (level.random.nextInt(50) == 0) {
                living.addEffect(effect);
            }
        }

        // Превращение обычного крипера в заражённого
        if (entity.getClass().equals(Creeper.class)) {
            EntityCreeperTainted creep = new EntityCreeperTainted(ModEntities.CREEPER_TAINTED.get(), level);
            creep.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());

            if (!level.isClientSide) {
                entity.discard();
                level.addFreshEntity(creep);
            }
        }

        // Превращение краба-теслы в заражённого краба
        if (entity instanceof EntityTeslaCrab) {
            EntityTaintCrab crab = new EntityTaintCrab(ModEntities.TAINT_CRAB.get(), level);
            crab.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());

            if (!level.isClientSide) {
                entity.discard();
                level.addFreshEntity(crab);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.taint").withStyle(ChatFormatting.RED));
    }
}