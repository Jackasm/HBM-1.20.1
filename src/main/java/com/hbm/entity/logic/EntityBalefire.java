package com.hbm.entity.logic;

import com.hbm.explosion.ExplosionBalefire;
import com.hbm.explosion.ExplosionNukeGeneric;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityBalefire extends EntityExplosionChunkloading {

    public int age = 0;
    public int destructionRange = 0;
    public ExplosionBalefire exp;
    public int speed = 1;
    public boolean did = false;

    public EntityBalefire(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        age = nbt.getInt("age");
        destructionRange = nbt.getInt("destructionRange");
        speed = nbt.getInt("speed");
        did = nbt.getBoolean("did");

        exp = new ExplosionBalefire((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), this.destructionRange);
        exp.readFromNbt(nbt, "exp_");

        this.did = true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("age", age);
        nbt.putInt("destructionRange", destructionRange);
        nbt.putInt("speed", speed);
        nbt.putBoolean("did", did);

        if (exp != null)
            exp.saveToNbt(nbt, "exp_");
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide)
            loadChunk(new BlockPos((int) Math.floor(getX() / 16D), (int) Math.floor(getY()), (int) Math.floor(getZ() / 16D)));

        if (!this.did) {

            exp = new ExplosionBalefire((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), this.destructionRange);

            this.did = true;
        }

        speed += 1; // increase speed to keep up with expansion

        boolean flag = false;
        for (int i = 0; i < this.speed; i++) {
            flag = exp.update();

            if (flag) {
                clearChunkLoader();
                this.discard();
            }
        }

        if (!flag) {
            ExplosionNukeGeneric.dealDamage(this.level(), this.getX(), this.getY(), this.getZ(), this.destructionRange * 2);
        }

        age++;
    }
}