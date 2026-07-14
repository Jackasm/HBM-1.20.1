package com.hbm.inventory.fluid.trait;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class FT_PWRModerator extends FluidTrait {

    private double multiplier;
    public FT_PWRModerator(){}
    public FT_PWRModerator(double mulitplier) {
        this.multiplier = mulitplier;
    }

    @Override
    public void addInfo(List<Component> info) {
        info.add(Component.literal(ChatFormatting.BLUE + "[PWR Flux Multiplier]"));
    }

    @Override
    public void addInfoHidden(List<Component> info) {
        int mult = (int) (multiplier * 100 - 100);
        info.add(Component.literal(ChatFormatting.BLUE + "Core flux " + (mult >= 0 ? "+" : "") + mult + "%"));
        info.add(Component.literal("PWR Moderator"));
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public void serializeJSON(JsonWriter writer) throws IOException {
        writer.name("multiplier").value(multiplier);
    }

    @Override
    public void deserializeJSON(JsonObject obj) {
        this.multiplier = obj.get("multiplier").getAsDouble();
    }
}