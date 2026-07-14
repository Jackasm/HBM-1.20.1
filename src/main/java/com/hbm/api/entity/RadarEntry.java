package com.hbm.api.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class RadarEntry {

    /** Name use for radar display, uses I18n for lookup */
    public String unlocalizedName;
    /** The type of dot to show on the radar as well as the redstone level in tier mode */
    public int blipLevel;
    public int posX;
    public int posY;
    public int posZ;
    public int dim;
    public int entityID;
    /** Whether this radar entry should be counted for the redstone output */
    public boolean redstone;

    public RadarEntry() { } //blank ctor for packets

    public RadarEntry(String name, int level, int x, int y, int z, int dim, int entityID, boolean redstone) {
        this.unlocalizedName = name;
        this.blipLevel = level;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.dim = dim;
        this.entityID = entityID;
        this.redstone = redstone;
    }

    public RadarEntry(IRadarDetectableNT detectable, Entity entity, boolean redstone) {
        this(detectable.getUnlocalizedName(), detectable.getBlipLevel(),
                (int) Math.floor(entity.getX()), (int) Math.floor(entity.getY()), (int) Math.floor(entity.getZ()),
                entity.level().dimension().location().hashCode(), entity.getId(), redstone);
    }

    // Удаляем конструктор с IRadarDetectable, так как он deprecated

    public RadarEntry(Player player) {
        this(player.getName().getString(), IRadarDetectableNT.PLAYER,
                (int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()),
                player.level().dimension().location().hashCode(), player.getId(), true);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        this.unlocalizedName = buf.readUtf();
        this.blipLevel = buf.readShort();
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.posZ = buf.readInt();
        this.dim = buf.readShort();
        this.entityID = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.unlocalizedName);
        buf.writeShort(this.blipLevel);
        buf.writeInt(this.posX);
        buf.writeInt(this.posY);
        buf.writeInt(this.posZ);
        buf.writeShort(this.dim);
        buf.writeInt(this.entityID);
    }
}