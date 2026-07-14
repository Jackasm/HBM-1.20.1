package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.BlockFloodlight;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileEntityFloodlight extends TileEntityLoadedBase implements IEnergyReceiver {

    public float rotation;
    protected final List<BlockPos> lightPos = new ArrayList<>(15);
    public static final long MAX_POWER = 5_000;
    public long power;
    public int delay;
    public boolean isOn;

    public TileEntityFloodlight(BlockPos pos, BlockState state) {
        super(ModTileEntity.FLOODLIGHT.get(), pos, state);
        // Инициализируем список
        for (int i = 0; i < 15; i++) {
            lightPos.add(null);
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        Direction dir = this.getBlockState().getValue(BlockFloodlight.FACING).getOpposite();
        this.trySubscribe(level, worldPosition.relative(dir), dir);

        if (delay > 0) {
            delay--;
            return;
        }

        if (power >= 100) {
            power -= 100;

            if (!isOn) {
                this.isOn = true;
                this.castLights();
                this.setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                networkPackNT(150);
            } else {
                long timer = level.getGameTime();
                if (timer % 5 == 0) {
                    timer = timer / 5;
                    this.castLight((int) Math.abs(timer % this.lightPos.size()));
                }
            }
        } else {
            if (isOn) {
                this.isOn = false;
                this.delay = 60;
                this.destroyLights();
                this.setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                networkPackNT(150);
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeFloat(rotation);
        buf.writeLong(power);
        buf.writeBoolean(isOn);

        // Синхронизация lightPos
        for (BlockPos pos : lightPos) {
            if (pos != null) {
                buf.writeBoolean(true);
                buf.writeInt(pos.getX());
                buf.writeInt(pos.getY());
                buf.writeInt(pos.getZ());
            } else {
                buf.writeBoolean(false);
            }
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.rotation = buf.readFloat();
        this.power = buf.readLong();
        this.isOn = buf.readBoolean();

        // Загрузка lightPos из сети
        for (int i = 0; i < lightPos.size(); i++) {
            if (buf.readBoolean()) {
                int x = buf.readInt();
                int y = buf.readInt();
                int z = buf.readInt();
                lightPos.set(i, new BlockPos(x, y, z));
            } else {
                lightPos.set(i, null);
            }
        }
    }

    private void castLight(int index) {
        BlockPos newPos = this.getRayEndpoint(index);
        BlockPos oldPos = this.lightPos.get(index);
        this.lightPos.set(index, null);

        // Если позиция изменилась или отсутствует – удаляем старый луч
        if (newPos == null || !newPos.equals(oldPos)) {
            if (oldPos != null) {
                BlockEntity tile = Objects.requireNonNull(level).getBlockEntity(oldPos);
                if (tile instanceof TileEntityFloodlightBeam beam && beam.cache == this) {
                    level.setBlock(oldPos, ModBlocks.FLOODLIGHT_BEAM.get().defaultBlockState(), 2);
                }
            }
        }

        if (newPos == null) return;

        // Если блок воздух – ставим новый луч
        if (Objects.requireNonNull(level).getBlockState(newPos).isAir()) {
            level.setBlock(newPos, ModBlocks.FLOODLIGHT_BEAM.get().defaultBlockState(), 2);
            BlockEntity tile = level.getBlockEntity(newPos);
            if (tile instanceof TileEntityFloodlightBeam beam) {
                beam.setSource(this, newPos, index);
            }
            this.lightPos.set(index, newPos);
        }
        // Если на позиции уже есть луч – просто сохраняем ссылку
        else if (level.getBlockState(newPos).getBlock() == ModBlocks.FLOODLIGHT_BEAM.get()) {
            this.lightPos.set(index, newPos);
            BlockEntity tile = level.getBlockEntity(newPos);
            if (tile instanceof TileEntityFloodlightBeam beam) {
                beam.setSource(this, newPos, index);
            }
        }
    }

    public BlockPos getRayEndpoint(int index) {
        if (index < 0 || index >= lightPos.size()) return null;

        Direction facing = this.getBlockState().getValue(BlockFloodlight.FACING);
        float rot = this.rotation;
        Vec3 dir = new Vec3(1, 0, 0);

        float[] angles = getVariation(index);

        // Корректировка угла в зависимости от направления
        if (facing == Direction.DOWN) rot = 180 - rot;
        if (facing == Direction.UP) rot = 180 - rot;

        dir = dir.zRot((float) (rot / 180D * Math.PI) + angles[0]);

        // Вращение в зависимости от направления
        switch (facing) {
            case DOWN -> dir = dir.yRot((float) (Math.PI / 2D));
            case NORTH -> dir = dir.yRot((float) (Math.PI / 2D));
            case SOUTH -> dir = dir.yRot((float) -(Math.PI / 2D));
            case WEST -> dir = dir.yRot((float) (Math.PI));
            case EAST -> {}
            case UP -> {}
        }
        dir = dir.yRot(angles[1]);

        BlockPos lastAir = null;

        for (int i = 1; i < 64; i++) {
            int iX = (int) Math.floor(worldPosition.getX() + 0.5 + dir.x * i);
            int iY = (int) Math.floor(worldPosition.getY() + 0.5 + dir.y * i);
            int iZ = (int) Math.floor(worldPosition.getZ() + 0.5 + dir.z * i);

            if (iX == worldPosition.getX() && iY == worldPosition.getY() && iZ == worldPosition.getZ()) continue;

            BlockPos checkPos = new BlockPos(iX, iY, iZ);
            BlockState state = Objects.requireNonNull(level).getBlockState(checkPos);

            // Если блок воздух или луч – запоминаем и продолжаем
            if (state.isAir() || state.getBlock() == ModBlocks.FLOODLIGHT_BEAM.get()) {
                lastAir = checkPos;
                continue;
            }

            // Нашли препятствие – возвращаем последний проходимый блок (или null)
            return lastAir;
        }

        // Если дошли до конца – возвращаем последний проходимый блок (может быть null)
        return lastAir;
    }

    private void castLights() {
        for (int i = 0; i < this.lightPos.size(); i++) {
            this.castLight(i);
        }
    }

    private void destroyLight(int index) {
        BlockPos pos = lightPos.get(index);
        if (pos != null && Objects.requireNonNull(level).getBlockState(pos).getBlock() == ModBlocks.FLOODLIGHT_BEAM.get()) {
            level.setBlock(pos, ModBlocks.FLOODLIGHT_BEAM.get().defaultBlockState(), 2);
        }
    }

    public void destroyLights() {
        for (int i = 0; i < this.lightPos.size(); i++) {
            destroyLight(i);
        }
    }

    private float[] getVariation(int index) {
        return new float[]{
                ((((float) index / 3) - 2) * 7.5F) / 180F * (float) Math.PI,
                (((index % 3) - 1) * 15F) / 180F * (float) Math.PI
        };
    }



    @Override
    public long getPower() {
        return power;
    }

    @Override
    public void setPower(long power) {
        this.power = power;
    }

    @Override
    public long getMaxPower() {
        return MAX_POWER;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.rotation = nbt.getFloat("rotation");
        this.power = nbt.getLong("power");
        this.isOn = nbt.getBoolean("isOn");

        // Загрузка lightPos
        if (nbt.contains("lightPos")) {
            int[] posArray = nbt.getIntArray("lightPos");
            for (int i = 0; i < Math.min(posArray.length / 3, lightPos.size()); i++) {
                int x = posArray[i * 3];
                int y = posArray[i * 3 + 1];
                int z = posArray[i * 3 + 2];
                if (x != 0 || y != 0 || z != 0) {
                    lightPos.set(i, new BlockPos(x, y, z));
                }
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putFloat("rotation", rotation);
        nbt.putLong("power", power);
        nbt.putBoolean("isOn", isOn);

        // Сохранение lightPos
        int[] posArray = new int[lightPos.size() * 3];
        for (int i = 0; i < lightPos.size(); i++) {
            BlockPos pos = lightPos.get(i);
            if (pos != null) {
                posArray[i * 3] = pos.getX();
                posArray[i * 3 + 1] = pos.getY();
                posArray[i * 3 + 2] = pos.getZ();
            }
        }
        nbt.putIntArray("lightPos", posArray);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1,
                worldPosition.getY() - 1,
                worldPosition.getZ() - 1,
                worldPosition.getX() + 2,
                worldPosition.getY() + 2,
                worldPosition.getZ() + 2
        );
    }
}