package com.hbm.items.tool;

import com.hbm.items.ISatChip;
import com.hbm.items.machine.ItemSatChip;

import com.hbm.saveddata.SatelliteSavedData;
import com.hbm.saveddata.satellites.Satellite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemSatDesignator extends ItemSatChip {

    public ItemSatDesignator(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            Satellite sat = Objects.requireNonNull(SatelliteSavedData.getData(level)).getSatFromFreq(ISatChip.getFreq(stack));

            if (sat != null) {
                Vec3 start = player.getEyePosition(1.0F);
                Vec3 look = player.getViewVector(1.0F);
                Vec3 end = start.add(look.scale(300));

                HitResult hitResult = level.clip(new ClipContext(
                        start,
                        end,
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        player
                ));

                if (hitResult instanceof BlockHitResult blockHit) {
                    Direction dir = blockHit.getDirection();
                    BlockPos pos = blockHit.getBlockPos().relative(dir);

                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();

                    if (sat.satIface == Satellite.Interfaces.SAT_COORD) {
                        sat.onCoordAction(level, player, x, y, z);
                    } else if (sat.satIface == Satellite.Interfaces.SAT_PANEL) {
                        sat.onClick(level, x, z);
                    }
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}