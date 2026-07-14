package com.hbm.config;

import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.blocks.ModBlocks;

import com.hbm.main.MainRegistry;
import com.hbm.util.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class FalloutConfigJSON {

    public static final List<FalloutEntry> entries = new ArrayList<>();
    public static final Random rand = new Random();
    public static final Gson gson = new Gson();

    public static void initialize() {
        File folder = MainRegistry.configHbmDir;

        File config = new File(folder.getAbsolutePath() + File.separatorChar + "hbmFallout.json");
        File template = new File(folder.getAbsolutePath() + File.separatorChar + "_hbmFallout.json");

        initDefault();

        if (!config.exists()) {
            writeDefault(template);
        } else {
            List<FalloutEntry> conf = readConfig(config);

            if (conf != null) {
                entries.clear();
                entries.addAll(conf);
            }
        }
    }

    private static void initDefault() {
        double woodEffectRange = 65D;

        /* petrify all wooden things possible */
        entries.add(new FalloutEntry().mB(Blocks.OAK_LOG).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.BIRCH_LOG).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.SPRUCE_LOG).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.JUNGLE_LOG).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.ACACIA_LOG).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.DARK_OAK_LOG).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.MUSHROOM_STEM).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.BROWN_MUSHROOM_BLOCK).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LOG.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.RED_MUSHROOM_BLOCK).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.BROWN_MUSHROOM_BLOCK).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.SNOW).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.OAK_PLANKS).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_PLANKS.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.BIRCH_PLANKS).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_PLANKS.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.SPRUCE_PLANKS).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_PLANKS.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.JUNGLE_PLANKS).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_PLANKS.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.ACACIA_PLANKS).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_PLANKS.get(), 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.DARK_OAK_PLANKS).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_PLANKS.get(), 1)).max(woodEffectRange));
        /* if it can't be petrified, destroy it */
        entries.add(new FalloutEntry().mTag(BlockTags.LOGS).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        /* destroy all leaves within the radios, kill all leaves outside of it */
        entries.add(new FalloutEntry().mTag(BlockTags.LEAVES).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mTag(BlockTags.FLOWERS).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mTag(BlockTags.CROPS).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mTag(BlockTags.SAPLINGS).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(ModBlocks.WASTE_LEAVES.get()).prim(new AbstractMap.SimpleEntry<>(Blocks.AIR, 1)).max(woodEffectRange));
        entries.add(new FalloutEntry().mB(Blocks.OAK_LEAVES).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LEAVES.get(), 1)).min(woodEffectRange - 5D));
        entries.add(new FalloutEntry().mB(Blocks.BIRCH_LEAVES).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LEAVES.get(), 1)).min(woodEffectRange - 5D));
        entries.add(new FalloutEntry().mB(Blocks.SPRUCE_LEAVES).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LEAVES.get(), 1)).min(woodEffectRange - 5D));
        entries.add(new FalloutEntry().mB(Blocks.JUNGLE_LEAVES).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LEAVES.get(), 1)).min(woodEffectRange - 5D));
        entries.add(new FalloutEntry().mB(Blocks.ACACIA_LEAVES).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LEAVES.get(), 1)).min(woodEffectRange - 5D));
        entries.add(new FalloutEntry().mB(Blocks.DARK_OAK_LEAVES).prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_LEAVES.get(), 1)).min(woodEffectRange - 5D));

        entries.add(new FalloutEntry().mB(ModBlocks.GLYPHID_BASE.get()).prim(new AbstractMap.SimpleEntry<>(ModBlocks.GLYPHID_BASE.get(), 1)).max(100));
        entries.add(new FalloutEntry().mB(ModBlocks.GLYPHID_SPAWNER.get()).prim(new AbstractMap.SimpleEntry<>(ModBlocks.GLYPHID_SPAWNER.get(), 1)).max(100));

        entries.add(new FalloutEntry().mB(Blocks.MOSSY_COBBLESTONE).prim(new AbstractMap.SimpleEntry<>(Blocks.COAL_ORE, 1)));
        entries.add(new FalloutEntry().mB(ModBlocks.ORE_NETHER_URANIUM.get()).prim(
                new AbstractMap.SimpleEntry<>(ModBlocks.ORE_NETHER_SCHRABIDIUM.get(), 1),
                new AbstractMap.SimpleEntry<>(ModBlocks.ORE_NETHER_URANIUM_SCORCHED.get(), 99)
        ));

        Block deepslate = Compat.tryLoadBlock(Compat.MOD_EF, "deepslate");
        Block stone = Compat.tryLoadBlock(Compat.MOD_EF, "stone");

        for (int i = 1; i <= 10; i++) {
            int m = 10 - i;
            entries.add(new FalloutEntry().prim(
                    new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_DIAMOND.get(), 3),
                    new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_EMERALD.get(), 2)
            ).c(0.5).max(i * 5).sol(true).mB(Blocks.COAL_ORE));

            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_DIAMOND.get(), 1)).c(0.2).max(i * 5).sol(true).mB(ModBlocks.ORE_LIGNITE.get()));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_EMERALD.get(), 1)).max(i * 5).sol(true).mB(ModBlocks.ORE_BERYLLIUM.get()));

            if (m > 4) entries.add(new FalloutEntry().prim(
                    new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_SCHRABIDIUM.get(), 1),
                    new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_URANIUM_SCORCHED.get(), 9)
            ).max(i * 5).sol(true).mB(ModBlocks.ORE_URANIUM.get()));

            if (m > 4) entries.add(new FalloutEntry().prim(
                    new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_SCHRABIDIUM.get(), 1),
                    new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_URANIUM_SCORCHED.get(), 9)
            ).max(i * 5).sol(true).mB(ModBlocks.ORE_GNEISS_URANIUM.get()));

            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.ORE_SELLAFIELD_RADGEM.get(), 1)).max(i * 5).sol(true).mB(Blocks.DIAMOND_ORE));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_BEDROCK.get(), 1)).max(i * 5).sol(true).mB(Blocks.BEDROCK));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_BEDROCK.get(), 1)).max(i * 5).sol(true).mB(ModBlocks.ORE_BEDROCK.get()));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_BEDROCK.get(), 1)).max(i * 5).sol(true).mB(ModBlocks.ORE_BEDROCK_OIL.get()));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_BEDROCK.get(), 1)).max(i * 5).sol(true).mB(ModBlocks.SELLAFIELD_BEDROCK.get()));

            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_SLAKED.get(), 1)).max(i * 5).sol(true).mTag(BlockTags.STONE_ORE_REPLACEABLES));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_SLAKED.get(), 1)).max(i * 5).sol(true).mTag(BlockTags.BASE_STONE_OVERWORLD));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_SLAKED.get(), 1)).max(i * 5).sol(true).mTag(BlockTags.SAND));
            entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_SLAKED.get(), 1)).max(i * 5).sol(true).mTag(BlockTags.DIRT));
            if (i <= 9) entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_SLAKED.get(), 1)).max(i * 5).sol(true).mTag(BlockTags.DIRT));

            if (deepslate != null) entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_SLAKED.get(), 1)).max(i * 5).sol(true).mB(deepslate));
            if (stone != null) entries.add(new FalloutEntry().prim(new AbstractMap.SimpleEntry<>(ModBlocks.SELLAFIELD_SLAKED.get(), 1)).max(i * 5).sol(true).mB(stone));
        }

        entries.add(new FalloutEntry()
                .mB(Blocks.MYCELIUM)
                .prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_MYCELIUM.get(), 1)));
        entries.add(new FalloutEntry()
                .mB(Blocks.SAND)
                .prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_TRINITITE.get(), 1))
                .c(0.05));
        entries.add(new FalloutEntry()
                .mB(Blocks.RED_SAND)
                .prim(new AbstractMap.SimpleEntry<>(ModBlocks.WASTE_TRINITITE_RED.get(), 1))
                .c(0.05));
        entries.add(new FalloutEntry()
                .mB(Blocks.CLAY)
                .prim(new AbstractMap.SimpleEntry<>(Blocks.TERRACOTTA, 1)));
    }

    private static void writeDefault(File file) {
        try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("entries").beginArray();

            for (FalloutEntry entry : entries) {
                writer.beginObject();
                entry.write(writer);
                writer.endObject();
            }

            writer.endArray();
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<FalloutEntry> readConfig(File config) {
        try (FileReader reader = new FileReader(config)) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            JsonArray recipes = json.getAsJsonArray("entries");
            List<FalloutEntry> conf = new ArrayList<>();

            for (JsonElement recipe : recipes) {
                FalloutEntry entry = FalloutEntry.readEntry(recipe);
                if (entry != null) {
                    conf.add(entry);
                }
            }
            return conf;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static class FalloutEntry {
        private Block matchesBlock = null;
        private int matchesMeta = -1;
        private TagKey<Block> matchesBlockTag = null;
        private boolean matchesOpaque = false;

        private AbstractMap.SimpleEntry<Block, Integer>[] primaryBlocks = null;
        private AbstractMap.SimpleEntry<Block, Integer>[] secondaryBlocks = null;
        private double primaryChance = 1.0D;
        private double minDist = 0.0D;
        private double maxDist = 100.0D;
        private double falloffStart = 0.9D;

        private boolean isSolid = false;

        public FalloutEntry clone() {
            FalloutEntry entry = new FalloutEntry();
            entry.mB(matchesBlock);
            entry.mM(matchesMeta);
            entry.mTag(matchesBlockTag);
            entry.mO(matchesOpaque);
            entry.prim(primaryBlocks);
            entry.sec(secondaryBlocks);
            entry.min(minDist);
            entry.max(maxDist);
            entry.fo(falloffStart);
            entry.sol(isSolid);
            return entry;
        }

        public FalloutEntry mB(Block block) {
            this.matchesBlock = block;
            return this;
        }

        public FalloutEntry mM(int meta) {
            this.matchesMeta = meta;
            return this;
        }

        public FalloutEntry mTag(TagKey<Block> tag) {
            this.matchesBlockTag = tag;
            return this;
        }

        public FalloutEntry mO(boolean opaque) {
            this.matchesOpaque = opaque;
            return this;
        }

        @SafeVarargs
        public final FalloutEntry prim(AbstractMap.SimpleEntry<Block, Integer>... blocks) {
            this.primaryBlocks = blocks;
            return this;
        }

        @SafeVarargs
        public final FalloutEntry sec(AbstractMap.SimpleEntry<Block, Integer>... blocks) {
            this.secondaryBlocks = blocks;
            return this;
        }

        public FalloutEntry c(double chance) {
            this.primaryChance = chance;
            return this;
        }

        public FalloutEntry min(double min) {
            this.minDist = min;
            return this;
        }

        public FalloutEntry max(double max) {
            this.maxDist = max;
            return this;
        }

        public FalloutEntry fo(double falloffStart) {
            this.falloffStart = falloffStart;
            return this;
        }

        public FalloutEntry sol(boolean solid) {
            this.isSolid = solid;
            return this;
        }

        public boolean eval(Level level, int x, int y, int z, BlockState state, double dist) {
            Block block = state.getBlock();

            if (dist > maxDist || dist < minDist) return false;
            if (matchesBlock != null && block != matchesBlock) return false;
            if (matchesBlockTag != null && !state.is(matchesBlockTag)) return false;

            if (matchesOpaque && !state.canOcclude()) return false;
            if (dist > maxDist * falloffStart && Math.abs(level.random.nextGaussian()) < Math.pow((dist - maxDist * falloffStart) / (maxDist - maxDist * falloffStart), 2D) * 3D)
                return false;

            AbstractMap.SimpleEntry<Block, Integer> conversion = chooseRandomOutcome(
                    (primaryChance == 1D || rand.nextDouble() < primaryChance) ? primaryBlocks : secondaryBlocks
            );

            if (conversion != null) {
                Block conversionBlock = conversion.getKey();

                if (conversionBlock == ModBlocks.SELLAFIELD_SLAKED.get() && block == ModBlocks.SELLAFIELD_SLAKED.get())
                    return false;
                if (conversionBlock == ModBlocks.SELLAFIELD_BEDROCK.get() && block == ModBlocks.SELLAFIELD_BEDROCK.get())
                    return false;
                if (block == ModBlocks.SELLAFIELD_BEDROCK.get() && conversionBlock != ModBlocks.SELLAFIELD_BEDROCK.get())
                    return false;
                if (y == 0 && conversionBlock != ModBlocks.SELLAFIELD_BEDROCK.get())
                    return false;

                BlockState newState = conversionBlock.defaultBlockState();
                level.setBlock(new BlockPos(x, y, z), newState, 3);
                return true;
            }

            return false;
        }

        private AbstractMap.SimpleEntry<Block, Integer> chooseRandomOutcome(AbstractMap.SimpleEntry<Block, Integer>[] blocks) {
            if (blocks == null) return null;

            int totalWeight = 0;
            for (AbstractMap.SimpleEntry<Block, Integer> entry : blocks) {
                totalWeight += entry.getValue();
            }

            int r = rand.nextInt(totalWeight);
            int currentWeight = 0;

            for (AbstractMap.SimpleEntry<Block, Integer> entry : blocks) {
                currentWeight += entry.getValue();
                if (r < currentWeight) {
                    return entry;
                }
            }

            return blocks[0];
        }

        public boolean isSolid() {
            return this.isSolid;
        }

        public void write(JsonWriter writer) throws IOException {
            if (matchesBlock != null)
                writer.name("matchesBlock").value(BuiltInRegistries.BLOCK.getKey(matchesBlock).toString());
            if (matchesMeta != -1) writer.name("matchesMeta").value(matchesMeta);
            if (matchesOpaque) writer.name("mustBeOpaque").value(true);

            if (matchesBlockTag != null) {
                String tagName = null;
                for (Map.Entry<String, TagKey<Block>> entry : matTags.entrySet()) {
                    if (entry.getValue().equals(matchesBlockTag)) {
                        tagName = entry.getKey();
                        break;
                    }
                }
                if (tagName != null) {
                    writer.name("matchesMaterial").value(tagName);
                }
            }
            if (isSolid) writer.name("restrictDepth").value(true);

            if (primaryBlocks != null) {
                writer.name("primarySubstitution");
                writeMetaArray(writer, primaryBlocks);
            }
            if (secondaryBlocks != null) {
                writer.name("secondarySubstitutions");
                writeMetaArray(writer, secondaryBlocks);
            }

            if (primaryChance != 1D) writer.name("chance").value(primaryChance);

            if (minDist != 0.0D) writer.name("minimumDistancePercent").value(minDist);
            if (maxDist != 100.0D) writer.name("maximumDistancePercent").value(maxDist);
            if (falloffStart != 0.9D) writer.name("falloffStartFactor").value(falloffStart);
        }

        private static FalloutEntry readEntry(JsonElement recipe) {
            if (!recipe.isJsonObject()) return null;

            JsonObject obj = recipe.getAsJsonObject();
            FalloutEntry entry = new FalloutEntry();

            if (obj.has("matchesBlock")) {
                String blockName = obj.get("matchesBlock").getAsString();
                entry.mB(BuiltInRegistries.BLOCK.get(ResLocation(blockName)));
            }
            if (obj.has("matchesMeta")) entry.mM(obj.get("matchesMeta").getAsInt());
            if (obj.has("mustBeOpaque")) entry.mO(obj.get("mustBeOpaque").getAsBoolean());
            if (obj.has("matchesMaterial")) entry.mTag(matTags.get(obj.get("matchesMaterial").getAsString()));
            if (obj.has("restrictDepth")) entry.sol(obj.get("restrictDepth").getAsBoolean());

            if (obj.has("primarySubstitution")) entry.prim(readMetaArray(obj.get("primarySubstitution")));
            if (obj.has("secondarySubstitutions")) entry.sec(readMetaArray(obj.get("secondarySubstitutions")));

            if (obj.has("chance")) entry.c(obj.get("chance").getAsDouble());

            if (obj.has("minimumDistancePercent")) entry.min(obj.get("minimumDistancePercent").getAsDouble());
            if (obj.has("maximumDistancePercent")) entry.max(obj.get("maximumDistancePercent").getAsDouble());
            if (obj.has("falloffStartFactor")) entry.fo(obj.get("falloffStartFactor").getAsDouble());

            return entry;
        }

        private static void writeMetaArray(JsonWriter writer, AbstractMap.SimpleEntry<Block, Integer>[] array) throws IOException {
            writer.beginArray();
            writer.setIndent("");

            for (AbstractMap.SimpleEntry<Block, Integer> entry : array) {
                writer.beginArray();
                writer.value(BuiltInRegistries.BLOCK.getKey(entry.getKey()).toString());
                writer.value(entry.getValue());
                writer.endArray();
            }

            writer.endArray();
            writer.setIndent("  ");
        }

        private static AbstractMap.SimpleEntry<Block, Integer>[] readMetaArray(JsonElement jsonElement) {
            if (!jsonElement.isJsonArray()) return null;

            JsonArray array = jsonElement.getAsJsonArray();
            @SuppressWarnings("unchecked")
            AbstractMap.SimpleEntry<Block, Integer>[] metaArray = new AbstractMap.SimpleEntry[array.size()];

            for (int i = 0; i < metaArray.length; i++) {
                JsonElement metaBlock = array.get(i);

                if (!metaBlock.isJsonArray()) {
                    throw new IllegalStateException("Could not read meta block " + metaBlock);
                }

                JsonArray mBArray = metaBlock.getAsJsonArray();

                Block block = BuiltInRegistries.BLOCK.get(ResLocation(mBArray.get(0).getAsString()));
                int weight = mBArray.get(1).getAsInt();

                metaArray[i] = new AbstractMap.SimpleEntry<>(block, weight);
            }

            return metaArray;
        }
    }

    public static final HashMap<String, TagKey<Block>> matTags = new HashMap<>();

    static {
        // Теги из Minecraft
        matTags.put("grass", BlockTags.DIRT);
        matTags.put("ground", BlockTags.DIRT);
        matTags.put("wood", BlockTags.LOGS);
        matTags.put("rock", BlockTags.BASE_STONE_OVERWORLD);
        matTags.put("iron", BlockTags.STONE_ORE_REPLACEABLES);
        matTags.put("anvil", BlockTags.ANVIL);
        matTags.put("water", BlockTags.create(ResLocation("minecraft", "water")));
        matTags.put("lava", BlockTags.create(ResLocation("minecraft", "lava")));
        matTags.put("leaves", BlockTags.LEAVES);
        matTags.put("plants", BlockTags.FLOWERS);
        matTags.put("vine", BlockTags.CLIMBABLE);
        matTags.put("sponge", BlockTags.create(ResLocation("minecraft", "sponge")));
        matTags.put("cloth", BlockTags.WOOL);
        matTags.put("fire", BlockTags.FIRE);
        matTags.put("sand", BlockTags.SAND);
        matTags.put("circuits", BlockTags.create(ResLocation("minecraft", "pressure_plates")));
        matTags.put("carpet", BlockTags.WOOL_CARPETS);
        matTags.put("redstoneLight", BlockTags.create(ResLocation("minecraft", "redstone_ores")));
        matTags.put("tnt", BlockTags.create(ResLocation("minecraft", "tnt")));
        matTags.put("coral", BlockTags.CORAL_BLOCKS);
        matTags.put("ice", BlockTags.ICE);
        matTags.put("packedIce", BlockTags.create(ResLocation("minecraft", "packed_ice")));
        matTags.put("snow", BlockTags.SNOW);
        matTags.put("craftedSnow", BlockTags.create(ResLocation("minecraft", "snow_block")));
        matTags.put("cactus", BlockTags.create(ResLocation("minecraft", "cactus")));
        matTags.put("clay", BlockTags.create(ResLocation("minecraft", "clay")));
        matTags.put("gourd", BlockTags.create(ResLocation("minecraft", "gourd")));
        matTags.put("dragonEgg", BlockTags.DRAGON_IMMUNE);
        matTags.put("portal", BlockTags.PORTALS);
        matTags.put("cake", BlockTags.create(ResLocation("minecraft", "cake")));
        matTags.put("web", BlockTags.create(ResLocation("minecraft", "web")));
    }
}