package com.hbm.blocks.generic;

import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockModDoor extends DoorBlock {

    public BlockModDoor(Properties properties) {
        super(properties, BlockSetType.IRON);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level,
                                          @NotNull BlockPos pos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        // Пропускаем ванильную проверку на canOpenByHand, принудительно открываем
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 10);

        // Кастомный звук
        boolean isOpen = state.getValue(OPEN);
        SoundEvent sound = isOpen ? ModSounds.DOOR_OPEN.get() : ModSounds.DOOR_CLOSE.get();
        level.playSound(player, pos, sound, player.getSoundSource(), 1.0F, level.random.nextFloat() * 0.1F + 0.9F);

        level.gameEvent(player, isOpen ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static BlockBehaviour.Properties createDoorProperties(float strength, float resistance) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(strength, resistance)
                .noOcclusion();
    }
}