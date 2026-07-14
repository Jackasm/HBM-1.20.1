package com.hbm.inventory.recipes;

import com.hbm.inventory.FluidStackHBM;
import com.hbm.items.machine.ItemFluidIcon;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Обёртка для рецепта газовой центрифуги
 */
public class GasCentrifugeRecipeWrapper {
    private final FluidStackHBM inputFluid;
    private final ItemStack[] outputs;
    private final boolean isHighSpeed;
    private final int speed;

    public GasCentrifugeRecipeWrapper(FluidStackHBM inputFluid, Object[] data) {
        this.inputFluid = inputFluid;
        this.outputs = (ItemStack[]) data[0];
        this.isHighSpeed = (boolean) data[1];
        this.speed = (int) data[2];
    }

    public FluidStackHBM getInputFluid() {
        return inputFluid;
    }

    public List<ItemStack> getOutputs() {
        return Arrays.asList(outputs);
    }

    public boolean isHighSpeed() {
        return isHighSpeed;
    }

    public int getSpeed() {
        return speed;
    }

    /**
     * Получить отображаемый вход (иконка жидкости)
     */
    public ItemStack getDisplayInput() {
        return ItemFluidIcon.make(inputFluid.type, inputFluid.fill);
    }

    /**
     * Получить отображаемые выходы (все предметы)
     */
    public List<ItemStack> getDisplayOutputs() {
        return Arrays.asList(outputs);
    }
}