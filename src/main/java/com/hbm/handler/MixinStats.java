package com.hbm.handler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Счётчики миксина отложенных тегов (#204).
 *
 * <p>Цифра нужна стенду и сборщику: «ноль предупреждений» сам по себе ничего не
 * доказывает — важно, что теги действительно были и их сняли. Стенд
 * {@code tile_probe} печатает {@code dropped_tags}; в обычной игре счётчик просто
 * живёт в памяти.</p>
 *
 * <p>Класс намеренно вне пакета {@code com.hbm.mixin}: пакет миксинов принадлежит
 * конфигу, и обращаться к его классам напрямую нельзя.</p>
 */
public final class MixinStats {

    /** Сколько отложенных тегов отброшено как заведомо мёртвые. */
    public static final AtomicLong DROPPED_DUMMY_TAGS = new AtomicLong();

    private MixinStats() {
    }

    public static long droppedDummyTags() {
        return DROPPED_DUMMY_TAGS.get();
    }

    public static void reset() {
        DROPPED_DUMMY_TAGS.set(0L);
    }
}
