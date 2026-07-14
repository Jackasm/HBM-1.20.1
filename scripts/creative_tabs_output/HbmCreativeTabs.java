package com.hbm.creativetabs;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.ItemFluidBarrel;
import com.hbm.items.fluid.ItemFluidCanister;
import com.hbm.items.fluid.ItemFluidTank;
import com.hbm.items.machine.ItemFluidID;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class HbmCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    // Существующая вкладка для руд и блоков
    public static final RegistryObject<CreativeModeTab> HBM_ORES_TAB = CREATIVE_MODE_TABS.register("hbm_ores_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.ores_and_blocks_tab"))
                    .icon(() -> new ItemStack(ModBlocks.ORE_COBALT.get(), 1))
                    .displayItems((parameters, output) -> {
                        // Блоки руд
                        output.accept(new ItemStack(ModItems.ORE_COPPER.get()));
                        output.accept(new ItemStack(ModItems.ORE_COPPER_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_IRON.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_IRON_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_GOLD.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_GOLD_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_COAL.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_COAL_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_TITANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_TITANIUM_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_SULFUR.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_SULFUR_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ALUMINIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ALUMINIUM_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ZINC.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ZINC_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_FLUORITE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_FLUORITE_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_NITER.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_NITER_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_TUNGSTEN.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_TUNGSTEN_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_LEAD.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_LEAD_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_BERYLLIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_BERYLLIUM_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_LIGNITE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_LIGNITE_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ASBESTOS.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ASBESTOS_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_RARE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_RARE_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_CINNABAR.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_CINNABAR_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_COBALT.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_COBALT_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_URANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_URANIUM_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_THORIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_THORIUM_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_PLUTONIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_PLUTONIUM_DEEPSLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.STONE_RESOURCE_LIMESTONE.get(), 1));

                        output.accept(new ItemStack(ModItems.DECO_TITANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_RED_COPPER.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_TUNGSTEN.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_ALUMINIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_STEEL.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_LEAD.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_BERYLLIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_STAINLESS.get(), 1));
                        output.accept(new ItemStack(ModItems.DECO_RUSTY_STEEL.get(), 1));

                        output.accept(new ItemStack(ModItems.BLOCK_ALUMINIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_TITANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_TUNGSTEN.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_MAGNETIZED_TUNGSTEN.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_LEAD.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_BERYLLIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_COBALT.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_URANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_PLUTONIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.BLOCK_THORIUM.get(), 1));


                    })
                    .build());

    // Новая вкладка для порошков
    public static final RegistryObject<CreativeModeTab> HBM_POWDERS_TAB = CREATIVE_MODE_TABS.register("hbm_powders_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.powders_tab"))
                    .icon(() -> new ItemStack(ModItems.POWDER_URANIUM.get(), 1))
                    .displayItems((parameters, output) -> {
                        // Слитки металлов
                        output.accept(new ItemStack(ModItems.INGOT_ALUMINIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_TITANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_TUNGSTEN.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_LEAD.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_RED_COPPER.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_STEEL.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_STAINLESS.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_BERYLLIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_COBALT.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_ZINC.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_URANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_PLUTONIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_THORIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_DESH.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_SATURNITE.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_FERROURANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_BISMUTH_BRONZE.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_ARSENIC_BRONZE.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_SCHRABIDATE.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_DNT.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_OSMIRIDIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.INGOT_FIREBRICK.get(), 1));
                        // Пластины
                        output.accept(new ItemStack(ModItems.PLATE_ALUMINIUM.get()));
                        output.accept(new ItemStack(ModItems.PLATE_COPPER.get()));
                        output.accept(new ItemStack(ModItems.PLATE_GOLD.get()));
                        output.accept(new ItemStack(ModItems.PLATE_IRON.get()));
                        output.accept(new ItemStack(ModItems.PLATE_TITANIUM.get()));
                        output.accept(new ItemStack(ModItems.PLATE_LEAD.get()));
                        output.accept(new ItemStack(ModItems.PLATE_STEEL.get()));
                        output.accept(new ItemStack(ModItems.PLATE_SATURNITE.get()));
                        output.accept(new ItemStack(ModItems.PLATE_POLYMER.get()));

                        // Обычные порошки
                        output.accept(new ItemStack(ModItems.POWDER_ALUMINIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_ASBESTOS.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_BERYLLIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_COAL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_COBALT.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_COPPER.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_GOLD.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_IRON.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_LEAD.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_LIGNITE.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_THORIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_TITANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_TUNGSTEN.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_MAGNETIZED_TUNGSTEN.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_URANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_ZINC.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_SULFUR.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_FLUORITE.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_CINNABAR.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_NITER.get(), 1));

                        // Маленькие порошки
                        output.accept(new ItemStack(ModItems.POWDER_ALUMINIUM_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_ASBESTOS_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_BERYLLIUM_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_COAL_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_COBALT_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_COPPER_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_GOLD_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_IRON_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_LEAD_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_LIGNITE_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_PLUTONIUM_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_THORIUM_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_TITANIUM_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_TUNGSTEN_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_MAGNETIZED_TUNGSTEN_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_URANIUM_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_ZINC_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_SULFUR_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_FLUORITE_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_CINNABAR_SMALL.get(), 1));
                        output.accept(new ItemStack(ModItems.POWDER_NITER_SMALL.get(), 1));

                        output.accept(new ItemStack(ModItems.ORE_COPPER_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_IRON_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_GOLD_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ALUMINIUM_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_BERYLLIUM_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_COBALT_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_LEAD_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_PLUTONIUM_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_THORIUM_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_TITANIUM_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_TUNGSTEN_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_URANIUM_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.ORE_ZINC_ITEM.get(), 1));

                        output.accept(new ItemStack(ModItems.CINNABAR.get(), 1));
                        output.accept(new ItemStack(ModItems.FLUORITE.get(), 1));
                        output.accept(new ItemStack(ModItems.RARE_EARTH_CHUNK.get(), 1));
                        output.accept(new ItemStack(ModItems.LIGNITE.get(), 1));

                        output.accept(new ItemStack(ModItems.BALL_FIRECLAY.get(), 1));

                    })
                    .build());



    public static final RegistryObject<CreativeModeTab> HBM_MACHINES_TAB = CREATIVE_MODE_TABS.register("hbm_machines_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.machines_tab"))
                    .icon(() -> new ItemStack(ModItems.ANVIL_IRON.get(), 1))
                    .displayItems((parameters, output) -> {

                        output.accept(new ItemStack(ModItems.MACHINE_PRESS_ITEM.get(), 1));
                        output.accept(new ItemStack(ModItems.MACHINE_BLAST_FURNACE.get(), 1));
                        output.accept(new ItemStack(ModItems.BLAST_FURNACE_EXTENSION.get(), 1));

                        output.accept(new ItemStack(ModItems.ANVIL_IRON.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_LEAD.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_STEEL.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_DESH.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_SATURNITE.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_FERROURANIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_BISMUTH_BRONZE.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_ARSENIC_BRONZE.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_SCHRABIDATE.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_DNT.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_OSMIRIDIUM.get(), 1));
                        output.accept(new ItemStack(ModItems.ANVIL_MURKY.get(), 1));

                        output.accept(new ItemStack(ModItems.BARREL_IRON.get(), 1));
                        output.accept(new ItemStack(ModItems.BARREL_STEEL.get(), 1));

                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> HBM_FUELS_AND_ELEMENTS = CREATIVE_MODE_TABS.register("hbm_fuels_and_elements",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.fuels_and_elements"))
                    .icon(() -> new ItemStack(ModItems.STAMP_DESH_PLATE.get(), 1))
                    .displayItems((parameters, output) -> {

                        output.accept(new ItemStack(ModItems.STAMP_357.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_357.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_44.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_44.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_50.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_50.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_9.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_9.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_CIRCUIT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_FLAT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_PLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_DESH_WIRE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_IRON_CIRCUIT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_IRON_FLAT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_IRON_PLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_IRON_WIRE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_OBSIDIAN_CIRCUIT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_OBSIDIAN_FLAT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_OBSIDIAN_PLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_OBSIDIAN_WIRE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STEEL_CIRCUIT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STEEL_FLAT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STEEL_PLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STEEL_WIRE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STONE_CIRCUIT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STONE_FLAT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STONE_PLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_STONE_WIRE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_TITANIUM_CIRCUIT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_TITANIUM_FLAT.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_TITANIUM_PLATE.get(), 1));
                        output.accept(new ItemStack(ModItems.STAMP_TITANIUM_WIRE.get(), 1));



                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> HBM_FLUID_IDENTIFIERS = CREATIVE_MODE_TABS.register("hbm_fluid_identifiers",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm_fluid_identifiers"))
                    .icon(() -> {
                        ItemStack stack = new ItemStack(ModItems.FLUID_IDENTIFIER.get());
                        ItemFluidID.setFluidType(stack, Fluids.WATER.get());
                        return stack;
                    })
                    .displayItems((parameters, output) -> {
                        output.accept(new ItemStack(ModItems.FLUID_PIPE_COPPER.get()));
                        output.accept(new ItemStack(ModItems.FLUID_PIPE_STEEL.get()));
                        output.accept(new ItemStack(ModItems.FLUID_PIPE_PLASTIC.get()));
                        output.accept(new ItemStack(ModItems.FLUID_PIPE_PAINTABLE.get()));

                        for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                                ItemStack stack = ItemFluidTank.createForFluid(fluid);
                                output.accept(stack);
                        }
                        for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                            ItemStack stack = ItemFluidBarrel.createForFluid(fluid);
                            output.accept(stack);
                        }
                        for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                            ItemStack stack = ItemFluidID.createForFluid(fluid);
                            output.accept(stack);
                        }
                        for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
                            ItemStack stack = ItemFluidCanister.createForFluid(fluid);
                            output.accept(stack);
                        }
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> HBM_OTHERS = CREATIVE_MODE_TABS.register("hbm_others",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.others"))
                    .icon(() -> new ItemStack(ModItems.EGG_GLYPHID.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(new ItemStack(ModItems.EGG_GLYPHID.get()));
                        output.accept(new ItemStack(ModItems.GLYPHID_MEAT.get()));
                        output.accept(new ItemStack(ModItems.GLYPHID_MEAT_GRILLED.get()));
                        output.accept(new ItemStack(ModItems.SCRAP.get()));
                        output.accept(new ItemStack(ModItems.FILING_CABINET_ITEM.get()));
                        output.accept(new ItemStack(ModItems.FILING_CABINET_STEEL_ITEM.get()));
                        output.accept(new ItemStack(ModItems.STEEL_BEAM.get()));
                        output.accept(new ItemStack(ModItems.STEEL_GRATE.get()));
                        output.accept(new ItemStack(ModItems.STEEL_GRATE_WIDE.get()));
                        output.accept(new ItemStack(ModItems.STEEL_SCAFFOLD.get()));

                    })
                    .build());




    // Вкладка для block
    public static final RegistryObject<CreativeModeTab> HBM_BLOCK_TAB = CREATIVE_MODE_TABS.register("block_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.block_tab"))
                    .icon(() -> new ItemStack(ModItems.BLOCK_ALUMINIUM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для block
                        // output.accept(new ItemStack(ModItems.key.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.padlock.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.padlock_reinforced.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.padlock_rusty.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.padlock_unbreakable.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для parts
    public static final RegistryObject<CreativeModeTab> HBM_PARTS_TAB = CREATIVE_MODE_TABS.register("parts_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.parts_tab"))
                    .icon(() -> new ItemStack(ModItems.PLATE_STEEL.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для parts
                        // output.accept(new ItemStack(ModItems.mech_key.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.pin.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_kit.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_fake.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для machine
    public static final RegistryObject<CreativeModeTab> HBM_MACHINE_TAB = CREATIVE_MODE_TABS.register("machine_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.machine_tab"))
                    .icon(() -> new ItemStack(ModItems.MACHINE_PRESS_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для machine
                        // output.accept(new ItemStack(ModItems.assembly_template.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.chemistry_template.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.chemistry_icon.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для control
    public static final RegistryObject<CreativeModeTab> HBM_CONTROL_TAB = CREATIVE_MODE_TABS.register("control_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.control_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для control
                        // output.accept(new ItemStack(ModItems.key_red.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_red_cracked.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для weapon
    public static final RegistryObject<CreativeModeTab> HBM_WEAPON_TAB = CREATIVE_MODE_TABS.register("weapon_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.weapon_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для weapon
                        // output.accept(new ItemStack(ModItems.gun_revolver.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.gun_kit_1.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.gun_kit_2.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_357.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_44.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_50.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.ammo_9.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для nuke
    public static final RegistryObject<CreativeModeTab> HBM_NUKE_TAB = CREATIVE_MODE_TABS.register("nuke_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.nuke_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для nuke
                        // output.accept(new ItemStack(ModItems.nuke_mk4.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.nuke_mk5.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.nuke_schrabidium.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.nuke_fleija.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для missile
    public static final RegistryObject<CreativeModeTab> HBM_MISSILE_TAB = CREATIVE_MODE_TABS.register("missile_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.missile_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для missile
                        // output.accept(new ItemStack(ModItems.missile_nuclear.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.missile_thermo.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.missile_doomsday.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для consumable
    public static final RegistryObject<CreativeModeTab> HBM_CONSUMABLE_TAB = CREATIVE_MODE_TABS.register("consumable_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.consumable_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для consumable
                        // output.accept(new ItemStack(ModItems.key.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_kit.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.key_fake.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.pin.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.padlock_rusty.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.padlock.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.padlock_reinforced.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

    // Вкладка для template
    public static final RegistryObject<CreativeModeTab> HBM_TEMPLATE_TAB = CREATIVE_MODE_TABS.register("template_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.template_tab"))
                    .icon(() -> new ItemStack(ModItems.ORE_THORIUM_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        // Предметы для template
                        // output.accept(new ItemStack(ModItems.blueprints.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.blueprint_folder.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.template_folder.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.assembly_template.get())); // TODO: Добавить в 1.20.1
                        // output.accept(new ItemStack(ModItems.chemistry_template.get())); // TODO: Добавить в 1.20.1
                    })
                    .build());

}