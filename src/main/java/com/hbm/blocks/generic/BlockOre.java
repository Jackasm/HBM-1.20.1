package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;


import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import com.hbm.util.HBMEnums;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


public class BlockOre extends Block {

    private float rad = 0.0F;
    private boolean radChecked = false;
    public boolean allowFortune = true;

    public BlockOre(Properties properties) {
        super(properties);
    }

    public BlockOre(Properties properties, float rad) {
        super(properties);
        this.rad = rad;
    }

    public BlockOre noFortune() {
        this.allowFortune = false;
        return this;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (this == ModBlocks.ORE_FLUORITE.get()) {
            return new ItemStack(ModItems.FLUORITE.get());
        }
        if (this == ModBlocks.ORE_NITER.get()) {
            return new ItemStack(ModItems.NITER.get());
        }
        if (this == ModBlocks.ORE_SULFUR.get() || this == ModBlocks.ORE_NETHER_SULFUR.get()) {
            return new ItemStack(ModItems.SULFUR.get());
        }
        if (this == ModBlocks.WASTE_TRINITITE.get() || this == ModBlocks.WASTE_TRINITITE_RED.get()) {
            return new ItemStack(ModItems.TRINITITE.get());
        }
        if (this == ModBlocks.WASTE_PLANKS.get()) {
            return new ItemStack(Items.COAL);
        }
        if (this == ModBlocks.FROZEN_DIRT.get()) {
            return new ItemStack(Items.SNOWBALL);
        }
        if (this == ModBlocks.FROZEN_PLANKS.get()) {
            return new ItemStack(Items.SNOWBALL);
        }
        if (this == ModBlocks.ORE_NETHER_FIRE.get()) {
            return new ItemStack(ModItems.POWDER_FIRE.get());
        }
        if (this == ModBlocks.BLOCK_METEOR_COBBLE.get()) {
            return new ItemStack(ModItems.FRAGMENT_METEORITE.get());
        }
        if (this == ModBlocks.BLOCK_METEOR_BROKEN.get()) {
            return new ItemStack(ModItems.FRAGMENT_METEORITE.get());
        }
        if (this == ModBlocks.ORE_RARE.get() || this == ModBlocks.ORE_GNEISS_RARE.get()) {
            return new ItemStack(ModItems.CHUNK_ORE.get());
        }
        if (this == ModBlocks.ORE_ASBESTOS.get() || this == ModBlocks.ORE_GNEISS_ASBESTOS.get()) {
            return new ItemStack(ModItems.INGOT_ASBESTOS.get());
        }
        if (this == ModBlocks.ORE_LIGNITE.get()) {
            return new ItemStack(ModItems.LIGNITE.get());
        }
        if (this == ModBlocks.ORE_CINNABAR.get()) {
            return new ItemStack(ModItems.CINNABAR.get());
        }
        if (this == ModBlocks.ORE_COLTAN.get()) {
            return new ItemStack(ModItems.FRAGMENT_COLTAN.get());
        }
        if (this == ModBlocks.ORE_COBALT.get() || this == ModBlocks.ORE_NETHER_COBALT.get()) {
            return new ItemStack(ModItems.FRAGMENT_COBALT.get());
        }
        if (this == ModBlocks.BLOCK_METEOR_MOLTEN.get()) {
            return ItemStack.EMPTY;
        }

        return super.getCloneItemStack(level, pos, state);
    }

    public int quantityDropped(RandomSource rand) {
        if (this == ModBlocks.ORE_FLUORITE.get()) {
            return 2 + rand.nextInt(3);
        }
        if (this == ModBlocks.ORE_NITER.get()) {
            return 2 + rand.nextInt(3);
        }
        if (this == ModBlocks.ORE_SULFUR.get() || this == ModBlocks.ORE_NETHER_SULFUR.get()) {
            return 2 + rand.nextInt(3);
        }
        if (this == ModBlocks.BLOCK_METEOR_BROKEN.get()) {
            return 1 + rand.nextInt(3);
        }

        if (this == ModBlocks.BLOCK_METEOR_TREASURE.get()) {
            return 1 + rand.nextInt(3);
        }

        if (this == ModBlocks.ORE_COBALT.get()) {
            return 4 + rand.nextInt(6);
        }
        if (this == ModBlocks.ORE_NETHER_COBALT.get()) {
            return 5 + rand.nextInt(8);
        }

        return 1;
    }

    public int quantityDroppedWithBonus(int fortune, RandomSource rand) {
        if (fortune > 0 && this.getItemDropped() != this.asItem() && allowFortune) {
            int mult = rand.nextInt(fortune + 2) - 1;
            if (mult < 0) {
                mult = 0;
            }
            return this.quantityDropped(rand) * (mult + 1);
        } else {
            return this.quantityDropped(rand);
        }
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
        return 0;
    }

    private Item getItemDropped() {
        ItemStack stack = this.getCloneItemStack(null, null, null);
        return stack.isEmpty() ? null : stack.getItem();
    }

    public int getDroppedDamage(BlockState state) {
        if (this == ModBlocks.ORE_RARE.get() || this == ModBlocks.ORE_GNEISS_RARE.get()) {
            return HBMEnums.EnumChunkType.RARE.ordinal();
        }
        if (this == ModBlocks.WASTE_PLANKS.get()) {
            return 1;
        }
        return 0;
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living) {
            if (this == ModBlocks.FROZEN_DIRT.get()) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2 * 60 * 20, 2));
            }
            if (this == ModBlocks.BLOCK_WASTE.get()) {
                living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 30 * 20, 2));
            }

            if (this == ModBlocks.BLOCK_TRINITITE.get()) {
                living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 30 * 20, 2));
            }
            if (this == ModBlocks.BRICK_JUNGLE_OOZE.get()) {
                living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 15 * 20, 9));
            }
            if (this == ModBlocks.BRICK_JUNGLE_MYSTIC.get()) {
                living.addEffect(new MobEffectInstance(HbmPotion.TAINT.get(), 15 * 20, 2));
            }

            if (this == ModBlocks.WASTE_TRINITITE.get() || this == ModBlocks.WASTE_TRINITITE_RED.get()) {
                living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 30 * 20, 0));
            }

        }

        if (this == ModBlocks.BLOCK_METEOR_MOLTEN.get()) {
            entity.setSecondsOnFire(5);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        super.animateTick(state, level, pos, rand);


        if (this == ModBlocks.WASTE_TRINITITE.get() || this == ModBlocks.WASTE_TRINITITE_RED.get() ||
                this == ModBlocks.BLOCK_TRINITITE.get() || this == ModBlocks.BLOCK_WASTE.get()) {

            level.addParticle(ParticleTypes.MYCELIUM,
                    pos.getX() + rand.nextFloat(),
                    pos.getY() + 1.1,
                    pos.getZ() + rand.nextFloat(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (level.getBlockState(pos.below()).getBlock() == ModBlocks.ORE_OIL_EMPTY.get()) {
            level.setBlock(pos, ModBlocks.ORE_OIL_EMPTY.get().defaultBlockState(), 3);
            level.setBlock(pos.below(), ModBlocks.ORE_OIL.get().defaultBlockState(), 3);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
                           @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (this == ModBlocks.BLOCK_METEOR_MOLTEN.get()) {
            if (!level.isClientSide) {
                level.setBlock(pos, ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState(), 3);
            }
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);

        }
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!radChecked) {
            ItemStack stack = new ItemStack(this);
            this.rad = HazardSystem.getHazardLevelFromStack(stack, HazardRegistry.RADIATION) * 0.2F;
            radChecked = true;
        }

        if (this.rad > 0) {
            RadiationEvents.incrementRadiation(level, pos, rad);
            level.scheduleTick(pos, this, this.getTickRate(level));
        }
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 20);
    }

    public int getTickRate(LevelReader level) {
        return this.rad > 0 ? 20 : 100;
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos,
                              @NotNull BlockState state, BlockEntity blockEntity, @NotNull ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (this == ModBlocks.BLOCK_METEOR_MOLTEN.get()) {
            if (!level.isClientSide) {
                level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
            }
        }
    }

    // Фабричный метод для создания свойств блока
    public static BlockBehaviour.Properties createProperties(MapColor color, float hardness, float resistance) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .pushReaction(PushReaction.DESTROY);
    }

    public static BlockBehaviour.Properties createOreProperties(MapColor color, float hardness, float resistance) {
        return createProperties(color, hardness, resistance)
                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties createRadioactiveProperties(MapColor color, float hardness, float resistance) {
        return createProperties(color, hardness, resistance)
                .randomTicks();
    }
}