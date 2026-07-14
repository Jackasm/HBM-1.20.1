package com.hbm.client;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.*;
import com.hbm.items.fluid.ItemFluidID;

import com.hbm.items.machine.ItemScraps;
import com.hbm.items.special.ItemBedrockOre;
import com.hbm.items.special.ItemBedrockOreNew;
import com.hbm.items.special.ItemByproduct;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.block.deco.DecoCTGeometry;
import com.hbm.render.item.ItemRenderMissileGeneric;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.model.*;
import com.hbm.render.overlay.OverlayManager;
import com.hbm.render.overlay.QuestOverlay;
import com.hbm.render.util.MissilePart;
import com.hbm.render.util.RenderInfoSystem;
import com.hbm.tileentity.block.TileEntityBedrockOre;
import com.hbm.util.RefStrings;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

            RenderInfoSystem.getInstance();

            ItemProperties.register(ModItems.FLUID_IDENTIFIER.get(),
                    ResLocation(MODID, "fluid_color"),
                    (stack, world, entity, seed)
                            -> ItemFluidID.isEmpty(stack) ? 0.0F : 1.0F);

            for (RegistryObject<Item> container : new RegistryObject[]{
                    ModItems.FLUID_TANK,
                    ModItems.FLUID_TANK_LEAD,
                    ModItems.FLUID_BARREL,
                    ModItems.FLUID_CANISTER,
                    ModItems.FLUID_BUCKET,
                    ModItems.DISPERSER_CANISTER,
                    ModItems.GLYPHID_GLAND
            }) {
                ItemProperties.register(container.get(),
                        ResLocation(MODID, "fluid_filled"),
                        (stack, world, entity, seed) ->
                                ItemFluidContainer.isEmpty(stack) ? 0.0F : 1.0F
                );
            }

            ItemProperties.register(ModItems.SCRAPS.get(),
                    ResLocation(MODID, "scrap_type"),
                    (stack, world, entity, seed) -> {
                        if (ItemScraps.isLiquid(stack)) return 0.5F; // liquid модель
                        Mats.MaterialStack contents = ItemScraps.getMats(stack);
                        if (contents != null && contents.material.smeltable == NTMMaterial.SmeltingBehavior.ADDITIVE) return 0.75F; // additive
                        return 0.0F; // обычный scraps
                    });



            // Аммуниция
            //AmmoRegistry.registerItemProperties();

            // Ресурсы
            HBMResourceManager.init();
            HBMResourceManager.initAnimations();
            // ==================== РЕГИСТРАЦИЯ РЕНДЕРОВ РАКЕТ ====================
            ItemRenderMissileGeneric.init();
            // ==================== РЕГИСТРАЦИЯ ЧАСТЕЙ РАКЕТ ====================
            // Инициализируем все части ракет
            MissilePart.registerAllParts();

            // Регистрируем кастомные рендереры для каждой части ракеты
            registerMissilePartRenderers();

        });

        OverlayManager.registerProvider(new QuestOverlay());
    }

    @SubscribeEvent
    public static void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex == 0 && level != null && pos != null) {
                if (level.getBlockEntity(pos) instanceof TileEntityBedrockOre ore) {
                    return ore.color != 0 ? ore.color : 0xffffff;
                }
            }
            return 0xffffff;
        }, ModBlocks.ORE_BEDROCK.get());
    }

    @SubscribeEvent
    public static void onItemColors(RegisterColorHandlersEvent.Item event) {
        // Бедроковые руды
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
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Маски и противогазы
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "model_m65"), "main"), ModelHazmatMask::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "model_m65_small"), "main"), ModelM65Small::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "gas_mask"), "main"), ModelGasMask::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "model_m65_blaze"), "main"), ModelM65Blaze::createLayer);

        // Аксессуары
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "goggles"), "main"), ModelGoggles::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "hat"), "main"), ModelHat::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "cloak"), "main"), ModelCloak::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "no9"), "main"), ModelNo9::createLayer);

        // T45
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "t45_helmet"), "main"), ModelT45Helmet::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "t45_chest"), "main"), ModelT45Chest::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "t45_legs"), "main"), ModelT45Legs::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "t45_boots"), "main"), ModelT45Boots::createLayer);

        // Броня с OBJ моделями
        for (ModelLayerLocation layer : ModelArmorT51.T51_LAYERS) event.registerLayerDefinition(layer, ModelArmorT51::createLayer);
        for (ModelLayerLocation layer : ModelArmorAJR.AJR_LAYERS) event.registerLayerDefinition(layer, ModelArmorAJR::createLayer);
        for (ModelLayerLocation layer : ModelArmorAJRO.AJRO_LAYERS) event.registerLayerDefinition(layer, ModelArmorAJRO::createLayer);
        for (ModelLayerLocation layer : ModelArmorDesh.DESH_LAYERS) event.registerLayerDefinition(layer, ModelArmorDesh::createLayer);
        for (ModelLayerLocation layer : ModelArmorDiesel.DIESEL_LAYERS) event.registerLayerDefinition(layer, ModelArmorDiesel::createLayer);
        for (ModelLayerLocation layer : ModelArmorHEV.HEV_LAYERS) event.registerLayerDefinition(layer, ModelArmorHEV::createLayer);
        for (ModelLayerLocation layer : ModelArmorFAU.DIGAMMA_LAYERS) event.registerLayerDefinition(layer, ModelArmorFAU::createLayer);
        for (ModelLayerLocation layer : ModelArmorDNT.DNT_LAYERS) event.registerLayerDefinition(layer, ModelArmorDNT::createLayer);
        for (ModelLayerLocation layer : ModelArmorRPA.RPA_LAYERS) event.registerLayerDefinition(layer, ModelArmorRPA::createLayer);
        for (ModelLayerLocation layer : ModelArmorTrenchmaster.TRENCH_LAYERS) event.registerLayerDefinition(layer, ModelArmorTrenchmaster::createLayer);
        for (ModelLayerLocation layer : ModelArmorEnvsuit.ENV_LAYERS) event.registerLayerDefinition(layer, ModelArmorEnvsuit::createLayer);
        for (ModelLayerLocation layer : ModelArmorBJ.BJ_LAYERS) event.registerLayerDefinition(layer, ModelArmorBJ::createLayer);

        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "sword_redstone"), "main"), ModelSword::createLayer);
        event.registerLayerDefinition(new ModelLayerLocation(ResLocation(RefStrings.MODID, "big_sword"), "main"), ModelBigSword::createLayer);
        event.registerLayerDefinition(ModelJetPack.LAYER_LOCATION, ModelJetPack::createLayer);
        event.registerLayerDefinition(ModelBackTesla.LAYER_LOCATION, ModelBackTesla::createLayer);
        event.registerLayerDefinition(ModelFBI.LAYER_LOCATION, ModelFBI::createBodyLayer);

    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("deco_ct_geometry", DecoCTGeometry.Loader.INSTANCE);
        event.register("hfr_obj", HFRWavefrontObject.Loader.INSTANCE);
    }

    private static void registerMissilePartRenderers() {
        for (MissilePart part : MissilePart.parts.values()) {
            if (part.part != null) {
                ItemProperties.register(part.part,
                        ResLocation(MODID, "missile_part_type"),
                        (stack, level, entity, seed) -> part.type.ordinal());
            }
        }
    }

}