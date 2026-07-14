package com.hbm.entity.missile;

import com.hbm.api.entity.IRadarDetectableNT;
import com.hbm.blocks.ModBlocks;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityBalefire;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.logic.IChunkLoader;
import com.hbm.entity.projectile.EntityBulletBaseMK4;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCustomMissilePart;
import com.hbm.items.weapon.ItemCustomMissilePart.FuelType;
import com.hbm.items.weapon.ItemCustomMissilePart.PartSize;
import com.hbm.items.weapon.ItemCustomMissilePart.WarheadType;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class EntityMissileCustom extends EntityMissileBaseNT implements IChunkLoader {

    public float fuel;
    public float consumption;

    public EntityMissileCustom(EntityType<? extends EntityMissileBaseNT> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileCustom(Level level, double x, double y, double z, int targetX, int targetZ, MissileStruct template) {
        super(ModEntities.MISSILE_CUSTOM.get(), level);

        this.setPos(x, y, z);
        startX = (int) x;
        startZ = (int) z;
        this.targetX = targetX;
        this.targetZ = targetZ;
        this.setDeltaMovement(0, 2, 0);

        Vec3 vector = new Vec3(targetX - startX, 0, targetZ - startZ);
        accelXZ = decelY = 1 / vector.length();
        decelY *= 2;
        velocity = 0;

        this.entityData.set(WARHEAD, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(template.warhead.asItem())).toString());
        this.entityData.set(FUSELAGE, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(template.fuselage.asItem())).toString());
        this.entityData.set(THRUSTER, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(template.thruster.asItem())).toString());
        if (template.fins != null) {
            this.entityData.set(FINS, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(template.fins.asItem())).toString());
        } else {
            this.entityData.set(FINS, "");
        }

        ItemCustomMissilePart fuselage = template.fuselage;
        ItemCustomMissilePart thruster = template.thruster;

        this.fuel = (Float) fuselage.attributes[1];
        this.consumption = (Float) thruster.attributes[1];

        this.setSize(1.5F, 1.5F);
    }

    @Override
    protected void killMissile() {
        if (!this.isRemoved()) {
            this.discard();
            ExplosionLarge.explode(level(), this.getX(), this.getY(), this.getZ(), 5, true, false, true);
            ExplosionLarge.spawnShrapnelShower(level(), this.getX(), this.getY(), this.getZ(),
                    this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z, 15, 0.075);
        }
    }

    @Override
    public void tick() {
        String warheadId = this.entityData.get(WARHEAD);
        if (!warheadId.isEmpty()) {
            Item warheadItem = ForgeRegistries.ITEMS.getValue(ResLocation(warheadId));
            if (warheadItem instanceof ItemCustomMissilePart part) {
                WarheadType type = (WarheadType) part.attributes[0];
                if (type != null && type.updateCustom != null) {
                    type.updateCustom.accept(this);
                }
            }
        }

        if (!level().isClientSide) {
            if (this.hasPropulsion()) this.fuel -= this.consumption;
        }

        super.tick();
    }

    @Override
    public boolean hasPropulsion() {
        return this.fuel > 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WARHEAD, "");
        this.entityData.define(FUSELAGE, "");
        this.entityData.define(FINS, "");
        this.entityData.define(THRUSTER, "");
    }

    private static final EntityDataAccessor<String> WARHEAD =
            SynchedEntityData.defineId(EntityMissileCustom.class,
                    EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> FUSELAGE =
            SynchedEntityData.defineId(EntityMissileCustom.class,
                    EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> FINS =
            SynchedEntityData.defineId(EntityMissileCustom.class,
                    EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> THRUSTER =
            SynchedEntityData.defineId(EntityMissileCustom.class,
                    EntityDataSerializers.STRING);

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        fuel = nbt.getFloat("fuel");
        consumption = nbt.getFloat("consumption");
        this.entityData.set(WARHEAD, nbt.getString("warhead"));
        this.entityData.set(FUSELAGE, nbt.getString("fuselage"));
        this.entityData.set(FINS, nbt.getString("fins"));
        this.entityData.set(THRUSTER, nbt.getString("thruster"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("fuel", fuel);
        nbt.putFloat("consumption", consumption);
        nbt.putString("warhead", this.entityData.get(WARHEAD));
        nbt.putString("fuselage", this.entityData.get(FUSELAGE));
        nbt.putString("fins", this.entityData.get(FINS));
        nbt.putString("thruster", this.entityData.get(THRUSTER));
    }

    @Override
    protected void spawnContrail() {
        Vec3 v = this.getDeltaMovement().normalize();
        String smoke = "";
        String fuselageId = this.entityData.get(FUSELAGE);
        if (!fuselageId.isEmpty()) {
            Item fuselageItem = ForgeRegistries.ITEMS.getValue(ResLocation(fuselageId));
            if (fuselageItem instanceof ItemCustomMissilePart part) {
                FuelType type = (FuelType) part.attributes[0];
                switch (type) {
                    case BALEFIRE: smoke = "exBalefire"; break;
                    case HYDROGEN: smoke = "exHydrogen"; break;
                    case KEROSENE: smoke = "exKerosene"; break;
                    case SOLID: smoke = "exSolid"; break;
                    case XENON: break;
                }
            }
        }

        if (!smoke.isEmpty()) {
            for (int i = 0; i < velocity; i++) {
                CompoundTag data = new CompoundTag();
                data.putDouble("posX", this.getX() - v.x * i);
                data.putDouble("posY", this.getY() - v.y * i);
                data.putDouble("posZ", this.getZ() - v.z * i);
                data.putString("type", smoke);
                // Отправка пакета через Proxy
            }
        }
    }

    @Override
    public void onMissileImpact(HitResult mop) {
        String warheadId = this.entityData.get(WARHEAD);
        if (warheadId.isEmpty()) return;

        Item warheadItem = ForgeRegistries.ITEMS.getValue(ResLocation(warheadId));
        if (!(warheadItem instanceof ItemCustomMissilePart part)) return;

        WarheadType type = (WarheadType) part.attributes[0];
        float strength = (Float) part.attributes[1];

        if (type.impactCustom != null) {
            type.impactCustom.accept(this);
            return;
        }

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        switch (type) {
            case HE:
                ExplosionLarge.explode(level(), x, y, z, strength, true, false, true);
                ExplosionLarge.jolt(level(), x, y, z, strength, (int) (strength * 50), 0.25);
                break;
            case INC:
                ExplosionLarge.explodeFire(level(), x, y, z, strength, true, false, true);
                ExplosionLarge.jolt(level(), x, y, z, strength * 1.5, (int) (strength * 50), 0.25);
                break;
            case CLUSTER:
                break;
            case BUSTER:
                ExplosionLarge.buster(level(), x, y, z, new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z), strength, strength * 4);
                break;
            case NUCLEAR:
            case TX:
                EntityNukeExplosionMK5.statFac(level(), (int) strength, x, y, z);
                EntityNukeTorex.statFacStandard(level(), x, y, z, strength);
                break;
            case BALEFIRE:
                EntityBalefire bf = new EntityBalefire(ModEntities.BALEFIRE.get(), level());
                bf.setPos(x, y, z);
                bf.destructionRange = (int) strength;
                level().addFreshEntity(bf);
                EntityNukeTorex.statFacBale(level(), x, y, z, strength);
                break;
            case N2:
                EntityNukeExplosionMK5.statFacNoRad(level(), (int) strength, x, y, z);
                EntityNukeTorex.statFacStandard(level(), x, y, z, strength);
                break;
            case TAINT:
                int r = (int) strength;
                for (int i = 0; i < r * 10; i++) {
                    int a = (int) (random.nextInt(r) + x - (r / 2 - 1));
                    int b = (int) (random.nextInt(r) + y - (r / 2 - 1));
                    int c = (int) (random.nextInt(r) + z - (r / 2 - 1));
                    BlockPos pos = new BlockPos(a, b, c);
                    if (level().getBlockState(pos).isSolid()) {
                        level().setBlock(pos, ModBlocks.TAINT.get().defaultBlockState(), 2);
                    }
                }
                break;
            case CLOUD:
                this.level().levelEvent(2002, BlockPos.containing(x, y, z), 0);
                if (this.level() instanceof ServerLevel serverLevel) {
                    ExplosionChaos.spawnPoisonCloud(
                            serverLevel,
                            x - this.getDeltaMovement().x,
                            y - this.getDeltaMovement().y,
                            z - this.getDeltaMovement().z,
                            750, 2.5, 2
                    );
                }
                break;
            case TURBINE:
                ExplosionLarge.explode(level(), x, y, z, 10, true, false, true);
                int count = (int) strength;
                Vec3 vec = new Vec3(0.5, 0, 0);

                // Получаем конфиг для турбины. Предположим, он зарегистрирован в BulletConfig.configs
                // под тем же индексом, что раньше использовался в BulletConfigSyncingUtil.TURBINE.
                // Лучше хранить ссылку на него где-то, например, в статическом поле.
                BulletConfig turbineConfig = BulletConfigRegistry.TURBINE;
                // Или если у вас есть другой способ получить конфиг (например, по имени), используйте его.

                for (int i = 0; i < count; i++) {
                    // Создаём пулю MK4
                    EntityBulletBaseMK4 blade = new EntityBulletBaseMK4(
                            level(),                // Level level
                            null,                   // LivingEntity shooter
                            turbineConfig,          // BulletConfig config
                            10F,                    // float baseDamage
                            0F,                     // float gunSpread
                            x - this.getDeltaMovement().x,    // double posX
                            y - this.getDeltaMovement().y + random.nextGaussian(), // double posY
                            z - this.getDeltaMovement().z,    // double posZ
                            vec.x,                  // double motionX
                            0,                      // double motionY
                            vec.z                   // double motionZ
                    );
                    level().addFreshEntity(blade);
                    vec = vec.yRot((float) (Math.PI * 2F / (float) count));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String getUnlocalizedName() {
        String fuselageId = this.entityData.get(FUSELAGE);
        if (fuselageId.isEmpty()) return "radar.target.custom";

        Item fuselageItem = ForgeRegistries.ITEMS.getValue(ResLocation(fuselageId));
        if (!(fuselageItem instanceof ItemCustomMissilePart part)) return "radar.target.custom";

        PartSize top = part.top;
        PartSize bottom = part.bottom;

        if (top == PartSize.SIZE_10 && bottom == PartSize.SIZE_10) return "radar.target.custom10";
        if (top == PartSize.SIZE_10 && bottom == PartSize.SIZE_15) return "radar.target.custom1015";
        if (top == PartSize.SIZE_15 && bottom == PartSize.SIZE_15) return "radar.target.custom15";
        if (top == PartSize.SIZE_15 && bottom == PartSize.SIZE_20) return "radar.target.custom1520";
        if (top == PartSize.SIZE_20 && bottom == PartSize.SIZE_20) return "radar.target.custom20";

        return "radar.target.custom";
    }

    @Override
    public int getBlipLevel() {
        String fuselageId = this.entityData.get(FUSELAGE);
        if (fuselageId.isEmpty()) return IRadarDetectableNT.TIER1;

        Item fuselageItem = ForgeRegistries.ITEMS.getValue(ResLocation(fuselageId));
        if (!(fuselageItem instanceof ItemCustomMissilePart part)) return IRadarDetectableNT.TIER1;

        PartSize top = part.top;
        PartSize bottom = part.bottom;

        if (top == PartSize.SIZE_10 && bottom == PartSize.SIZE_10) return IRadarDetectableNT.TIER10;
        if (top == PartSize.SIZE_10 && bottom == PartSize.SIZE_15) return IRadarDetectableNT.TIER10_15;
        if (top == PartSize.SIZE_15 && bottom == PartSize.SIZE_15) return IRadarDetectableNT.TIER15;
        if (top == PartSize.SIZE_15 && bottom == PartSize.SIZE_20) return IRadarDetectableNT.TIER15_20;
        if (top == PartSize.SIZE_20 && bottom == PartSize.SIZE_20) return IRadarDetectableNT.TIER20;

        return IRadarDetectableNT.TIER1;
    }

    @Override
    public List<ItemStack> getDebris() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getDebrisRareDrop() {
        return null;
    }

    @Override
    public ItemStack getMissileItemForInfo() {
        return new ItemStack(ModItems.MISSILE_CUSTOM.get());
    }

    public void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}