package com.hbm.network.client;

import com.hbm.network.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ParticleBurstPacket extends PacketBase implements PacketBase.DecodablePacket {

    private BlockPos pos;
    private Block block;
    private int meta;

    public ParticleBurstPacket() {}

    public ParticleBurstPacket(BlockPos pos, Block block, int meta) {
        this.pos = pos;
        this.block = block;
        this.meta = meta;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new ParticleBurstPacket(buf.readBlockPos(),
                ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation()),
                buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(block));
        buf.writeInt(meta);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level == null) return;
            var level = Minecraft.getInstance().level;

            if (block != null) {
                BlockState state = block.defaultBlockState();
                // В 1.20.1 эффект разрушения блока
                for (int i = 0; i < 4; i++) {
                    double x = pos.getX() + level.random.nextDouble();
                    double y = pos.getY() + level.random.nextDouble();
                    double z = pos.getZ() + level.random.nextDouble();
                    double speedX = (level.random.nextDouble() - 0.5) * 0.5;
                    double speedY = (level.random.nextDouble() - 0.5) * 0.5;
                    double speedZ = (level.random.nextDouble() - 0.5) * 0.5;

                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                            x, y, z, speedX, speedY, speedZ);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}