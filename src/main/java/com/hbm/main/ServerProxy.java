package com.hbm.main;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ServerProxy extends CommonProxy {

    @Override
    public void effectNT(CompoundTag nbt) {
    }

    public Player me() {
        return null;
    }

}