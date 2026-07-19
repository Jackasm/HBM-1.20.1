package com.hbm.entity.mob;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.items.DictFrame;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.factory.GunFactory.EnumAmmo;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.ModDamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class EntityCreeperNuclear extends Creeper {

    public EntityCreeperNuclear(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isDeadOrDying()) return false;
        if (source.is(ModDamageSource.RADIATION) || source.is(ModDamageSource.MUD_POISONING)) {
            if (this.isAlive()) this.heal(amount);
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        this.spawnAtLocation(new ItemStack(Blocks.TNT));
        if (random.nextInt(3) == 0) {
            this.spawnAtLocation(new ItemStack(ModItems.COIN_CREEPER.get()));
        }
        if (source.getEntity() instanceof Skeleton ||
                (source.is(DamageTypeTags.IS_PROJECTILE) &&
                        source.getDirectEntity() instanceof Arrow &&
                        ((Arrow) source.getDirectEntity()).getOwner() == null)) {
            this.spawnAtLocation(DictFrame.fromOne(ModAmmoItems.AMMO_NUKE_STANDARD.get(), EnumAmmo.NUKE_STANDARD));
        }
        AABB box = this.getBoundingBox().inflate(50, 50, 50);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, box);
        for (Player player : players) {
            if (player instanceof ServerPlayer sp) {
                ModCriteriaTriggers.NUCLEAR_CREEPER_DEATH.trigger(sp);
            }
        }
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            AABB box = this.getBoundingBox().inflate(5.0D, 5.0D, 5.0D);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, box,
                    e -> e != this && e.isAlive());
            for (LivingEntity e : entities) {
                ContaminationUtil.contaminate(e, HazardType.RADIATION, ContaminationType.CREATIVE, 0.25F);
            }
        }
        if (this.isAlive() && this.getHealth() < this.getMaxHealth() && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }
        super.tick();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.level().isClientSide) return;

        // ВСЁ выполняется отложенно
        Objects.requireNonNull(this.level().getServer()).execute(() -> {
            boolean griefing = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            if (griefing && this.isPowered()) {
                // Большой взрыв
                CompoundTag data = new CompoundTag();
                data.putString("type", "muke");
                PacketDispatcher.sendAuxParticleNT(data, this.getX(), this.getY() + 0.5, this.getZ(), this);
                this.level().playSound(null, this.getX(), this.getY() + 0.5, this.getZ(),
                        ModSounds.MUKE_EXPLOSION.get(), SoundSource.HOSTILE, 15.0F, 1.0F);

                EntityNukeExplosionMK5 entity = EntityNukeExplosionMK5.statFac(
                        this.level(), 50, this.getX(), this.getY(), this.getZ());
                this.level().addFreshEntity(entity);
            } else if (griefing) {
                // Маленький взрыв (тоже отложен)
                ExplosionNukeSmall.explode(this.level(), this.getX(), this.getY() + 0.5, this.getZ(),
                        ExplosionNukeSmall.PARAMS_MEDIUM);
            }
        });
    }
}