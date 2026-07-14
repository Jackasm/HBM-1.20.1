package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.pile.TileEntityPileBreedingFuel;
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

public class BlockGraphiteBreedingFuel extends BlockGraphiteDrilledTE implements IToolable {

    public BlockGraphiteBreedingFuel(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPileBreedingFuel(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.PILE_BREEDING_FUEL.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityPileBreedingFuel fuel) {
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
        this.blockIconAluminum = ResLocation(RefStrings.MODID, "textures/block/block_graphite_lithium_aluminum.png");
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
                    ejectItem(level, pos, side, new ItemStack(ModItems.PILE_ROD_LITHIUM.get()));
                }
            }

            if (tool == ToolType.HAND_DRILL) {
                if (level.getBlockEntity(pos) instanceof TileEntityPileBreedingFuel pile) {
                    player.sendSystemMessage(Component.literal("CP1 FUEL ASSEMBLY " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
                            .withStyle(ChatFormatting.GOLD));
                    player.sendSystemMessage(Component.literal("DEPLETION: " + pile.progress + "/" + TileEntityPileBreedingFuel.maxProgress)
                            .withStyle(ChatFormatting.YELLOW));
                    player.sendSystemMessage(Component.literal("FLUX: " + pile.lastNeutrons)
                            .withStyle(ChatFormatting.YELLOW));
                }
            }
        }
        return true;
    }

    @Override
    protected Item getInsertedItem() {
        return ModItems.PILE_ROD_LITHIUM.get();
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }
}