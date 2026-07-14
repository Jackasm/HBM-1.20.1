package com.hbm.entity.mob;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockTaint;
import com.hbm.config.ServerConfig;
import com.hbm.extprop.IRadiationImmune;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EntityCreeperTainted extends Creeper implements IRadiationImmune {

    public EntityCreeperTainted(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    public void tick() {
        // Регенерация здоровья (каждые 10 тиков)
        if (this.isAlive() && this.getHealth() < this.getMaxHealth() && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }
        super.tick();
    }

    @Override
    public void die(@NotNull DamageSource source) {
        // Спавним TNT перед смертью
        this.spawnAtLocation(new ItemStack(Blocks.TNT));
        super.die(source);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!this.level().isClientSide) {
            // Проверяем, что крипер умер от взрыва, а не от урона
            boolean griefing = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            if (griefing) {
                spreadTaint(this);
            }
        }
    }
    private static void spreadTaint(EntityCreeperTainted creeper) {
        RandomSource rand = creeper.getRandom();
        boolean trails = ServerConfig.TAINT_TRAILS; // используйте свой конфиг

        Level level = creeper.level();
        double x = creeper.getX();
        double y = creeper.getY();
        double z = creeper.getZ();

        if (creeper.isPowered()) {
            for (int i = 0; i < 255; i++) {
                int a = rand.nextInt(15) + (int) x - 7;
                int b = rand.nextInt(15) + (int) y - 7;
                int c = rand.nextInt(15) + (int) z - 7;
                BlockPos pos = new BlockPos(a, b, c);
                BlockState state = level.getBlockState(pos);

                if (!state.isAir() && state.isSolidRender(level, pos)) {
                    int meta = trails ? rand.nextInt(3) : rand.nextInt(3) + 5;
                    level.setBlock(pos, ModBlocks.TAINT.get().defaultBlockState().setValue(BlockTaint.META, meta), 2);
                }
            }
        } else {
            for (int i = 0; i < 85; i++) {
                int a = rand.nextInt(7) + (int) x - 3;
                int b = rand.nextInt(7) + (int) y - 3;
                int c = rand.nextInt(7) + (int) z - 3;
                BlockPos pos = new BlockPos(a, b, c);
                BlockState state = level.getBlockState(pos);

                if (!state.isAir() && state.isSolidRender(level, pos)) {
                    int meta = trails ? rand.nextInt(3) + 4 : rand.nextInt(6) + 10;
                    level.setBlock(pos, ModBlocks.TAINT.get().defaultBlockState().setValue(BlockTaint.META, meta), 2);
                }
            }
        }
    }
}