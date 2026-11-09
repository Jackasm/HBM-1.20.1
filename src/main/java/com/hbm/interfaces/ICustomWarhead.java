package com.hbm.interfaces;

import com.google.common.annotations.Beta;
import com.hbm.hazard.HazardRegistration;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Beta
public interface ICustomWarhead {

    Logger LOGGER = LogManager.getLogger("hbm/customwarhead");
    DecimalFormat DF = new DecimalFormat("#.00");

    enum EnumCustomWarhead {
        AMAT, BF, BIO, CHEM, FUSION, GRAV, HE, NUCLEAR, TX, SCHRAB, ZPE;

        public Component getLoc() {
            return Component.translatable("warhead." + this);
        }

        public enum EnumChemicalType {
            ACID, CHLORINE, NERVE, TOX;
            public Component getLoc() { return Component.translatable("warhead.CHEM." + this); }
        }

        public enum EnumBioType {
            ANTHRAX, MKU;
            public Component getLoc() { return Component.translatable("warhead.BIO." + this); }
        }
    }

    enum EnumCustomWarheadTrait {
        CLEAN, CLEANISH, DIRTY, RAD, SALT;
        public Component getLoc() { return Component.translatable("warheadTrait." + this); }
    }

    enum EnumWeaponType {
        DENIAL, STRATEGIC, TACTICAL, WMD;
        public Component getLoc() { return Component.translatable("warheadType." + this); }
    }

    String KEY_ANTHRAX = "warheadFuel.ANTHRAX";
    String KEY_MKU = "warheadFuel.MKU";
    String KEY_CAUSTIC = "warheadFuel.ACID";
    String KEY_NERVE = "warheadFuel.NERVE";
    String KEY_TOX = "warheadFuel.TOX";

    String NBT_GROUP = "NTM_NUKE_INFO";
    String NBT_YIELD = "YIELD";
    String NBT_ALTITUDE = "ALTITUDE";
    String NBT_MASS = "MASS";
    String NBT_SPECIAL = "SPECIAL_FIELD";
    String NBT_WARHEAD = "WARHEAD";
    String NBT_TYPE = "WARHEAD_TYPE";
    String NBT_TRAIT = "WARHEAD_TRAIT";

    static ChatFormatting getColorFromWarhead(EnumCustomWarhead warhead) {
        return switch (warhead) {
            case AMAT -> ChatFormatting.DARK_RED;
            case BF -> ChatFormatting.GREEN;
            case BIO -> ChatFormatting.GOLD;
            case CHEM -> ChatFormatting.YELLOW;
            case FUSION -> ChatFormatting.BLUE;
            case GRAV -> ChatFormatting.DARK_GRAY;
            case HE -> ChatFormatting.RED;
            case NUCLEAR -> ChatFormatting.DARK_GREEN;
            case SCHRAB -> ChatFormatting.AQUA;
            case TX -> ChatFormatting.DARK_PURPLE;
            case ZPE -> (System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.DARK_AQUA : ChatFormatting.LIGHT_PURPLE);
            default -> ChatFormatting.WHITE;
        };
    }

    default float getYield() { return 0.0F; }

    default EnumCustomWarhead getWarheadType(CompoundTag data) {
        return EnumCustomWarhead.valueOf(data.getString(NBT_WARHEAD));
    }

    default EnumWeaponType getWeaponType(CompoundTag data) {
        return EnumWeaponType.valueOf(data.getString(NBT_TYPE));
    }

    default EnumCustomWarheadTrait getWeaponTrait(CompoundTag data) {
        return EnumCustomWarheadTrait.valueOf(data.getString(NBT_TRAIT));
    }

    ItemStack constructNew();
    ICustomWarhead getInstance();

    default Item getItem() { return (Item) this; }

    static ItemStack addData(CompoundTag data, Item item) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().put(NBT_GROUP, data);
        return stack.copy();
    }

    default CompoundTag getWarheadData(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return new CompoundTag();
        return tag.getCompound(NBT_GROUP);
    }

    default ItemStack addFuel(ItemStack stack, Enum<?> fuel, float amount) {
        if (!stack.isEmpty() && stack.getItem() instanceof ICustomWarhead) {
            CompoundTag data = getWarheadData(stack);
            data.putFloat(fuel.toString(), amount);
            data.putFloat(NBT_MASS, data.getFloat(NBT_MASS) + amount);
        }
        return stack;
    }

    default ItemStack addData(ItemStack stack, String key, String value) {
        if (!stack.isEmpty() && stack.getItem() instanceof ICustomWarhead) {
            getWarheadData(stack).putString(key, value);
        }
        return stack;
    }

    default void addCompositionalInfo(CompoundTag data, List<Component> tooltip, List<Enum<?>> combinedFuels) {
        for (Enum<?> f : combinedFuels) {
            if (data.getFloat(f.toString()) > 0) {
                tooltip.add(Component.literal(String.format(Locale.US, "%s: %skg (%s)",
                        Component.translatable("warheadFuel." + f).getString(),
                        DF.format(data.getFloat(f.toString())),
                        BobMathUtil.toPercentage(data.getFloat(f.toString()), data.getFloat(NBT_MASS)))));
            }
        }
    }

    default void addTooltip(ItemStack stack, List<Component> tooltip) {
        try {
            CompoundTag data = getWarheadData(stack);

            ArrayList<Enum<?>> combinedFuels = new ArrayList<>();
            combinedFuels.addAll(Arrays.asList(FissileFuel.values()));
            combinedFuels.addAll(Arrays.asList(FusionFuel.values()));
            combinedFuels.addAll(Arrays.asList(SaltedFuel.values()));
            combinedFuels.addAll(Arrays.asList(EnumCustomWarhead.values()));

            switch (getWarheadType(data)) {
                case NUCLEAR, TX, HE -> {
                    tooltip.add(Component.translatable("hbm.text.composition"));
                    addCompositionalInfo(data, tooltip, combinedFuels);
                }
                default -> { }
            }

            EnumCustomWarhead warhead = getWarheadType(data);
            tooltip.add(Component.literal(data.getFloat(NBT_MASS) + "kg total"));
            tooltip.add(Component.empty());

            switch (warhead) {
                case CHEM, BIO -> tooltip.add(Component.translatable("hbm.text.warhead_type", getColorFromWarhead(warhead),
                        Component.translatable("warhead." + warhead, Component.translatable(data.getString(NBT_SPECIAL))).getString()));
                default -> tooltip.add(Component.translatable("hbm.text.type", getColorFromWarhead(warhead), warhead.getLoc().getString()));
            }
            tooltip.add(Component.translatable("hbm.text.function", getWeaponType(data).getLoc().getString()));

            switch (warhead) {
                case AMAT, BF, FUSION, GRAV, HE, NUCLEAR, TX ->
                        tooltip.add(Component.translatable("hbm.text.yield", BobMathUtil.getShortNumber(data.getInt(NBT_YIELD))));
                case BIO, CHEM, SCHRAB ->
                        tooltip.add(Component.translatable("hbm.text.radius", BobMathUtil.getShortNumber(data.getInt(NBT_YIELD))));
                default -> { }
            }
            tooltip.add(Component.translatable("hbm.text.trait", getWeaponTrait(data).getLoc().getString()));
        } catch (Exception e) {
            LOGGER.catching(Level.ERROR, e);
        }
    }

    enum FissileFuel {
        U233(15F, 197.5F, HazardRegistration.u233, 19.05F),
        U235(52F, 202.5F, HazardRegistration.u235, 19.05F),
        Np237(60F, 202.5F, HazardRegistration.np237, 20.45F),
        Pu239(10F, 207.1F, HazardRegistration.pu239, 19.86F),
        Pu241(12, 210F, HazardRegistration.pu241, 19.86F),
        Am241(66, 210F, HazardRegistration.am241, 13.67F),
        Am242m(11F, 212F, HazardRegistration.am242, 13.67F),
        Sa326(1F, 250F, HazardRegistration.sa326, 39.7F);

        public final float criticalMass;
        public final float energyReleased;
        public final float radioactivity;
        private final float mass;

        FissileFuel(float criticalMass, float energyReleased, float radioactivity, float mass) {
            this.criticalMass = criticalMass;
            this.energyReleased = energyReleased;
            this.radioactivity = radioactivity;
            this.mass = mass;
        }

        public float getBlockMass() { return mass * 100; }
        public float getIngotMass() { return getBlockMass() / 9; }
        public float getNuggetMass() { return getIngotMass() / 9; }
        public Component getLoc() { return Component.translatable("warheadFuel." + this); }
    }

    enum FusionFuel {
        DEUT, TRIT, Li, LiDEUT;
        public Component getLoc() { return Component.translatable("warheadFuel." + this); }
    }

    enum SaltedFuel {
        Co59(1.4902F * 0.75F, 5, HalfLifeType.MEDIUM, 8.86F),
        Co60(1.4902F, 5, HalfLifeType.MEDIUM, 8.86F),
        Sr90(0.546F, 28, HalfLifeType.MEDIUM, 2.64F),
        Cs137(1.1737F, 30, HalfLifeType.MEDIUM, 1.93F),
        Ta181(0.52F * 0.75F, 114, HalfLifeType.SHORT, 16.65F),
        Ta182(0.52F, 114, HalfLifeType.SHORT, 16.654F),
        Au197(1.3735F * 0.75F, 2, HalfLifeType.SHORT, 19.32F),
        Au198(1.3735F, 2, HalfLifeType.SHORT, 19.32F),
        Pu240(5.25575F, 65, HalfLifeType.LONG, 19.86F),
        Sa327(0.5F, 100, HalfLifeType.LONG, 39.7F);

        public final float decayEnergy;
        public final int halfLife;
        public final HalfLifeType type;
        private final float mass;

        SaltedFuel(float decayEnergy, int halfLife, HalfLifeType type, float mass) {
            this.decayEnergy = decayEnergy;
            this.halfLife = halfLife;
            this.type = type;
            this.mass = mass;
        }

        public enum HalfLifeType {
            SHORT, MEDIUM, LONG;
        }

        public float getBlockMass() { return mass * 100; }
        public float getIngotMass() { return getBlockMass() / 9; }
        public float getNuggetMass() { return getIngotMass() / 9; }
    }
}