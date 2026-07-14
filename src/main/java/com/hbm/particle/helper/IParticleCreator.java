package com.hbm.particle.helper;

import java.util.HashMap;
import java.util.Map;

import com.hbm.network.PacketDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Does two cool things:
 * <br>- requires no more additions to ClientProxy which is already bloated, full of other stuff and cumbersome to work with
 * <br>- being a separate class, we can get as messy as we want without affecting other particles, so effects can overall have more logic behind them without turning into a big ugly clump
 * @author hbm
 *
 */
public interface IParticleCreator {
    Map<String, IParticleCreator> creators = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag data);

    static void sendPacket(Level world, double x, double y, double z, int range, CompoundTag data) {
        if(!world.isClientSide()) {
            PacketDispatcher.sendAuxParticleNT(data, x, y, z, world, BlockPos.containing(x, y, z));
        }
    }
}