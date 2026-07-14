package com.hbm.items.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class ItemCoordinateBase extends Item {

    public ItemCoordinateBase(Properties properties) {
        super(properties);
    }

    @Nullable
    public static BlockPos getPosition(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            int x = Objects.requireNonNull(tag).getInt("posX");
            int y = tag.getInt("posY");
            int z = tag.getInt("posZ");
            return new BlockPos(x, y, z);
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (this.canGrabCoordinateHere(level, pos)) {
            if (!level.isClientSide) {
                BlockPos targetPos = this.getCoordinates(level, pos);

                CompoundTag tag = stack.getOrCreateTag();
                tag.putInt("posX", targetPos.getX());
                if (includeY()) {
                    tag.putInt("posY", targetPos.getY());
                }
                tag.putInt("posZ", targetPos.getZ());

                this.onTargetSet(level, targetPos.getX(), targetPos.getY(), targetPos.getZ(), player);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /** Whether this position can be saved or if the position target is valid */
    public abstract boolean canGrabCoordinateHere(Level level, BlockPos pos);

    /** Whether this linking item saves the Y coordinate */
    public boolean includeY() {
        return true;
    }

    /** Modified the saved coordinates, for example detecting the core for multiblocks */
    public BlockPos getCoordinates(Level level, BlockPos pos) {
        return pos;
    }

    /** Extra on successful target set, eg. sounds */
    public void onTargetSet(Level level, int x, int y, int z, Player player) { }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            tooltip.add(Component.literal("X: " + Objects.requireNonNull(tag).getInt("posX")));
            if (includeY()) {
                tooltip.add(Component.literal("Y: " + tag.getInt("posY")));
            }
            tooltip.add(Component.literal("Z: " + tag.getInt("posZ")));
        } else {
            tooltip.add(Component.literal("No position set!").withStyle(style -> style.withColor(0xFF0000)));
        }
    }
}