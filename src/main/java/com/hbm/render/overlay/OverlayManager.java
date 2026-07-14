// OverlayManager.java - главный управляющий класс
package com.hbm.render.overlay;

import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OverlayManager {
    private static final List<IOverlayProvider> providers = new ArrayList<>();

    static {
        // Регистрируем провайдеры по умолчанию
        registerProvider(new BlockOverlayProvider());
        registerProvider(new EntityOverlayProvider());
        registerProvider(new TileEntityOverlayProvider());
    }

    public static void registerProvider(IOverlayProvider provider) {
        providers.add(provider);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        OverlayContext context = new OverlayContext(mc, event.getGuiGraphics());

        // Собираем информацию от всех провайдеров
        List<OverlaySection> sections = new ArrayList<>();
        for (IOverlayProvider provider : providers) {
            if (provider.shouldRender(context)) {
                sections.addAll(provider.getSections(context));
            }
        }

        if (!sections.isEmpty()) {
            renderOverlay(context, sections);
        }
    }

    private static void renderOverlay(OverlayContext context, List<OverlaySection> sections) {
        // Расчет позиции и рендеринг
        OverlayRenderer.render(context, sections);
    }
}