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

public class CargoContainerExtractPacket extends PacketBase implements PacketBase.DecodablePacket {

    private BlockPos pos;
    private int slotIndex;
    private boolean shift;

    public CargoContainerExtractPacket() {}

    public CargoContainerExtractPacket(BlockPos pos, int slotIndex, boolean shift) {
        this.pos = pos;
        this.slotIndex = slotIndex;
        this.shift = shift;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new CargoContainerExtractPacket(buf.readBlockPos(), buf.readInt(), buf.readBoolean());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slotIndex);
        buf.writeBoolean(shift);
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
            ItemStack slotStack = container.getItemHandler().getStackInSlot(slotIndex);

            if (shift) {
                // Shift+клик - берём до максимального стака, но с учётом места в руке
                int maxStackSize = slotStack.getMaxStackSize();
                int currentInHand = carried.isEmpty() ? 0 : carried.getCount();
                int spaceInHand = maxStackSize - currentInHand;

                if (spaceInHand > 0) {
                    long currentCount = container.getItemHandler().getRealCount(slotIndex);
                    int toExtract = (int) Math.min(spaceInHand, currentCount);

                    if (toExtract > 0) {
                        ItemStack extracted = container.getItemHandler().extractItem(slotIndex, toExtract, false);
                        if (!extracted.isEmpty()) {
                            if (carried.isEmpty()) {
                                player.containerMenu.setCarried(extracted);
                            } else {
                                carried.grow(extracted.getCount());
                                player.containerMenu.setCarried(carried);
                            }
                            player.containerMenu.broadcastChanges();
                        }
                    }
                }
            } else {
                // Обычный клик - берём 1 предмет
                long currentCount = container.getItemHandler().getRealCount(slotIndex);
                if (currentCount > 0) {
                    ItemStack extracted = container.getItemHandler().extractItem(slotIndex, 1, false);
                    if (!extracted.isEmpty()) {
                        if (carried.isEmpty()) {
                            player.containerMenu.setCarried(extracted);
                        } else if (ItemStack.isSameItemSameTags(carried, extracted) &&
                                carried.getCount() + extracted.getCount() <= carried.getMaxStackSize()) {
                            carried.grow(extracted.getCount());
                            player.containerMenu.setCarried(carried);
                        } else {
                            container.getItemHandler().insertItem(slotIndex, extracted, false);
                        }
                        player.containerMenu.broadcastChanges();
                    }
                }
            }
        }
    }
}