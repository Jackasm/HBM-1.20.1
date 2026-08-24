package com.hbm.items.special;

import com.hbm.entity.item.EntityFlamePonyPainting;
import com.hbm.items.ModPaintings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemFlamePonyPainting extends Item {

    public ItemFlamePonyPainting(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.FAIL;

        if (direction == Direction.DOWN || direction == Direction.UP) {
            return InteractionResult.FAIL;
        }

        BlockPos placePos = pos.relative(direction);

        if (!level.isEmptyBlock(placePos)) {
            return InteractionResult.FAIL;
        }

        AABB aabb = new AABB(placePos).inflate(0.5);
        List<Painting> existing = level.getEntitiesOfClass(Painting.class, aabb);
        if (!existing.isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            // Используем конструктор, который принимает все параметры
            EntityFlamePonyPainting painting = new EntityFlamePonyPainting(
                    level,
                    placePos,
                    direction,
                    ModPaintings.FLAME_PONY.getHolder().orElseThrow()
            );

            level.addFreshEntity(painting);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }
}