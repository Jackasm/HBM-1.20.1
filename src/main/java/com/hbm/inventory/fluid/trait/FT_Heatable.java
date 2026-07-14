package com.hbm.inventory.fluid.trait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.network.chat.Component;

public class FT_Heatable extends FluidTrait {

    protected HashMap<HeatingType, Double> efficiency = new HashMap();
    protected List<HeatingStep> steps = new ArrayList();

    public enum HeatingType {
        BOILER("Boilable"),
        HEATEXCHANGER("Heatable"),
        PWR("PWR Coolant"),
        ICF("ICF Coolant"),
        PA("Particle Accelerator Coolant");

        public String name;

        HeatingType(String name) {
            this.name = name;
        }
    }

    public static class HeatingStep {
        public final int amountReq;
        public final int heatReq;
        public final FluidTypeHBM typeProduced;
        public final int amountProduced;

        public HeatingStep(int req, int heat, FluidTypeHBM type, int prod) {
            this.amountReq = req;
            this.heatReq = heat;
            this.typeProduced = type;
            this.amountProduced = prod;
        }
    }

    @Override
    public void addInfoHidden(List<Component> info) {
        info.add(Component.literal("Heatable"));
    }

    public double getEfficiency(HeatingType type) {
        Double eff = this.efficiency.get(type);
        return eff != null ? eff : 0.0D;
    }

    public FT_Heatable setEfficiency(HeatingType type, double eff) {
        efficiency.put(type, eff);
        return this;
    }

    public HeatingStep getFirstStep() {
        return this.steps.get(0);

    }

    public FT_Heatable addStep(int heat, int req, FluidTypeHBM type, int prod) {
        steps.add(new HeatingStep(req, heat, type, prod));
        return this;
    }

}