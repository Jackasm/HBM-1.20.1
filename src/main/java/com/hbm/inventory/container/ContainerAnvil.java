package com.hbm.inventory.container;

import com.hbm.inventory.SlotCraftingOutput;
import com.hbm.inventory.recipes.AnvilRecipes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerAnvil extends AbstractContainerMenu {

    private final ItemStackHandler input = new ItemStackHandler(2);
    private final ItemStackHandler output = new ItemStackHandler(1);
    public final int tier;
    private final ContainerLevelAccess access;
    private final Player player;

    public ContainerAnvil(int windowId, Inventory playerInventory, int tier) {
        this(windowId, playerInventory, tier, ContainerLevelAccess.NULL);
    }

    public ContainerAnvil(int windowId, Inventory playerInventory, int tier, ContainerLevelAccess access) {
        super(ModContainers.ANVIL.get(), windowId);
        this.tier = tier;
        this.access = access;
        this.player = playerInventory.player;

        // Input slots
        this.addSlot(new SmithingSlot(input, 0, 17, 27));
        this.addSlot(new SmithingSlot(input, 1, 53, 27));

        // Output slot
        this.addSlot(new SlotCraftingOutput(playerInventory.player, output, 0, 89, 27) {
            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                super.onTake(player, stack);

                ItemStack left = ContainerAnvil.this.input.getStackInSlot(0);
                ItemStack right = ContainerAnvil.this.input.getStackInSlot(1);

                if(left.isEmpty() || right.isEmpty()) {
                    return;
                }

                for(AnvilRecipes.AnvilSmithingRecipe rec : AnvilRecipes.getSmithing()) {
                    int i = rec.matchesInt(left, right);
                    if(i != -1) {
                        ContainerAnvil.this.input.extractItem(0, rec.amountConsumed(0, i == 1), false);
                        ContainerAnvil.this.input.extractItem(1, rec.amountConsumed(1, i == 1), false);
                        ContainerAnvil.this.updateSmithing();
                        return;
                    }
                }
            }
        });

        // Player inventory
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        // Player hotbar
        for(int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }
        this.updateSmithing();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // Output slot (index 2)
            if(index == 2) {
                if(!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            }
            // Input slots (index 0-1)
            else if(index < 2) {
                if(!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // Player inventory to input slots
            else if(index < 39) {
                // Попробовать переместить в слоты ввода
                if(!this.moveItemStackTo(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if(itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if(itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
            // Отдаем предметы обратно игроку при закрытии GUI
            for(int i = 0; i < input.getSlots(); i++) {
                ItemStack stack = input.getStackInSlot(i);
                if(!stack.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(stack);
                }
            }
        });
    }

    public class SmithingSlot extends SlotItemHandler {
        public SmithingSlot(IItemHandler itemHandler, int index, int x, int y) {
            super(itemHandler, index, x, y);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            ContainerAnvil.this.updateSmithing();
        }

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            super.onTake(player, stack);
            ContainerAnvil.this.updateSmithing();
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            super.set(stack);
            ContainerAnvil.this.updateSmithing();
        }
    }

    private void updateSmithing() {
        ItemStack left = this.input.getStackInSlot(0);
        ItemStack right = this.input.getStackInSlot(1);

        if(left.isEmpty() || right.isEmpty()) {
            this.output.setStackInSlot(0, ItemStack.EMPTY);
            return;
        }

        for(AnvilRecipes.AnvilSmithingRecipe rec : AnvilRecipes.getSmithing()) {
            boolean matches = rec.matches(left, right);
            boolean tierValid = rec.tier <= this.tier;

            if(matches && tierValid) {
                ItemStack outputStack = rec.getSimpleOutput();
                this.output.setStackInSlot(0, outputStack);
                return;
            }
        }

        this.output.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    public void slotsChanged(net.minecraft.world.@NotNull Container container) {
        super.slotsChanged(container);
        this.updateSmithing();
    }

    public void tryCraft(int recipeIndex, boolean shift) {
        var recipes = AnvilRecipes.getConstruction();
        if (recipeIndex >= 0 && recipeIndex < recipes.size()) {
            var recipe = recipes.get(recipeIndex);

            if (!recipe.isTierValid(this.tier)) return;

            Player player = getPlayer();
            if (player == null) return;

            if (shift) {
                int craftCount = 0;
                while (canCraftConstructionRecipe(recipe, player) && craftCount < 64) {
                    consumeItems(player, recipe);
                    giveResults(player, recipe);
                    craftCount++;
                }
            } else {
                if (canCraftConstructionRecipe(recipe, player)) {
                    consumeItems(player, recipe);
                    giveResults(player, recipe);
                }
            }
        }
    }

    private Player getPlayer() {
        return this.player;
    }

    private void consumeItems(Player player, AnvilRecipes.AnvilConstructionRecipe recipe) {
        Inventory inventory = player.getInventory();

        for (var input : recipe.input) {
            int countToConsume = input.getStackSize();

            if (input instanceof com.hbm.inventory.recipes.common.ComparableStack compStack) {
                ItemStack requiredStack = compStack.toStack();

                // Потребляем предметы из инвентаря
                for (int i = 0; i < inventory.getContainerSize() && countToConsume > 0; i++) {
                    ItemStack stackInSlot = inventory.getItem(i);
                    if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameTags(stackInSlot, requiredStack)) {
                        int consumeAmount = Math.min(countToConsume, stackInSlot.getCount());
                        stackInSlot.shrink(consumeAmount);
                        countToConsume -= consumeAmount;

                        if (stackInSlot.isEmpty()) {
                            inventory.setItem(i, ItemStack.EMPTY);
                        }
                    }
                }

            } else if (input instanceof com.hbm.inventory.recipes.common.TagStack tagStack) {

                // Потребляем предметы из тега
                for (int i = 0; i < inventory.getContainerSize() && countToConsume > 0; i++) {
                    ItemStack stackInSlot = inventory.getItem(i);
                    if (!stackInSlot.isEmpty() && stackInSlot.is(tagStack.tag)) {
                        int consumeAmount = Math.min(countToConsume, stackInSlot.getCount());
                        stackInSlot.shrink(consumeAmount);
                        countToConsume -= consumeAmount;

                        if (stackInSlot.isEmpty()) {
                            inventory.setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    private void giveResults(Player player, AnvilRecipes.AnvilConstructionRecipe recipe) {
        Inventory inventory = player.getInventory();

        for (var output : recipe.output) {
            // Проверяем шанс
            if (Math.random() <= output.chance) {
                ItemStack result = output.stack.copy();

                // Пытаемся добавить в инвентарь
                if (!inventory.add(result)) {
                    // Если не помещается, выбрасываем на землю
                    player.drop(result, false);
                }
            }
        }
    }

    private boolean canCraftConstructionRecipe(AnvilRecipes.AnvilConstructionRecipe recipe, Player player) {
        for (var input : recipe.input) {
            if (!hasEnoughItems(player, input)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasEnoughItems(Player player, com.hbm.inventory.recipes.common.AStack required) {
        Inventory inventory = player.getInventory();
        int countNeeded = required.getStackSize();
        int countFound = 0;

        if (required instanceof com.hbm.inventory.recipes.common.ComparableStack compStack) {
            ItemStack requiredStack = compStack.toStack();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stackInSlot = inventory.getItem(i);
                if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameTags(stackInSlot, requiredStack)) {
                    countFound += stackInSlot.getCount();
                    if (countFound >= countNeeded) {
                        return true;
                    }
                }
            }
        } else if (required instanceof com.hbm.inventory.recipes.common.TagStack tagStack) {

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stackInSlot = inventory.getItem(i);
                if (!stackInSlot.isEmpty() && stackInSlot.is(tagStack.tag)) {
                    countFound += stackInSlot.getCount();
                    if (countFound >= countNeeded) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}