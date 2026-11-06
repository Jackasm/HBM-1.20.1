package com.hbm.util;

import com.hbm.config.VersatileConfig;
import com.hbm.interfaces.ICustomWarhead.SaltedFuel.HalfLifeType;
import com.hbm.items.machine.ItemRTGPellet;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class RTGUtil {

    public static short getPower(ItemRTGPellet fuel, ItemStack stack) {
        return VersatileConfig.scaleRTGPower() ? ItemRTGPellet.getScaledPower(fuel, stack) : fuel.getHeat();
    }

    // --- Работа с ItemStack[] (если где-то ещё используется) ---

    public static boolean hasHeat(ItemStack[] inventory, int[] rtgSlots) {
        for (int slot : rtgSlots) {
            ItemStack stack = inventory[slot];
            if (stack == null || stack.isEmpty()) continue;
            if (stack.getItem() instanceof ItemRTGPellet) return true;
        }
        return false;
    }

    public static int updateRTGs(ItemStack[] inventory, int[] rtgSlots) {
        int newHeat = 0;
        for (int slot : rtgSlots) {
            ItemStack stack = inventory[slot];
            if (stack == null || stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof ItemRTGPellet pellet)) continue;

            newHeat += getPower(pellet, stack);
            inventory[slot] = ItemRTGPellet.handleDecay(stack, pellet);
        }
        return newHeat;
    }

    // --- Работа с ItemStackHandler (для TileEntityMachineRTG) ---

    public static boolean hasHeat(ItemStackHandler inventory, int[] rtgSlots) {
        for (int slot : rtgSlots) {
            if (slot < 0 || slot >= inventory.getSlots()) continue;
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemRTGPellet) return true;
        }
        return false;
    }

    public static int updateRTGs(ItemStackHandler inventory, int[] rtgSlots) {
        int newHeat = 0;
        for (int slot : rtgSlots) {
            if (slot < 0 || slot >= inventory.getSlots()) continue;
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof ItemRTGPellet pellet)) continue;

            newHeat += getPower(pellet, stack);
            ItemStack result = ItemRTGPellet.handleDecay(stack, pellet);
            inventory.setStackInSlot(slot, result);
        }
        return newHeat;
    }

    /**
     * Gets the lifespan of an RTG based on half-life
     * @author UFFR
     * @param halfLife The half-life
     * @param type Half-life units: {@link HalfLifeType}
     * @param realYears Whether or not to use 365 days per year instead of 100 to calculate time
     * @return The half-life calculated into Minecraft ticks
     */
    public static long getLifespan(float halfLife, HalfLifeType type, boolean realYears) {
        float life = 0;
        switch (type) {
            case LONG -> life = (48000 * (realYears ? 365 : 100) * 100) * halfLife;
            case MEDIUM -> life = (48000 * (realYears ? 365 : 100)) * halfLife;
            case SHORT -> life = 48000 * halfLife;
        }
        return (long) life;
    }
}