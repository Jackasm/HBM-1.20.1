package com.hbm.datagen;

import com.hbm.datagen.advancements.ModAdvancementProvider;
import com.hbm.datagen.models.ModelProvider;
import com.hbm.datagen.recipes.ModRecipeProvider;
import com.hbm.datagen.worldgen.structure.ModBubbleStructureJsonGenerator;
import com.hbm.datagen.worldgen.structure.ModNtmFeaturesJsonGenerator;
import com.hbm.datagen.worldgen.structure.ModOreStructureJsonGenerator;
import com.hbm.datagen.worldgen.structure.ModStructureJsonGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

import static com.hbm.util.RefStrings.MODID;


@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput));
        generator.addProvider(event.includeServer(), new HbmWorldGenProvider(packOutput, lookupProvider));
        ModBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(), new ModBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(), new ModAdvancementProvider(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeClient(), new ModelProvider(packOutput));

        generator.addProvider(true, new ModStructureJsonGenerator(generator));
        generator.addProvider(event.includeServer(), new ModOreStructureJsonGenerator(generator));
        generator.addProvider(event.includeServer(), new ModBubbleStructureJsonGenerator(generator));
        generator.addProvider(event.includeServer(), new ModNtmFeaturesJsonGenerator(event.getGenerator())
        );
    }
}