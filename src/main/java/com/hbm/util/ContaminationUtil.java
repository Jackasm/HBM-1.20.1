package com.hbm.util;

import com.hbm.entity.mob.EntityCreeperNuclear;
import com.hbm.entity.mob.EntityDuck;
import com.hbm.entity.mob.EntityQuackos;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.extprop.IRadiationImmune;
import com.hbm.handler.HazmatRegistry;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry.HazardClass;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class ContaminationUtil {

    /**
     * Calculates how much radiation can be applied to this entity by calculating resistance
     * @param entity the entity to calculate resistance for
     * @return radiation modifier
     */
    public static float calculateRadiationMod(LivingEntity entity) {
        if(entity instanceof Player player) {
            float koeff = 10.0F;
            return (float) Math.pow(koeff, -HazmatRegistry.getResistance(player));
        }
        return 1;
    }

    public static float getRads(Entity e) {
        if(!(e instanceof LivingEntity living))
            return 0.0F;

        if(isRadImmune(e))
            return 0.0F;

        return HbmLivingProps.getRadiation(living);
    }

    public static final Set<Class<?>> immuneEntities = new HashSet<>();

    static {
        immuneEntities.add(EntityCreeperNuclear.class);
        immuneEntities.add(MushroomCow.class);
        immuneEntities.add(Zombie.class);
        immuneEntities.add(Skeleton.class);
        immuneEntities.add(EntityQuackos.class);
        immuneEntities.add(Ocelot.class);
        immuneEntities.add(IRadiationImmune.class);
    }

    public static boolean isRadImmune(Entity e) {
        if(e instanceof LivingEntity living && living.hasEffect(HbmPotion.MUTATION.get()))
            return true;

        if("cyano.lootable.entities.EntityLootableBody".equals(e.getClass().getName()))
            return true;

        for(Class<?> clazz : immuneEntities) {
            if(clazz.isAssignableFrom(e.getClass()))
                return true;
        }

        return false;
    }

    /// ASBESTOS ///
    public static void applyAsbestos(Entity e, int i) {
        if(!(e instanceof LivingEntity living))
            return;

        if(e instanceof Player player && player.getAbilities().instabuild)
            return;

        if(e instanceof Player && e.tickCount < 200)
            return;

        if(ArmorRegistry.hasAllProtection(living, 3, HazardClass.PARTICLE_FINE))
            ArmorUtil.damageGasMaskFilter(living, i);
        else
            HbmLivingProps.incrementAsbestos(living, i);
    }

    /// DIGAMMA ///
    public static void applyDigammaData(Entity e, float f) {
        if(!(e instanceof LivingEntity living))
            return;

        if(e instanceof EntityDuck || e instanceof Ocelot)
            return;

        if(e instanceof Player player && player.getAbilities().instabuild)
            return;

        if(e instanceof Player && e.tickCount < 200)
            return;

        if(living.hasEffect(HbmPotion.STABILITY.get()))
            return;

        if(!(e instanceof Player && ArmorUtil.checkForDigamma((Player) e)))
            HbmLivingProps.incrementDigamma(living, f);
    }

    public static void applyDigammaDirect(Entity e, float f) {
        if(!(e instanceof LivingEntity living))
            return;

        if(e instanceof IRadiationImmune)
            return;

        if(e instanceof Player player && player.getAbilities().instabuild)
            return;

        HbmLivingProps.incrementDigamma(living, f);
    }

    public static float getDigamma(Entity e) {
        if(!(e instanceof LivingEntity living))
            return 0.0F;

        return HbmLivingProps.getDigamma(living);
    }

    public static void printGeigerData(Player player) {
        Level world = player.level();

        double eRad = Math.round(HbmLivingProps.getRadiation(player) * 10) / 10D;
        double e =HbmLivingProps.getRadiation(player);
        double rads = Math.round(RadiationEvents.getRadiation(world,
                player.blockPosition()) * 10) / 10D;
        double r = RadiationEvents.getRadiation(world, player.blockPosition());
        double env = Math.round(HbmLivingProps.getRadBuf(player) * 10D) / 10D;

        double res = Math.round(10000D - ContaminationUtil.calculateRadiationMod(player) * 10000D) / 100D;
        double resKoeff = Math.round(HazmatRegistry.getResistance(player) * 100D) / 100D;

        String chunkPrefix = getPrefixFromRad(rads);
        String envPrefix = getPrefixFromRad(env);
        String radPrefix = "";
        String resPrefix = "" + ChatFormatting.WHITE;

        if(eRad < 200)
            radPrefix += ChatFormatting.GREEN;
        else if(eRad < 400)
            radPrefix += ChatFormatting.YELLOW;
        else if(eRad < 600)
            radPrefix += ChatFormatting.GOLD;
        else if(eRad < 800)
            radPrefix += ChatFormatting.RED;
        else if(eRad < 1000)
            radPrefix += ChatFormatting.DARK_RED;
        else
            radPrefix += ChatFormatting.DARK_GRAY;

        if(resKoeff > 0)
            resPrefix += ChatFormatting.GREEN;

        Component title = Component.literal("===== ☢ ")
                .append(Component.translatable("geiger.title"))
                .append(Component.literal(" ☢ ====="))
                .withStyle(ChatFormatting.GOLD);

        Component chunkRad = Component.translatable("geiger.chunkRad")
                .append(Component.literal(" " + chunkPrefix + rads + " RAD/s"))
                .withStyle(ChatFormatting.YELLOW);

        Component envRad = Component.translatable("geiger.envRad")
                .append(Component.literal(" " + envPrefix + env + " RAD/s"))
                .withStyle(ChatFormatting.YELLOW);

        Component playerRad = Component.translatable("geiger.playerRad")
                .append(Component.literal(" " + radPrefix + eRad + " RAD"))
                .withStyle(ChatFormatting.YELLOW);

        Component playerRes = Component.translatable("geiger.playerRes")
                .append(Component.literal(" " + resPrefix + res + "% (" + resKoeff + ")"))
                .withStyle(ChatFormatting.YELLOW);

        player.sendSystemMessage(title);
        player.sendSystemMessage(chunkRad);
        player.sendSystemMessage(envRad);
        player.sendSystemMessage(playerRad);
        player.sendSystemMessage(playerRes);
    }

    public static void printDosimeterData(Player player) {
        double env = Math.round(HbmLivingProps.getRadBuf(player) * 10D) / 10D;
        boolean limit = false;

        if(env > 3.6D) {
            env = 3.6D;
            limit = true;
        }

        String envPrefix = getPrefixFromRad(env);

        Component title = Component.literal("===== ☢ ")
                .append(Component.translatable("geiger.title.dosimeter"))
                .append(Component.literal(" ☢ ====="))
                .withStyle(ChatFormatting.GOLD);

        Component envRad = Component.translatable("geiger.envRad")
                .append(Component.literal(" " + envPrefix + (limit ? ">" : "") + env + " RAD/s"))
                .withStyle(ChatFormatting.YELLOW);

        player.sendSystemMessage(title);
        player.sendSystemMessage(envRad);
    }

    public static String getPrefixFromRad(double rads) {
        if(rads == 0)
            return ChatFormatting.GREEN.toString();
        else if(rads < 1)
            return ChatFormatting.YELLOW.toString();
        else if(rads < 10)
            return ChatFormatting.GOLD.toString();
        else if(rads < 100)
            return ChatFormatting.RED.toString();
        else if(rads < 1000)
            return ChatFormatting.DARK_RED.toString();
        else
            return ChatFormatting.DARK_GRAY.toString();
    }

    public static void printDiagnosticData(Player player) {
        double digamma = Math.round(HbmLivingProps.getDigamma(player) * 100) / 100D;
        double halflife = Math.round((1D - Math.pow(0.5, digamma)) * 10000) / 100D;

        Component title = Component.literal("===== Ϝ ")
                .append(Component.translatable("digamma.title"))
                .append(Component.literal(" Ϝ ====="))
                .withStyle(ChatFormatting.DARK_PURPLE);

        Component playerDigamma = Component.translatable("digamma.playerDigamma")
                .append(Component.literal(ChatFormatting.RED + " " + digamma + " DRX"))
                .withStyle(ChatFormatting.LIGHT_PURPLE);

        Component playerHealth = Component.translatable("digamma.playerHealth")
                .append(Component.literal(ChatFormatting.RED + " " + halflife + "%"))
                .withStyle(ChatFormatting.LIGHT_PURPLE);

        Component playerRes = Component.translatable("digamma.playerRes")
                .append(Component.literal(ChatFormatting.BLUE + " " + "N/A"))
                .withStyle(ChatFormatting.LIGHT_PURPLE);

        player.sendSystemMessage(title);
        player.sendSystemMessage(playerDigamma);
        player.sendSystemMessage(playerHealth);
        player.sendSystemMessage(playerRes);
    }

    public enum HazardType {
        RADIATION,
        DIGAMMA
    }

    public enum ContaminationType {
        FARADAY,        // preventable by metal armor
        HAZMAT,         // preventable by hazmat
        HAZMAT2,        // preventable by heavy hazmat
        DIGAMMA,        // preventable by fau armor or stability
        DIGAMMA2,       // preventable by robes
        CREATIVE,       // preventable by creative mode, for rad calculation armor piece bonuses still apply
        RAD_BYPASS,     // same as creative but will not apply radiation resistance calculation
        NONE            // not preventable
    }

    /**
     * Contaminates an entity with a specific hazard type and contamination type
     * @param entity the entity to contaminate
     * @param hazard the type of hazard
     * @param cont the contamination type
     * @param amount the amount of contamination
     * @return true if contamination was applied, false otherwise
     */
    @SuppressWarnings("incomplete-switch")
    public static boolean contaminate(LivingEntity entity, HazardType hazard, ContaminationType cont, float amount) {
        if(hazard == HazardType.RADIATION) {
            float radEnv = HbmLivingProps.getRadEnv(entity);
            HbmLivingProps.setRadEnv(entity, radEnv + amount);
        }

        if(entity instanceof Player player) {
            if(player.getAbilities().instabuild && cont != ContaminationType.NONE && cont != ContaminationType.DIGAMMA2)
                return false;

            if(player.tickCount < 200)
                return false;

            switch(cont) {
                case FARADAY: if(ArmorUtil.checkForFaraday(player)) return false; break;
                case HAZMAT: if(ArmorUtil.checkForHazmat(player)) return false; break;
                case HAZMAT2: if(ArmorUtil.checkForHaz2(player)) return false; break;
                case DIGAMMA: if(ArmorUtil.checkForDigamma(player)) return false;
                case DIGAMMA2: if(ArmorUtil.checkForDigamma2(player)) return false; break;
            }
        }

        if(hazard == HazardType.RADIATION && isRadImmune(entity))
            return false;

        switch(hazard) {
            case RADIATION:
                HbmLivingProps.incrementRadiation(entity,
                        amount * (cont == ContaminationType.RAD_BYPASS ? 1 : calculateRadiationMod(entity)));
                break;
            case DIGAMMA:
                HbmLivingProps.incrementDigamma(entity, amount);
                break;
        }

        return true;
    }
}