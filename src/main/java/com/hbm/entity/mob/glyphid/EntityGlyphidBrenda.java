package com.hbm.entity.mob.glyphid;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityMist;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityGlyphidBrenda extends EntityGlyphid {

    public EntityGlyphidBrenda(EntityType<? extends EntityGlyphidBrenda> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
    }

    @Override
    public float getScale() {
        return 2.0F;
    }

    @Override
    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getBrenda();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGlyphid.createAttributes()
                .add(Attributes.MAX_HEALTH, GlyphidStats.getStats().getBrenda().health)
                .add(Attributes.MOVEMENT_SPEED, GlyphidStats.getStats().getBrenda().speed)
                .add(Attributes.ATTACK_DAMAGE, GlyphidStats.getStats().getBrenda().damage);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.12, 2), 100);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide && this.getHealth() <= 0.0F) {
            // Создаём туман с феромонами
            EntityMist mist = new EntityMist(this.level());
            mist.setFluidType(Fluids.PHEROMONE.get());
            mist.setPos(this.getX(), this.getY(), this.getZ());
            mist.setArea(14, 6);
            mist.setDuration(80);
            this.level().addFreshEntity(mist);

            // Спавним 12 обычных глифидов
            for (int i = 0; i < 12; ++i) {
                EntityGlyphid glyphid = new EntityGlyphid(ModEntities.GLYPHID.get(), this.level());
                glyphid.setPos(this.getX(), this.getY() + 0.5D, this.getZ());
                glyphid.setYRot(this.random.nextFloat() * 360.0F);
                glyphid.setXRot(0.0F);
                this.level().addFreshEntity(glyphid);
                // Даём случайное движение
                glyphid.setDeltaMovement(
                        this.random.nextGaussian() * 0.5,
                        0,
                        this.random.nextGaussian() * 0.5
                );
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        if (this.random.nextInt(3) == 0) {
            this.spawnAtLocation(new ItemStack(ModItems.GLYPHID_GLAND.get()), 1);
        }
    }
}