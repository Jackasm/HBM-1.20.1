package com.hbm.api.quest;

import net.minecraft.world.entity.player.Player;

/**
 * Шов между ядром мода и аддон-контентом квестов.
 * Ядро не знает об аддоне ничего, кроме этого интерфейса: реализацию
 * подставляет аддон при инициализации. Пока её нет, {@link #get()}
 * возвращает заглушку, которая всё запрещает.
 */
public interface QuestHooks {

    /** Включены ли квесты в конфиге аддона. */
    boolean isAvailable();

    boolean hasActiveQuest(Player player, String questId);

    boolean hasCompletedQuest(Player player, String questId);

    /** Клиентская сторона: принять квест и отправить запрос на сервер. */
    void accept(Player player, String questId);

    /** Клиентская сторона: сдать квест. */
    void complete(Player player, String questId);

    QuestHooks UNAVAILABLE = new QuestHooks() {
        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean hasActiveQuest(Player player, String questId) {
            return false;
        }

        @Override
        public boolean hasCompletedQuest(Player player, String questId) {
            return false;
        }

        @Override
        public void accept(Player player, String questId) {
        }

        @Override
        public void complete(Player player, String questId) {
        }
    };

    static QuestHooks get() {
        return Holder.INSTANCE;
    }

    static void set(QuestHooks hooks) {
        Holder.INSTANCE = hooks == null ? UNAVAILABLE : hooks;
    }

    final class Holder {
        private static QuestHooks INSTANCE = UNAVAILABLE;

        private Holder() {
        }
    }
}
