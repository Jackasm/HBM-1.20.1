package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;

import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.pile.TileEntityPileSource;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.hbm.util.ResLocation.ResLocation;

public class BlockGraphiteSource extends BlockGraphiteDrilledTE implements IToolable {

    public BlockGraphiteSource(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPileSource(pos, state);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initTextures() {
        super.initTextures();
        if (this == ModBlocks.BLOCK_GRAPHITE_PLUTONIUM.get()) {
            this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_plutonium_aluminum.png");
        } else if (this == ModBlocks.BLOCK_GRAPHITE_SOURCE.get()) {
            this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_source_aluminum.png");
        }
    }

    @Override
    protected Item getInsertedItem() {
        return this == ModBlocks.BLOCK_GRAPHITE_PLUTONIUM.get() ? ModItems.PILE_ROD_PLUTONIUM.get() : ModItems.PILE_ROD_SOURCE.get();
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.PILE_SOURCE.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityPileSource source) {
                    source.tick();
                }
            };
        }
        return null;
    }
}