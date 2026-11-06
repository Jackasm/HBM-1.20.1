package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsVendingMachine;
import com.hbm.items.ModItems;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockVendingMachine extends BlockDummyable {

    public enum VendingVariant { SODA, OBAMNA }

    private final VendingVariant variant;

    public BlockVendingMachine(Properties properties, VendingVariant variant) {
        super(properties);
        this.variant = variant;
    }

    public VendingVariant getVariant() { return variant; }

    @Override
    protected int getGuiID() {
        return 0; // GUI нет
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityVendingMachine(pos, state);
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[] { 1, 0, 0, 0, 0, 0 };
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    /** В 1.7.10 делал extra-блоки (верхний слот) после установки. */
    @Override
    protected void fillSpace(LevelAccessor level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, originalPos, corePos, dir);

        // Верхний блок (y+1) — extra, если там стоит такой же блок
        BlockPos above = corePos.above(1);
        if (level.getBlockState(above).getBlock() == this) {
            makeExtra(level, above);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() || held.getItem() != ModItems.COIN_TOKEN.get()) {
            return InteractionResult.PASS;
        }

        if (!player.isCreative()) held.shrink(1);

        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        int dmg = 0;
        BlockPos above = core.above(1);
        if (level.getBlockState(above).getBlock() == this) {
            // Если верхний блок — extra того же блока, значит это высокий автомат (2 блока)
            dmg = 1;
        }

        ItemStack toDrop = null;
        if (this.variant == VendingVariant.SODA) {
            toDrop = ItemPool.getStack(ItemPoolsVendingMachine.POOL_SODA, level.random);
        } else {
            toDrop = ItemPool.getStack(ItemPoolsVendingMachine.POOL_SNACKS, level.random);
        }

        if (toDrop != null) {
            Direction facing = state.getValue(FACING);
            Direction front = facing.getOpposite(); // «лицом» наружу
            double dx = pos.getX() + 0.5 + front.getStepX() * 0.75;
            double dy = core.getY() + 0.25;
            double dz = pos.getZ() + 0.5 + front.getStepZ() * 0.75;
            ItemEntity item = new ItemEntity(level, dx, dy, dz, toDrop);
            level.addFreshEntity(item);
        }

        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                ModSounds.RELOAD_BOLT_OPEN.get(), SoundSource.BLOCKS, 1F, 0.75F);

        return InteractionResult.CONSUME;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    public static class TileEntityVendingMachine extends BlockEntity {

        private AABB renderBox;

        public TileEntityVendingMachine(BlockPos pos, BlockState state) {
            super(ModTileEntity.VENDING_MACHINE.get(), pos, state);
        }

        @Override
        public @NotNull AABB getRenderBoundingBox() {
            if (renderBox == null) {
                renderBox = new AABB(
                        worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        worldPosition.getX() + 1, worldPosition.getY() + 2, worldPosition.getZ() + 1
                );
            }
            return renderBox;
        }
    }
}