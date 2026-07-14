package com.hbm.tileentity.bomb;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.api.item.IDesignatorItem;
import com.hbm.entity.ModEntities;
import com.hbm.entity.missile.*;
import com.hbm.entity.missile.EntityMissileTier0.*;
import com.hbm.entity.missile.EntityMissileTier1.*;
import com.hbm.entity.missile.EntityMissileTier2.*;
import com.hbm.entity.missile.EntityMissileTier3.*;
import com.hbm.entity.missile.EntityMissileTier4.*;
import com.hbm.interfaces.IBomb.BombReturnCode;
import com.hbm.inventory.container.ContainerLaunchPadLarge;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemMissile;
import com.hbm.items.weapon.ItemMissile.MissileFuel;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.IRadarCommandReceiver;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.Library;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class TileEntityLaunchPadBase extends TileEntityMachineBase implements IEnergyReceiver, IFluidStandardReceiver, MenuProvider, IRadarCommandReceiver {

    // ------ Статический реестр ракет ------
    public static final HashMap<ComparableStack, Class<? extends EntityMissileBaseNT>> missiles = new HashMap<>();

    static {
        // Tier 0
        missiles.put(new ComparableStack(ModItems.MISSILE_TEST.get()), EntityMissileTest.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_MICRO.get()), EntityMissileMicro.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_SCHRABIDIUM.get()), EntityMissileSchrabidium.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_BHOLE.get()), EntityMissileBHole.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_TAINT.get()), EntityMissileTaint.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_EMP.get()), EntityMissileEMP.class);
        // Tier 1
        missiles.put(new ComparableStack(ModItems.MISSILE_GENERIC.get()), EntityMissileGeneric.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_DECOY.get()), EntityMissileDecoy.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_INCENDIARY.get()), EntityMissileIncendiary.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_CLUSTER.get()), EntityMissileCluster.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_BUSTER.get()), EntityMissileBunkerBuster.class);
        // Tier 2
        missiles.put(new ComparableStack(ModItems.MISSILE_STRONG.get()), EntityMissileStrong.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_INCENDIARY_STRONG.get()), EntityMissileIncendiaryStrong.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_CLUSTER_STRONG.get()), EntityMissileClusterStrong.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_BUSTER_STRONG.get()), EntityMissileBusterStrong.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_EMP_STRONG.get()), EntityMissileEMPStrong.class);
        // Tier 3
        missiles.put(new ComparableStack(ModItems.MISSILE_BURST.get()), EntityMissileBurst.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_INFERNO.get()), EntityMissileInferno.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_RAIN.get()), EntityMissileRain.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_DRILL.get()), EntityMissileDrill.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_SHUTTLE.get()), EntityMissileShuttle.class);
        // Tier 4
        missiles.put(new ComparableStack(ModItems.MISSILE_NUCLEAR.get()), EntityMissileNuclear.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_NUCLEAR_CLUSTER.get()), EntityMissileMirv.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_VOLCANO.get()), EntityMissileVolcano.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_DOOMSDAY.get()), EntityMissileDoomsday.class);
        missiles.put(new ComparableStack(ModItems.MISSILE_STEALTH.get()), EntityMissileStealth.class);
    }

    // ------ Поля ------
    public ItemStack toRender;          // для отображения в GUI
    public long power;
    public static final long MAX_POWER = 100_000;
    public Set<BlockPos> activatedBlocks = new HashSet<>();
    public int redstonePower;

    public int state = STATE_MISSING;
    public static final int STATE_MISSING = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_READY = 2;

    public FluidTankHBM[] tanks;

    public int prevRedstonePower = -1;

    // Инвентарь: 7 слотов
    // 0 - ракета, 1 - дизайнатор, 2 - батарея, 3 - топливо в, 4 - топливо вых, 5 - окислитель в, 6 - окислитель вых
    protected final ItemStackHandler inventory = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
                case 0 -> isMissileValid(stack);
                case 1 -> stack.getItem() instanceof IDesignatorItem;
                case 2 -> stack.getItem() instanceof com.hbm.api.energy.IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get();
                case 3, 5 -> true;  // входные слоты для жидкостей
                case 4, 6 -> false; // выходные – только извлечение
                default -> false;
            };
        }
    };

    private final LazyOptional<ItemStackHandler> itemHandlerCap = LazyOptional.of(() -> inventory);

    // ------ Конструктор ------
    public TileEntityLaunchPadBase(BlockPos pos, BlockState state) {
        super(ModTileEntity.LAUNCH_PAD.get(), pos, state); // предполагаем, что у вас зарегистрирован такой тип
        this.tanks = new FluidTankHBM[2];
        this.tanks[0] = new FluidTankHBM(Fluids.NONE.get(), 24_000);
        this.tanks[1] = new FluidTankHBM(Fluids.NONE.get(), 24_000);
    }

    // ------ Доступ к инвентарю для контейнера ------
    public ItemStackHandler getInventory() {
        return inventory;
    }

    // ------ Абстрактные методы для конкретных пусковых установок ------
    public abstract Library.PosDir[] getConPos();
    public abstract boolean isReadyForLaunch();
    public abstract double getLaunchOffset();

    // ------ Основной тик ------

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Подписка на энергию и жидкости
        if (level.getGameTime() % 20 == 0) {
            for (Library.PosDir pos : getConPos()) {
                trySubscribe(level, pos.pos(), pos.dir());
                if (tanks[0].getTankType() != Fluids.NONE.get())
                    trySubscribe(tanks[0].getTankType(), level, pos.pos(), pos.dir());
                if (tanks[1].getTankType() != Fluids.NONE.get())
                    trySubscribe(tanks[1].getTankType(), level, pos.pos(), pos.dir());
            }
        }

        // Зарядка от батареи в слоте 2
        power = Library.chargeTEFromItems(inventory, 2, power, MAX_POWER);

        // Загрузка жидкостей из слотов 3→4 и 5→6
        tanks[0].loadTank(3, 4, toArray(inventory), inventory);
        tanks[1].loadTank(5, 6, toArray(inventory), inventory);

        // Установка типа топлива по установленной ракете
        if (isMissileValid() && inventory.getStackInSlot(0).getItem() instanceof ItemMissile missile) {
            setFuel(missile);
        }

        if (this.redstonePower > 0 && this.prevRedstonePower <= 0) {
            this.launchFromDesignator();
        }
        this.prevRedstonePower = this.redstonePower;

        // Отправка данных клиенту
        if (level.getGameTime() % 10 == 0) {
            setChanged();
        }
        // Для синхронизации через пакеты (вызывается в конце)
        this.networkPackNT(50);
    }

    private ItemStack[] toArray(ItemStackHandler handler) {
        ItemStack[] array = new ItemStack[handler.getSlots()];
        for (int i = 0; i < handler.getSlots(); i++) {
            array[i] = handler.getStackInSlot(i);
        }
        return array;
    }

    // ------ Установка топлива по типу ракеты ------
    public void setFuel(ItemMissile missile) {
        switch (missile.fuel) {
            case ETHANOL_PEROXIDE -> {
                tanks[0].setType(Fluids.ETHANOL.get());
                tanks[1].setType(Fluids.PEROXIDE.get());
            }
            case KEROSENE_PEROXIDE -> {
                tanks[0].setType(Fluids.KEROSENE.get());
                tanks[1].setType(Fluids.PEROXIDE.get());
            }
            case KEROSENE_LOXY -> {
                tanks[0].setType(Fluids.KEROSENE.get());
                tanks[1].setType(Fluids.OXYGEN.get());
            }
            case JETFUEL_LOXY -> {
                tanks[0].setType(Fluids.KEROSENE_REFORM.get());
                tanks[1].setType(Fluids.OXYGEN.get());
            }
            default -> { /* SOLID – ничего не делаем */ }
        }
    }

    // ------ Проверка валидности ракеты ------
    public boolean isMissileValid() {
        return !inventory.getStackInSlot(0).isEmpty() && isMissileValid(inventory.getStackInSlot(0));
    }

    public boolean isMissileValid(ItemStack stack) {
        return stack.getItem() instanceof ItemMissile && ((ItemMissile) stack.getItem()).launchable;
    }

    // ------ Проверка наличия топлива и энергии ------
    public boolean hasFuel() {
        if (power < 75_000) return false;

        ItemStack missileStack = inventory.getStackInSlot(0);
        if (missileStack.getItem() instanceof ItemMissile missile) {
            if (tanks[0].getFill() < missile.fuelCap) return false;
            if (tanks[1].getFill() < missile.fuelCap) return false;
            return true;
        }
        return false;
    }

    // ------ Создание сущности ракеты ------
    public Entity instantiateMissile(int targetX, int targetZ) {
        ItemStack missileStack = inventory.getStackInSlot(0);
        if (missileStack.isEmpty()) return null;

        Class<? extends EntityMissileBaseNT> clazz = missiles.get(new ComparableStack(missileStack).makeSingular());

        if (clazz != null) {
            try {
                // Получаем EntityType
                EntityType<?> entityType = getEntityTypeFromClass(clazz);
                if (entityType == null) return null;

                // Используем конструктор с (EntityType, Level)
                Constructor<? extends EntityMissileBaseNT> ctor = clazz.getConstructor(
                        Level.class, double.class, double.class, double.class, int.class, int.class, EntityType.class
                );

                return ctor.newInstance(
                        level,
                        worldPosition.getX() + 0.5D,
                        worldPosition.getY() + getLaunchOffset(),
                        worldPosition.getZ() + 0.5D,
                        targetX,
                        targetZ,
                        entityType
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Антибаллистическая ракета
        if (missileStack.getItem() == ModItems.MISSILE_ANTI_BALLISTIC.get()) {
            EntityMissileAntiBallistic missile = new EntityMissileAntiBallistic(level);
            missile.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + getLaunchOffset(), worldPosition.getZ() + 0.5);
            return missile;
        }

        return null;
    }

    private EntityType<?> getEntityTypeFromClass(Class<? extends EntityMissileBaseNT> clazz) {
        // Tier 0
        if (clazz == EntityMissileTest.class) return ModEntities.MISSILE_TEST.get();
        if (clazz == EntityMissileMicro.class) return ModEntities.MISSILE_MICRO.get();
        if (clazz == EntityMissileSchrabidium.class) return ModEntities.MISSILE_SCHRABIDIUM.get();
        if (clazz == EntityMissileBHole.class) return ModEntities.MISSILE_BHOLE.get();
        if (clazz == EntityMissileTaint.class) return ModEntities.MISSILE_TAINT.get();
        if (clazz == EntityMissileEMP.class) return ModEntities.MISSILE_EMP.get();

        // Tier 1
        if (clazz == EntityMissileGeneric.class) return ModEntities.MISSILE_GENERIC.get();
        if (clazz == EntityMissileDecoy.class) return ModEntities.MISSILE_DECOY.get();
        if (clazz == EntityMissileIncendiary.class) return ModEntities.MISSILE_INCENDIARY.get();
        if (clazz == EntityMissileCluster.class) return ModEntities.MISSILE_CLUSTER.get();
        if (clazz == EntityMissileBunkerBuster.class) return ModEntities.MISSILE_BUSTER.get();

        // Tier 2
        if (clazz == EntityMissileStrong.class) return ModEntities.MISSILE_STRONG.get();
        if (clazz == EntityMissileIncendiaryStrong.class) return ModEntities.MISSILE_INCENDIARY_STRONG.get();
        if (clazz == EntityMissileClusterStrong.class) return ModEntities.MISSILE_CLUSTER_STRONG.get();
        if (clazz == EntityMissileBusterStrong.class) return ModEntities.MISSILE_BUSTER_STRONG.get();
        if (clazz == EntityMissileEMPStrong.class) return ModEntities.MISSILE_EMP_STRONG.get();

        // Tier 3
        if (clazz == EntityMissileBurst.class) return ModEntities.MISSILE_BURST.get();
        if (clazz == EntityMissileInferno.class) return ModEntities.MISSILE_INFERNO.get();
        if (clazz == EntityMissileRain.class) return ModEntities.MISSILE_RAIN.get();
        if (clazz == EntityMissileDrill.class) return ModEntities.MISSILE_DRILL.get();

        // Tier 4
        if (clazz == EntityMissileNuclear.class) return ModEntities.MISSILE_NUCLEAR.get();
        if (clazz == EntityMissileMirv.class) return ModEntities.MISSILE_NUCLEAR_CLUSTER.get();
        if (clazz == EntityMissileVolcano.class) return ModEntities.MISSILE_VOLCANO.get();
        if (clazz == EntityMissileDoomsday.class) return ModEntities.MISSILE_DOOMSDAY.get();
        if (clazz == EntityMissileDoomsdayRusted.class) return ModEntities.MISSILE_DOOMSDAY_RUSTED.get();

        // Special
        if (clazz == EntityMissileStealth.class) return ModEntities.MISSILE_STEALTH.get();
        if (clazz == EntityMissileShuttle.class) return ModEntities.MISSILE_SHUTTLE.get();
        if (clazz == EntityMissileCustom.class) return ModEntities.MISSILE_CUSTOM.get();

        return null;
    }

    // ------ Финализация запуска ------
    public void finalizeLaunch(Entity missile) {
        if (level == null) return;

        level.addFreshEntity(missile);

        level.playSound(null,
                worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5,
                ModSounds.MISSILE_TAKEOFF.get(),
                net.minecraft.sounds.SoundSource.PLAYERS, 2.0F, 1.0F);

        power -= 75_000;

        ItemStack missileStack = inventory.getStackInSlot(0);
        if (missileStack.getItem() instanceof ItemMissile item) {
            tanks[0].setFill(tanks[0].getFill() - item.fuelCap);
            tanks[1].setFill(tanks[1].getFill() - item.fuelCap);
        }

        inventory.extractItem(0, 1, false);
    }

    // ------ Запуск по дизайнатору ------
    public BombReturnCode launchFromDesignator() {
        if (!canLaunch()) return BombReturnCode.ERROR_MISSING_COMPONENT;

        boolean needsDesignator = needsDesignator(inventory.getStackInSlot(0).getItem());

        int targetX = worldPosition.getX();
        int targetZ = worldPosition.getZ();

        ItemStack designatorStack = inventory.getStackInSlot(1);
        if (designatorStack.getItem() instanceof IDesignatorItem designator) {
            if (needsDesignator) {
                if (!designator.isReady(level, designatorStack, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()))
                    return BombReturnCode.ERROR_MISSING_COMPONENT;
                Vec3 coords = designator.getCoords(level, designatorStack, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
                targetX = (int) Math.floor(coords.x);
                targetZ = (int) Math.floor(coords.z);
            }
        } else {
            if (needsDesignator) return BombReturnCode.ERROR_MISSING_COMPONENT;
        }

        return launchToCoordinate(targetX, targetZ);
    }

    public BombReturnCode launchToEntity(Entity entity) {
        if (!canLaunch()) return BombReturnCode.ERROR_MISSING_COMPONENT;

        Entity e = instantiateMissile((int) Math.floor(entity.getX()), (int) Math.floor(entity.getZ()));
        if (e != null) {
            if (e instanceof EntityMissileAntiBallistic abm) {
                abm.tracking = entity;
            }
            finalizeLaunch(e);
            return BombReturnCode.LAUNCHED;
        }
        return BombReturnCode.ERROR_MISSING_COMPONENT;
    }

    public BombReturnCode launchToCoordinate(int targetX, int targetZ) {
        if (!canLaunch()) return BombReturnCode.ERROR_MISSING_COMPONENT;

        Entity e = instantiateMissile(targetX, targetZ);
        if (e != null) {
            finalizeLaunch(e);
            return BombReturnCode.LAUNCHED;
        }
        return BombReturnCode.ERROR_MISSING_COMPONENT;
    }

    public boolean needsDesignator(Item item) {
        return item != ModItems.MISSILE_ANTI_BALLISTIC.get();
    }

    public boolean canLaunch() {
        return isMissileValid() && hasFuel() && isReadyForLaunch();
    }

    // ------ Состояние индикаторов топлива (для GUI) ------
    public int getFuelState() {
        return getGaugeState(0);
    }

    public int getOxidizerState() {
        return getGaugeState(1);
    }

    public int getGaugeState(int tankIndex) {
        ItemStack missileStack = inventory.getStackInSlot(0);
        if (missileStack.isEmpty()) return 0;
        if (missileStack.getItem() instanceof ItemMissile missile) {
            if (missile.fuel == MissileFuel.SOLID) return 0;
            return tanks[tankIndex].getFill() >= missile.fuelCap ? 1 : -1;
        }
        return 0;
    }

    // ------ NBT ------
    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        tanks[0].writeToNBT(nbt, "t0");
        tanks[1].writeToNBT(nbt, "t1");
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        tanks[0].readFromNBT(nbt, "t0");
        tanks[1].readFromNBT(nbt, "t1");
    }

    // ------ Сеть (serialize/deserialize) ------
    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeInt(state);
        tanks[0].serialize(buf);
        tanks[1].serialize(buf);
        ItemStack missile = inventory.getStackInSlot(0);
        buf.writeBoolean(!missile.isEmpty());
        if (!missile.isEmpty()) {
            buf.writeInt(Item.getId(missile.getItem()));
            // damage не пишем, так как он всегда 0
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        state = buf.readInt();
        tanks[0].deserialize(buf);
        tanks[1].deserialize(buf);
        if (buf.readBoolean()) {
            Item item = Item.byId(buf.readInt());
            toRender = new ItemStack(item, 1);
        } else {
            toRender = null;
        }
    }

    // ------ Интерфейсы энергии ------
    @Override public long getPower() { return power; }
    @Override public void setPower(long power) { this.power = power; }
    @Override public long getMaxPower() { return MAX_POWER; }

    // ------ Интерфейсы жидкостей ------
    @Override public FluidTankHBM[] getAllTanks() { return tanks; }
    @Override public FluidTankHBM[] getReceivingTanks() { return tanks; }

    @Override
    public boolean canConnect(Direction dir) {
        return dir != Direction.UP && dir != Direction.DOWN;
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != Direction.UP && dir != Direction.DOWN;
    }

    public void updateRedstonePower(BlockPos pos) {
        boolean powered = Objects.requireNonNull(level).getBestNeighborSignal(pos) > 0;
        boolean contained = activatedBlocks.contains(pos);

        if (!contained && powered) {
            activatedBlocks.add(pos);
            if (redstonePower == -1) {
                redstonePower = 0;
            }
            redstonePower++;
        } else if (contained && !powered) {
            activatedBlocks.remove(pos);
            redstonePower--;
            if (redstonePower == 0) {
                redstonePower = -1;
            }
        }
    }

    // ------ GUI и контейнер ------
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.@NotNull Inventory inv, @NotNull Player player) {
        return new ContainerLaunchPadLarge(id, inv, this);
    }

    // ------ Capabilities ------
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
    }

    // ------ IRadarCommandReceiver ------
    @Override
    public boolean sendCommandPosition(int x, int y, int z) {
        return launchToCoordinate(x, z) == BombReturnCode.LAUNCHED;
    }

    @Override
    public boolean sendCommandEntity(Entity target) {
        return launchToEntity(target) == BombReturnCode.LAUNCHED;
    }
}