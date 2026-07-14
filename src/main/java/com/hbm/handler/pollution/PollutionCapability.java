package com.hbm.handler.pollution;

import com.hbm.util.RefStrings;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.hbm.util.ResLocation.ResLocation;

public class PollutionCapability {
    public static final Capability<IPollutionData> POLLUTION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public static void attachToChunk(AttachCapabilitiesEvent<LevelChunk> event) {
        LevelChunk chunk = event.getObject();
        PollutionChunkProvider provider = new PollutionChunkProvider(chunk);
        event.addCapability(ResLocation(RefStrings.MODID, "pollution"), provider);
    }

    @Nullable
    public static IPollutionData getChunkData(Level level, ChunkPos pos) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(pos.x, pos.z);
        if (chunk == null) {
            return null;
        }
        return chunk.getCapability(POLLUTION_CAPABILITY).orElse(null);
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        chunk.getCapability(POLLUTION_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof PollutionData data) {

                if (data.getLastUpdateTick() == 0) {
                    data.setLastUpdateTick(serverLevel.getGameTime());
                }
            }
        });
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        chunk.getCapability(POLLUTION_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof PollutionData data && data.isDirty()) {
                // Помечаем чанк как несохранённый, чтобы Minecraft сохранил его
                chunk.setUnsaved(true);
                data.clearDirty();
            }
        });
    }

    // Внутренний класс-провайдер, который также умеет сериализоваться в NBT
    private static class PollutionChunkProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final PollutionData data;
        private final LazyOptional<IPollutionData> optional;

        public PollutionChunkProvider(LevelChunk chunk) {
            this.data = new PollutionData();
            this.optional = LazyOptional.of(() -> data);
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == POLLUTION_CAPABILITY) {
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