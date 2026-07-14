package com.hbm.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;

public class Compat {

    public static final String MOD_GT6 = "gregtech";
    public static final String MOD_GCC = "GalacticraftCore";
    public static final String MOD_AR = "advancedrocketry";
    public static final String MOD_EF = "etfuturum";
    public static final String MOD_REC = "ReactorCraft";
    public static final String MOD_TIC = "TConstruct";
    public static final String MOD_RC = "Railcraft";
    public static final String MOD_TC = "tc";
    public static final String MOD_EIDS = "endlessids";
    public static final String MOD_ANG = "angelica";
    public static final String MOD_TOR = "Torcherino";

    public static Block tryLoadBlock(String domain, String name) {
        return BuiltInRegistries.BLOCK.get(ResLocation.ResLocation(domain, name));
    }

    public static boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    public static BlockEntity getTileStandard(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) return null;
        return level.getBlockEntity(pos);
    }
}
