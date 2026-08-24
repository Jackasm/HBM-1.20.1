package com.hbm.entity.item;

import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EntityFlamePonyPainting extends Painting {

    public EntityFlamePonyPainting(EntityType<? extends Painting> type, Level level) {
        super(type, level);
    }

    public EntityFlamePonyPainting(Level level, BlockPos pos, Direction direction, Holder<PaintingVariant> variant) {
        super(level, pos, direction, variant);
    }

    @Override
    public void dropItem(@Nullable Entity p_31925_) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(net.minecraft.sounds.SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (p_31925_ instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    return;
                }
            }
            // Вместо Items.PAINTING дропаем FLAME_PONY
            this.spawnAtLocation(ModItems.FLAME_PONY.get());
        }
    }
}
