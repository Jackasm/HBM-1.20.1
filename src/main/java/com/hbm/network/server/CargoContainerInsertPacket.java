package com.hbm.network.server;

import com.hbm.inventory.container.ContainerCargoContainer;
import com.hbm.network.PacketBase;
import com.hbm.tileentity.machine.TileEntityCargoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CargoContainerInsertPacket extends PacketBase implements PacketBase.DecodablePacket {

    private BlockPos pos;
    private int amount; // количество для вставки (0 = весь стак)

    public CargoContainerInsertPacket() {}

    public CargoContainerInsertPacket(BlockPos pos, int amount) {
        this.pos = pos;
        this.amount = amount;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new CargoContainerInsertPacket(buf.readBlockPos(), buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(amount);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) return;

        Level level = player.level();
        if (!level.hasChunkAt(pos)) return;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityCargoContainer container)) return;

        if (player.containerMenu instanceof ContainerCargoContainer menu && menu.getTile() == container) {
            ItemStack carried = player.containerMenu.getCarried();
            if (!carried.isEmpty()) {
                ItemStack toInsert = carried.copy();
                if (amount > 0) {
                    toInsert.setCount(Math.min(amount, toInsert.getCount()));
                }
                ItemStack remaining = container.getItemHandler().insertItem(0, toInsert, false);

                if (remaining.getCount() != toInsert.getCount()) {
                    int removed = toInsert.getCount() - remaining.getCount();
                    carried.shrink(removed);
                    if (carried.isEmpty()) {
                        player.containerMenu.setCarried(ItemStack.EMPTY);
                    } else {
                        player.containerMenu.setCarried(carried);
                    }
                    player.containerMenu.broadcastChanges();
                }
            }
        }
    }
}