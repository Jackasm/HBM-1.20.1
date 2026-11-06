package com.hbm.blocks.generic;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.IRepairable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.deco.TileEntityLanternBehemoth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockLanternBehemoth extends BlockDummyable implements IToolable, ILookOverlay {

    public BlockLanternBehemoth(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return isCore(state) ? new TileEntityLanternBehemoth(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (!isCore(state)) return null;
        if (type == ModTileEntity.LANTERN_BEHEMOTH.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityLanternBehemoth l) l.tick();
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);
        ToolType tool = ToolType.getType(stack);
        if (tool != ToolType.TORCH) return InteractionResult.PASS;

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        boolean repaired = IRepairable.tryRepairMultiblock(level, core, this, player);
        if (repaired) {
            HbmPlayerProps.IHbmPlayerProps data = HbmPlayerProps.getData(player);
            if (data.getReputation() < 25) {
                data.setReputation(data.getReputation() + 1);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onScrew(Level world, Player player, BlockPos pos, Direction side,
                           float fX, float fY, float fZ, ToolType tool) {
        return tool == ToolType.TORCH;
    }

    @Override
    public int[] getDimensions() {
        return new int[] {4, 0, 0, 0, 0, 0};
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    protected int getGuiID() {
        return -1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new java.util.ArrayList<>();
        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity be = level.getBlockEntity(core);
        if (be instanceof IRepairable repairable) {
            repairable.addOverlaySections(sections, level, core, this);
        }
        return sections;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                .strength(3.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL)
                .noOcclusion()
                .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK);
    }
}