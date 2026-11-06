package com.hbm.datagen.worldgen.structure;

import com.hbm.datagen.worldgen.structure.pieces.AtomPiece;
import com.hbm.datagen.worldgen.structure.pieces.ConformingNBTStructurePiece;
import com.hbm.datagen.worldgen.structure.pieces.EmptyStructurePiece;
import com.hbm.datagen.worldgen.structure.pieces.NBTStructurePiece;
import com.hbm.datagen.worldgen.structure.pieces.NTMFeaturesPiece;
import com.hbm.datagen.worldgen.structure.pieces.OreVeinPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.util.RefStrings.MODID;

public class ModStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, MODID);

    public static final RegistryObject<StructurePieceType> NBT_PIECE =
            STRUCTURE_PIECE_TYPES.register("nbt_piece",
                    () -> NBTStructurePiece::new);

    public static final RegistryObject<StructurePieceType> CONFORMING_NBT_PIECE =
            STRUCTURE_PIECE_TYPES.register("conforming_nbt_piece",
                    () -> ConformingNBTStructurePiece::new);

    public static final RegistryObject<StructurePieceType> EMPTY_PIECE =
            STRUCTURE_PIECE_TYPES.register("empty_piece", () -> EmptyStructurePiece::new);

    public static final RegistryObject<StructurePieceType> NTM_FEATURES_PIECE =
            STRUCTURE_PIECE_TYPES.register("ntm_features_piece",
                    () -> (StructurePieceType.ContextlessType) NTMFeaturesPiece::new);

    public static final RegistryObject<StructurePieceType> ATOM_PIECE =
            STRUCTURE_PIECE_TYPES.register("atom_piece",
                    () -> (StructurePieceType.ContextlessType) AtomPiece::new);

    public static final RegistryObject<StructurePieceType> ORE_VEIN_PIECE =
            STRUCTURE_PIECE_TYPES.register("ore_vein_piece", () -> OreVeinPiece::new);

    public static final RegistryObject<StructurePieceType> OIL_BUBBLE_PIECE =
            STRUCTURE_PIECE_TYPES.register("oil_bubble_piece", () -> OilBubblePiece::new);
}