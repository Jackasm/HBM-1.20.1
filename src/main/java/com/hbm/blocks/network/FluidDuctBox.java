package com.hbm.blocks.network;

import com.hbm.blocks.IBlockMulti;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;


import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;

import com.hbm.tileentity.network.TileEntityPipeBoxNT;
import com.hbm.util.Library;
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

public class FluidDuctBox extends FluidDuctBase implements IBlockMulti, ILookOverlay {

    // Текстуры для разных материалов и конфигураций
    public static final Map<Integer, ResourceLocation> TEXTURE_STRAIGHT = new HashMap<>();
    public static final Map<Integer, ResourceLocation> TEXTURE_END = new HashMap<>();


    private static final String[] materials = new String[]{"silver", "copper", "white"};

    static {
        for (int i = 0; i < materials.length; i++) {
            TEXTURE_STRAIGHT.put(i, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_box_" + materials[i] + "_straight.png"));
            TEXTURE_END.put(i, ResLocation(RefStrings.MODID, "textures/block/storage/fluid_duct_box_" + materials[i] + "_end.png"));
        }
    }

    public enum FluidDuctBoxMaterial
    {
        SILVER,
        COPPER,
        WHITE
    }

    // Свойство для материала (0-2)
    public static final IntegerProperty MATERIAL = IntegerProperty.create("material", 0, 2);

    public FluidDuctBox(Properties properties, FluidDuctBoxMaterial material) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MATERIAL, material.ordinal()));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPipeBoxNT(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MATERIAL);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED; // Используем кастомный рендер
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
        int material = state.getValue(MATERIAL);
        ItemStack stack = switch (material) {
            case 1 -> new ItemStack(ModItems.FLUID_DUCT_SILVER.get());
            case 2 -> new ItemStack(ModItems.FLUID_DUCT_COLORED.get());
            default -> new ItemStack(ModItems.FLUID_DUCT.get());
        };

        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityPipeBaseNT pipe) {
            FluidTypeHBM fluidType = pipe.getFluidType();
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt("FluidID", Fluids.getID(fluidType));
        }

        return stack;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        // Тип материала определяется самим предметом
        return this.defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level world, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
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
        return materials.length;
    }

    public int rectify(int meta) {
        return Math.abs(meta % getSubCount());
    }

    public boolean canConnectTo(BlockGetter world, BlockPos pos, Direction dir, FluidTypeHBM type) {
        return Library.canConnectFluid(world, pos.relative(dir), dir.getOpposite(), type);
    }

    public ResourceLocation getStraightTexture(BlockState state) {
        int material = state.getValue(MATERIAL);
        return TEXTURE_STRAIGHT.getOrDefault(material, TEXTURE_STRAIGHT.get(0));
    }

    public ResourceLocation getEndTexture(BlockState state) {
        int material = state.getValue(MATERIAL);
        return TEXTURE_END.getOrDefault(material, TEXTURE_END.get(0));
    }

    // ========== КОЛЛИЗИИ ==========

    private double getLower(int meta) {
        double lower = 0.125D;
        for (int i = 2; i < 13; i += 3) {
            if (meta > i) lower += 0.0625D;
        }
        return lower;
    }

    private double getUpper(int meta) {
        double upper = 0.875D;
        for (int i = 2; i < 13; i += 3) {
            if (meta > i) upper -= 0.0625D;
        }
        return upper;
    }

    private double getJLower(int meta) {
        double jLower = 0.0625D;
        for (int i = 2; i < 13; i += 3) {
            if (meta > i) jLower += 0.0625D;
        }
        return jLower;
    }

    private double getJUpper(int meta) {
        double jUpper = 0.9375D;
        for (int i = 2; i < 13; i += 3) {
            if (meta > i) jUpper -= 0.0625D;
        }
        return jUpper;
    }

    private int getConnectionMask(BlockGetter world, BlockPos pos, FluidTypeHBM type) {
        boolean nX = canConnectTo(world, pos, Direction.WEST, type);
        boolean pX = canConnectTo(world, pos, Direction.EAST, type);
        boolean nY = canConnectTo(world, pos, Direction.DOWN, type);
        boolean pY = canConnectTo(world, pos, Direction.UP, type);
        boolean nZ = canConnectTo(world, pos, Direction.NORTH, type);
        boolean pZ = canConnectTo(world, pos, Direction.SOUTH, type);

        return (pX ? 32 : 0) | (nX ? 16 : 0) | (pY ? 8 : 0) | (nY ? 4 : 0) | (pZ ? 2 : 0) | (nZ ? 1 : 0);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof TileEntityPipeBaseNT pipe)) {
            return Shapes.block();
        }

        FluidTypeHBM type = pipe.getFluidType();
        int mask = getConnectionMask(world, pos, type);
        int count = Integer.bitCount(mask & 0x3F);
        int meta = state.getValue(MATERIAL) * 5; // Упрощённо, в реальности метаданные сложнее

        double lower = getLower(meta);
        double upper = getUpper(meta);
        double jLower = getJLower(meta);
        double jUpper = getJUpper(meta);

        VoxelShape shape = Shapes.empty();

        if (mask == 0) {
            shape = Shapes.box(jLower, jLower, jLower, jUpper, jUpper, jUpper);
        } else if ((mask & 0b110000) != 0 && (mask & 0b001111) == 0) {
            shape = Shapes.box(0.0, lower, lower, 1.0, upper, upper);
        } else if ((mask & 0b001100) != 0 && (mask & 0b110011) == 0) {
            shape = Shapes.box(lower, 0.0, lower, upper, 1.0, upper);
        } else if ((mask & 0b000011) != 0 && (mask & 0b111100) == 0) {
            shape = Shapes.box(lower, lower, 0.0, upper, upper, 1.0);
        } else {
            if (count != 2) {
                shape = Shapes.box(jLower, jLower, jLower, jUpper, jUpper, jUpper);
            } else {
                shape = Shapes.box(lower, lower, lower, upper, upper, upper);
            }

            // Добавляем ответвления
            VoxelShape connections = Shapes.empty();

            if ((mask & 32) != 0) connections = Shapes.or(connections, Shapes.box(upper, lower, lower, 1.0, upper, upper));
            if ((mask & 16) != 0) connections = Shapes.or(connections, Shapes.box(0.0, lower, lower, lower, upper, upper));
            if ((mask & 8) != 0) connections = Shapes.or(connections, Shapes.box(lower, upper, lower, upper, 1.0, upper));
            if ((mask & 4) != 0) connections = Shapes.or(connections, Shapes.box(lower, 0.0, lower, upper, lower, upper));
            if ((mask & 2) != 0) connections = Shapes.or(connections, Shapes.box(lower, lower, upper, upper, upper, 1.0));
            if ((mask & 1) != 0) connections = Shapes.or(connections, Shapes.box(lower, lower, 0.0, upper, upper, lower));

            shape = Shapes.or(shape, connections);
        }

        return shape.optimize();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    // ========== ТИКЕР ==========

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, blockState, t) -> {
            if (t instanceof TileEntityPipeBaseNT pipe) {
                pipe.tick();
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityPipeBaseNT duct)) return sections;

        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);

        FluidTypeHBM fluidType = duct.getFluidType();
        int fluidColor = fluidType.getColor();

        int material = level.getBlockState(pos).getValue(FluidDuctBox.MATERIAL);
        ItemStack icon = switch (material) {
            case 0 -> new ItemStack(ModItems.FLUID_DUCT_BOX_SILVER.get());
            case 1 -> new ItemStack(ModItems.FLUID_DUCT_BOX_COPPER.get());
            case 2 -> new ItemStack(ModItems.FLUID_DUCT_BOX_WHITE.get());
            default -> new ItemStack(ModItems.FLUID_DUCT_BOX_SILVER.get());
        };
        mainSection.setIcon(icon);

        mainSection.addLine(Component.translatable(this.getDescriptionId())
                .withStyle(style -> style.withColor(0xffff00)));

        mainSection.addLine(Component.literal(fluidType.getLocalizedName())
                .withStyle(style -> style.withColor(fluidColor)));

        sections.add(mainSection);
        return sections;
    }

    public int getMaterialFromBlock(Block block) {
        if (block == ModBlocks.FLUID_DUCT_BOX_SILVER.get()) return 0;
        if (block == ModBlocks.FLUID_DUCT_BOX_COPPER.get()) return 1;
        if (block == ModBlocks.FLUID_DUCT_BOX_WHITE.get()) return 2;
        return 0;
    }

    public ResourceLocation getStraightTexture(int material) {
        return TEXTURE_STRAIGHT.getOrDefault(material, TEXTURE_STRAIGHT.get(0));
    }

    public ResourceLocation getEndTexture(int material) {
        return TEXTURE_END.getOrDefault(material, TEXTURE_END.get(0));
    }

}