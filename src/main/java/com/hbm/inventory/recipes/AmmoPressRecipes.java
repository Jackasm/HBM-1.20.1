package com.hbm.inventory.recipes;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.ItemFluidCanister;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class AmmoPressRecipes {

    public static List<AmmoPressRecipe> recipes = new ArrayList<>();

    public static void register() {
        ComparableStack lead = new ComparableStack(ModItems.INGOT_LEAD.get());
        ComparableStack nugget = new ComparableStack(ModItems.NUGGET_LEAD.get());
        ComparableStack flechette = new ComparableStack(ModItems.BOLT_LEAD.get());
        ComparableStack steel = new ComparableStack(ModItems.INGOT_STEEL.get());
        ComparableStack wSteel = new ComparableStack(ModItems.INGOT_WEAPON_STEEL.get());
        ComparableStack copper = new ComparableStack(Items.COPPER_INGOT);
        TagStack plastic = new TagStack(ModItemTags.ANY_PLASTIC_INGOT);
        ComparableStack uranium = new ComparableStack(ModItems.INGOT_URANIUM.get());
        ComparableStack ferro = new ComparableStack(ModItems.INGOT_FERROURANIUM.get());
        ComparableStack nb = new ComparableStack(ModItems.INGOT_NIOBIUM.get());
        TagStack smokeless = new TagStack(ModItemTags.ANY_SMOKELESS_DUST);
        TagStack he = new TagStack(ModItemTags.ANY_HIGHEXPLOSIVE);
        ComparableStack wp = new ComparableStack(ModItems.INGOT_PHOSPHORUS.get());
        ComparableStack rp = new ComparableStack(ModItems.POWDER_FIRE.get());
        ComparableStack pipe = new ComparableStack(ModItems.PIPE_STEEL.get());
        ComparableStack smokeful = new ComparableStack(Items.GUNPOWDER);
        ComparableStack rocket = new ComparableStack(ModItems.ROCKET_FUEL.get());
        ComparableStack dyn = new ComparableStack(ModItems.BALL_DYNAMITE.get());
        ComparableStack diesel = new ComparableStack(ItemFluidCanister.createForFluid(Fluids.DIESEL.get()));
        ComparableStack napalm = new ComparableStack(ItemFluidCanister.createForFluid(Fluids.NAPALM.get()));
        ComparableStack gas = new ComparableStack(ItemFluidCanister.createForFluid(Fluids.GAS.get()));
        ComparableStack bf = new ComparableStack(ItemFluidCanister.createForFluid(Fluids.BALEFIRE.get()));
        ComparableStack tatb = new ComparableStack(ModItems.BALL_TATB.get());
        ComparableStack shell = new ComparableStack(ModItems.ASSEMBLY_NUKE.get());

        // ----- ПЛАСТИНЫ -----
        ComparableStack coplate = new ComparableStack(ModItems.PLATE_COPPER.get());
        ComparableStack sPlate = new ComparableStack(ModItems.PLATE_STEEL.get());
        ComparableStack lPlate = new ComparableStack(ModItems.PLATE_LEAD.get());

        // ----- КРЕМНИЙ И ВОЛЬФРАМ -----
        ComparableStack silicon = new ComparableStack(ModItems.BILLET_SILICON.get());
        ComparableStack tungsten = new ComparableStack(ModItems.INGOT_TUNGSTEN.get());

        // ----- ПЛУТОНИЙ -----
        ComparableStack plutonium = new ComparableStack(ModItems.NUGGET_PU239.get());

        // ----- ГИЛЬЗЫ -----
        ComparableStack cSmall = new ComparableStack(ModItems.CASING_SMALL.get());
        ComparableStack cBig = new ComparableStack(ModItems.CASING_LARGE.get());
        ComparableStack sSmall = new ComparableStack(ModItems.CASING_SMALL_STEEL.get());
        ComparableStack sBig = new ComparableStack(ModItems.CASING_LARGE_STEEL.get());
        ComparableStack bpShell = new ComparableStack(ModItems.CASING_SHOTSHELL.get());
        ComparableStack pShell = new ComparableStack(ModItems.CASING_BUCKSHOT.get());
        ComparableStack sShell = new ComparableStack(ModItems.CASING_BUCKSHOT_ADVANCED.get());

        // ============================================================
        // .357 MAGNUM
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M357_BP.get(), 16),
                null, lead.copy(2), null,
                null, smokeful, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M357_SP.get(), 8),
                null, lead, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M357_FMJ.get(), 8),
                null, steel, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M357_JHP.get(), 8),
                plastic, copper, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M357_AP.get(), 8),
                null, wSteel, null,
                null, smokeless.copy(2), null,
                null, sSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M357_EXPRESS.get(), 8),
                null, steel, null,
                null, smokeless.copy(3), null,
                null, cSmall, null
        ));

        // ============================================================
        // .44 MAGNUM
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M44_BP.get(), 12),
                null, lead.copy(2), null,
                null, smokeful, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M44_SP.get(), 6),
                null, lead, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M44_FMJ.get(), 6),
                null, steel, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M44_JHP.get(), 6),
                plastic, copper, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M44_AP.get(), 6),
                null, wSteel, null,
                null, smokeless.copy(2), null,
                null, sSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_M44_EXPRESS.get(), 6),
                null, steel, null,
                null, smokeless.copy(3), null,
                null, cSmall, null
        ));

        // ============================================================
        // .22 LR
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P22_SP.get(), 24),
                null, lead, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P22_FMJ.get(), 24),
                null, steel, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P22_JHP.get(), 24),
                plastic, copper, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P22_AP.get(), 24),
                null, wSteel, null,
                null, smokeless.copy(2), null,
                null, sSmall, null
        ));

        // ============================================================
        // 9mm
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P9_SP.get(), 12),
                null, lead, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P9_FMJ.get(), 12),
                null, steel, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P9_JHP.get(), 12),
                plastic, copper, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P9_AP.get(), 12),
                null, wSteel, null,
                null, smokeless.copy(2), null,
                null, sSmall, null
        ));

        // ============================================================
        // .45 ACP
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P45_SP.get(), 8),
                null, lead, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P45_FMJ.get(), 8),
                null, steel, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P45_JHP.get(), 8),
                plastic, copper, null,
                null, smokeless, null,
                null, cSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P45_AP.get(), 8),
                null, wSteel, null,
                null, smokeless.copy(2), null,
                null, sSmall, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_P45_DU.get(), 8),
                null, uranium, null,
                null, smokeless.copy(2), null,
                null, sSmall, null
        ));

        // ============================================================
        // 5.56mm
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R556_SP.get(), 16),
                null, lead.copy(2), null,
                null, smokeless.copy(2), null,
                null, cSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R556_FMJ.get(), 16),
                null, steel.copy(2), null,
                null, smokeless.copy(2), null,
                null, cSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R556_JHP.get(), 16),
                plastic, copper.copy(2), null,
                null, smokeless.copy(2), null,
                null, cSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R556_AP.get(), 16),
                null, wSteel.copy(2), null,
                null, smokeless.copy(4), null,
                null, sSmall.copy(2), null
        ));

        // ============================================================
        // 7.62mm
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R762_SP.get(), 12),
                null, lead.copy(2), null,
                null, smokeless.copy(2), null,
                null, cSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R762_FMJ.get(), 12),
                null, steel.copy(2), null,
                null, smokeless.copy(2), null,
                null, cSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R762_JHP.get(), 12),
                plastic, copper.copy(2), null,
                null, smokeless.copy(2), null,
                null, cSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R762_AP.get(), 12),
                null, wSteel.copy(2), null,
                null, smokeless.copy(4), null,
                null, sSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R762_DU.get(), 12),
                null, uranium.copy(2), null,
                null, smokeless.copy(4), null,
                null, sSmall.copy(2), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_R762_HE.get(), 12),
                he, ferro, null,
                null, smokeless.copy(4), null,
                null, sSmall.copy(2), null
        ));

        // ============================================================
        // .50 BMG
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_BMG50_SP.get(), 12),
                null, lead.copy(2), null,
                null, smokeless.copy(3), null,
                null, cBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_BMG50_FMJ.get(), 12),
                null, steel.copy(2), null,
                null, smokeless.copy(3), null,
                null, cBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_BMG50_JHP.get(), 12),
                plastic, copper.copy(2), null,
                null, smokeless.copy(3), null,
                null, cBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_BMG50_AP.get(), 12),
                null, wSteel.copy(2), null,
                null, smokeless.copy(6), null,
                null, sBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_BMG50_DU.get(), 12),
                null, uranium.copy(2), null,
                null, smokeless.copy(6), null,
                null, sBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_BMG50_HE.get(), 12),
                he, ferro, null,
                null, smokeless.copy(6), null,
                null, sBig, null
        ));

        // ============================================================
        // 12 GAUGE
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_BP.get(), 6),
                null, nugget.copy(6), null,
                null, smokeful, null,
                null, bpShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_BP_MAGNUM.get(), 6),
                null, nugget.copy(8), null,
                null, smokeful, null,
                null, bpShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_BP_SLUG.get(), 6),
                null, lead, null,
                null, smokeful, null,
                null, bpShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12.get(), 6),
                null, nugget.copy(6), null,
                null, smokeless, null,
                null, pShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_SLUG.get(), 6),
                null, lead, null,
                null, smokeless, null,
                null, pShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_FLECHETTE.get(), 6),
                null, flechette.copy(12), null,
                null, smokeless, null,
                null, pShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_MAGNUM.get(), 6),
                null, nugget.copy(8), null,
                null, smokeless, null,
                null, sShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_EXPLOSIVE.get(), 6),
                null, he, null,
                null, smokeless, null,
                null, sShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G12_PHOSPHORUS.get(), 6),
                null, wp, null,
                null, smokeless, null,
                null, sShell, null
        ));

        // ============================================================
        // 10 GAUGE
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G10.get(), 4),
                null, nugget.copy(8), null,
                null, smokeless.copy(2), null,
                null, sShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G10_SHRAPNEL.get(), 4),
                plastic, nugget.copy(8), null,
                null, smokeless.copy(2), null,
                null, sShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G10_DU.get(), 4),
                null, uranium, null,
                null, smokeless.copy(2), null,
                null, sShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G10_SLUG.get(), 4),
                null, lead, null,
                null, smokeless.copy(2), null,
                null, sShell, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G10_EXPLOSIVE.get(), 4),
                he, ferro, null,
                null, smokeless.copy(2), null,
                null, sShell, null
        ));

        // ============================================================
        // 26mm FLARE
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G26_FLARE.get(), 4),
                null, rp, null,
                null, smokeless, null,
                null, cBig, null
        ));

        // ============================================================
        // 40mm GRENADE
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G40_HE.get(), 4),
                null, dyn, null,
                null, smokeless, null,
                null, cBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G40_HEAT.get(), 4),
                coplate, he, null,
                null, smokeless, null,
                null, cBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G40_DEMO.get(), 4),
                null, he.copy(2), null,
                null, smokeless, null,
                null, cBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G40_INC.get(), 4),
                diesel, dyn, null,
                null, smokeless, null,
                null, cBig, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_G40_PHOSPHORUS.get(), 4),
                wp, he, null,
                null, smokeless, null,
                null, cBig, null
        ));

        // ============================================================
        // ROCKETS
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_HE.get(), 2),
                null, dyn, null,
                null, cBig, null,
                null, smokeless.copy(3), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_HEAT.get(), 2),
                coplate, he, null,
                null, cBig, null,
                null, smokeless.copy(3), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_DEMO.get(), 2),
                null, he.copy(2), null,
                null, cBig, null,
                null, smokeless.copy(3), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_INC.get(), 2),
                diesel, dyn, null,
                null, cBig, null,
                null, smokeless.copy(3), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_PHOSPHORUS.get(), 2),
                wp, he, null,
                null, cBig, null,
                null, smokeless.copy(3), null
        ));
        // Ракеты с топливом
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_HE.get(), 2),
                null, dyn, null,
                null, cBig, null,
                null, rocket, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_HEAT.get(), 2),
                coplate, he, null,
                null, cBig, null,
                null, rocket, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_DEMO.get(), 2),
                null, he.copy(2), null,
                null, cBig, null,
                null, rocket, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_INC.get(), 2),
                diesel, dyn, null,
                null, cBig, null,
                null, rocket, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_ROCKET_PHOSPHORUS.get(), 2),
                wp, he, null,
                null, cBig, null,
                null, rocket, null
        ));

        // ============================================================
        // FLAMETHROWER
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_FLAME_DIESEL.get(), 1),
                null, sPlate, null,
                null, diesel, null,
                null, sPlate, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_FLAME_NAPALM.get(), 1),
                null, sPlate, null,
                null, napalm, null,
                null, sPlate, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_FLAME_GAS.get(), 1),
                null, sPlate, null,
                null, gas, null,
                null, sPlate, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_FLAME_BALEFIRE.get(), 1),
                null, sPlate, null,
                null, bf, null,
                null, sPlate, null
        ));

        // ============================================================
        // ENERGY
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_CAPACITOR.get(), 4),
                null, plastic, null,
                null, silicon.copy(4), null,
                null, plastic, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_CAPACITOR_OVERCHARGE.get(), 4),
                null, plastic, null,
                null, silicon.copy(6), null,
                null, plastic, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_CAPACITOR_IR.get(), 4),
                null, plastic, null,
                null, nb, null,
                null, plastic, null
        ));

        // ============================================================
        // TAU
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_TAU_URANIUM.get(), 16),
                null, lPlate, null,
                null, uranium, null,
                null, lPlate, null
        ));

        // ============================================================
        // COILGUN
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_COIL_TUNGSTEN.get(), 4),
                null, null, null,
                null, tungsten, null,
                null, null, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_COIL_FERROURANIUM.get(), 4),
                null, null, null,
                null, ferro, null,
                null, null, null
        ));

        // ============================================================
        // NUCLEAR
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_NUKE_STANDARD.get(), 1),
                null, plutonium, null,
                null, shell, null,
                null, null, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_NUKE_DEMO.get(), 1),
                null, plutonium.copy(2), null,
                null, shell, null,
                null, null, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_NUKE_HIGH.get(), 1),
                null, plutonium.copy(4), null,
                null, shell, null,
                null, null, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_NUKE_TOTS.get(), 1),
                null, plutonium.copy(2), null,
                null, tatb.copy(2), null,
                null, sPlate.copy(4), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_NUKE_HIVE.get(), 1),
                null, he.copy(8), null,
                null, sBig.copy(2), null,
                null, sPlate.copy(4), null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_NUKE_BALEFIRE.get(), 1),
                null, new ComparableStack(ModItems.EGG_BALEFIRE_SHARD.get()), null,
                null, shell, null,
                null, null, null
        ));

        // ============================================================
        // CABLE TOOL
        // ============================================================
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_CT_HOOK.get(), 16),
                null, steel, null,
                null, pipe, null,
                null, smokeless, null
        ));
        recipes.add(new AmmoPressRecipe(
                new ItemStack(ModAmmoItems.AMMO_CT_MORTAR.get(), 4),
                null, he.copy(4), null,
                null, pipe, null,
                null, smokeless, null
        ));
    }

    public static class AmmoPressRecipe {
        public ItemStack output;
        public AStack[] input;

        public AmmoPressRecipe(ItemStack output, AStack... input) {
            this.output = output;
            this.input = input;
        }
    }
}