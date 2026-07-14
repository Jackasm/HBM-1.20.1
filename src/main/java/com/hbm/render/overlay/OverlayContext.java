// OverlayContext.java
package com.hbm.render.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public record OverlayContext(Minecraft mc, GuiGraphics guiGraphics) {
    // Простая запись для передачи контекста
}