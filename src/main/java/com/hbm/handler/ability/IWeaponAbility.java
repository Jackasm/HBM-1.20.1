package com.hbm.handler.ability;


import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.deco.TileEntityBobble;
import com.hbm.util.ContaminationUtil;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface IWeaponAbility extends IBaseAbility {

    void onHit(int level, Level world, Player player, Entity victim, Item tool);

    int SORT_ORDER_BASE = 200;

    // region implementations
    IWeaponAbility NONE = new IWeaponAbility() {
        @Override
        public String getName() {
            return "";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
        }
    };

    IWeaponAbility PHOSPHORUS = new IWeaponAbility() {
        @Override
        public String getName() {
            return "weapon.ability.phosphorus";
        }

        private final int[] durationAtLevel = {60, 90};

        @Override
        public int levels() {
            return durationAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + durationAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 4;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            int duration = durationAtLevel[level];

            if (victim instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(HbmPotion.PHOSPHORUS.get(), duration * 20, 4));
            }
        }
    };

    IWeaponAbility RADIATION = new IWeaponAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.radiation";
        }

        private final float[] radAtLevel = {15F, 50F, 500F};

        @Override
        public int levels() {
            return radAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + radAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 1;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            if(victim instanceof LivingEntity living) {
                ContaminationUtil.contaminate(living, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, radAtLevel[level]);
            }
        }
    };

    IWeaponAbility VAMPIRE = new IWeaponAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.vampire";
        }

        private final float[] amountAtLevel = {2F, 3F, 5F, 10F, 50F};

        @Override
        public int levels() {
            return amountAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + amountAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 2;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            float amount = amountAtLevel[level];

            if(victim instanceof LivingEntity living) {
                if(living.getHealth() <= 0) return;

                living.hurt(living.damageSources().magic(), amount);
                if(living.getHealth() <= 0) {
                    living.die(living.damageSources().magic());
                }
                player.heal(amount);
            }
        }
    };

    IWeaponAbility STUN = new IWeaponAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.stun";
        }

        private final int[] durationAtLevel = {2, 3, 5, 10, 15};

        @Override
        public int levels() {
            return durationAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + durationAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 3;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            int duration = durationAtLevel[level];

            if(victim instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration * 20, 4));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration * 20, 4));
            }
        }
    };

    IWeaponAbility FIRE = new IWeaponAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.fire";
        }

        private final int[] durationAtLevel = {5, 10};

        @Override
        public int levels() {
            return durationAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + durationAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 4;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            if(victim instanceof LivingEntity) {
                victim.setSecondsOnFire(durationAtLevel[level]);
            }
        }
    };

    IWeaponAbility CHAINSAW = new IWeaponAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.chainsaw";
        }

        private final int[] dividerAtLevel = {15, 10};

        @Override
        public int levels() {
            return dividerAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (1:" + dividerAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 5;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            int divider = dividerAtLevel[level];

            if(victim instanceof LivingEntity living && living.getHealth() <= 0.0F) {
                int count = Math.min((int) Math.ceil(living.getMaxHealth() / divider), 250);

                for(int i = 0; i < count; i++) {
                    living.spawnAtLocation(new ItemStack(ModItems.NITRA_SMALL.get()), 1);
                    world.addFreshEntity(new ExperienceOrb(world, living.getX(), living.getY(), living.getZ(), 1));
                }

                if(world instanceof ServerLevel serverLevel) {
                    RandomSource rand = serverLevel.random;
                    for(int i = 0; i < count * 4; i++) {
                        double dx = (rand.nextDouble() - 0.5) * 0.2;
                        double dy = (rand.nextDouble() - 0.5) * 0.2;
                        double dz = (rand.nextDouble() - 0.5) * 0.2;

                        serverLevel.sendParticles(
                                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()),
                                living.getX(),
                                living.getY() + living.getBbHeight() * 0.5,
                                living.getZ(),
                                1, dx, dy, dz, 0.1
                        );
                    }
                }

                world.playSound(null, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(),
                         ModSounds.CHAINSAW.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
    };

    IWeaponAbility BEHEADER = new IWeaponAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.beheader";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 6;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            if(victim instanceof LivingEntity living && living.getHealth() <= 0.0F) {
                if(living instanceof Skeleton) {
                    // Skeleton type check removed in 1.20.1 - use regular skeleton
                    living.spawnAtLocation(new ItemStack(Items.SKELETON_SKULL), 0.0F);
                } else if(living instanceof Zombie) {
                    living.spawnAtLocation(new ItemStack(Items.ZOMBIE_HEAD), 0.0F);
                } else if(living instanceof Creeper) {
                    living.spawnAtLocation(new ItemStack(Items.CREEPER_HEAD), 0.0F);
                } else if(living instanceof MagmaCube) {
                    living.spawnAtLocation(new ItemStack(Items.MAGMA_CREAM, 3), 0.0F);
                } else if(living instanceof Slime) {
                    living.spawnAtLocation(new ItemStack(Items.SLIME_BALL, 3), 0.0F);
                } else if(living instanceof Player) {
                    ItemStack head = new ItemStack(Items.PLAYER_HEAD);
                    head.getOrCreateTag().putString("SkullOwner", living.getDisplayName().getString());
                    living.spawnAtLocation(head, 0.0F);
                } else {
                    living.spawnAtLocation(new ItemStack(Items.ROTTEN_FLESH, 3), 0.0F);
                    living.spawnAtLocation(new ItemStack(Items.BONE, 2), 0.0F);
                }
            }
        }
    };

    IWeaponAbility BOBBLE = new IWeaponAbility() {
        @Override
        public String getName() {
            return "weapon.ability.bobble";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 9;
        }

        @Override
        public void onHit(int level, Level world, Player player, Entity victim, Item tool) {
            if (victim instanceof Mob mob && mob.getHealth() <= 0.0F) {
                int chance = 1000;

                if (mob.getMaxHealth() > 20) {
                    chance = 750;
                }

                if (world.random.nextInt(chance) == 0) {
                    int type = world.random.nextInt(TileEntityBobble.BobbleType.values().length - 1) + 1;
                    ItemStack stack = new ItemStack(ModBlocks.BOBBLEHEAD.get(), 1);
                    stack.setDamageValue(type);
                    mob.spawnAtLocation(stack, 0.0F);
                }
            }
        }
    };

    IWeaponAbility[] ABILITIES = {NONE, RADIATION, VAMPIRE, STUN, FIRE, PHOSPHORUS,CHAINSAW, BEHEADER, BOBBLE};

    static IWeaponAbility getByName(String name) {
        for(IWeaponAbility ability : ABILITIES) {
            if(ability.getName().equals(name)) {
                return ability;
            }
        }
        return NONE;
    }
}