package com.hbm.items.weapon.sedna.hud;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DurabilityBarHUD implements IHUDComponent {

    private static final ResourceLocation MISC_TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/misc/overlay_misc.png");

    private final boolean mirrored;

    public DurabilityBarHUD() {
        this(false);
    }

    public DurabilityBarHUD(boolean mirrored) {
        this.mirrored = mirrored;
    }

    @Override
    public int getComponentHeight(@NotNull Player player, @NotNull ItemStack stack) {
        return 3; // Высота бара прочности
    }

    @Override
    public void renderHUDComponent(@NotNull GuiGraphics guiGraphics,
                                   @NotNull Player player,
                                   @NotNull ItemStack stack,
                                   int bottomOffset,
                                   int gunIndex) {

        if (!(stack.getItem() instanceof GunItem gun)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != player) return;

        var config = gun.getConfig(stack, gunIndex);
        if (config == null) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int pX = screenWidth / 2 + (mirrored ? -(62 + 36 + 52) : (62 + 36));
        int pY = screenHeight - bottomOffset - 3;

        float maxDura = config.getDurability(stack);
        if (maxDura <= 0) return;

        float wear = GunItem.getWear(stack, gunIndex);
        float durability = Math.max(0, maxDura - wear);
        int durabilityPercent = (int) ((durability * 100) / maxDura);
        durabilityPercent = Math.max(0, Math.min(100, durabilityPercent));

        guiGraphics.blit(MISC_TEXTURE,
                pX, pY,
                94, 0,
                52, 3
        );

        int filledWidth = (durabilityPercent * 50) / 100;
        if (filledWidth > 0) {
            guiGraphics.fill(
                    pX + 1, pY + 1,
                    pX + 1 + filledWidth, pY + 2,
                    0xFF00FF00
            );
        }

    }
}
