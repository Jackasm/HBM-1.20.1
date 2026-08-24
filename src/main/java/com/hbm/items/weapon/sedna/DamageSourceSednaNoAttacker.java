package com.hbm.items.weapon.sedna;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class DamageSourceSednaNoAttacker extends DamageSource {

    protected final String typeName;

    public DamageSourceSednaNoAttacker(String typeName, Level level) {
        this(typeName, getGenericHolder(level));
    }

    public DamageSourceSednaNoAttacker(String typeName, Holder<DamageType> typeHolder) {
        super(typeHolder);
        this.typeName = typeName.toLowerCase(Locale.US);
    }

    private static Holder<DamageType> getGenericHolder(Level level) {
        return level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.GENERIC);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(LivingEntity entity) {
        return Component.translatable("death.sedna." + this.typeName, entity.getDisplayName());
    }
}