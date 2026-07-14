package com.hbm.blocks.machine;

import com.hbm.blocks.ILookOverlay;
import com.hbm.tileentity.machine.TileEntityCapacitor;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class MachineCapacitor extends BaseEntityBlock implements ILookOverlay {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    protected final long power;

    public MachineCapacitor(Properties properties, long power) {
        super(properties);
        this.power = power;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, te) -> {
            if (te instanceof TileEntityCapacitor capacitor) {
                capacitor.tick();
            }
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityCapacitor(pos, state, this.power);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null) {
            Direction dir = Direction.fromYRot(placer.getYRot());
            level.setBlock(pos, state.setValue(FACING, dir), 3);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Stores up to " + BobMathUtil.getShortNumber(this.power) + "HE"));
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Charge speed: " + BobMathUtil.getShortNumber(this.power / 200) + "HE"));
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Discharge speed: " + BobMathUtil.getShortNumber(this.power / 600) + "HE"));

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("power") && tag.contains("maxPower")) {
            tooltip.add(Component.literal(ChatFormatting.YELLOW + BobMathUtil.getShortNumber(tag.getLong("power")) + "/" +
                    BobMathUtil.getShortNumber(tag.getLong("maxPower")) + "HE"));
        }
    }

    public ResourceLocation[] getTextures() {
        String name = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(this)).getPath();

        String base = "textures/block/" + name;

        return new ResourceLocation[]{
                ResLocation(base + "_top.png"),
                ResLocation(base + "_side.png"),
                ResLocation(base + "_bottom.png"),
                ResLocation(base + "_inner_top.png"),
                ResLocation(base + "_inner_side.png")
        };
    }
}