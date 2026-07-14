package com.hbm.inventory.container;

import com.hbm.tileentity.storage.TileEntityFileCabinet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerFileCabinet extends AbstractContainerMenu {

    private final BlockPos pos;
    private final Player player;
    private final Inventory playerInv;
    @Nullable
    private TileEntityFileCabinet cabinet;

    // Конструктор для сервера
    public ContainerFileCabinet(int id, Inventory playerInv, TileEntityFileCabinet cabinet) {
        this(id, playerInv, cabinet.getBlockPos(), playerInv.player);
        this.cabinet = cabinet;

        if (cabinet.getLevel() != null && !cabinet.getLevel().isClientSide) {
            cabinet.startOpen(playerInv.player);
        }

        initSlots();
    }

    // Конструктор для клиента (без TileEntity)
    public ContainerFileCabinet(int id, Inventory playerInv, BlockPos pos) {
        this(id, playerInv, pos, playerInv.player);
        initSlots();
    }

    // Основной приватный конструктор
    private ContainerFileCabinet(int id, Inventory playerInv, BlockPos pos, Player player) {
        super(ModContainers.FILING_CABINET.get(), id);
        this.pos = pos;
        this.player = player;
        this.playerInv = playerInv; // Сохраняем инвентарь игрока
    }

    // Инициализация слотов (вызывается отдельно)
    private void initSlots() {
        // Очищаем старые слоты
        this.slots.clear();

        // 8 слотов шкафа (2 ряда по 4)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                int x = 53 + j * 18;
                int y = 18 + i * 36;
                int slotIndex = i * 4 + j;

                // Проверяем, что cabinet не null перед созданием слота
                if (getCabinet() != null) {
                    this.addSlot(new SlotItemHandler(getCabinet().getInventory(), slotIndex, x, y) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            return true;
                        }
                    });
                } else {
                    // Если cabinet null, создаем пустой слот
                    this.addSlot(new Slot(new DummyInventory(), slotIndex, x, y) {
                        @Override
                        public boolean mayPlace(@NotNull ItemStack stack) {
                            return true;
                        }
                    });
                }
            }
        }

        // Инвентарь игрока - используем сохраненный playerInv
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 88 + i * 18));
            }
        }

        // Хотбар игрока
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 146));
        }
    }

    // Метод для получения TileEntity (с отложенной загрузкой)
    @Nullable
    public TileEntityFileCabinet getCabinet() {
        if (cabinet == null) {
            // Пытаемся получить TileEntity из мира
            if (player.level().getBlockEntity(pos) instanceof TileEntityFileCabinet tile) {
                cabinet = tile;
            }
        }
        return cabinet;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        TileEntityFileCabinet cabinet = getCabinet();
        if (cabinet != null && cabinet.getLevel() != null && !cabinet.getLevel().isClientSide) {
            cabinet.stopOpen(player);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            // Если кликнули по слоту шкафа (первые 8 слотов)
            if (index < 8) {
                // Перемещаем в инвентарь игрока
                if (!this.moveItemStackTo(slotStack, 8, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Если кликнули по инвентарю игрока
            else {
                if (!this.moveItemStackTo(slotStack, 0, 8, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }

    // Dummy инвентарь для клиента
    private static class DummyInventory implements Container {

        public DummyInventory() {}

        @Override
        public int getContainerSize() { return 8; }
        @Override
        public boolean isEmpty() { return true; }
        @Override
        public @NotNull ItemStack getItem(int slot) { return ItemStack.EMPTY; }
        @Override
        public @NotNull ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
        @Override
        public @NotNull ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
        @Override
        public void setItem(int slot, @NotNull ItemStack stack) {}
        @Override
        public void setChanged() {}
        @Override
        public boolean stillValid(@NotNull Player player) { return false; }
        @Override
        public void clearContent() {}
    }
}