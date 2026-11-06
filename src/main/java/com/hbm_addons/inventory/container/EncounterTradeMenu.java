package com.hbm_addons.inventory.container;

import com.hbm_addons.entity.mob.EntityEncounterVillager;
import com.hbm_addons.main.AddonMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EncounterTradeMenu extends AbstractContainerMenu {

    /* ============================================================
     *  КООРДИНАТЫ — ПОДГОНЯЕШЬ В ДЕБАГГЕРЕ
     * ============================================================ */

    private static final int SLOT_SIZE = 18;

    // --- ИНВЕНТАРЬ МОБА (3×9, первые 6 — экипировка) ---
    private static final int MOB_INV_X = 8;
    private static final int MOB_INV_Y = 8;
    private static final int MOB_INV_COLS = 3;
    private static final int MOB_INV_ROWS = 9;

    // --- ПАНЕЛЬ ОБМЕНА МОБА (5×2 = 10 слотов) ---
    private static final int MOB_TRADE_X = 64;
    private static final int MOB_TRADE_Y = 80;
    private static final int MOB_TRADE_COLS = 2;
    private static final int MOB_TRADE_ROWS = 5;

    // --- ПАНЕЛЬ ОБМЕНА ИГРОКА (5×2 = 10 слотов) ---
    private static final int PLAYER_TRADE_X = 136;
    private static final int PLAYER_TRADE_Y = 80;
    private static final int PLAYER_TRADE_COLS = 2;
    private static final int PLAYER_TRADE_ROWS = 5;

    // --- ИНВЕНТАРЬ ИГРОКА (3×9) ---
    private static final int PLAYER_INV_X = 174;
    private static final int PLAYER_INV_Y = 8;
    private static final int PLAYER_INV_COLS = 3;
    private static final int PLAYER_INV_ROWS = 9;

    // --- ХОТБАР ИГРОКА (1×9) ---
    private static final int PLAYER_HOTBAR_X = 232;
    private static final int PLAYER_HOTBAR_Y = 8;
    private static final int PLAYER_HOTBAR_ROWS = 9;

    @Nullable
    private final EntityEncounterVillager mob;
    private final Container mobInventory;
    private final Container mobTradePanel;
    private final Container playerTradePanel;

    /** Клиентский конструктор из сетевого пакета. */
    public EncounterTradeMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, getMobFromBuf(playerInv.player.level(), buf));
    }

    /** Основной конструктор. mob == null допустим только на клиенте при ошибке. */
    public EncounterTradeMenu(int id, Inventory playerInv, @Nullable EntityEncounterVillager mob) {
        super(AddonMenus.ENCOUNTER_TRADE.get(), id);
        this.mob = mob;

        // Инвентарь моба — 27 слотов
        this.mobInventory = mob != null
                ? mob.getLootInventory()
                : new SimpleContainer(27);

        // Панели обмена живут только в меню, не в мобе
        this.mobTradePanel = new SimpleContainer(MOB_TRADE_COLS * MOB_TRADE_ROWS);
        this.playerTradePanel = new SimpleContainer(PLAYER_TRADE_COLS * PLAYER_TRADE_ROWS);

        // === ИНВЕНТАРЬ МОБА 3×9 ===
        for (int row = 0; row < MOB_INV_ROWS; row++) {
            for (int col = 0; col < MOB_INV_COLS; col++) {
                int index = row * MOB_INV_COLS + col;
                int x = MOB_INV_X + col * SLOT_SIZE;
                int y = MOB_INV_Y + row * SLOT_SIZE;
                this.addSlot(new Slot(mobInventory, index, x, y));
            }
        }

        // === ПАНЕЛЬ ОБМЕНА МОБА ===
        for (int row = 0; row < MOB_TRADE_ROWS; row++) {
            for (int col = 0; col < MOB_TRADE_COLS; col++) {
                int x = MOB_TRADE_X + col * SLOT_SIZE;
                int y = MOB_TRADE_Y + row * SLOT_SIZE;
                this.addSlot(new Slot(mobTradePanel, row * MOB_TRADE_COLS + col, x, y));
            }
        }

        // === ПАНЕЛЬ ОБМЕНА ИГРОКА ===
        for (int row = 0; row < PLAYER_TRADE_ROWS; row++) {
            for (int col = 0; col < PLAYER_TRADE_COLS; col++) {
                int x = PLAYER_TRADE_X + col * SLOT_SIZE;
                int y = PLAYER_TRADE_Y + row * SLOT_SIZE;
                this.addSlot(new Slot(playerTradePanel, row * PLAYER_TRADE_COLS + col, x, y));
            }
        }

        // ИНВЕНТАРЬ ИГРОКА 3×9 (вертикальные столбцы)
        for (int visRow = 0; visRow < PLAYER_INV_ROWS; visRow++) {
            for (int visCol = 0; visCol < PLAYER_INV_COLS; visCol++) {
                int index = 9 + visRow + visCol * 9;
                int x = PLAYER_INV_X + visCol * SLOT_SIZE;
                int y = PLAYER_INV_Y + visRow * SLOT_SIZE;
                this.addSlot(new Slot(playerInv, index, x, y));
            }
        }

        for (int col = 0; col < PLAYER_HOTBAR_ROWS; col++) {
            int y = PLAYER_HOTBAR_Y + col * SLOT_SIZE;
            this.addSlot(new Slot(playerInv, col, PLAYER_HOTBAR_X, y));
        }
    }

    @Nullable
    private static EntityEncounterVillager getMobFromBuf(Level level, FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        var entity = level.getEntity(entityId);
        return entity instanceof EntityEncounterVillager ev ? ev : null;
    }

    @Nullable
    public EntityEncounterVillager getMob() {
        return mob;
    }

    public Container getMobTradePanel() {
        return mobTradePanel;
    }

    public Container getPlayerTradePanel() {
        return playerTradePanel;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return mob != null && mob.isAlive() && mob.distanceToSqr(player) < 64.0;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        // TODO: shift-клик. Пока — простой обмен между инвентарём игрока и панелью обмена.
        return ItemStack.EMPTY;
    }

    /** Открыть GUI. Вызывается из моба. Безопасно для dedicated server. */
    public static void openFor(Player player, EntityEncounterVillager mob) {
        if (player instanceof ServerPlayer sp) {
            NetworkHooks.openScreen(sp,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new EncounterTradeMenu(id, inv, mob),
                            mob.getDisplayName()),
                    buf -> buf.writeVarInt(mob.getId()));
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        if (player.level().isClientSide) return;

        // Панель обмена игрока — всегда возвращаем игроку
        returnItemsToPlayer(playerTradePanel, player);

        // Панель обмена моба — только если моб жив
        if (mob != null && mob.isAlive()) {
            returnItemsToContainer(mobTradePanel, mob.getLootInventory());
        } else {
            // Моб мёртв или деспавн — предметы уходят игроку (либо дропаются)
            returnItemsToPlayer(mobTradePanel, player);
        }
    }

    /** Возврат предметов из панели в инвентарь игрока; лишнее — выбрасывается на землю. */
    private static void returnItemsToPlayer(Container from, Player player) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < from.getContainerSize(); i++) {
            ItemStack stack = from.getItem(i);
            if (stack.isEmpty()) continue;

            ItemStack remaining = stack.copy();
            from.setItem(i, ItemStack.EMPTY);

            // Сначала пробуем положить в существующие стеки
            for (int j = 0; j < inv.getContainerSize() && !remaining.isEmpty(); j++) {
                ItemStack target = inv.getItem(j);
                if (ItemStack.isSameItemSameTags(target, remaining)) {
                    int space = target.getMaxStackSize() - target.getCount();
                    if (space > 0) {
                        int move = Math.min(space, remaining.getCount());
                        target.grow(move);
                        remaining.shrink(move);
                    }
                }
            }

            // Потом в пустые слоты
            for (int j = 0; j < inv.getContainerSize() && !remaining.isEmpty(); j++) {
                if (inv.getItem(j).isEmpty()) {
                    inv.setItem(j, remaining);
                    remaining = ItemStack.EMPTY;
                }
            }

            // Не влезло — выбрасываем
            if (!remaining.isEmpty()) {
                player.drop(remaining, false);
            }
        }
    }

    /** Возврат предметов из панели в контейнер моба; лишнее — выбрасывается. */
    private static void returnItemsToContainer(Container from, Container to) {
        for (int i = 0; i < from.getContainerSize(); i++) {
            ItemStack stack = from.getItem(i);
            if (stack.isEmpty()) continue;

            ItemStack remaining = stack.copy();
            from.setItem(i, ItemStack.EMPTY);

            for (int j = 0; j < to.getContainerSize() && !remaining.isEmpty(); j++) {
                if (to.getItem(j).isEmpty()) {
                    to.setItem(j, remaining);
                    remaining = ItemStack.EMPTY;
                }
            }

            // Не влезло — дропаем от лица сущности. Упрощённо — в мир.
            if (!remaining.isEmpty()) {
                // Если хочешь — можно ItemEntity спавнить.
                // Пока просто игнорируем или дропаем рядом с игроком.
            }
        }
    }
}