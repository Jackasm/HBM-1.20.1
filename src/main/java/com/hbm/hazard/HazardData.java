package com.hbm.hazard;

import com.hbm.hazard.type.HazardTypeBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HazardData {

    /**
     * Purges all previously loaded data when read, useful for when specific items should fully override ore dict data.
     */
    private boolean doesOverride = false;

    /**
     * MUTEX, even more precise to make only specific entries mutually exclusive, for example oredict aliases such as plutonium238 and pu238.
     * Does the opposite of overrides, if a previous entry collides with this one, this one will yield.
     * <p>
     * RESERVED BITS (please keep this up to date)
     * -1: oredict ("ingotX")
     */
    private int mutexBits = 0b0000_0000_0000_0000_0000_0000_0000_0000;

    private final List<HazardEntry> entries = new ArrayList<>();

    public HazardData addEntry(@NotNull HazardTypeBase hazard) {
        return this.addEntry(hazard, 1.0F);
    }

    public HazardData addEntry(@NotNull HazardTypeBase hazard, float level) {
        return this.addEntry(new HazardEntry(hazard, level));
    }

    public HazardData addEntry(@NotNull HazardTypeBase hazard, float level, boolean override) {
        HazardData data = this.addEntry(new HazardEntry(hazard, level));
        if (override) {
            this.doesOverride = true;
        }
        return data;
    }

    public HazardData addEntry(@NotNull HazardEntry entry) {
        this.entries.add(entry);
        return this;
    }

    public HazardData setMutex(int mutex) {
        this.mutexBits = mutex;
        return this;
    }

    public int getMutex() {
        return mutexBits;
    }

    public boolean doesOverride() {
        return doesOverride;
    }

    @NotNull
    public List<HazardEntry> getEntries() {
        return entries;
    }
}