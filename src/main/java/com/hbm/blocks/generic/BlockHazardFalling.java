package com.hbm.blocks.generic;

import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.handler.radiation.RadiationEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class BlockHazardFalling extends FallingBlock {

    private float rad = 0.0F;
    private boolean beaconable = false;

    public BlockHazardFalling(Properties properties) {
        super(properties);
    }

    public BlockHazardFalling(MapColor color) {
        super(Properties.of()
                .mapColor(color)
                .strength(0.2F)
                .sound(SoundType.SAND)
                .pushReaction(PushReaction.NORMAL));
    }

    public BlockHazardFalling makeBeaconable() {
        this.beaconable = true;
        return this;
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (this.rad > 0) {
            RadiationEvents.incrementRadiation(level, pos, rad);
            level.scheduleTick(pos, this, this.getDelayAfterPlace());
        }

        super.tick(state, level, pos, rand);
    }

    @Override
    public int getDelayAfterPlace() {
        if (this.rad > 0) {
            return 20;
        }
        return super.getDelayAfterPlace();
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        this.rad = HazardSystem.getHazardLevelFromStack(new ItemStack(this), HazardRegistry.RADIATION) * 0.1F;

        if (this.rad > 0) {
            level.scheduleTick(pos, this, this.getDelayAfterPlace());
        }
    }

    // Статический метод для создания свойств
    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.SAND)
                .strength(0.2F)
                .sound(SoundType.SAND)
                .pushReaction(PushReaction.NORMAL);
    }

    public static Properties createProperties(MapColor color) {
        return Properties.of()
                .mapColor(color)
                .strength(0.2F)
                .sound(SoundType.SAND)
                .pushReaction(PushReaction.NORMAL);
    }
}