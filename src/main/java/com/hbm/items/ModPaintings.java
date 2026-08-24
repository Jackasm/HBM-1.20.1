package com.hbm.items;

import com.hbm.util.RefStrings;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPaintings {

    public static final DeferredRegister<PaintingVariant> PAINTING_VARIANTS =
            DeferredRegister.create(Registries.PAINTING_VARIANT, RefStrings.MODID);

    public static final RegistryObject<PaintingVariant> FLAME_PONY =
            PAINTING_VARIANTS.register("flame_pony_painting", () -> new PaintingVariant(16, 16));

    public static void register(IEventBus bus) {
        PAINTING_VARIANTS.register(bus);
    }
}
