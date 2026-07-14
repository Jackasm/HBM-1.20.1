package com.hbm.hazard;

import com.hbm.hazard.type.*;

import java.util.HashMap;
import java.util.Map;

public class HazardRegistry {

    private static final Map<String, HazardTypeBase> HAZARD_TYPES = new HashMap<>();

    public static final HazardTypeBase RADIATION = new HazardTypeRadiation();
    public static final HazardTypeBase DIGAMMA = new HazardTypeDigamma();
    public static final HazardTypeBase HOT = new HazardTypeHot();
    public static final HazardTypeBase BLINDING = new HazardTypeBlinding();
    public static final HazardTypeBase ASBESTOS = new HazardTypeAsbestos();
    public static final HazardTypeBase COAL = new HazardTypeCoal();
    public static final HazardTypeBase HYDROACTIVE = new HazardTypeHydroactive();
    public static final HazardTypeBase EXPLOSIVE = new HazardTypeExplosive();

    public static void register() {
        registerType("coal", new HazardTypeCoal());
        registerType("radiation", new HazardTypeRadiation());
        registerType("explosive", new HazardTypeExplosive());
        registerType("digamma", new HazardTypeDigamma());
        registerType("hot", new HazardTypeHot());
        registerType("blinding", new HazardTypeBlinding());
        registerType("asbestos", new HazardTypeAsbestos());
        registerType("hydroactive", new HazardTypeHydroactive());

    }

    public static void registerType(String key, HazardTypeBase type) {
        HAZARD_TYPES.put(key, type);
    }

    public static HazardTypeBase getHazardType(String key) {
        return HAZARD_TYPES.get(key);
    }
}