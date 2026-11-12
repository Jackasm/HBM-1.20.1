package com.hbm.network.client;

import com.hbm.network.PacketBase;
import com.hbm.tileentity.bomb.TileEntityLaunchPadBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * One-shot, client-bound state of the missile rendered ON a launch pad.
 *
 * Deliberately separate from the 50-block BufPacket that contains power,
 * fluid tanks and machine state. This tiny packet is sent ONLY when the
 * inventory's missile slot changes and never every tick.
 *
 * A client which has no chunk here cannot render the pad anyway. Clients
 * starting to track a loaded chunk use the vanilla getUpdateTag() fallback.
 */
public final class LaunchPadVisualPacket extends PacketBase implements PacketBase.DecodablePacket {

    private BlockPos pos;
    private int missileItemId;

    public LaunchPadVisualPacket() {}

    public LaunchPadVisualPacket(BlockPos pos, ItemStack missile) {
        this.pos = pos;
        this.missileItemId = missile.isEmpty() ? 0 : Item.getId(missile.getItem());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(missileItemId);
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        missileItemId = buf.readVarInt();
        return this;
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    public int getMissileItemId() {
        return missileItemId;
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::applyOnClient));
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void applyOnClient() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !minecraft.level.hasChunkAt(pos)) return;

        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        if (!(blockEntity instanceof TileEntityLaunchPadBase launchPad)) return;

        Item item = missileItemId <= 0 ? Items.AIR : Item.byId(missileItemId);
        launchPad.toRender = item == Items.AIR ? null : new ItemStack(item);
    }
}
