package com.hbm.handler.radiation;

import com.hbm.util.RefStrings;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.hbm.util.ResLocation.ResLocation;

public class RadiationCapability {
    public static final Capability<IRadiationData> RADIATION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private static final ResourceLocation KEY = ResLocation(RefStrings.MODID, "radiation");
    private static final String NBT_KEY = "radiation_data_4x4";

    public static IRadiationData createRadiationData() {
        //return new RadiationDataSimple();
        return new RadiationDataPrism();
    }

    @SubscribeEvent
    public static void attachToChunk(AttachCapabilitiesEvent<LevelChunk> event) {
        LevelChunk chunk = event.getObject();
        RadiationChunkProvider provider = new RadiationChunkProvider(chunk);
        event.addCapability(KEY, provider);
    }

    @Nullable
    public static IRadiationData getChunkData(Level level, ChunkPos pos) {
        if (!level.hasChunk(pos.x, pos.z)) return null;
        LevelChunk chunk = level.getChunk(pos.x, pos.z);
        return chunk.getCapability(RADIATION_CAPABILITY).orElse(null);
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        chunk.getCapability(RADIATION_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof RadiationDataPrism prism) {
                prism.calculateResistances(chunk);
            }
        });
    }

    @SubscribeEvent
    public static void onChunkDataLoad(ChunkDataEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        chunk.getCapability(RADIATION_CAPABILITY).ifPresent(cap -> {
            CompoundTag chunkNbt = event.getData();
            if (chunkNbt.contains(NBT_KEY)) {
                cap.deserializeNBT(chunkNbt.getCompound(NBT_KEY));
            }
        });
    }

    @SubscribeEvent
    public static void onChunkDataSave(ChunkDataEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        chunk.getCapability(RADIATION_CAPABILITY).ifPresent(cap ->
                event.getData().put(NBT_KEY, cap.serializeNBT()));
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        chunk.getCapability(RADIATION_CAPABILITY).ifPresent(cap -> {
            if (cap.isDirty()) {
                chunk.setUnsaved(true);
                cap.clearDirty();
            }
        });
    }

    private static class RadiationChunkProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final IRadiationData data;
        private final LazyOptional<IRadiationData> optional;

        public RadiationChunkProvider(LevelChunk chunk) {
            this.data = createRadiationData();
            this.optional = LazyOptional.of(() -> data);
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == RADIATION_CAPABILITY) {
                return optional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.deserializeNBT(nbt);
        }
    }
}