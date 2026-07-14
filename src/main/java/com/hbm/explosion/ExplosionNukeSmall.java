package com.hbm.explosion;

import com.hbm.config.BombConfig;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorStandard;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class ExplosionNukeSmall {

    public static void explode(Level level, double posX, double posY, double posZ, MukeParams params) {

        // spawn particles, if present
        if (params.particle != null) {
            CompoundTag data = new CompoundTag();
            data.putString("type", params.particle);
            // if the FX type is "muke", apply random BF effect
            if (params.particle.equals("muke") && (level.random.nextInt(100) == 0)) {
                data.putBoolean("balefire", true);
            }

            PacketDispatcher.sendAuxParticleNT(data, posX, posY + 0.5, posZ,
                    level, BlockPos.containing(posX, posY, posZ));
        }

        // play the sound in any case
        level.playSound(null, posX, posY, posZ,
                ModSounds.MUKE_EXPLOSION.get(), SoundSource.HOSTILE, 15.0F, 1.0F);

        if (params.shrapnelCount > 0) {
            ExplosionLarge.spawnShrapnels(level, posX, posY, posZ, params.shrapnelCount);
        }

        if (params.miniNuke && !params.safe) {
            ExplosionVNT vnt = new ExplosionVNT(level, posX, posY, posZ, params.blastRadius)
                    .setBlockAllocator(new BlockAllocatorStandard(params.resolution))
                    .setEntityProcessor(new EntityProcessorStandard())
                    .setPlayerProcessor(new PlayerProcessorStandard());

            vnt.addAllAttrib(params.explosionAttribs);
            vnt.explode();
        }

        if (params.killRadius > 0) {
            ExplosionNukeGeneric.dealDamage(level, posX, posY, posZ, params.killRadius);
        }

        if (!params.miniNuke) {
            EntityNukeExplosionMK5 entity = EntityNukeExplosionMK5.statFac(level, (int) params.blastRadius, posX, posY, posZ);
            level.addFreshEntity(entity);
        }

        if (params.miniNuke) {
            float radMod = params.radiationLevel / 3F;

            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    if (Math.abs(i) + Math.abs(j) < 4) {
                        BlockPos targetPos = new BlockPos(
                                (int) Math.floor(posX + i * 16),
                                (int) Math.floor(posY),
                                (int) Math.floor(posZ + j * 16)
                        );
                        RadiationEvents.incrementRadiation(level, targetPos, 50 / (Math.abs(i) + Math.abs(j) + 1) * radMod);
                    }
                }
            }
        }
    }



    public static MukeParams PARAMS_SAFE = new MukeParams() {{
        safe = true;
        killRadius = 45F;
        radiationLevel = 2F;
    }};

    public static MukeParams PARAMS_TOTS = new MukeParams() {{
        blastRadius = 10F;
        killRadius = 30F;
        particle = "tinytot";
        shrapnelCount = 0;
        resolution = 32;
        radiationLevel = 1;
    }};

    public static MukeParams PARAMS_LOW = new MukeParams() {{
        blastRadius = 15F;
        killRadius = 45F;
        radiationLevel = 2;
    }};

    public static MukeParams PARAMS_MEDIUM = new MukeParams() {{
        blastRadius = 20F;
        killRadius = 55F;
        radiationLevel = 3;
    }};

    public static MukeParams PARAMS_HIGH = new MukeParams() {{
        miniNuke = false;
        blastRadius = BombConfig.FATMAN_RADIUS;
        shrapnelCount = 0;
    }};

    /* more sensible approach with more customization options, idea shamelessly stolen from Martin */
    public static class MukeParams {
        public boolean miniNuke = true;
        public boolean safe = false;
        public float blastRadius;
        public float killRadius;
        public float radiationLevel = 1F;
        public String particle = "muke";
        public int shrapnelCount = 25;
        public int resolution = 64;
        public ExplosionVNT.ExAttrib[] explosionAttribs = new ExplosionVNT.ExAttrib[]{
                ExplosionVNT.ExAttrib.FIRE, ExplosionVNT.ExAttrib.NOPARTICLE, ExplosionVNT.ExAttrib.NOSOUND,
                ExplosionVNT.ExAttrib.NODROP, ExplosionVNT.ExAttrib.NOHURT
        };
    }
}