package com.hbm.blocks.network;

import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityRadioTorchCounter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
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
 * RTTY-счётчик предметов: висит на контейнере и передаёт в канал число
 * подходящих под образец предметов.
 *
 * <p>Порт {@code ref:blocks/network/RadioTorchCounter.java}. В 1.7.10 факел
 * разрешался на любой стене или на инвентаре — здесь то же: опорой считается
 * либо прочное основание, либо соседний {@link Container}.</p>
 */
public class RadioTorchCounter extends RadioTorchBase {

    public RadioTorchCounter(Properties properties) {
        // У счётчика нет состояния on/off: он ничего не излучает, только считает.
        super(properties, "rtty_counter", null);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos attach = pos.relative(state.getValue(FACING));
        if (level.getBlockEntity(attach) instanceof Container) return true;
        return super.canSurvive(state, level, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityRadioTorchCounter(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.RADIO_TORCH_COUNTER.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityRadioTorchCounter counter) counter.tick();
            };
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        if (!(level.getBlockEntity(pos) instanceof TileEntityRadioTorchCounter counter)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new net.minecraft.world.item.ItemStack(this));
        section.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(s -> s.withColor(0xFFFF00)));

        for (int i = 0; i < 3; i++) {
            if (counter.channel[i] == null || counter.channel[i].isEmpty()) continue;
            section.addLine(Component.translatable("hbm.text.freq_2", (i + 1), counter.channel[i]));
            section.addLine(Component.translatable("hbm.text.signal", (i + 1), counter.lastCount[i]));
        }

        sections.add(section);
        return sections;
    }
}
