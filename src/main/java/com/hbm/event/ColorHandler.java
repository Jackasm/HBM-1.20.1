package com.hbm.event;

import com.hbm.items.ModItems;
import com.hbm.items.fluid.*;
import com.hbm.items.fluid.ItemFluidID;
import com.hbm.items.machine.ItemScraps;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hbm.util.RefStrings.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ColorHandler {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {

        event.register((stack, tintIndex) -> {
                    if (tintIndex == 1) {
                        return ItemFluidContainer.getFluidColor(stack);
                    }
                    return 0xFFFFFF;
                },
                ModItems.FLUID_CANISTER.get(),
                ModItems.GAS_TANK.get(),
                ModItems.FLUID_TANK.get(),
                ModItems.FLUID_BARREL.get(),
                ModItems.FLUID_BUCKET.get(),
                ModItems.FLUID_TANK_LEAD.get(),
                ModItems.DISPERSER_CANISTER.get(),
                ModItems.GLYPHID_GLAND.get()
        );

        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ItemScraps.getScrapColor(stack);
            }
            return 0xFFFFFF;
        }, ModItems.SCRAPS.get());

        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ItemFluidID.getFluidColor(stack);
            }
            return 0xFFFFFF;
        }, ModItems.FLUID_IDENTIFIER.get());

    }

}