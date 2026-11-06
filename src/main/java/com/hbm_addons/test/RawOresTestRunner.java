package com.hbm_addons.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.items.ModItems;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.inventory.recipes.ShredderRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Raw-руды: предмет, лут и плавка (#213).
 *
 * <p>Проверяет, что у четырёх «самодропающих» руд появился raw-предмет, что лут
 * отдаёт его без шёлкового касания и блок с шёлковым, и что плавка raw даёт тот же
 * результат, что и плавка блока. Данные читаются из ресурсов сервера, поэтому
 * стенд ловит и правку кода, и регресс датагена.</p>
 */
public class RawOresTestRunner extends TestRunner {

    private record OreCase(String material, Supplier<Item> raw, String smeltResult, String... blocks) {}

    private static final List<OreCase> CASES = List.of(
            new OreCase("australium", ModItems.RAW_AUSTRALIUM, "nugget_australium", "ore_australium"),
            new OreCase("schrabidium", ModItems.RAW_SCHRABIDIUM, "ingot_schrabidium",
                    "ore_schrabidium", "ore_gneiss_schrabidium", "ore_sellafield_schrabidium",
                    "ore_nether_schrabidium"),
            new OreCase("tikite", ModItems.RAW_TIKITE, null, "ore_tikite"),
            new OreCase("osmiridium", ModItems.RAW_OSMIRIDIUM, null, "ore_tektite_osmiridium")
    );

    public RawOresTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount++ > 0) return true;

        int lootTables = 0;
        int smelted = 0;
        int fortune = 0;

        for (OreCase ore : CASES) {
            Item raw = ore.raw().get();
            check("raw_" + ore.material() + "_registered", raw != null);

            for (String block : ore.blocks()) {
                LootShape shape = readLoot(server, new ResourceLocation("hbm", "loot_tables/blocks/" + block + ".json"));
                if (shape == null) {
                    check("loot_" + block + "_present", false);
                    continue;
                }
                lootTables++;
                check("loot_" + block + "_raw", shape.plain.contains("hbm:raw_" + ore.material()));
                check("loot_" + block + "_silk_block", shape.silk.contains("hbm:" + block));
                check("loot_" + block + "_fortune", shape.bonus);
                if (shape.bonus) fortune++;
            }

            if (ore.smeltResult() != null) {
                ResourceLocation result = smelt(server, raw);
                boolean ok = result != null && result.equals(new ResourceLocation("hbm", ore.smeltResult()));
                check("smelt_raw_" + ore.material(), ok);
                if (ok) smelted++;
                metric("smelt_" + ore.material(), result == null ? "none" : result.toString());
            }
        }

        // Тикит: центрифуга принимает и блок, и raw, выходы совпадают.
        ItemStack[] fromBlock = CentrifugeRecipes.getOutput(new ItemStack(ModBlocks.ORE_TIKITE.get()));
        ItemStack[] fromRaw = CentrifugeRecipes.getOutput(new ItemStack(ModItems.RAW_TIKITE.get()));
        check("centrifuge_tikite_block", fromBlock != null && fromBlock.length > 0);
        check("centrifuge_tikite_raw", fromRaw != null && fromRaw.length > 0);
        check("centrifuge_tikite_same", sameStacks(fromBlock, fromRaw));
        metric("centrifuge_tikite", describe(fromRaw));

        // Осмиридий: дробление в powder_tektite, как в эталоне — и для руды, и для raw.
        ResourceLocation orePowder = shred(ModBlocks.ORE_TEKTITE_OSMIRIDIUM.get().asItem());
        ResourceLocation rawPowder = shred(ModItems.RAW_OSMIRIDIUM.get());
        check("shredder_osmiridium_ore", isTektite(orePowder));
        check("shredder_osmiridium_raw", isTektite(rawPowder));
        metric("shredder_osmiridium_raw", rawPowder == null ? "none" : rawPowder.toString());

        metric("loot_tables", lootTables);
        metric("smelt_recipes", smelted);
        metric("fortune_tables", fortune);

        // Удача не должна расползтись на руды без raw-дропа.
        LootShape cinnabar = readLoot(server, new ResourceLocation("hbm", "loot_tables/blocks/ore_cinnabar.json"));
        check("fortune_not_on_plain_ores", cinnabar != null && !cinnabar.bonus);

        // Оба пути датагена: руда из старых девяти (silk + raw) и метеоритная медь (без silk).
        LootShape uranium = readLoot(server, new ResourceLocation("hbm", "loot_tables/blocks/ore_uranium.json"));
        check("fortune_ore_uranium", uranium != null && uranium.bonus && uranium.plain.contains("hbm:raw_uranium"));
        LootShape meteor = readLoot(server, new ResourceLocation("hbm", "loot_tables/blocks/ore_meteor_copper.json"));
        check("fortune_ore_meteor_copper",
                meteor != null && meteor.bonus && meteor.plain.contains("minecraft:raw_copper"));

        // Ванильная медь: переопределение из мода, один предмет и удача.
        for (String block : new String[]{"copper_ore", "deepslate_copper_ore"}) {
            LootShape copper = readLoot(server, new ResourceLocation("minecraft", "loot_tables/blocks/" + block + ".json"));
            String tag = block;
            check("vanilla_" + tag + "_present", copper != null);
            if (copper == null) continue;
            check("vanilla_" + tag + "_raw", copper.plain.contains("minecraft:raw_copper"));
            check("vanilla_" + tag + "_silk_block", copper.silk.contains("minecraft:" + block));
            check("vanilla_" + tag + "_fortune", copper.bonus);
            check("vanilla_" + tag + "_single", copper.plainMin == 1 && copper.plainMax == 1);
            metric("vanilla_" + tag, copper.plainMin + ".." + copper.plainMax + (copper.bonus ? "+fortune" : ""));
        }

        boolean pass = checks.stream().allMatch(c -> c.pass);
        finish(pass);
        return true;
    }

    private boolean isTektite(ResourceLocation id) {
        return id != null && id.equals(new ResourceLocation("hbm", "powder_tektite"));
    }

    private ResourceLocation shred(Item item) {
        ItemStack result = ShredderRecipes.getShredderResult(new ItemStack(item));
        return result == null || result.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(result.getItem());
    }

    private static boolean sameStacks(ItemStack[] a, ItemStack[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (!ItemStack.isSameItemSameTags(a[i], b[i]) || a[i].getCount() != b[i].getCount()) return false;
        }
        return true;
    }

    private static String describe(ItemStack[] stacks) {
        if (stacks == null) return "none";
        StringBuilder sb = new StringBuilder();
        for (ItemStack stack : stacks) {
            if (sb.length() > 0) sb.append(',');
            sb.append(ForgeRegistries.ITEMS.getKey(stack.getItem())).append('x').append(stack.getCount());
        }
        return sb.toString();
    }

    private record LootShape(List<String> plain, List<String> silk, boolean bonus, int plainMin, int plainMax) {}

    /** Разбирает лут-таблицу: что падает с шёлковым касанием и без, есть ли удача и сколько предметов. */
    private LootShape readLoot(MinecraftServer server, ResourceLocation id) {
        Optional<Resource> resource = server.getResourceManager().getResource(id);
        if (resource.isEmpty()) return null;
        try (var reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            List<String> plain = new ArrayList<>();
            List<String> silk = new ArrayList<>();
            boolean bonus = false;
            int min = 1;
            int max = 1;
            for (JsonElement poolElement : root.getAsJsonArray("pools")) {
                JsonObject pool = poolElement.getAsJsonObject();
                String json = pool.toString();
                boolean isPlain = json.contains("minecraft:inverted") || !json.contains("silk_touch");
                collectItems(pool, isPlain ? plain : silk);
                if (isPlain) {
                    if (json.contains("apply_bonus") && json.contains("ore_drops")) bonus = true;
                    int[] count = readCount(pool);
                    if (count != null) {
                        min = count[0];
                        max = count[1];
                    }
                }
            }
            return new LootShape(plain, silk, bonus, min, max);
        } catch (Exception e) {
            info("loot_error_" + id, e.toString());
            return null;
        }
    }

    /** Достаёт min/max из функции set_count, если она есть в пуле. */
    private int[] readCount(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("function") && "minecraft:set_count".equals(object.get("function").getAsString())
                    && object.has("count") && object.get("count").isJsonObject()) {
                JsonObject count = object.getAsJsonObject("count");
                return new int[]{count.get("min").getAsInt(), count.get("max").getAsInt()};
            }
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                int[] found = readCount(entry.getValue());
                if (found != null) return found;
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                int[] found = readCount(child);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void collectItems(JsonElement element, List<String> out) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("type") && object.get("type").getAsString().endsWith(":item")
                    && object.has("name")) {
                out.add(object.get("name").getAsString());
            }
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if ("name".equals(entry.getKey())) continue;
                collectItems(entry.getValue(), out);
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) collectItems(child, out);
        }
    }

    private ResourceLocation smelt(MinecraftServer server, Item raw) {
        Optional<SmeltingRecipe> recipe = server.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack(raw)), level);
        return recipe
                .map(r -> r.getResultItem(level.registryAccess()))
                .filter(stack -> !stack.isEmpty())
                .map(stack -> ForgeRegistries.ITEMS.getKey(stack.getItem()))
                .orElse(null);
    }
}
