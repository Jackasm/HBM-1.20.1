package com.hbm_addons.test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Инварианты реестра жидкостей и танка, которые не требуют мира:
 * количество и уникальность имён, наличие текстур и ключей локализации,
 * NBT-круг танка с клэмпом, инертность Forge-капабилити.
 */
public class FluidsRegistryTestRunner extends TestRunner {

    private boolean ok = true;

    public FluidsRegistryTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    private void expect(String name, boolean condition) {
        check(name, condition);
        if (!condition) ok = false;
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (tickCount != 1) return false;

        FluidTypeHBM[] all = Fluids.getAllFluids();
        metric("types", all.length);
        expect("count_157", all.length == 157);

        Set<String> names = new HashSet<>();
        boolean unique = true;
        boolean textures = true;
        for (FluidTypeHBM type : all) {
            String name = type.getName().toLowerCase(Locale.ROOT);
            if (!names.add(name)) {
                unique = false;
                info("duplicate_name", name);
            }
            String path = "/assets/hbm/" + type.getTexture().getPath();
            try (InputStream in = FluidsRegistryTestRunner.class.getResourceAsStream(path)) {
                if (in == null) {
                    textures = false;
                    info("missing_texture", name);
                }
            } catch (Exception e) {
                textures = false;
                info("texture_error", name + ":" + e.getClass().getSimpleName());
            }
        }
        expect("unique_names", unique);
        expect("textures_present", textures);

        JsonObject en = loadLang("en_us");
        JsonObject ru = loadLang("ru_ru");
        boolean enOk = en != null;
        boolean ruOk = ru != null;
        for (String name : names) {
            if (en == null || !en.has("hbmfluid." + name)) {
                enOk = false;
                info("missing_lang_en", name);
            }
            if (ru == null || !ru.has("hbmfluid." + name)) {
                ruOk = false;
                info("missing_lang_ru", name);
            }
        }
        expect("lang_en", enOk);
        expect("lang_ru", ruOk);

        FluidTankHBM tank = new FluidTankHBM(Fluids.WATER.get(), 100);
        tank.setFill(150);
        CompoundTag tag = new CompoundTag();
        tank.writeToNBT(tag, "t");

        FluidTankHBM loaded = new FluidTankHBM(Fluids.NONE.get(), 1);
        loaded.readFromNBT(tag, "t");
        expect("nbt_fill_clamped", loaded.getFill() == 100);
        expect("nbt_capacity", loaded.getMaxFill() == 100);
        expect("nbt_type", loaded.getTankType() == Fluids.WATER.get());

        FluidStack water = new FluidStack(Fluids.WATER.get().getForgeFluid(), 10);
        FluidStack oil = new FluidStack(Fluids.OIL.get().getForgeFluid(), 10);
        FluidTankHBM handlerTank = new FluidTankHBM(Fluids.WATER.get(), 100);
        expect("handler_wrong_type_rejected",
                handlerTank.fill(oil, IFluidHandler.FluidAction.EXECUTE) == 0);
        expect("handler_fill_accepts",
                handlerTank.fill(water, IFluidHandler.FluidAction.EXECUTE) == 10);
        expect("handler_fill_amount", handlerTank.getFill() == 10);
        expect("handler_stack_type", handlerTank.getFluid().getFluid() == water.getFluid());
        FluidStack drained = handlerTank.drain(5, IFluidHandler.FluidAction.EXECUTE);
        expect("handler_drain_type", drained.getFluid() == water.getFluid());
        expect("handler_drain_amount", drained.getAmount() == 5 && handlerTank.getFill() == 5);
        expect("handler_drain_rest", !handlerTank.drain(5, IFluidHandler.FluidAction.EXECUTE).isEmpty());

        // #164: пустое имя типа — штатное состояние NBT без ключа, а не ошибка.
        // Опечатки в непустых именах по-прежнему диагностируются WARN.
        org.apache.logging.log4j.core.Logger fluidsLogger =
                (org.apache.logging.log4j.core.Logger) LogManager.getLogger("hbm/Fluids");
        WarnCapture capture = new WarnCapture();
        capture.start();
        fluidsLogger.addAppender(capture);
        try {
            expect("empty_name_is_none", Fluids.fromName("") == Fluids.NONE.get());
            expect("null_name_is_none", Fluids.fromName(null) == Fluids.NONE.get());
            expect("empty_name_quiet", capture.count() == 0);

            CompoundTag bare = new CompoundTag();
            FluidTankHBM bareTank = new FluidTankHBM(Fluids.NONE.get(), 1);
            bareTank.readFromNBT(bare, "t");
            expect("tank_nbt_no_type_none",
                    bareTank.getTankType() == Fluids.NONE.get() && bareTank.getFill() == 0);
            expect("tank_nbt_no_type_quiet", capture.count() == 0);

            expect("unknown_name_is_none", Fluids.fromName("НЕТ_ТАКОГО") == Fluids.NONE.get());
            expect("unknown_name_warns", capture.count() == 1);
            metric("warn_messages", capture.messages);
        } finally {
            fluidsLogger.removeAppender(capture);
            capture.stop();
        }

        finish(ok);
        return true;
    }

    private JsonObject loadLang(String locale) {
        String path = "/assets/hbm/lang/" + locale + ".json";
        try (InputStream in = FluidsRegistryTestRunner.class.getResourceAsStream(path)) {
            if (in == null) {
                info("missing_lang_file", locale);
                return null;
            }
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            info("lang_error", locale + ":" + e.getClass().getSimpleName());
            return null;
        }
    }

    /** Копит события WARN и выше от логгера `hbm/Fluids`: проверка «пустое имя не шумит» (#164). */
    private static final class WarnCapture extends AbstractAppender {
        private final List<String> messages = new ArrayList<>();

        WarnCapture() {
            super("hbm-fluids-warn-capture", null, null, true, null);
        }

        @Override
        public void append(LogEvent event) {
            if (event.getLevel().intLevel() <= Level.WARN.intLevel()) {
                messages.add(event.getMessage().getFormattedMessage());
            }
        }

        int count() {
            return messages.size();
        }
    }
}
