package com.hbm.items.armor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IHelmetOverlay {

    @OnlyIn(Dist.CLIENT)
    void renderHelmetOverlay(GuiGraphics guiGraphics, ItemStack stack, Player player,
                             int width, int height, float partialTicks);
}
