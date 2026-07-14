package com.hbm.tileentity.block;

import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityBedrockOre extends BlockEntity {

    public ItemStack resource = ItemStack.EMPTY;
    public FluidStackHBM acidRequirement;
    public int tier;
    public int color;
    public int shape;

    public TileEntityBedrockOre(BlockPos pos, BlockState state) {
        super(ModTileEntity.BEDROCK_ORE.get(), pos, state);
    }

    public TileEntityBedrockOre setStyle(int color, int shape) {
        this.color = color;
        this.shape = shape;
        return this;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        if (nbt.contains("resource")) {
            this.resource = ItemStack.of(nbt.getCompound("resource"));
        }

        if (this.resource.isEmpty()) {
            this.resource = new ItemStack(ModItems.POWDER_IRON.get());
        }

        if (nbt.contains("fluid")) {
            FluidTypeHBM type = Fluids.fromID(nbt.getInt("fluid"));
            if (type != Fluids.NONE.get()) {
                this.acidRequirement = new FluidStackHBM(type, nbt.getInt("amount"));
            }
        }

        this.tier = nbt.getInt("tier");
        this.color = nbt.getInt("color");
        this.shape = nbt.getInt("shape");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        if (this.resource != null && !this.resource.isEmpty()) {
            nbt.put("resource", this.resource.save(new CompoundTag()));
        }

        if (this.acidRequirement != null) {
            nbt.putInt("fluid", Fluids.getID(this.acidRequirement.type));
            nbt.putInt("amount", this.acidRequirement.fill);
        }

        nbt.putInt("tier", this.tier);
        nbt.putInt("color", this.color);
        nbt.putInt("shape", this.shape);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        this.load(nbt);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null) {
            this.load(nbt);
            if (color == 0 && !resource.isEmpty()) {
                this.color = MainRegistry.proxy.getStackColor(resource, true);
            }
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }
}
