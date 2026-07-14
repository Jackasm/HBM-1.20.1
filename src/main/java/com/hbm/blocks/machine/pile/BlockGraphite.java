package com.hbm.blocks.machine.pile;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockFlammable;

import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;

import com.hbm.network.client.ParticleBurstPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockGraphite extends BlockFlammable implements IToolable {

    public BlockGraphite(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, ToolType tool) {
        if (tool != ToolType.HAND_DRILL) return false;

        if (!level.isClientSide) {
            // Превращаем в просверлённый графит
            BlockState newState = ModBlocks.BLOCK_GRAPHITE_DRILLED.get().defaultBlockState();
            level.setBlock(pos, newState, 3);

            // Отправляем частицы
            PacketDispatcher.sendToAllAround(new ParticleBurstPacket(pos, ModBlocks.BLOCK_GRAPHITE.get(), 0),
                    level, pos, 50);

            // Звук
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.STONE_BREAK, SoundSource.BLOCKS,
                    (1.0F + 1.0F) / 2.0F, 0.8F);

            // Выпадает графитовый слиток
            BlockGraphiteRod.ejectItem(level, pos, side, new ItemStack(ModItems.INGOT_GRAPHITE.get()));
        }

        return true;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.BLOCK);
    }
}