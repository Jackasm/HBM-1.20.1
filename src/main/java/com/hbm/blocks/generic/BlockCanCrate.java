package com.hbm.blocks.generic;

import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import com.hbm.items.food.ItemConserve.EnumFoodType;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockCanCrate extends Block {

    public BlockCanCrate() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(1.0F, 2.5F)
                .noOcclusion()
                .sound(SoundType.WOOD));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (held.getItem() == ModToolItems.CROWBAR.get()) {
            if (!level.isClientSide) {
                dropContents(level, pos);
                level.setBlock(pos, defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public void dropContents(Level level, BlockPos pos) {
        ArrayList<ItemStack> items = getContents(level, pos);

        for (ItemStack item : items) {
            popResource(level, pos, item);
        }
    }

    public ArrayList<ItemStack> getContents(Level level, BlockPos pos) {
        ArrayList<ItemStack> ret = new ArrayList<>();
        RandomSource rand = level.getRandom();

        int count = getContentAmount(rand);
        for (int i = 0; i < count; i++) {
            ret.add(getRandomItem(rand));
        }

        return ret;
    }

    public ItemStack getRandomItem(RandomSource rand) {
        List<ItemStack> items = new ArrayList<>();

        // Консервы всех типов
        for (int a = 0; a < EnumFoodType.values().length; a++) {
            ItemStack stack = new ItemStack(ModItems.CANNED_CONSERVE.get(), 1);
            stack.getOrCreateTag().putInt("CustomModelData", a);
            items.add(stack);
        }

        // Специальные банки
        items.add(new ItemStack(ModItems.CAN_SMART.get()));
        items.add(new ItemStack(ModItems.CAN_CREATURE.get()));
        items.add(new ItemStack(ModItems.CAN_REDBOMB.get()));
        items.add(new ItemStack(ModItems.CAN_MRSUGAR.get()));
        items.add(new ItemStack(ModItems.CAN_OVERCHARGE.get()));
        items.add(new ItemStack(ModItems.CAN_LUNA.get()));
        items.add(new ItemStack(ModItems.CAN_BREEN.get()));
        items.add(new ItemStack(ModItems.CAN_BEPIS.get()));
        items.add(new ItemStack(ModItems.PUDDING.get()));

        return items.get(rand.nextInt(items.size()));
    }

    public int getContentAmount(RandomSource rand) {
        return 5 + rand.nextInt(4);
    }
}