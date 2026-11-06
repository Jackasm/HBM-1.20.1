package com.hbm.tileentity.deco;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockGeysir;
import com.hbm.entity.ModEntities;
import com.hbm.entity.particle.EntityOrangeFX;
import com.hbm.entity.projectile.EntityShrapnel;
import com.hbm.entity.projectile.EntityWaterSplash;
import com.hbm.network.PacketDispatcher;

import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class TileEntityGeysir extends BlockEntity {

    private int timer;
    private static final int NETWORK_RANGE = 75;

    public TileEntityGeysir(BlockPos pos, BlockState state) {
        super(ModTileEntity.GEYSIR.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof BlockGeysir)) return;

        // Проверка: над гейзером воздух
        if (!level.getBlockState(worldPosition.above()).isAir()) return;

        boolean active = state.getValue(BlockGeysir.ACTIVE);

        timer--;

        if (timer <= 0) {
            // Переключаем активность
            boolean newActive = !active;
            level.setBlock(worldPosition, state.setValue(BlockGeysir.ACTIVE, newActive), 2);
            timer = getDelay(newActive);
            setChanged();
        }

        // Выполняем эффект, только когда активен
        if (active) {
            perform();
        }
    }

    private void water() {
        if (level == null) return;

        EntityWaterSplash splash = new EntityWaterSplash(
                ModEntities.WATER_SPLASH.get(),
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 1.5,
                worldPosition.getZ() + 0.5,
                level
        );

        splash.setMaxAge(80);

        splash.setDeltaMovement(
                level.random.nextGaussian() * 0.35,
                2.0,
                level.random.nextGaussian() * 0.35
        );

        level.addFreshEntity(splash);
    }

    private void chlorine() {
        if (level == null) return;

        for (int i = 0; i < 3; i++) {
            EntityOrangeFX fx = new EntityOrangeFX(
                    level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.5,
                    worldPosition.getZ() + 0.5,
                    level.random.nextGaussian() * 0.45,
                    timer * 0.3,
                    level.random.nextGaussian() * 0.45
            );

            fx.setMaxAge(60);

            double velY = Math.min(timer * 0.3, 2.0); // максимум 2 блока/тик
            fx.setDeltaMovement(
                    level.random.nextGaussian() * 0.45,
                    velY,
                    level.random.nextGaussian() * 0.45
            );

            level.addFreshEntity(fx);
        }
    }

    private void vapor() {
        if (level == null) return;

        AABB box = new AABB(
                worldPosition.getX() - 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() - 0.5,
                worldPosition.getX() + 1.5, worldPosition.getY() + 2.0, worldPosition.getZ() + 1.5
        );

        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, box)) {
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 0, true, false));
        }
    }

    private void fire() {
        if (level == null) return;

        int range = 32;
        AABB playerBox = new AABB(
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5
        ).inflate(range, range, range);

        if (level.getEntitiesOfClass(Player.class, playerBox).isEmpty()) {
            return;
        }

        if (level.random.nextInt(3) == 0) {
            EntityShrapnel shrapnel = new EntityShrapnel(level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.5,
                    worldPosition.getZ() + 0.5);

            double velY = 0.5 + level.random.nextDouble() * 0.5; // 0.5..1.0 вместо привязки к timer
            shrapnel.setDeltaMovement(
                    level.random.nextGaussian() * 0.05,
                    velY,
                    level.random.nextGaussian() * 0.05
            );

            // Ограничиваем жизнь, чтобы не летел бесконечно
            // если у EntityShrapnel нет setMaxAge — добавь поле и метод
            shrapnel.setMaxAge(80);

            level.addFreshEntity(shrapnel);
        }

        if (timer % 2 == 0) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "gasfire");
            data.putDouble("mX", level.random.nextGaussian() * 0.05);
            data.putDouble("mY", 0.2);
            data.putDouble("mZ", level.random.nextGaussian() * 0.05);

            PacketDispatcher.sendToAllAround(
                    new AuxParticlePacketNT(data,
                            worldPosition.getX() + 0.5,
                            worldPosition.getY() + 1.1,
                            worldPosition.getZ() + 0.5),
                    level,
                    worldPosition,
                    NETWORK_RANGE
            );
        }
    }

    private int getDelay(boolean active) {
        if (level == null) return 100;
        RandomSource rand = level.random;
        Block b = level.getBlockState(worldPosition).getBlock();

        if (b == ModBlocks.GEYSIR_WATER.get()) {
            return active ? 100 + rand.nextInt(40) : 600;
        } else if (b == ModBlocks.GEYSIR_CHLORINE.get()) {
            return active ? 400 + rand.nextInt(100) : 400;
        } else if (b == ModBlocks.GEYSIR_VAPOR.get()) {
            return active ? 30 + rand.nextInt(20) : 400;
        } else if (b == ModBlocks.GEYSIR_NETHER.get()) {
            return active ? 80 + rand.nextInt(60) : (rand.nextBoolean() ? 300 : 450);
        }
        return 100;
    }

    private void perform() {
        if (level == null) return;

        Block b = level.getBlockState(worldPosition).getBlock();

        if (b == ModBlocks.GEYSIR_WATER.get()) {
            water();
        } else if (b == ModBlocks.GEYSIR_CHLORINE.get()) {
            chlorine();
        } else if (b == ModBlocks.GEYSIR_VAPOR.get()) {
            vapor();
        } else if (b == ModBlocks.GEYSIR_NETHER.get()) {
            fire();
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        timer = nbt.getInt("timer");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("timer", timer);
    }

    // Для обратной совместимости с оригиналом
    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getTimer() {
        return timer;
    }
}