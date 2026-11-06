package com.hbm.datagen.worldgen.structure.cleanup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Занятые позиции шаблона и их состояния — то, чего нельзя получить из
 * ванильного {@link StructureTemplate} без рефлексии: {@code palettes} приватное.
 *
 * <p>Шаблон один раз сериализуется через {@link StructureTemplate#save(CompoundTag)}
 * (публичный метод) и разбирается здесь: список {@code blocks} даёт позиции,
 * {@code palette} — состояния. Кэш по имени NBT-файла: все куски структуры
 * используют один и тот же шаблон.</p>
 *
 * <p>Нужно это для чистки после декорации (#102): повторная постановка шаблона
 * возвращает блоки структуры, а декор, которого в палитре нет, удаляется.
 * Удалять «всё, чего нет в палитре» нельзя — у руин в палитре есть брёвна,
 * и без проверки чистка съела бы саму структуру.</p>
 */
public final class TemplateLayout {

    private static final Map<String, TemplateLayout> CACHE = new ConcurrentHashMap<>();

    private final Map<BlockPos, BlockState> blocks;

    private TemplateLayout(Map<BlockPos, BlockState> blocks) {
        this.blocks = blocks;
    }

    /** Локальная позиция → состояние блока в шаблоне. */
    public Map<BlockPos, BlockState> blocks() {
        return this.blocks;
    }

    public static TemplateLayout of(String key, StructureTemplate template, HolderGetter<Block> blockRegistry) {
        TemplateLayout cached = CACHE.get(key);
        if (cached != null) {
            // Шаблон могли перезагрузить ресурспаком — сверяем размер.
            if (cached.blocks.size() > 0) return cached;
        }
        TemplateLayout parsed = parse(template, blockRegistry);
        TemplateLayout previous = CACHE.putIfAbsent(key, parsed);
        return previous == null ? parsed : previous;
    }

    private static TemplateLayout parse(StructureTemplate template, HolderGetter<Block> blockRegistry) {
        CompoundTag tag = template.save(new CompoundTag());

        ListTag palettes = tag.getList("palettes", ListTag.TAG_LIST);
        ListTag paletteTags = palettes.isEmpty() ? tag.getList("palette", ListTag.TAG_COMPOUND) : palettes.getList(0);

        BlockState[] palette = new BlockState[paletteTags.size()];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = NbtUtils.readBlockState(blockRegistry, paletteTags.getCompound(i));
        }

        ListTag blockTags = tag.getList("blocks", ListTag.TAG_COMPOUND);
        Map<BlockPos, BlockState> blocks = new HashMap<>(Math.max(16, blockTags.size()));
        for (int i = 0; i < blockTags.size(); i++) {
            CompoundTag entry = blockTags.getCompound(i);
            ListTag posTag = entry.getList("pos", ListTag.TAG_INT);
            BlockPos pos = new BlockPos(posTag.getInt(0), posTag.getInt(1), posTag.getInt(2));
            int state = entry.getInt("state");
            if (state >= 0 && state < palette.length) {
                blocks.put(pos, palette[state]);
            }
        }
        return new TemplateLayout(Map.copyOf(blocks));
    }
}
