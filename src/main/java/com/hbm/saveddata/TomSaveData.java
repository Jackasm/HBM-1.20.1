package com.hbm.saveddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomSaveData extends SavedData {

    public final static String DATA_NAME = "impactData";
    public float dust;
    public float fire;
    public boolean impact;

    private static TomSaveData lastCachedUnsafe = null;

    public TomSaveData() {
        // Конструктор по умолчанию
    }

    public TomSaveData(float dust, float fire, boolean impact) {
        this.dust = dust;
        this.fire = fire;
        this.impact = impact;
    }

    @Nullable
    public static TomSaveData forWorld(Level world) {
        if (world == null || world.isClientSide) {
            return null;
        }

        if (world instanceof ServerLevel serverLevel) {
            TomSaveData data = serverLevel.getDataStorage().computeIfAbsent(
                    TomSaveData::load,  // Функция загрузки из NBT
                    TomSaveData::new,   // Поставщик нового экземпляра
                    DATA_NAME           // Имя данных
            );

            lastCachedUnsafe = data;
            return data;
        }

        return null;
    }

    /**
     * Certain biome events do not have access to a world instance (very very bad),
     * in those cases we have to rely on a possibly incorrect cached result.
     * However, due to the world gen invoking TomSaveData.forWorld() quite a lot,
     * it is safe to say that in most cases, we do end up with the correct result.
     */
    @Nullable
    public static TomSaveData getLastCachedOrNull() {
        return lastCachedUnsafe;
    }

    public static void resetLastCached() {
        lastCachedUnsafe = null;
    }

    public static TomSaveData load(CompoundTag nbt) {
        TomSaveData data = new TomSaveData();
        data.dust = nbt.getFloat("dust");
        data.fire = nbt.getFloat("fire");
        data.impact = nbt.getBoolean("impact");
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        nbt.putFloat("dust", dust);
        nbt.putFloat("fire", fire);
        nbt.putBoolean("impact", impact);
        return nbt;
    }
}