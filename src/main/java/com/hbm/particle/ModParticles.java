package com.hbm.particle;

import com.hbm.particle.helper.CasingCreator;
import com.hbm.particle.helper.IParticleCreator;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.util.RefStrings.MODID;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

    public static final RegistryObject<SimpleParticleType> BLACK_POWDER_SMOKE =
            PARTICLE_TYPES.register("black_powder_smoke",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> BLACK_POWDER_SPARK =
            PARTICLE_TYPES.register("black_powder_spark",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> EXPLOSION_SMALL =
            PARTICLE_TYPES.register("explosion_small",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> PLASMA_BLAST =
            PARTICLE_TYPES.register("plasma_blast",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GIBLET =
            PARTICLE_TYPES.register("giblet",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ASHES =
            PARTICLE_TYPES.register("ashes",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> COOLING_TOWER =
            PARTICLE_TYPES.register("cooling_tower",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> GAS_FLAME =
            PARTICLE_TYPES.register("gas_flame", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> EX_SMOKE =
            PARTICLE_TYPES.register("ex_smoke", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> DIGAMMA_SMOKE =
            PARTICLE_TYPES.register("digamma_smoke", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> PARTICLE_CLOUD =
            PARTICLE_TYPES.register("custom_cloud", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> FOAM =
            PARTICLE_TYPES.register("foam",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> RADIATION_FOG =
            PARTICLE_TYPES.register("radiation_fog",
                    () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> SMOKE_PLUME =
            PARTICLE_TYPES.register("smoke_plume", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> ROCKET_FLAME =
            PARTICLE_TYPES.register("rocket_flame", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
        eventBus.addListener(ModParticles::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
        IParticleCreator.creators.put("casingNT", new CasingCreator());
        });
    }
}
