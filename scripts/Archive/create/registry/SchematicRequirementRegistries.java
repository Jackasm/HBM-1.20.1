package com.hbm.create.registry;

import com.hbm.create.blockentity.ItemRequirement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SchematicRequirementRegistries {
    public static final SimpleRegistry<Block, BlockRequirement> BLOCKS = SimpleRegistry.create();
    public static final SimpleRegistry<BlockEntityType<?>, BlockEntityRequirement> BLOCK_ENTITIES = SimpleRegistry.create();
    public static final SimpleRegistry<EntityType<?>, EntityRequirement> ENTITIES = SimpleRegistry.create();

    @FunctionalInterface
    public interface BlockRequirement {
        ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity);
    }

    @FunctionalInterface
    public interface BlockEntityRequirement {
        ItemRequirement getRequiredItems(BlockEntity blockEntity, BlockState state);
    }

    @FunctionalInterface
    public interface EntityRequirement {
        ItemRequirement getRequiredItems(Entity entity);
    }
}
