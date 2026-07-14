package com.hbm.items.weapon.sedna.factory;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockLayering;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorBulkie;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockMutatorDebris;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.ExplosionEffectWeapon;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.items.ModGunItems;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;
import com.hbm.items.weapon.sedna.impl.GunChargeThrower;
import com.hbm.tileentity.IRepairable;
import com.hbm.util.CompatExternal;
import com.hbm.util.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.particle.helper.ExplosionCreator;
import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
//import com.hbm.tileentity.IRepairable;
//import com.hbm.tileentity.IRepairable.EnumExtinguishType;
//import com.hbm.util.CompatExternal;

import com.hbm.util.ResLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.ForgeRegistries;

public class XFactoryTool {

    public static final ResourceLocation scope = ResLocation.ResLocation(RefStrings.MODID, "textures/misc/scope_tool.png");

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> EXTINGUISH_ENTITY = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            if (entity.isOnFire()) {
                entity.clearFire();
            }
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_CO2_HIT = (bullet, hit) -> {
        if(!bullet.level().isClientSide()) {
            if(hit.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) hit;
                Entity entity = entityHit.getEntity();

                if(entity instanceof net.minecraft.world.entity.monster.MagmaCube magmaCube) {
                    int size = magmaCube.getSize();
                    float explosionPower = switch (size) {
                        case 1 -> 1.0F;
                        case 2 -> 2.5F;
                        case 4 -> 5.0F;
                        default -> size * 1.25F;
                    };
                    bullet.level().explode(bullet,
                            entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                            explosionPower, Level.ExplosionInteraction.TNT);
                    magmaCube.discard();
                    bullet.discard();
                    return;
                }

                entity.clearFire();
                if(entity instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
                }
            }

            if(hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = null;
                if (hit instanceof BlockHitResult) {
                    blockHit = (BlockHitResult) hit;
                }
                BlockPos pos = Objects.requireNonNull(blockHit).getBlockPos();

                boolean anyEffect = false;

                for(int i = -2; i <= 2; i++)
                    for(int j = -1; j <= 1; j++)
                        for(int k = -2; k <= 2; k++) {
                            BlockPos offsetPos = pos.offset(i, j, k);
                            BlockState state = bullet.level().getBlockState(offsetPos);

                            // Тушим огонь
                            if(state.is(BlockTags.FIRE)) {
                                bullet.level().setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 3);
                                anyEffect = true;
                            }

                            // Превращаем ЛАВУ в ОБСИДИАН
                            if(state.getBlock() == Blocks.LAVA) {
                                bullet.level().setBlock(offsetPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                                anyEffect = true;
                            }

                            // Вулканическая лава -> обсидиан
                            if(state.getBlock() == ModBlocks.VOLCANIC_LAVA_BLOCK.get()) {
                                bullet.level().setBlock(offsetPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                                anyEffect = true;
                            }
                        }

                BlockEntity core = CompatExternal.getCoreFromPos(bullet.level(), pos);
                if(core instanceof IRepairable repairable) {
                    repairable.tryExtinguish(bullet.level(), pos, IRepairable.EnumExtinguishType.CO2);
                    return;
                }

                if(anyEffect) {
                    bullet.level().playSound(null,
                            bullet.getX(), bullet.getY(), bullet.getZ(),
                            SoundEvents.FIRE_EXTINGUISH,
                            SoundSource.BLOCKS, 1.0F, 1.2F + bullet.level().random.nextFloat() * 0.5F);
                }
                bullet.discard();
            }
        }
    };

    public static Consumer<Entity> LAMBDA_CO2_UPDATE = (bullet) -> {
        if(bullet.level().isClientSide()) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "vanillaExt");
            data.putString("mode", "cloud");
            data.putDouble("posX", bullet.getX());
            data.putDouble("posY", bullet.getY());
            data.putDouble("posZ", bullet.getZ());
            data.putDouble("mX", bullet.getDeltaMovement().x + bullet.level().random.nextGaussian() * 0.1);
            data.putDouble("mY", bullet.getDeltaMovement().y - 0.1 + bullet.level().random.nextGaussian() * 0.1);
            data.putDouble("mZ", bullet.getDeltaMovement().z + bullet.level().random.nextGaussian() * 0.1);
            data.putFloat("r", 0.9F);
            data.putFloat("g", 0.9F);
            data.putFloat("b", 0.9F);
            MainRegistry.proxy.effectNT(data);
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_FOAM_HIT = (bullet, hit) -> {
        if(!bullet.level().isClientSide() && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos pos = blockHit.getBlockPos();

            boolean anyEffect = false;

            // Обработка лавы вокруг
            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++)
                    for(int k = -1; k <= 1; k++) {
                        BlockPos offsetPos = pos.offset(i, j, k);
                        BlockState state = bullet.level().getBlockState(offsetPos);

                        // Тушим огонь
                        if(state.is(BlockTags.FIRE)) {
                            bullet.level().setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 3);
                            anyEffect = true;
                        }

                        // Превращаем ЛАВУ в ПЕНУ
                        if(state.getBlock() == Blocks.LAVA || state.getBlock() == ModBlocks.VOLCANIC_LAVA_BLOCK.get()) {
                            bullet.level().setBlock(offsetPos, ModBlocks.BLOCK_FOAM.get().defaultBlockState(), 3);
                            anyEffect = true;
                        }
                    }

            BlockEntity core = CompatExternal.getCoreFromPos(bullet.level(), pos);
            if(core instanceof IRepairable repairable) {
                repairable.tryExtinguish(bullet.level(), pos, IRepairable.EnumExtinguishType.FOAM);
                return;
            }

            Direction dir = blockHit.getDirection();
            BlockPos targetPos = bullet.level().random.nextBoolean() ?
                    pos.relative(dir) : pos;

            BlockState targetState = bullet.level().getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if(targetState.canBeReplaced() && ModBlocks.FOAM_LAYER.get().canSurvive(targetState, bullet.level(), targetPos)) {
                if(targetBlock != ModBlocks.FOAM_LAYER.get()) {
                    bullet.level().setBlock(targetPos, ModBlocks.FOAM_LAYER.get().defaultBlockState(), 3);
                    anyEffect = true;
                } else {
                    int currentLayers = targetState.getValue(BlockLayering.LAYERS);
                    if(currentLayers < 6) {
                        bullet.level().setBlock(targetPos, targetState.setValue(BlockLayering.LAYERS, currentLayers + 1), 3);
                        anyEffect = true;
                    } else {
                        bullet.level().setBlock(targetPos, ModBlocks.BLOCK_FOAM.get().defaultBlockState(), 3);
                        anyEffect = true;
                    }
                }
            }

            if(anyEffect) {
                bullet.level().playSound(null,
                        bullet.getX(), bullet.getY(), bullet.getZ(),
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS, 1.0F, 1.5F + bullet.level().random.nextFloat() * 0.5F);
            }
        }
    };

    public static Consumer<Entity> LAMBDA_FOAM_UPDATE = (bullet) -> {
        if(bullet.level().isClientSide()) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "vanillaExt");
            data.putString("mode", "blockdust");
            data.putString("block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(ModBlocks.BLOCK_FOAM.get())).toString());
            data.putDouble("posX", bullet.getX());
            data.putDouble("posY", bullet.getY());
            data.putDouble("posZ", bullet.getZ());
            data.putDouble("mX", bullet.getDeltaMovement().x + bullet.level().random.nextGaussian() * 0.1);
            data.putDouble("mY", bullet.getDeltaMovement().y - 0.2 + bullet.level().random.nextGaussian() * 0.1);
            data.putDouble("mZ", bullet.getDeltaMovement().z + bullet.level().random.nextGaussian() * 0.1);
            MainRegistry.proxy.effectNT(data);
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_SAND_HIT = (bullet, hit) -> {
        if(!bullet.level().isClientSide() && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos pos = blockHit.getBlockPos();

            boolean anyEffect = false;

            // Обработка лавы вокруг
            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++)
                    for(int k = -1; k <= 1; k++) {
                        BlockPos offsetPos = pos.offset(i, j, k);
                        BlockState state = bullet.level().getBlockState(offsetPos);

                        // Тушим огонь
                        if(state.is(BlockTags.FIRE)) {
                            bullet.level().setBlock(offsetPos, Blocks.AIR.defaultBlockState(), 3);
                            anyEffect = true;
                        }

                        // Превращаем ЛАВУ в ПЕСОК
                        if(state.getBlock() == Blocks.LAVA || state.getBlock() == ModBlocks.VOLCANIC_LAVA_BLOCK.get()) {
                            bullet.level().setBlock(offsetPos, Blocks.SAND.defaultBlockState(), 3);
                            anyEffect = true;
                        }
                    }

            BlockEntity core = CompatExternal.getCoreFromPos(bullet.level(), pos);
            if(core instanceof IRepairable repairable) {
                repairable.tryExtinguish(bullet.level(), pos, IRepairable.EnumExtinguishType.SAND);
                return;
            }

            Direction dir = blockHit.getDirection();
            BlockPos targetPos = bullet.level().random.nextBoolean() ?
                    pos.relative(dir) : pos;

            BlockState targetState = bullet.level().getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if((targetState.canBeReplaced() || targetBlock == ModBlocks.SAND_BORON_LAYER.get()) &&
                    ModBlocks.SAND_BORON_LAYER.get().canSurvive(targetState, bullet.level(), targetPos)) {
                if(targetBlock != ModBlocks.SAND_BORON_LAYER.get()) {
                    bullet.level().setBlock(targetPos, ModBlocks.SAND_BORON_LAYER.get().defaultBlockState(), 3);
                    anyEffect = true;
                } else {
                    int currentLayers = targetState.getValue(BlockLayering.LAYERS);
                    if(currentLayers < 6) {
                        bullet.level().setBlock(targetPos, targetState.setValue(BlockLayering.LAYERS, currentLayers + 1), 3);
                        anyEffect = true;
                    } else {
                        bullet.level().setBlock(targetPos, ModBlocks.SAND_BORON.get().defaultBlockState(), 3);
                        anyEffect = true;
                    }
                }
            }

            if(anyEffect) {
                bullet.level().playSound(null,
                        bullet.getX(), bullet.getY(), bullet.getZ(),
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS, 1.0F, 1.5F + bullet.level().random.nextFloat() * 0.5F);
            }
        }
    };

    public static Consumer<Entity> LAMBDA_SAND_UPDATE = (bullet) -> {
        if(bullet.level().isClientSide()) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "vanillaExt");
            data.putString("mode", "blockdust");
            data.putString("block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(ModBlocks.SAND_BORON.get())).toString());
            data.putDouble("posX", bullet.getX());
            data.putDouble("posY", bullet.getY());
            data.putDouble("posZ", bullet.getZ());
            data.putDouble("mX", bullet.getDeltaMovement().x + bullet.level().random.nextGaussian() * 0.1);
            data.putDouble("mY", bullet.getDeltaMovement().y - 0.2 + bullet.level().random.nextGaussian() * 0.1);
            data.putDouble("mZ", bullet.getDeltaMovement().z + bullet.level().random.nextGaussian() * 0.1);
            MainRegistry.proxy.effectNT(data);
        }
    };

    public static Consumer<Entity> LAMBDA_SET_HOOK = (entity) -> {
        EntityBulletBaseMK4 bullet = (EntityBulletBaseMK4) entity;
        if(!bullet.level().isClientSide() && bullet.tickCount < 2 && bullet.getOwner() instanceof Player player) {

        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_HOOK = (bullet, hit) -> {
        if(hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            Vec3 hitVec = blockHit.getLocation();
            Vec3 motion = bullet.getDeltaMovement().reverse().normalize().scale(0.05);

            bullet.setPos(
                    hitVec.x + motion.x,
                    hitVec.y + motion.y,
                    hitVec.z + motion.z
            );

            // Застревание
            bullet.setDeltaMovement(Vec3.ZERO);
            bullet.setNoGravity(true);
            bullet.setStuck(blockHit.getBlockPos(), blockHit.getDirection());
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_MORTAR = (bullet, hit) -> {
        if(hit.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            if(entityHit.getEntity() == bullet.getOwner()) return;
        }

        Vec3 hitVec = hit.getLocation();
        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitVec.x, hitVec.y, hitVec.z, 5, bullet.getOwner());
        vnt.setBlockAllocator(new BlockAllocatorBulkie(60, 8));
        vnt.setBlockProcessor(new BlockProcessorStandard());
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.damage)
                .setupPiercing(bullet.config.armorThresholdNegation, bullet.config.armorPiercingPercent));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        vnt.explode();
        bullet.discard();
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_MORTAR_CHARGE = (bullet, hit) -> {
        if(hit.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            if(entityHit.getEntity() == bullet.getOwner()) return;
        }

        Vec3 hitVec = hit.getLocation();
        ExplosionVNT vnt = new ExplosionVNT(bullet.level(), hitVec.x, hitVec.y, hitVec.z, 15, bullet.getOwner());
        vnt.setBlockAllocator(new BlockAllocatorStandard());
        vnt.setBlockProcessor(new BlockProcessorStandard().setNoDrop()
                .withBlockEffect(new BlockMutatorDebris(ModBlocks.BLOCK_SLAG.get())));
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.damage)
                .setupPiercing(bullet.config.armorThresholdNegation, bullet.config.armorPiercingPercent));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        ExplosionCreator.composeEffectSmall(bullet.level(), hitVec.x, hitVec.y + 0.5, hitVec.z);
        vnt.explode();
        bullet.discard();
    };


    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_CT = (stack, ctx) -> GunItem.setupRecoil(10, (float) (ctx.entity.getRandom().nextGaussian() * 1.5));

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_CT_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(-45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL));
        case RELOAD -> new BusAnimation()
                .addBus("RAISE", new BusAnimationSequence().addPos(-45, 0, 0, 500, IType.SIN_FULL).hold(2000).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("AMMO", new BusAnimationSequence().setPos(0, -10, -5).hold(500).addPos(0, 0, 5, 750, IType.SIN_FULL).addPos(0, 0, 0, 500, IType.SIN_UP).hold(4000))
                .addBus("TWIST", new BusAnimationSequence().setPos(0, 0, 25).hold(2000).addPos(0, 0, 0, 150));
        case INSPECT -> new BusAnimation()
                .addBus("TURN", new BusAnimationSequence().addPos(0, 60, 0, 500, IType.SIN_FULL).hold(1750).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("ROLL", new BusAnimationSequence().hold(750).addPos(0, 0, -90, 500, IType.SIN_FULL).hold(1000).addPos(0, 0, 0, 500, IType.SIN_FULL));
        default -> null;
    };

}