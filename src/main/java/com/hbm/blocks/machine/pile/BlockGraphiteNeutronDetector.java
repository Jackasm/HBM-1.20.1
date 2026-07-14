package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;

import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.pile.TileEntityPileNeutronDetector;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class BlockGraphiteNeutronDetector extends BlockGraphiteDrilledTE implements IToolable {

    @OnlyIn(Dist.CLIENT)
    protected ResourceLocation outIcon;
    @OnlyIn(Dist.CLIENT)
    protected ResourceLocation outIconAluminum;

    public BlockGraphiteNeutronDetector(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPileNeutronDetector(pos, state);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initTextures() {
        super.initTextures();
        this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_detector_aluminum.png");
        this.outIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_detector_out_aluminum.png");
        this.outIcon = ResLocation(RefStrings.MODID, "textures/block/block_graphite_detector_out.png");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIcon(int side, int metadata) {
        int cfg = metadata & 3;

        if (side == cfg * 2 || side == cfg * 2 + 1) {
            if ((metadata & 4) == 4) {
                return ((metadata & 8) > 0) ? this.outIconAluminum : this.blockIconAluminum;
            }
            return ((metadata & 8) > 0) ? this.outIcon : this.blockIcon;
        }

        return this.sideIcon;
    }

    public void triggerRods(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        int oldMeta = getMeta(state);
        int newMeta = oldMeta ^ 8;
        int pureMeta = oldMeta & 3;

        level.setBlock(pos, state, 3);

        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.BLOCKS, 0.02F, 1.0F);

        Direction dir = Direction.from3DDataValue(pureMeta * 2);

        for (int i = -1; i <= 1; i += 1) {
            if (i == 0) continue;

            int ix = pos.getX() + dir.getStepX() * i;
            int iy = pos.getY() + dir.getStepY() * i;
            int iz = pos.getZ() + dir.getStepZ() * i;
            BlockPos checkPos = new BlockPos(ix, iy, iz);

            while (level.getBlockState(checkPos).getBlock() == ModBlocks.BLOCK_GRAPHITE_ROD.get() &&
                    getMeta(level.getBlockState(checkPos)) == oldMeta) {
                level.setBlock(checkPos, state, 3);

                ix += dir.getStepX() * i;
                iy += dir.getStepY() * i;
                iz += dir.getStepZ() * i;
                checkPos = new BlockPos(ix, iy, iz);
            }
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.PILE_NEUTRON_DETECTOR.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityPileNeutronDetector detector) {
                    detector.tick();
                }
            };
        }
        return null;
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, ToolType tool) {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            int meta = getMeta(state);
            int cfg = meta & 3;

            if (tool == ToolType.SCREWDRIVER) {
                if (!player.isShiftKeyDown()) {
                    if (side.get3DDataValue() == cfg * 2 || side.get3DDataValue() == cfg * 2 + 1) {
                        level.setBlock(pos, ModBlocks.BLOCK_GRAPHITE_DRILLED.get().defaultBlockState(), 3);
                        ejectItem(level, pos, side, new ItemStack(getInsertedItem()));
                    }
                } else {
                    if (level.getBlockEntity(pos) instanceof TileEntityPileNeutronDetector pile) {
                        player.sendSystemMessage(Component.literal("CP1 FUEL ASSEMBLY " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
                                .withStyle(ChatFormatting.GOLD));
                        player.sendSystemMessage(Component.literal("FLUX: " + pile.lastNeutrons + "/" + pile.maxNeutrons)
                                .withStyle(ChatFormatting.YELLOW));
                    }
                }
            }

            if (tool == ToolType.DEFUSER) {
                if (level.getBlockEntity(pos) instanceof TileEntityPileNeutronDetector pile) {
                    if (player.isShiftKeyDown()) {
                        if (pile.maxNeutrons > 1) {
                            pile.maxNeutrons--;
                        }
                    } else {
                        pile.maxNeutrons++;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected Item getInsertedItem() {
        return ModItems.PILE_ROD_DETECTOR.get();
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }
}