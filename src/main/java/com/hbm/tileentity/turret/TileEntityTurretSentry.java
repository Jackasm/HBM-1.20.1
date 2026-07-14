package com.hbm.tileentity.turret;

import com.hbm.handler.CasingEjector;
import com.hbm.inventory.container.ContainerTurretSentry;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;

import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TileEntityTurretSentry extends TileEntityTurretBaseNT implements MenuProvider {

    // ========== Конфигурации боеприпасов ==========
    private static final List<Integer> CONFIGS = List.of(
            BulletConfigRegistry.p9_sp.id,
            BulletConfigRegistry.p9_fmj.id,
            BulletConfigRegistry.p9_jhp.id,
            BulletConfigRegistry.p9_ap.id
    );

    // ========== Анимация стволов ==========
    public boolean didJustShootLeft = false;
    public boolean retractingLeft = false;
    public double barrelLeftPos = 0;
    public double lastBarrelLeftPos = 0;
    public boolean didJustShootRight = false;
    public boolean retractingRight = false;
    public double barrelRightPos = 0;
    public double lastBarrelRightPos = 0;

    // ========== Внутренние счётчики ==========
    private boolean shotSide = false;   // какой ствол стреляет: false = правый, true = левый
    protected int timer = 0;              // счётчик для темпа стрельбы

    // ========== Конструктор ==========
    public TileEntityTurretSentry(BlockPos pos, BlockState state) {
        super(ModTileEntity.TURRET_SENTRY.get(), pos, state);
    }

    public TileEntityTurretSentry(BlockEntityType<?> type ,BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ========== Реализация абстрактных методов ==========
    @Override
    public String getName() {
        return "container.turretSentry";
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getName());
    }

    @Override
    public List<Integer> getAmmoList() {
        return CONFIGS;
    }

    // ========== Параметры турели (скорость, углы, радиус и т.д.) ==========
    @Override
    public double getTurretYawSpeed() {
        return 3.0D;
    }

    @Override
    public double getTurretPitchSpeed() {
        return 2.0D;
    }

    @Override
    public double getTurretDepression() {
        return 20.0D;
    }

    @Override
    public double getTurretElevation() {
        return 20.0D;
    }

    @Override
    public int getDecetorInterval() {
        return 10;
    }

    @Override
    public double getDecetorRange() {
        return 24.0D;
    }

    @Override
    public double getDecetorGrace() {
        return 2.0D;
    }

    @Override
    public long getMaxPower() {
        return 1000L;
    }

    @Override
    public long getConsumption() {
        return 5L;
    }

    @Override
    public double getBarrelLength() {
        return 1.25D;
    }

    @Override
    public double getAcceptableInaccuracy() {
        return 15.0D;
    }

    @Override
    public boolean hasThermalVision() {
        return false;
    }

    @Override
    public boolean hasPower() {
        return this.power >= getConsumption();
    }

    @Override
    public boolean isOn() {
        return this.isOn;
    }

    @Override
    public double getHeightOffset() {
        return 1.5D;
    }

    @Override
    public Vec3 getHorizontalOffset() {
        // Стандартное смещение для обычной турели
        return new Vec3(0.5D, 0.0D, 0.5D);
    }

    // ========== Логика работы с боеприпасами и выстрелом ==========


    @Override
    public BulletConfig getFirstConfigLoaded() {
        List<Integer> list = getAmmoList();
        if (list == null || list.isEmpty()) return null;

        for (int i = 1; i <= 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                for (Integer id : list) {
                    BulletConfig cfg = BulletConfig.configs.get(id);
                    if (cfg != null && cfg.ammo != null &&
                            cfg.ammo.getItem() == stack.getItem() &&
                            cfg.ammo.getDamageValue() == stack.getDamageValue()) {
                        return cfg;
                    }
                }
            }
        }
        return null;
    }

    // ========== Стрельба ==========
    @Override
    public void updateFiringTick() {
        timer++;
        if (timer % 10 == 0) {
            BulletConfig conf = getFirstConfigLoaded();
            if (conf != null) {
                // Расходуем патрон (метод из базового класса)
                conusmeAmmo(conf);
                // Звук
                float pitch = shotSide ? 1.0F : 0.75F;
                Objects.requireNonNull(level).playSound(null, worldPosition, ModSounds.TURRET_SENTRY_FIRE.get(), SoundSource.BLOCKS, 2.0F, pitch);
                // Спавн пули
                spawnBullet(conf, 5F);

                if (shotSide) {
                    didJustShootLeft = true;
                } else {
                    didJustShootRight = true;
                }
                shotSide = !shotSide;
            }
        }
    }

    // ========== Поиск цели ==========
    @Override
    protected void seekNewTarget() {
        Entity lastTarget = this.target;
        super.seekNewTarget();
        if (lastTarget != this.target && this.target != null && level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, ModSounds.TURRET_SENTRY_LOCKON.get(), SoundSource.BLOCKS, 2.0F, 1.5F);
        }
    }

    // Вспомогательная дистанция
    private double getDistanceToEntity(Entity e) {
        Vec3 pos = getTurretPos();
        Vec3 ent = getEntityPos(e);
        return pos.distanceTo(ent);
    }

    // ========== Проверка цели на допустимость ==========
    @Override
    protected boolean entityAcceptableTarget(Entity e) {
        // Игроки в креативе — нет
        if (e instanceof Player && ((Player) e).isCreative()) return false;

        // Только живые существа (LivingEntity)
        if (!(e instanceof Entity)) return false;

        // Проверка белого списка (чип)
        List<String> whitelist = getWhitelist();
        if (whitelist != null && !whitelist.isEmpty()) {
            if (e instanceof Player && whitelist.contains(e.getName().getString())) return false;
            if (e instanceof LivingEntity && e.hasCustomName() && whitelist.contains(Objects.requireNonNull(e.getCustomName()).getString())) return false;
        }

        // Проверка флагов настроек
        boolean isPlayer = e instanceof Player;
        boolean isAnimal = e instanceof net.minecraft.world.entity.animal.Animal;
        boolean isMob = e instanceof net.minecraft.world.entity.monster.Monster;
        boolean isMachine = e instanceof com.hbm.entity.missile.EntityMissileCustom; // или свои механические цели

        if (isPlayer && targetPlayers) return true;
        if (isAnimal && targetAnimals) return true;
        if (isMob && targetMobs) return true;
        if (isMachine && targetMachines) return true;

        return false;
    }

    // ========== Гильзы ==========
    @Override
    public boolean usesCasings() {
        return true;
    }

    @Override
    public CasingEjector getEjector() {
        // Параметры выброса гильзы
        return new CasingEjector().setMotion(0.3, 0.6, 0).setAngleRange(0.01F, 0.01F);
    }

    @Override
    protected int casingDelay() {
        return 0; // задержка перед выбросом гильзы (0 = сразу)
    }

    @Override
    protected Vec3 getCasingSpawnPos() {
        // Позиция вылета гильзы – ствол
        Vec3 pos = getTurretPos();
        Vec3 vec = new Vec3(getBarrelLength() + 0.5, 0, 0);
        vec = vec.xRot((float) -rotationPitch);
        vec = vec.yRot((float) -(rotationYaw + Math.PI * 0.5));
        return new Vec3(pos.x + vec.x, pos.y + vec.y, pos.z + vec.z);
    }

    @Override
    protected void spawnCasing() {
        if (cachedCasingConfig == null) return;
        CasingEjector ej = getEjector();
        Vec3 spawn = getCasingSpawnPos();
        CompoundTag data = new CompoundTag();
        data.putString("type", "casingNT");
        data.putFloat("pitch", (float) -rotationPitch);
        data.putFloat("yaw", (float) rotationYaw);
        data.putBoolean("crouched", false);
        data.putString("name", cachedCasingConfig.getName());
        if (ej != null) data.putInt("ej", ej.getId());
        PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data, spawn.x, spawn.y, spawn.z),
                level, worldPosition, 50);
        cachedCasingConfig = null;
    }

    // ========== Клиентская анимация ==========
    @Override
    public void tick() {
        if (level != null && level.isClientSide) {
            // Интерполяция анимации стволов
            lastBarrelLeftPos = barrelLeftPos;
            lastBarrelRightPos = barrelRightPos;
            float retractSpeed = 0.5F;
            float pushSpeed = 0.25F;

            if (retractingLeft) {
                barrelLeftPos += retractSpeed;
                if (barrelLeftPos >= 1) retractingLeft = false;
            } else {
                barrelLeftPos -= pushSpeed;
                if (barrelLeftPos < 0) barrelLeftPos = 0;
            }

            if (retractingRight) {
                barrelRightPos += retractSpeed;
                if (barrelRightPos >= 1) retractingRight = false;
            } else {
                barrelRightPos -= pushSpeed;
                if (barrelRightPos < 0) barrelRightPos = 0;
            }
        }
        super.tick();
    }

    // ========== Синхронизация ==========
    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(didJustShootLeft);
        buf.writeBoolean(didJustShootRight);
        // Сброс флагов после отправки
        didJustShootLeft = false;
        didJustShootRight = false;
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        retractingLeft = buf.readBoolean();
        retractingRight = buf.readBoolean();
    }

    // ========== NBT ==========
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        // Дополнительные поля, если нужно
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        // Дополнительные поля, если нужно
    }

    // ========== Capabilities и инвентарь ==========


    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) return stack.getItem() instanceof com.hbm.items.machine.ItemTurretBiometry;
        if (index == 10) return stack.getItem() instanceof com.hbm.api.energy.IBatteryItem;
        if (index >= 1 && index <= 9) {
            // Можно проверить, что предмет является патроном
            return true;
        }
        return false;
    }

    // ========== Вспомогательное ==========
    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerTurretSentry(containerId, playerInventory, this);
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    public void handleButtonPacket(int value, int meta) {
        switch(meta) {
            case 0: this.isOn = !this.isOn; break;
            case 1: this.targetPlayers = !this.targetPlayers; break;
            case 2: this.targetAnimals = !this.targetAnimals; break;
            case 3: this.targetMobs = !this.targetMobs; break;
            case 4: this.targetMachines = !this.targetMachines; break;
        }
        // Отправляем обновление клиенту
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }
}
