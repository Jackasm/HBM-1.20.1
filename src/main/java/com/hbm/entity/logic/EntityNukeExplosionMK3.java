package com.hbm.entity.logic;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.config.BombConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.explosion.*;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EntityNukeExplosionMK3 extends EntityExplosionChunkloading {

    public int age = 0;
    public int destructionRange = 0;
    public ExplosionNukeAdvanced exp;
    public ExplosionNukeAdvanced wst;
    public ExplosionNukeAdvanced vap;
    public ExplosionFleija expl;
    public ExplosionSolinium sol;
    public int speed = 1;
    public float coefficient = 1;
    public float coefficient2 = 1;
    public boolean did = false;
    public boolean did2 = false;
    public boolean waste = true;
    public int extType = 0;

    public EntityNukeExplosionMK3(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            loadChunk(new BlockPos((int) Math.floor(getX() / 16D), 0, (int) Math.floor(getZ() / 16D)));
        }

        if (!this.did) {
            for (Player player : this.level().players()) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.NUCLEAR_EXPLOSION.trigger(sp);
                }
            }


            if (this.waste) {
                this.exp = new ExplosionNukeAdvanced((int) getX(), (int) getY(), (int) getZ(), this.level(), this.destructionRange, this.coefficient, 0);
                this.wst = new ExplosionNukeAdvanced((int) getX(), (int) getY(), (int) getZ(), this.level(), (int) (this.destructionRange * 1.8), this.coefficient, 2);
                this.vap = new ExplosionNukeAdvanced((int) getX(), (int) getY(), (int) getZ(), this.level(), (int) (this.destructionRange * 2.5), this.coefficient, 1);
            } else {
                if (extType == 0) {
                    this.expl = new ExplosionFleija((int) getX(), (int) getY(), (int) getZ(), this.level(), this.destructionRange, this.coefficient, this.coefficient2);
                }
                if (extType == 1) {
                    this.sol = new ExplosionSolinium((int) getX(), (int) getY(), (int) getZ(), this.level(), this.destructionRange, this.coefficient, this.coefficient2);
                }
            }

            this.did = true;
        }

        speed += 1;

        boolean flag = false;
        boolean flag3;

        for (int i = 0; i < this.speed; i++) {
            if (waste) {
                flag = exp.update();
                wst.update();
                flag3 = vap.update();

                if (flag3) {
                    this.clearChunkLoader();
                    this.discard();
                }
            } else {
                if (extType == 0) {
                    if (expl.update()) {
                        this.clearChunkLoader();
                        this.discard();
                    }
                }
                if (extType == 1) {
                    if (sol.update()) {
                        this.clearChunkLoader();
                        this.discard();
                    }
                }
            }
        }

        if (!flag) {
            this.level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);

            if (waste || extType != 1) {
                ExplosionNukeGeneric.dealDamage(this.level(), getX(), getY(), getZ(), this.destructionRange * 2);
            } else {
                ExplosionHurtUtil.doRadiation(this.level(), getX(), getY(), getZ(), 15000, 250000, this.destructionRange);
            }

        } else {
            if (!did2 && waste) {
                EntityFalloutRain fallout = new EntityFalloutRain(ModEntities.FALLOUT_RAIN.get(), this.level());
                fallout.setPos(this.getX(), this.getY(), this.getZ());
                fallout.setScale((int) (this.destructionRange * 1.8));
                this.level().addFreshEntity(fallout);

                did2 = true;
            }
        }

        age++;
    }

    public static final Map<ATEntry, Long> at = new HashMap<>();

    public static EntityNukeExplosionMK3 statFacFleija(ServerLevel level, double x, double y, double z, int range) {
        EntityNukeExplosionMK3 entity = new EntityNukeExplosionMK3(ModEntities.NUKE_EXPLOSION_MK3.get(), level);
        entity.setPos(x, y, z);
        entity.destructionRange = range;
        entity.speed = BombConfig.BLAST_SPEED;
        entity.coefficient = 1.0F;
        entity.waste = false;

        Iterator<Map.Entry<ATEntry, Long>> it = at.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<ATEntry, Long> next = it.next();
            if (next.getValue() < level.getGameTime()) {
                it.remove();
                continue;
            }

            ATEntry entry = next.getKey();
            if (entry.dim != level.dimension()) continue;

            Vec3 vec = new Vec3(x - entry.x, y - entry.y, z - entry.z);

            if (vec.length() < 300) {
                entity.discard();

                if (!level.isClientSide) {
                    for (int i = 0; i < 2; i++) {
                        double ix = i == 0 ? x : (entry.x + 0.5);
                        double iy = i == 0 ? y : (entry.y + 0.5);
                        double iz = i == 0 ? z : (entry.z + 0.5);

                        level.playSound(null, ix, iy, iz,
                                ModSounds.ENTITY_UFOBLAST.get(), SoundSource.HOSTILE, 15.0F, 0.7F + level.random.nextFloat() * 0.2F);

                        CompoundTag data = new CompoundTag();
                        data.putString("type", "plasmablast");
                        data.putFloat("r", 0.0F);
                        data.putFloat("g", 0.75F);
                        data.putFloat("b", 1.0F);
                        data.putFloat("scale", 7.5F);

                        PacketDispatcher.sendAuxParticleNT(data, ix, iy, iz, level, BlockPos.containing(ix, iy, iz));
                    }
                }
                break;
            }
        }

        if (!entity.isRemoved()) {
            entity.loadChunk(new BlockPos((int) Math.floor(x / 16D), 0, (int) Math.floor(z / 16D)));
        }

        return entity;
    }

    public EntityNukeExplosionMK3 makeSol() {
        this.extType = 1;
        return this;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("age");
        this.destructionRange = tag.getInt("destructionRange");
        this.speed = tag.getInt("speed");
        this.coefficient = tag.getFloat("coefficient");
        this.coefficient2 = tag.getFloat("coefficient2");
        this.did = tag.getBoolean("did");
        this.did2 = tag.getBoolean("did2");
        this.waste = tag.getBoolean("waste");
        this.extType = tag.getInt("extType");

        long time = tag.getLong("milliTime");

        if (BombConfig.LIMIT_EXPLOSION_LIFESPAN > 0 && System.currentTimeMillis() - time > BombConfig.LIMIT_EXPLOSION_LIFESPAN * 1000L) {
            this.clearChunkLoader();
            this.discard();
        }

        if (this.waste) {
            exp = new ExplosionNukeAdvanced((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), this.destructionRange, this.coefficient, 0);
            exp.readFromNbt(tag, "exp_");
            wst = new ExplosionNukeAdvanced((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), (int) (this.destructionRange * 1.8), this.coefficient, 2);
            wst.readFromNbt(tag, "wst_");
            vap = new ExplosionNukeAdvanced((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), (int) (this.destructionRange * 2.5), this.coefficient, 1);
            vap.readFromNbt(tag, "vap_");
        } else {
            if (extType == 0) {
                expl = new ExplosionFleija((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), this.destructionRange, this.coefficient, this.coefficient2);
                expl.readFromNbt(tag, "expl_");
            }
            if (extType == 1) {
                sol = new ExplosionSolinium((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), this.destructionRange, this.coefficient, this.coefficient2);
                sol.readFromNbt(tag, "sol_");
            }
        }

        this.did = true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("age", age);
        tag.putInt("destructionRange", destructionRange);
        tag.putInt("speed", speed);
        tag.putFloat("coefficient", coefficient);
        tag.putFloat("coefficient2", coefficient2);
        tag.putBoolean("did", did);
        tag.putBoolean("did2", did2);
        tag.putBoolean("waste", waste);
        tag.putInt("extType", extType);

        tag.putLong("milliTime", System.currentTimeMillis());

        if (exp != null) exp.saveToNbt(tag, "exp_");
        if (wst != null) wst.saveToNbt(tag, "wst_");
        if (vap != null) vap.saveToNbt(tag, "vap_");
        if (expl != null) expl.saveToNbt(tag, "expl_");
        if (sol != null) sol.saveToNbt(tag, "sol_");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class ATEntry {
        public ResourceKey<Level> dim;
        public int x;
        public int y;
        public int z;

        public ATEntry(ResourceKey<Level> dim, int x, int y, int z) {
            this.dim = dim;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int hashCode() {
            final int prime = 27644437;
            int result = 1;
            result = prime * result + dim.hashCode();
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + z;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            ATEntry other = (ATEntry) obj;
            if (!dim.equals(other.dim)) return false;
            if (x != other.x) return false;
            if (y != other.y) return false;
            return z == other.z;
        }
    }
}