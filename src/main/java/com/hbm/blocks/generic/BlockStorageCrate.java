package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemLock;
import com.hbm.tileentity.storage.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockStorageCrate extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockStorageCrate(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public static Properties createPropertiesMetal() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    public static Properties createPropertiesTungsten() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(7.5F, 300.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    public static Properties createPropertiesSafe() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(7.5F, 10000.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        ItemStack heldItem = player.getItemInHand(hand);

        // НЕ открывать GUI, если держим замок/набор ключей
        if (!heldItem.isEmpty()
                && (heldItem.getItem() instanceof ItemLock
                || heldItem.getItem() == ModItems.KEY_KIT.get())) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityCrateBase crate) {
            if (crate.canAccess(player)) {
                NetworkHooks.openScreen((ServerPlayer) player, crate, pos);
                return InteractionResult.CONSUME;
            } else {
                // Заперто — звук
                level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) return;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityCrateBase crate)) return;

        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();

            // Восстанавливаем содержимое
            if (Objects.requireNonNull(nbt).contains("crate_items")) {
                CompoundTag itemsTag = nbt.getCompound("crate_items");
                for (int i = 0; i < crate.getContainerSize(); i++) {
                    if (itemsTag.contains("slot" + i)) {
                        crate.setItem(i, ItemStack.of(itemsTag.getCompound("slot" + i)));
                    }
                }
            }

            // Восстанавливаем замок
            if (nbt.contains("crate_lock")) {
                CompoundTag lockTag = nbt.getCompound("crate_lock");
                crate.setPins(lockTag.getInt("lock"));
                crate.setMod(lockTag.getDouble("lockMod"));
                if (lockTag.getBoolean("isLocked")) crate.lock();
            }

            // Пауки
            if (nbt.contains("crate_spiders")) {
                crate.hasSpiders = nbt.getBoolean("crate_spiders");
            }

            // Имя
            if (nbt.contains("crate_name")) {
                crate.customName = nbt.getString("crate_name");
            }

            crate.setChanged();
        }
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                                  @NotNull Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityCrateBase crate) {
                ItemStack drop = new ItemStack(this);

                CompoundTag nbt = new CompoundTag();

                // Содержимое
                CompoundTag itemsTag = new CompoundTag();
                for (int i = 0; i < crate.getContainerSize(); i++) {
                    ItemStack stack = crate.getItem(i);
                    if (!stack.isEmpty()) {
                        itemsTag.put("slot" + i, stack.save(new CompoundTag()));
                    }
                }
                if (!itemsTag.isEmpty()) nbt.put("crate_items", itemsTag);

                // Замок
                if (crate.isLocked()) {
                    CompoundTag lockTag = new CompoundTag();
                    lockTag.putInt("lock", crate.getPins());
                    lockTag.putDouble("lockMod", crate.getMod());
                    lockTag.putBoolean("isLocked", true);
                    nbt.put("crate_lock", lockTag);
                }

                // Пауки
                if (crate.hasSpiders) {
                    nbt.putBoolean("crate_spiders", true);
                }

                // Имя
                if (crate.customName != null && !crate.customName.isEmpty()) {
                    nbt.putString("crate_name", crate.customName);
                }

                if (!nbt.isEmpty()) {
                    drop.setTag(nbt);
                }

                // Дропаем предмет
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // НЕ дропаем содержимое — оно уже в предмете через playerWillDestroy
            level.updateNeighbourForOutputSignal(pos, this);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (this == ModBlocks.CRATE_IRON.get()) {
            return new TileEntityCrateIron(pos, state);
        }
        if (this == ModBlocks.CRATE_STEEL.get()) {
            return new TileEntityCrateSteel(pos, state);
        }
        if (this == ModBlocks.CRATE_DESH.get()) {
            return new TileEntityCrateDesh(pos, state);
        }
        if (this == ModBlocks.CRATE_TEMPLATE.get()) {
            return new TileEntityCrateTemplate(pos, state);
        }
        if (this == ModBlocks.CRATE_TUNGSTEN.get()) {
            return new TileEntityCrateTungsten(pos, state);
        }
        if (this == ModBlocks.SAFE.get()) {
            return new TileEntitySafe(pos, state);
        }
        return null;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityCrateBase crate) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(crate);
        }
        return 0;
    }
}