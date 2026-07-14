package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TileEntityMachineTeleporter extends TileEntityLoadedBase implements IEnergyReceiver, IBufPacketReceiver {

    public long power = 0;
    public int targetX = -1;
    public int targetY = -1;
    public int targetZ = -1;
    public int targetDim = 0;
    public static final int MAX_POWER = 1_500_000;
    public static final int CONSUMPTION = 1_000_000;

    public TileEntityMachineTeleporter(BlockPos pos, BlockState state) {
        super(ModTileEntity.TELEPORTER.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            for (Direction dir : Direction.values()) {
                this.trySubscribe(level, worldPosition.relative(dir), dir);
            }

            if (this.targetY != -1) {
                AABB aabb = new AABB(
                        worldPosition.getX() + 0.25, worldPosition.getY(), worldPosition.getZ() + 0.25,
                        worldPosition.getX() + 0.75, worldPosition.getY() + 2, worldPosition.getZ() + 0.75
                );
                List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb);

                if (!entities.isEmpty()) {
                    for (Entity e : entities) {
                        teleport(e);
                    }
                }
            }

            networkPackNT(15);
            setChanged();

        } else {
            if (this.targetY != -1 && power >= CONSUMPTION) {
                double x = worldPosition.getX() + 0.5 + level.random.nextGaussian() * 0.25D;
                double y = worldPosition.getY() + 1 + level.random.nextDouble() * 2D;
                double z = worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 0.25D;
                level.addParticle(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, x, y, z, 0.4F, 0.8F, 1F);
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeLong(power);
        buf.writeInt(targetX);
        buf.writeInt(targetY);
        buf.writeInt(targetZ);
        buf.writeInt(targetDim);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.power = buf.readLong();
        this.targetX = buf.readInt();
        this.targetY = buf.readInt();
        this.targetZ = buf.readInt();
        this.targetDim = buf.readInt();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        power = nbt.getLong("power");
        targetX = nbt.getInt("x1");
        targetY = nbt.getInt("y1");
        targetZ = nbt.getInt("z1");
        targetDim = nbt.getInt("dim");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
        nbt.putInt("x1", targetX);
        nbt.putInt("y1", targetY);
        nbt.putInt("z1", targetZ);
        nbt.putInt("dim", targetDim);
    }

    public void teleport(Entity entity) {
        if (level == null) return;
        if (this.power < CONSUMPTION) return;

        level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 1.5, worldPosition.getZ() + 0.5,
                net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

        if (entity instanceof ServerPlayer player) {
            if (entity.level().dimension().location().hashCode() == this.targetDim) {
                player.teleportTo(this.targetX + 0.5D, this.targetY + 1.5D + entity.getMyRidingOffset(), this.targetZ + 0.5D);
            } else {
                teleportPlayerInterdimensionally(player, this.targetX + 0.5D, this.targetY + 1.5D + entity.getMyRidingOffset(), this.targetZ + 0.5D, this.targetDim);
            }
        } else {
            if (entity.level().dimension().location().hashCode() == this.targetDim) {
                entity.setPos(this.targetX + 0.5D, this.targetY + 1.5D + entity.getMyRidingOffset(), this.targetZ + 0.5D);
                entity.setYRot(entity.getYRot());
                entity.setXRot(entity.getXRot());
            } else {
                teleportEntityInterdimensionally(entity, this.targetX + 0.5D, this.targetY + 1.5D + entity.getMyRidingOffset(), this.targetZ + 0.5D, this.targetDim);
            }
        }

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

        this.power -= CONSUMPTION;
        this.setChanged();
    }

    /** Teleports a player to a different dimension */
    public static boolean teleportPlayerInterdimensionally(ServerPlayer player, double x, double y, double z, int dim) {
        if (player.server == null) return false;

        ServerLevel prevWorld = player.server.getLevel(player.level().dimension());
        ServerLevel newWorld = player.server.getLevel(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                ResourceLocation.parse("minecraft:overworld")
        ));

        // Ищем мир по хешу измерения
        for (ServerLevel level : player.server.getAllLevels()) {
            if (level.dimension().location().hashCode() == dim) {
                newWorld = level;
                break;
            }
        }

        if (newWorld == null) return false;

        // Используем ITeleporter для перемещения
        player.teleportTo(newWorld, x, y, z, player.getYRot(), player.getXRot());
        return true;
    }

    /** Teleports non-player entities to different dimensions */
    public static boolean teleportEntityInterdimensionally(Entity entity, double x, double y, double z, int dim) {
        if (entity.getServer() == null) return false;

        ServerLevel newWorld = entity.getServer().getLevel(entity.level().dimension());

        // Ищем мир по хешу измерения
        for (ServerLevel level : entity.getServer().getAllLevels()) {
            if (level.dimension().location().hashCode() == dim) {
                newWorld = level;
                break;
            }
        }

        if (newWorld == null) return false;

        // Сохраняем сущность и перемещаем
        CompoundTag nbt = new CompoundTag();
        entity.save(nbt);

        entity.remove(Entity.RemovalReason.DISCARDED);

        Entity newEntity = entity.getType().create(newWorld);
        if (newEntity == null) return false;

        newEntity.load(nbt);
        newEntity.setPos(x, y, z);
        newWorld.addFreshEntity(newEntity);

        return true;
    }

    @Override
    public void setPower(long i) {
        power = i;
    }

    @Override
    public long getPower() {
        return power;
    }

    @Override
    public long getMaxPower() {
        return MAX_POWER;
    }

    @Override
    public boolean canConnect(Direction dir) {
        return true; // Подключение со всех сторон
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1,
                worldPosition.getY(),
                worldPosition.getZ() - 1,
                worldPosition.getX() + 2,
                worldPosition.getY() + 3,
                worldPosition.getZ() + 2
        );
    }
}