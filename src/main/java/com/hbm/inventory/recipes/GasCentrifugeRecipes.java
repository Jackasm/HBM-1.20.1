package com.hbm.inventory.recipes;

import java.util.*;

import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;

import com.hbm.items.machine.ItemFluidIcon;
import net.minecraft.world.item.ItemStack;


public class GasCentrifugeRecipes {

    public static class PseudoFluidType {

        public static HashMap<String, PseudoFluidType> types = new HashMap<>();

        public static PseudoFluidType NONE = new PseudoFluidType("NONE", 0, 0, null, false, (ItemStack[]) null);

        public static PseudoFluidType HEUF6 = new PseudoFluidType("HEUF6", 300, 0, NONE, true,
                new ItemStack(ModItems.NUGGET_U238.get(), 2),
                new ItemStack(ModItems.NUGGET_U235.get(), 1),
                new ItemStack(ModItems.FLUORITE.get(), 1));

        public static PseudoFluidType MEUF6 = new PseudoFluidType("MEUF6", 200, 100, HEUF6, false,
                new ItemStack(ModItems.NUGGET_U238.get(), 1));

        public static PseudoFluidType LEUF6 = new PseudoFluidType("LEUF6", 300, 200, MEUF6, false,
                new ItemStack(ModItems.NUGGET_U238.get(), 1),
                new ItemStack(ModItems.FLUORITE.get(), 1));

        public static PseudoFluidType NUF6 = new PseudoFluidType("NUF6", 400, 300, LEUF6, false,
                new ItemStack(ModItems.NUGGET_U238.get(), 1));

        public static PseudoFluidType PF6 = new PseudoFluidType("PF6", 300, 0, NONE, false,
                new ItemStack(ModItems.NUGGET_PU238.get(), 1),
                new ItemStack(ModItems.NUGGET_PU_MIX.get(), 2),
                new ItemStack(ModItems.FLUORITE.get(), 1));

        public static PseudoFluidType MUD_HEAVY = new PseudoFluidType("MUD_HEAVY", 500, 0, NONE, false,
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(ModItems.DUST.get(), 1),
                new ItemStack(ModItems.NUCLEAR_WASTE_TINY.get(), 1));

        public static PseudoFluidType MUD = new PseudoFluidType("MUD", 1000, 500, MUD_HEAVY, false,
                new ItemStack(ModItems.POWDER_LEAD.get(), 1),
                new ItemStack(ModItems.DUST.get(), 1));

        public final String name;
        private final int fluidConsumed;
        private final int fluidProduced;
        private final PseudoFluidType outputFluid;
        private final boolean isHighSpeed;
        private final ItemStack[] output;

        PseudoFluidType(String name, int fluidConsumed, int fluidProduced, PseudoFluidType outputFluid, boolean isHighSpeed, ItemStack... output) {
            this.name = name;
            this.fluidConsumed = fluidConsumed;
            this.fluidProduced = fluidProduced;
            this.outputFluid = outputFluid;
            this.isHighSpeed = isHighSpeed;
            this.output = output != null ? output : new ItemStack[0];
            types.put(name, this);
        }

        public int getFluidConsumed() { return this.fluidConsumed; }
        public int getFluidProduced() { return this.fluidProduced; }
        public PseudoFluidType getOutputType() { return this.outputFluid; }
        public ItemStack[] getOutput() { return this.output; }
        public boolean getIfHighSpeed() { return this.isHighSpeed; }
        public String getName() { return "hbmpseudofluid." + this.name.toLowerCase(Locale.US); }
    }

    private static final Map<FluidStackHBM, Object[]> gasCent = new HashMap<>();


    public static List<GasCentrifugeRecipeWrapper> getAllRecipes() {
        List<GasCentrifugeRecipeWrapper> list = new ArrayList<>();
        for (Map.Entry<FluidStackHBM, Object[]> entry : gasCent.entrySet()) {
            list.add(new GasCentrifugeRecipeWrapper(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    public static Map<Object, Object[]> getGasCentrifugeRecipes() {
        Map<Object, Object[]> recipes = new HashMap<>();

        for (Map.Entry<FluidStackHBM, Object[]> entry : gasCent.entrySet()) {
            FluidStackHBM input = entry.getKey();
            ItemStack[] out = new ItemStack[4];
            ItemStack[] outputs = (ItemStack[]) entry.getValue()[0];

            for (int j = 0; j < outputs.length; j++) {
                out[j] = outputs[j].copy();
            }
            for (int j = 0; j < 4; j++) {
                if (out[j] == null) {
                    out[j] = new ItemStack(ModItems.NOTHING.get());
                }
            }

            recipes.put(ItemFluidIcon.make(input.type, input.fill), new Object[]{out, entry.getValue()[1], entry.getValue()[2]});
        }

        return recipes;
    }

    public static HashMap<FluidTypeHBM, PseudoFluidType> fluidConversions = new HashMap<>();

    public static void register() {
        fluidConversions.put(Fluids.UF6.get(), PseudoFluidType.NUF6);
        fluidConversions.put(Fluids.PUF6.get(), PseudoFluidType.PF6);
        fluidConversions.put(Fluids.WATZ.get(), PseudoFluidType.MUD);

        gasCent.put(new FluidStackHBM(1200, Fluids.UF6.get()), new Object[] {
                new ItemStack[] {
                        new ItemStack(ModItems.NUGGET_U238.get(), 11),
                        new ItemStack(ModItems.NUGGET_U235.get(), 1),
                        new ItemStack(ModItems.FLUORITE.get(), 4)
                }, true, 4 });

        gasCent.put(new FluidStackHBM(1200, Fluids.UF6.get()), new Object[] {
                new ItemStack[] {
                        new ItemStack(ModItems.NUGGET_U238.get(), 6),
                        new ItemStack(ModItems.NUGGET_URANIUM_FUEL.get(), 6),
                        new ItemStack(ModItems.FLUORITE.get(), 4)
                }, false, 2 });

        gasCent.put(new FluidStackHBM(900, Fluids.PUF6.get()), new Object[] {
                new ItemStack[] {
                        new ItemStack(ModItems.NUGGET_PU238.get(), 3),
                        new ItemStack(ModItems.NUGGET_PU_MIX.get(), 6),
                        new ItemStack(ModItems.FLUORITE.get(), 3)
                }, false, 1 });

        gasCent.put(new FluidStackHBM(1000, Fluids.WATZ.get()), new Object[] {
                new ItemStack[] {
                        new ItemStack(ModItems.POWDER_IRON.get(), 1),
                        new ItemStack(ModItems.POWDER_LEAD.get(), 1),
                        new ItemStack(ModItems.NUCLEAR_WASTE_TINY.get(), 1),
                        new ItemStack(ModItems.DUST.get(), 2)
                }, false, 2 });
    }
}