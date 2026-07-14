package com.hbm.blocks.network;

import com.hbm.blocks.IBlockMulti;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.util.Library;

import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class FluidDuctStandard extends FluidDuctBase implements IBlockMulti, ILookOverlay {


    public static final Map<Integer, ResourceLocation> TEXTURES = new HashMap<>();
    public static final Map<Integer, ResourceLocation> OVERLAYS = new HashMap<>();

    static {
        TEXTURES.put(0, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct.png"));
        TEXTURES.put(1, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_silver.png"));
        TEXTURES.put(2, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_colored.png"));

        OVERLAYS.put(0, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_overlay.png"));
        OVERLAYS.put(1, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_silver_overlay.png"));
        OVERLAYS.put(2, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_colored_overlay.png"));
    }

    // Свойство для метаданных (тип трубы: 0-обычная, 1-серебряная, 2-цветная)
    public static final IntegerProperty PIPE_TYPE = IntegerProperty.create("pipe_type", 0, 2);


    public FluidDuctStandard(Properties properties, FluidDuctType type) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PIPE_TYPE, type.ordinal()));
    }

    public enum FluidDuctType {
        FLUID_DUCT(0),
        FLUID_DUCT_SILVER(1),
        FLUID_DUCT_COLORED(2);

        private final int id;

        FluidDuctType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null; // если нужен клиентский тик, можно вернуть тикер
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof TileEntityPipeBaseNT pipe) {
                pipe.tick();
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIPE_TYPE);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_49232_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        ItemStack stack = new ItemStack(this);

        if (tileEntity instanceof TileEntityPipeBaseNT pipe) {
            FluidTypeHBM fluidType = pipe.getFluidType();
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt("FluidID", Fluids.getID(fluidType));
        }

        return stack;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityPipeBaseNT pipe) {
            FluidTypeHBM fluidType = Fluids.NONE.get();

            if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("FluidID")) {
                int fluidId = stack.getTag().getInt("FluidID");
                fluidType = Fluids.fromID(fluidId);
            }

            pipe.setFluidType(fluidType);
            pipe.setChanged();
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public int getSubCount() {
        return 3;
    }

    public int rectify(int meta) {
        return Math.abs(meta % getSubCount());
    }

    public boolean canConnectTo(BlockGetter world, BlockPos pos, Direction dir, FluidTypeHBM type) {
        return Library.canConnectFluid(world, pos.relative(dir), dir.getOpposite(), type);
    }

    /**
     * Получает VoxelShape блока в зависимости от его соединений.
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof TileEntityPipeBaseNT pipe)) {
            return Shapes.block();
        }

        FluidTypeHBM type = pipe.getFluidType();

        boolean nX = canConnectTo(world, pos, Direction.WEST, type);
        boolean pX = canConnectTo(world, pos, Direction.EAST, type);
        boolean nY = canConnectTo(world, pos, Direction.DOWN, type);
        boolean pY = canConnectTo(world, pos, Direction.UP, type);
        boolean nZ = canConnectTo(world, pos, Direction.NORTH, type);
        boolean pZ = canConnectTo(world, pos, Direction.SOUTH, type);

        // Маска соединений (порядок: pX, nX, pY, nY, pZ, nZ) - для удобства перебора
        // Можно использовать битовую маску, но для построения VoxelShape будем использовать логику из оригинального setBlockBoundsBasedOnState
        int mask = (pX ? 32 : 0) | (nX ? 16 : 0) | (pY ? 8 : 0) | (nY ? 4 : 0) | (pZ ? 2 : 0) | (nZ ? 1 : 0);

        VoxelShape shape;

        if (mask == 0) {

            shape = Shapes.block();
        } else if ((mask & 0b110000) == mask) {
            shape = Shapes.box(0.0, 0.3125, 0.3125, 1.0, 0.6875, 0.6875);
        } else if ((mask & 0b001100) == mask) {
            shape = Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 1.0, 0.6875);
        } else if ((mask & 0b000011) == mask) {
            shape = Shapes.box(0.3125, 0.3125, 0.0, 0.6875, 0.6875, 1.0);
        } else {
            shape = Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);

            if (pX) shape = Shapes.or(shape, Shapes.box(0.6875, 0.3125, 0.3125, 1.0, 0.6875, 0.6875));
            if (nX) shape = Shapes.or(shape, Shapes.box(0.0, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875));
            if (pY) shape = Shapes.or(shape, Shapes.box(0.3125, 0.6875, 0.3125, 0.6875, 1.0, 0.6875));
            if (nY) shape = Shapes.or(shape, Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 0.3125, 0.6875));
            if (pZ) shape = Shapes.or(shape, Shapes.box(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1.0));
            if (nZ) shape = Shapes.or(shape, Shapes.box(0.3125, 0.3125, 0.0, 0.6875, 0.6875, 0.3125));
        }

        return shape.optimize();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityPipeBaseNT duct)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new ItemStack(this));

        String pipeName = Component.translatable(this.getDescriptionId()).getString();
        section.addLine(Component.literal(pipeName).withStyle(style -> style.withColor(0xFFD700)));

        // Информация о жидкости
        FluidTypeHBM fluidType = duct.getFluidType();
        if (fluidType != null && fluidType != Fluids.NONE.get()) {
            int fluidColor = fluidType.getColor();

            // Название жидкости с цветом
            Component fluidName = Component.literal(fluidType.getLocalizedName())
                    .withStyle(style -> style.withColor(fluidColor));
            section.addLine(Component.translatable("overlay.pipe.fluid_type")
                    .append(Component.literal(": "))
                    .append(fluidName));


        } else {
            section.addLine(Component.translatable("overlay.pipe.empty"));
        }

        sections.add(section);
        return sections;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }
}