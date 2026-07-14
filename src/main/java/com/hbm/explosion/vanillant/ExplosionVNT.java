package com.hbm.explosion.vanillant;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.vanillant.interfaces.*;
import com.hbm.explosion.vanillant.standard.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ExplosionVNT {

    private IBlockAllocator blockAllocator;
    private IEntityProcessor entityProcessor;
    private IBlockProcessor blockProcessor;
    private IPlayerProcessor playerProcessor;
    private IExplosionSFX[] sfx;

    public Level world;
    public double posX;
    public double posY;
    public double posZ;
    public float size;
    public Entity exploder;

    private Map<Player, Vec3> compatPlayers = new HashMap<>();
    public Explosion compat;

    boolean noParticle = false;
    boolean noSound = false;

    public enum ExAttrib {
        FIRE,           // Classic vanilla fire explosion
        BALEFIRE,       // Same but with balefire
        DIGAMMA,
        DIGAMMA_CIRCUIT,
        LAVA,           // Again the same thing but lava
        LAVA_V,         // Again the same thing but volcanic lava
        LAVA_R,         // Again the same thing but radioactive lava
        ERRODE,         // Will turn select block into gravel or sand
        ALLMOD,         // Block placer attributes like fire are applied for all destroyed block
        ALLDROP,        // Miner TNT!
        NODROP,         // The opposite
        NOPARTICLE,
        NOSOUND,
        NOHURT
    }

    public ExplosionVNT(Level world, double x, double y, double z, float size) {
        this(world, x, y, z, size, null);
    }

    public ExplosionVNT(Level world, double x, double y, double z, float size, Entity exploder) {
        this.world = world;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.size = size;
        this.exploder = exploder;

        DamageSource damageSource = world.damageSources().explosion(exploder, exploder);

        this.compat = new Explosion(
                world,                     // Level
                exploder,                  // Entity source
                damageSource,              // DamageSource
                new ExplosionDamageCalculator(), // ExplosionDamageCalculator
                x, y, z,                   // position
                size,                      // radius
                false,                     // fire
                Explosion.BlockInteraction.KEEP // BlockInteraction
        );
    }

    public void explode() {
        // Сначала вызываем explode(), который вычисляет блоки и сущности
        this.compat.explode();

        // Затем вызываем finalizeExplosion() для визуальных эффектов и обработки
        if (!world.isClientSide) {
            this.compat.finalizeExplosion(true);
        } else {
            // На клиенте только эффекты
            this.compat.finalizeExplosion(true);
        }

        // Наша дополнительная логика
        boolean processBlocks = blockAllocator != null && blockProcessor != null;
        boolean processEntities = entityProcessor != null && playerProcessor != null;

        HashSet<BlockPos> affectedBlocks = null;
        HashMap<Player, Vec3> affectedPlayers = null;

        // Используем нашу логику для дополнительной обработки
        if (processBlocks) {
            affectedBlocks = blockAllocator.allocate(this, world, posX, posY, posZ, size);
            if (affectedBlocks != null && !affectedBlocks.isEmpty()) {
                blockProcessor.process(this, world, posX, posY, posZ, affectedBlocks);
            }
        }

        if (processEntities) {
            affectedPlayers = entityProcessor.process(this, world, posX, posY, posZ, size);
            if (affectedPlayers != null && !affectedPlayers.isEmpty()) {
                playerProcessor.process(this, world, posX, posY, posZ, affectedPlayers);
                this.compatPlayers.putAll(affectedPlayers);
            }
        }

        // Наши эффекты
        if (sfx != null) {
            for (IExplosionSFX fx : sfx) {
                fx.doEffect(this, world, posX, posY, posZ, size);
            }
        }
    }

    public ExplosionVNT setBlockAllocator(IBlockAllocator blockAllocator) {
        this.blockAllocator = blockAllocator;
        return this;
    }

    public ExplosionVNT setEntityProcessor(IEntityProcessor entityProcessor) {
        this.entityProcessor = entityProcessor;
        return this;
    }

    public ExplosionVNT setBlockProcessor(IBlockProcessor blockProcessor) {
        this.blockProcessor = blockProcessor;
        return this;
    }

    public ExplosionVNT setPlayerProcessor(IPlayerProcessor playerProcessor) {
        this.playerProcessor = playerProcessor;
        return this;
    }

    public ExplosionVNT setSFX(IExplosionSFX... sfx) {
        this.sfx = sfx;
        return this;
    }

    public ExplosionVNT makeStandard() {
        this.setBlockAllocator(new BlockAllocatorStandard());
        this.setBlockProcessor(new BlockProcessorStandard());
        this.setEntityProcessor(new EntityProcessorStandard());
        this.setPlayerProcessor(new PlayerProcessorStandard());
        this.setSFX(new ExplosionEffectStandard());
        return this;
    }

    public ExplosionVNT makeAmat() {
        this.setBlockAllocator(new BlockAllocatorStandard(this.size < 15 ? 16 : 32));
        this.setBlockProcessor(new BlockProcessorStandard().setNoDrop());
        this.setEntityProcessor(new EntityProcessorStandard()
                .withRangeMod(2F)
                .withDamageMod(new CustomDamageHandlerAmat(50F)));
        this.setPlayerProcessor(new PlayerProcessorStandard());
        this.setSFX(new ExplosionEffectAmat());
        return this;
    }

    // Вспомогательные методы для доступа к защищенным полям
    public Map<Player, Vec3> getHitPlayers() {
        return this.compatPlayers;
    }

    public void addToBlow(BlockPos pos) {
        if (!this.compat.getToBlow().contains(pos)) {
            this.compat.getToBlow().add(pos);
        }
    }

    /**
     * Добавляет все атрибуты из списка к взрыву
     * @param attribs список атрибутов
     * @return этот же объект ExplosionVNT для цепочечных вызовов
     */

    public ExplosionVNT addAllAttrib(ExAttrib[] attribs) {
        return addAllAttrib(Arrays.asList(attribs));
    }

    public ExplosionVNT addAllAttrib(List<ExAttrib> attribs) {

        // Создаём блок процессор с настройками (если ещё не создан)
        BlockProcessorStandard blockProc;
        if (this.blockProcessor instanceof BlockProcessorStandard bps) {
            blockProc = bps;
        } else {
            blockProc = new BlockProcessorStandard();
        }

        // Собираем мутаторы
        List<IBlockMutator> mutators = new ArrayList<>();

        for (ExAttrib attrib : attribs) {
            switch (attrib) {
                case FIRE:
                    mutators.add(new BlockMutatorFire(Blocks.FIRE));
                    break;

                case BALEFIRE:
                    mutators.add(new BlockMutatorFire(ModBlocks.BALEFIRE.get()));
                    break;

                case LAVA:
                    mutators.add(new BlockMutatorFire(Blocks.LAVA));
                    break;

                case LAVA_V:
                    mutators.add(new BlockMutatorFire(ModBlocks.VOLCANIC_LAVA_BLOCK.get()));
                    break;

                case LAVA_R:
                    mutators.add(new BlockMutatorFire(ModBlocks.RAD_LAVA_BLOCK.get()));
                    break;

                case ERRODE:
                    mutators.add(new BlockMutatorErrode());
                    break;

                case ALLDROP:
                    blockProc = blockProc.setAllDrop();
                    break;

                case NODROP:
                    blockProc = blockProc.setNoDrop();
                    break;

                case NOHURT:
                    this.setEntityProcessor(new EntityProcessorNoDamage());
                    break;

                case NOSOUND:
                    noSound = true;
                    break;
                case NOPARTICLE:
                    noParticle = true;
                    break;

                case DIGAMMA:
                case DIGAMMA_CIRCUIT:
                    this.makeAmat();
                    break;
            }
        }

        // Добавляем мутаторы в процессор
        for (IBlockMutator mutator : mutators) {
            blockProc = blockProc.withBlockEffect(mutator);
        }

        this.setBlockProcessor(blockProc);

        if (noParticle && noSound) {
            this.setSFX(new ExplosionEffectSilent());
        } else if (noParticle) {
            this.setSFX(new ExplosionEffectNoParticle());
        } else if (noSound) {
            this.setSFX(new ExplosionEffectNoSound());
        }

        return this;
    }

    // ExplosionEffectSilent.java - без звука и без частиц
    public class ExplosionEffectSilent implements IExplosionSFX {
        @Override
        public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
            // Ничего не делаем
        }
    }

    // ExplosionEffectNoParticle.java - только звук, без частиц
    public class ExplosionEffectNoParticle implements IExplosionSFX {
        @Override
        public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
            world.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    // ExplosionEffectNoSound.java - только частицы, без звука
    public class ExplosionEffectNoSound implements IExplosionSFX {
        @Override
        public void doEffect(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
            // Создаём частицы взрыва
            for (int i = 0; i < 100; i++) {
                world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 0, 0, 0);
            }
        }
    }
}