package com.hbm.blocks.network;

import com.hbm.api.redstoneoverradio.IRORInteractive;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityRadioTorchController;
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
 * RTTY-контроллер: висит только на блоке, который принимает команды
 * ({@link IRORInteractive}) — как в эталоне
 * {@code ref:blocks/network/RadioTorchController.java}.
 */
public class RadioTorchController extends RadioTorchBase {

    public RadioTorchController(Properties properties) {
        super(properties, "rtty_controller", null);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos attach = pos.relative(state.getValue(FACING));
        return level.getBlockEntity(attach) instanceof IRORInteractive;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityRadioTorchController(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.RADIO_TORCH_CONTROLLER.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityRadioTorchController controller) controller.tick();
            };
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        if (!(level.getBlockEntity(pos) instanceof TileEntityRadioTorchController controller)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new net.minecraft.world.item.ItemStack(this));
        section.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(s -> s.withColor(0xFFFF00)));
        section.addLine(Component.translatable("hbm.text.freq", controller.channel));

        sections.add(section);
        return sections;
    }
}
