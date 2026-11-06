package com.hbm.items.special;

public class ScramblingNameHolder {
    public static final String[] NAMES = {
            "THE DEFAULT", "NEXT ONE", "ANOTHER ONE", "NON-STANDARD NAME",
            "AMBIGUOUS TITLE", "SHORT"
    };

    public static ScramblingName NAME = new ScramblingName(NAMES[0]);

    public static void update() {
        NAME.updateTick(NAMES);
    }
}