package com.hbm.inventory.recipes;

import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.Tuple.Pair;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class FractionRecipes {

    public static final Map<FluidTypeHBM, Pair<FluidStackHBM, FluidStackHBM>> fractions = new HashMap<>();

    /** Вызывается один раз при инициализации (CommonSetup / FMLCommonSetupEvent). */
    public static void register() {
        fractions.put(Fluids.HEAVYOIL.get(),          new Pair<>(new FluidStackHBM(Fluids.BITUMEN.get(),              30), new FluidStackHBM(Fluids.SMEAR.get(),              70)));
        fractions.put(Fluids.HEAVYOIL_VACUUM.get(),   new Pair<>(new FluidStackHBM(Fluids.SMEAR.get(),                40), new FluidStackHBM(Fluids.HEATINGOIL_VACUUM.get(),  60)));
        fractions.put(Fluids.SMEAR.get(),             new Pair<>(new FluidStackHBM(Fluids.HEATINGOIL.get(),            60), new FluidStackHBM(Fluids.LUBRICANT.get(),          40)));
        fractions.put(Fluids.NAPHTHA.get(),           new Pair<>(new FluidStackHBM(Fluids.HEATINGOIL.get(),            40), new FluidStackHBM(Fluids.DIESEL.get(),             60)));
        fractions.put(Fluids.NAPHTHA_DS.get(),        new Pair<>(new FluidStackHBM(Fluids.XYLENE.get(),                60), new FluidStackHBM(Fluids.DIESEL_REFORM.get(),      40)));
        fractions.put(Fluids.NAPHTHA_CRACK.get(),     new Pair<>(new FluidStackHBM(Fluids.HEATINGOIL.get(),            30), new FluidStackHBM(Fluids.DIESEL_CRACK.get(),       70)));
        fractions.put(Fluids.LIGHTOIL.get(),          new Pair<>(new FluidStackHBM(Fluids.DIESEL.get(),                40), new FluidStackHBM(Fluids.KEROSENE.get(),           60)));
        fractions.put(Fluids.LIGHTOIL_DS.get(),       new Pair<>(new FluidStackHBM(Fluids.DIESEL_REFORM.get(),         60), new FluidStackHBM(Fluids.KEROSENE_REFORM.get(),    40)));
        fractions.put(Fluids.LIGHTOIL_CRACK.get(),    new Pair<>(new FluidStackHBM(Fluids.KEROSENE.get(),              70), new FluidStackHBM(Fluids.PETROLEUM.get(),          30)));
        fractions.put(Fluids.COALOIL.get(),           new Pair<>(new FluidStackHBM(Fluids.COALGAS.get(),               30), new FluidStackHBM(Fluids.OIL.get(),                70)));
        fractions.put(Fluids.COALCREOSOTE.get(),      new Pair<>(new FluidStackHBM(Fluids.COALOIL.get(),               10), new FluidStackHBM(Fluids.BITUMEN.get(),            90)));
        fractions.put(Fluids.REFORMATE.get(),         new Pair<>(new FluidStackHBM(Fluids.AROMATICS.get(),             40), new FluidStackHBM(Fluids.XYLENE.get(),             60)));
        fractions.put(Fluids.LIGHTOIL_VACUUM.get(),   new Pair<>(new FluidStackHBM(Fluids.KEROSENE.get(),              70), new FluidStackHBM(Fluids.REFORMGAS.get(),          30)));
        fractions.put(Fluids.EGG.get(),               new Pair<>(new FluidStackHBM(Fluids.CHOLESTEROL.get(),           50), new FluidStackHBM(Fluids.RADIOSOLVENT.get(),       50)));
        fractions.put(Fluids.OIL_COKER.get(),         new Pair<>(new FluidStackHBM(Fluids.CRACKOIL.get(),              30), new FluidStackHBM(Fluids.HEATINGOIL.get(),         70)));
        fractions.put(Fluids.NAPHTHA_COKER.get(),     new Pair<>(new FluidStackHBM(Fluids.NAPHTHA_CRACK.get(),         75), new FluidStackHBM(Fluids.LIGHTOIL_CRACK.get(),     25)));
        fractions.put(Fluids.GAS_COKER.get(),         new Pair<>(new FluidStackHBM(Fluids.AROMATICS.get(),             25), new FluidStackHBM(Fluids.CARBONDIOXIDE.get(),      75)));
        fractions.put(Fluids.CHLOROCALCITE_MIX.get(), new Pair<>(new FluidStackHBM(Fluids.CHLOROCALCITE_CLEANED.get(),50), new FluidStackHBM(Fluids.COLLOID.get(),            50)));
        fractions.put(Fluids.BAUXITE_SOLUTION.get(),  new Pair<>(new FluidStackHBM(Fluids.REDMUD.get(),                50), new FluidStackHBM(Fluids.SODIUM_ALUMINATE.get(),   50)));
    }

    public static Pair<FluidStackHBM, FluidStackHBM> getFractions(FluidTypeHBM oil) {
        return fractions.get(oil);
    }

    /** Для JEI/EMI — карта «вход → [выход1, выход2]». */
    public static Map<Object, Object> getFractionRecipesForJEI() {
        Map<Object, Object> recipes = new HashMap<>();
        for (Map.Entry<FluidTypeHBM, Pair<FluidStackHBM, FluidStackHBM>> entry : fractions.entrySet()) {
            ItemStack[] out = new ItemStack[] {
                    ItemFluidIcon.make(entry.getValue().key()),
                    ItemFluidIcon.make(entry.getValue().value())
            };
            recipes.put(ItemFluidIcon.make(entry.getKey(), 100), out);
        }
        return recipes;
    }
}