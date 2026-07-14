package com.hbm.tileentity.turret;

import com.hbm.api.energy.IEnergyReceiver;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.handler.CasingEjector;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemTurretBiometry;
import com.hbm.items.weapon.sedna.BulletConfig;

import com.hbm.particle.SpentCasing;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Абстрактный базовый класс для турелей.
 * В 1.20.1: наследуется от TileEntityLoadedBase (который уже содержит networkPackNT и работу с синхронизацией)
 * Реализует IEnergyReceiver (энергия Forge Energy / HBM Energy)
 */
public abstract class TileEntityTurretBaseNT extends TileEntityLoadedBase implements IEnergyReceiver {

    // Инвентарь: 0 - чип, 1-9 - боеприпасы, 10 - батарея
    protected final ItemStackHandler inventory = new ItemStackHandler(11) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> handlerCap = LazyOptional.of(() -> inventory);

    // Данные турели
    public long power;
    public boolean isOn = false;
    public int stattrak = 0;                 // счётчик убийств
    public int searchTimer = 0;
    public Entity target = null;
    public Vec3 tPos = null;
    public boolean aligned = false;

    // Углы поворота (в радианах)
    public double rotationYaw = 0;
    public double rotationPitch = 0;
    // Для клиентской интерполяции
    public double lastRotationYaw = 0;
    public double lastRotationPitch = 0;
    public double syncRotationYaw = 0;
    public double syncRotationPitch = 0;
    protected int turnProgress = 0;

    // Флаги целей (переопределяются в GUI)
    public boolean targetPlayers = false;
    public boolean targetAnimals = false;
    public boolean targetMobs = true;
    public boolean targetMachines = true;

    // Задержки
    public int casingDelay = 0;
    protected SpentCasing cachedCasingConfig = null;

    // Конструктор
    public TileEntityTurretBaseNT(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== Абстрактные методы ====================
    public abstract void updateFiringTick();
    protected abstract List<Integer> getAmmoList();
    protected abstract Vec3 getHorizontalOffset();
    protected abstract double getHeightOffset();
    protected abstract double getBarrelLength();
    protected abstract boolean usesCasings();
    public abstract CasingEjector getEjector();
    protected abstract int casingDelay();
    protected abstract void spawnCasing();   // реализуется в конкретной турели
    protected abstract Vec3 getCasingSpawnPos();
    protected abstract boolean entityAcceptableTarget(Entity e);
    public abstract long getMaxPower();
    protected abstract long getConsumption();
    protected abstract int getDecetorInterval();
    protected abstract double getDecetorRange();
    protected abstract double getDecetorGrace();
    protected abstract double getTurretYawSpeed();
    protected abstract double getTurretPitchSpeed();
    protected abstract double getTurretDepression();  // опускание вниз (положительный угол)
    protected abstract double getTurretElevation();  // подъём вверх
    protected abstract double getAcceptableInaccuracy();
    protected abstract boolean hasThermalVision();
    public abstract boolean hasPower();
    public abstract boolean isOn();

    // ==================== Базовые геттеры ====================
    public Component getDisplayName() {
        return Component.translatable(getName());
    }
    public abstract String getName();

    // Получение первого загруженного боеприпаса из инвентаря
    public BulletConfig getFirstConfigLoaded() {
        List<Integer> list = getAmmoList();
        if (list == null || list.isEmpty()) return null;

        for (int i = 1; i <= 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                for (Integer id : list) {
                    BulletConfig cfg = BulletConfig.configs.get(id);
                    // Сравниваем ItemStack с ammo конфига
                    if (cfg != null && cfg.ammo != null && ItemStack.isSameItem(cfg.ammo, stack)) {
                        return cfg;
                    }
                }
            }
        }
        return null;
    }

    // Выстрел пулей
    public void spawnBullet(BulletConfig bullet, float baseDamage) {
        this.cachedCasingConfig = bullet.casing;
        Vec3 pos = getTurretPos();
        Vec3 vec = new Vec3(getBarrelLength(), 0, 0);
        vec = vec.xRot((float) -rotationPitch);
        vec = vec.yRot((float) -(rotationYaw + Math.PI * 0.5));

        EntityBulletBaseMK4 proj = new EntityBulletBaseMK4(level, bullet, baseDamage, bullet.spread, (float) rotationYaw, (float) rotationPitch);
        proj.setPos(pos.x + vec.x, pos.y + vec.y, pos.z + vec.z);
        Objects.requireNonNull(level).addFreshEntity(proj);

        if (usesCasings()) {
            if (casingDelay() == 0) {
                spawnCasing();
            } else {
                casingDelay = casingDelay();
            }
        }
    }

    // Расход боеприпаса
    protected void conusmeAmmo(BulletConfig config) {
        for (int i = 1; i <= 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && config.ammo != null &&
                    config.ammo.getItem() == stack.getItem() &&
                    config.ammo.getDamageValue() == stack.getDamageValue()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                setChanged();
                break;
            }
        }
    }

    // ==================== Работа со списком имён (чип) ====================
    public List<String> getWhitelist() {
        ItemStack chip = inventory.getStackInSlot(0);
        if (!chip.isEmpty() && chip.getItem() == ModItems.TURRET_CHIP.get()) {
            String[] array = ItemTurretBiometry.getNames(chip);
            if (array == null) return null;
            return Arrays.asList(array);
        }
        return null;
    }

    public void addName(String name) {
        ItemStack chip = inventory.getStackInSlot(0);
        if (!chip.isEmpty() && chip.getItem() == ModItems.TURRET_CHIP.get()) {
            ItemTurretBiometry.addName(chip, name);
        }
    }

    public void removeName(int index) {
        ItemStack chip = inventory.getStackInSlot(0);
        if (!chip.isEmpty() && chip.getItem() == ModItems.TURRET_CHIP.get()) {
            String[] array = ItemTurretBiometry.getNames(chip);
            if (array == null) return;
            List<String> names = new ArrayList<>(Arrays.asList(array));
            ItemTurretBiometry.clearNames(chip);
            names.remove(index);
            for (String name : names) ItemTurretBiometry.addName(chip, name);
        }
    }

    // ==================== Поиск цели ====================
    protected void seekNewTarget() {
        Vec3 pos = getTurretPos();
        double range = getDecetorRange();
        AABB aabb = new AABB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z).inflate(range);
        List<Entity> entities = Objects.requireNonNull(level).getEntitiesOfClass(Entity.class, aabb);
        Entity best = null;
        double bestDist = range;
        for (Entity e : entities) {
            Vec3 entPos = getEntityPos(e);
            double dist = entPos.distanceTo(pos);
            if (dist > range) continue;
            if (!entityAcceptableTarget(e)) continue;
            if (!entityInLOS(e)) continue;
            if (dist < bestDist) {
                bestDist = dist;
                best = e;
            }
        }
        target = best;
        if (target != null) tPos = getEntityPos(target);
    }

    // Проверка прямой видимости и угла
    public boolean entityInLOS(Entity e) {
        if (e.isRemoved()) return false;

        // Проверка невидимости
        if (!hasThermalVision() && e instanceof LivingEntity living && living.hasEffect(net.minecraft.world.effect.MobEffects.INVISIBILITY))
            return false;

        Vec3 pos = getTurretPos();
        Vec3 ent = getEntityPos(e);
        Vec3 delta = ent.subtract(pos);
        double length = delta.length();
        if (length < getDecetorGrace() || length > getDecetorRange() * 1.1) return false;

        delta = delta.normalize();
        double pitch = Math.asin(delta.y);
        double pitchDeg = Math.toDegrees(pitch);
        if (pitchDeg < -getTurretDepression() || pitchDeg > getTurretElevation()) return false;

        // Проверка на препятствия - используем RayTrace
        BlockHitResult hitResult = Objects.requireNonNull(level).clip(new ClipContext(pos, ent, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));

        // Если луч упёрся в блок, и этот блок находится до цели - есть препятствие
        if (hitResult.getType() != HitResult.Type.MISS) {
            Vec3 hitPos = hitResult.getLocation();
            double hitDist = hitPos.distanceTo(pos);
            // Если препятствие ближе, чем цель - цель не видна
            return hitDist >= length - 0.5; // небольшая погрешность
        }

        return true;
    }

    // Поворот турели в сторону цели
    protected void alignTurret() {
        if (tPos != null) turnTowards(tPos);
    }

    public void turnTowards(Vec3 worldPos) {
        Vec3 pos = getTurretPos();
        Vec3 delta = worldPos.subtract(pos);
        double targetPitch = Math.asin(delta.y / delta.length());
        double targetYaw = -Math.atan2(delta.x, delta.z);
        turnTowardsAngle(targetPitch, targetYaw);
    }

    public void turnTowardsAngle(double targetPitch, double targetYaw) {
        double turnYaw = Math.toRadians(getTurretYawSpeed());
        double turnPitch = Math.toRadians(getTurretPitchSpeed());
        double pi2 = Math.PI * 2;

        // Обработка pitch
        if (Math.abs(rotationPitch - targetPitch) < turnPitch || Math.abs(rotationPitch - targetPitch) > pi2 - turnPitch) {
            rotationPitch = targetPitch;
        } else {
            rotationPitch += (targetPitch > rotationPitch) ? turnPitch : -turnPitch;
        }

        // Обработка yaw с учётом кратчайшего пути
        double deltaYaw = (targetYaw - rotationYaw) % pi2;
        int dir = 0;
        if (deltaYaw < -Math.PI) dir = 1;
        else if (deltaYaw < 0) dir = -1;
        else if (deltaYaw > Math.PI) dir = -1;
        else if (deltaYaw > 0) dir = 1;

        if (Math.abs(rotationYaw - targetYaw) < turnYaw || Math.abs(rotationYaw - targetYaw) > pi2 - turnYaw) {
            rotationYaw = targetYaw;
        } else {
            rotationYaw += turnYaw * dir;
        }

        // Проверка выравнивания
        double deltaPitch = targetPitch - rotationPitch;
        deltaYaw = targetYaw - rotationYaw;
        double deltaAngle = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
        rotationYaw %= pi2;
        rotationPitch %= pi2;
        if (deltaAngle <= Math.toRadians(getAcceptableInaccuracy())) aligned = true;
    }

    // ==================== Позиции ====================
    public Vec3 getTurretPos() {
        Vec3 offset = getHorizontalOffset();
        return new Vec3(worldPosition.getX() + offset.x, worldPosition.getY() + getHeightOffset(), worldPosition.getZ() + offset.z);
    }

    public Vec3 getEntityPos(Entity e) {
        return new Vec3(e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ());
    }

    // ==================== Энергия ====================
    @Override
    public long getPower() { return power; }
    @Override
    public void setPower(long power) { this.power = power; }
    @Override
    public long transferPower(long power) {
        long toReceive = Math.min(power, getMaxPower() - this.power);
        this.power += toReceive;
        return power - toReceive;
    }

    // ==================== Capabilities ====================
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return handlerCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handlerCap.invalidate();
    }

    // ==================== NBT / Синхронизация ====================
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        power = nbt.getLong("power");
        isOn = nbt.getBoolean("isOn");
        targetPlayers = nbt.getBoolean("targetPlayers");
        targetAnimals = nbt.getBoolean("targetAnimals");
        targetMobs = nbt.getBoolean("targetMobs");
        targetMachines = nbt.getBoolean("targetMachines");
        stattrak = nbt.getInt("stattrak");
        inventory.deserializeNBT(nbt.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
        nbt.putBoolean("isOn", isOn);
        nbt.putBoolean("targetPlayers", targetPlayers);
        nbt.putBoolean("targetAnimals", targetAnimals);
        nbt.putBoolean("targetMobs", targetMobs);
        nbt.putBoolean("targetMachines", targetMachines);
        nbt.putInt("stattrak", stattrak);
        nbt.put("inventory", inventory.serializeNBT());
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeDouble(rotationPitch);
        buf.writeDouble(rotationYaw);
        buf.writeLong(power);
        buf.writeBoolean(isOn);
        buf.writeBoolean(targetPlayers);
        buf.writeBoolean(targetAnimals);
        buf.writeBoolean(targetMobs);
        buf.writeBoolean(targetMachines);
        buf.writeInt(stattrak);
        if (tPos != null) {
            buf.writeDouble(tPos.x);
            buf.writeDouble(tPos.y);
            buf.writeDouble(tPos.z);
        } else {
            buf.writeDouble(0);
            buf.writeDouble(0);
            buf.writeDouble(0);
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        turnProgress = 2;
        syncRotationPitch = buf.readDouble();
        syncRotationYaw = buf.readDouble();
        power = buf.readLong();
        isOn = buf.readBoolean();
        targetPlayers = buf.readBoolean();
        targetAnimals = buf.readBoolean();
        targetMobs = buf.readBoolean();
        targetMachines = buf.readBoolean();
        stattrak = buf.readInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        if (x != 0 || y != 0 || z != 0) tPos = new Vec3(x, y, z);
        else tPos = null;
    }

    // ==================== Логика обновления ====================

    public void tick() {
        if (level == null) return;

        // Клиентская интерполяция
        if (level.isClientSide) {
            lastRotationPitch = rotationPitch;
            lastRotationYaw = rotationYaw;
            rotationPitch = syncRotationPitch;
            rotationYaw = syncRotationYaw;
            // Исправление перехода через 360°
            if (Math.abs(lastRotationYaw - rotationYaw) > Math.PI) {
                if (lastRotationYaw < rotationYaw) lastRotationYaw += Math.PI * 2;
                else lastRotationYaw -= Math.PI * 2;
            }
            return;
        }

        // Серверная логика
        updateConnections();

        // Проверка живучести цели
        if (target != null && (!target.isAlive() || target.isRemoved())) {
            target = null;
            tPos = null;
            stattrak++;
            aligned = false; // Добавь сброс aligned
            return; // Можно сразу выйти, чтобы не продолжать
        }

        if (target != null && !entityInLOS(target)) {
            target = null;
            tPos = null;
            aligned = false; // Добавь сброс aligned
        }

        if (target != null) tPos = getEntityPos(target);

        // Поворот
        if (isOn() && hasPower()) {
            if (tPos != null) alignTurret();
        } else {
            target = null;
            tPos = null;
        }

        // Поиск цели и энергопотребление
        if (isOn() && hasPower()) {
            searchTimer--;
            setPower(getPower() - getConsumption());
            if (searchTimer <= 0) {
                searchTimer = getDecetorInterval();
                if (target == null) seekNewTarget();
            }
        } else {
            searchTimer = 0;
        }

        // Стрельба
        if (aligned) updateFiringTick();

        // Зарядка от батарей в слоте 10
        power = Library.chargeTEFromItems(inventory, 10, power, getMaxPower());

        // Отправка пакета клиентам
        networkPackNT(50);
    }

    protected void updateConnections() {
        // Подписка на энергию со всех направлений (можно переопределить в конкретной турели)
        for (Direction dir : Direction.values()) {
            trySubscribe(level, worldPosition.relative(dir), dir);
        }
    }

    // ==================== GUI и инвентарь ====================

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true; // проверки на совместимость могут быть добавлены в конкретной турели
    }

    // Звуки открытия/закрытия (опционально)
    public void openInventory(Player player) {
        if (level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public void closeInventory(Player player) {
        if (level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    // ==================== Служебное ====================
    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public void dropItems(Level level, BlockPos pos) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            }
        }
    }
}