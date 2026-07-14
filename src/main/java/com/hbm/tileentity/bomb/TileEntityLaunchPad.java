package com.hbm.tileentity.bomb;

import com.hbm.blocks.bomb.LaunchPad;
import com.hbm.entity.missile.EntityMissileBaseNT;
import com.hbm.main.MainRegistry;
import com.hbm.util.Library.PosDir;
import com.hbm.network.PacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityLaunchPad extends TileEntityLaunchPadBase {

    private int delay = 0;

    public TileEntityLaunchPad(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public boolean isReadyForLaunch() {
        return delay <= 0;
    }

    @Override
    public double getLaunchOffset() {
        return 1D;
    }

    @Override
    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (delay > 0) delay--;

            if (!isMissileValid() || !hasFuel()) {
                delay = 100;
            }

            if (!hasFuel() || !isMissileValid()) {
                state = STATE_MISSING;
            } else {
                if (delay > 0) {
                    state = STATE_LOADING;
                } else {
                    state = STATE_READY;
                }
            }
        } else {
            AABB box = new AABB(
                    worldPosition.getX() - 0.5, worldPosition.getY(), worldPosition.getZ() - 0.5,
                    worldPosition.getX() + 1.5, worldPosition.getY() + 10, worldPosition.getZ() + 1.5
            );

            List<EntityMissileBaseNT> entities = level.getEntitiesOfClass(EntityMissileBaseNT.class, box);

            if (!entities.isEmpty()) {
                for (int i = 0; i < 15; i++) {
                    Direction dir = this.getBlockState().getValue(LaunchPad.FACING);
                    if (level.random.nextBoolean()) dir = dir.getOpposite();
                    if (level.random.nextBoolean()) dir = dir.getClockWise();

                    float moX = (float) ((level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepX());
                    float moZ = (float) ((level.random.nextGaussian() * 0.15F + 0.75) * dir.getStepZ());

                    CompoundTag data = new CompoundTag();
                    data.putDouble("posX", worldPosition.getX() + 0.5);
                    data.putDouble("posY", worldPosition.getY() + 0.25);
                    data.putDouble("posZ", worldPosition.getZ() + 0.5);
                    data.putString("type", "launchSmoke");
                    data.putDouble("moX", moX);
                    data.putDouble("moY", 0);
                    data.putDouble("moZ", moZ);
                    MainRegistry.proxy.effectNT(data);
                }
            }
        }

        super.tick();
    }

    @Override
    public void finalizeLaunch(Entity missile) {
        super.finalizeLaunch(missile);
        delay = 100;
    }

    @Override
    public PosDir[] getConPos() {
        return new PosDir[]{
                new PosDir(worldPosition.offset(2, 0, -1), Direction.EAST),
                new PosDir(worldPosition.offset(2, 0, 1), Direction.EAST),
                new PosDir(worldPosition.offset(-2, 0, -1), Direction.WEST),
                new PosDir(worldPosition.offset(-2, 0, 1), Direction.WEST),
                new PosDir(worldPosition.offset(-1, 0, 2), Direction.SOUTH),
                new PosDir(worldPosition.offset(1, 0, 2), Direction.SOUTH),
                new PosDir(worldPosition.offset(-1, 0, -2), Direction.NORTH),
                new PosDir(worldPosition.offset(1, 0, -2), Direction.NORTH)
        };
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        delay = nbt.getInt("delay");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("delay", delay);
    }

    private AABB renderBoundingBox = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBoundingBox == null) {
            renderBoundingBox = new AABB(
                    worldPosition.getX() - 2,
                    worldPosition.getY(),
                    worldPosition.getZ() - 2,
                    worldPosition.getX() + 3,
                    worldPosition.getY() + 15,
                    worldPosition.getZ() + 3
            );
        }
        return renderBoundingBox;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }
}