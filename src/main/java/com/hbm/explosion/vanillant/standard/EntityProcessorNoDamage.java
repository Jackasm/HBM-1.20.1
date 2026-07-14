package com.hbm.explosion.vanillant.standard;

import java.util.HashMap;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IEntityProcessor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityProcessorNoDamage implements IEntityProcessor {

    @Override
    public HashMap<Player, Vec3> process(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
        return new HashMap<>();
    }
}