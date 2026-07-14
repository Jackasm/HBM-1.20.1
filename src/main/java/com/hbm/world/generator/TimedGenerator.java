package com.hbm.world.generator;

import com.hbm.main.MainRegistry;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hbm.util.RefStrings.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class TimedGenerator {

    private static final Map<Level, List<ITimedJob>> operations = new HashMap<>();
    private static boolean isProcessing = false;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level == null) return;
        if (event.level.isClientSide) return;

        Level level = event.level;
        List<ITimedJob> jobs = operations.get(level);
        if (jobs == null || jobs.isEmpty()) return;

        // Выполняем не более 5 задач за тик, чтобы не нагружать
        int processed = 0;
        int maxPerTick = 5;

        while (!jobs.isEmpty() && processed < maxPerTick) {
            ITimedJob job = jobs.remove(0);
            if (job != null) {
                job.work();
                processed++;
            }
        }

        // Если все задачи выполнены, удаляем запись
        if (jobs.isEmpty()) {
            operations.remove(level);
        }
    }

    public static void automaton(Level level, int amount) {
        // Можно использовать для отладки или как заглушку
        List<ITimedJob> jobs = operations.get(level);
        if (jobs != null) {
            MainRegistry.logger.debug("TimedGenerator: {} pending jobs for dimension {}",
                    jobs.size(), level.dimension().location());
        }
    }

    public static void addOp(Level level, ITimedJob job) {
        if (level == null || job == null) return;

        operations.computeIfAbsent(level, k -> new ArrayList<>()).add(job);
    }

    public interface ITimedJob {
        void work();
    }

    // Для очистки при выгрузке измерения
    public static void clear(Level level) {
        operations.remove(level);
    }
}