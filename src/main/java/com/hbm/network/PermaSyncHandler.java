package com.hbm.network;

import com.hbm.handler.ImpactWorldHandler;
import com.hbm.handler.pollution.IPollutionData;
import com.hbm.handler.pollution.PollutionData;
import com.hbm.handler.pollution.PollutionHandler;

import com.hbm.handler.pollution.PollutionType;
import com.hbm.potion.HbmPotion;
import com.hbm.saveddata.TomSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility for permanently synchronizing values every tick with a player in the given context of a world.
 * Uses the Byte Buffer directly instead of NBT to cut back on unnecessary data.
 * @author hbm
 */
public class PermaSyncHandler {

    public static Set<Integer> boykissers = new HashSet<>();
    public static float[] pollution = new float[PollutionType.values().length];

    public static void writePacket(FriendlyByteBuf buf, Level level, ServerPlayer player) {

        /// TOM IMPACT DATA ///
        TomSaveData data = TomSaveData.forWorld(level);
        buf.writeFloat(data.fire);
        buf.writeFloat(data.dust);
        buf.writeBoolean(data.impact);
        /// TOM IMPACT DATA ///

        /// SHITTY MEMES ///
        List<Integer> ids = new ArrayList<>();
        for (Player p : level.players()) {
            if (p.hasEffect(HbmPotion.DEATH.get())) {
                ids.add(p.getId());
            }
        }
        buf.writeShort((short) ids.size());
        for (Integer i : ids) buf.writeInt(i);
        /// SHITTY MEMES ///

        /// POLLUTION ///
        IPollutionData pollutionData = PollutionHandler.getPollutionData(level, BlockPos.containing(player.getX(), player.getY(), player.getZ()));
        if (pollutionData == null) pollutionData = new PollutionData();
        for (PollutionType type : PollutionType.values()) {
            buf.writeFloat(pollutionData.getPollution(type));
        }
        /// POLLUTION ///
    }

    public static void readPacket(FriendlyByteBuf buf, Player player) {

        /// TOM IMPACT DATA ///
        ImpactWorldHandler.lastSyncLevel = player.level();
        ImpactWorldHandler.fire = buf.readFloat();
        ImpactWorldHandler.dust = buf.readFloat();
        ImpactWorldHandler.impact = buf.readBoolean();
        /// TOM IMPACT DATA ///

        /// SHITTY MEMES ///
        boykissers.clear();
        int ids = buf.readShort();
        for (int i = 0; i < ids; i++) boykissers.add(buf.readInt());
        /// SHITTY MEMES ///

        /// POLLUTION ///
        for (int i = 0; i < PollutionType.values().length; i++) {
            pollution[i] = buf.readFloat();
        }
        /// POLLUTION ///
    }
}
