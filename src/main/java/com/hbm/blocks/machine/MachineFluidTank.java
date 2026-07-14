package com.hbm.blocks.machine;

import com.hbm.blocks.*;
import com.hbm.entity.projectile.EntityBombletZeta;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.IRepairable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.storage.TileEntityMachineFluidTank;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MachineFluidTank extends BlockDummyable implements ILookOverlay {


    private static final VoxelShape SHAPE = Shapes.block();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public MachineFluidTank(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DUMMY_STATE, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(DUMMY_STATE, 0);
    }

    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityMachineFluidTank(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setFluid(); // или аналогичный
        } else {
            return null; // обычные заглушки без TileEntity
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Находим ядро мультиблока
        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(core);
        if (!(blockEntity instanceof TileEntityMachineFluidTank tank)) {
            return InteractionResult.PASS;
        }

        if (tank.hasExploded) {
            return InteractionResult.PASS;
        }

        if (!player.isCrouching()) {
            // Открываем GUI от core-блока
            NetworkHooks.openScreen((ServerPlayer) player, tank, core);
            return InteractionResult.SUCCESS;
        } else {
            // С шифтом - работа с идентификатором жидкости
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty() && stack.getItem() instanceof IItemFluidIdentifier identifier) {
                FluidTypeHBM type = identifier.getType(level, core, stack);
                tank.tank.setType(type);
                tank.setChanged();

                player.sendSystemMessage(
                        Component.literal("Changed type to ")
                                .append(Component.translatable(type.getLocalizedName()))
                                .append(Component.literal("!"))
                );
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.MACHINE_FLUID_TANK.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityMachineFluidTank tank) {
                    tank.tick(); // Вызываем метод tick() из TileEntity
                }
            };
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, net.minecraft.world.item.@NotNull TooltipFlag flag) {
        // Добавляем информацию о жидкости из NBT
        if (stack.hasTag()) {
            FluidTankHBM tank = new FluidTankHBM(Fluids.NONE.get(), 0);
            tank.readFromNBT(stack.getTag(), "tank");
            tooltip.add(Component.literal("§e" + tank.getFill() + "/" + tank.getMaxFill() + "mB " + tank.getTankType().getLocalizedName()));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityMachineFluidTank tank) {
            return tank.getComparatorPower();
        }
        return 0;
    }

    @Override
    public void wasExploded(Level level, @NotNull BlockPos pos, net.minecraft.world.level.@NotNull Explosion explosion) {
        // Обработка взрыва
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TileEntityMachineFluidTank tank) {
                if (tank.lastExplosion == explosion) return;
                tank.lastExplosion = explosion;

                if (!tank.hasExploded) {
                    tank.explode();

                    // Специальная обработка для зета-бомбы
                    if (explosion.getExploder() instanceof EntityBombletZeta) {
                        if (tank.tank.getTankType().getTrait(FT_Flammable.class) == null) return;

                        AABB aabb = new AABB(pos).inflate(100);
                        List<Player> players = level.getEntitiesOfClass(Player.class, aabb);
                        for (Player p : players) {
                            // Достижение
                            // MainRegistry.achInferno.trigger(p);
                        }
                    }
                } else {
                    level.removeBlock(pos, false);
                }
            }
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeForFacing(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeForFacing(state);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return getShapeForFacing(state);
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeForFacing(state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    private VoxelShape getShapeForFacing(BlockState state) {
        return SHAPE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity be = level.getBlockEntity(core);
        if (!(be instanceof TileEntityMachineFluidTank tank)) return sections;

        // Основная секция с информацией о цистерне
        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        mainSection.setIcon(getMachineItem());

        // Название цистерны (уже локализовано через блок)
        String tankName = Component.translatable(this.getDescriptionId()).getString();
        mainSection.addLine(Component.literal(tankName).withStyle(style -> style.withColor(0xFFD700)));

        // Информация о жидкости
        FluidTankHBM fluidTank = tank.tank;
        FluidTypeHBM fluidType = fluidTank.getTankType();
        int fill = fluidTank.getFill();
        int capacity = fluidTank.getMaxFill();
        float percentage = capacity > 0 ? (fill * 100f / capacity) : 0;

        if (fluidType != null && fluidType != Fluids.NONE.get()) {
            // Название жидкости
            mainSection.addLine(Component.translatable("overlay.fluid_tank.fluid_type")
                    .append(Component.literal(": "))
                    .append(fluidType.getLocalizedName()));

            // Количество
            String amount = String.format(Locale.US, "%,d", fill) + " / " +
                    String.format(Locale.US, "%,d", capacity) + " mB";
            mainSection.addLine(Component.translatable("overlay.fluid_tank.amount")
                    .append(Component.literal(": "))
                    .append(Component.literal(amount)));

            // Процент заполнения
            String bar = createBar(percentage, 15);
            int barColor = fluidType.getColor();
            mainSection.addLine(Component.translatable("overlay.fluid_tank.fill")
                    .append(Component.literal(": "))
                    .append(Component.literal(bar).withStyle(style -> style.withColor(barColor))));
        } else {
            mainSection.addLine(Component.translatable("overlay.fluid_tank.empty"));
        }

        // Режим работы
        String modeKey = getMode(tank.mode);
        mainSection.addLine(Component.translatable("overlay.fluid_tank.mode")
                .append(Component.literal(": "))
                .append(Component.translatable(modeKey)));

        sections.add(mainSection);

        // Секция ремонта (если нужна)
        if (be instanceof IRepairable repairable) {
            repairable.addOverlaySections(sections, level, core, this);
        }

        return sections;
    }

    @OnlyIn(Dist.CLIENT)
    private String getMode(short mode) {
        return switch(mode) {
            case 0 -> "overlay.fluid_tank.mode.input";
            case 1 -> "overlay.fluid_tank.mode.buffer";
            case 2 -> "overlay.fluid_tank.mode.output";
            case 3 -> "overlay.fluid_tank.mode.disabled";
            default -> "overlay.fluid_tank.mode.unknown";
        };
    }

    @OnlyIn(Dist.CLIENT)
    private String createBar(float percentage, int length) {
        int filledChars = (int) (percentage / 100 * length);
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < length; i++) {
            bar.append(i < filledChars ? "█" : "░");
        }

        return String.format("%s %.1f%%", bar, percentage);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
    }

    public static Block.Properties createProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 20.0F)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }

    @Override
    protected int getGuiID() {
        return 0; // ID GUI для танка
    }

    @Override
    public int[] getDimensions() {
        return new int[] {2, 0, 1, 1, 2, 2}; // up, down, north, south, west, east
    }

    @Override
    public int getZOffset() {
        return -1; // смещение ядра от места установки
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(PoseStack poseStack, MultiBufferSource bufferSource, Level level, BlockPos pos) {
        // Находим ядро мультиблока
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        BlockState state = level.getBlockState(core);
        Direction rot = state.getValue(FACING);

        // Получаем позицию камеры для правильного смещения
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        // Смещаемся относительно камеры (рендер в мировых координатах)
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        // Получаем консьюмер для линий
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        // Получаем боксы для выделения
        List<AABB> boxes = getHighlightBoxes(level, core, rot);

        // Рендерим каждый бокс
        for (AABB box : boxes) {
            ICustomBlockHighlight.renderAABB(poseStack, consumer, box, 0.0F, 1.0F, 0.0F, 0.4F);
        }

        poseStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        switch(rotation) {
            case NORTH:
                boxes.add(new AABB(-2, 0, -1, 3, 3, 2).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case SOUTH:
                boxes.add(new AABB(-2, 0, -1, 3, 3, 2).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case WEST:
                boxes.add(new AABB(-1, 0, -2, 2, 3, 3).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case EAST:
                boxes.add(new AABB(-1, 0, -2, 2, 3, 3).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
        }
        return boxes;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        int y = corePos.getY();

        for (int dx : new int[]{-1, 1}) {
            for (int dz : new int[]{-1, 1}) {
                BlockPos extraPos = new BlockPos(
                        corePos.getX() + dx,
                        y,
                        corePos.getZ() + dz
                );

                if (level.getBlockState(extraPos).is(this)) {
                    this.makeExtra(level, extraPos);
                }
            }
        }
    }

}