package com.hbm.items.weapon.sedna.hud;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AmmoCounterHUD implements IHUDComponent {

    private final int receiverIndex;
    private boolean mirrored = false;
    private boolean noCounter = false;

    public AmmoCounterHUD(int receiverIndex) {
        this.receiverIndex = receiverIndex;
    }

    public AmmoCounterHUD mirror() {
        this.mirrored = true;
        return this;
    }

    public AmmoCounterHUD noCounter() {
        this.noCounter = true;
        return this;
    }

    @Override
    public int getComponentHeight(@NotNull Player player, @NotNull ItemStack stack) {
        return 24;
    }

    @Override
    public void renderHUDComponent(@NotNull GuiGraphics guiGraphics,
                                   @NotNull Player player,
                                   @NotNull ItemStack stack,
                                   int bottomOffset,
                                   int gunIndex) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != player) return;

        if (!(stack.getItem() instanceof GunItem gunItem)) return;

        var config = gunItem.getConfig(stack, gunIndex);
        if (config == null) return;

        Receiver[] receivers = config.getReceivers(stack);
        if (receivers == null || receiverIndex >= receivers.length) return;

        Receiver receiver = receivers[receiverIndex];
        IMagazine mag = receiver.getMagazine(stack);
        if (mag == null) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Calculate position
        int pX = screenWidth / 2 + (mirrored ? -(62 + 36 + 52) : (62 + 36)) + (noCounter ? 14 : 0);
        int pY = screenHeight - bottomOffset - 23;

        // Render ammo count
        if (!noCounter) {
            String ammoText = mag.reportAmmoStateForHUD(stack, player);
            guiGraphics.drawString(mc.font, ammoText, pX + 17, pY + 6, 0xFFFFFF);
        }

        // Render magazine icon
        ItemStack icon = mag.getIconForHUD(stack, player);
        if (!icon.isEmpty()) {
            guiGraphics.renderItem(icon, pX, pY);
            guiGraphics.renderItemDecorations(mc.font, icon, pX, pY);
        }
    }
}