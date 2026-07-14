package com.hbm.main;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class CommonProxy {

    public abstract void effectNT(CompoundTag nbt);

    public abstract Player me();

    public int getStackColor(ItemStack stack, boolean amplify) { return 0x000000; }
}