package com.hbm.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.IProxyController;
import com.hbm.util.Compat;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityProxyBase extends TileEntityLoadedBase {

    private BlockPos cachedPosition;
    private boolean extra = false;

    public TileEntityProxyBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockEntity getTE() {
        if (level == null) return null;

        if (cachedPosition != null) {
            BlockEntity te = Compat.getTileStandard(level, cachedPosition);
            if (te != null && !(te instanceof TileEntityProxyBase)) {
                return te;
            }
            cachedPosition = null;
            setChanged();
        }

        if (getBlockState().getBlock() instanceof BlockDummyable dummy) {
            BlockPos pos = dummy.findCore(level, worldPosition);
            if (pos != null) {
                BlockEntity te = Compat.getTileStandard(level, pos);
                if (te != null && !(te instanceof TileEntityProxyBase)) {
                    return te;
                }
            }
        }

        if (getBlockState().getBlock() instanceof IProxyController controller) {
            BlockEntity tile = controller.getCore(level, worldPosition);
            if (tile != null && !(tile instanceof TileEntityProxyBase)) {
                return tile;
            }
        }

        return null;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.getBoolean("hasPos")) {
            cachedPosition = new BlockPos(nbt.getInt("pX"), nbt.getInt("pY"), nbt.getInt("pZ"));
        }
        this.extra = nbt.getBoolean("extra");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (cachedPosition != null) {
            nbt.putBoolean("hasPos", true);
            nbt.putInt("pX", cachedPosition.getX());
            nbt.putInt("pY", cachedPosition.getY());
            nbt.putInt("pZ", cachedPosition.getZ());
        }
        nbt.putBoolean("extra", extra);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            // Запланировать проверку через 1 секунду после загрузки
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 20);
        }
    }

    public void setExtra(boolean extra) {
        this.extra = extra;
        setChanged();
        if (level != null && !level.isClientSide) {
            networkPackNT(150);
        }
    }

    public boolean isExtra() {
        return extra;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(extra);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.extra = buf.readBoolean();
    }
}