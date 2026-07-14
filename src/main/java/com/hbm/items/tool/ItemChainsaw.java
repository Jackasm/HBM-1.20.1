package com.hbm.items.tool;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.IAnimatedItem;
import com.hbm.render.anim.AnimationEnums.ToolAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.HbmAnimations;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ItemChainsaw extends ItemToolAbilityFueled implements IAnimatedItem<ToolAnimation> {

    @SafeVarargs
    public ItemChainsaw(Properties properties, Tier tier, float attackDamage, float attackSpeed,
                        int maxFuel, int consumption, int fillRate,
                        Supplier<FluidTypeHBM>... acceptedFuels) {
        super(properties, tier, BlockTags.MINEABLE_WITH_AXE,
                attackDamage, attackSpeed,
                maxFuel, consumption, fillRate,
                acceptedFuels);
    }

    @Override
    public BusAnimation getAnimation(ToolAnimation type, ItemStack stack) {
        int forward = 150;
        int sideways = 100;
        int retire = 200;

        if (HbmAnimations.getRelevantAnim(0) == null) {
            return new BusAnimation()
                    .addBus("SWING_ROT", new BusAnimationSequence()
                            .addPos(0, 0, 90, forward)
                            .addPos(45, 0, 90, sideways)
                            .addPos(0, 0, 0, retire))
                    .addBus("SWING_TRANS", new BusAnimationSequence()
                            .addPos(0, 0, 3, forward)
                            .addPos(2, 0, 2, sideways)
                            .addPos(0, 0, 0, retire));
        } else {
            double[] rot = HbmAnimations.getRelevantTransformation("SWING_ROT");
            double[] trans = HbmAnimations.getRelevantTransformation("SWING_TRANS");

            if (System.currentTimeMillis() - Objects.requireNonNull(HbmAnimations.getRelevantAnim(0)).startMillis < 50) return null;

            return new BusAnimation()
                    .addBus("SWING_ROT", new BusAnimationSequence()
                            .addPos(rot[0], rot[1], rot[2], 0)
                            .addPos(0, 0, 90, forward)
                            .addPos(45, 0, 90, sideways)
                            .addPos(0, 0, 0, retire))
                    .addBus("SWING_TRANS", new BusAnimationSequence()
                            .addPos(trans[0], trans[1], trans[2], 0)
                            .addPos(0, 0, 3, forward)
                            .addPos(2, 0, 2, sideways)
                            .addPos(0, 0, 0, retire));
        }
    }

    @Override
    public Class<ToolAnimation> getEnum() {
        return ToolAnimation.class;
    }

    @Override
    public boolean shouldPlayerModelAim(ItemStack stack) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
    }
}