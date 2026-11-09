package com.hbm_addons.test;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Двери в NBT-структурах: нижняя и верхняя половины должны нести одинаковые
 * {@code facing}, {@code open} и {@code hinge}.
 *
 * <p>В 1.7.10 hinge хранился только в верхней половине, конвертер переносил его
 * только туда, а в 1.20.1 свойство есть у обеих половин и по умолчанию равно
 * {@code left}. Правая дверь с потерянным hinge рисует нижнюю створку зеркально —
 * пока {@code BlockModDoor.updateShape} не подтянет её из верхней половины при
 * первом обновлении блока (например, после использования двери).</p>
 *
 * <p>Стенд читает сами шаблоны из ресурсов, без генерации мира: быстро и не
 * зависит от сида. Отсутствующий hinge считается ванильным значением по
 * умолчанию — {@code left}; ошибкой считается только реальное расхождение.</p>
 */
public class StructureDoorsTestRunner extends TestRunner {

    public StructureDoorsTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        scan(server);
        return true;
    }

    private void scan(MinecraftServer server) {
        Map<ResourceLocation, Resource> templates = server.getResourceManager()
                .listResources("structures", path -> path.getPath().endsWith(".nbt"));

        int templatesScanned = 0;
        int doorPairs = 0;
        int implicitHinge = 0;
        int unpaired = 0;
        int mismatched = 0;
        int readErrors = 0;
        Map<String, Integer> problems = new TreeMap<>();

        for (Map.Entry<ResourceLocation, Resource> entry : templates.entrySet()) {
            ResourceLocation id = entry.getKey();
            if (!id.getNamespace().equals("hbm")) continue;

            CompoundTag root = read(entry.getValue());
            if (root == null) {
                readErrors++;
                problems.merge(id + " read", 1, Integer::sum);
                continue;
            }
            templatesScanned++;

            ListTag paletteList = root.getList("palettes", Tag.TAG_LIST);
            if (paletteList.isEmpty()) {
                // Старый формат: единственная палитра лежит в ключе "palette".
                paletteList = root.getList("palette", Tag.TAG_COMPOUND);
            }
            List<ListTag> palettes = new ArrayList<>();
            if (root.contains("palettes", Tag.TAG_LIST)) {
                for (int p = 0; p < paletteList.size(); p++) {
                    palettes.add(paletteList.getList(p));
                }
            } else if (!paletteList.isEmpty()) {
                palettes.add(paletteList);
            }
            if (palettes.isEmpty()) {
                problems.merge(id + " no_palette", 1, Integer::sum);
                continue;
            }

            ListTag blocks = root.getList("blocks", Tag.TAG_COMPOUND);
            Map<Long, CompoundTag> blocksByPos = new HashMap<>(blocks.size() * 2);
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                ListTag pos = block.getList("pos", Tag.TAG_INT);
                if (pos.size() != 3) continue;
                blocksByPos.put(BlockPos.asLong(pos.getInt(0), pos.getInt(1), pos.getInt(2)), block);
            }

            for (int paletteIndex = 0; paletteIndex < palettes.size(); paletteIndex++) {
                ListTag palette = palettes.get(paletteIndex);
                if (palette.isEmpty()) continue;

                for (int i = 0; i < blocks.size(); i++) {
                    CompoundTag block = blocks.getCompound(i);
                    int stateIndex = block.getInt("state");
                    if (stateIndex < 0 || stateIndex >= palette.size()) continue;
                    CompoundTag state = palette.getCompound(stateIndex);
                    if (!state.getString("Name").contains("door")) continue;

                    CompoundTag props = state.getCompound("Properties");
                    if (!"lower".equals(props.getString("half"))) continue;
                    if (paletteIndex == 0) doorPairs++;

                    ListTag pos = block.getList("pos", Tag.TAG_INT);
                    int x = pos.getInt(0), y = pos.getInt(1), z = pos.getInt(2);
                    CompoundTag above = blocksByPos.get(BlockPos.asLong(x, y + 1, z));
                    if (above == null) {
                        unpaired++;
                        problems.merge(id + " " + x + "," + y + "," + z + " no_upper", 1, Integer::sum);
                        continue;
                    }

                    CompoundTag aboveState = palette.getCompound(above.getInt("state"));
                    CompoundTag aboveProps = aboveState.getCompound("Properties");
                    String lowerHinge = hinge(props);
                    String upperHinge = hinge(aboveProps);
                    if (!props.contains("hinge", Tag.TAG_STRING)) implicitHinge++;

                    List<String> diffs = new ArrayList<>();
                    if (!aboveState.getString("Name").equals(state.getString("Name"))) {
                        diffs.add("name " + aboveState.getString("Name"));
                    }
                    if (!"upper".equals(aboveProps.getString("half"))) {
                        diffs.add("half " + aboveProps.getString("half"));
                    }
                    if (!props.getString("facing").equals(aboveProps.getString("facing"))) {
                        diffs.add("facing " + props.getString("facing") + "/" + aboveProps.getString("facing"));
                    }
                    if (!props.getString("open").equals(aboveProps.getString("open"))) {
                        diffs.add("open " + props.getString("open") + "/" + aboveProps.getString("open"));
                    }
                    if (!lowerHinge.equals(upperHinge)) {
                        diffs.add("hinge " + lowerHinge + "/" + upperHinge);
                    }

                    if (!diffs.isEmpty()) {
                        mismatched++;
                        problems.merge(id + " " + x + "," + y + "," + z + " " + String.join(", ", diffs),
                                1, Integer::sum);
                    }
                }
            }
        }

        info("templates", templatesScanned);
        info("read_errors", readErrors);
        info("door_pairs", doorPairs);
        info("implicit_lower_hinge", implicitHinge);
        info("unpaired", unpaired);
        info("mismatched", mismatched);
        info("problems", problems);

        boolean ok = true;
        ok &= expect("templates_scanned", templatesScanned > 0);
        ok &= expect("templates_readable", readErrors == 0);
        ok &= expect("doors_found", doorPairs > 0);
        ok &= expect("door_halves_paired", unpaired == 0);
        ok &= expect("door_halves_synced", mismatched == 0);

        metric("templates", templatesScanned);
        metric("door_pairs", doorPairs);
        metric("implicit_hinge", implicitHinge);
        metric("unpaired", unpaired);
        metric("mismatched", mismatched);
        finish(ok);
    }

    /** Hinge половины; отсутствие свойства — ванильное {@code left}. */
    private static String hinge(CompoundTag props) {
        return props.contains("hinge", Tag.TAG_STRING) ? props.getString("hinge") : "left";
    }

    private static CompoundTag read(Resource resource) {
        try (InputStream in = resource.open()) {
            return NbtIo.readCompressed(in);
        } catch (Exception e) {
            System.out.println("HBM_TEST_INFO: door_read_error=" + e);
            return null;
        }
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}
