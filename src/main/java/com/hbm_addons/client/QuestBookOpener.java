package com.hbm_addons.client;

import com.hbm_addons.inventory.gui.QuestBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Клиентская точка открытия книги квестов — из предмета и из клавиши. */
@OnlyIn(Dist.CLIENT)
public final class QuestBookOpener {

    private QuestBookOpener() {
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new QuestBookScreen(mc.player));
        }
    }
}
