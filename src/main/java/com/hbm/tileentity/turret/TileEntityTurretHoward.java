package com.hbm.tileentity.turret;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hbm.blocks.BlockDummyable;
import com.hbm.config.WeaponConfig;
import com.hbm.handler.CasingEjector;

import com.hbm.inventory.container.ContainerTurretHoward;
import com.hbm.items.weapon.sedna.BulletConfig;


import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.particle.SpentCasing;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.EntityDamageUtil;
import com.hbm.util.Library.PosDir;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class TileEntityTurretHoward extends TileEntityTurretBaseNT implements MenuProvider {

    static List<Integer> configs = new ArrayList<>();

    static {
        configs.add(BulletConfigRegistry.DGK_NORMAL.id);
    }

    public int loaded;
    public int timer;
    public float spin;
    public float lastSpin;

    public TileEntityTurretHoward(BlockPos pos, BlockState state) {
        super(ModTileEntity.TURRET_HOWARD.get(), pos, state);
    }

    public TileEntityTurretHoward(BlockEntityType<?> turret, BlockPos pos, BlockState state) {
        super(turret, pos, state);
    }

    @Override
    public List<Integer> getAmmoList() {
        return configs;
    }

    @Override
    public String getName() {
        return "container.turretHoward";
    }

    @Override
    public double getHeightOffset() {
        return 2.25D;
    }

    @Override
    public double getDecetorGrace() {
        return 3D;
    }

    @Override
    public double getTurretYawSpeed() {
        return 12D;
    }

    @Override
    public double getTurretPitchSpeed() {
        return 8D;
    }

    @Override
    public double getTurretElevation() {
        return 90D;
    }

    @Override
    public double getTurretDepression() {
        return 50D;
    }

    @Override
    public double getDecetorRange() {
        return 250D;
    }

    @Override
    public double getBarrelLength() {
        return 3.25D;
    }

    @Override
    public long getMaxPower() {
        return 50000;
    }

    @Override
    public long getConsumption() {
        return 500;
    }

    @Override
    public int getDecetorInterval() {
        return 10;
    }

    @Override
    public double getAcceptableInaccuracy() {
        return 1D;
    }

    @Override
    public boolean hasThermalVision() {
        return false;
    }

    @Override
    public boolean hasPower() {
        return power >= getConsumption();
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    @Override
    public boolean entityAcceptableTarget(Entity e) {
        if (e instanceof Player player && player.getAbilities().instabuild) return false;
        return e instanceof LivingEntity;
    }

    @Override
    public Vec3 getHorizontalOffset() {
        return new Vec3(0, 0, 0);
    }

    @Override
    public CasingEjector getEjector() {
        return ejector;
    }

    private static final CasingEjector ejector = new CasingEjector().setAngleRange(0.01F, 0.01F).setMotion(0.4, 0, 0).setAngleRange(0.02F, 0.03F);

    @Override
    protected int casingDelay() {
        return 0;
    }

    @Override
    protected Vec3 getCasingSpawnPos() {
        Vec3 pos = getTurretPos();
        Vec3 vec = new Vec3(-0.875, 0.2, -0.125);
        vec = vec.xRot((float) -rotationPitch);
        vec = vec.yRot((float) -(rotationYaw + Math.PI * 0.5));
        return new Vec3(pos.x + vec.x, pos.y + vec.y, pos.z + vec.z);
    }

    @Override
    public void tick() {
        if (Objects.requireNonNull(level).isClientSide) {
            lastSpin = spin;
            if (tPos != null) spin += 45;
            if (spin >= 360F) {
                spin -= 360F;
                lastSpin -= 360F;
            }
        } else {
            if (loaded <= 0) {
                BulletConfig conf = getFirstConfigLoaded();
                if (conf != null) {
                    this.consumeAmmo(conf);
                    level.playSound(null, worldPosition, ModSounds.TURRET_HOWARD_RELOAD.get(), net.minecraft.sounds.SoundSource.BLOCKS, 4.0F, 1F);
                    loaded = 200;
                }
            }
        }
        super.tick();
    }

    @Override
    public void updateFiringTick() {
        timer++;

        if (loaded > 0 && tPos != null) {
            SpentCasing cfg = BulletConfigRegistry.CASINGDGK;

            Objects.requireNonNull(level).playSound(null, worldPosition, ModSounds.TURRET_HOWARD_FIRE.get(), net.minecraft.sounds.SoundSource.BLOCKS, 4.0F, 0.9F + level.random.nextFloat() * 0.3F);
            level.playSound(null, worldPosition, ModSounds.TURRET_HOWARD_FIRE.get(), net.minecraft.sounds.SoundSource.BLOCKS, 4.0F, 1F + level.random.nextFloat() * 0.3F);

            for (int i = 0; i < 2; i++) {
                cachedCasingConfig = cfg;
                spawnCasing();
            }

            if (timer % 2 == 0) {
                loaded--;

                if (level.random.nextInt(100) + 1 <= WeaponConfig.CIWS_HITRATE) {
                    EntityDamageUtil.attackEntityFromIgnoreIFrame(target, ModDamageSource.causeShrapnelDamage(level),
                            2F + level.random.nextInt(2));
                }

                Vec3 pos = getTurretPos();
                Vec3 vec = new Vec3(getBarrelLength(), 0, 0);
                vec = vec.xRot((float) -rotationPitch);
                vec = vec.yRot((float) -(rotationYaw + Math.PI * 0.5));

                Vec3 hOff = new Vec3(0, 0.25, 0);
                hOff = hOff.xRot((float) -rotationPitch);
                hOff = hOff.yRot((float) -(rotationYaw + Math.PI * 0.5));

                for (int i = 0; i < 2; i++) {
                    if (i == 1) {
                        hOff = new Vec3(-hOff.x, -hOff.y, -hOff.z);
                    }

                    CompoundTag data = new CompoundTag();
                    data.putString("type", "vanillaExt");
                    data.putString("mode", "largeexplode");
                    data.putFloat("size", 1.5F);
                    data.putByte("count", (byte) 1);
                    PacketDispatcher.sendAuxParticleNT(data, pos.x + vec.x + hOff.x, pos.y + vec.y + hOff.y, pos.z + vec.z + hOff.z, level, worldPosition);
                }
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        loaded = nbt.getInt("loaded");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("loaded", loaded);
    }

    @Override
    public boolean usesCasings() {
        return true;
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerTurretHoward(windowId, inv, this);
    }

    @Override
    protected void updateConnections() {
        for (PosDir pos : getConPos()) {
            this.trySubscribe(level, pos.pos(), pos.dir());
        }
    }

    protected List<PosDir> getConPos() {
        Direction dir = getBlockState().getValue(BlockDummyable.FACING);
        Direction forward = dir.getOpposite();
        Direction right = dir.getClockWise();

        List<PosDir> list = new ArrayList<>();

        // 4 блока нижней грани
        BlockPos[] baseBlocks = {
                worldPosition,
                worldPosition.relative(right, 1),
                worldPosition.relative(forward, 1),
                worldPosition.relative(forward, 1).relative(right, 1)
        };

        for (BlockPos base : baseBlocks) {
            // Снизу каждого блока
            list.add(new PosDir(base.below(), Direction.DOWN));

            // Боковые стороны (наружу)
            if (base.getX() == worldPosition.getX() && base.getZ() == worldPosition.getZ()) {
                // Ядро
                list.add(new PosDir(base.relative(right.getOpposite(), 1), right.getOpposite()));
                list.add(new PosDir(base.relative(forward.getOpposite(), 1), forward.getOpposite()));
            } else if (base.equals(worldPosition.relative(right, 1))) {
                list.add(new PosDir(base.relative(right, 1), right));
                list.add(new PosDir(base.relative(forward.getOpposite(), 1), forward.getOpposite()));
            } else if (base.equals(worldPosition.relative(forward, 1))) {
                list.add(new PosDir(base.relative(right.getOpposite(), 1), right.getOpposite()));
                list.add(new PosDir(base.relative(forward, 1), forward));
            } else if (base.equals(worldPosition.relative(forward, 1).relative(right, 1))) {
                list.add(new PosDir(base.relative(right, 1), right));
                list.add(new PosDir(base.relative(forward, 1), forward));
            }
        }

        return list;
    }
}