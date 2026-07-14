package com.hbm.items.tool;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemMS extends Item {

    public ItemMS(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Lost but not forgotten"));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockState(pos).getBlock() == ModBlocks.NTM_DIRT.get()) {
            level.removeBlock(pos, false);

            RandomSource rand = RandomSource.create();
            List<ItemStack> list = new ArrayList<>();

            list.add(new ItemStack(ModItems.INGOT_U238M2.get(), 1));
            list.add(new ItemStack(ModItems.INGOT_U238M2.get(), 1));
            list.add(new ItemStack(ModItems.INGOT_U238M2.get(), 1));

            for (ItemStack sta : list) {
                float f = rand.nextFloat() * 0.8F + 0.1F;
                float f1 = rand.nextFloat() * 0.8F + 0.1F;
                float f2 = rand.nextFloat() * 0.8F + 0.1F;

                ItemEntity entityitem = new ItemEntity(level,
                        pos.getX() + f,
                        pos.getY() + f1,
                        pos.getZ() + f2,
                        sta);

                float f3 = 0.05F;
                entityitem.setDeltaMovement(
                        (float) rand.nextGaussian() * f3,
                        (float) rand.nextGaussian() * f3 + 0.2F,
                        (float) rand.nextGaussian() * f3
                );
                level.addFreshEntity(entityitem);
            }

            if (player != null && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}