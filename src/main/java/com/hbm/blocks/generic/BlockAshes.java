package com.hbm.blocks.generic;

import com.hbm.items.ModArmorItems;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockAshes extends FallingBlock {

    public static int ashes = 0;

    public BlockAshes(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (random.nextInt(25) == 0) {
            var player = Minecraft.getInstance().player;
            if (player == null) return;

            if (ArmorUtil.checkArmorPiece(player, ModArmorItems.ASHGLASSES.get(), 3)) {
                if (ashes < 256 * 0.25) {
                    ashes++;
                }
            } else if (ArmorRegistry.hasAnyProtection(player, 3, ArmorRegistry.HazardClass.SAND, ArmorRegistry.HazardClass.LIGHT)) {
                if (ashes < 256 * 0.75) {
                    ashes++;
                }
            } else {
                if (ashes < 256 * 0.95) {
                    ashes++;
                }
            }
        }
    }
}