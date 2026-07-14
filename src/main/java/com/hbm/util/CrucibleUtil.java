package com.hbm.util;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.material.NTMMaterial.SmeltingBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CrucibleUtil {

    /**
     * Pour a single stack straight down. Returns leftover material.
     */
    public static MaterialStack pourSingleStack(Level level, double x, double y, double z, double range,
                                                boolean safe, MaterialStack stack, int quanta, Vec3[] impactPosHolder) {
        Vec3 start = new Vec3(x, y, z);
        Vec3 end = new Vec3(x, y - range, z);

        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));

        if (hit.getType() != HitResult.Type.BLOCK) {
            spill(hit, safe, stack, quanta, impactPosHolder);
            return stack;
        }

        BlockPos pos = hit.getBlockPos();
        Block block = level.getBlockState(pos).getBlock();

        if (!(block instanceof ICrucibleAcceptor acc)) {
            spill(hit, safe, stack, quanta, impactPosHolder);
            return stack;
        }

        MaterialStack ret = tryPourStack(level, acc, hit, stack, impactPosHolder);
        if (ret != null) return ret;

        spill(hit, safe, stack, quanta, impactPosHolder);
        return stack;
    }

    private static MaterialStack tryPourStack(Level level, ICrucibleAcceptor acc, BlockHitResult hit,
                                              MaterialStack stack, Vec3[] impactPosHolder) {
        Vec3 hitVec = hit.getLocation();

        if (stack.material.smeltable != SmeltingBehavior.SMELTABLE) return null;

        BlockPos pos = hit.getBlockPos();
        Direction side = hit.getDirection();

        if (acc.canAcceptPartialPour(level, pos, hitVec.x, hitVec.y, hitVec.z, side, stack)) {
            MaterialStack left = acc.pour(level, pos, hitVec.x, hitVec.y, hitVec.z, side, stack);
            if (left == null) left = new MaterialStack(stack.material, 0);

            if (impactPosHolder != null && impactPosHolder.length > 0) {
                impactPosHolder[0] = new Vec3(hitVec.x, hitVec.y, hitVec.z);
            }
            return left;
        }

        return null;
    }

    private static void spill(BlockHitResult hit, boolean safe, MaterialStack stack, int quanta, Vec3[] impactPos) {
        if (safe) return;

        MaterialStack toWaste = new MaterialStack(stack.material, Math.min(stack.amount, quanta));
        stack.amount -= toWaste.amount;

        if (impactPos != null && impactPos.length > 0 && hit != null) {
            Vec3 hitVec = hit.getLocation();
            impactPos[0] = new Vec3(hitVec.x, hitVec.y, hitVec.z);
        }
    }

    /**
     * Spill routine for a list of stacks (using array for impact position).
     */
    private static MaterialStack spill(BlockHitResult hit, boolean safe, List<MaterialStack> stacks, int quanta, Vec3[] impactPos) {
        if (stacks.isEmpty()) return null;
        MaterialStack top = stacks.get(0);
        MaterialStack ret = spillToList(hit, safe, top, quanta, impactPos);
        stacks.removeIf(o -> o.amount <= 0);
        return ret;
    }

    private static MaterialStack spillToList(BlockHitResult hit, boolean safe, MaterialStack stack, int quanta, Vec3[] impactPos) {
        if (safe) return null;

        MaterialStack toWaste = new MaterialStack(stack.material, Math.min(stack.amount, quanta));
        stack.amount -= toWaste.amount;

        if (impactPos != null && impactPos.length > 0 && hit != null) {
            Vec3 hitVec = hit.getLocation();
            impactPos[0] = new Vec3(hitVec.x, hitVec.y, hitVec.z);
        }

        return toWaste;
    }

    /**
     * Pour a full stack list.
     */
    public static MaterialStack pourFullStack(Level level, double x, double y, double z, double range,
                                              boolean safe, List<MaterialStack> stacks, int quanta, Vec3[] impactPosHolder) {
        if (stacks.isEmpty()) return null;

        Vec3 start = new Vec3(x, y, z);
        Vec3 end = new Vec3(x, y - range, z);

        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));

        if (hit.getType() != HitResult.Type.BLOCK) {
            return spill(hit, safe, stacks, quanta, impactPosHolder);
        }

        BlockPos pos = hit.getBlockPos();
        Block block = level.getBlockState(pos).getBlock();

        if (!(block instanceof ICrucibleAcceptor acc)) {
            return spill(hit, safe, stacks, quanta, impactPosHolder);
        }

        for (MaterialStack stack : stacks) {
            if (stack.material == null) continue;
            int amountToPour = Math.min(stack.amount, quanta);
            MaterialStack toPour = new MaterialStack(stack.material, amountToPour);
            MaterialStack left = tryPourStack(level, acc, hit, toPour, impactPosHolder);

            if (left != null) {
                stack.amount -= (amountToPour - left.amount);
                return new MaterialStack(stack.material, amountToPour - left.amount);
            }
        }

        return spill(hit, safe, stacks, quanta, impactPosHolder);
    }
}