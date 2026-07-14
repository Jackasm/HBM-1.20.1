package com.hbm.blocks.bomb;

import com.hbm.blocks.generic.BlockFlammable;
import com.hbm.entity.item.EntityTNTPrimedBase;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;

import net.minecraft.world.level.Explosion;
import org.jetbrains.annotations.NotNull;

public abstract class BlockDetonatable extends BlockFlammable implements IFuckingExplode {

    protected int popFuse; // A shorter fuse for when this explosive is dinked by another
    protected boolean detonateOnCollision;
    protected boolean detonateOnShot;

    public BlockDetonatable(Properties properties, int popFuse, boolean detonateOnCollision, boolean detonateOnShot) {
        super(properties);
        this.popFuse = popFuse;
        this.detonateOnCollision = detonateOnCollision;
        this.detonateOnShot = detonateOnShot;
    }

    // Конструктор для совместимости со старым кодом
    @Deprecated
    public BlockDetonatable(Properties properties) {
        this(properties, 0, false, false);
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        if(!world.isClientSide()) {
            // Получаем сущность, вызвавшую взрыв
            LivingEntity igniter = explosion.getIndirectSourceEntity() instanceof LivingEntity ?
                    explosion.getIndirectSourceEntity() : null;

            EntityTNTPrimedBase tntPrimed = new EntityTNTPrimedBase(world,
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    igniter, this);

            tntPrimed.fuse = popFuse <= 0 ? 0 : world.random.nextInt(popFuse) + popFuse / 2;
            tntPrimed.detonateOnCollision = detonateOnCollision;
            world.addFreshEntity(tntPrimed);
        }

        // Удаляем блок
        super.onBlockExploded(state, world, pos, explosion);
    }

    public boolean canDropFromExplosion(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        return false;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level world, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if(!world.isClientSide() && shouldIgnite(world, pos)) {
            world.removeBlock(pos, false);
            explodeEntity(world, pos, null);
        }
    }

    public void onShot(Level world, BlockPos pos) {
        if (!detonateOnShot || world.isClientSide()) return;

        world.removeBlock(pos, false);
        explodeEntity(world, pos, null); // insta-explode
    }

    // Статический вспомогательный метод для создания свойств блока
    public static Properties createProperties(MapColor color, float hardness, float resistance,
                                              SoundType sound, int lightLevel) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .sound(sound)
                .lightLevel(state -> lightLevel)
                .requiresCorrectToolForDrops(); // Если нужно инструменты
    }

}