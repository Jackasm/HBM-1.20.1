package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.network.RadioTorchBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Стенд постановки RTTY-факелов: ставит факел на все шесть граней опорного
 * блока рукой (через {@code BlockItem#useOn}) и проверяет, что блок встал,
 * что {@code FACING} смотрит НА опору и что со снятой опорой факел выпадает.
 *
 * <p>Проверка появилась после #165: {@code canSurvive} ищет опору по
 * {@code FACING}, и неверное соглашение делает блок непоставляемым — снаружи
 * это выглядит как «факелы не ставятся вообще».</p>
 */
public class RttyPlaceTestRunner extends TestRunner {

    private static final int BASE_X = 11800;
    private static final int BASE_Y = 100;
    private static final int BASE_Z = 11800;

    /** Факелы, которые должны появиться: позиция -> ожидаемое FACING. */
    private final Map<BlockPos, Direction> expected = new LinkedHashMap<>();

    private int stage = 0;

    public RttyPlaceTestRunner(net.minecraft.server.level.ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (stage == 0) {
            progress("place");
            placeAllFaces();
            stage = 1;
            return false;
        }

        progress("verify");
        int ok = 0;
        for (Map.Entry<BlockPos, Direction> e : expected.entrySet()) {
            BlockState state = level.getBlockState(e.getKey());
            boolean isTorch = state.getBlock() instanceof RadioTorchBase;
            boolean rightFacing = isTorch && state.getValue(RadioTorchBase.FACING) == e.getValue();
            info("torch_at_" + e.getKey().toShortString(),
                    (isTorch ? state.getValue(RadioTorchBase.FACING).getName() : "нет блока")
                            + " ожидалось " + e.getValue().getName());
            check("placed_" + e.getKey().toShortString(), rightFacing);
            if (rightFacing) ok++;
        }

        // Снимаем опору у факела на верхней грани — он должен исчезнуть.
        BlockPos support = new BlockPos(BASE_X, BASE_Y, BASE_Z);
        BlockPos above = support.above();
        level.removeBlock(support, false);
        level.updateNeighborsAt(above, ModBlocks.RADIO_TORCH_SENDER.get());
        boolean gone = !(level.getBlockState(above).getBlock() instanceof RadioTorchBase);
        check("falls_without_support", gone);

        metric("placed", ok);
        metric("expected", expected.size());
        finish(ok == expected.size() && expected.size() == 6 && gone);
        return true;
    }

    /** По одному факелу на каждую из шести граней опорного блока. */
    private void placeAllFaces() {
        FakePlayer player = FakePlayerFactory.getMinecraft(level);
        ItemStack stack = new ItemStack(ModBlocks.RADIO_TORCH_SENDER.get());
        int index = 0;

        for (Direction face : Direction.values()) {
            BlockPos support = new BlockPos(BASE_X, BASE_Y, BASE_Z + index * 4);
            BlockPos target = support.relative(face);

            level.setBlock(support, Blocks.STONE.defaultBlockState(), 3);
            level.setBlock(target, Blocks.AIR.defaultBlockState(), 3);

            Vec3 hitVec = Vec3.atCenterOf(support).add(
                    face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);

            player.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
            BlockPlaceContext ctx = new BlockPlaceContext(player, InteractionHand.MAIN_HAND,
                    player.getItemInHand(InteractionHand.MAIN_HAND),
                    new BlockHitResult(hitVec, face, support, false));
            stack.copy().useOn(ctx);

            // FACING по соглашению порта смотрит на опору, то есть против клика.
            expected.put(target, face.getOpposite());
            index++;
        }
    }
}
