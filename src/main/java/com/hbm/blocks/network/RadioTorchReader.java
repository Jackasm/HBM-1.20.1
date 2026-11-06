package com.hbm.blocks.network;

import com.hbm.api.redstoneoverradio.IRORValueProvider;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityRadioTorchReader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * RTTY-читатель: висит только на блоке, который умеет отдавать значения
 * ({@link IRORValueProvider}) — как в эталоне
 * {@code ref:blocks/network/RadioTorchReader.java}.
 */
public class RadioTorchReader extends RadioTorchBase {

    public RadioTorchReader(Properties properties) {
        super(properties, "rtty_reader", null);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos attach = pos.relative(state.getValue(FACING));
        return level.getBlockEntity(attach) instanceof IRORValueProvider;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityRadioTorchReader(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.RADIO_TORCH_READER.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityRadioTorchReader reader) reader.tick();
            };
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        if (!(level.getBlockEntity(pos) instanceof TileEntityRadioTorchReader reader)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new net.minecraft.world.item.ItemStack(this));
        section.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(s -> s.withColor(0xFFFF00)));

        for (int i = 0; i < 8; i++) {
            if (reader.channels[i] == null || reader.channels[i].isEmpty()) continue;
            if (reader.names[i] == null || reader.names[i].isEmpty()) continue;
            section.addLine(Component.literal("§b" + reader.channels[i] + ": " + reader.names[i]));
        }

        sections.add(section);
        return sections;
    }
}
