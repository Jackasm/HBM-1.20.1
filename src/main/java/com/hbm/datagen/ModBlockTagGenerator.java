package com.hbm.datagen;

import com.hbm.blocks.ModBlocks;
import com.hbm.util.RefStrings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, RefStrings.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        for (Map.Entry<RegistryObject<Block>, ToolInfo> entry : ModBlocks.BLOCK_TOOL.entrySet()) {
            Block block = entry.getKey().get();
            ToolInfo tool = entry.getValue();

            switch (tool.getTool()) {
                case PICKAXE -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                case AXE -> tag(BlockTags.MINEABLE_WITH_AXE).add(block);
                case SHOVEL -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block);
                case NONE -> {}
            }

            switch (tool.getLevel()) {
                case STONE -> tag(BlockTags.NEEDS_STONE_TOOL).add(block);
                case IRON -> tag(BlockTags.NEEDS_IRON_TOOL).add(block);
                case DIAMOND -> tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
                case NETHERITE -> {}
                case NONE -> {}
            }
        }

        // Тег для добычи лопатой
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        ModBlocks.FALLOUT.get()
                );

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        ModBlocks.PLANT_TALL.get()  // трава/растения
                );

        // Стандартные теги добычи для ВСЕХ руд
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        ModBlocks.ORE_BEDROCK.get()


                );


        // Руды, требующие ЖЕЛЕЗНУЮ кирку и выше
        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(
                        ModBlocks.ORE_BEDROCK.get()

                );

        // Общие теги для совместимости
        this.tag(Tags.Blocks.ORES)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
}