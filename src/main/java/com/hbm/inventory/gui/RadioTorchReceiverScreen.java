package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerRadioTorch;

import com.hbm.util.RefStrings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.hbm.util.ResLocation.ResLocation;

public class RadioTorchReceiverScreen extends RadioTorchScreenBase {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/machine/gui_rtty_receiver.png");

    public RadioTorchReceiverScreen(ContainerRadioTorch menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
    // getFrequencyOffset() по умолчанию = 0
}