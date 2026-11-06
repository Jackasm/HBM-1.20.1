package com.hbm.tileentity.machine;

import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.container.ContainerFunnel;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TileEntityMachineFunnel extends TileEntityMachineBase implements MenuProvider, IControlReceiver {

    public int mode = 0;
    public static final int MODE_ALL = 0;
    public static final int MODE_3x3 = 1;
    public static final int MODE_2x2 = 2;

    // Кеш для рецептов
    public static final Map<ComparableStack, ItemStack> FROM_4_CACHE = new HashMap<>();
    public static final Map<ComparableStack, ItemStack> FROM_9_CACHE = new HashMap<>();

    private final ItemStackHandler inventory = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot >= 9) return false;
            return TileEntityMachineFunnel.this.isItemValidForSlot(slot, stack);
        }
    };
    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineFunnel(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_FUNNEL.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        for (int i = 0; i < 9; i++) {
            ItemStack input = inventory.getStackInSlot(i);
            if (input == null || input.isEmpty()) continue;

            int stackSize = 9;
            ItemStack compressed = (mode == MODE_2x2 || input.getCount() < 9) ? null : getFrom9(input);

            if (compressed == null) {
                compressed = (mode == MODE_3x3 || input.getCount() < 4) ? null : getFrom4(input);
                stackSize = 4;
            }

            if (compressed != null && input.getCount() >= stackSize) {
                int outputSlot = i + 9;
                ItemStack output = inventory.getStackInSlot(outputSlot);

                if (output == null || output.isEmpty()) {
                    inventory.setStackInSlot(outputSlot, compressed.copy());
                    input.shrink(stackSize);
                    setChanged();
                } else if (output.getItem() == compressed.getItem() &&
                        output.getDamageValue() == compressed.getDamageValue() &&
                        output.getCount() + compressed.getCount() <= output.getMaxStackSize()) {
                    output.grow(compressed.getCount());
                    input.shrink(stackSize);
                    setChanged();
                }
            }
        }
        this.networkPackNT(15);
    }

    private ItemStack getFrom4(ItemStack ingredient) {
        ComparableStack key = new ComparableStack(ingredient).makeSingular();
        if (FROM_4_CACHE.containsKey(key)) {
            return FROM_4_CACHE.get(key);
        }

        TransientCraftingContainer inv = new TransientCraftingContainer(new DummyContainer(), 2, 2);

        for (int i = 0; i < 4; i++) {
            inv.setItem(i, ingredient.copy());
        }

        ItemStack result = getMatch(Objects.requireNonNull(level), inv);
        FROM_4_CACHE.put(key, result != null ? result.copy() : null);
        return result;
    }

    private ItemStack getFrom9(ItemStack ingredient) {
        ComparableStack key = new ComparableStack(ingredient).makeSingular();
        if (FROM_9_CACHE.containsKey(key)) {
            return FROM_9_CACHE.get(key);
        }

        TransientCraftingContainer inv = new TransientCraftingContainer(new DummyContainer(), 3, 3);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, ingredient.copy());
        }

        ItemStack result = getMatch(Objects.requireNonNull(level), inv);
        FROM_9_CACHE.put(key, result != null ? result.copy() : null);
        return result;
    }

    public void serialize(ByteBuf buf) {
        buf.writeInt(mode);
    }

    public void deserialize(ByteBuf buf) {
        this.mode = buf.readInt();
    }

    @Nullable
    private ItemStack getMatch(Level level, CraftingContainer inv) {
        for (CraftingRecipe recipe : level.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, inv, level)) {
            if (recipe.matches(inv, level)) {
                return recipe.assemble(inv, level.registryAccess());
            }
        }
        return null;
    }

    public int getRedstoneSignal() {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) count++;
        }
        return count;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        this.mode = tag.getInt("mode");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("mode", mode);
    }

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot >= 9) return false;
        if (!inventory.getStackInSlot(slot).isEmpty()) {
            // Если слот уже занят, проверяем совместимость
            return true;
        }
        return getFrom9(stack) != null || getFrom4(stack) != null;
    }

    @Override
    public boolean hasPermission(Player player) {
        return true;
    }

    @Override
    public void receiveControl(CompoundTag data) {
        if (data.contains("toggle")) {
            mode++;
            if (mode > 2) mode = 0;
            setChanged();
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.machines.machine_funnel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ContainerFunnel(id, inventory, this);
    }

    private static class DummyContainer extends AbstractContainerMenu {
        public DummyContainer() {
            super(null, -1);
        }

        @Override
        public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(@NotNull Player player) {
            return true;
        }
    }

    // ==================== Capabilities ====================
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
    }

    // ==================== IItemHandler методы для совместимости ====================
    public IItemHandler getItemHandler() {
        return inventory;
    }

    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }
}