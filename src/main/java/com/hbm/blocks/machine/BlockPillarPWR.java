package com.hbm.blocks.machine;

import com.hbm.blocks.ITooltipProvider;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockPillarPWR extends BlockPillar implements ITooltipProvider {

    public BlockPillarPWR(Properties properties) {
        super(properties);
    }

    // ITooltipProvider.addTooltip уже реализован по умолчанию.
    // Если нужен свой тултип — переопредели addTooltip, но здесь он не нужен.

    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops();
    }
}