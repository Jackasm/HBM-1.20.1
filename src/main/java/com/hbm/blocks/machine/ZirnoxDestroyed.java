package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityZirnoxDestroyed;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ZirnoxDestroyed extends BlockDummyable {

    public ZirnoxDestroyed(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DUMMY_STATE, FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityZirnoxDestroyed(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setInventory().setPower().setFluid();
        }
        return null;
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, BlockPos pos, @NotNull RandomSource random) {
        Block blockAbove = level.getBlockState(pos.above()).getBlock();

        if (blockAbove == net.minecraft.world.level.block.Blocks.AIR) {
            if (random.nextInt(10) == 0) {
                level.setBlock(pos.above(), ModBlocks.GAS_MELTDOWN.get().defaultBlockState(), 3);
            }
        } else if (blockAbove == ModBlocks.FOAM_LAYER.get() || blockAbove == ModBlocks.BLOCK_FOAM.get()) {
            if (random.nextInt(25) == 0) {
                BlockPos core = findCore(level, pos);
                if (core != null) {
                    BlockEntity te = level.getBlockEntity(core);
                    if (te instanceof TileEntityZirnoxDestroyed destroyed) {
                        destroyed.onFire = false;
                    }
                }
            }
        }

        if (random.nextInt(10) == 0 && level.getBlockState(pos.above()).getBlock() == net.minecraft.world.level.block.Blocks.AIR) {
            level.setBlock(pos.above(), ModBlocks.GAS_MELTDOWN.get().defaultBlockState(), 3);
        }

        level.scheduleTick(pos, this, getTickRate(level, pos));
    }

    public int getTickRate(Level level, BlockPos pos) {
        return 100 + level.random.nextInt(20);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide && level.random.nextInt(4) == 0) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "rbmkflame");
            data.putInt("maxAge", 90);
            PacketDispatcher.sendAuxParticleNT(data,
                    pos.getX() + 0.25 + level.random.nextDouble() * 0.5,
                    pos.getY() + 1.75,
                    pos.getZ() + 0.25 + level.random.nextDouble() * 0.5,
                    level,
                    pos
            );

            level.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                    1.0F + level.random.nextFloat(),
                    level.random.nextFloat() * 0.7F + 0.3F
            );
        }

        level.scheduleTick(pos, this, getTickRate(level, pos));
    }

    @Override
    public void wasExploded(@NotNull Level level, @NotNull BlockPos pos, net.minecraft.world.level.@NotNull Explosion explosion) {
        // Ничего не делаем
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Проверяем, что это ядро мультиблока, чтобы не дублировать дроп
            if (isCore(state)) {
                // Дропаем предметы
                List<ItemStack> drops = getDrops();
                for (ItemStack stack : drops) {
                    popResource(level, pos, stack);
                }
            }

            // Вызываем супер, чтобы удалить TileEntity
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    private List<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(ModBlocks.CONCRETE_SMOOTH.get(), 6));
        drops.add(new ItemStack(ModItems.PIPE_STEEL.get(), 4));
        drops.add(new ItemStack(ModBlocks.STEEL_GRATE.get(), 2));
        drops.add(new ItemStack(ModItems.DEBRIS_METAL.get(), 6));
        drops.add(new ItemStack(ModItems.DEBRIS_GRAPHITE.get(), 2));
        drops.add(new ItemStack(ModItems.FALLOUT.get(), 4));
        return drops;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(100.0F, 800.0F)
                .noOcclusion()
                .noLootTable()
                .requiresCorrectToolForDrops();
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public int[] getDimensions() {
        return new int[] {1, 0, 2, 2, 2, 2};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
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
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        // Получаем боксы для выделения
        List<AABB> boxes = getHighlightBoxes(level, core, rot);

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

        int[] dim = getDimensions();
        int o = getZOffset();

        double minX = -dim[4];
        double maxX = dim[5] + 1;
        double minY = -dim[1];
        double maxY = dim[0] + 1;
        double minZ = -dim[2];
        double maxZ = dim[3] + 1;

        // Для ZirnoxDestroyed размеры: {1, 0, 2, 2, 2, 2}
        // minY=0, maxY=2, minZ=-2, maxZ=3

        switch (rotation) {
            case WEST, EAST -> boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(corePos.getX(), corePos.getY(), corePos.getZ()));
            default -> boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
        }

        return boxes;
    }
}