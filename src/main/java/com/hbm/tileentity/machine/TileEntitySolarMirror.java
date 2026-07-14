package com.hbm.tileentity.machine;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityTickingBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class TileEntitySolarMirror extends TileEntityTickingBase {

    public int tX;
    public int tY;
    public int tZ;
    public boolean isOn;

    public TileEntitySolarMirror(BlockPos pos, BlockState state) {
        super(ModTileEntity.SOLAR_MIRROR.get(), pos, state);
    }

    @Override
    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (level.getGameTime() % 20 == 0) {
                this.networkPackNT(200);
            }

            if (tY < worldPosition.getY()) {
                isOn = false;
                return;
            }

            int sun = level.getBrightness(net.minecraft.world.level.LightLayer.SKY, worldPosition)
                    - level.getSkyDarken() - 11;

            if (sun <= 0 || !level.canSeeSky(worldPosition.above())) {
                isOn = false;
                return;
            }

            isOn = true;

            BlockEntity te = level.getBlockEntity(new BlockPos(tX, tY - 1, tZ));

            if (te instanceof TileEntitySolarBoiler boiler) {
                boiler.heat += sun;
            }
        } else {
            BlockEntity te = level.getBlockEntity(new BlockPos(tX, tY - 1, tZ));

            if (isOn && te instanceof TileEntitySolarBoiler boiler) {
                boiler.primary.add(new BlockPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()));
            }

            if (level.getGameTime() % 20 == 0) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public String getInventoryName() {
        return "container.solarMirror";
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeInt(this.tX);
        buf.writeInt(this.tY);
        buf.writeInt(this.tZ);
        buf.writeBoolean(this.isOn);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.tX = buf.readInt();
        this.tY = buf.readInt();
        this.tZ = buf.readInt();
        this.isOn = buf.readBoolean();
    }

    public void setTarget(int x, int y, int z) {
        tX = x;
        tY = y;
        tZ = z;
        this.setChanged();
        this.networkPackNT(200);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        tX = nbt.getInt("targetX");
        tY = nbt.getInt("targetY");
        tZ = nbt.getInt("targetZ");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("targetX", tX);
        nbt.putInt("targetY", tY);
        nbt.putInt("targetZ", tZ);
    }

    private AABB renderBoundingBox = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBoundingBox == null) {
            renderBoundingBox = new AABB(
                    worldPosition.getX() - 25,
                    worldPosition.getY() - 25,
                    worldPosition.getZ() - 25,
                    worldPosition.getX() + 25,
                    worldPosition.getY() + 25,
                    worldPosition.getZ() + 25
            );
        }
        return renderBoundingBox;
    }
}