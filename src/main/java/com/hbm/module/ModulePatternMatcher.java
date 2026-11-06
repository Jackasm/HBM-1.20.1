package com.hbm.module;

import com.hbm.items.special.ItemBedrockOreNew;
import com.hbm.util.BufferUtil;
import com.hbm.util.ItemStackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Сравнение предметов по образцу: «тот же предмет», «тот же предмет и урон»
 * или «тот же тег предмета».
 *
 * <p>Порт {@code ref:module/ModulePatternMatcher.java}. В 1.7.10 третьим
 * режимом был ore dictionary; в 1.20.1 его заменили теги предметов, поэтому
 * режим хранит идентификатор тега ({@code forge:ingots/iron}) и совпадает,
 * если у входного стека этот тег есть. {@code ItemStackUtil.getOreDictNames}
 * в порте уже отдаёт имена тегов в том же виде, так что остальная логика
 * перенесена один в один.</p>
 *
 * <p>Одно отличие от эталона вынужденное: в 1.7.10 режим по умолчанию выбирался
 * по {@code Item#getHasSubtypes} — в 1.20.1 такого признака нет. Поэтому
 * {@link #initPatternStandard} ставит {@link #MODE_EXACT}: считать только
 * идентичные предметы, а остальное — колесом на слоте.</p>
 */
public class ModulePatternMatcher {

    public static final String MODE_EXACT = "exact";
    public static final String MODE_WILDCARD = "wildcard";
    public static final String MODE_BEDROCK = "bedrock";

    /** Режим на каждый слот образца; {@code null} — режим ещё не выбран. */
    public final String[] modes;

    public ModulePatternMatcher() {
        this(1);
    }

    public ModulePatternMatcher(int count) {
        this.modes = new String[count];
    }

    /** Умный выбор режима: сначала пробуем тег по префиксу, потом общие режимы. */
    public void initPatternSmart(Level level, ItemStack stack, int index) {
        if (level != null && level.isClientSide) return;

        if (stack == null || stack.isEmpty()) {
            modes[index] = null;
            return;
        }

        List<String> names = ItemStackUtil.getOreDictNames(stack);

        if (iterateAndCheck(names, index, "ingot")) return;
        if (iterateAndCheck(names, index, "block")) return;
        if (iterateAndCheck(names, index, "dust")) return;
        if (iterateAndCheck(names, index, "nugget")) return;
        if (iterateAndCheck(names, index, "plate")) return;

        if (stack.getItem() instanceof ItemBedrockOreNew) {
            modes[index] = MODE_BEDROCK;
        } else {
            modes[index] = MODE_EXACT;
        }
    }

    private boolean iterateAndCheck(List<String> names, int index, String prefix) {
        // В 1.7.10 тег начинался с префикса ("ingotCopper"); в 1.20.1 перед ним
        // стоит пространство имён ("forge:ingots/copper"), поэтому префикс ищем
        // по последнему сегменту пути, а не по началу строки.
        for (String name : names) {
            String path = name.contains(":") ? name.substring(name.indexOf(':') + 1) : name;
            if (path.startsWith(prefix)) {
                modes[index] = name;
                return true;
            }
        }
        return false;
    }

    /** Режим по умолчанию для только что вставленного образца. */
    public void initPatternStandard(Level level, ItemStack stack, int index) {
        if (level != null && level.isClientSide) return;

        if (stack == null || stack.isEmpty()) {
            modes[index] = null;
            return;
        }

        modes[index] = stack.getItem() instanceof ItemBedrockOreNew ? MODE_BEDROCK : MODE_EXACT;
    }

    /** Следующий режим: exact → wildcard → теги образца → exact. */
    public void nextMode(Level level, ItemStack pattern, int index) {
        if (level != null && level.isClientSide) return;

        if (pattern == null || pattern.isEmpty()) {
            modes[index] = null;
            return;
        }

        if (modes[index] == null) {
            modes[index] = MODE_EXACT;
            return;
        }

        if (MODE_EXACT.equals(modes[index])) {
            modes[index] = pattern.getItem() instanceof ItemBedrockOreNew ? MODE_BEDROCK : MODE_WILDCARD;
            return;
        }

        if (MODE_BEDROCK.equals(modes[index])) {
            modes[index] = MODE_WILDCARD;
            return;
        }

        List<String> names = ItemStackUtil.getOreDictNames(pattern);

        if (MODE_WILDCARD.equals(modes[index])) {
            modes[index] = names.isEmpty() ? MODE_EXACT : names.get(0);
            return;
        }

        if (names.size() < 2 || modes[index].equals(names.get(names.size() - 1))) {
            modes[index] = MODE_EXACT;
            return;
        }

        for (int j = 0; j < names.size() - 1; j++) {
            if (modes[index].equals(names.get(j))) {
                modes[index] = names.get(j + 1);
                return;
            }
        }
    }

    /** Подходит ли входной стек под образец с учётом режима слота. */
    public boolean isValidForFilter(ItemStack filter, int index, ItemStack input) {
        if (filter == null || filter.isEmpty()) return false;
        if (input == null || input.isEmpty()) return false;

        String mode = modes[index];

        if (mode == null) {
            modes[index] = mode = MODE_EXACT;
        }

        switch (mode) {
            case MODE_EXACT -> {
                // Эталон сравнивал item + damage и игнорировал NBT — isSameItem делает то же.
                return ItemStack.isSameItem(input, filter);
            }
            case MODE_WILDCARD -> {
                return input.getItem() == filter.getItem();
            }
            case MODE_BEDROCK -> {
                if (input.getItem() != filter.getItem()) return false;
                if (!(input.getItem() instanceof ItemBedrockOreNew ore)) return false;
                return ore.getGrade(input.getDamageValue()) == ((ItemBedrockOreNew) filter.getItem()).getGrade(filter.getDamageValue());
            }
            default -> {
                return ItemStackUtil.getOreDictNames(input).contains(mode);
            }
        }
    }

    public void readFromNBT(CompoundTag nbt) {
        for (int i = 0; i < modes.length; i++) {
            modes[i] = nbt.contains("mode" + i) ? nbt.getString("mode" + i) : null;
        }
    }

    public void writeToNBT(CompoundTag nbt) {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i] != null) nbt.putString("mode" + i, modes[i]);
        }
    }

    public void serialize(ByteBuf buf) {
        for (String mode : modes) BufferUtil.writeString(buf, mode);
    }

    public void deserialize(ByteBuf buf) {
        for (int i = 0; i < modes.length; i++) modes[i] = BufferUtil.readString(buf);
    }

    /** Подпись режима для тултипа слота. */
    public static String getLabel(String mode) {
        if (mode == null) return ChatFormatting.YELLOW + "не выбран";
        return switch (mode) {
            case MODE_EXACT -> ChatFormatting.YELLOW + "Предмет и урон совпадают";
            case MODE_WILDCARD -> ChatFormatting.YELLOW + "Совпадает предмет";
            case MODE_BEDROCK -> ChatFormatting.YELLOW + "Совпадают предмет и сорт руды";
            default -> ChatFormatting.YELLOW + "Совпадает тег: " + mode;
        };
    }
}
