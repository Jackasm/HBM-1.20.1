package com.hbm.render.util;

import com.hbm.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class RenderInfoSystem {

    private static int nextID = 1000;
    private static final Map<Integer, InfoEntry> inbox = new HashMap<>();
    private static final Map<Integer, InfoEntry> messages = new HashMap<>();

    private static RenderInfoSystem instance;

    public static RenderInfoSystem getInstance() {
        if (instance == null) {
            instance = new RenderInfoSystem();
            MinecraftForge.EVENT_BUS.register(instance);
        }
        return instance;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            messages.putAll(inbox);
            inbox.clear();

            List<Integer> keys = new ArrayList<>(messages.keySet());

            for (int i = 0; i < keys.size(); i++) {
                Integer key = keys.get(i);
                InfoEntry entry = messages.get(key);

                if (entry.start + entry.millis < System.currentTimeMillis()) {
                    messages.remove(key);
                    keys = new ArrayList<>(messages.keySet());
                    i--;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Pre event) {
        if (messages.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        if (mc.player == null) return;

        List<InfoEntry> entries = new ArrayList<>(messages.values());
        Collections.sort(entries);

        int longest = 0;
        for (InfoEntry entry : entries) {
            int length = mc.font.width(entry.text);
            if (length > longest) {
                longest = length;
            }
        }

        int mode = ClientConfig.INFO_POSITION;
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        int pX, pZ;

        pZ = switch (mode) {
            case 0 -> {
                pX = 15;
                yield 15;
            }
            case 1 -> {
                pX = screenWidth - longest - 15;
                yield 15;
            }
            case 2 -> {
                pX = screenWidth / 2 + 7;
                yield screenHeight / 2 + 7;
            }
            default -> {
                pX = screenWidth / 2 - longest - 6;
                yield screenHeight / 2 + 7;
            }
        };

        pX += ClientConfig.INFO_OFFSET_HORIZONTAL;
        pZ += ClientConfig.INFO_OFFSET_VERTICAL;

        int side = pX + 5 + longest;
        int height = messages.size() * 10 + pZ + 2;

        // Рисуем фон
        guiGraphics.fill(pX - 5, pZ - 5, side, height, 0x807F7F7F);

        int off = 0;
        long now = System.currentTimeMillis();

        for (InfoEntry entry : entries) {
            int elapsed = (int) (now - entry.start);
            int alpha = Math.max(Math.min(510 * (entry.millis - elapsed) / entry.millis, 255), 5);
            int color = entry.color + (alpha << 24);
            guiGraphics.drawString(mc.font, entry.text, pX, pZ + off, color, false);
            off += 10;
        }
    }

    public static void push(InfoEntry entry) {
        push(entry, nextID++);
    }

    public static void push(InfoEntry entry, int id) {
        inbox.put(id, entry);
    }

    public static class InfoEntry implements Comparable<InfoEntry> {
        String text;
        int color = 0xFFFFFF;
        long start;
        int millis;

        public InfoEntry(String text) {
            this(text, 3000);
        }

        public InfoEntry(String text, int millis) {
            this.text = text;
            this.millis = millis;
            this.start = System.currentTimeMillis();
        }

        public InfoEntry withColor(int color) {
            this.color = color;
            return this;
        }

        @Override
        public int compareTo(InfoEntry other) {
            return Integer.compare(this.millis, other.millis);
        }
    }
}