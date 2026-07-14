package com.hbm.blocks.machine;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockBarrel extends BlockBarrelBase {

    private final int capacity;
    private final BarrelType barrelType;

    public BlockBarrel(Properties properties, int capacity, BarrelType type) {
        super(properties);
        this.capacity = capacity;
        this.barrelType = type;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public BarrelType getBarrelType() {
        return barrelType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<net.minecraft.network.chat.Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public static Properties createProperties(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(2.0F, 5.0F)
                .sound(sound)
                .pushReaction(PushReaction.DESTROY);
    }
}