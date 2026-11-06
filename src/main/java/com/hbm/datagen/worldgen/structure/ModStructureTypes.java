package com.hbm.datagen.worldgen.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.util.RefStrings.MODID;

public class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

    public static final RegistryObject<StructureType<NBTStructure>> SIMPLE_NBT =
            STRUCTURE_TYPES.register("simple_nbt", () -> () -> NBTStructure.CODEC);

    public static final RegistryObject<StructureType<NTMFeaturesStructure>> NTM_FEATURES =
            STRUCTURE_TYPES.register("ntm_features", () -> () -> NTMFeaturesStructure.CODEC);

    public static final RegistryObject<StructureType<AtomStructure>> ATOM =
            STRUCTURE_TYPES.register("atom", () -> () -> AtomStructure.CODEC);

    public static final RegistryObject<StructureType<OreCaveStructure>> ORE_CAVE =
            STRUCTURE_TYPES.register("ore_cave", () -> () -> OreCaveStructure.CODEC);

    public static final RegistryObject<StructureType<SchistStratumStructure>> SCHIST_STRATUM =
            STRUCTURE_TYPES.register("schist_stratum", () -> () -> SchistStratumStructure.CODEC);

    public static final RegistryObject<StructureType<OilBubbleStructure>> OIL_BUBBLE =
            STRUCTURE_TYPES.register("oil_bubble", () -> () -> OilBubbleStructure.CODEC);

    public static final RegistryObject<StructureType<SandBubbleStructure>> SAND_BUBBLE =
            STRUCTURE_TYPES.register("sand_bubble", () -> () -> SandBubbleStructure.CODEC);

    public static final RegistryObject<StructureType<NothingStructure>> NOTHING =
            STRUCTURE_TYPES.register("nothing", () -> () -> NothingStructure.CODEC);
}