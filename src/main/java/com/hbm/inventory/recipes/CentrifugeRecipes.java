package com.hbm.inventory.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.DictFrame;
import com.hbm.items.ItemEnumMulti;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemBedrockOre;
import com.hbm.items.special.ItemByproduct;
import com.hbm.util.HBMEnums;
import com.hbm.util.ItemStackUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class CentrifugeRecipes {

    public static HashMap<AStack, ItemStack[]> recipes = new HashMap<>();

    public static void register() {

        boolean lbs = GeneralConfig.enableLBSM.get() && GeneralConfig.enableLBSMSimpleCentrifuge.get();

        recipes.put(new ComparableStack(DictFrame.fromOne(ModItems.CHUNK_ORE.get(), HBMEnums.EnumChunkType.RARE)), new ItemStack[] {
                new ItemStack(ModItems.POWDER_COBALT_TINY.get(), 2),
                new ItemStack(ModItems.POWDER_BORON_TINY.get(), 2),
                new ItemStack(ModItems.POWDER_NIOBIUM_TINY.get(), 2),
                new ItemStack(ModItems.NUGGET_ZIRCONIUM.get(), 3) });

        recipes.put(new ComparableStack(Items.COAL), new ItemStack[] {
                new ItemStack(ModItems.POWDER_COAL.get(), 2),
                new ItemStack(ModItems.POWDER_COAL.get(), 2),
                new ItemStack(ModItems.POWDER_COAL.get(), 2),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.LIGNITE.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_LIGNITE.get(), 2),
                new ItemStack(ModItems.POWDER_LIGNITE.get(), 2),
                new ItemStack(ModItems.POWDER_LIGNITE.get(), 2),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(Items.IRON_ORE), new ItemStack[] {
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(Items.GOLD_ORE), new ItemStack[] {
                lbs ? new ItemStack(ModItems.POWDER_GOLD.get(), 2) : new ItemStack(ModItems.POWDER_GOLD.get(), 1),
                new ItemStack(ModItems.POWDER_GOLD.get(), 1),
                lbs ? new ItemStack(ModItems.NUGGET_BISMUTH.get(), 1) : new ItemStack(ModItems.POWDER_GOLD.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(Items.DIAMOND_ORE), new ItemStack[] {
                new ItemStack(ModItems.POWDER_DIAMOND.get(), 1),
                new ItemStack(ModItems.POWDER_DIAMOND.get(), 1),
                new ItemStack(ModItems.POWDER_DIAMOND.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(Items.EMERALD_ORE), new ItemStack[] {
                new ItemStack(ModItems.POWDER_EMERALD.get(), 1),
                new ItemStack(ModItems.POWDER_EMERALD.get(), 1),
                new ItemStack(ModItems.POWDER_EMERALD.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_TITANIUM.get()), new ItemStack[] {
                lbs ? new ItemStack(ModItems.POWDER_TITANIUM.get(), 2) : new ItemStack(ModItems.POWDER_TITANIUM.get(), 1),
                lbs ? new ItemStack(ModItems.POWDER_TITANIUM.get(), 2) : new ItemStack(ModItems.POWDER_TITANIUM.get(), 1),
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(Items.NETHER_QUARTZ_ORE), new ItemStack[] {
                new ItemStack(ModItems.POWDER_QUARTZ.get(), 1),
                new ItemStack(ModItems.POWDER_QUARTZ.get(), 1),
                new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1),
                new ItemStack(Blocks.NETHERRACK, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_TUNGSTEN.get()), new ItemStack[] {
                lbs ? new ItemStack(ModItems.POWDER_TUNGSTEN.get(), 2) : new ItemStack(ModItems.POWDER_TUNGSTEN.get(), 1),
                new ItemStack(ModItems.POWDER_TUNGSTEN.get(), 1),
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(Items.COPPER_ORE), new ItemStack[] {
                lbs ? new ItemStack(ModItems.POWDER_COPPER.get(), 2) : new ItemStack(ModItems.POWDER_COPPER.get(), 1),
                new ItemStack(ModItems.POWDER_COPPER.get(), 1),
                new ItemStack(ModItems.POWDER_GOLD.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        ItemStack cryoliteStack = DictFrame.fromOne(ModItems.CHUNK_ORE.get(), HBMEnums.EnumChunkType.CRYOLITE);
        cryoliteStack.setCount(2);
        recipes.put(new ComparableStack(ModItems.ORE_ALUMINIUM.get()), new ItemStack[] {
                cryoliteStack,
                new ItemStack(ModItems.POWDER_TITANIUM.get(), 1),
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_LEAD.get()), new ItemStack[] {
                lbs ? new ItemStack(ModItems.POWDER_LEAD.get(), 2) : new ItemStack(ModItems.POWDER_LEAD.get(), 1),
                lbs ? new ItemStack(ModItems.NUGGET_BISMUTH.get(), 1) : new ItemStack(ModItems.POWDER_LEAD.get(), 1),
                new ItemStack(ModItems.POWDER_GOLD.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_SCHRABIDIUM.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_SCHRABIDIUM.get(), 1),
                new ItemStack(ModItems.POWDER_SCHRABIDIUM.get(), 1),
                new ItemStack(ModItems.NUGGET_SOLINIUM.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_RARE.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_DESH_MIX.get(), 1),
                new ItemStack(ModItems.NUGGET_ZIRCONIUM.get(), 1),
                new ItemStack(ModItems.NUGGET_ZIRCONIUM.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_PLUTONIUM.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 1),
                new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 1),
                new ItemStack(ModItems.NUGGET_POLONIUM.get(), 3),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_URANIUM.get()), new ItemStack[] {
                lbs ? new ItemStack(ModItems.POWDER_URANIUM.get(), 2) : new ItemStack(ModItems.POWDER_URANIUM.get(), 1),
                lbs ? new ItemStack(ModItems.NUGGET_TECHNETIUM.get(), 2) : new ItemStack(ModItems.POWDER_URANIUM.get(), 1),
                lbs ? new ItemStack(ModItems.NUGGET_RA_226.get(), 2) : new ItemStack(ModItems.NUGGET_RA_226.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_THORIUM.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_THORIUM.get(), 1),
                new ItemStack(ModItems.POWDER_THORIUM.get(), 1),
                new ItemStack(ModItems.POWDER_URANIUM.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_BERYLLIUM.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_BERYLLIUM.get(), 1),
                new ItemStack(ModItems.POWDER_BERYLLIUM.get(), 1),
                new ItemStack(ModItems.POWDER_EMERALD.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.ORE_FLUORITE.get()), new ItemStack[] {
                new ItemStack(ModItems.FLUORITE.get(), 3),
                new ItemStack(ModItems.FLUORITE.get(), 3),
                new ItemStack(ModItems.GEM_SODALITE.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(Items.REDSTONE_ORE), new ItemStack[] {
                new ItemStack(Items.REDSTONE, 3),
                new ItemStack(Items.REDSTONE, 3),
                lbs ? new ItemStack(ModItems.INGOT_MERCURY.get(), 3) : new ItemStack(ModItems.INGOT_MERCURY.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModBlocks.ORE_TIKITE.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 1),
                new ItemStack(ModItems.POWDER_COBALT.get(), 2),
                new ItemStack(ModItems.POWDER_NIOBIUM.get(), 2),
                new ItemStack(Blocks.END_STONE, 1) });

        recipes.put(new ComparableStack(Items.LAPIS_ORE), new ItemStack[] {
                new ItemStack(ModItems.POWDER_LAPIS.get(), 6),
                new ItemStack(ModItems.POWDER_COBALT_TINY.get(), 1),
                new ItemStack(ModItems.GEM_SODALITE.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModBlocks.BLOCK_EUPHEMIUM_CLUSTER.get()), new ItemStack[] {
                new ItemStack(ModItems.NUGGET_EUPHEMIUM.get(), 7),
                new ItemStack(ModItems.POWDER_SCHRABIDIUM.get(), 4),
                new ItemStack(ModItems.INGOT_STARMETAL.get(), 2),
                new ItemStack(ModItems.NUGGET_SOLINIUM.get(), 2) });

        recipes.put(new ComparableStack(ModBlocks.ORE_NETHER_FIRE.get()), new ItemStack[] {
                new ItemStack(Items.BLAZE_POWDER, 2),
                new ItemStack(ModItems.POWDER_FIRE.get(), 2),
                new ItemStack(ModItems.INGOT_PHOSPHORUS.get()),
                new ItemStack(Blocks.NETHERRACK) });

        recipes.put(new ComparableStack(ModItems.ORE_COBALT.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_COBALT.get(), 2),
                new ItemStack(ModItems.POWDER_IRON.get(), 1),
                new ItemStack(ModItems.POWDER_COPPER.get(), 1),
                new ItemStack(Blocks.GRAVEL, 1) });

        recipes.put(new ComparableStack(ModItems.POWDER_TEKTITE.get()), new ItemStack[] {
                new ItemStack(ModItems.POWDER_METEORITE_TINY.get(), 1),
                new ItemStack(ModItems.POWDER_PALEOGENITE_TINY.get(), 1),
                new ItemStack(ModItems.POWDER_METEORITE_TINY.get(), 1),
                new ItemStack(ModItems.DUST.get(), 6) });

        recipes.put(new ComparableStack(ModBlocks.BLOCK_SLAG.get()), new ItemStack[] {
                new ItemStack(Blocks.GRAVEL, 1),
                new ItemStack(ModItems.POWDER_FIRE.get(), 1),
                new ItemStack(ModItems.POWDER_CALCIUM.get()),
                new ItemStack(ModItems.DUST.get()) });

        recipes.put(new ComparableStack(DictFrame.fromOne(ModItems.POWDER_ASH.get(), HBMEnums.EnumAshType.COAL)), new ItemStack[] {
                new ItemStack(ModItems.POWDER_COAL_TINY.get(), 2),
                new ItemStack(ModItems.POWDER_BORON_TINY.get(), 1),
                new ItemStack(ModItems.DUST_TINY.get(), 6)});

        for(ItemBedrockOre.EnumBedrockOre ore : ItemBedrockOre.EnumBedrockOre.values()) {
            int i = ore.ordinal();

            recipes.put(new ComparableStack(ModItems.ORE_BEDROCK.get(), 1, i), new ItemStack[] {
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_CENTRIFUGED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_CENTRIFUGED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_CENTRIFUGED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_CENTRIFUGED, 1, i) });

            recipes.put(new ComparableStack(ModItems.ORE_CLEANED.get(), 1, i), new ItemStack[] {
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_SEPARATED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_SEPARATED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_SEPARATED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_SEPARATED, 1, i) });

            recipes.put(new ComparableStack(ModItems.ORE_PURIFIED.get(), 1, i), new ItemStack[] {
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i) });

            ItemByproduct.EnumByproduct tier1 = ore.byproducts[0];
            ItemStack by1 = tier1 == null ? new ItemStack(ModItems.DUST.get()) : DictFrame.fromOne(ModItems.ORE_BYPRODUCT.get(), tier1, 1);
            recipes.put(new ComparableStack(ModItems.ORE_NITRATED.get(), 1, i), new ItemStack[] {
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_NITROCRYSTALLINE, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_NITROCRYSTALLINE, 1, i),
                    ItemStackUtil.carefulCopy(by1),
                    ItemStackUtil.carefulCopy(by1) });

            ItemByproduct.EnumByproduct tier2 = ore.byproducts[1];
            ItemStack by2 = tier2 == null ? new ItemStack(ModItems.DUST.get()) : DictFrame.fromOne(ModItems.ORE_BYPRODUCT.get(), tier2, 1);
            recipes.put(new ComparableStack(ModItems.ORE_DEEPCLEANED.get(), 1, i), new ItemStack[] {
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i),
                    ItemStackUtil.carefulCopy(by2),
                    ItemStackUtil.carefulCopy(by2) });

            ItemByproduct.EnumByproduct tier3 = ore.byproducts[2];
            ItemStack by3 = tier3 == null ? new ItemStack(ModItems.DUST.get()) : DictFrame.fromOne(ModItems.ORE_BYPRODUCT.get(), tier3, 1);
            recipes.put(new ComparableStack(ModItems.ORE_SEARED.get(), 1, i), new ItemStack[] {
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i),
                    ItemEnumMulti.createStackWithMeta(ModItems.ORE_ENRICHED, 1, i),
                    ItemStackUtil.carefulCopy(by3),
                    ItemStackUtil.carefulCopy(by3) });
        }



        TagKey<Item> oreCertusQuartz = TagKey.create(Registries.ITEM, ResLocation("forge", "ores/certus_quartz"));
        TagKey<Item> crystalCertusQuartz = TagKey.create(Registries.ITEM, ResLocation("forge", "crystals/certus_quartz"));

        List<ItemStack> quartz = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(crystalCertusQuartz).stream()
                .map(ItemStack::new)
                .toList();

        if (!quartz.isEmpty()) {
            ItemStack qItem = quartz.get(0).copy();
            qItem.setCount(2);

            recipes.put(new TagStack(oreCertusQuartz, 1), new ItemStack[] {
                    qItem.copy(),
                    qItem.copy(),
                    qItem.copy(),
                    qItem.copy()
            });
        }

        ItemEnumMulti<HBMEnums.EnumChunkType> chunkOre = (ItemEnumMulti<HBMEnums.EnumChunkType>) ModItems.CHUNK_ORE.get();
        recipes.put(new ComparableStack(Items.BLAZE_ROD), new ItemStack[] {new ItemStack(Items.BLAZE_POWDER, 1), new ItemStack(Items.BLAZE_POWDER, 1), new ItemStack(ModItems.POWDER_FIRE.get(), 1), new ItemStack(ModItems.POWDER_FIRE.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_COAL.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_COAL.get(), 3), new ItemStack(ModItems.POWDER_COAL.get(), 3), new ItemStack(ModItems.POWDER_COAL.get(), 3), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_IRON.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_IRON.get(), 2), new ItemStack(ModItems.POWDER_IRON.get(), 2), new ItemStack(ModItems.POWDER_TITANIUM.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_GOLD.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_GOLD.get(), 2), new ItemStack(ModItems.POWDER_GOLD.get(), 2), new ItemStack(ModItems.INGOT_MERCURY.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_REDSTONE.get()), new ItemStack[] { new ItemStack(Items.REDSTONE, 3), new ItemStack(Items.REDSTONE, 3), new ItemStack(Items.REDSTONE, 3), new ItemStack(ModItems.INGOT_MERCURY.get(), 3) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_LAPIS.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_LAPIS.get(), 4), new ItemStack(ModItems.POWDER_LAPIS.get(), 4), new ItemStack(ModItems.POWDER_COBALT.get(), 1), new ItemStack(ModItems.GEM_SODALITE.get(), 2) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_DIAMOND.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_DIAMOND.get(), 1), new ItemStack(ModItems.POWDER_DIAMOND.get(), 1), new ItemStack(ModItems.POWDER_DIAMOND.get(), 1), new ItemStack(ModItems.POWDER_DIAMOND.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_URANIUM.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_URANIUM.get(), 2), new ItemStack(ModItems.POWDER_URANIUM.get(), 2), new ItemStack(ModItems.NUGGET_RA_226.get(), 2), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_THORIUM.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_THORIUM.get(), 2), new ItemStack(ModItems.POWDER_THORIUM.get(), 2), new ItemStack(ModItems.POWDER_URANIUM.get(), 1), new ItemStack(ModItems.NUGGET_RA_226.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_PLUTONIUM.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 2), new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 2), new ItemStack(ModItems.POWDER_POLONIUM.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_TITANIUM.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_TITANIUM.get(), 2), new ItemStack(ModItems.POWDER_TITANIUM.get(), 2), new ItemStack(ModItems.POWDER_IRON.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_SULFUR.get()), new ItemStack[] { new ItemStack(ModItems.SULFUR.get(), 4), new ItemStack(ModItems.SULFUR.get(), 4), new ItemStack(ModItems.POWDER_IRON.get(), 1), new ItemStack(ModItems.INGOT_MERCURY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_NITER.get()), new ItemStack[] { new ItemStack(ModItems.NITER.get(), 3), new ItemStack(ModItems.NITER.get(), 3), new ItemStack(ModItems.NITER.get(), 3), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_COPPER.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_COPPER.get(), 2), new ItemStack(ModItems.POWDER_COPPER.get(), 2), new ItemStack(ModItems.SULFUR.get(), 1), new ItemStack(ModItems.POWDER_COBALT_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_TUNGSTEN.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_TUNGSTEN.get(), 2), new ItemStack(ModItems.POWDER_TUNGSTEN.get(), 2), new ItemStack(ModItems.POWDER_IRON.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_ALUMINIUM.get()), new ItemStack[] { chunkOre.stackFromEnum(3, HBMEnums.EnumChunkType.CRYOLITE), new ItemStack(ModItems.POWDER_TITANIUM.get(), 1), new ItemStack(ModItems.POWDER_IRON.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_FLUORITE.get()), new ItemStack[] { new ItemStack(ModItems.FLUORITE.get(), 4), new ItemStack(ModItems.FLUORITE.get(), 4), new ItemStack(ModItems.GEM_SODALITE.get(), 2), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_BERYLLIUM.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_BERYLLIUM.get(), 2), new ItemStack(ModItems.POWDER_BERYLLIUM.get(), 2), new ItemStack(ModItems.POWDER_QUARTZ.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_LEAD.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_LEAD.get(), 2), new ItemStack(ModItems.POWDER_LEAD.get(), 2), new ItemStack(ModItems.POWDER_GOLD.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_SCHRARANIUM.get()), new ItemStack[] { new ItemStack(ModItems.NUGGET_SCHRABIDIUM.get(), 2), new ItemStack(ModItems.NUGGET_SCHRABIDIUM.get(), 2), new ItemStack(ModItems.NUGGET_URANIUM.get(), 2), new ItemStack(ModItems.NUGGET_NEPTUNIUM.get(), 2) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_SCHRABIDIUM.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_SCHRABIDIUM.get(), 2), new ItemStack(ModItems.POWDER_SCHRABIDIUM.get(), 2), new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 1), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_RARE.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_DESH_MIX.get(), 1), new ItemStack(ModItems.POWDER_DESH_MIX.get(), 1), new ItemStack(ModItems.NUGGET_ZIRCONIUM.get(), 2), new ItemStack(ModItems.NUGGET_ZIRCONIUM.get(), 2) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_PHOSPHORUS.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_FIRE.get(), 3), new ItemStack(ModItems.POWDER_FIRE.get(), 3), new ItemStack(ModItems.INGOT_PHOSPHORUS.get(), 2), new ItemStack(Items.BLAZE_POWDER, 2) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_TRIXITE.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 2), new ItemStack(ModItems.POWDER_COBALT.get(), 3), new ItemStack(ModItems.POWDER_NIOBIUM.get(), 2), new ItemStack(ModItems.POWDER_NITAN_MIX.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_LITHIUM.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_LITHIUM.get(), 2), new ItemStack(ModItems.POWDER_LITHIUM.get(), 2), new ItemStack(ModItems.POWDER_QUARTZ.get(), 1), new ItemStack(ModItems.FLUORITE.get(), 1) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_STARMETAL.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_DURA_STEEL.get(), 3), new ItemStack(ModItems.POWDER_COBALT.get(), 3), new ItemStack(ModItems.POWDER_ASTATINE.get(), 2), new ItemStack(ModItems.INGOT_MERCURY.get(), 5) });
        recipes.put(new ComparableStack(ModItems.CRYSTAL_COBALT.get()), new ItemStack[] { new ItemStack(ModItems.POWDER_COBALT.get(), 2), new ItemStack(ModItems.POWDER_IRON.get(), 3), new ItemStack(ModItems.POWDER_COPPER.get(), 3), new ItemStack(ModItems.POWDER_LITHIUM_TINY.get(), 1) });
    }

    public static List<CentrifugeRecipeWrapper> getAllRecipes() {
        List<CentrifugeRecipeWrapper> list = new ArrayList<>();
        for (Map.Entry<AStack, ItemStack[]> entry : recipes.entrySet()) {
            list.add(new CentrifugeRecipeWrapper(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    public static ItemStack[] getOutput(ItemStack stack) {
        if (stack == null) {
            return null;
        } else {
            stack.getItem();
        }

        ComparableStack comp = new ComparableStack(stack).makeSingular();

        if (recipes.containsKey(comp)) {
            return copyStackArray(recipes.get(comp));
        }

        for (Map.Entry<AStack, ItemStack[]> entry : recipes.entrySet()) {
            if (entry.getKey().matchesRecipe(stack, false)) {
                return copyStackArray(entry.getValue());
            }
        }

        return null;
    }

    private static ItemStack[] copyStackArray(ItemStack[] array) {
        if (array == null) return null;
        ItemStack[] copy = new ItemStack[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                copy[i] = array[i].copy();
            }
        }
        return copy;
    }

    /**
     * Обёртка для рецепта центрифуги (независимо от JEI)
     */
    public static class CentrifugeRecipeWrapper {
        private final AStack input;
        private final ItemStack[] outputs;

        public CentrifugeRecipeWrapper(AStack input, ItemStack[] outputs) {
            this.input = input;
            this.outputs = outputs;
        }

        public AStack getInput() { return input; }
        public List<ItemStack> getOutputs() {
            if (outputs == null) return Collections.emptyList();
            List<ItemStack> result = new ArrayList<>();
            for (ItemStack stack : outputs) {
                if (stack != null && !stack.isEmpty()) {
                    result.add(stack.copy());
                }
            }
            return result;
        }
    }
}
