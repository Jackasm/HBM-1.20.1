package com.hbm.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ChatBuilder {

    private MutableComponent root;
    private MutableComponent current;

    private ChatBuilder(Component component) {
        this.root = component.copy();
        this.current = this.root;
    }

    public static ChatBuilder start(String text) {
        return new ChatBuilder(Component.literal(text));
    }

    public static ChatBuilder startTranslation(String key, Object... args) {
        return new ChatBuilder(Component.translatable(key, args));
    }

    public ChatBuilder next(String text) {
        MutableComponent append = Component.literal(text);
        this.current.append(append);
        this.current = append;
        return this;
    }

    public ChatBuilder nextTranslation(String key, Object... args) {
        MutableComponent append = Component.translatable(key, args);
        this.current.append(append);
        this.current = append;
        return this;
    }

    public ChatBuilder color(ChatFormatting format) {
        this.current.withStyle(this.current.getStyle().withColor(format));
        return this;
    }

    public ChatBuilder color(ChatFormatting format, ChatFormatting... additionalFormats) {
        Style style = this.current.getStyle();
        style = style.withColor(format);
        for (ChatFormatting additional : additionalFormats) {
            style = style.applyFormat(additional);
        }
        this.current.withStyle(style);
        return this;
    }

    /** Will recursively go over all Components added to the root and then set the style */
    public ChatBuilder colorAll(ChatFormatting format) {
        List<Component> list = new ArrayList<>();
        list.add(root);

        ListIterator<Component> it = list.listIterator();

        while (it.hasNext()) {
            Component component = it.next();
            // Создаем новый стиль с цветом
            Style style = component.getStyle().withColor(format);
            // Для MutableComponent можем изменить стиль
            if (component instanceof MutableComponent mutable) {
                mutable.withStyle(style);
            }

            // Добавляем дочерние компоненты в список для обработки
            for (Component sibling : component.getSiblings()) {
                it.add(sibling);
            }
        }

        return this;
    }

    public MutableComponent build() {
        return this.root;
    }

    public MutableComponent flush() {
        return this.root;
    }

    // Дополнительные полезные методы для совместимости
    public ChatBuilder bold() {
        this.current.withStyle(this.current.getStyle().withBold(true));
        return this;
    }

    public ChatBuilder italic() {
        this.current.withStyle(this.current.getStyle().withItalic(true));
        return this;
    }

    public ChatBuilder underlined() {
        this.current.withStyle(this.current.getStyle().withUnderlined(true));
        return this;
    }

    public ChatBuilder strikethrough() {
        this.current.withStyle(this.current.getStyle().withStrikethrough(true));
        return this;
    }

    public ChatBuilder obfuscated() {
        this.current.withStyle(this.current.getStyle().withObfuscated(true));
        return this;
    }

    // Метод для добавления кликабельной ссылки
    public ChatBuilder clickEvent(ClickEvent event) {
        this.current.withStyle(this.current.getStyle().withClickEvent(event));
        return this;
    }

    // Метод для добавления подсказки при наведении
    public ChatBuilder hoverEvent(HoverEvent event) {
        this.current.withStyle(this.current.getStyle().withHoverEvent(event));
        return this;
    }

    // Статический метод для быстрого создания цветного текста
    public static Component colored(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(color);
    }

    public static Component coloredTranslation(String key, ChatFormatting color, Object... args) {
        return Component.translatable(key, args).withStyle(color);
    }
}