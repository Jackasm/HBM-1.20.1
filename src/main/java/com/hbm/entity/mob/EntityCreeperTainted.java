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

    private int swell = 0;
    private final int maxSwell = 30;

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
        super.tick();

        if (this.level().isClientSide) return;

        // Регенерация
        if (this.isAlive() && this.getHealth() < this.getMaxHealth() && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }

        // Свайп для взрыва
        if (this.isIgnited() || this.getSwellDir() > 0) {
            swell++;
        } else {
            if (swell > 0) {
                swell -= 2;
                if (swell < 0) swell = 0;
            }
        }

        if (swell >= maxSwell) {
            explodeCreeperCustom();
        }
    }

    private void explodeCreeperCustom() {
        if (this.level().isClientSide) return;

        boolean griefing = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

        // Ванильный взрыв + заражение
        if (griefing) {
            // Стандартный взрыв крипера
            this.level().explode(this, this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                    this.isPowered() ? 7 : 3, Level.ExplosionInteraction.MOB);
            // Заражение вокруг
            spreadTaint();
        } else {
            this.level().explode(this, this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                    this.isPowered() ? 7 : 3, Level.ExplosionInteraction.MOB);
        }

        this.discard();
    }

    private void spreadTaint() {
        RandomSource rand = this.getRandom();
        boolean trails = ServerConfig.TAINT_TRAILS;
        int count = this.isPowered() ? 255 : 85;
        int range = this.isPowered() ? 15 : 7;

        for (int i = 0; i < count; i++) {
            int dx = rand.nextInt(range) - range / 2;
            int dy = rand.nextInt(range) - range / 2;
            int dz = rand.nextInt(range) - range / 2;
            BlockPos pos = this.blockPosition().offset(dx, dy, dz);
            BlockState state = this.level().getBlockState(pos);

            if (!state.isAir() && state.isSolidRender(this.level(), pos)) {
                int meta = trails ? rand.nextInt(3) : rand.nextInt(3) + 5;
                if (!this.isPowered()) {
                    meta += 4;
                }
                this.level().setBlock(pos, ModBlocks.TAINT.get().defaultBlockState()
                        .setValue(BlockTaint.META, meta), 2);
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource source) {
        this.spawnAtLocation(new ItemStack(Blocks.TNT));
        super.die(source);
    }
}