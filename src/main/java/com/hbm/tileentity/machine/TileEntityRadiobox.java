package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.blocks.machine.BlockRadiobox;
import com.hbm.entity.mob.EntityFBI;
import com.hbm.entity.mob.EntityFBIDrone;
import com.hbm.inventory.container.ContainerRadiobox;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileEntityRadiobox extends TileEntityLoadedBase implements IEnergyReceiver, MenuProvider {

    public long power;
    public static final long MAX_POWER = 500000;
    public boolean infinite = false;

    public TileEntityRadiobox(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADIOBOX.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        updateConnections();

        BlockState state = getBlockState();
        boolean powered = state.getValue(BlockRadiobox.POWERED);

        if (powered && (power >= 25000 || infinite)) {
            if (!infinite) {
                power -= 25000;
                setChanged();
            }

            int range = 15;

            AABB aabb = new AABB(
                    worldPosition.getX() - range, worldPosition.getY() - range, worldPosition.getZ() - range,
                    worldPosition.getX() + range, worldPosition.getY() + range, worldPosition.getZ() + range
            );

            List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb,
                    e -> e instanceof Enemy && !(e instanceof EntityFBI) && !(e instanceof EntityFBIDrone));

            for (Entity entity : entities) {
                entity.hurt(ModDamageSource.createDamageSource(
                        ModDamageSource.ENERVATION, null, null, level), 20.0F);
            }
        }
    }

    private void updateConnections() {
        for (Direction dir : Direction.values()) {
            trySubscribe(level, worldPosition.relative(dir), dir);
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        power = nbt.getLong("power");
        infinite = nbt.getBoolean("infinite");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
        nbt.putBoolean("infinite", infinite);
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
    public long transferPower(long power) {
        long toReceive = Math.min(power, MAX_POWER - this.power);
        this.power += toReceive;
        return power - toReceive;
    }


    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.radiobox");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerRadiobox(id, inv, this);
    }
}