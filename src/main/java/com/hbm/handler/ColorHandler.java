package com.hbm.handler;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.*;
import com.hbm.items.fluid.ItemFluidID;
import com.hbm.items.machine.ItemScraps;
import com.hbm.items.special.ItemBedrockOre;
import com.hbm.items.special.ItemBedrockOreNew;
import com.hbm.items.special.ItemByproduct;
import com.hbm.tileentity.block.TileEntityBedrockOre;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
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

        event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(),
                ModItems.PLANT_FLOWER_TOBACCO.get(),
                ModItems.PLANT_FLOWER_WEED.get(),
                ModItems.PLANT_TALL_WEED.get()
        );

        event.register((stack, tintIndex) -> {
                    if (stack.getItem() instanceof ItemBedrockOre oreItem) {
                        return oreItem.getColor(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                },
                ModItems.ORE_BEDROCK.get(),
                ModItems.ORE_CENTRIFUGED.get(),
                ModItems.ORE_CLEANED.get(),
                ModItems.ORE_SEPARATED.get(),
                ModItems.ORE_PURIFIED.get(),
                ModItems.ORE_NITRATED.get(),
                ModItems.ORE_NITROCRYSTALLINE.get(),
                ModItems.ORE_DEEPCLEANED.get(),
                ModItems.ORE_SEARED.get(),
                ModItems.ORE_ENRICHED.get()
        );

        event.register((stack, tintIndex) -> {
                    if (tintIndex == 1 && stack.getItem() instanceof ItemByproduct byproduct) {
                        return byproduct.getColor(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                },
                ModItems.ORE_BYPRODUCT.get()
        );

        event.register((stack, tintIndex) -> {
                    if (tintIndex == 1 && stack.getItem() instanceof ItemBedrockOreNew bedrockOreNew) {
                        return bedrockOreNew.getColor(stack, tintIndex);
                    }
                    return 0xFFFFFF;
                },
                ModItems.BEDROCK_ORE.get()
        );
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {

        event.register((state, level, pos, tintIndex) -> {
                    if (level == null || pos == null) return FoliageColor.getDefaultColor();
                    return BiomeColors.getAverageFoliageColor(level, pos);
                },
                ModBlocks.PLANT_FLOWER_TOBACCO.get(),
                ModBlocks.PLANT_FLOWER_WEED.get()
        );

        event.register((state, level, pos, tintIndex) -> {
                    if (level == null || pos == null) return FoliageColor.getDefaultColor();
                    return BiomeColors.getAverageFoliageColor(level, pos);
                },
                ModBlocks.PLANT_TALL_WEED.get()
        );

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex == 0 && level != null && pos != null) {
                if (level.getBlockEntity(pos) instanceof TileEntityBedrockOre ore) {
                    return ore.color != 0 ? ore.color : 0xffffff;
                }
            }
            return 0xffffff;
        }, ModBlocks.ORE_BEDROCK.get());
    }

}