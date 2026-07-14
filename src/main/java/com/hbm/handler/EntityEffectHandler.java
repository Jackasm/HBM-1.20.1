package com.hbm.handler;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.config.GeneralConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.config.WorldConfig;
import com.hbm.datagen.worldgen.ModBiomes;
import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.EntityCreeperNuclear;
import com.hbm.entity.mob.EntityDuck;
import com.hbm.entity.mob.EntityQuackos;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.extprop.HbmLivingProps;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.items.armor.ArmorFSB;
import com.hbm.items.armor.IArmorModDash;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.main.MainRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.particle.helper.FlameCreator;
import com.hbm.potion.HbmPotion;
import com.hbm.sound.ModSounds;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ModDamageSource;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityEffectHandler {

    public static void onUpdate(LivingEntity entity) {

        if (entity.tickCount % 20 == 0) {
            HbmLivingProps.setRadBuf(entity, HbmLivingProps.getRadEnv(entity));
            HbmLivingProps.setRadEnv(entity, 0);
        }

        if (entity instanceof Player player && player == MainRegistry.proxy.me()) {
            ResourceKey<Biome> biomeKey = player.level().getBiome(player.blockPosition()).unwrapKey().orElse(null);

            if (biomeKey == ModBiomes.CRATER || biomeKey == ModBiomes.CRATER_INNER) {
                RandomSource rand = player.getRandom();
                for (int i = 0; i < 3; i++) {
                    player.level().addParticle(ParticleTypes.MYCELIUM,
                            player.getX() + rand.nextGaussian() * 3,
                            player.getY() + rand.nextGaussian() * 2,
                            player.getZ() + rand.nextGaussian() * 3,
                            0, 0, 0);
                }
            }
        }

        if (entity instanceof ServerPlayer serverPlayer) {
            HbmLivingProps props = HbmLivingProps.getData(entity);
            HbmPlayerProps.IHbmPlayerProps pprps = HbmPlayerProps.getData(serverPlayer);
            if (pprps == null) return;

            if (pprps.getShield() < pprps.getEffectiveMaxShield() && entity.tickCount > pprps.getLastDamage() + 60) {
                int tsd = entity.tickCount - (pprps.getLastDamage() + 60);
                pprps.setShield(pprps.getShield() + Math.min(pprps.getEffectiveMaxShield() - pprps.getShield(), 0.005F * tsd));
            }

            if (pprps.getShield() > pprps.getEffectiveMaxShield())
                pprps.setShield(pprps.getEffectiveMaxShield());

            PacketDispatcher.sendExtPropsToPlayer(props, pprps, serverPlayer);
        }

        if (!entity.level().isClientSide) {
            int timer = HbmLivingProps.getTimer(entity);
            if (timer > 0) {
                HbmLivingProps.setTimer(entity, timer - 1);

                if (timer == 1) {
                    ExplosionNukeSmall.explode(entity.level(), entity.getX(), entity.getY(), entity.getZ(), ExplosionNukeSmall.PARAMS_MEDIUM);
                }
            }

            if (GeneralConfig.enable528.get() && GeneralConfig.enable528NetherBurn.get() &&
                    entity instanceof Player && !entity.fireImmune() &&
                    entity.level().dimension() == Level.NETHER) {
                entity.setRemainingFireTicks(100); // 5 секунд огня
            }

            float radiation = 0;
            if (!entity.level().isClientSide && entity instanceof Player player) {
                ResourceKey<Biome> biomeKey = entity.level().getBiome(entity.blockPosition()).unwrapKey().orElse(null);

                if (biomeKey == ModBiomes.CRATER_OUTER) {
                    radiation = WorldConfig.craterBiomeOuterRad.get().floatValue();
                } else if (biomeKey == ModBiomes.CRATER) {
                    radiation = WorldConfig.craterBiomeRad.get().floatValue();
                } else if (biomeKey == ModBiomes.CRATER_INNER) {
                    radiation = WorldConfig.craterBiomeInnerRad.get().floatValue();
                }
            }

            if (entity.isInWaterRainOrBubble()) radiation *= WorldConfig.craterBiomeWaterMult.get().floatValue();

            if (radiation > 0) {
                ContaminationUtil.contaminate(entity, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, radiation / 20F);
            }
        }

        handleContamination(entity);
        handleContagion(entity);
        handleRadiationEffect(entity);
        handleRadiationFX(entity);
        handleDigamma(entity);
        handleLungDisease(entity);
        handleOil(entity);
        handlePollution(entity);
        handleTemperature(entity);

        handleDashing(entity);
        handlePlinking(entity);

        if (entity instanceof Player) handleFauxLadder((Player) entity);

        if (!entity.level().isClientSide && entity.tickCount % 20 == 0) {
            PacketDispatcher.syncLivingPropsToTracking(entity);
        }
    }

    private static void handleContamination(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        List<HbmLivingProps.ContaminationEffect> contamination = HbmLivingProps.getCont(entity);
        List<HbmLivingProps.ContaminationEffect> rem = new ArrayList<>();

        for (HbmLivingProps.ContaminationEffect con : contamination) {
            ContaminationUtil.contaminate(entity, ContaminationUtil.HazardType.RADIATION,
                    con.ignoreArmor ? ContaminationUtil.ContaminationType.RAD_BYPASS : ContaminationUtil.ContaminationType.CREATIVE,
                    con.getRad());
            con.time--;
            if (con.time <= 0) rem.add(con);
        }

        contamination.removeAll(rem);
    }

    private static void handleRadiationEffect(LivingEntity entity) {

        if (entity.isDeadOrDying()) return;
        if (entity.level().isClientSide) return;
        if (entity instanceof Player player && player.getAbilities().instabuild) return;

        Level world = entity.level();

        float eRad = HbmLivingProps.getRadiation(entity);

        /// TRANSFORMATIONS ///
        if (entity.getClass().equals(Creeper.class) && eRad >= 200 && entity.getHealth() > 0) {

            if (world.random.nextInt(3) == 0) {
                Objects.requireNonNull(world.getServer()).execute(() -> {
                    EntityCreeperNuclear creep = new EntityCreeperNuclear(ModEntities.CREEPER_NUCLEAR.get(), world);
                    creep.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                    world.addFreshEntity(creep);
                    entity.discard();
                });
            } else {
                entity.hurt(ModDamageSource.createDamageSource(ModDamageSource.RADIATION, null, null, world), 100F);
            }
            return;
        } else if (entity instanceof Cow && !(entity instanceof MushroomCow) && eRad >= 50) {
            Objects.requireNonNull(world.getServer()).execute(() -> {
                MushroomCow cow = new MushroomCow(EntityType.MOOSHROOM, world);
                cow.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                world.addFreshEntity(cow);
                entity.discard();
            });
            return;
        } else if (entity instanceof Villager && eRad >= 500) {
            Zombie zomb = new Zombie(EntityType.ZOMBIE, world);
            zomb.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
            world.addFreshEntity(zomb);
            entity.discard();
            return;
        }

        else if (entity.getClass().equals(EntityDuck.class) && eRad >= 200) {
            EntityQuackos quacc = new EntityQuackos(ModEntities.QUACKOS.get(), world);
            quacc.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
            world.addFreshEntity(quacc);
            entity.discard();
            return;
        }


        if (eRad < 200 || ContaminationUtil.isRadImmune(entity)) return;
        if (eRad > 2500) HbmLivingProps.setRadiation(entity, 2500);

        /// EFFECTS ///
        if (eRad >= 1000) {

            entity.hurt(ModDamageSource.createDamageSource(ModDamageSource.RADIATION, null, null, world), 1000F);
            HbmLivingProps.setRadiation(entity, 0);

            if (entity.getHealth() > 0) {
                entity.setHealth(0);
                entity.die(ModDamageSource.createDamageSource(ModDamageSource.RADIATION, null, null, world));
            }

            if (entity instanceof Player player && !player.level().isClientSide) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.RAD_DEATH.trigger(sp);
                }
            }

        } else if (eRad >= 800) {
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0));
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 2));
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 2));
            if (world.random.nextInt(500) == 0) entity.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 2));
            if (world.random.nextInt(700) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 3 * 20, 1));

        } else if (eRad >= 600) {
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0));
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 2));
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 20, 2));
            if (world.random.nextInt(500) == 0) entity.addEffect(new MobEffectInstance(MobEffects.POISON, 3 * 20, 1));

        } else if (eRad >= 400) {
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 30, 0));
            if (world.random.nextInt(500) == 0) entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * 20, 0));
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 1));

        } else if (eRad >= 200) {
            if (world.random.nextInt(300) == 0) entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 20, 0));
            if (world.random.nextInt(500) == 0) entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5 * 20, 0));

            if (entity instanceof Player player && !player.level().isClientSide) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.RAD_POISON.trigger(sp);
                }
            }
        }
    }

    private static void handleRadiationFX(LivingEntity entity) {

        Level world = entity.level();

        if (!world.isClientSide) {

            if (ContaminationUtil.isRadImmune(entity)) return;

            int ix = Mth.floor(entity.getX());
            int iy = Mth.floor(entity.getY());
            int iz = Mth.floor(entity.getZ());

            float rad = RadiationEvents.getRadiation(world, new BlockPos(ix, iy, iz));

            if (world.dimension() == Level.NETHER && RadiationConfig.hellRad.get() > 0 && rad < RadiationConfig.hellRad.get())
                rad = (float) (double) RadiationConfig.hellRad.get();

            if (rad > 0) ContaminationUtil.contaminate(entity, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, rad / 20F);

            if (entity instanceof Player player && player.getAbilities().instabuild) return;

            RandomSource rand = RandomSource.create(entity.getUUID().hashCode());

            int r600 = rand.nextInt(600);
            int r1200 = rand.nextInt(1200);

            if (HbmLivingProps.getRadiation(entity) > 600) {

                if ((world.getGameTime() + r600) % 600 < 20 && canVomit(entity)) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.putString("type", "vomit");
                    nbt.putString("mode", "blood");
                    nbt.putInt("count", 25);
                    nbt.putInt("entity", entity.getId());
                    PacketDispatcher.sendAuxParticleNT(nbt, 0, 0, 0, world, entity.blockPosition());

                    if ((world.getGameTime() + r600) % 600 == 1) {
                        world.playSound(null, ix, iy, iz, ModSounds.PLAYER_VOMIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 19));
                    }
                }

            } else if (HbmLivingProps.getRadiation(entity) > 200 && (world.getGameTime() + r1200) % 1200 < 20 && canVomit(entity)) {

                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "vomit");
                nbt.putString("mode", "normal");
                nbt.putInt("count", 15);
                nbt.putInt("entity", entity.getId());
                PacketDispatcher.sendAuxParticleNT(nbt, 0, 0, 0, world, entity.blockPosition());

                if ((world.getGameTime() + r1200) % 1200 == 1) {
                    world.playSound(null, ix, iy, iz, ModSounds.PLAYER_VOMIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 19));
                }

            }

            if (HbmLivingProps.getRadiation(entity) > 900 && (world.getGameTime() + rand.nextInt(10)) % 10 == 0) {

                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "sweat");
                nbt.putInt("count", 1);
                nbt.putInt("block", BuiltInRegistries.BLOCK.getId(Blocks.REDSTONE_BLOCK));
                nbt.putInt("entity", entity.getId());
                PacketDispatcher.sendAuxParticleNT(nbt, 0, 0, 0, world, entity.blockPosition());
            }
        } else {
            float radiation = HbmLivingProps.getRadiation(entity);

            if (entity instanceof Player && radiation > 600) {

                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "radiation");
                nbt.putInt("count", radiation > 900 ? 4 : radiation > 800 ? 2 : 1);
                MainRegistry.proxy.effectNT(nbt);
            }
        }
    }

    private static void handleDigamma(LivingEntity entity) {

        if (!entity.level().isClientSide) {

            float digamma = HbmLivingProps.getDigamma(entity);

            if (digamma < 0.01F)
                return;

            int chance = Math.max(10 - (int) (digamma), 1);

            if (chance == 1 || entity.getRandom().nextInt(chance) == 0) {

                CompoundTag data = new CompoundTag();
                data.putString("type", "sweat");
                data.putInt("count", 1);
                data.putInt("block", BuiltInRegistries.BLOCK.getId(Blocks.SOUL_SAND));
                data.putInt("entity", entity.getId());
                PacketDispatcher.sendAuxParticleNT(data, 0, 0, 0, entity.level(), entity.blockPosition());
            }
        }
    }

    private static void handleContagion(LivingEntity entity) {

        Level world = entity.level();

        if (!world.isClientSide) {

            RandomSource rand = entity.getRandom();
            int minute = 60 * 20;
            int hour = 60 * minute;

            int contagion = HbmLivingProps.getContagion(entity);

            if (entity instanceof Player player) {

                int randSlot = rand.nextInt(player.getInventory().items.size());
                ItemStack stack = player.getInventory().items.get(randSlot);

                if (rand.nextInt(100) == 0) {
                    // В 1.20.1 броня хранится в armorItems
                    stack = player.getInventory().armor.get(rand.nextInt(4));
                }

                // only affect unstackables (e.g. tools and armor) so that the NBT tag's stack restrictions isn't noticeable
                if (!stack.isEmpty() && stack.getMaxStackSize() == 1) {

                    if (contagion > 0) {

                        CompoundTag tag = stack.getOrCreateTag();
                        tag.putBoolean("ntmContagion", true);

                    } else {

                        if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).getBoolean("ntmContagion")) {
                            if (!ArmorUtil.checkForHaz2(player) || !ArmorRegistry.hasProtection(player, 3, ArmorRegistry.HazardClass.BACTERIA))
                                HbmLivingProps.setContagion(player, 3 * hour);
                        }
                    }
                }
            }

            if (contagion > 0) {
                HbmLivingProps.setContagion(entity, contagion - 1);

                // aerial transmission only happens once a second 5 minutes into the contagion
                if (contagion < (2 * hour + 55 * minute) && contagion % 20 == 0) {

                    double range = entity.isInWaterOrRain() ? 16D : 2D; // avoid rain, just avoid it

                    AABB aabb = entity.getBoundingBox().inflate(range);
                    List<Entity> list = world.getEntities(entity, aabb);

                    for (Entity ent : list) {

                        if (ent instanceof LivingEntity living) {
                            if (HbmLivingProps.getContagion(living) <= 0) {
                                if (!ArmorUtil.checkForHaz2(living) || !ArmorRegistry.hasProtection(living, 3, ArmorRegistry.HazardClass.BACTERIA))
                                    HbmLivingProps.setContagion(living, 3 * hour);
                            }
                        }

                        if (ent instanceof ItemEntity item) {
                            ItemStack stack = item.getItem();

                            CompoundTag tag = stack.getOrCreateTag();
                            tag.putBoolean("ntmContagion", true);
                        }
                    }
                }

                // one hour in, add rare and subtle screen fuckery
                if (contagion < 2 * hour && rand.nextInt(1000) == 0) {
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 0));
                }

                // two hours in, give 'em the full blast
                if (contagion < 1 * hour && rand.nextInt(100) == 0) {
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 4));
                }

                // T-30 minutes, take damage every 20 seconds
                if (contagion < 30 * minute && rand.nextInt(400) == 0) {
                    entity.hurt(ModDamageSource.createDamageSource(ModDamageSource.MKU, null, null, world), 1F);
                }

                // T-5 minutes, take damage every 5 seconds
                if (contagion < 5 * minute && rand.nextInt(100) == 0) {
                    entity.hurt(ModDamageSource.createDamageSource(ModDamageSource.MKU, null, null, world), 2F);
                }

                if (contagion < 30 * minute && (contagion + entity.getId()) % 200 < 20 && canVomit(entity)) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.putString("type", "vomit");
                    nbt.putString("mode", "blood");
                    nbt.putInt("count", 25);
                    nbt.putInt("entity", entity.getId());
                    PacketDispatcher.sendAuxParticleNT(nbt, 0, 0, 0, world, entity.blockPosition());

                    if ((contagion + entity.getId()) % 200 == 19)
                        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.PLAYER_VOMIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                }

                // end of contagion, drop dead
                if (contagion == 0) {
                    entity.hurt(ModDamageSource.createDamageSource(ModDamageSource.MKU, null, null, world), 1000F);
                }
            }
        }
    }

    private static void handleLungDisease(LivingEntity entity) {

        if (entity.level().isClientSide)
            return;

        if (entity instanceof Player player && player.getAbilities().instabuild) {
            HbmLivingProps.setBlackLung(entity, 0);
            HbmLivingProps.setAsbestos(entity, 0);
            return;
        } else {

            int bl = HbmLivingProps.getBlackLung(entity);

            if (bl > 0 && bl < HbmLivingProps.MAX_BLACK_LUNG * 0.5)
                HbmLivingProps.setBlackLung(entity, HbmLivingProps.getBlackLung(entity) - 1);
        }

        double blacklung = Math.min(HbmLivingProps.getBlackLung(entity), HbmLivingProps.MAX_BLACK_LUNG);
        double asbestos = Math.min(HbmLivingProps.getAsbestos(entity), HbmLivingProps.MAX_ASBESTOS);
        double soot = PollutionHandler.getPollution(entity.level(),
                new BlockPos(
                Mth.floor(entity.getX()),
                Mth.floor(entity.getY() + entity.getEyeHeight()),
                Mth.floor(entity.getZ())
        ), PollutionType.SOOT);

        if (!(entity instanceof Player)) soot = 0;

        if (ArmorRegistry.hasProtection(entity, 3, ArmorRegistry.HazardClass.PARTICLE_COARSE)) soot = 0;

        boolean coughs = blacklung / HbmLivingProps.MAX_BLACK_LUNG > 0.25D || asbestos / HbmLivingProps.MAX_ASBESTOS > 0.25D || soot > 30;

        if (!coughs)
            return;

        boolean coughsCoal = blacklung / HbmLivingProps.MAX_BLACK_LUNG > 0.5D;
        boolean coughsALotOfCoal = blacklung / HbmLivingProps.MAX_BLACK_LUNG > 0.8D;
        boolean coughsBlood = asbestos / HbmLivingProps.MAX_ASBESTOS > 0.75D || blacklung / HbmLivingProps.MAX_BLACK_LUNG > 0.75D;

        double blacklungDelta = 1D - (blacklung / (double) HbmLivingProps.MAX_BLACK_LUNG);
        double asbestosDelta = 1D - (asbestos / (double) HbmLivingProps.MAX_ASBESTOS);
        double sootDelta = 1D - Math.min(soot / 100, 1D);

        double total = 1 - (blacklungDelta * asbestosDelta);

        Level world = entity.level();

        if (total > 0.75D) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2));
        }

        if (total > 0.95D) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        }

        total = 1 - (blacklungDelta * asbestosDelta * sootDelta);
        int freq = Math.max((int) (1000 - 950 * total), 20);

        if (world.getGameTime() % freq == entity.getId() % freq) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.PLAYER_COUGH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            if (coughsBlood) {
                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "vomit");
                nbt.putString("mode", "blood");
                nbt.putInt("count", 5);
                nbt.putInt("entity", entity.getId());
                PacketDispatcher.sendAuxParticleNT(nbt, 0, 0, 0, world, entity.blockPosition());
            }

            if (coughsCoal) {
                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "vomit");
                nbt.putString("mode", "smoke");
                nbt.putInt("count", coughsALotOfCoal ? 50 : 10);
                nbt.putInt("entity", entity.getId());
                PacketDispatcher.sendAuxParticleNT(nbt, 0, 0, 0, world, entity.blockPosition());
            }
        }
    }

    private static void handleOil(LivingEntity entity) {

        if (entity.level().isClientSide)
            return;

        int oil = HbmLivingProps.getOil(entity);

        if (oil > 0) {

            if (entity.isOnFire()) {
                HbmLivingProps.setOil(entity, 0);
                entity.level().explode(null, entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), 3F, Level.ExplosionInteraction.NONE);
            } else {
                HbmLivingProps.setOil(entity, oil - 1);
            }

            if (entity.tickCount % 5 == 0) {
                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "sweat");
                nbt.putInt("count", 1);
                nbt.putInt("block", BuiltInRegistries.BLOCK.getId(Blocks.COAL_BLOCK));
                nbt.putInt("entity", entity.getId());
                PacketDispatcher.sendAuxParticleNT(nbt, 0, 0, 0, entity.level(), entity.blockPosition());
            }
        }
    }

    private static void handlePollution(LivingEntity entity) {

        if (!RadiationConfig.enablePollution.get()) return;

        if (RadiationConfig.enablePoison.get() && !ArmorRegistry.hasProtection(entity, 3, ArmorRegistry.HazardClass.GAS_BLISTERING) && entity.tickCount % 60 == 0) {

            float poison = PollutionHandler.getPollution(entity.level(),
                    new BlockPos(
                            Mth.floor(entity.getX()),
                            Mth.floor(entity.getY() + entity.getEyeHeight()),
                            Mth.floor(entity.getZ())
                    ),
                    PollutionType.POISON);

            if (poison > 10) {

                if (poison < 25) {
                    entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                } else if (poison < 50) {
                    entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                } else {
                    entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
                }
            }
        }

        if (RadiationConfig.enableLeadPoisoning.get() && !ArmorRegistry.hasProtection(entity, 3, ArmorRegistry.HazardClass.PARTICLE_FINE) && entity.tickCount % 60 == 0) {

            float poison = PollutionHandler.getPollution(entity.level(),
                    new BlockPos(
                            Mth.floor(entity.getX()),
                            Mth.floor(entity.getY() + entity.getEyeHeight()),
                            Mth.floor(entity.getZ())
                    ),
                    PollutionType.HEAVYMETAL);

            if (poison > 25) {

                int amplifier;
                if (poison < 50) {
                    amplifier = 0;
                } else if (poison < 75) {
                    amplifier = 2;
                } else {
                    amplifier = 2;
                }
                entity.addEffect(new MobEffectInstance(HbmPotion.LEAD.get(), 100, amplifier));
            }
        }
    }

    private static void handleDashing(Entity entity) {

        if (entity instanceof Player player) {

            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
            if (props == null) return;

            props.setTotalDashCount(0);

            ArmorFSB chestplate = null;

            int armorDashCount = 0;
            int armorModDashCount = 0;

            if (ArmorFSB.hasFSBArmor(player)) {
                ItemStack plate = player.getInventory().armor.get(2); // НАГРУДНИК
                if (plate.getItem() instanceof ArmorFSB) {
                    chestplate = (ArmorFSB) plate.getItem();
                }
            }

            if (chestplate != null)
                armorDashCount = chestplate.dashCount;

            for (int armorSlot = 0; armorSlot < 4; armorSlot++) {
                ItemStack armorStack = player.getInventory().armor.get(armorSlot);

                if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem) {

                    ItemStack[] mods = ArmorModHandler.pryMods(armorStack);
                    for (ItemStack mod : mods) {
                        if (mod != null && !mod.isEmpty() && mod.getItem() instanceof IArmorModDash) {
                            int count = ((IArmorModDash) mod.getItem()).getDashes();
                            armorModDashCount += count;
                        }
                    }
                }
            }

            int dashCount = armorDashCount + armorModDashCount;
            boolean dashActivated = props.getKeyPressed(HbmKeybinds.EnumKeybind.DASH);

            if (dashCount * 30 < props.getStamina()) props.setStamina(dashCount * 30);

            if (dashCount > 0) {

                int perDash = 30;
                int stamina = props.getStamina();

                props.setTotalDashCount(dashCount);

                if (props.getDashCooldown() <= 0) {

                    if (dashActivated && stamina >= perDash) {

                        Vec3 lookingIn = player.getLookAngle();
                        Vec3 strafeVec = player.getLookAngle().yRot((float) Math.PI * 0.5F);

                        int forward = (int) Math.signum(player.zza); // moveForward в 1.20.1
                        int strafe = (int) Math.signum(player.xxa); // moveStrafing в 1.20.1

                        if (forward == 0 && strafe == 0) forward = 1;

                        player.setDeltaMovement(
                                player.getDeltaMovement().x + lookingIn.x * forward + strafeVec.x * strafe,
                                0,
                                player.getDeltaMovement().z + lookingIn.z * forward + strafeVec.z * strafe
                        );
                        player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                        player.fallDistance = 0F;
                        player.playSound(ModSounds.ROCKET_FLAME.get(), 1.0F, 1.0F);

                        props.setDashCooldown(HbmPlayerProps.dashCooldownLength);
                        stamina -= perDash;
                    }
                } else {
                    props.setDashCooldown(props.getDashCooldown() - 1);
                    props.setKeyPressed(HbmKeybinds.EnumKeybind.DASH, false);
                }

                if (stamina < props.getTotalDashCount() * perDash) {
                    stamina++;

                    if (stamina % perDash == perDash - 1) {
                        player.playSound(ModSounds.TECH_BOOP.get(), 1.0F, (1.0F + ((1F / 12F) * ((float) stamina / perDash))));
                        stamina++;
                    }
                }

                props.setStamina(stamina);
            }
        }
    }

    private static void handlePlinking(Entity entity) {

        if (entity instanceof Player player) {
            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
            if (props == null) return;
            if (props.getPlinkCooldown() > 0)
                props.setPlinkCooldown(props.getPlinkCooldown() - 1);
        }
    }

    private static void handleFauxLadder(Player player) {

        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);

        if (props.isOnLadder()) {
            double climbSpeed = 0.15;

            Vec3 motion = player.getDeltaMovement();

            if (motion.x < -climbSpeed) motion = new Vec3(-climbSpeed, motion.y, motion.z);
            if (motion.x > climbSpeed) motion = new Vec3(climbSpeed, motion.y, motion.z);
            if (motion.z < -climbSpeed) motion = new Vec3(motion.x, motion.y, -climbSpeed);
            if (motion.z > climbSpeed) motion = new Vec3(motion.x, motion.y, climbSpeed);

            player.setDeltaMovement(motion);
            player.fallDistance = 0.0F;

            if (motion.y < -climbSpeed) {
                player.setDeltaMovement(new Vec3(player.getDeltaMovement().x, -climbSpeed, player.getDeltaMovement().z));
            }

            if (player.isShiftKeyDown() && motion.y < 0.0D) {
                player.setDeltaMovement(new Vec3(player.getDeltaMovement().x, 0.0D, player.getDeltaMovement().z));
            }

            if (player.horizontalCollision) {
                player.setDeltaMovement(new Vec3(player.getDeltaMovement().x, 0.2D, player.getDeltaMovement().z));
            }

            props.setIsOnLadder(false);

            if (!player.level().isClientSide) {
                ArmorUtil.resetFlightTime(player);
            }
        }
    }

    private static void handleTemperature(LivingEntity living) {

        HbmLivingProps props = HbmLivingProps.getData(living);
        if (props == null) return;

        Level level = living.level();
        if(living.isInvulnerableTo(living.level().damageSources().onFire())){
            props.fire = 0;
            props.phosphorus = 0;
        }

        if(props.fire > 0) {
            props.fire--;
            if((living.tickCount + living.getId()) % 15 == 0) {
                living.level().playSound(
                        null,
                        living.getX(),
                        living.getY() + living.getBbHeight() / 2,
                        living.getZ(),
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.AMBIENT,
                        1F,
                        1.5F + living.level().random.nextFloat() * 0.5F
                );
            }
            if((living.tickCount + living.getId()) % 40 == 0) {
                living.hurt(living.level().damageSources().onFire(), 2F);
            }
            FlameCreator.composeEffect(
                    living.level(),
                    living.getX() - living.getBbWidth() / 2 + living.getBbWidth() * living.level().random.nextDouble(),
                    living.getY() + living.level().random.nextDouble() * living.getBbHeight(),
                    living.getZ() - living.getBbWidth() / 2 + living.getBbWidth() * living.level().random.nextDouble(),
                    FlameCreator.META_FIRE
            );
        }

        if(props.phosphorus > 0) {
            props.phosphorus--;
            if((living.tickCount + living.getId()) % 15 == 0) {
                living.level().playSound(
                        null,
                        living.getX(),
                        living.getY() + living.getBbHeight() / 2,
                        living.getZ(),
                        net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                        net.minecraft.sounds.SoundSource.AMBIENT,
                        1F,
                        1.5F + level.random.nextFloat() * 0.5F
                );
            }
            if((living.tickCount + living.getId()) % 40 == 0) {
                living.hurt(living.level().damageSources().onFire(), 5F);
            }
            FlameCreator.composeEffect(
                    living.level(),
                    living.getX() - living.getBbWidth() / 2 + living.getBbWidth() * level.random.nextDouble(),
                    living.getY() + level.random.nextDouble() * living.getBbHeight(),
                    living.getZ() - living.getBbWidth() / 2 + living.getBbWidth() * level.random.nextDouble(),
                    FlameCreator.META_FIRE
            );
        }

        if(props.balefire > 0) {
            props.balefire--;
            if((living.tickCount + living.getId()) % 15 == 0) {
                living.level().playSound(
                        null,
                        living.getX(),
                        living.getY() + living.getBbHeight() / 2,
                        living.getZ(),
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.AMBIENT,
                        1F,
                        1.5F + living.level().random.nextFloat() * 0.5F
                );
            }
            ContaminationUtil.contaminate(living, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, 5F);
            if((living.tickCount + living.getId()) % 20 == 0) {
                living.hurt(living.level().damageSources().onFire(), 5F);
            }
            FlameCreator.composeEffect(
                    living.level(),
                    living.getX() - living.getBbWidth() / 2 + living.getBbWidth() * living.level().random.nextDouble(),
                    living.getY() + living.level().random.nextDouble() * living.getBbHeight(),
                    living.getZ() - living.getBbWidth() / 2 + living.getBbWidth() * living.level().random.nextDouble(),
                    FlameCreator.META_BALEFIRE
            );
        }

        if(props.blackFire > 0) {
            props.blackFire--;
            if((living.tickCount + living.getId()) % 10 == 0) {
                living.level().playSound(
                        null,
                        living.getX(),
                        living.getY() + living.getBbHeight() / 2,
                        living.getZ(),
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.AMBIENT,
                        1F,
                        1.5F + living.level().random.nextFloat() * 0.5F
                );
            }
            ContaminationUtil.contaminate(living, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, 5F);
            if((living.tickCount + living.getId()) % 10 == 0) {
                living.hurt(living.level().damageSources().onFire(), 10F);
            }
            FlameCreator.composeEffect(
                    living.level(),
                    living.getX() - living.getBbWidth() / 2 + living.getBbWidth() * living.level().random.nextDouble(),
                    living.getY() + living.level().random.nextDouble() * living.getBbHeight(),
                    living.getZ() - living.getBbWidth() / 2 + living.getBbWidth() * living.level().random.nextDouble(),
                    FlameCreator.META_BLACK
            );
        }

        if(props.fire > 0 || props.phosphorus > 0 || props.balefire > 0)
            if(!living.isAlive()) ConfettiUtil.decideConfetti(living, living.level().damageSources().onFire());

    }

    private static boolean canVomit(Entity entity) {
        return entity.getType().getCategory() != MobCategory.WATER_CREATURE &&
                entity.getType().getCategory() != MobCategory.WATER_AMBIENT &&
                entity.getType().getCategory() != MobCategory.UNDERGROUND_WATER_CREATURE;
    }
}