package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.oil.TileEntityMachineFractionTower;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineFractionTower extends BlockDummyable implements ILookOverlay {

    public MachineFractionTower(Properties properties) {
        super(properties);
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityMachineFractionTower(pos, state);
        }
        if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setFluid();
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[] {2, 0, 1, 1, 1, 1};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    protected void fillSpace(LevelAccessor level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, originalPos, corePos, dir);

        makeExtra(level, corePos.offset(1, 0, 0));
        makeExtra(level, corePos.offset(-1, 0, 0));
        makeExtra(level, corePos.offset(0, 0, 1));
        makeExtra(level, corePos.offset(0, 0, -1));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {

        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() || !(held.getItem() instanceof IItemFluidIdentifier)) {
            return InteractionResult.PASS;
        }

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntityMachineFractionTower frac)) return InteractionResult.PASS;

        // Проверяем, что ядро — нижний сегмент (т.е. сверху него не ещё один FractionTower)
        BlockEntity above = level.getBlockEntity(core.above(3));
        if (above instanceof TileEntityMachineFractionTower) {
            player.displayClientMessage(
                    Component.literal("You can only change the type in the bottom segment!")
                            .withStyle(ChatFormatting.RED), false);
        } else {
            FluidTypeHBM type = ((IItemFluidIdentifier) held.getItem())
                    .getType(level, core, held);
            frac.tanks[0].setType(type);
            frac.setChanged();
            player.displayClientMessage(
                    Component.literal("Changed type to ")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(Component.literal(type.getLocalizedName()))
                            .append(Component.literal("!")), false);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.MACHINE_FRACTION_TOWER.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityMachineFractionTower tower) {
                    tower.tick();
                }
            };
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntityMachineFractionTower tower)) return sections;

        List<Component> text = new ArrayList<>();
        for (int i = 0; i < tower.tanks.length; i++) {
            ChatFormatting color = (i == 0) ? ChatFormatting.GREEN : ChatFormatting.RED;
            String arrow = (i == 0) ? "-> " : "<- ";
            text.add(Component.literal(arrow + tower.tanks[i].getTankType().getLocalizedName()
                            + ": " + tower.tanks[i].getFill() + "/" + tower.tanks[i].getMaxFill() + "mB")
                    .withStyle(color));
        }

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        ILookOverlay.printGeneric(section, blockDisplayName(), 0xffff00, text);
        sections.add(section);
        return sections;
    }

    private String blockDisplayName() {
        return net.minecraft.network.chat.Component.translatable(this.getDescriptionId()).getString();
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
}