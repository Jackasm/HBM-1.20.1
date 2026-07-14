package com.hbm.explosion.vanillant.standard;

import java.util.HashMap;
import java.util.Map.Entry;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IPlayerProcessor;
import com.hbm.network.PacketDispatcher;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PlayerProcessorStandard implements IPlayerProcessor {

    @Override
    public void process(ExplosionVNT explosion, Level world, double x, double y, double z, HashMap<Player, Vec3> affectedPlayers) {

        for(Entry<Player, Vec3> entry : affectedPlayers.entrySet()) {
            Player player = entry.getKey();

            if(player instanceof ServerPlayer serverPlayer) {
                Vec3 knockback = entry.getValue();

                // Применяем отбрасывание на сервере
                player.setDeltaMovement(player.getDeltaMovement().add(knockback));
                player.hurtMarked = true;

                // Отправка пакета для дополнительных эффектов на клиенте
                // PacketDispatcher.sendKnockbackPacket(serverPlayer, knockback);
            }
        }
    }
}