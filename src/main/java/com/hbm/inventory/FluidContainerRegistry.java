package com.hbm.inventory;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidContainerRegistry {

    public static List<FluidContainer> allContainers = new ArrayList<>();
    private static Map<FluidTypeHBM, List<FluidContainer>> containerMap = new HashMap<>();

    public static void clearRegistry() {
        allContainers.clear();
        containerMap.clear();
    }

    public static void register() {

        registerContainer(new FluidContainer(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET), Fluids.WATER.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(Items.POTION), new ItemStack(Items.GLASS_BOTTLE), Fluids.WATER.get(), 250));
        registerContainer(new FluidContainer(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BUCKET), Fluids.LAVA.get(), 1000));

        registerContainer(new FluidContainer(ItemFluidBucket.createForFluid(Fluids.WATZ.get()), new ItemStack(Items.BUCKET), Fluids.WATZ.get(), 1000));
        registerContainer(new FluidContainer(ItemFluidBucket.createForFluid(Fluids.SCHRABIDIC.get()), new ItemStack(Items.BUCKET), Fluids.SCHRABIDIC.get(), 1000));
        registerContainer(new FluidContainer(ItemFluidBucket.createForFluid(Fluids.SULFURIC_ACID.get()), new ItemStack(Items.BUCKET), Fluids.SULFURIC_ACID.get(), 1000));

        registerContainer(new FluidContainer(new ItemStack(ModBlocks.RED_BARREL.get()), new ItemStack(ModItems.TANK_STEEL.get()), Fluids.DIESEL.get(), 10000));
        registerContainer(new FluidContainer(new ItemStack(ModBlocks.PINK_BARREL.get()), new ItemStack(ModItems.TANK_STEEL.get()), Fluids.KEROSENE.get(), 10000));
        registerContainer(new FluidContainer(new ItemStack(ModBlocks.LOX_BARREL.get()), new ItemStack(ModItems.TANK_STEEL.get()), Fluids.OXYGEN.get(), 10000));

        registerContainer(new FluidContainer(new ItemStack(ModBlocks.ORE_OIL.get()), null, Fluids.OIL.get(), 250));
        registerContainer(new FluidContainer(new ItemStack(ModBlocks.ORE_GNEISS_GAS.get()), null, Fluids.PETROLEUM.get(), GeneralConfig.enable528.get() ? 50 : 250));

        registerContainer(new FluidContainer(new ItemStack(ModItems.CELL_DEUTERIUM.get()), new ItemStack(ModItems.CELL_EMPTY.get()), Fluids.DEUTERIUM.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.CELL_TRITIUM.get()), new ItemStack(ModItems.CELL_EMPTY.get()), Fluids.TRITIUM.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.CELL_UF6.get()), new ItemStack(ModItems.CELL_EMPTY.get()), Fluids.UF6.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.CELL_PUF6.get()), new ItemStack(ModItems.CELL_EMPTY.get()), Fluids.PUF6.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.CELL_ANTIMATTER.get()), new ItemStack(ModItems.CELL_EMPTY.get()), Fluids.AMAT.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.CELL_ANTI_SCHRABIDIUM.get()), new ItemStack(ModItems.CELL_EMPTY.get()), Fluids.ASCHRAB.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.CELL_SAS3.get()), new ItemStack(ModItems.CELL_EMPTY.get()), Fluids.SAS3.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.BOTTLE_MERCURY.get()), new ItemStack(Items.GLASS_BOTTLE), Fluids.MERCURY.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.INGOT_MERCURY.get()), null, Fluids.MERCURY.get(), 125));

        registerContainer(new FluidContainer(new ItemStack(ModItems.ROD_ZIRNOX_TRITIUM.get()), new ItemStack(ModItems.ROD_ZIRNOX_EMPTY.get()), Fluids.TRITIUM.get(), 2000));

        registerContainer(new FluidContainer(new ItemStack(ModItems.PARTICLE_HYDROGEN.get()), new ItemStack(ModItems.PARTICLE_EMPTY.get()), Fluids.HYDROGEN.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.PARTICLE_AMAT.get()), new ItemStack(ModItems.PARTICLE_EMPTY.get()), Fluids.AMAT.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.PARTICLE_ASCHRAB.get()), new ItemStack(ModItems.PARTICLE_EMPTY.get()), Fluids.ASCHRAB.get(), 1000));
        registerContainer(new FluidContainer(new ItemStack(ModItems.CAN_MUG.get()), new ItemStack(ModItems.CAN_EMPTY.get()), Fluids.MUG.get(), 100));
        registerContainer(new FluidContainer(new ItemStack(ModItems.IV_BLOOD.get()), new ItemStack(ModItems.IV_EMPTY.get()), Fluids.BLOOD.get(), 100));
        registerContainer(new FluidContainer(new ItemStack(ModItems.IV_XP.get()), new ItemStack(ModItems.IV_XP_EMPTY.get()), Fluids.XPJUICE.get(), 100));

        registerContainer(new FluidContainer(new ItemStack(Items.EXPERIENCE_BOTTLE), new ItemStack(Items.GLASS_BOTTLE), Fluids.XPJUICE.get(), 100));




        // ====== БАЗОВЫЕ ЖИДКОСТНЫЕ КОНТЕЙНЕРЫ ======
        for (FluidTypeHBM type : Fluids.getAllFluids()) {
            if (type == Fluids.NONE.get()) continue;

            // 1. Канистра (8000 mB) - хранит всё, кроме антиматерии и газов
            if (!type.isAntimatter() && !type.isGaseous()) {
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.FLUID_CANISTER.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.FLUID_CANISTER.get()),
                        type,
                        8000
                ));
            }

            // 2. Газовый баллон (8000 mB) - ТОЛЬКО для газов
            if (type.isGaseous() && !type.isAntimatter() && !type.isCorrosive()) {
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.GAS_TANK.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.GAS_TANK.get()),
                        type,
                        8000
                ));
            }

            // 3. Танк (1000 mB) - хранит всё, кроме антиматерии и жидкостей требующих свинец
            if (!type.isAntimatter() && !type.needsLeadContainer() && !type.isGaseous()) {
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.FLUID_TANK.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.FLUID_TANK.get()),
                        type,
                        1000
                ));
            }

            // 4. Свинцовый танк (1000 mB) - для антиматерии и коррозивных жидкостей
            if (type.isAntimatter() || type.isCorrosive() || type.needsLeadContainer()) {
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.FLUID_TANK_LEAD.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.FLUID_TANK_LEAD.get()),
                        type,
                        1000
                ));
            }

            // 5. Бочка (16000 mB) - хранит всё, кроме антиматерии и газов
            if (!type.isAntimatter() && !type.isGaseous()) {
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.FLUID_BARREL.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.FLUID_BARREL.get()),
                        type,
                        16000
                ));
            }

            // 6. Ведро (1000 mB) - ТОЛЬКО для жидкостей (НЕ газов, НЕ антиматерии, НЕ коррозивных)
            if (!type.isAntimatter() && !type.isGaseous() && !type.isCorrosive() && !type.needsLeadContainer()) {
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.FLUID_BUCKET.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.FLUID_BUCKET.get()),
                        type,
                        1000
                ));
            }

            // 7. Дисперсная канистра (2000 mB) - для dispersable жидкостей
            if (type.isDispersable() && !type.isAntimatter() && !type.isGaseous()) {
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.DISPERSER_CANISTER.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.DISPERSER_CANISTER.get()),
                        type,
                        2000
                ));
                registerContainer(new FluidContainer(
                        ItemFluidContainer.createForFluid(ModItems.GLYPHID_GLAND.get(), type),
                        ItemFluidContainer.createEmpty(ModItems.GLYPHID_GLAND.get()),
                        type,
                        2000
                ));
            }
        }
    }

    public static void registerContainer(FluidContainer con) {
        allContainers.add(con);

        if (!containerMap.containsKey(con.type)) {
            containerMap.put(con.type, new ArrayList<>());
        }

        List<FluidContainer> items = containerMap.get(con.type);
        items.add(con);
    }

    public static List<FluidContainer> getContainers(FluidTypeHBM type) {
        return containerMap.get(type);
    }

    public static FluidContainer getContainer(FluidTypeHBM type, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        ItemStack sta = stack.copy();
        sta.setCount(1);

        if (!containerMap.containsKey(type)) return null;

        for (FluidContainer container : getContainers(type)) {
            if (ItemStack.isSameItemSameTags(container.emptyContainer, sta)) {
                return container;
            }
        }

        return null;
    }

    public static int getFluidContent(ItemStack stack, FluidTypeHBM type) {
        if (stack == null || stack.isEmpty()) return 0;

        ItemStack sta = stack.copy();
        sta.setCount(1);

        if (!containerMap.containsKey(type)) return 0;

        for (FluidContainer container : containerMap.get(type)) {
            if (ItemStack.isSameItemSameTags(container.fullContainer, sta)) {
                return container.content;
            }
        }

        return 0;
    }

    public static FluidTypeHBM getFluidType(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return Fluids.NONE.get();

        ItemStack sta = stack.copy();
        sta.setCount(1);

        for (FluidContainer container : allContainers) {
            if (ItemStack.isSameItemSameTags(container.fullContainer, sta)) {
                return container.type;
            }
        }

        return Fluids.NONE.get();
    }

    public static ItemStack getFullContainer(ItemStack stack, FluidTypeHBM type) {
        if (stack == null || stack.isEmpty()) return null;

        ItemStack sta = stack.copy();
        sta.setCount(1);

        if (!containerMap.containsKey(type)) return null;

        for (FluidContainer container : containerMap.get(type)) {
            if (ItemStack.isSameItemSameTags(container.emptyContainer, sta)) {
                return container.fullContainer.copy();
            }
        }

        return null;
    }

    public static ItemStack getEmptyContainer(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        ItemStack sta = stack.copy();
        sta.setCount(1);

        for (FluidContainer container : allContainers) {
            if (ItemStack.isSameItemSameTags(container.fullContainer, sta)) {
                return container.emptyContainer == null ? null : container.emptyContainer.copy();
            }
        }

        return null;
    }
}