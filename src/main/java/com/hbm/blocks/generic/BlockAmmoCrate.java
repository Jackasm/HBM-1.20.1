package com.hbm.blocks.generic;

import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class BlockAmmoCrate extends Block {

    private final Random rand = new Random();

    public BlockAmmoCrate(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(1.0F, 2.5F)
                .sound(SoundType.METAL);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (held.getItem() == ModToolItems.CROWBAR.get()) {
            if (!level.isClientSide) {
                dropContents(level, pos);
                level.removeBlock(pos, false);
                level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, 1.0F);
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

        ret.add(new ItemStack(ModItems.CAP_NUKA.get(), 12 + rand.nextInt(21)));
        ret.add(new ItemStack(ModItems.SYRINGE_METAL_STIMPAK.get(), 1 + rand.nextInt(3)));

        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_P9_SP.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_P9_FMJ.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_M357_SP.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_M357_FMJ.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_M44_SP.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_M44_FMJ.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_R556_SP.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_R556_FMJ.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_R762_SP.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_R762_FMJ.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_G12.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_G12_SLUG.get(), 16 + rand.nextInt(17)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_G40_HE.get(), 2 + rand.nextInt(3)));
        if (rand.nextBoolean()) ret.add(new ItemStack(ModAmmoItems.AMMO_ROCKET_HE.get(), 2 + rand.nextInt(3)));

        if (rand.nextInt(10) == 0) ret.add(new ItemStack(ModItems.SYRINGE_METAL_SUPER.get(), 2));

        return ret;
    }
}