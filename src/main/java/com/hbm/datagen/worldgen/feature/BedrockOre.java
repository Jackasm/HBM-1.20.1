package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.WorldConfig;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.tileentity.block.TileEntityBedrockOre;
import com.hbm.util.WeightedRandomGeneric;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BedrockOre {

    public static List<WeightedRandomGeneric<BedrockOreDefinition>> weightedOres = new ArrayList<>();
    public static List<WeightedRandomGeneric<BedrockOreDefinition>> weightedOresNether = new ArrayList<>();

    public static void init() {
        weightedOres.clear();
        weightedOresNether.clear();

        // === OVERWORLD РУДЫ ===
        // Железо
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(Items.RAW_IRON, 8), 1, 0xD78A16), WorldConfig.bedrockIronWeight.get());
        // Медь
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(Items.RAW_COPPER, 8), 1, 0xEF7213), WorldConfig.bedrockCopperWeight.get());
        // Бура
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.POWDER_BORAX.get(), 8), 3, 0xD3D3D3), WorldConfig.bedrockBoraxWeight.get());
        // Хлорокальцит
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.POWDER_CHLOROCALCITE.get(), 8), 3, 0x8FBC8F), WorldConfig.bedrockChlorocalciteWeight.get());
        // Асбест
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.INGOT_ASBESTOS.get(), 8), 2, 0xC0C0C0), WorldConfig.bedrockAsbestosWeight.get());
        // Ниобий
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.INGOT_NIOBIUM.get(), 8), 2, 0x708090), WorldConfig.bedrockNiobiumWeight.get());
        // Неодим
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.INGOT_NEODYMIUM.get(), 8), 3, 0xCD7F32), WorldConfig.bedrockNeodymiumWeight.get());
        // Титан
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.RAW_TITANIUM.get(), 8), 2, 0xA9A9A9), WorldConfig.bedrockTitaniumWeight.get());
        // Вольфрам
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.RAW_TUNGSTEN.get(), 8), 2, 0x696969), WorldConfig.bedrockTungstenWeight.get());
        // Золото
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(Items.RAW_GOLD, 8), 1, 0xFFD700), WorldConfig.bedrockGoldWeight.get());
        // Уран
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.RAW_URANIUM.get(), 8), 4, 0x7CFC00, new FluidStackHBM(Fluids.SULFURIC_ACID.get(), 500)), WorldConfig.bedrockUraniumWeight.get());
        // Торий
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.RAW_THORIUM.get(), 8), 4, 0x808080, new FluidStackHBM(Fluids.SULFURIC_ACID.get(), 500)), WorldConfig.bedrockThoriumWeight.get());
        // Уголь
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(Items.COAL, 8), 1, 0x202020), WorldConfig.bedrockCoalWeight.get());
        // Селитра
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.NITER.get(), 8), 2, 0x808080, new FluidStackHBM(Fluids.PEROXIDE.get(), 500)), WorldConfig.bedrockNiterWeight.get());
        // Флюорит
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.FLUORITE.get(), 8), 1, 0x87CEEB), WorldConfig.bedrockFluoriteWeight.get());
        // Редстоун
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(Items.REDSTONE, 8), 1, 0xD01010), WorldConfig.bedrockRedstoneWeight.get());
        // Редкоземельные
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.RARE_EARTH_CHUNK.get(), 8), 2, 0x8F9999, new FluidStackHBM(Fluids.PEROXIDE.get(), 500)), WorldConfig.bedrockRareEarthWeight.get());
        // Бокситы (алюминий)
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(ModItems.RAW_ALUMINIUM.get(), 8), 1, 0xEF7213), WorldConfig.bedrockBauxiteWeight.get());
        // Изумруд
        registerOre(weightedOres, new BedrockOreDefinition(new ItemStack(Items.EMERALD, 8), 1, 0x3FDD85), WorldConfig.bedrockEmeraldWeight.get());

        // === NETHER РУДЫ ===
        // Светокамень
        registerOre(weightedOresNether, new BedrockOreDefinition(new ItemStack(Items.GLOWSTONE_DUST, 8), 1, 0xF9FF4D), WorldConfig.bedrockGlowstoneWeight.get());
        // Фосфор
        registerOre(weightedOresNether, new BedrockOreDefinition(new ItemStack(ModItems.POWDER_FIRE.get(), 8), 1, 0xD7341F), WorldConfig.bedrockPhosphorusWeight.get());
        // Кварц
        registerOre(weightedOresNether, new BedrockOreDefinition(new ItemStack(Items.QUARTZ, 8), 1, 0xF0EFDD), WorldConfig.bedrockQuartzWeight.get());
    }

    private static void registerOre(List<WeightedRandomGeneric<BedrockOreDefinition>> list, BedrockOreDefinition def, int weight) {
        if (weight > 0) {
            list.add(new WeightedRandomGeneric<>(def, weight));
        }
    }

    public static void generate(WorldGenLevel level, int chunkX, int chunkZ, Random rand) {
        if (WorldConfig.newBedrockOres.get()) {
            // Упрощённая версия с базовой рудой
            if (rand.nextInt(WorldConfig.bedrockOreChance.get()) == 0) {
                int x = chunkX + rand.nextInt(16);
                int z = chunkZ + rand.nextInt(16);
                generate(level, x, z, new ItemStack(ModItems.ORE_BEDROCK.get()), null, 0xD78A16, 1);
            }
        } else {
            // Полная версия с выбором из weightedOres
            if (!weightedOres.isEmpty() && rand.nextInt(3) == 0) {
                WeightedRandomGeneric<BedrockOreDefinition> entry = getRandomItem(rand, weightedOres);
                if (entry != null) {
                    BedrockOreDefinition def = entry.get();
                    int x = chunkX + rand.nextInt(2) + 8;
                    int z = chunkZ + rand.nextInt(2) + 8;
                    generate(level, x, z, def.stack, def.acid, def.color, def.tier);
                }
            }
        }
    }

    private static <T> WeightedRandomGeneric<T> getRandomItem(Random rand, List<WeightedRandomGeneric<T>> list) {
        int totalWeight = list.stream().mapToInt(WeightedRandomGeneric::getWeight).sum();
        if (totalWeight <= 0) return null;
        int roll = rand.nextInt(totalWeight);
        int current = 0;
        for (WeightedRandomGeneric<T> entry : list) {
            current += entry.getWeight();
            if (roll < current) return entry;
        }
        return null;
    }

    public static void generate(WorldGenLevel level, int x, int z, ItemStack stack, FluidStackHBM acid, int color, int tier) {
        generate(level, x, z, stack, acid, color, tier, ModBlocks.STONE_DEPTH.get());
    }

    public static void generate(WorldGenLevel level, int x, int z, ItemStack stack, FluidStackHBM acid, int color, int tier, Block depthRock) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Генерация руды (3x3 область)
        for (int ix = x - 1; ix <= x + 1; ix++) {
            for (int iz = z - 1; iz <= z + 1; iz++) {
                pos.set(ix, 0, iz);
                if (level.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                    if ((ix == x && iz == z) || level.getRandom().nextBoolean()) {
                        level.setBlock(pos, ModBlocks.ORE_BEDROCK.get().defaultBlockState(), 3);
                        if (level.getBlockEntity(pos) instanceof TileEntityBedrockOre ore) {
                            ore.resource = stack.copy();
                            ore.acidRequirement = acid != null ? acid.copy() : null;
                            ore.tier = tier;
                            ore.color = color;
                            ore.shape = level.getRandom().nextInt(10);
                        }
                    }
                }
            }
        }

        // Замена окружающих блоков на глубинный камень
        for (int ix = x - 3; ix <= x + 3; ix++) {
            for (int iz = z - 3; iz <= z + 3; iz++) {
                for (int iy = 1; iy < 7; iy++) {
                    pos.set(ix, iy, iz);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.BEDROCK) {
                        level.setBlock(pos, depthRock.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
