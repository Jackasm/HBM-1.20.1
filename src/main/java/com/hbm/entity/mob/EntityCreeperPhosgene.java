package com.hbm.entity.mob;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityMist;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityCreeperPhosgene extends Creeper {

    private int customSwell = 0;

    public EntityCreeperPhosgene(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void tick() {
        if (this.isAlive() && this.getSwellDir() > 0) {
            customSwell++;
            int customMaxSwell = 20;
            if (customSwell >= customMaxSwell) {
                explodeCreeper();
            }
        } else {
            customSwell = 0;
        }
        super.tick();
    }

    public void explodeCreeper() {
        if (this.level().isClientSide) return;

        // ВСЁ выполняется отложенно
        Objects.requireNonNull(this.level().getServer()).execute(() -> {
            this.discard();
            boolean griefing = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            this.level().explode(this, this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(),
                    2.0F, Level.ExplosionInteraction.MOB);
            cleanArea();

            if (griefing) {
                EntityMist mist = new EntityMist(ModEntities.MIST.get(), this.level());
                mist.setFluidType(Fluids.PHOSGENE.get());
                mist.setPos(this.getX(), this.getY(), this.getZ());
                mist.setArea(10, 5);
                mist.setDuration(150);
                this.level().addFreshEntity(mist);
            }
        });
    }

    private void cleanArea() {
        double radius = this.isPowered() ? 14 : 7;
        AABB box = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);
        List<ItemEntity> items = new ArrayList<>(this.level().getEntitiesOfClass(ItemEntity.class, box));
        for (ItemEntity item : items) {
            if (item.getY() > this.getY() + 2) {
                item.discard();
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        boolean bypassesArmor = source.typeHolder().is(DamageTypeTags.BYPASSES_ARMOR);
        boolean bypassesInvul = source.typeHolder().is(DamageTypeTags.BYPASSES_INVULNERABILITY);

        if (!bypassesArmor && !bypassesInvul) {
            amount -= 4F;
        }
        if (amount <= 0) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean checkSpawnObstruction(@NotNull LevelReader level) {
        BlockPos pos = this.blockPosition();
        BlockState below = level.getBlockState(pos.below());
        BlockState current = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        if (current.liquid() || below.liquid() || above.liquid()) return false;
        if (!below.isSolid()) return false;
        if (!current.isAir()) return false;
        if (!above.isAir()) return false;
        return level.getBlockState(pos.above(2)).isAir();
    }
}