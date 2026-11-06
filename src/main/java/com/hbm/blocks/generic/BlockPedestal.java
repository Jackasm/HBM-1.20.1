package com.hbm.blocks.generic;

import com.hbm.datagen.worldgen.structure.util.PedestalEntry;
import com.hbm.datagen.worldgen.structure.util.PedestalEntryType;

import com.hbm.inventory.recipes.PedestalRecipes;
import com.hbm.extprop.HbmPlayerProps;

import com.hbm.items.ModItems;
import com.hbm.items.armor.ItemModDefuser;
import com.hbm.particle.helper.ExplosionSmallCreator;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockPedestal extends BaseEntityBlock {

    public static HashMap<Integer, List<PedestalEntry>> pedestalEntries = new HashMap<>();
    public static final int TIMEOUT = 60;

    private static final VoxelShape SHAPE = Shapes.block();

    public BlockPedestal(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPedestal(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.PEDESTAL.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityPedestal ped) ped.tick();
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityPedestal pedestal)) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        // === ПОЛОЖИТЬ 1 ПРЕДМЕТ ===
        if (pedestal.item.isEmpty() && !held.isEmpty()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            pedestal.item = held.copyWithCount(1);
            held.shrink(1);
            pedestal.setChanged();

            // Синхронизация: одинаковый state не отправляет пакет — используем UPDATE_CLIENTS
            level.sendBlockUpdated(pos, state, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
            level.setBlock(pos, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);

            return InteractionResult.CONSUME;
        }
        // === ЗАБРАТЬ 1 ПРЕДМЕТ ===
        else if (!pedestal.item.isEmpty() && held.isEmpty()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            player.setItemInHand(hand, pedestal.item.copy());
            pedestal.item = ItemStack.EMPTY;
            pedestal.setChanged();

            level.sendBlockUpdated(pos, state, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
            level.setBlock(pos, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);

            return InteractionResult.CONSUME;
        }
        // === ДОКИДЫВАНИЕ ТОГО ЖЕ ПРЕДМЕТА ===
        else if (!pedestal.item.isEmpty() && !held.isEmpty()) {
            if (ItemStack.isSameItemSameTags(pedestal.item, held)
                    && pedestal.item.getCount() < pedestal.item.getMaxStackSize()) {
                if (level.isClientSide) return InteractionResult.SUCCESS;

                pedestal.item.grow(1);
                held.shrink(1);
                pedestal.setChanged();

                level.sendBlockUpdated(pos, state, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
                level.setBlock(pos, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);

                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityPedestal pedestal && !pedestal.item.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, pedestal.item.copy());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull net.minecraft.world.level.block.Block neighborBlock,
                                @NotNull BlockPos neighborPos, boolean isMoving) {
        if (level.isClientSide) return;
        if (!level.hasNeighborSignal(pos)) return;

        TileEntityPedestal nw  = castOrNull(level, pos.offset(-2, 0, -2));
        TileEntityPedestal n   = castOrNull(level, pos.offset(0, 0, -3));
        TileEntityPedestal ne  = castOrNull(level, pos.offset(2, 0, -2));
        TileEntityPedestal w   = castOrNull(level, pos.offset(-3, 0, 0));
        TileEntityPedestal c   = castOrNull(level, pos);
        TileEntityPedestal e   = castOrNull(level, pos.offset(3, 0, 0));
        TileEntityPedestal sw  = castOrNull(level, pos.offset(-2, 0, 2));
        TileEntityPedestal s   = castOrNull(level, pos.offset(0, 0, 3));
        TileEntityPedestal se  = castOrNull(level, pos.offset(2, 0, 2));

        TileEntityPedestal[] grid = new TileEntityPedestal[]{nw, n, ne, w, c, e, sw, s, se};

        AABB playerBox = new AABB(pos).inflate(20, 20, 20);
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, playerBox);

        outer:
        for (PedestalRecipes.PedestalRecipe recipe : PedestalRecipes.recipes) {
            // EXTRA CONDITIONS
            switch (recipe.extra) {
                case FULL_MOON -> {
                    if (level.getTimeOfDay(0) < 0.35 || level.getTimeOfDay(0) > 0.65) continue;
                    if (level.getMoonPhase() != 0) continue;
                }
                case NEW_MOON -> {
                    if (level.getTimeOfDay(0) < 0.35 || level.getTimeOfDay(0) > 0.65) continue;
                    if (level.getMoonPhase() != 4) continue;
                }
                case SUN -> {
                    if (level.getTimeOfDay(0) > 0.15 && level.getTimeOfDay(0) < 0.85) continue;
                }
                case BAD_KARMA -> {
                    boolean matches = false;
                    for (Player p : nearbyPlayers) {
                        if (HbmPlayerProps.getData(p).getReputation() <= -10) { matches = true; break; }
                    }
                    if (!matches) continue;
                }
                case GOOD_KARMA -> {
                    boolean matches = false;
                    for (Player p : nearbyPlayers) {
                        if (HbmPlayerProps.getData(p).getReputation() >= 10) { matches = true; break; }
                    }
                    if (!matches) continue;
                }
                default -> {}
            }

            // CHECK ITEMS
            for (int i = 0; i < 9; i++) {
                ItemStack pedestal = grid[i] != null ? grid[i].item : ItemStack.EMPTY;
                if (pedestal.isEmpty() && recipe.input[i] != null) continue outer;
                if (!pedestal.isEmpty() && recipe.input[i] == null) continue outer;
                if (pedestal.isEmpty() && recipe.input[i] == null) continue;

                if (!recipe.input[i].matchesRecipe(pedestal, true) || recipe.input[i].stacksize != pedestal.getCount()) continue outer;
            }

            // REMOVE ITEMS
            for (int i = 0; i < 9; i++) {
                if (i == 4) continue;
                ItemStack pedestal = grid[i] != null ? grid[i].item : ItemStack.EMPTY;
                if (pedestal.isEmpty() && recipe.input[i] == null) continue;
                if (grid[i] == null) continue;
                grid[i].item = ItemStack.EMPTY;
                grid[i].setChanged();
                level.sendBlockUpdated(grid[i].getBlockPos(), grid[i].getBlockState(), grid[i].getBlockState(), 3);
            }

            // PRODUCE RESULT
            if (c != null) {
                c.item = recipe.output.copy();
                c.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
            ExplosionSmallCreator.composeEffect(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 10, 2.5F, 1F);

            return;
        }
    }

    private static TileEntityPedestal castOrNull(Level level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        return te instanceof TileEntityPedestal ped ? ped : null;
    }

    public static void pushPedestalEntry(Level level, PedestalEntryType type, BlockPos pos) {
        PedestalEntry entry = new PedestalEntry(type, pos, level.getGameTime());
        int dim = 0;
        List<PedestalEntry> entries = pedestalEntries.computeIfAbsent(dim, k -> new ArrayList<>());
        entries.add(entry);
    }

    public static void checkPedestalEntries(int dim, long currentTime) {
        List<PedestalEntry> entries = pedestalEntries.get(dim);
        if (entries == null) return;
        entries.removeIf(x -> x.timestamp < currentTime - TIMEOUT);
    }

    public static List<PedestalEntry> getEntriesForDimension(int dim) {
        return pedestalEntries.get(dim);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.STONE)
                .strength(2.0F, 10.0F)
                .sound(SoundType.STONE)
                .noOcclusion();
    }

    // ============================================================
    // TileEntity
    // ============================================================

    public static class TileEntityPedestal extends BlockEntity {

        public ItemStack item = ItemStack.EMPTY;

        public TileEntityPedestal(BlockPos pos, BlockState state) {
            super(ModTileEntity.PEDESTAL.get(), pos, state);
        }

        public void tick() {
            if (level == null || level.isClientSide) return;
            if (level.getGameTime() % 20 != 0) return;

            if (!item.isEmpty()) {
                if (item.getItem() == ModItems.PROTECTION_CHARM.get())
                    pushPedestalEntry(level, PedestalEntryType.CHARM_OF_PROTECTION, worldPosition);
                if (item.getItem() == ModItems.METEOR_CHARM.get())
                    pushPedestalEntry(level, PedestalEntryType.METEORITE_CHARM, worldPosition);
                if (level.getGameTime() % 60 == 0 && item.getItem() == ModItems.DEFUSER_GOLD.get())
                    castrateCreepers();
            }
        }

        public void castrateCreepers() {
            if (level == null) return;
            AABB box = new AABB(worldPosition).inflate(25, 25, 25);
            for (Creeper creeper : level.getEntitiesOfClass(Creeper.class, box)) {
                ItemModDefuser.castrateCreeper(creeper, null, false);
            }
        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag nbt) {
            super.saveAdditional(nbt);
            nbt.put("item", item.save(new CompoundTag()));
        }

        @Override
        public void load(@NotNull CompoundTag nbt) {
            super.load(nbt);
            if (nbt.contains("item")) {
                this.item = ItemStack.of(nbt.getCompound("item"));
            } else {
                this.item = ItemStack.EMPTY;
            }
        }

        @Override
        public @NotNull CompoundTag getUpdateTag() {
            CompoundTag tag = new CompoundTag();
            saveAdditional(tag);
            return tag;
        }

        @Override
        public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
            return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public void onDataPacket(net.minecraft.network.Connection net,
                                 net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {
            if (pkt.getTag() != null) load(pkt.getTag());
        }
    }
}