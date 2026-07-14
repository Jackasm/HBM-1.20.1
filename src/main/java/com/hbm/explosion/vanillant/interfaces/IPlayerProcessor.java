package com.hbm.explosion.vanillant.interfaces;

import java.util.HashMap;

import com.hbm.explosion.vanillant.ExplosionVNT;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface IPlayerProcessor {

    /**
     * Обрабатывает игроков, затронутых взрывом
     *
     * @param explosion экземпляр взрыва
     * @param world мир
     * @param x координата X центра взрыва
     * @param y координата Y центра взрыва
     * @param z координата Z центра взрыва
     * @param affectedPlayers карта игроков и векторов воздействия
     */
    void process(ExplosionVNT explosion, Level world, double x, double y, double z, HashMap<Player, Vec3> affectedPlayers);
}