package com.hbm.datagen;

public class ToolInfo {
    private final ToolType tool;
    private final ToolLevel level;

    public enum ToolType {
        PICKAXE, AXE, SHOVEL, NONE
    }

    public enum ToolLevel {
        WOOD, STONE, IRON, DIAMOND, NETHERITE, NONE
    }

    private ToolInfo(ToolType tool, ToolLevel level) {
        this.tool = tool;
        this.level = level;
    }

    public static ToolInfo pickaxe(ToolLevel level) {
        return new ToolInfo(ToolType.PICKAXE, level);
    }

    public static ToolInfo axe(ToolLevel level) {
        return new ToolInfo(ToolType.AXE, level);
    }

    public static ToolInfo shovel(ToolLevel level) {
        return new ToolInfo(ToolType.SHOVEL, level);
    }

    public static ToolInfo none() {
        return new ToolInfo(ToolType.NONE, ToolLevel.NONE);
    }

    public ToolType getTool() { return tool; }
    public ToolLevel getLevel() { return level; }
}