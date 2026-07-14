package com.hbm.blocks.machine;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityMachineAutosaw;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
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
import java.util.Objects;

public class MachineAutosaw extends BaseEntityBlock implements ILookOverlay, ITooltipProvider {

    public MachineAutosaw(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityMachineAutosaw(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.AUTOSAW.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityMachineAutosaw saw) {
                    saw.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof IItemFluidIdentifier identifier) {

            TileEntityMachineAutosaw saw = (TileEntityMachineAutosaw) level.getBlockEntity(pos);

            FluidTypeHBM type = identifier.getType(level, pos, heldItem);
            if (TileEntityMachineAutosaw.acceptedFuels.contains(type)) {
                Objects.requireNonNull(saw).tank.setType(type);
                saw.setChanged();
                player.sendSystemMessage(Component.literal("Changed type to ")
                        .append(Component.translatable(type.getDescriptionId()))
                        .append(Component.literal("!")));
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            addStandardInfo(stack, player, tooltip, flag.isAdvanced());
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static Block.Properties createProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityMachineAutosaw saw)) return sections;

        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        mainSection.setIcon(new ItemStack(this));

        String tankName = Component.translatable(this.getDescriptionId()).getString();
        mainSection.addLine(Component.literal(tankName).withStyle(style -> style.withColor(0xFFD700)));

        // Информация о жидкости
        mainSection.addLine(Component.literal(saw.tank.getTankType().getLocalizedName() + ": " +
                saw.tank.getFill() + "/" + saw.tank.getMaxFill() + " mB"));

        if (saw.isSuspended) {
            mainSection.addLine(Component.literal("§c! " + Component.translatable(this.getDescriptionId() + ".suspended").getString() + " !"));
        }

        sections.add(mainSection);

        return sections;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, Player player, List list, boolean ext) {
        this.addStandardInfo(stack, player, list, ext);
    }
}