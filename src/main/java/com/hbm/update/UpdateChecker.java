package com.hbm.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@OnlyIn(Dist.CLIENT)
public class UpdateChecker {
    private static final Logger LOGGER = LogManager.getLogger("HBM-Update");
    private static boolean hasChecked = false;

    public static void checkForUpdates() {
        if (hasChecked) return;
        hasChecked = true;

        new Thread(() -> {
            try {
                String currentVersion = ModList.get().getModContainerById(RefStrings.MODID)
                        .orElseThrow().getModInfo().getVersion().toString();

                URL url = new URL("https://api.github.com/repos/Jackasm/HBM-1.20.1/releases/latest");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() != 200) {
                    LOGGER.warn("Failed to check for updates: HTTP {}", conn.getResponseCode());
                    return;
                }

                JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                String latestTag = json.get("tag_name").getAsString().replace("v", "");

                if (!latestTag.equals(currentVersion)) {
                    // Версии различаются – показываем сообщение
                    showUpdateMessage(currentVersion, latestTag);
                }
            } catch (Exception e) {
                LOGGER.error("Error while checking for updates", e);
            }
        }, "HBM-Update-Thread").start();
    }

    private static void showUpdateMessage(String current, String latest) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(
                    Component.translatable("chat.hbm.update", current, latest),
                    false
            );
        }
    }
}