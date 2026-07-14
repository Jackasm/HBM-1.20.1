package com.hbm.uninos;

import com.hbm.uninos.networkproviders.EnergyNetProvider;

import net.minecraft.world.level.Level;

/**
 * Обёртка над UniNodespace для работы с электрическими узлами.
 * Сохраняет совместимость с оригинальным кодом HBM.
 */
public class EnergyNodespace {

    public static final EnergyNetProvider PROVIDER = EnergyNetProvider.INSTANCE;

    public static com.hbm.uninos.EnergyNode getNode(Level world, int x, int y, int z) {
        return (EnergyNode) UniNodespace.getNode(world, x, y, z, PROVIDER);
    }

    public static EnergyNode getNode(Level world, net.minecraft.core.BlockPos pos) {
        return (EnergyNode) UniNodespace.getNode(world, pos, PROVIDER);
    }

    public static void createNode(Level world, EnergyNode node) {
        UniNodespace.createNode(world, node);
    }

    public static void destroyNode(Level world, int x, int y, int z) {
        UniNodespace.destroyNode(world, x, y, z, PROVIDER);
    }

    public static void destroyNode(Level world, net.minecraft.core.BlockPos pos) {
        UniNodespace.destroyNode(world, pos, PROVIDER);
    }
}