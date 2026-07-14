package com.hbm.pawn;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class StructureLoader {

    public static Map<Block, Integer> countBlocksInTemplate(ServerLevel level, String modid, String path) {
        Map<Block, Integer> blockCount = new HashMap<>();
        StructureTemplate template = loadTemplate(level, modid, path);
        if (template == null) return blockCount;

        try {
            Field palettesField = StructureTemplate.class.getDeclaredField("palettes");
            palettesField.setAccessible(true);
            List<?> palettes = (List<?>) palettesField.get(template);
            if (palettes.isEmpty()) return blockCount;

            Object firstPalette = palettes.get(0);
            Field blocksField = firstPalette.getClass().getDeclaredField("blocks");
            blocksField.setAccessible(true);
            List<StructureTemplate.StructureBlockInfo> blocks = (List<StructureTemplate.StructureBlockInfo>) blocksField.get(firstPalette);

            Field stateField = StructureTemplate.StructureBlockInfo.class.getDeclaredField("state");
            stateField.setAccessible(true);

            for (StructureTemplate.StructureBlockInfo info : blocks) {
                BlockState state = (BlockState) stateField.get(info);
                if (state == null || state.isAir()) continue;
                Block block = state.getBlock();
                Block canonical = getCanonicalBlock(block);
                if (canonical == null) continue;
                blockCount.put(canonical, blockCount.getOrDefault(canonical, 0) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockCount;
    }

    public static Block getCanonicalBlock(Block block) {
        if (block.defaultBlockState().is(BlockTags.STAIRS)) {
            return Blocks.STONE_STAIRS;
        }
        if (block == Blocks.GLASS_PANE || block.defaultBlockState().is(BlockTags.IMPERMEABLE)) {
            return Blocks.GLASS_PANE;
        }
        if (block.defaultBlockState().is(BlockTags.PLANKS)) {
            return Blocks.OAK_PLANKS;
        }
        if (block.defaultBlockState().is(BlockTags.STONE_BRICKS)) {
            return Blocks.STONE_BRICKS;
        }

        if (block == Blocks.DIRT || block == Blocks.GRASS_BLOCK) {
            return null; // null означает, что блок не нужен
        }

        return block;
    }

    public static StructureTemplate loadTemplate(ServerLevel level, String modid, String path) {
        StructureTemplateManager manager = level.getStructureManager();
        ResourceLocation location = ResLocation(modid, path);
        return manager.get(location).orElse(null);
    }

    public static void printBlockCount(Map<Block, Integer> blockCount) {
        System.out.println("=== Required materials for mine construction ===");
        for (var entry : blockCount.entrySet()) {
            String name = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(entry.getKey())).toString();
            System.out.println(name + " : " + entry.getValue());
        }
    }

    public static void fillBelow(ServerLevel level, BlockPos origin, StructureTemplate template) {
        Vec3i size = template.getSize();
        int width = size.getX();
        int depth = size.getZ();

        // Определяем толщину периметра (3 блока)
        int borderThickness = 3;

        // Собираем карту позиций нижнего слоя ТОЛЬКО ПО ПЕРИМЕТРУ
        Map<BlockPos, BlockState> borderBlocks = new HashMap<>();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // Проверяем, находится ли позиция на периметре (первые/последние borderThickness блоков)
                boolean isBorder = x < borderThickness || x >= width - borderThickness ||
                        z < borderThickness || z >= depth - borderThickness;
                if (!isBorder) continue; // пропускаем внутренность

                BlockPos localPos = new BlockPos(x, 0, z);
                BlockState state = getBlockStateFromTemplate(template, localPos);
                if (state != null && !state.isAir()) {
                    Block block = state.getBlock();
                    if (block == Blocks.GRASS || block == Blocks.GRASS_BLOCK) {
                        state = Blocks.DIRT.defaultBlockState();
                    }
                    borderBlocks.put(new BlockPos(x, 0, z), state);
                }
            }
        }
        if (borderBlocks.isEmpty()) return;

        int maxDepth = 100;
        int currentY = origin.getY() - 1;
        while (currentY >= level.getMinBuildHeight() && currentY > origin.getY() - maxDepth) {
            boolean layerHasAir = false;
            for (var entry : borderBlocks.entrySet()) {
                BlockPos localPos = entry.getKey();
                BlockPos worldPos = origin.offset(localPos.getX(), currentY - origin.getY(), localPos.getZ());
                BlockState worldState = level.getBlockState(worldPos);
                if (worldState.isAir()) {
                    layerHasAir = true;
                    level.setBlock(worldPos, entry.getValue(), 3);
                } else if (worldState.getBlock() == Blocks.BEDROCK) {
                    return;
                }
            }
            if (!layerHasAir) break;
            currentY--;
        }
    }

    public static BlockState getBlockStateFromTemplate(StructureTemplate template, BlockPos localPos) {
        try {
            Field palettesField = StructureTemplate.class.getDeclaredField("palettes");
            palettesField.setAccessible(true);
            List<?> palettes = (List<?>) palettesField.get(template);
            if (palettes.isEmpty()) return null;
            Object firstPalette = palettes.get(0);
            Field blocksField = firstPalette.getClass().getDeclaredField("blocks");
            blocksField.setAccessible(true);
            List<StructureTemplate.StructureBlockInfo> blocks = (List<StructureTemplate.StructureBlockInfo>) blocksField.get(firstPalette);
            Field posField = StructureTemplate.StructureBlockInfo.class.getDeclaredField("pos");
            posField.setAccessible(true);
            Field stateField = StructureTemplate.StructureBlockInfo.class.getDeclaredField("state");
            stateField.setAccessible(true);
            for (StructureTemplate.StructureBlockInfo info : blocks) {
                BlockPos infoPos = (BlockPos) posField.get(info);
                if (infoPos.equals(localPos)) {
                    return (BlockState) stateField.get(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void terraform(ServerLevel level, BlockPos origin, StructureTemplate template) {
        Vec3i size = template.getSize();
        int width = size.getX();
        int depth = size.getZ();
        int borderThickness = 3;

        // 1. Собираем высоты земли по всему периметру
        Map<BlockPos, Integer> groundHeights = new HashMap<>();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                boolean isBorder = x < borderThickness || x >= width - borderThickness ||
                        z < borderThickness || z >= depth - borderThickness;
                if (!isBorder) continue;

                BlockPos worldPos = origin.offset(x, 0, z);
                int groundY = getGroundHeight(level, worldPos);
                groundHeights.put(new BlockPos(x, z, 0), groundY);
            }
        }

        // 2. Находим минимальную и максимальную высоту земли по периметру
        int minGroundY = groundHeights.values().stream().min(Integer::compareTo).orElse(origin.getY());
        int maxGroundY = groundHeights.values().stream().max(Integer::compareTo).orElse(origin.getY());
        int foundationY = origin.getY(); // уровень фундамента шахты

        // 3. Если перепад высот небольшой (< 5 блоков) – используем обычную подушку
        if (maxGroundY - minGroundY < 5) {
            fillBelow(level, origin, template);
            return;
        }

        // 4. Создаём ступеньки от уровня земли до фундамента
        for (var entry : groundHeights.entrySet()) {
            int x = entry.getKey().getX();
            int z = entry.getKey().getZ();
            int groundY = entry.getValue();

            if (groundY <= foundationY) continue; // земля ниже фундамента – не нужно террасировать

            // Создаём ступеньки от уровня земли до фундамента
            for (int y = groundY; y > foundationY; y--) {
                BlockPos stepPos = origin.offset(x, y - origin.getY(), z);
                BlockState worldState = level.getBlockState(stepPos);

                // Если блок воздуха или жидкость – заполняем землёй/камнем
                if (worldState.isAir() || isLiquid(worldState)) {
                    // Выбираем подходящий блок для ступеньки (земля, камень, дерн)
                    BlockState fillBlock = getTerraceBlock(level, stepPos, y == groundY);
                    level.setBlock(stepPos, fillBlock, 3);
                }
            }
        }

        // 5. Дополнительно: сглаживаем края (заменяем резкие перепады)
        smoothEdges(level, origin, template, borderThickness);
    }

    // Вспомогательный метод: получить высоту земли (первый не-воздух и не-жидкость)
    private static int getGroundHeight(ServerLevel level, BlockPos pos) {
        for (int y = pos.getY(); y > level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && !isLiquid(state)) {
                return y;
            }
        }
        return pos.getY();
    }

    // Вспомогательный метод: определить, жидкость ли блок
    private static boolean isLiquid(BlockState state) {
        return state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA;
    }

    // Выбор блока для ступеньки (верхний слой – трава/дерн, остальные – земля/камень)
    private static BlockState getTerraceBlock(ServerLevel level, BlockPos pos, boolean isTop) {
        // Верхний слой – трава или дерн (если биом позволяет)
        if (isTop) {
            // Можно определить биом и выбрать подходящий блок
            var biome = level.getBiome(pos);
            if (biome.is(Biomes.DESERT) || biome.is(Biomes.BADLANDS)) {
                return Blocks.SAND.defaultBlockState();
            }
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }
        // Нижние слои – земля или камень
        return Blocks.DIRT.defaultBlockState();
    }

    // Сглаживание краёв – заменяем резкие углы на пологие ступеньки
    private static void smoothEdges(ServerLevel level, BlockPos origin, StructureTemplate template, int borderThickness) {
        Vec3i size = template.getSize();
        int width = size.getX();
        int depth = size.getZ();

        // Проходим по внешнему периметру и сглаживаем перепады высот
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                // Только внешний слой периметра (толщина 1)
                boolean isOuterBorder = x == 0 || x == width - 1 || z == 0 || z == depth - 1;
                if (!isOuterBorder) continue;

                BlockPos worldPos = origin.offset(x, 0, z);
                int currentY = getGroundHeight(level, worldPos);

                // Проверяем соседние позиции для сглаживания
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        int nx = x + dx;
                        int nz = z + dz;
                        if (nx < 0 || nx >= width || nz < 0 || nz >= depth) continue;

                        BlockPos neighborPos = origin.offset(nx, 0, nz);
                        int neighborY = getGroundHeight(level, neighborPos);

                        // Если перепад больше 2 блоков – добавляем промежуточную ступеньку
                        if (Math.abs(currentY - neighborY) > 2) {
                            int stepY = Math.min(currentY, neighborY) + 1;
                            BlockPos stepPos = new BlockPos(worldPos.getX(), stepY, worldPos.getZ());
                            if (level.getBlockState(stepPos).isAir()) {
                                level.setBlock(stepPos, Blocks.DIRT.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
        }
    }
}