package com.hbm.blocks.generic;

import com.hbm.datagen.worldgen.structure.util.LogicBlockActions;
import com.hbm.datagen.worldgen.structure.util.LogicBlockConditions;
import com.hbm.datagen.worldgen.structure.util.LogicBlockInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class LogicBlock extends BaseEntityBlock {

    public LogicBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        // Невидимый: BER не нужен, блок не рендерится. Маскировка — через отдельный BER, если нужно.
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityLogicBlock(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == com.hbm.tileentity.ModTileEntity.LOGIC_BLOCK.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityLogicBlock logic) logic.tick();
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityLogicBlock logic && logic.interaction != null) {
            Object[] args = new Object[]{
                    level, te,
                    pos.getX(), pos.getY(), pos.getZ(),
                    player, hit.getDirection().get3DDataValue(),
                    (float) hit.getLocation().x, (float) hit.getLocation().y, (float) hit.getLocation().z
            };
            logic.interaction.accept(args);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 3600000.0F) // неразрушимый
                .noLootTable()
                .pushReaction(PushReaction.BLOCK);
    }

    // ============================================================
    // TileEntity
    // ============================================================

    public static class TileEntityLogicBlock extends BlockEntity {

        public int phase = 0;
        public int timer = 0;

        public Block disguise;
        public int disguiseMeta;

        public String conditionID = "PLAYER_CUBE_5";
        public String actionID = "FODDER_WAVE";
        public String interactionID;

        public Function<TileEntityLogicBlock, Boolean> condition;
        public Consumer<TileEntityLogicBlock> action;
        public Consumer<Object[]> interaction;

        public Player player;

        public Direction direction = Direction.UP;

        public TileEntityLogicBlock(BlockPos pos, BlockState state) {
            super(com.hbm.tileentity.ModTileEntity.LOGIC_BLOCK.get(), pos, state);
        }

        public void tick() {
            if (level == null || level.isClientSide) return;

            if (action == null) action = LogicBlockActions.actions.get(actionID);
            if (condition == null) condition = LogicBlockConditions.conditions.get(conditionID);
            if (interaction == null && interactionID != null) interaction = LogicBlockInteractions.interactions.get(interactionID);

            if (action == null || condition == null) {
                level.removeBlock(worldPosition, false);
                return;
            }

            action.accept(this);

            if (condition.apply(this)) {
                phase++;
                timer = 0;
            } else {
                timer++;
            }
        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag nbt) {
            super.saveAdditional(nbt);
            nbt.putInt("phase", phase);
            nbt.putString("actionID", actionID);
            nbt.putString("conditionID", conditionID);
            if (interactionID != null) nbt.putString("interactionID", interactionID);
            nbt.putInt("direction", direction.ordinal());
            if (disguise != null) {
                ResourceLocation id = ForgeRegistries.BLOCKS.getKey(disguise);
                if (id != null) nbt.putString("disguise", id.toString());
                nbt.putInt("disguiseMeta", disguiseMeta);
            }
        }

        @Override
        public void load(@NotNull CompoundTag nbt) {
            super.load(nbt);
            this.phase = nbt.getInt("phase");
            this.actionID = nbt.getString("actionID");
            this.conditionID = nbt.getString("conditionID");
            if (nbt.contains("interactionID")) this.interactionID = nbt.getString("interactionID");
            this.direction = Direction.values()[nbt.getInt("direction") % 6];
            if (nbt.contains("disguise")) {
                this.disguise = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("disguise")));
                this.disguiseMeta = nbt.getInt("disguiseMeta");
            }
        }

        @Override
        public @NotNull CompoundTag getUpdateTag() {
            CompoundTag tag = new CompoundTag();
            saveAdditional(tag);
            return tag;
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
            if (pkt.getTag() != null) load(pkt.getTag());
        }
    }
}