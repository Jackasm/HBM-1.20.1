package com.hbm.items.tool;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.network.TileEntityPipelineBase;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemWrench extends ModSword {

    public ItemWrench(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();

        if (player == null) return InteractionResult.PASS;

        if (!player.isShiftKeyDown()) {
            Block b = world.getBlockState(pos).getBlock();

            if (b instanceof BlockDummyable dummyable) {
                BlockPos core = dummyable.findCore(world, pos);
                if (core != null) {
                    pos = core;
                }
            }

            BlockEntity te = world.getBlockEntity(pos);

            if (te != null && te instanceof TileEntityPipelineBase pipeline) {

                if (stack.getTag() == null) {
                    stack.getOrCreateTag().putInt("x", pos.getX());
                    stack.getOrCreateTag().putInt("y", pos.getY());
                    stack.getOrCreateTag().putInt("z", pos.getZ());

                    if (!world.isClientSide) {
                        player.displayClientMessage(Component.literal("Pipe start"), true);
                    }
                } else if (!world.isClientSide) {
                    int x1 = stack.getTag().getInt("x");
                    int y1 = stack.getTag().getInt("y");
                    int z1 = stack.getTag().getInt("z");

                    BlockEntity teFirst = world.getBlockEntity(new BlockPos(x1, y1, z1));
                    if (teFirst instanceof TileEntityPipelineBase first) {

                        switch (TileEntityPipelineBase.canConnect(first, pipeline)) {
                            case 0:
                                first.addConnection(pos.getX(), pos.getY(), pos.getZ());
                                pipeline.addConnection(x1, y1, z1);
                                player.displayClientMessage(Component.literal("Pipe end"), true);
                                break;
                            case 1:
                                player.displayClientMessage(Component.literal("Pipe error - Pipes are not the same type"), true);
                                break;
                            case 2:
                                player.displayClientMessage(Component.literal("Pipe error - Cannot connect to the same pipe anchor"), true);
                                break;
                            case 3:
                                player.displayClientMessage(Component.literal("Pipe error - Pipe anchor is too far away"), true);
                                break;
                            case 4:
                                player.displayClientMessage(Component.literal("Pipe error - Pipe anchor fluid types do not match"), true);
                                break;
                        }

                    } else {
                        player.displayClientMessage(Component.literal("Pipe error"), true);
                    }
                    stack.getTag().remove("x");
                    stack.getTag().remove("y");
                    stack.getTag().remove("z");
                    if (stack.getTag().isEmpty()) {
                        stack.setTag(null);
                    }
                }

                player.swing(hand);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        Level world = target.level();
        Vec3 look = attacker.getLookAngle();

        double dX = look.x * 0.5;
        double dY = look.y * 0.5;
        double dZ = look.z * 0.5;

        target.setDeltaMovement(target.getDeltaMovement().add(dX, dY, dZ));
        world.playSound(null, target.getX(), target.getY(), target.getZ(),
                net.minecraft.sounds.SoundEvents.ANVIL_LAND, target.getSoundSource(),
                3.0F, 0.75F);

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("x")) {
            tooltip.add(Component.literal("Pipe start x: " + stack.getTag().getInt("x"))
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Pipe start y: " + stack.getTag().getInt("y"))
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Pipe start z: " + stack.getTag().getInt("z"))
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.wrench.tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level,
                              @NotNull Entity entity, int slot, boolean selected) {
        if (level.isClientSide && stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("x")) {
            Vec3 vec = new Vec3(
                    entity.getX() - stack.getTag().getInt("x"),
                    entity.getY() - stack.getTag().getInt("y"),
                    entity.getZ() - stack.getTag().getInt("z")
            );

            double distance = vec.length();
            String text = stack.getHoverName().getString() + ": " + (int) distance + "m";

            // Отображаем тултип (нужен свой способ)
            // В 1.20.1 нет прямого аналога MainRegistry.proxy.displayTooltip
            // Можно использовать событие RenderGameOverlayEvent для отображения
            // Или сохранить в NBT и отобразить через HUD
        }
    }
}