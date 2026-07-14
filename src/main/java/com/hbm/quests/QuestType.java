package com.hbm.quests;

public enum QuestType {
    CRAFT("craft"),
    ITEM_CHECK("item_check"),
    ACHIEVEMENT("achievement"),
    CUSTOM("custom");

    private final String name;

    QuestType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static QuestType fromName(String name) {
        for (QuestType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return CUSTOM;
    }
}