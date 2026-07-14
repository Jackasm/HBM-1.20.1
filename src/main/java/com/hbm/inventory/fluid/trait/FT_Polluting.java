package com.hbm.inventory.fluid.trait;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.fluid.FluidTypeHBM;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FT_Polluting extends FluidTrait {

    public HashMap<PollutionType, Float> releaseMap = new HashMap();
    public HashMap<PollutionType, Float> burnMap = new HashMap();

    public FT_Polluting release(PollutionType type, float amount) {
        releaseMap.put(type, amount);
        return this;
    }

    public FT_Polluting burn(PollutionType type, float amount) {
        burnMap.put(type, amount);
        return this;
    }

    @Override
    public void addInfo(List<Component> info) {
        info.add(Component.literal(ChatFormatting.GOLD + "[Polluting]"));
    }

    @Override
    public void addInfoHidden(List<Component> info) {

        if(!this.releaseMap.isEmpty()) {
            info.add(Component.literal(ChatFormatting.GREEN + "When spilled:"));
            for(Map.Entry<PollutionType, Float> entry : releaseMap.entrySet()) info.add(Component.literal(ChatFormatting.GREEN + " - " + entry.getValue() + " " + entry.getKey() + " per mB"));
        }

        if(!this.burnMap.isEmpty()) {
            info.add(Component.literal(ChatFormatting.RED + "When burned:"));
            for(Map.Entry<PollutionType, Float> entry : burnMap.entrySet()) info.add(Component.literal(ChatFormatting.RED + " - " + entry.getValue() + " " + entry.getKey() + " per mB"));
        }
    }

    @Override
    public void onFluidRelease(Level level, BlockPos pos, FluidTank tank, int overflowAmount, FluidReleaseType type) {
        if(type == FluidReleaseType.SPILL) for(Map.Entry<PollutionType, Float> entry : releaseMap.entrySet()) PollutionHandler.incrementPollution(level, pos, entry.getKey(), entry.getValue());
        if(type == FluidReleaseType.BURN) for(Map.Entry<PollutionType, Float> entry : burnMap.entrySet()) PollutionHandler.incrementPollution(level, pos, entry.getKey(), entry.getValue());
    }

    @Override
    public void serializeJSON(JsonWriter writer) throws IOException {
        writer.name("release").beginObject();
        for(Map.Entry<PollutionType, Float> entry : releaseMap.entrySet()) {
            writer.name(entry.getKey().name()).value(entry.getValue());
        }
        writer.endObject();
        writer.name("burn").beginObject();
        for(Map.Entry<PollutionType, Float> entry : burnMap.entrySet()) {
            writer.name(entry.getKey().name()).value(entry.getValue());
        }
        writer.endObject();
    }

    @Override
    public void deserializeJSON(JsonObject obj) {
        if(obj.has("release")) {
            JsonObject release = obj.get("release").getAsJsonObject();
            for(PollutionType type : PollutionType.values()) {
                if(release.has(type.name())) {
                    releaseMap.put(type, release.get(type.name()).getAsFloat());
                }
            }
        }
        if(obj.has("burn")) {
            JsonObject release = obj.get("burn").getAsJsonObject();
            for(PollutionType type : PollutionType.values()) {
                if(release.has(type.name())) {
                    burnMap.put(type, release.get(type.name()).getAsFloat());
                }
            }
        }
    }

    public static void pollute(Level level, BlockPos pos, FluidTypeHBM type, FluidReleaseType release, float mB) {
        FT_Polluting trait = type.getTrait(FT_Polluting.class);
        if(trait == null) return;
        if(release == FluidReleaseType.VOID) return;

        HashMap<PollutionType, Float> map = release == FluidReleaseType.BURN ? trait.burnMap : trait.releaseMap;

        for(Map.Entry<PollutionType, Float> entry : map.entrySet()) {
            PollutionHandler.incrementPollution(level, pos, entry.getKey(), entry.getValue() * mB);
        }
    }
}