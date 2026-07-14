package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.tile.IHeatSource;
import com.hbm.module.ModuleBurnTime;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class TileEntityHeaterOven extends TileEntityFireboxBase implements IConfigurableMachine, MenuProvider {

    public static int baseHeat = 500;
    public static double timeMult = 0.125D;
    public static int maxHeatEnergy = 500_000;
    public static double heatEff = 0.5D;
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

    public TileEntityHeaterOven(BlockPos pos, BlockState state) {
        super(ModTileEntity.HEATER_OVEN.get(), pos, state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.heaterOven");
    }

    @Override
    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            this.tryPullHeat();
        }
        super.tick();
    }

    protected void tryPullHeat() {
        if (level == null) return;

        BlockEntity con = level.getBlockEntity(worldPosition.below());

        if (con instanceof IHeatSource source) {
            int toPull = Math.max(Math.min(source.getHeatStored(), this.getMaxHeat() - this.heatEnergy), 0);
            this.heatEnergy += (int) (toPull * heatEff);
            source.useUpHeat(toPull);
            this.setChanged();
        }
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

    @Override
    public String getConfigName() {
        return "heatingoven";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        baseHeat = IConfigurableMachine.grab(obj, "I:baseHeat", baseHeat);
        timeMult = IConfigurableMachine.grab(obj, "D:burnTimeMult", timeMult);
        heatEff = IConfigurableMachine.grab(obj, "D:heatPullEff", heatEff);
        maxHeatEnergy = IConfigurableMachine.grab(obj, "I:heatCap", maxHeatEnergy);
        if (obj.has("burnModule")) {
            burnModule.readIfPresent(obj.get("M:burnModule").getAsJsonObject());
        }
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:baseHeat").value(baseHeat);
        writer.name("D:burnTimeMult").value(timeMult);
        writer.name("D:heatPullEff").value(heatEff);
        writer.name("I:heatCap").value(maxHeatEnergy);
        writer.name("M:burnModule").beginObject();
        burnModule.writeConfig(writer);
        writer.endObject();
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getGuiTexture() {
        if (texture == null) {
            texture = ResLocation(RefStrings.MODID, "textures/gui/machine/gui_heating_oven.png");
        }
        return texture;
    }
}