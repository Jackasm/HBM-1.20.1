package com.hbm.blocks.machine;

import com.hbm.blocks.ILookOverlay;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityMachineTeleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MachineTeleporter extends BaseEntityBlock implements ILookOverlay {

    public MachineTeleporter(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityMachineTeleporter(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.TELEPORTER.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityMachineTeleporter teleporter) {
                    teleporter.tick();
                }
            };
        }
        return null;
    }

    @Override
    public void printHook(RenderGuiOverlayEvent.Pre event, Level level, BlockPos pos) {
        if (level == null) return;

        BlockEntity tile = level.getBlockEntity(pos);
        if (!(tile instanceof TileEntityMachineTeleporter tele)) return;

        List<Component> text = new ArrayList<>();

        if (tele.targetY == -1) {
            text.add(Component.literal("No destination set!").withStyle(style -> style.withColor(0xFF0000)));
        } else {
            boolean hasPower = tele.power >= TileEntityMachineTeleporter.CONSUMPTION;
            text.add(Component.literal(
                    (hasPower ? "§a" : "§c") +
                            String.format(Locale.US, "%,d", tele.power) + " / " +
                            String.format(Locale.US, "%,d", TileEntityMachineTeleporter.MAX_POWER)
            ));
            text.add(Component.literal("Destination: " + tele.targetX + " / " + tele.targetY + " / " + tele.targetZ + " (D: " + tele.targetDim + ")"));
        }

        ILookOverlay.printGeneric(event,
                Component.translatable(this.getDescriptionId() + ".name").getString(),
                0xffff00, 0x404000, text);
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
}