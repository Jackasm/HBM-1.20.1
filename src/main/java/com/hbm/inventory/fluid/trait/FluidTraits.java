package com.hbm.inventory.fluid.trait;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;

public class FluidTraits {
    public static final FT_Liquid LIQUID = new FT_Liquid();
    public static final FT_Viscous VISCOUS = new FT_Viscous();
    public static final FT_Gaseous_ART EVAP = new FT_Gaseous_ART();
    public static final FT_Gaseous GASEOUS = new FT_Gaseous();
    public static final FT_Plasma PLASMA = new FT_Plasma();
    public static final FT_Amat ANTI = new FT_Amat();
    public static final FT_LeadContainer LEADCON = new FT_LeadContainer();
    public static final FT_NoContainer NOCON = new FT_NoContainer();
    public static final FluidTraitsSimple.FT_NoID NOID = new FluidTraitsSimple.FT_NoID();
    public static final FT_Delicious DELICIOUS = new FT_Delicious();
    public static final FT_Unsiphonable UNSIPHONABLE = new FT_Unsiphonable();

    public static final float SOOT_UNREFINED_OIL = PollutionHandler.SOOT_PER_SECOND * 0.1F;
    /* Original baseline, used for most fuels */
    public static final float SOOT_REFINED_OIL = PollutionHandler.SOOT_PER_SECOND * 0.025F;
    /* Gasses burn very cleanly */
    public static final float SOOT_GAS = PollutionHandler.SOOT_PER_SECOND * 0.005F;
    /* Original baseline for leaded fuels */
    public static final float LEAD_FUEL = PollutionHandler.HEAVY_METAL_PER_SECOND * 0.025F;
    /* Poison stat for most petrochemicals */
    public static final float POISON_OIL = PollutionHandler.POISON_PER_SECOND * 0.0025F;
    /* Poison stat for horrible chemicals like red mud or phosgene */
    public static final float POISON_EXTREME = PollutionHandler.POISON_PER_SECOND * 0.025F;
    /* Poison stat for mostly inert things like carbon dioxide */
    public static final float POISON_MINOR = PollutionHandler.POISON_PER_SECOND * 0.001F;

    public static final FT_Polluting P_OIL =			new FT_Polluting().burn(PollutionType.SOOT, SOOT_UNREFINED_OIL).release(PollutionType.POISON, POISON_OIL);
    public static final FT_Polluting P_FUEL =			new FT_Polluting().burn(PollutionType.SOOT, SOOT_REFINED_OIL).release(PollutionType.POISON, POISON_OIL);
    public static final FT_Polluting P_FUEL_LEADED =	new FT_Polluting().burn(PollutionType.SOOT, SOOT_REFINED_OIL).burn(PollutionType.HEAVYMETAL, LEAD_FUEL).release(PollutionType.POISON, POISON_OIL).release(PollutionType.HEAVYMETAL, LEAD_FUEL * 0.1F);
    public static final FT_Polluting P_GAS =			new FT_Polluting().burn(PollutionType.SOOT, SOOT_GAS);
    public static final FT_Polluting P_LIQUID_GAS =		new FT_Polluting().burn(PollutionType.SOOT, SOOT_GAS * 2F);



}