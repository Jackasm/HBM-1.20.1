package com.hbm.tileentity.machine;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.blocks.machine.BlockMachineFan;
import com.hbm.api.block.IBlowable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityFan extends TileEntityLoadedBase {

    public float spin;
    public float prevSpin;
    public boolean falloff = true;
    public boolean suck = false;

    public TileEntityFan(BlockPos pos, BlockState state) {
        super(ModTileEntity.FAN.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;

        this.prevSpin = this.spin;

        BlockState state = getBlockState();
        Direction dir = state.getValue(BlockMachineFan.FACING);

        if (level.hasNeighborSignal(worldPosition)) {
            int range = 10;
            int effRange = 0;
            double push = 0.1;

            for (int i = 1; i <= range; i++) {
                BlockPos checkPos = worldPosition.relative(dir, i);
                BlockState checkState = level.getBlockState(checkPos);
                Block block = checkState.getBlock();
                boolean blowable = block instanceof IBlowable;

                if (checkState.isSolidRender(level, checkPos) || blowable) {
                    if (!level.isClientSide && blowable) {
                        ((IBlowable) block).applyFan(level, checkPos, dir, i);
                    }
                    break;
                }

                effRange = i;
            }

            if (effRange > 0) {
                BlockPos end = worldPosition.relative(dir, effRange);
                AABB box = new AABB(worldPosition).minmax(new AABB(end)).inflate(0.5);

                List<Entity> affected = level.getEntitiesOfClass(Entity.class, box);

                for (Entity e : affected) {
                    double coeff = push;

                    if (falloff) {
                        double dist = e.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
                        dist = Math.sqrt(dist);
                        coeff *= 1.5 * (1 - dist / range / 2);
                    }
                    if (suck) coeff *= -1;

                    e.push(dir.getStepX() * coeff, dir.getStepY() * coeff, dir.getStepZ() * coeff);
                }
            }

            if (level.isClientSide && level.random.nextInt(30) == 0) {
                double speed = suck ? -0.2 : 0.2;
                level.addParticle(ParticleTypes.CLOUD,
                        worldPosition.getX() + 0.5 + dir.getStepX() * 0.5,
                        worldPosition.getY() + 0.5 + dir.getStepY() * 0.5,
                        worldPosition.getZ() + 0.5 + dir.getStepZ() * 0.5,
                        dir.getStepX() * speed,
                        dir.getStepY() * speed,
                        dir.getStepZ() * speed);
            }

            this.spin += 30;
        }

        if (this.spin >= 360) {
            this.prevSpin -= 360;
            this.spin -= 360;
        }

        if (!level.isClientSide) {
            networkPackNT(150);
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.falloff = nbt.getBoolean("falloff");
        this.suck = nbt.getBoolean("suck");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("falloff", falloff);
        nbt.putBoolean("suck", suck);
    }

    @Override
    public void serialize(io.netty.buffer.ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(falloff);
        buf.writeBoolean(suck);
    }

    @Override
    public void deserialize(io.netty.buffer.ByteBuf buf) {
        super.deserialize(buf);
        falloff = buf.readBoolean();
        suck = buf.readBoolean();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 1, worldPosition.getY() - 1, worldPosition.getZ() - 1,
                worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2
        );
    }
}