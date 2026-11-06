package com.hbm.items.special;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;

public class ItemModRecord extends RecordItem {

    private final String recordName;

    public ItemModRecord(String recordName, SoundEvent sound, Properties properties) {
        // 15 — длина проигрывания в секундах (стандарт для пластинок).
        // comparatorOutput — сила сигнала компаратора (1..15).
        super(15, sound, properties.stacksTo(1).rarity(Rarity.RARE), 15);
        this.recordName = recordName;
    }

    public String getRecordName() {
        return recordName;
    }
}