package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.module.ModuleBurnTime;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TileEntityHeaterFirebox extends TileEntityFireboxBase implements IConfigurableMachine{

    public static int baseHeat = 100;
    public static double timeMult = 1D;
    public static int maxHeatEnergy = 100_000;

    public static ModuleBurnTime burnModule = new ModuleBurnTime()
            .setLigniteTimeMod(1.25)
            .setCoalTimeMod(1.25)
            .setCokeTimeMod(1.25)
            .setSolidTimeMod(1.5)
            .setRocketTimeMod(1.5)
            .setBalefireTimeMod(0.5)
            .setLigniteHeatMod(2)
            .setCoalHeatMod(2)
            .setCokeHeatMod(2)
            .setSolidHeatMod(3)
            .setRocketHeatMod(5)
            .setBalefireHeatMod(15);

    public TileEntityHeaterFirebox(BlockPos pos, BlockState state) {
        super(ModTileEntity.HEATER_FIREBOX.get(), pos, state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.heaterFirebox");
    }

    @Override
    public ModuleBurnTime getModule() {
        return burnModule;
    }

    @Override
    public int getBaseHeat() {
        return baseHeat;
    }

    @Override
    public double getTimeMult() {
        return timeMult;
    }

    @Override
    public int getMaxHeat() {
        return maxHeatEnergy;
    }

    @OnlyIn(Dist.CLIENT)
    private ResourceLocation texture;



    // Для обратной совместимости с IConfigurableMachine
    @Override
    public String getConfigName() {
        return "firebox";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        baseHeat = IConfigurableMachine.grab(obj, "I:baseHeat", baseHeat);
        timeMult = IConfigurableMachine.grab(obj, "D:burnTimeMult", timeMult);
        maxHeatEnergy = IConfigurableMachine.grab(obj, "I:heatCap", maxHeatEnergy);
        if (obj.has("burnModule")) {
            burnModule.readIfPresent(obj.get("M:burnModule").getAsJsonObject());
        }
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:baseHeat").value(baseHeat);
        writer.name("D:burnTimeMult").value(timeMult);
        writer.name("I:heatCap").value(maxHeatEnergy);
        writer.name("M:burnModule").beginObject();
        burnModule.writeConfig(writer);
        writer.endObject();
    }
}