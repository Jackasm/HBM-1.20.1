package com.hbm.blocks.generic;

import com.hbm.items.armor.ArmorFSB;
import com.hbm.items.armor.ArmorFSBPowered;
import com.hbm.api.energy.IBatteryItem;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HEVBattery extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Shapes.box(6/16.0, 0, 6/16.0, 10/16.0, 6/16.0, 10/16.0);

    public HEVBattery(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityHEVBattery(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {

        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        if (!ArmorFSB.hasFSBArmorIgnoreCharge(player)) return InteractionResult.PASS;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof ArmorFSBPowered)) return InteractionResult.PASS;

        // Заряжаем все батарейки в броне
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof IBatteryItem battery) {
                long maxCharge = battery.getMaxCharge(stack);
                long charge = battery.getCharge(stack);
                long newCharge = Math.min(charge + 150000, maxCharge);
                battery.setCharge(stack, newCharge);
            }
        }

        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                ModSounds.ITEM_BATTERY.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        level.removeBlock(pos, false);

        return InteractionResult.CONSUME;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(0.5F, 0.25F)
                .lightLevel(state -> 10)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    public static class TileEntityHEVBattery extends BlockEntity {

        public TileEntityHEVBattery(BlockPos pos, BlockState state) {
            super(ModTileEntity.HEV_BATTERY.get(), pos, state);
        }
    }
}