package com.hbm.inventory.fluid.trait;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class FT_Combustible extends FluidTrait {

    protected FuelGrade fuelGrade;
    protected long combustionEnergy;

    public FT_Combustible() { }

    public FT_Combustible(FuelGrade grade, long energy) {
        this.fuelGrade = grade;
        this.combustionEnergy = energy;
    }

    @Override
    public void addInfo(List<Component> info) {
        super.addInfo(info);

        info.add(Component.literal(ChatFormatting.GOLD + "[Combustible]"));

        if(combustionEnergy > 0) {
            info.add(Component.literal(ChatFormatting.GOLD + "Provides " + ChatFormatting.RED + BobMathUtil.getShortNumber(combustionEnergy) + "HE " + ChatFormatting.GOLD + "per bucket"));
            info.add(Component.literal(ChatFormatting.GOLD + "Fuel grade: " + ChatFormatting.RED + this.fuelGrade.getGrade()));
        }
    }

    public long getCombustionEnergy() {
        return this.combustionEnergy;
    }

    public FuelGrade getGrade() {
        return this.fuelGrade;
    }

    public enum FuelGrade {
        LOW("Low"),			//heating and industrial oil				< star engine, iGen
        MEDIUM("Medium"),	//petroil									< diesel generator
        HIGH("High"),		//diesel, gasoline							< HP engine
        AERO("Aviation"),	//kerosene and other light aviation fuels	< turbofan
        GAS("Gaseous");		//fuel gasses like NG, PG and syngas		< gas turbine

        private final String grade;

        FuelGrade(String grade) {
            this.grade = grade;
        }

        public String getGrade() {
            return this.grade;
        }
    }

    @Override
    public void serializeJSON(JsonWriter writer) throws IOException {
        writer.name("energy").value(combustionEnergy);
        writer.name("grade").value(fuelGrade.name());
    }

    @Override
    public void deserializeJSON(JsonObject obj) {
        this.combustionEnergy = obj.get("energy").getAsLong();
        this.fuelGrade = FuelGrade.valueOf(obj.get("grade").getAsString());
    }
}