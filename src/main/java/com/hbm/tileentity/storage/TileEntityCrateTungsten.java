package com.hbm.tileentity.storage;

import com.hbm.api.block.ILaserable;
import com.hbm.blocks.generic.BlockStorageCrateTungsten;
import com.hbm.inventory.container.ContainerCrateTungsten;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TileEntityCrateTungsten extends TileEntityCrateBase implements ILaserable {

    private int heatTimer = 0;

    public TileEntityCrateTungsten(BlockPos pos, BlockState state) {
        super(ModTileEntity.CRATE_TUNGSTEN.get(), pos, state, 27);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (heatTimer > 0) heatTimer--;

            BlockState state = getBlockState();
            boolean isHeated = state.getValue(BlockStorageCrateTungsten.HEATED);

            if (!isHeated && heatTimer > 0) {
                level.setBlock(worldPosition, state.setValue(BlockStorageCrateTungsten.HEATED, true), 3);
            }
            if (isHeated && heatTimer <= 0) {
                level.setBlock(worldPosition, state.setValue(BlockStorageCrateTungsten.HEATED, false), 3);
            }

            if (heatTimer > 0) {
                spawnParticles();
            }
        }
    }

    private void spawnParticles() {
        if (level == null) return;

        for (int i = 0; i < 2; i++) {
            level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                    worldPosition.getX() + level.random.nextDouble(),
                    worldPosition.getY() + 1.1,
                    worldPosition.getZ() + level.random.nextDouble(),
                    0.0, 0.0, 0.0);
            level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                    worldPosition.getX() + level.random.nextDouble(),
                    worldPosition.getY() + 1.1,
                    worldPosition.getZ() + level.random.nextDouble(),
                    0.0, 0.0, 0.0);
        }
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        if (isLocked()) return false;
        if (stack.getItem() == ModItems.BILLET_POLONIUM.get()) return false;
        if (stack.getItem() == ModToolItems.CRUCIBLE.get() && stack.getDamageValue() > 0) return false;

        // Проверяем, можно ли переплавить предмет
        var recipe = level != null ? level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level) : Optional.empty();
        return recipe.isEmpty();
    }

    @Override
    public void addEnergy(Level level, BlockPos pos, long energy, Direction dir) {
        if (this.level == null) return;

        heatTimer = 5;

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = slots[i];
            if (stack.isEmpty()) continue;

            ItemStack result = ItemStack.EMPTY;
            var recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), this.level);
            if (recipe.isPresent()) {
                result = recipe.get().getResultItem(this.level.registryAccess());
            }

            // Специальные рецепты для полония и тигля
            if (stack.getItem() == ModItems.BILLET_POLONIUM.get() && energy > 10000000) {
                result = new ItemStack(ModItems.BILLET_YHARONITE.get());
            }

            if (stack.getItem() == ModToolItems.CRUCIBLE.get() && stack.getDamageValue() > 0 && energy > 10000000) {
                // Устанавливаем повреждение через setDamageValue
                ItemStack crucibleStack = new ItemStack(ModToolItems.CRUCIBLE.get(), 1);
                crucibleStack.setDamageValue(0);
                result = crucibleStack;
            }

            if (!result.isEmpty()) {
                int size = stack.getCount();
                if (result.getCount() * size <= result.getMaxStackSize()) {
                    ItemStack newResult = result.copy();
                    newResult.setCount(result.getCount() * size);
                    slots[i] = newResult;
                }
            }
        }
        setChanged();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crateTungsten");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerCrateTungsten(id, inv, this);
    }
}