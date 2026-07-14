package com.hbm.blocks.bomb;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.MultiblockUtil;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.grenade.EntityGrenadeZOMG;
import com.hbm.entity.logic.EntityBalefire;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.projectile.EntityFallingNuke;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.interfaces.IBomb;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NukeCustom extends BlockDummyable implements IBomb {

    private static final VoxelShape SHAPE = Shapes.block();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static final int maxTnt = 150;
    public static final int maxNuke = 200;
    public static final int maxHydro = 350;
    public static final int maxAmat = 350;
    public static final int maxSchrab = 250;

    private static final boolean keepInventory = false;
    private static final Random RANDOM = new Random();

    public NukeCustom(Properties properties) {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityNukeCustom(pos, state);
        }
        return null;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!keepInventory) {
                BlockPos core = findCore(level, pos);
                if (core != null) {
                    BlockEntity blockEntity = level.getBlockEntity(core);
                    if (blockEntity instanceof TileEntityNukeCustom tile) {
                        IItemHandler handler = tile.getItemHandler();
                        for (int i = 0; i < handler.getSlots(); ++i) {
                            ItemStack stack = handler.getStackInSlot(i);
                            if (!stack.isEmpty()) {
                                float f = RANDOM.nextFloat() * 0.8F + 0.1F;
                                float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
                                float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;
                                while (!stack.isEmpty()) {
                                    int j1 = RANDOM.nextInt(21) + 10;
                                    if (j1 > stack.getCount()) {
                                        j1 = stack.getCount();
                                    }
                                    ItemStack dropStack = stack.split(j1);
                                    ItemEntity entityItem = new ItemEntity(level,
                                            core.getX() + f,
                                            core.getY() + f1,
                                            core.getZ() + f2,
                                            dropStack);
                                    float f3 = 0.05F;
                                    entityItem.setDeltaMovement(
                                            RANDOM.nextGaussian() * f3,
                                            RANDOM.nextGaussian() * f3 + 0.2F,
                                            RANDOM.nextGaussian() * f3
                                    );
                                    level.addFreshEntity(entityItem);
                                }
                            }
                        }
                    }
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            BlockPos core = findCore(level, pos);
            if (core == null) return InteractionResult.PASS;
            BlockEntity blockEntity = level.getBlockEntity(core);
            if (blockEntity instanceof TileEntityNukeCustom tile) {
                NetworkHooks.openScreen((ServerPlayer) player, tile, core);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.NUKE_CUSTOM.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityNukeCustom tile && !level1.isClientSide) {
                    tile.tick();
                }
            };
        }
        return null;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        BlockPos core = findCore(level, pos);
        if (core != null && level.hasNeighborSignal(core)) {
            this.explode(level, core);
        }
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (!(placer instanceof Player player)) return;
        if (level.isClientSide) return;

        // Удаляем временный блок
        level.removeBlock(pos, false);

        int yaw = Math.floorMod((int) player.getYRot(), 360);
        Direction dir = Direction.fromYRot(yaw);
        // При необходимости можно ограничить направления: например, только горизонтальные
        if (dir.getAxis() == Direction.Axis.Y) dir = Direction.NORTH;

        BlockPos corePos = pos.relative(dir, -getZOffset());

        if (!checkRequirement(level, pos, corePos, dir)) {
            if (!player.isCreative()) {
                player.getInventory().add(new ItemStack(this));
            }
            return;
        }

        // Устанавливаем ядро
        BlockState coreState = getCoreState(dir);
        level.setBlock(corePos, coreState, 3);
        // Восстанавливаем NBT из предмета
        if (stack.hasTag()) {
            BlockEntity be = level.getBlockEntity(corePos);
            if (be instanceof TileEntityNukeCustom tile) {
                tile.load(Objects.requireNonNull(stack.getTag()));
            }
        }

        // Заполняем пространство заглушками
        fillSpace(level, pos, corePos, dir);
    }

    @Override
    protected boolean checkRequirement(Level level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        int[] dim = getDimensions();
        int[] rotated = MultiblockUtil.rotate(dim, dir);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -rotated[4]; x <= rotated[5]; x++) {
            for (int y = -rotated[1]; y <= rotated[0]; y++) {
                for (int z = -rotated[2]; z <= rotated[3]; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    mutable.set(corePos.getX() + x, corePos.getY() + y, corePos.getZ() + z);
                    if (!level.getBlockState(mutable).canBeReplaced()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void fillSpace(Level level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        // Сначала вызываем родительский метод для заполнения всех блоков
        super.fillSpace(level, originalPos, corePos, dir);

        // Теперь исправляем направления для боковых блоков, чтобы они указывали на ядро
        // Для бомбы размеры: west=1, east=1 (только лево и право)
        BlockPos westPos = corePos.west();
        BlockPos eastPos = corePos.east();

        BlockState westState = level.getBlockState(westPos);
        if (westState.is(this) && isDummy(westState)) {
            level.setBlock(westPos, westState.setValue(FACING, Direction.EAST), 3);
        }

        BlockState eastState = level.getBlockState(eastPos);
        if (eastState.is(this) && isDummy(eastState)) {
            level.setBlock(eastPos, eastState.setValue(FACING, Direction.WEST), 3);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (level.isClientSide) return BombReturnCode.UNDEFINED;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileEntityNukeCustom tile) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            if (!tile.isFalling()) {
                // Очищаем инвентарь через ItemStackHandler
                IItemHandler handler = tile.getItemHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    handler.extractItem(i, handler.getStackInSlot(i).getCount(), false);
                }
                level.removeBlock(pos, false);
                explodeCustom(level, x, y, z,
                        tile.tnt, tile.nuke, tile.hydro, tile.amat, tile.dirty, tile.schrab, tile.euph);
                return BombReturnCode.DETONATED;
            } else {
                EntityFallingNuke bomb = new EntityFallingNuke(ModEntities.FALLING_NUKE.get(), level,
                        tile.tnt, tile.nuke, tile.hydro, tile.amat, tile.dirty, tile.schrab, tile.euph);
                bomb.setPos(x, pos.getY(), z);
                BlockState state = level.getBlockState(pos);
                bomb.setMeta((byte) state.getValue(FACING).get3DDataValue());
                // Очищаем инвентарь
                IItemHandler handler = tile.getItemHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    handler.extractItem(i, handler.getStackInSlot(i).getCount(), false);
                }
                level.removeBlock(pos, false);
                level.addFreshEntity(bomb);
                return BombReturnCode.TRIGGERED;
            }
        }
        return BombReturnCode.UNDEFINED;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public int[] getDimensions() {
        // Размеры: ширина 3 блока (влево 1, вправо 1), высота 1, глубина 1
        // Формат: {up, down, north, south, west, east}
        return new int[] {0, 0, 0, 0, 1, 1};
    }

    @Override
    public int getZOffset() {
        return 0; // ядро на месте установки
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(PoseStack poseStack, MultiBufferSource bufferSource, Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        BlockState state = level.getBlockState(core);
        Direction rot = state.getValue(FACING);

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        List<AABB> boxes = getHighlightBoxes(core, rot);

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
    public List<AABB> getHighlightBoxes(BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = getDimensions(); // {up, down, north, south, west, east}
        // Для бомбы 3 блока по горизонтали: dim[4]=1 (west), dim[5]=1 (east)

        double minX = -dim[4]; // -1
        double maxX = dim[5] + 1; // 2
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 1 (если высота 1)
        double minZ = -dim[2]; // 0 (если north=0)
        double maxZ = dim[3] + 1; // 1 (если south=0)

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        switch (rotation) {
            case SOUTH:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case WEST, EAST:
                boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case NORTH:
            default:
                boxes.add(box.move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
        }

        return boxes;
    }

    public static void explodeCustom(Level level, double x, double y, double z,
                                     float tnt, float nuke, float hydro, float amat,
                                     float dirty, float schrab, float euph) {
        dirty = Math.min(dirty, 100);
        if (euph > 0) {
            EntityGrenadeZOMG zomg = new EntityGrenadeZOMG(ModEntities.GRENADE_ZOMG.get(), level);
            zomg.setPos(x, y, z);
            ExplosionChaos.zomgMeSinPi(level, x, y, z, 1000, null, zomg);
            level.addFreshEntity(zomg);
        } else if (schrab > 0) {
            schrab += amat / 2 + hydro / 4 + nuke / 8 + tnt / 16;
            schrab = Math.min(schrab, maxSchrab);
            EntityNukeExplosionMK5 ex = EntityNukeExplosionMK5.statFac(level, (int) schrab, x, y, z);
            level.addFreshEntity(ex);
            EntityCloudFleija cloud = new EntityCloudFleija(ModEntities.CLOUD_FLEIJA.get(), level, (int) schrab);
            cloud.setPos(x, y, z);
            level.addFreshEntity(cloud);
        } else if (amat > 0) {
            amat += hydro / 2 + nuke / 4 + tnt / 8;
            amat = Math.min(amat, maxAmat);
            EntityBalefire bf = new EntityBalefire(ModEntities.BALEFIRE.get(), level);
            bf.setPos(x, y, z);
            bf.destructionRange = (int) amat;
            level.addFreshEntity(bf);
            EntityNukeTorex.statFacBale(level, x, y + 5, z, amat);
        } else if (hydro > 0) {
            hydro += nuke / 2 + tnt / 4;
            hydro = Math.min(hydro, maxHydro);
            dirty *= 0.25F;
            EntityNukeExplosionMK5 ex = EntityNukeExplosionMK5.statFac(level, (int) hydro, x, y, z).moreFallout((int) dirty);
            level.addFreshEntity(ex);
            EntityNukeTorex.statFacStandard(level, x, y + 5, z, hydro);
        } else if (nuke > 0) {
            nuke += tnt / 2;
            nuke = Math.min(nuke, maxNuke);
            EntityNukeExplosionMK5 ex = EntityNukeExplosionMK5.statFac(level, (int) nuke, x, y, z).moreFallout((int) dirty);
            level.addFreshEntity(ex);
            EntityNukeTorex.statFacStandard(level, x, y + 5, z, nuke);
        } else if (tnt >= 75) {
            tnt = Math.min(tnt, maxTnt);
            EntityNukeExplosionMK5 ex = EntityNukeExplosionMK5.statFacNoRad(level, (int) tnt, x, y, z);
            level.addFreshEntity(ex);
            EntityNukeTorex.statFacStandard(level, x, y + 5, z, tnt);
        } else if (tnt > 0) {
            ExplosionLarge.explode(level, x, y, z, tnt, true, true, true);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    public static Block.Properties createProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
}