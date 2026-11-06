package com.hbm.tileentity.deco;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileEntityLoot extends BlockEntity {

    public final List<LootEntry> items = new ArrayList<>();

    public TileEntityLoot(BlockPos pos, BlockState state) {
        super(ModTileEntity.LOOT.get(), pos, state);
    }

    public TileEntityLoot addItem(ItemStack stack, double x, double y, double z) {
        items.add(new LootEntry(stack, x, y, z));
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return this;
    }

    public List<LootEntry> getItems() {
        return items;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        items.clear();

        int count = nbt.getInt("count");

        for (int i = 0; i < count; i++) {
            CompoundTag stackTag = nbt.getCompound("item" + i);
            ItemStack stack = ItemStack.of(stackTag);
            if (stack == null || stack.isEmpty()) continue;

            double x = nbt.getDouble("x" + i);
            double y = nbt.getDouble("y" + i);
            double z = nbt.getDouble("z" + i);
            items.add(new LootEntry(stack, x, y, z));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("count", items.size());

        for (int i = 0; i < items.size(); i++) {
            LootEntry entry = items.get(i);
            if (entry == null || entry.stack().isEmpty()) continue;

            CompoundTag stackTag = new CompoundTag();
            entry.stack().save(stackTag);
            nbt.put("item" + i, stackTag);
            nbt.putDouble("x" + i, entry.x());
            nbt.putDouble("y" + i, entry.y());
            nbt.putDouble("z" + i, entry.z());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) load(tag);
    }

    public record LootEntry(ItemStack stack, double x, double y, double z) {}
}
