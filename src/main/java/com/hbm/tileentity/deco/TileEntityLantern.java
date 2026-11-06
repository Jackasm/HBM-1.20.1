package com.hbm.tileentity.deco;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TileEntityLantern extends TileEntityLoadedBase {

    public TileEntityLantern(BlockPos pos, BlockState state) {
        super(ModTileEntity.LANTERN.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (level.getGameTime() % 20 != 0) return;

        AABB box = new AABB(
                worldPosition.getX() + 0.5, worldPosition.getY() + 5.5, worldPosition.getZ() + 0.5,
                worldPosition.getX() + 0.5, worldPosition.getY() + 5.5, worldPosition.getZ() + 0.5
        ).inflate(7.5);

        List<EntityGlyphid> glyphids = level.getEntitiesOfClass(EntityGlyphid.class, box);
        for (EntityGlyphid glyphid : glyphids) {
            glyphid.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX(),
                worldPosition.getY(),
                worldPosition.getZ(),
                worldPosition.getX() + 1,
                worldPosition.getY() + 6,
                worldPosition.getZ() + 1
        );
    }
}