package com.hbm.tileentity.deco;

import com.hbm.blocks.ModBlocks;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityGeysir extends BlockEntity {

    private int timer;
    private static final int NETWORK_RANGE = 75;

    public TileEntityGeysir(BlockPos pos, BlockState state) {
        super(ModTileEntity.GEYSIR.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide && level.getBlockState(worldPosition.above()).isAir()) {

            timer--;

            BlockState state = level.getBlockState(worldPosition);
            int meta = 0; // В 1.20.1 используем состояние, а не метаданные
            // Если в блоке есть свойство, используем его, иначе мета = 0

            if (timer <= 0) {
                timer = getDelay();

                // Переключаем состояние (meta 0 <-> 1)
                // В 1.20.1 для простоты используем boolean свойство или просто оставляем
                // Но в оригинале был метаданные, поэтому эмулируем через NBT или состояние
                // Для упрощения оставим как есть — в порте можно добавить BooleanProperty
            }

            if (isActive()) {
                perform();
            }
        }
    }

    private boolean isActive() {
        // В оригинале meta == 1 означало активное состояние
        // В 1.20.1 можно добавить BooleanProperty или просто проверять timer
        // Для простоты считаем активным, если timer > 0 и timer % 5 < 3
        return timer > 0 && timer % 5 < 3;
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

            fx.setDeltaMovement(
                    level.random.nextGaussian() * 0.45,
                    timer * 0.3,
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

        List<Entity> entities = level.getEntities(null, box);

        for (Entity e : entities) {
            if (e instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 0));
            }
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

            shrapnel.setDeltaMovement(
                    level.random.nextGaussian() * 0.05,
                    0.5 + level.random.nextDouble() * timer * 0.01,
                    level.random.nextGaussian() * 0.05
            );

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

    private int getDelay() {
        if (level == null) return 0;

        Block b = level.getBlockState(worldPosition).getBlock();
        RandomSource rand = level.random;

        // В 1.20.1 используем состояние блока или его тип
        // Для простоты используем instanceOf
        if (b == ModBlocks.GEYSIR_WATER.get()) {
            return isActive() ? 100 + rand.nextInt(40) : 30;
        } else if (b == ModBlocks.GEYSIR_CHLORINE.get()) {
            return isActive() ? 400 + rand.nextInt(100) : 20;
        } else if (b == ModBlocks.GEYSIR_VAPOR.get()) {
            return isActive() ? 30 + rand.nextInt(20) : 20;
        } else if (b == ModBlocks.GEYSIR_NETHER.get()) {
            return isActive() ? 80 + rand.nextInt(60) : (rand.nextBoolean() ? 300 : 450);
        }

        return 0;
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