package com.hbm.items.weapon.sedna.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IHUDComponent {

    /**
     * Get the height of this HUD component in pixels.
     * Used for stacking components vertically.
     */
    int getComponentHeight(@NotNull Player player, @NotNull ItemStack stack);

    /**
     * Render the HUD component.
     *
     * @param player The player holding the weapon
     * @param stack The weapon item stack
     * @param bottomOffset The vertical offset from the bottom of the screen
     * @param gunIndex The index of the gun config (for multi-config weapons)
     */
    void renderHUDComponent(@NotNull GuiGraphics guiGraphics,
                            @NotNull Player player,
                            @NotNull ItemStack stack,
                            int bottomOffset,
                            int gunIndex);
}