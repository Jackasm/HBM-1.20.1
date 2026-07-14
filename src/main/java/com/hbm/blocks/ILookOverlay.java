package com.hbm.blocks;

import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

import java.util.ArrayList;
import java.util.List;

public interface ILookOverlay {

    /**
     * Новый метод, совместимый с системой оверлеев
     */
    @OnlyIn(Dist.CLIENT)
    default boolean shouldRender(OverlayContext context) {
        if (context.mc().level == null || context.mc().hitResult == null ||
                context.mc().hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return false;
        }
        BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) context.mc().hitResult).getBlockPos();
        return shouldRender(context.mc().level, pos);
    }

    /**
     * Проверка, нужно ли рендерить оверлей для этого блока
     */
    @OnlyIn(Dist.CLIENT)
    default boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }

    /**
     * Получение секций оверлея
     */
    @OnlyIn(Dist.CLIENT)
    default List<OverlaySection> getSections(OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();
        if (context.mc().level == null || context.mc().hitResult == null ||
                context.mc().hitResult.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return sections;
        }
        BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) context.mc().hitResult).getBlockPos();
        return getSections(context.mc().level, pos, context);
    }

    /**
     * Получение секций оверлея (для обратной совместимости)
     */
    @OnlyIn(Dist.CLIENT)
    default List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        return new ArrayList<>();
    }

    /**
     * Старый метод для обратной совместимости
     * Помечен как устаревший
     */
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    default void printHook(RenderGuiOverlayEvent.Pre event, Level level, BlockPos blockPos){}

    /**
     * Статический метод для создания оверлея из списка компонентов
     */
    @OnlyIn(Dist.CLIENT)
    static void printGeneric(OverlaySection section, String title, int titleCol, List<? extends Component> text) {
        section.addLine(Component.literal(title).withStyle(style -> style.withColor(titleCol)));
        for (Component line : text) {
            section.addLine(line);
        }
    }

    @Deprecated
    @OnlyIn(Dist.CLIENT)
    static void printGeneric(RenderGuiOverlayEvent.Pre event, String title, int titleCol, int bgCol, List<? extends Component> text) {

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        int pX = width / 2 + 8;
        int pY = height / 2;

        // Тень для заголовка
        graphics.drawString(mc.font, Component.literal(title), pX + 1, pY - 9, bgCol, false);
        graphics.drawString(mc.font, Component.literal(title), pX, pY - 10, titleCol, false);

        try {
            for (Component line : text) {
                String lineStr = line.getString();
                int color = 0xFFFFFF;
                String displayText = lineStr;

                if (lineStr.startsWith("&[")) {
                    int end = lineStr.lastIndexOf("&]");
                    if (end > 2) {
                        color = Integer.parseInt(lineStr.substring(2, end));
                        displayText = lineStr.substring(end + 2);
                    }
                }

                graphics.drawString(mc.font, Component.literal(displayText), pX, pY, color, true);
                pY += 10;
            }
        } catch (Exception ex) {
            graphics.drawString(mc.font, Component.literal(ex.getClass().getSimpleName()), pX, pY + 10, 0xff0000, true);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}