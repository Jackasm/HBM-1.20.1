package com.hbm.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockBase extends Block {

    private boolean beaconable = false;
    private boolean canSpawn = true;

    public BlockBase() {
        this(BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(1.5F, 6.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
    }

    public BlockBase(Properties properties) {
        super(properties);
    }

    /**
     * Конструктор для обратной совместимости со старым кодом
     * @deprecated Используй конструктор с Properties
     */
    @Deprecated
    public BlockBase(MapColor color, float hardness, float resistance, SoundType sound) {
        this(Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .sound(sound));
    }

    /**
     * Daisychainable setter for making the block a beacon base block
     */
    public BlockBase setBeaconable() {
        this.beaconable = true;
        return this;
    }

    public BlockBase noMobSpawn() {
        this.canSpawn = false;
        return this;
    }



    /**
     * Sets the block to air and drops it
     */
    public void dismantle(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.isAir()) return;

        // Удаляем блок
        world.removeBlock(pos, false);

        // Создаем предмет для дропа
        ItemStack itemstack = new ItemStack(this, 1);

        // Случайное смещение для визуала
        double offsetX = world.random.nextDouble() * 0.6 + 0.2;
        double offsetY = world.random.nextDouble() * 0.2;
        double offsetZ = world.random.nextDouble() * 0.6 + 0.2;

        ItemEntity entityitem = new ItemEntity(world,
                pos.getX() + offsetX,
                pos.getY() + offsetY + 1,
                pos.getZ() + offsetZ,
                itemstack);

        // Добавляем случайное движение
        float motionScale = 0.05F;
        entityitem.setDeltaMovement(
                world.random.nextGaussian() * motionScale,
                world.random.nextGaussian() * motionScale + 0.2F,
                world.random.nextGaussian() * motionScale
        );

        // Добавляем небольшую задержку перед подбором
        entityitem.setDefaultPickUpDelay();

        // Спавним предмет в мире
        if (!world.isClientSide()) {
            world.addFreshEntity(entityitem);
        }
    }

    /**
     * Перегруженная версия для обратной совместимости
     * @deprecated Используй dismantle(Level, BlockPos)
     */
    @Deprecated
    public void dismantle(Level world, int x, int y, int z) {
        dismantle(world, new BlockPos(x, y, z));
    }

    /**
     * Статический метод для создания базовых свойств блока
     */
    public static Properties createDefaultProperties(MapColor color, float hardness, float resistance, SoundType sound) {
        return Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .sound(sound);
    }

    /**
     * Статический метод для создания свойств металлического блока
     */
    public static Properties createMetalProperties(MapColor color, float hardness, float resistance) {
        return Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                .requiresCorrectToolForDrops()
                .strength(hardness, resistance)
                .sound(SoundType.METAL);
    }

    /**
     * Статический метод для создания свойств деревянного блока
     */
    public static Properties createWoodProperties(MapColor color, float hardness, float resistance) {
        return Properties.of()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASS)
                .strength(hardness, resistance)
                .sound(SoundType.WOOD)
                .ignitedByLava(); // Дерево может загореться от лавы
    }
}