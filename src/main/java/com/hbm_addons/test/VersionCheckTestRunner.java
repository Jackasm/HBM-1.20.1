package com.hbm_addons.test;

import com.hbm.util.VersionUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Проверка сравнения версий для клиентского уведомления об обновлении.
 *
 * <p>Симптом: на dev-сборке 0.1.1-dev.a88e7fc приходит «доступна новая версия
 * 0.1.0». Причина — сравнение строкой на неравенство; числовая база 0.1.1
 * старше 0.1.0 и уведомления быть не должно.</p>
 */
public class VersionCheckTestRunner extends TestRunner {

    public VersionCheckTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        boolean ok = true;

        ok &= test("0.1.1", "0.1.0", true);
        ok &= test("0.1.0", "0.1.1-dev.a88e7fc", false);
        ok &= test("0.1.0", "0.1.0", false);
        ok &= test("0.1.1", "0.1.1-dev.a88e7fc", false);
        ok &= test("0.1.10", "0.1.9", true);
        ok &= test("0.2.0", "0.1.99", true);
        ok &= test("v0.1.1", "0.1.0", true);
        ok &= test("abc", "0.1.0", false);
        ok &= test("0.1.0", "abc", false);

        ok &= expect("strip_v", VersionUtil.stripTagPrefix("v0.1.0").equals("0.1.0"));
        ok &= expect("strip_keeps_inner_v", VersionUtil.stripTagPrefix("dev").equals("dev"));
        ok &= expect("strip_plain", VersionUtil.stripTagPrefix("0.1.0").equals("0.1.0"));

        metric("cases", 12);
        finish(ok);
        return true;
    }

    private boolean test(String latest, String current, boolean expected) {
        boolean actual = VersionUtil.isNewer(latest, current);
        return expect("newer_" + latest + "_over_" + current, actual == expected);
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}
