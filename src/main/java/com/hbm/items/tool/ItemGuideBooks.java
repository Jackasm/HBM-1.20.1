package com.hbm.items.tool;

/**
 * Конкретные варианты книги-гайда.
 *
 * <p>В 1.7.10 все варианты жили в одном предмете и различались
 * {@code getDamageValue()}: книгу можно было «перекрасить» в другую, поменяв
 * прочность стека, а любой посторонний код мог выдать не ту. Здесь у каждого
 * варианта свой предмет и свой класс над общим {@link ItemGuideBook}.</p>
 */
public final class ItemGuideBooks {

    private ItemGuideBooks() {
    }

    /** Стартовая книга, её мод выдаёт при первом входе (в 1.7.10 — BookType.STARTER). */
    public static class Starter extends ItemGuideBook {
        @Override
        public BookType getType() {
            return BookType.STARTER;
        }
    }

    /** Справочник по РБМК. */
    public static class Rbmk extends ItemGuideBook {
        @Override
        public BookType getType() {
            return BookType.RBMK;
        }
    }

    /** Справочник по адронному коллайдеру. */
    public static class Hadron extends ItemGuideBook {
        @Override
        public BookType getType() {
            return BookType.HADRON;
        }
    }

    /** Отладочная книга: страницы для проверки самого механизма. */
    public static class Test extends ItemGuideBook {
        @Override
        public BookType getType() {
            return BookType.TEST;
        }
    }
}
