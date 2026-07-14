package com.hbm.api.block;

import com.hbm.inventory.recipes.common.ComparableStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface IToolable {

    boolean onScrew(Level world, Player player, BlockPos pos, Direction side, float fX, float fY, float fZ, ToolType tool);

    enum ToolType {
        SCREWDRIVER,
        HAND_DRILL,
        DEFUSER,
        WRENCH,
        TORCH,
        BOLT;

        public List<ItemStack> stacksForDisplay = new ArrayList();
        private static HashMap<ComparableStack, ToolType> map = new HashMap();

        public void register(ItemStack stack) {
            stacksForDisplay.add(stack);
        }

        public static ToolType getType(ItemStack stack) {

            if(!map.isEmpty()) {
                return map.get(new ComparableStack(stack));
            }

            for(ToolType type : ToolType.values()) {
                for(ItemStack tool : type.stacksForDisplay) {
                    map.put(new ComparableStack(tool), type);
                }
            }

            return map.get(new ComparableStack(stack));
        }
    }
}