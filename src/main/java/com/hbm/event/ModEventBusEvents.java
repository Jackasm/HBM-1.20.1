package com.hbm.event;

import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.*;
import com.hbm.entity.mob.botprime.EntityBOTPrimeBody;
import com.hbm.entity.mob.botprime.EntityBOTPrimeHead;
import com.hbm.entity.mob.glyphid.*;
import com.hbm.handler.pollution.IPollutionData;
import com.hbm.handler.radiation.IRadiationData;
import com.hbm.util.RefStrings;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CREEPER_NUCLEAR.get(), EntityCreeperNuclear.createAttributes().build());
        event.put(ModEntities.CREEPER_TAINTED.get(), EntityCreeperTainted.createAttributes().build());
        event.put(ModEntities.CREEPER_GOLD.get(), EntityCreeperGold.createAttributes().build());
        event.put(ModEntities.CREEPER_PHOSGENE.get(), EntityCreeperPhosgene.createAttributes().build());
        event.put(ModEntities.CREEPER_VOLATILE.get(), EntityCreeperVolatile.createAttributes().build());
        event.put(ModEntities.TAINT_CRAB.get(), EntityTaintCrab.createAttributes().build());
        event.put(ModEntities.CYBER_CRAB.get(), EntityTaintCrab.createAttributes().build());
        event.put(ModEntities.GLYPHID.get(), EntityTaintCrab.createAttributes().build());
        event.put(ModEntities.GLYPHID_DIGGER.get(), EntityTaintCrab.createAttributes().build());
        event.put(ModEntities.GLYPHID_NUCLEAR.get(), EntityTaintCrab.createAttributes().build());
        event.put(ModEntities.GLYPHID_SCOUT.get(), EntityTaintCrab.createAttributes().build());
        event.put(ModEntities.GLYPHID_BRAWLER.get(), EntityGlyphidBrawler.createAttributes().build());
        event.put(ModEntities.GLYPHID_BOMBARDIER.get(), EntityGlyphidBombardier.createAttributes().build());
        event.put(ModEntities.GLYPHID_BLASTER.get(), EntityGlyphidBlaster.createAttributes().build());
        event.put(ModEntities.GLYPHID_BEHEMOTH.get(), EntityGlyphidBehemoth.createAttributes().build());
        event.put(ModEntities.GLYPHID_BRENDA.get(), EntityGlyphidBrenda.createAttributes().build());
        event.put(ModEntities.CUSTOM_SKELETON.get(), CustomSkeleton.createAttributes().build());
        event.put(ModEntities.DUCK.get(), Chicken.createAttributes().build());
        event.put(ModEntities.QUACKOS.get(), Chicken.createAttributes().build());
        event.put(ModEntities.GHOST.get(), EntityGhost.createAttributes().build());
        event.put(ModEntities.RADBEAST.get(), EntityRADBeast.createAttributes().build());
        event.put(ModEntities.FBI.get(), EntityFBI.createAttributes().build());
        event.put(ModEntities.FBI_DRONE.get(), EntityFBIDrone.createAttributes().build());
        event.put(ModEntities.UFO.get(), EntityUFO.createAttributes().build());
        event.put(ModEntities.MASKMAN.get(), EntityMaskMan.createAttributes().build());
        event.put(ModEntities.BOT_PRIME_HEAD.get(), EntityBOTPrimeHead.createAttributes().build());
        event.put(ModEntities.BOT_PRIME_BODY.get(), EntityBOTPrimeBody.createAttributes().build());


    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPollutionData.class);
        event.register(IRadiationData.class);
    }
}