package com.hbm.network.server;

import com.hbm.config.MobConfig;
import com.hbm.entity.mob.EntityDuck;
import com.hbm.network.PacketBase;
import com.hbm.tileentity.machine.TileEntityHeaterOilburner;
import com.hbm.tileentity.storage.TileEntityBarrel;
import com.hbm.tileentity.storage.TileEntityMachineBattery;
import com.hbm.tileentity.storage.TileEntityMachineFluidTank;
import com.hbm.tileentity.turret.TileEntityTurretSentry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AuxButtonPacket extends PacketBase implements PacketBase.DecodablePacket {

    private BlockPos pos;
    private int button;
    private int data;

    public AuxButtonPacket() {}

    public AuxButtonPacket(BlockPos pos, int button, int data) {
        this.pos = pos;
        this.button = button;
        this.data = data;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new AuxButtonPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(button);
        buf.writeInt(data);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            Level level = player.level();

            if (button == 999) {
                handleDuckLogic(player);
                return;
            }

            if (level.hasChunkAt(pos)) {
                BlockEntity te = level.getBlockEntity(pos);
                if (te instanceof TileEntityBarrel barrel) {
                    barrel.handleButtonPacket();
                }
                if (te instanceof TileEntityMachineFluidTank tank) {
                    tank.handleButtonPacket(button, data);
                }
                if (te instanceof TileEntityHeaterOilburner oilburner) {
                    oilburner.handleButtonPacket(button, data);
                }
                if (te instanceof TileEntityTurretSentry turret) {
                    turret.handleButtonPacket(button, data);
                }
                if (te instanceof TileEntityMachineBattery battery) {
                    battery.handleButtonPacket(button, data);
                }
            }
        }
    }

    private void handleDuckLogic(ServerPlayer player) {
        if (!MobConfig.enableDucks) return;

        var persistentData = player.getPersistentData();
        if (persistentData.getBoolean("hasDucked")) return;

        EntityDuck duck = new EntityDuck(player.level());
        duck.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        Vec3 look = player.getLookAngle();
        duck.setDeltaMovement(look.x, look.y, look.z);

        player.level().addFreshEntity(duck);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CHICKEN_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);

        persistentData.putBoolean("hasDucked", true);
    }
}