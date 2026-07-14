package com.hbm.entity.mob.glyphid;

import com.hbm.entity.effect.EntityMist;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityGlyphidBehemoth extends EntityGlyphid {

    public int timer = 120;
    public int breathTime = 0;

    public EntityGlyphidBehemoth(EntityType<? extends EntityGlyphidBehemoth> type, Level world) {
        super(type, world);
    }


    @Override
    public float getScale() { return 1.5F; }

    @Override
    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getBehemoth();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGlyphid.createAttributes()
                .add(Attributes.MAX_HEALTH, GlyphidStats.getStats().getBehemoth().health)
                .add(Attributes.MOVEMENT_SPEED, GlyphidStats.getStats().getBehemoth().speed)
                .add(Attributes.ATTACK_DAMAGE, GlyphidStats.getStats().getBehemoth().damage);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Entity e = this.getTarget();
        if (e == null) {
            timer = 120;
            breathTime = 0;
        } else {
            if (breathTime > 0) {
                if (!this.swinging) {
                    this.swing(this.getUsedItemHand());
                }
                acidAttack();
                this.setYRot(this.yRotO);
                breathTime--;
            } else if (--timer <= 0) {
                breathTime = 120;
                timer = 120;
            }
        }
    }

    public void acidAttack() {
        if (!this.level().isClientSide && this.getTarget() instanceof LivingEntity && this.distanceTo(this.getTarget()) < 20) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2 * 20, 6));
            EntityChemical chem = new EntityChemical(this.level(), this, 0, 0, 0);
            chem.setFluid(Fluids.SULFURIC_ACID.get());
            this.level().addFreshEntity(chem);
        }
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            EntityMist mist = new EntityMist(this.level());
            mist.setFluidType(Fluids.SULFURIC_ACID.get());
            mist.setPos(this.getX(), this.getY(), this.getZ());
            mist.setArea(10, 4);
            mist.setDuration(120);
            this.level().addFreshEntity(mist);
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        this.spawnAtLocation(new ItemStack(ModItems.GLYPHID_GLAND.get()), 1);
    }

    @Override
    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.15, 2), 100);
    }

    public int swingDuration() {
        return 100;
    }
}