package com.hbm.util;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.energy.IEnergyConnector;
import com.hbm.api.energy.IEnergyConnectorBlock;
import com.hbm.api.fluid.IFluidConnector;
import com.hbm.api.fluid.IFluidConnectorBlock;
import com.hbm.entity.mob.EntityHunterChopper;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.ModItems;
import com.hbm.tileentity.TileEntityProxyBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;

public class Library {

    public static boolean isObstructed(Level level, double x, double y, double z, double a, double b, double c) {
        Vec3 start = new Vec3(x, y, z);
        Vec3 end = new Vec3(a, b, c);

        // Используем ClipContext для проверки коллизий
        ClipContext context = new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,  // проверяем коллизии блоков
                ClipContext.Fluid.NONE,       // не проверяем жидкости
                null                          // сущность не важна для этого контекста
        );

        BlockHitResult hit = level.clip(context);
        return hit.getType() != HitResult.Type.MISS;
    }

    public record PosDir(BlockPos pos, Direction dir) {
    }

    /**
     * Проверяет, может ли труба соединиться с соседним блоком
     * @param level Мир
     * @param pos Позиция соседнего блока
     * @param dir Направление от трубы к соседу (сторона трубы)
     * @param type Тип жидкости
     * @return true если соединение возможно
     */
    public static boolean canConnectFluid(BlockGetter level, BlockPos pos, Direction dir, FluidTypeHBM type) {

        if (pos.getY() > level.getMaxBuildHeight() || pos.getY() < level.getMinBuildHeight())
            return false;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Проверяем блок (если он реализует интерфейс)
        if (block instanceof IFluidConnectorBlock con) {
            if (con.canConnect(type, level, pos, dir.getOpposite())) {
                return true;
            }
        }

        // Проверяем TileEntity
        BlockEntity te = level.getBlockEntity(pos);

        if (te instanceof TileEntityProxyBase proxy && proxy.isExtra()) {
            return true; // это extra-блок, можно соединяться
        }

        if (te instanceof IFluidConnector con) {
            return con.canConnect(type, dir.getOpposite());
        }

        return false;
    }

    public static long chargeTEFromItems(IItemHandler inventory, int index, long power, long maxPower) {

        ItemStack stack = inventory.getStackInSlot(index);

        if (!stack.isEmpty() && stack.getItem() == ModItems.BATTERY_CREATIVE.get())
            return maxPower;

        if (!stack.isEmpty() && stack.getItem() instanceof IBatteryItem battery) {
            long batCharge = battery.getCharge(stack);
            long batRate = battery.getDischargeRate();
            long toDischarge = Math.min(Math.min((maxPower - power), batRate), batCharge);

            if (toDischarge > 0) {
                battery.dischargeBattery(stack, toDischarge);
                power += toDischarge;

                // Обновляем слот, если предмет изменился
                if (inventory instanceof net.minecraftforge.items.IItemHandlerModifiable modifiable) {
                    modifiable.setStackInSlot(index, stack);
                }
            }
        }

        return power;
    }

    public static long chargeItemsFromTE(IItemHandler inventory, int index, long power, long maxPower) {
        if (power < 0) return 0;
        if (power > maxPower) return maxPower;

        ItemStack stack = inventory.getStackInSlot(index);

        if (stack.isEmpty() || stack.getItem() == ModItems.BATTERY_CREATIVE.get()) {
            return power;
        }

        if (stack.getItem() instanceof IBatteryItem battery) {
            long batMax = battery.getMaxCharge(stack);
            long batCharge = battery.getCharge(stack);
            long batRate = battery.getChargeRate();
            long toCharge = Math.min(Math.min(power, batRate), batMax - batCharge);

            power -= toCharge;
            battery.chargeBattery(stack, toCharge);

            // Обновляем слот, если предмет изменился
            if (inventory instanceof net.minecraftforge.items.IItemHandlerModifiable modifiable) {
                modifiable.setStackInSlot(index, stack);
            }
        }

        return power;
    }

    public static boolean canConnect(BlockGetter level, BlockPos pos, Direction dir) {
        if (pos.getY() > level.getMaxBuildHeight() || pos.getY() < level.getMinBuildHeight())
            return false;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IEnergyConnectorBlock connectorBlock) {
            if (connectorBlock.canConnect(level, pos, dir.getOpposite()))
                return true;
        }

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof IEnergyConnector connector) {
            return connector.canConnect(dir.getOpposite());
        }

        return false;
    }

    public static LivingEntity getClosestEntityForChopper(Level level, double x, double y, double z, double radius) {
        double closestDist = -1.0D;
        LivingEntity closestEntity = null;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius))) {

            if (entity instanceof EntityHunterChopper) continue;
            if (!entity.isAlive()) continue;
            if (entity instanceof Player player && player.getAbilities().invulnerable) continue;

            double dist = entity.distanceToSqr(x, y, z);
            double effectiveRadius = radius;

            if (entity.isShiftKeyDown()) {
                effectiveRadius = radius * 0.8D;
            }

            if ((closestDist == -1.0D || dist < closestDist) && (effectiveRadius < 0.0D || dist < effectiveRadius * effectiveRadius)) {
                closestDist = dist;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }
}
