package com.hbm.items.tool;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.network.TileEntityPylonBase;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemWiring extends Item {

    public ItemWiring(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null || player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (level.getBlockState(pos).getBlock() instanceof BlockDummyable dummy) {
            BlockPos core = dummy.findCore(level, pos);
            if (core != null) pos = core;
        }

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityPylonBase currentPylon)) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();

        if (!stack.hasTag() || !Objects.requireNonNull(stack.getTag()).contains("wireStart")) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            stack.getOrCreateTag().put("wireStart", tag);
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("Wire start"), true);
            }
            player.swing(context.getHand());
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            CompoundTag wireTag = stack.getTag().getCompound("wireStart");
            int x1 = wireTag.getInt("x");
            int y1 = wireTag.getInt("y");
            int z1 = wireTag.getInt("z");
            BlockPos firstPos = new BlockPos(x1, y1, z1);
            BlockEntity firstTe = level.getBlockEntity(firstPos);

            if (firstTe instanceof TileEntityPylonBase firstPylon) {
                int result = TileEntityPylonBase.canConnect(firstPylon, currentPylon);
                if (!level.isClientSide) {
                    switch (result) {
                        case 0:
                            firstPylon.addConnection(pos.getX(), pos.getY(), pos.getZ());
                            currentPylon.addConnection(x1, y1, z1);
                            player.displayClientMessage(Component.literal("Wire end"), true);
                            break;
                        case 1:
                            player.displayClientMessage(Component.literal("Wire error - Pylons are not the same type"), true);
                            break;
                        case 2:
                            player.displayClientMessage(Component.literal("Wire error - Cannot connect to the same pylon"), true);
                            break;
                        case 3:
                            player.displayClientMessage(Component.literal("Wire error - Pylon is too far away"), true);
                            break;
                    }
                }
                stack.removeTagKey("wireStart");
                player.swing(context.getHand());
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.literal("Wire error – start pylon missing"), true);
                }
                stack.removeTagKey("wireStart");
                return InteractionResult.FAIL;
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("wireStart")) {
            CompoundTag wireTag = stack.getTag().getCompound("wireStart");
            tooltip.add(Component.literal("Wire start x: " + wireTag.getInt("x")));
            tooltip.add(Component.literal("Wire start y: " + wireTag.getInt("y")));
            tooltip.add(Component.literal("Wire start z: " + wireTag.getInt("z")));
        } else {
            tooltip.add(Component.literal("Right-click poles to connect"));
        }
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("wireStart");
    }
}