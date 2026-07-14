package com.hbm.network.client;

import com.hbm.network.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticlePacket extends PacketBase implements PacketBase.DecodablePacket {
    private String mode;
    private int entityId;

    public ParticlePacket() {}

    public ParticlePacket(String mode, int entityId) {
        this.mode = mode;
        this.entityId = entityId;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new ParticlePacket(buf.readUtf(), buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(mode);
        buf.writeInt(entityId);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) return;
            var level = Minecraft.getInstance().level;
            var entity = level.getEntity(this.entityId); // Используем this.entityId вместо msg.entityId
            if (entity == null) return;

            // Получаем направление взгляда игрока
            double lookX = entity.getLookAngle().x;
            double lookY = entity.getLookAngle().y;
            double lookZ = entity.getLookAngle().z;

            // Начальная позиция - рот игрока
            double ix = entity.getX();
            double iy = entity.getY() + entity.getEyeHeight() - 0.2; // немного ниже глаз
            double iz = entity.getZ();

            int count = "blood".equals(this.mode) ? 8 : 5; // Используем this.mode вместо msg.mode

            for (int i = 0; i < count; i++) {
                // Небольшой случайный разброс вокруг направления взгляда
                double spread = 0.3; // угол разброса
                double spreadX = (level.random.nextDouble() - 0.5) * spread;
                double spreadY = (level.random.nextDouble() - 0.5) * spread;
                double spreadZ = (level.random.nextDouble() - 0.5) * spread;

                // Основное направление + случайный разброс
                double dirX = lookX + spreadX;
                double dirY = lookY + spreadY;
                double dirZ = lookZ + spreadZ;

                // Нормализуем вектор направления (чтобы скорость была одинаковой)
                double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                dirX /= length;
                dirY /= length;
                dirZ /= length;

                // Скорость частиц
                double speed = "blood".equals(this.mode) ? 0.3 : 0.2; // Используем this.mode

                // Случайное смещение для начальной позиции (чтобы частицы не из одной точки)
                double offsetX = (level.random.nextDouble() - 0.5) * 0.2;
                double offsetY = (level.random.nextDouble() - 0.5) * 0.2;
                double offsetZ = (level.random.nextDouble() - 0.5) * 0.2;

                if ("blood".equals(this.mode)) { // Используем this.mode
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK,
                                    Blocks.REDSTONE_BLOCK.defaultBlockState()),
                            ix + offsetX, iy + offsetY, iz + offsetZ,
                            dirX * speed, dirY * speed, dirZ * speed);
                } else if ("smoke".equals(this.mode)) { // Используем this.mode
                    level.addParticle(ParticleTypes.SMOKE,
                            ix + offsetX, iy + offsetY, iz + offsetZ,
                            dirX * speed, dirY * speed, dirZ * speed);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}