package com.hbm.util;

import com.hbm.api.fluid.IFluidUser;

import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * EXTERNAL COMPATIBILITY CLASS - DO NOT CHANGE METHOD NAMES/PARAMS ONCE CREATED
 * Is there a smarter way to do this? Most likely. Is there an easier one? Probably not.
 * @author hbm
 */
public class CompatExternal {

    /**
     * Gets the tile entity at that pos. If the tile entity is a multiblock part, it will return the core instead.
     * This method will be updated in the event that other multiblock systems are added to retain the intended functionality.
     * @return the core tile entity if the given position holds a part, the tile entity at that position if it doesn't or null if there is no tile entity
     */
    public static BlockEntity getCoreFromPos(Level level, BlockPos pos) {
        if (level == null || pos == null) return null;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Если блок является BlockDummyable, используем findCore
        if (block instanceof BlockDummyable dummy) {
            BlockPos corePos = dummy.findCore(level, pos);
            if (corePos != null) {
                return level.getBlockEntity(corePos);
            }
        }

        // Для обычных блоков возвращаем их TileEntity
        return level.getBlockEntity(pos);
    }



    /**
     * Returns a list of tank definitions from the supplied tile entity.
     * Uses IFluidUser, if the tile is incompatible it returns an empty list.
     * @param tile the block entity
     * @return an ArrayList of Object arrays with each array representing a fluid tank.<br>
     * [0]: STRING - unlocalized name of the fluid<br>
     * [1]: INT - the unique ID of this fluid<br>
     * [2]: INT - the hexadecimal color of this fluid<br>
     * [3]: INT - the amount of fluid in this tank in millibuckets<br>
     * [4]: INT - the capacity of this tank in millibuckets
     */
    public static ArrayList<Object[]> getFluidInfoFromTile(BlockEntity tile) {
        ArrayList<Object[]> list = new ArrayList<>();

        if (!(tile instanceof IFluidUser container)) {
            return list;
        }

        for (FluidTankHBM tank : container.getAllTanks()) {
            FluidTypeHBM type = tank.getTankType();
            list.add(new Object[] {
                    type.getDescription().getString(),
                    Fluids.getID(type),
                    type.getColor(),
                    tank.getFill(),
                    tank.getMaxFill()
            });
        }

        return list;
    }

    public static final Set<Class<?>> turretTargetPlayer = new HashSet<>();
    public static final Set<Class<?>> turretTargetFriendly = new HashSet<>();
    public static final Set<Class<?>> turretTargetHostile = new HashSet<>();
    public static final Set<Class<?>> turretTargetMachine = new HashSet<>();

    /**
     * Registers a class for turret targeting
     * @param clazz is the class that should be targeted.
     * @param type determines what setting the turret needs to have enabled to target this class.
     *             0 is player, 1 is friendly, 2 is hostile and 3 is machine.
     */
    public static void registerTurretTargetSimple(Class<?> clazz, int type) {
        switch (type) {
            case 0 -> turretTargetPlayer.add(clazz);
            case 1 -> turretTargetFriendly.add(clazz);
            case 2 -> turretTargetHostile.add(clazz);
            case 3 -> turretTargetMachine.add(clazz);
        }
    }

    public static final Set<Class<?>> turretTargetBlacklist = new HashSet<>();

    /**
     * Registers a class to be fully ignored by turrets
     * @param clazz is the class that should be ignored.
     */
    public static void registerTurretTargetBlacklist(Class<?> clazz) {
        turretTargetBlacklist.add(clazz);
    }

    public static final HashMap<Class<?>, BiFunction<Entity, Object, Integer>> turretTargetCondition = new HashMap<>();

    /**
     * Registers a BiFunction lambda for more complex targeting compatibility
     * @param clazz is the class that this rule should apply to
     * @param bi is the lambda. The function should return 0 to continue with other targeting checks,
     *           -1 to ignore this entity or 1 to target it.
     * The params for this lambda are the entity and the turret in question.
     */
    public static void registerTurretTargetingCondition(Class<?> clazz, BiFunction<Entity, Object, Integer> bi) {
        turretTargetCondition.put(clazz, bi);
    }

}