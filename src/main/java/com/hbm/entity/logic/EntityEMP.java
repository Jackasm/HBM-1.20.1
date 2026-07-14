package com.hbm.entity.logic;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.IEnergyProvider;
import com.hbm.entity.ModEntities;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.ParticleBurstPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

public class EntityEMP extends Entity {

    private List<BlockPos> machines;
    private final int life = 10 * 60 * 20;

    public EntityEMP(EntityType<? extends EntityEMP> entityType, Level level) {
        super(entityType, level);
    }

    public EntityEMP(Level level) {
        this(ModEntities.EMP.get(), level);
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (machines == null) {
                allocate();
            } else {
                shock();
            }

            if (this.tickCount > life) {
                this.discard();
            }
        }
    }

    private void allocate() {
        machines = new ArrayList<>();
        int radius = 100;

        for (int x = -radius; x <= radius; x++) {
            int x2 = x * x;
            for (int y = -radius; y <= radius; y++) {
                int y2 = y * y;
                for (int z = -radius; z <= radius; z++) {
                    int z2 = z * z;
                    if (Math.sqrt(x2 + y2 + z2) <= radius) {
                        add(BlockPos.containing(getX() + x, getY() + y, getZ() + z));
                    }
                }
            }
        }
    }

    private void shock() {
        for (BlockPos pos : machines) {
            emp(pos);
        }
    }

    private void add(BlockPos pos) {
        BlockEntity te = level().getBlockEntity(pos);

        if (te instanceof IEnergyHandler) {
            machines.add(pos);
        } else if (te instanceof IEnergyProvider) {
            machines.add(pos);
        }
    }

    private void emp(BlockPos pos) {
        BlockEntity te = level().getBlockEntity(pos);
        boolean flag = false;

        if (te instanceof IEnergyHandler) {
            ((IEnergyHandler) te).setPower(0);
            flag = true;
        }

        if (te instanceof IEnergyProvider provider) {
            long stored = provider.getPower();
            if (stored > 0) {
                provider.usePower(stored);
            }
        }

        if (flag && random.nextInt(20) == 0) {
            PacketDispatcher.sendToAllAround(
                    new ParticleBurstPacket(pos,
                            Blocks.GLASS, 3),
                    level(), BlockPos.containing(getX(), getY(), getZ()), 50
            );
        }
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        // Нет данных для загрузки
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        // Нет данных для сохранения
    }
}