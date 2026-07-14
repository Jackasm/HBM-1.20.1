package com.hbm.inventory.fluid.trait;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.fluid.FluidTypeHBM;

import com.hbm.inventory.fluid.Fluids;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FT_Coolable extends FluidTrait {

    protected HashMap<CoolingType, Double> efficiency = new HashMap<>();

    public FluidTypeHBM coolsTo;
    public int amountReq;
    public int amountProduced;
    public int heatEnergy;

    public FT_Coolable() { }

    public FT_Coolable(FluidTypeHBM type, int req, int prod, int heat) {
        this.coolsTo = type;
        this.amountReq = req;
        this.amountProduced = prod;
        this.heatEnergy = heat;
    }

    public enum CoolingType {
        TURBINE("Turbine Steam"),
        HEATEXCHANGER("Coolable");

        public final String name;

        CoolingType(String name) {
            this.name = name;
        }
    }

    public FT_Coolable setEfficiency(CoolingType type, double eff) {
        efficiency.put(type, eff);
        return this;
    }

    public double getEfficiency(CoolingType type) {
        Double eff = this.efficiency.get(type);
        return eff != null ? eff : 0.0D;
    }

    @Override
    public void addInfoHidden(List<Component> info) {
        info.add(Component.literal("Thermal capacity: " + heatEnergy + " TU per " + amountReq + "mB")
                .withStyle(ChatFormatting.RED));

        for (CoolingType type : CoolingType.values()) {
            double eff = getEfficiency(type);

            if (eff > 0) {
                MutableComponent text = Component.literal("[" + type.name + "] ")
                        .withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal("Efficiency: " + ((int) (eff * 100D)) + "%")
                                .withStyle(ChatFormatting.AQUA));
                info.add(text);
            }
        }
    }

    @Override
    public void serializeJSON(JsonWriter writer) throws IOException {
        writer.name("coolsTo").value(this.coolsTo.getName());
        writer.name("amountReq").value(this.amountReq);
        writer.name("amountProd").value(this.amountProduced);
        writer.name("heatEnergy").value(this.heatEnergy);

        for (Map.Entry<CoolingType, Double> entry : this.efficiency.entrySet()) {
            writer.name(entry.getKey().name).value(entry.getValue());
        }
    }

    @Override
    public void deserializeJSON(JsonObject obj) {
        this.coolsTo = Fluids.fromName(obj.get("coolsTo").getAsString());
        this.amountReq = obj.get("amountReq").getAsInt();
        this.amountProduced = obj.get("amountProd").getAsInt();
        this.heatEnergy = obj.get("heatEnergy").getAsInt();

        for (CoolingType type : CoolingType.values()) {
            if (obj.has(type.name)) {
                efficiency.put(type, obj.get(type.name).getAsDouble());
            }
        }
    }
}