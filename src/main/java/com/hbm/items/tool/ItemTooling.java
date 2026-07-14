package com.hbm.items.tool;

import com.hbm.api.block.IToolable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemTooling extends ItemCraftingDegradation {

    protected IToolable.ToolType type;
    private static List<ItemTooling> toolInstances = new ArrayList<>();

    public ItemTooling(IToolable.ToolType type, int durability) {
        super(durability);
        this.type = type;
    }

    public static void register() {
        for (ItemTooling tool : toolInstances) {
            tool.type.register(new ItemStack(tool));
        }
        toolInstances.clear();
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IToolable toolable) {
            if (toolable.onScrew(world, player, pos, context.getClickedFace(),
                    (float) context.getClickLocation().x - pos.getX(),
                    (float) context.getClickLocation().y - pos.getY(),
                    (float) context.getClickLocation().z - pos.getZ(),
                    this.type)) {

                if (this.getMaxDamage(stack) > 0) {
                    stack.hurtAndBreak(1, player, (p) -> {
                        p.broadcastBreakEvent(context.getHand());
                    });
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }
}