package com.hbm.blocks.machine;

import com.hbm.blocks.ILookOverlay;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityCondenser;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineCondenser extends BaseEntityBlock implements ILookOverlay {

    public MachineCondenser(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityCondenser(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.CONDENSER.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityCondenser c) c.tick();
            };
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityCondenser condenser)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new net.minecraft.world.item.ItemStack(this));
        section.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(style -> style.withColor(0xFFFF00)));

        for (int i = 0; i < condenser.tanks.length; i++) {
            String arrow = (i < 1) ? "§a-> " : "§c<- ";
            String tankName = condenser.tanks[i].getTankType().getLocalizedName();
            int fill = condenser.tanks[i].getFill();
            int max = condenser.tanks[i].getMaxFill();

            section.addLine(Component.literal(arrow + "§r" + tankName + ": " + fill + "/" + max + "mB"));
        }

        sections.add(section);
        return sections;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }
}