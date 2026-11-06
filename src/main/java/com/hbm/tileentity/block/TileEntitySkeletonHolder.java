package com.hbm.tileentity.block;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TileEntitySkeletonHolder extends BlockEntity {

    public ItemStack item = ItemStack.EMPTY;

    public TileEntitySkeletonHolder(BlockPos pos, BlockState state) {
        super(ModTileEntity.SKELETON_HOLDER.get(), pos, state);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item.copy();
        sync();
    }

    public boolean hasItem() {
        return !item.isEmpty();
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("item", item.save(new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("item", 10)) {
            item = ItemStack.of(tag.getCompound("item"));
        } else {
            item = ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    public InteractionResult use(Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (item.isEmpty() && !heldItem.isEmpty()) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            this.item = heldItem.copyWithCount(1);
            heldItem.shrink(1);
            sync();
            return InteractionResult.CONSUME;
        } else if (!item.isEmpty() && heldItem.isEmpty()) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.setItemInHand(hand, item.copy());
            this.item = ItemStack.EMPTY;
            sync();
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    public void dropContents(Level level, BlockPos pos) {
        if (!item.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, item);
            item = ItemStack.EMPTY;
            sync();
        }
    }
}