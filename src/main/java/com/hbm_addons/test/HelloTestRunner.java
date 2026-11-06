package com.hbm_addons.test;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Минимальный сценарий: проверяет, что инфраструктура тестов работает.
 * Всегда PASS. Живёт 20 тиков (1 секунда).
 */
public class HelloTestRunner extends TestRunner {

    public HelloTestRunner(ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (tickCount == 1) {
            progress("started");
            info("scenario", scenarioName);
            info("server_tick", server.getTickCount());
        }

        if (tickCount >= 20) {
            progress("finished");
            check("no_op", true);
            metric("ticks_elapsed", tickCount);
            finish(true);
            return true;
        }

        return false;
    }
}
