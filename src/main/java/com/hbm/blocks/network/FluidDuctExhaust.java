package com.hbm.blocks.network;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.network.TileEntityPipeExhaust;
import com.hbm.util.Library;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

public class FluidDuctExhaust extends FluidDuctBox {

    public FluidDuctExhaust(Properties properties, FluidDuctBoxMaterial material) {
        super(properties, material);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, blockState, t) -> {
            if (t instanceof TileEntityPipeExhaust pipe) {
                pipe.tick();
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MATERIAL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPipeExhaust(pos, state);
    }

    @Override
    public boolean canConnectTo(BlockGetter world, BlockPos pos, Direction dir, FluidTypeHBM type) {
        return Library.canConnectFluid(world, pos.relative(dir), dir.getOpposite(), type) &&
                (type == Fluids.SMOKE.get() || type == Fluids.SMOKE_LEADED.get() || type == Fluids.SMOKE_POISON.get());
    }

    @Override
    public int getSubCount() {
        return 1;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(ModItems.FLUID_DUCT_EXHAUST.get());
    }

    // Переопределяем методы получения текстур для выхлопной трубы
    @Override
    public ResourceLocation getStraightTexture(BlockState state) {
        return TEXTURE_STRAIGHT_EXHAUST;
    }

    @Override
    public ResourceLocation getEndTexture(BlockState state) {
        return TEXTURE_END_EXHAUST;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);

        mainSection.setIcon(new ItemStack(ModItems.FLUID_DUCT_EXHAUST.get()));

        mainSection.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(style -> style.withColor(0xffff00)));

        mainSection.addLine(Component.literal(Fluids.SMOKE.get().getLocalizedName()));
        mainSection.addLine(Component.literal(Fluids.SMOKE_LEADED.get().getLocalizedName()));
        mainSection.addLine(Component.literal(Fluids.SMOKE_POISON.get().getLocalizedName()));

        sections.add(mainSection);
        return sections;
    }

    // Статические текстуры для выхлопной трубы
    public static final ResourceLocation TEXTURE_STRAIGHT_EXHAUST = ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_exhaust_straight.png");
    public static final ResourceLocation TEXTURE_END_EXHAUST = ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_exhaust_end.png");

}