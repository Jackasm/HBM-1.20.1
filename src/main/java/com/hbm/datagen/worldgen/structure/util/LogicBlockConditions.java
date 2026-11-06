package com.hbm.datagen.worldgen.structure.util;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockPedestal;
import com.hbm.blocks.generic.LogicBlock;
import com.hbm.entity.mob.EntityUndeadSoldier;
import com.hbm.items.ModToolItems;
import com.hbm.tileentity.bomb.TileEntityCharge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public class LogicBlockConditions {

    public static LinkedHashMap<String, Function<LogicBlock.TileEntityLogicBlock, Boolean>> conditions;

    /** For use with interactions, for having them handle all conditional tasks */
    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> EMPTY = (tile) -> false;

    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> ABERRATOR = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return false;
        if (world.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) return false;

        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        boolean aoeCheck = !world.getEntitiesOfClass(Player.class,
                new AABB(x, y, z, x + 1, y - 2, z + 1).inflate(10, 10, 10)).isEmpty();

        if (tile.phase == 0) {
            if (world.getGameTime() % 20 != 0) return false;
            return aoeCheck;
        }
        if (tile.phase < 3) {
            if (world.getGameTime() % 20 != 0 || tile.timer < 60) return false;
            return world.getEntitiesOfClass(EntityUndeadSoldier.class,
                    new AABB(x, y, z, x - 2, y + 1, z + 1).inflate(50, 20, 50)).isEmpty() && aoeCheck;
        }
        return false;
    };

    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> PLAYER_CUBE_3 = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return false;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();
        return !world.getEntitiesOfClass(Player.class,
                new AABB(x, y, z, x + 1, y - 2, z + 1).inflate(3, 3, 3)).isEmpty();
    };

    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> PLAYER_CUBE_5 = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return false;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();
        return !world.getEntitiesOfClass(Player.class,
                new AABB(x, y, z, x + 1, y - 2, z + 1).inflate(5, 5, 5)).isEmpty();
    };

    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> PLAYER_CUBE_25 = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return false;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();
        return !world.getEntitiesOfClass(Player.class,
                new AABB(x, y, z, x + 1, y - 2, z + 1).inflate(25, 25, 25)).isEmpty();
    };

    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> REDSTONE = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return false;
        return world.hasNeighborSignal(tile.getBlockPos());
    };

    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> PUZZLE_TEST = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return false;
        BlockPos pos = tile.getBlockPos();

        if (tile.phase == 0 && world.hasNeighborSignal(pos)) {
            Player player = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 25, false);
            if (player != null) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                        "Find a " + net.minecraft.ChatFormatting.GOLD + "great" + net.minecraft.ChatFormatting.RESET +
                                " ancient weapon, of questionable use in the modern age"), false);
            }
            world.setBlockAndUpdate(pos.above(1), ModBlocks.PEDESTAL.get().defaultBlockState());
            return true;
        }

        var pedestal = world.getBlockEntity(pos.above(1));

        return tile.phase == 1
                && pedestal instanceof BlockPedestal.TileEntityPedestal ped
                && ped.item != null
                && !ped.item.isEmpty()
                && ped.item.getItem() == ModToolItems.BIG_SWORD.get();
    };

    public static Function<LogicBlock.TileEntityLogicBlock, Boolean> BOMB_CRANE = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return false;
        BlockPos pos = tile.getBlockPos();

        if (tile.phase == 0) {
            world.setBlockAndUpdate(pos.above(1), ModBlocks.CHARGE_C4.get().defaultBlockState());
            var te = world.getBlockEntity(pos.above(1));
            if (te instanceof TileEntityCharge bomb) {
                bomb.timer = 200;
            }
        }

        return !world.getEntitiesOfClass(Player.class,
                new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() - 2, pos.getZ() + 1)
                        .inflate(10, 10, 10)).isEmpty();
    };

    public static List<String> getConditionNames() {
        return new ArrayList<>(conditions.keySet());
    }

    static {
        initialize();
    }

    public static void initialize() {
        conditions = new LinkedHashMap<>();

        conditions.put("EMPTY", EMPTY);
        conditions.put("PLAYER_CUBE_3", PLAYER_CUBE_3);
        conditions.put("PLAYER_CUBE_5", PLAYER_CUBE_5);
        conditions.put("PLAYER_CUBE_25", PLAYER_CUBE_25);

        conditions.put("BOMB_CRANE", BOMB_CRANE);

        conditions.put("ABERRATOR", ABERRATOR);
        conditions.put("REDSTONE", REDSTONE);
        conditions.put("PUZZLE_TEST", PUZZLE_TEST);
    }
}