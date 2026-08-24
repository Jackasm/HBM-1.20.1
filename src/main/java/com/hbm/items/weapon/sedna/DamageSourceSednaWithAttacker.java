package com.hbm.items.weapon.sedna;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

public class DamageSourceSednaWithAttacker extends DamageSourceSednaNoAttacker {

    private final Entity directEntity;
    private final Entity causingEntity;

    public DamageSourceSednaWithAttacker(String typeName, Entity directEntity, Entity causingEntity, Level level) {
        super(typeName, level);
        this.directEntity = directEntity;
        this.causingEntity = causingEntity;
    }

    public DamageSourceSednaWithAttacker(String typeName, Entity directEntity, Entity causingEntity, Holder<DamageType> typeHolder) {
        super(typeName, typeHolder);
        this.directEntity = directEntity;
        this.causingEntity = causingEntity;
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(LivingEntity entity) {
        Component entityName = entity.getDisplayName();
        Component killerName = this.causingEntity != null ?
                this.causingEntity.getDisplayName() :
                Component.literal("Unknown").withStyle(ChatFormatting.OBFUSCATED);
        return Component.translatable("death.sedna." + this.typeName + ".attacker", entityName, killerName);
    }

    @Override
    public Entity getDirectEntity() {
        return this.directEntity;
    }

    @Override
    public Entity getEntity() {
        return this.causingEntity;
    }
}