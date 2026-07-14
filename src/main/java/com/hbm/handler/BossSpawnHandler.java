package com.hbm.handler;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.config.MobConfig;
import com.hbm.config.WorldConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.*;
import com.hbm.entity.projectile.EntityMeteor;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.ModItems;
import com.hbm.util.ContaminationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class BossSpawnHandler {

    private static final Random meteorRand = new Random();

    public static void rollTheDice(Level world) {
        if (world.isClientSide) return;

        // MASK MAN
        if (MobConfig.enableMaskman && world.getGameTime() % MobConfig.maskmanDelay == 0) {
            if (world.random.nextInt(MobConfig.maskmanChance) == 0 && !world.players().isEmpty()) {
                Player player = world.players().get(world.random.nextInt(world.players().size()));
                if (!(player instanceof ServerPlayer serverPlayer)) return;

                boolean hasAcidizer = false;
                // Проверяем статы игрока
                var stats = serverPlayer.getStats();
                Item acidizerItem = Item.byBlock(ModBlocks.MACHINE_CRYSTALLIZER.get());
                int id = Item.getId(acidizerItem);

                if (!GeneralConfig.enableStatReRegistering.get()) {
                    hasAcidizer = true;
                } else {
                    hasAcidizer = stats.getValue(Stats.ITEM_CRAFTED.get(acidizerItem)) > 0 ||
                            stats.getValue(Stats.ITEM_USED.get(acidizerItem)) > 0;
                }

                if (hasAcidizer &&
                        ContaminationUtil.getRads(player) >= MobConfig.maskmanMinRad &&
                        (world.getHeight(Heightmap.Types.WORLD_SURFACE,
                                (int) player.getX(), (int) player.getZ()) > player.getY() + 3 ||
                                !MobConfig.maskmanUnderground)) {

                    player.sendSystemMessage(Component.literal("The mask man is about to claim another victim.")
                            .withStyle(ChatFormatting.RED));

                    double spawnX = player.getX() + world.random.nextGaussian() * 20;
                    double spawnZ = player.getZ() + world.random.nextGaussian() * 20;
                    double spawnY = world.getHeight(Heightmap.Types.WORLD_SURFACE,
                            (int) spawnX, (int) spawnZ);
                }
            }
        }

            // FBI RAIDS
        if (MobConfig.enableRaids && world.getGameTime() % MobConfig.raidDelay == 0) {
            if (world.random.nextInt(MobConfig.raidChance) == 0 && !world.players().isEmpty()) {
                Player player = world.players().get(world.random.nextInt(world.players().size()));

                long mark = player.getPersistentData().getLong("fbiMark");
                if (mark < world.getGameTime()) {
                    player.sendSystemMessage(Component.literal("FBI, OPEN UP!").withStyle(ChatFormatting.RED));

                    Vec3 vec = new Vec3(MobConfig.raidAttackDistance, 0, 0);
                    vec = vec.yRot((float) (Math.PI * 2) * world.random.nextFloat());

                    for (int i = 0; i < MobConfig.raidAmount; i++) {
                        double spawnX = player.getX() + vec.x + world.random.nextGaussian() * 5;
                        double spawnZ = player.getZ() + vec.z + world.random.nextGaussian() * 5;
                        double spawnY = world.getHeight(Heightmap.Types.WORLD_SURFACE, (int)spawnX, (int)spawnZ);
                        trySpawn(world, spawnX, spawnY, spawnZ, new EntityFBI(ModEntities.FBI.get(), world));
                    }

                    for (int i = 0; i < MobConfig.raidDrones; i++) {
                        double spawnX = player.getX() + vec.x + world.random.nextGaussian() * 5;
                        double spawnZ = player.getZ() + vec.z + world.random.nextGaussian() * 5;
                        double spawnY = world.getHeight(Heightmap.Types.WORLD_SURFACE, (int)spawnX, (int)spawnZ);
                        trySpawn(world, spawnX, spawnY + 10, spawnZ, new EntityFBIDrone(world));
                    }
                }
            }
        }

        // ELEMENTALS
        if (MobConfig.enableElementals && world.getGameTime() % MobConfig.elementalDelay == 0) {
            if (world.random.nextInt(MobConfig.elementalChance) == 0 && !world.players().isEmpty()) {
                Player player = world.players().get(world.random.nextInt(world.players().size()));

                if (player.getPersistentData().getBoolean("radMark")) {
                    player.sendSystemMessage(Component.literal("You hear a faint clicking...")
                            .withStyle(ChatFormatting.YELLOW));
                    player.getPersistentData().putBoolean("radMark", false);

                    Vec3 vec = new Vec3(MobConfig.raidAttackDistance, 0, 0);

                    for (int i = 0; i < MobConfig.elementalAmount; i++) {
                        vec = vec.yRot((float) (Math.PI * 2) * world.random.nextFloat());

                        double spawnX = player.getX() + vec.x + world.random.nextGaussian();
                        double spawnZ = player.getZ() + vec.z + world.random.nextGaussian();
                        double spawnY = world.getHeight(Heightmap.Types.WORLD_SURFACE, (int)spawnX, (int)spawnZ);

                        EntityRADBeast rad = new EntityRADBeast(world);
                        if (i == 0) rad.makeLeader();
                        trySpawn(world, spawnX, spawnY, spawnZ, rad);
                    }
                }
            }
        }

        // METEORS
        if (WorldConfig.ENABLE_METEOR_STIKES) {
            meteorUpdate(world);
        }

        // GHOSTS
        if (world.getGameTime() % 20 == 0 && world.random.nextInt(5) == 0 && !world.players().isEmpty()) {
            Player player = world.players().get(world.random.nextInt(world.players().size()));

            if (HbmLivingProps.getDigamma(player) > 0) {
                Vec3 vec = new Vec3(75, 0, 0);
                vec = vec.yRot((float) (Math.PI * 2) * world.random.nextFloat());
                double spawnX = player.getX() + vec.x + world.random.nextGaussian();
                double spawnZ = player.getZ() + vec.z + world.random.nextGaussian();
                double spawnY = world.getHeight(Heightmap.Types.WORLD_SURFACE, (int)spawnX, (int)spawnZ);
                trySpawn(world, spawnX, spawnY, spawnZ, new EntityGhost(ModEntities.GHOST.get(), world));
            }
        }

    }

        public static void markFBI (Player player){
            if (!player.level().isClientSide) {
                player.getPersistentData().putLong("fbiMark", player.level().getGameTime() + 20 * 60 * 20);
            }
        }

        public static int meteorShower = 0;

        private static void meteorUpdate (Level world){
            if (meteorRand.nextInt(meteorShower > 0 ? WorldConfig.METEOR_SHOWER_CHANCE : WorldConfig.METEOR_STRIKE_CHANCE) == 0) {
                if (!world.players().isEmpty()) {
                    Player p = world.players().get(meteorRand.nextInt(world.players().size()));
                    if (p != null && p.level().dimension() == Level.OVERWORLD) {
                        boolean repell = false;
                        boolean strike = true;

                        for (ItemStack armor : p.getInventory().armor) {
                            if (!armor.isEmpty() && ArmorModHandler.hasMods(armor)) {
                                for (ItemStack mod : ArmorModHandler.pryMods(armor)) {
                                    if (!mod.isEmpty()) {
                                        if (mod.getItem() == ModItems.PROTECTION_CHARM.get()) repell = true;
                                        if (mod.getItem() == ModItems.METEOR_CHARM.get()) strike = false;
                                    }
                                }
                            }
                        }

                        if (strike) spawnMeteorAtPlayer(p, repell);
                    }
                }
            }

            if (meteorShower > 0) {
                meteorShower--;
            }

            if (meteorRand.nextInt(WorldConfig.METEOR_STRIKE_CHANCE * 100) == 0 && WorldConfig.ENABLE_METEOR_SHOWERS) {
                meteorShower = (int) (WorldConfig.METEOR_SHOWER_DURATION * 0.75 +
                        WorldConfig.METEOR_SHOWER_DURATION * 0.25 * meteorRand.nextFloat());
            }
        }

        public static void spawnMeteorAtPlayer(Player player, boolean repell){
            EntityMeteor meteor = new EntityMeteor(player.level());
            meteor.setPos(
                    player.getX() + meteorRand.nextInt(201) - 100,
                    384,
                    player.getZ() + meteorRand.nextInt(201) - 100
            );
            meteor.setYRot(0);
            meteor.setXRot(0);

            Vec3 vec;
            if (repell) {
                vec = new Vec3(meteor.getX() - player.getX(), 0, meteor.getZ() - player.getZ()).normalize();
                double vel = meteorRand.nextDouble();
                vec = vec.scale(vel);
                meteor.safe = true;
            } else {
                vec = new Vec3(meteorRand.nextDouble() - 0.5D, 0, 0);
                vec = vec.yRot((float) (Math.PI * meteorRand.nextDouble()));
            }

            meteor.setDeltaMovement(vec.x, -2.5, vec.z);
            player.level().addFreshEntity(meteor);
        }


    private static void trySpawn(Level world, double x, double y, double z, Mob entity) {
        entity.setPos(x, y, z);
        entity.setYRot(world.random.nextFloat() * 360.0F);
        entity.setXRot(0);

        // Проверяем, может ли сущность спавниться в этом месте
        if (entity.checkSpawnRules(world, MobSpawnType.EVENT)) {
            world.addFreshEntity(entity);
        }
    }
    }