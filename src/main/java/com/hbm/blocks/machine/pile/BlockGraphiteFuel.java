package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IBlowable;
import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;

import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.pile.TileEntityPileFuel;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

public class BlockGraphiteFuel extends BlockGraphiteDrilledTE implements IToolable, IBlowable {

    public BlockGraphiteFuel(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        int meta = getMeta(state);
        TileEntityPileFuel pile = new TileEntityPileFuel(pos, state);
        if ((meta & 8) != 0) {
            pile.progress = TileEntityPileFuel.maxProgress - 1000;
        }
        return pile;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.PILE_FUEL.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityPileFuel fuel) {
                    fuel.tick();
                }
            };
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initTextures() {
        super.initTextures();
        this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_fuel_aluminum.png");
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TileEntityPileFuel pile) {
            return (int) Math.min((pile.progress * 15.0F) / (TileEntityPileFuel.maxProgress - 1000), 15);
        }
        return 0;
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, ToolType tool) {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            int meta = getMeta(state);

            if (tool == ToolType.SCREWDRIVER) {
                int cfg = meta & 3;
                if (side.get3DDataValue() == cfg * 2 || side.get3DDataValue() == cfg * 2 + 1) {
                    level.setBlock(pos, ModBlocks.BLOCK_GRAPHITE_DRILLED.get().defaultBlockState(), 3);
                    ejectItem(level, pos, side, new ItemStack(getInsertedItem(meta)));
                }
            }

            if (tool == ToolType.HAND_DRILL) {
                if (level.getBlockEntity(pos) instanceof TileEntityPileFuel pile) {
                    player.sendSystemMessage(Component.literal("CP1 FUEL ASSEMBLY " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
                            .withStyle(ChatFormatting.GOLD));
                    player.sendSystemMessage(Component.literal("HEAT: " + pile.heat + "/" + TileEntityPileFuel.maxHeat)
                            .withStyle(ChatFormatting.YELLOW));
                    player.sendSystemMessage(Component.literal("DEPLETION: " + pile.progress + "/" + TileEntityPileFuel.maxProgress)
                            .withStyle(ChatFormatting.YELLOW));
                    player.sendSystemMessage(Component.literal("FLUX: " + pile.lastNeutrons)
                            .withStyle(ChatFormatting.YELLOW));
                    if ((meta & 8) == 8) {
                        player.sendSystemMessage(Component.literal("PU-239 RICH")
                                .withStyle(ChatFormatting.DARK_GREEN));
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected Item getInsertedItem(int meta) {
        return (meta & 8) == 8 ? ModItems.PILE_ROD_PU239.get() : ModItems.PILE_ROD_URANIUM.get();
    }

    @Override
    public void applyFan(Level level, BlockPos pos, Direction dir, int dist) {
        if (level.getBlockEntity(pos) instanceof TileEntityPileFuel pile) {
            pile.heat -= pile.heat * 0.025;
        }
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }
}