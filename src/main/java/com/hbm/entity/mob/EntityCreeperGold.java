package com.hbm.entity.mob;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityCreeperGold extends Creeper {

    public EntityCreeperGold(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public boolean checkSpawnObstruction(@NotNull LevelReader level) {
        BlockPos pos = this.blockPosition();
        BlockState below = level.getBlockState(pos.below());
        BlockState current = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        if (this.getY() > 40) return false;
        if (current.liquid() || below.liquid() || above.liquid()) return false;
        if (!below.isSolid()) return false;
        if (!current.isAir()) return false;
        if (!above.isAir()) return false;
        return level.getBlockState(pos.above(2)).isAir();
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        int amount = source.getEntity() != null ? 5 + random.nextInt(6) : 3;
        for (int i = 0; i < amount; ++i) {
            this.spawnAtLocation(new ItemStack(ModItems.CRYSTAL_GOLD.get()));
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.level().isClientSide) return;

        // ВСЁ выполняется отложенно
        Objects.requireNonNull(this.level().getServer()).execute(() -> {
            boolean griefing = this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING);

            if (griefing) {
                ExplosionVNT vnt = new ExplosionVNT(this.level(), this.getX(), this.getY(), this.getZ(),
                        this.isPowered() ? 14 : 7, this);
                vnt.setBlockAllocator(new BlockAllocatorBulkie(60, this.isPowered() ? 32 : 16));
                vnt.setBlockProcessor(new BlockProcessorStandard().withBlockEffect(new BlockMutatorBulkie(Blocks.GOLD_ORE)));
                vnt.setEntityProcessor(new EntityProcessorStandard().withRangeMod(0.5F));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.setSFX(new ExplosionEffectStandard());
                vnt.explode();
            } else {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                        this.isPowered() ? 7 : 3, Level.ExplosionInteraction.MOB);
            }

            cleanArea();
        });
    }

    private void cleanArea() {
        double radius = this.isPowered() ? 14 : 7;
        AABB box = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius);
        // Копия списка
        List<ItemEntity> items = new ArrayList<>(this.level().getEntitiesOfClass(ItemEntity.class, box));
        for (ItemEntity item : items) {
            if (item.getY() > this.getY() + 2) {
                item.discard();
            }
        }
    }
}