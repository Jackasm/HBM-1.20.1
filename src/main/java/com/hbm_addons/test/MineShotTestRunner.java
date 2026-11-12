package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.IShotDetonatable;
import com.hbm.blocks.bomb.Landmine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * release-0.1.2: mine shots are server-authoritative and go through the
 * existing Landmine explosion rather than BlockDetonatable's TNT fuse.
 *
 * Only the AP mine is exploded automatically. Nuclear/naval/special mine
 * visual and gameplay effects require separate manual testing on a copy.
 */
public final class MineShotTestRunner extends TestRunner {

    private static final BlockPos POS = new BlockPos(10, 250, 10);

    public MineShotTestRunner(ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount++ > 0) return true;

        Landmine mine = (Landmine) ModBlocks.MINE_AP.get();
        boolean previousSafeMode = Landmine.safeMode;

        try {
            // A mine must not detonate during fixture cleanup.
            Landmine.safeMode = true;
            level.setBlockAndUpdate(POS, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(POS.below(), Blocks.OBSIDIAN.defaultBlockState());
            Landmine.safeMode = false;

            // Every registered subtype shares Landmine's shot callback.
            check("ap_shot_contract", ModBlocks.MINE_AP.get() instanceof IShotDetonatable);
            check("he_shot_contract", ModBlocks.MINE_HE.get() instanceof IShotDetonatable);
            check("shrap_shot_contract", ModBlocks.MINE_SHRAP.get() instanceof IShotDetonatable);
            check("naval_shot_contract", ModBlocks.MINE_NAVAL.get() instanceof IShotDetonatable);
            check("fat_shot_contract", ModBlocks.MINE_FAT.get() instanceof IShotDetonatable);

            level.setBlockAndUpdate(POS, mine.defaultBlockState());
            check("mine_placed", level.getBlockState(POS).is(mine));

            // A temporary safe-mode window (defusing) must suppress shots.
            Landmine.safeMode = true;
            mine.onShot(level, POS);
            check("safe_mode_blocks_shot", level.getBlockState(POS).is(mine));
            Landmine.safeMode = false;

            // Real AP explosion. The block must disappear exactly once;
            // repeating this callback against the same position is a no-op.
            mine.onShot(level, POS);
            check("ap_removed_after_shot", level.getBlockState(POS).isAir());
            mine.onShot(level, POS);
            check("repeat_shot_is_noop", level.getBlockState(POS).isAir());

            // Unrelated support block is not removed by our shot callback.
            BlockState support = level.getBlockState(POS.below());
            check("support_kept", support.is(Blocks.OBSIDIAN));
        } finally {
            Landmine.safeMode = true;
            level.setBlockAndUpdate(POS, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(POS.below(), Blocks.AIR.defaultBlockState());
            Landmine.safeMode = previousSafeMode;
        }

        metric("mine_types", 5);
        metric("explosion_types_exercised", 1);
        finish(checks.stream().allMatch(c -> c.pass));
        return true;
    }
}
