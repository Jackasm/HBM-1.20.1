package com.hbm.tileentity.deco;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityDecoBlockAltF extends BlockEntity {

    public TileEntityDecoBlockAltF(BlockPos pos, BlockState state) {
        super(ModTileEntity.DECO_BLOCK_ALT_F.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityDecoBlockAltF te) {
        if (level.isClientSide) return;

        int strength = 4;
        float f = strength;
        double wat = 4 * 2;
        strength *= 2.0F;

        int i = (int) Math.floor(pos.getX() - wat - 1.0D);
        int j = (int) Math.floor(pos.getX() + wat + 1.0D);
        int k = (int) Math.floor(pos.getY() - wat - 1.0D);
        int i2 = (int) Math.floor(pos.getY() + wat + 1.0D);
        int l = (int) Math.floor(pos.getZ() - wat - 1.0D);
        int j2 = (int) Math.floor(pos.getZ() + wat + 1.0D);

        AABB aabb = new AABB(i, k, l, j, i2, j2);
        List<Entity> list = level.getEntitiesOfClass(Entity.class, aabb, entity -> !(entity instanceof Player));

        for (Entity entity : list) {
            double d4 = entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) / 16.0; // distance squared / 4^2

            if (d4 <= 1.0D) {
                double d5 = entity.getX() - pos.getX();
                double d6 = entity.getY() + entity.getEyeHeight() - pos.getY();
                double d7 = entity.getZ() - pos.getZ();
                double d9 = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);

                if (d9 < wat) {
                    if (entity instanceof Player player) {
                        player.addEffect(new MobEffectInstance(MobEffects.HEAL, 5, 99));
                        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 5, 99));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

}