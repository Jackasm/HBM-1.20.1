package com.hbm.tileentity.network;

import com.hbm.api.fluid.FluidNode;
import com.hbm.api.fluid.IFluidPipe;

import com.hbm.blocks.network.IBlockFluidDuct;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.tileentity.IFluidCopiable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.uninos.UniNodespace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityPipeBaseNT extends TileEntityLoadedBase implements IFluidPipe, IFluidCopiable {

    protected FluidNode node;
    protected FluidTypeHBM type = Fluids.NONE.get();
    protected FluidTypeHBM lastType = Fluids.NONE.get();

    public TileEntityPipeBaseNT(BlockPos pos, BlockState state) {
        super(ModTileEntity.FLUID_DUCT.get(), pos, state);
    }

    public TileEntityPipeBaseNT(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (level == null) return;

        if (level.isClientSide && lastType != type) {
            // Отмечаем блок для обновления рендера
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            lastType = type;
        }

        if (!level.isClientSide) {
            if (this.node == null || this.node.expired) {
                this.node = (FluidNode) UniNodespace.getNode(level, worldPosition, type.getNetworkProvider());

                if (this.node == null || this.node.expired) {
                    this.node = this.createNode(type);
                    UniNodespace.createNode(level, this.node);
                }
            }
        }
    }

    public FluidTypeHBM getFluidType() {
        return this.type;
    }

    @Override
    public boolean setFluidType(FluidTypeHBM type) {
        FluidTypeHBM prev = this.type;
        this.type = type;
        this.setChanged();

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(worldPosition);
        }

        if (level != null) {
            UniNodespace.destroyNode(level, worldPosition, prev.getNetworkProvider());
        }

        if (this.node != null) {
            this.node = null;
        }
        return true;
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && type == this.type;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level != null && !level.isClientSide) {
            if (this.node != null) {
                UniNodespace.destroyNode(level, worldPosition, type.getNetworkProvider());
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            load(pkt.getTag());
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.type = Fluids.fromID(nbt.getInt("type"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("type", Fluids.getID(this.type));
    }

    public boolean isLoaded = true;

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.isLoaded = false;
    }

    @Override
    public int[] getFluidIDToCopy() {
        return new int[]{Fluids.getID(type)};
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return null;
    }

    @Override
    public void pasteSettings(CompoundTag nbt, int index, Level world, Player player, BlockPos pos) {
        int[] ids = nbt.getIntArray("fluidID");
        if (ids.length > 0) {
            int id;
            if (index < ids.length)
                id = ids[index];
            else
                id = 0;

            FluidTypeHBM fluid = Fluids.fromID(id);

            if (HbmPlayerProps.getData(player).getKeyPressed(HbmKeybinds.EnumKeybind.TOOL_CTRL)) {
                IBlockFluidDuct pipe = (IBlockFluidDuct) world.getBlockState(pos).getBlock();
                pipe.changeTypeRecursively(world, pos, getFluidType(), fluid, 64);
            } else {
                this.setFluidType(fluid);
            }
        }
    }
}