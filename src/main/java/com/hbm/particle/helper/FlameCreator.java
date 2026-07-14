package com.hbm.particle.helper;

import com.hbm.main.ClientProxy;
import com.hbm.particle.ParticleFlamethrower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlameCreator implements IParticleCreator {

    public static final int META_FIRE = 0;
    public static final int META_BALEFIRE = 1;
    public static final int META_DIGAMMA = 2;
    public static final int META_OXY = 3;
    public static final int META_BLACK = 4;

    public static void composeEffect(Level world, double x, double y, double z, int meta) {

        CompoundTag data = new CompoundTag();
        data.putString("type", "flamethrower");
        data.putInt("meta", meta);
        IParticleCreator.sendPacket(world, x, y, z, 50, data);
    }

    public static void composeEffectClient(Level world, double x, double y, double z, int meta) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "flamethrower");
        data.putInt("meta", meta);
        data.putDouble("posX", x);
        data.putDouble("posY", y);
        data.putDouble("posZ", z);

        ClientProxy proxy = new ClientProxy();
        proxy.effectNT(data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void makeParticle(Level world, Player player, TextureManager texman, RandomSource rand, double x, double y, double z, CompoundTag data) {
        ParticleFlamethrower particle = new ParticleFlamethrower(
                (ClientLevel) world,
                x, y, z,
                data.getInt("meta")
        );
        Minecraft.getInstance().particleEngine.add(particle);
    }
}