package com.hbm.creativetabs;

import com.hbm.items.ItemEnumMulti;
import com.hbm.items.ItemGenericPart;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemBedrockOreNew;
import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class PartsTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.parts_tab"))
                .icon(() -> ModItems.INGOT_URANIUM.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.PARTS_TAB, output);

                    ItemEnumMulti powderAsh = (ItemEnumMulti) ModItems.POWDER_ASH.get();
                    for (Object enumConstant : powderAsh.theEnum.getEnumConstants()) {
                        output.accept(powderAsh.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti briquette = (ItemEnumMulti) ModItems.BRIQUETTE.get();
                    for (Object enumConstant : briquette.theEnum.getEnumConstants()) {
                        output.accept(briquette.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti page = (ItemEnumMulti) ModItems.PAGE_OF.get();
                    for (Object enumConstant : page.theEnum.getEnumConstants()) {
                        output.accept(page.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti coke = (ItemEnumMulti) ModItems.COKE.get();
                    for (Object enumConstant : coke.theEnum.getEnumConstants()) {
                        output.accept(coke.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti pelletRtgDepleted = (ItemEnumMulti) ModItems.PELLET_RTG_DEPLETED.get();
                    for (Object enumConstant : pelletRtgDepleted.theEnum.getEnumConstants()) {
                        output.accept(pelletRtgDepleted.stackFromEnum((Enum) enumConstant));
                    }

                    ItemGenericPart partGeneric = (ItemGenericPart) ModItems.PART_GENERIC.get();
                    for (Object enumConstant : partGeneric.theEnum.getEnumConstants()) {
                        output.accept(partGeneric.stackFromEnum((ItemGenericPart.EnumPartType) enumConstant));
                    }

                    ItemEnumMulti chunk_ore = (ItemEnumMulti) ModItems.CHUNK_ORE.get();
                    for (Object enumConstant : chunk_ore.theEnum.getEnumConstants()) {
                        output.accept(chunk_ore.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_bedrock = (ItemEnumMulti) ModItems.ORE_BEDROCK.get();
                    for (Object enumConstant : ore_bedrock.theEnum.getEnumConstants()) {
                        output.accept(ore_bedrock.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_centrifuged = (ItemEnumMulti) ModItems.ORE_CENTRIFUGED.get();
                    for (Object enumConstant : ore_centrifuged.theEnum.getEnumConstants()) {
                        output.accept(ore_centrifuged.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_cleaned = (ItemEnumMulti) ModItems.ORE_CLEANED.get();
                    for (Object enumConstant : ore_cleaned.theEnum.getEnumConstants()) {
                        output.accept(ore_cleaned.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_separated = (ItemEnumMulti) ModItems.ORE_SEPARATED.get();
                    for (Object enumConstant : ore_separated.theEnum.getEnumConstants()) {
                        output.accept(ore_separated.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_purified = (ItemEnumMulti) ModItems.ORE_PURIFIED.get();
                    for (Object enumConstant : ore_purified.theEnum.getEnumConstants()) {
                        output.accept(ore_purified.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_nitrated = (ItemEnumMulti) ModItems.ORE_NITRATED.get();
                    for (Object enumConstant : ore_nitrated.theEnum.getEnumConstants()) {
                        output.accept(ore_nitrated.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_nitrocrystalline = (ItemEnumMulti) ModItems.ORE_NITROCRYSTALLINE.get();
                    for (Object enumConstant : ore_nitrocrystalline.theEnum.getEnumConstants()) {
                        output.accept(ore_nitrocrystalline.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_deepcleaned = (ItemEnumMulti) ModItems.ORE_DEEPCLEANED.get();
                    for (Object enumConstant : ore_deepcleaned.theEnum.getEnumConstants()) {
                        output.accept(ore_deepcleaned.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_seared = (ItemEnumMulti) ModItems.ORE_SEARED.get();
                    for (Object enumConstant : ore_seared.theEnum.getEnumConstants()) {
                        output.accept(ore_seared.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_enriched = (ItemEnumMulti) ModItems.ORE_ENRICHED.get();
                    for (Object enumConstant : ore_enriched.theEnum.getEnumConstants()) {
                        output.accept(ore_enriched.stackFromEnum((Enum) enumConstant));
                    }

                    ItemEnumMulti ore_byproduct = (ItemEnumMulti) ModItems.ORE_BYPRODUCT.get();
                    for (Object enumConstant : ore_byproduct.theEnum.getEnumConstants()) {
                        output.accept(ore_byproduct.stackFromEnum((Enum) enumConstant));
                    }

                    // Bedrock Ore (ItemBedrockOreNew)
                    ItemBedrockOreNew bedrockOre = (ItemBedrockOreNew) ModItems.BEDROCK_ORE.get();
                    for (ItemBedrockOreNew.BedrockOreGrade grade : ItemBedrockOreNew.BedrockOreGrade.values()) {
                        for (ItemBedrockOreNew.BedrockOreType type : ItemBedrockOreNew.BedrockOreType.values()) {
                            output.accept(ItemBedrockOreNew.make(grade, type));
                        }
                    }


                })
                .build();
    }
}