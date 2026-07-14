package com.hbm.handler;

import com.google.common.collect.Multimap;
import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.blocks.IStepTickReceiver;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockTaint;
import com.hbm.config.GeneralConfig;
import com.hbm.config.MobConfig;
import com.hbm.config.ServerConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.CustomSkeleton;
import com.hbm.entity.mob.EntityCreeperTainted;
import com.hbm.entity.mob.EntityCyberCrab;
import com.hbm.entity.mob.util.MobUtil;
import com.hbm.entity.projectile.EntityBurningFOEQ;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
import com.hbm.hazard.HazardSystem;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolHelper;
import com.hbm.items.IEquipReceiver;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.items.armor.ArmorFSB;
import com.hbm.items.armor.ArmorNo9;
import com.hbm.items.armor.ItemArmorMod;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.PlayerInformPacket;
import com.hbm.sound.ModSounds;
import com.hbm.uninos.UniNodespace;
import com.hbm.util.ModDamageSource;
import com.hbm.world.generator.TimedGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber
public class ModEventHandler {

    private static final Random rand = new Random();

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level == null) return;
        if (event.level.isClientSide) return;

        Level level = event.level;
        long gameTime = level.getGameTime();

        // ===== PHASE START =====
        if (event.phase == TickEvent.Phase.START) {
            // Боссы и генераторы
            BossSpawnHandler.rollTheDice(level);
            TimedGenerator.automaton(level, 100);
        }

        // ===== PHASE END =====
        if (event.phase == TickEvent.Phase.END) {
            // Опасности предметов
            int tickrate = Math.max(1, ServerConfig.ITEM_HAZARD_DROP_TICKRATE);

            if (gameTime % tickrate == 0) {
                for (Player player : level.players()) {
                    AABB area = player.getBoundingBox().inflate(64);
                    List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
                    for (ItemEntity item : items) {
                        HazardSystem.updateDroppedItem(item);
                    }
                }
            }

            // Обновление вагонов (если есть)
            // EntityRailCarBase.updateMotion(level);
        }
    }


    @SubscribeEvent
    public static void onLivingTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level == null) return;
        if (event.level.isClientSide) return;

        Level level = event.level;
        long gameTime = level.getGameTime();


        // Получаем всех игроков, чтобы ограничить радиус
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        double px = player.getX(), pz = player.getZ();
        AABB area = new AABB(
                px - 64, -64, pz - 64,
                px + 64, 256, pz + 64
        );

        try {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area,
                    e -> !(e instanceof Player))) {
                if (entity == null) continue;

                ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
                if (helmet == null || helmet.isEmpty()) continue;
                if (helmet.getItem() instanceof ArmorNo9) {
                    ArmorNo9.handleMobLight(level, entity, helmet, gameTime);
                }
            }
        } catch (Exception e) {
            // Игнорируем
        }
    }

    @SubscribeEvent
    public void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();

        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Player player) {

            if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).getBoolean("ntmCyanide")) {
                for (int i = 0; i < 10; i++) {
                    if (rand.nextBoolean()) {
                        player.hurt(ModDamageSource.euthanizedSelf(player), 1000);
                    } else {
                        player.hurt(ModDamageSource.euthanizedSelf2(player), 1000);
                    }
                }
            }

        }
    }


    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide) return;

        // Обработка зомби
        if (entity instanceof Zombie zombie && !zombie.isBaby()) {
            decorateZombie(zombie);
        }

        // Обработка скелетов
        if (entity instanceof Skeleton skeleton) {
            // Проверяем тип сущности напрямую
            if (skeleton.getType() == EntityType.SKELETON) {
                // Это обычный скелет, заменяем на кастомного
                replaceWithCustomSkeleton(skeleton, event);
            } else {
                // Stray или wither skeleton - просто декорируем
                decorateSkeleton(skeleton);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        if (MobConfig.enableDucks && player instanceof ServerPlayer serverPlayer &&
                !player.getPersistentData().getBoolean("hasDucked")) {

            PacketDispatcher.sendTo(new PlayerInformPacket("Press O to Duck!", 0, 30000), serverPlayer);
        }
    }

    private static void replaceWithCustomSkeleton(Skeleton old, EntityJoinLevelEvent event) {
        Level level = old.level();

        // Сохраняем данные старого скелета
        double x = old.getX();
        double y = old.getY();
        double z = old.getZ();
        float yRot = old.getYRot();
        float xRot = old.getXRot();
        float yHeadRot = old.getYHeadRot();
        float yBodyRot = old.yBodyRot;
        float health = old.getHealth();

        ItemStack mainHand = old.getMainHandItem().copy();
        ItemStack head = old.getItemBySlot(EquipmentSlot.HEAD).copy();
        ItemStack chest = old.getItemBySlot(EquipmentSlot.CHEST).copy();
        ItemStack legs = old.getItemBySlot(EquipmentSlot.LEGS).copy();
        ItemStack feet = old.getItemBySlot(EquipmentSlot.FEET).copy();

        // Отменяем добавление старого скелета
        event.setCanceled(true);

        // Создаём нового кастомного скелета
        CustomSkeleton newSkeleton = new CustomSkeleton(ModEntities.CUSTOM_SKELETON.get(), level);
        newSkeleton.setPos(x, y, z);
        newSkeleton.setYRot(yRot);
        newSkeleton.setXRot(xRot);
        newSkeleton.setYHeadRot(yHeadRot);
        newSkeleton.yBodyRot = yBodyRot;
        newSkeleton.setHealth(health);

        newSkeleton.setItemSlot(EquipmentSlot.MAINHAND, mainHand);
        newSkeleton.setItemSlot(EquipmentSlot.HEAD, head);
        newSkeleton.setItemSlot(EquipmentSlot.CHEST, chest);
        newSkeleton.setItemSlot(EquipmentSlot.LEGS, legs);
        newSkeleton.setItemSlot(EquipmentSlot.FEET, feet);

        level.addFreshEntity(newSkeleton);
        decorateSkeleton(newSkeleton);
    }

    private static void decorateZombie(Zombie zombie) {
        if (!MobConfig.enableMobGear) return;

        float soot = PollutionHandler.getPollution(zombie.level(), zombie.blockPosition(), PollutionType.SOOT);
        RandomSource rand = zombie.getRandom();

        // Шанс 0.5% при уровне сажи >2 надеть полный химзащитный костюм
        if (rand.nextFloat() < 0.005F && soot > 2) {
            MobUtil.equipFullSet(zombie,
                    ModArmorItems.HAZMAT_HELMET.get(),
                    ModArmorItems.HAZMAT_CHESTPLATE.get(),
                    ModArmorItems.HAZMAT_LEGGINGS.get(),
                    ModArmorItems.HAZMAT_BOOTS.get());
            return;
        }
        // Обычная экипировка
        MobUtil.assignItemsToEntity(zombie, MobUtil.COMMON_ARMOR_POOLS, rand);
    }

    public static void decorateSkeleton(AbstractSkeleton skeleton) {
        if (!MobConfig.enableMobGear) return;

        float soot = PollutionHandler.getPollution(skeleton.level(), skeleton.blockPosition(), PollutionType.SOOT);
        RandomSource rand = skeleton.getRandom();

        // Сначала назначаем броню из ranged пулов
        MobUtil.assignItemsToEntity(skeleton, MobUtil.RANGED_ARMOR_POOLS, rand);

        // Затем оружие
        ItemStack gun = getSkelegun(soot, rand);
        if (gun != null && !gun.isEmpty()) {
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, gun);
        } else {
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }
    }

    public static ItemStack getSkelegun(float soot, RandomSource rand) {
        if (!MobConfig.enableMobWeapons) return null;

        // Шанс получить оружие зависит от логарифма сажи
        if (rand.nextDouble() > Math.log(soot) * 0.25) return null;

        ItemPool gunPool;
        if (soot < 0.3) {
            // Особый пул для очень низкой сажи
            gunPool = new ItemPool()
                    .add(ItemPoolHelper.weighted(ModGunItems.GUN_PEPPERBOX.get(), 1, 5))
                    .add(ItemPoolHelper.empty(20));
        } else if (soot > 0.3 && soot < 1) {
            gunPool = MobUtil.GUN_POOLS.get(0.3);
        } else if (soot < 3) {
            gunPool = MobUtil.GUN_POOLS.get(1.0);
        } else if (soot < 5) {
            gunPool = MobUtil.GUN_POOLS.get(3.0);
        } else {
            gunPool = MobUtil.GUN_POOLS.get(5.0);
        }

        if (gunPool == null) return null;
        return gunPool.generateOne(rand);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            UniNodespace.tick();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        Player player = event.player;

        ArmorNo9.onPlayerTick(event);

        if (!player.getInventory().armor.get(2).isEmpty() &&
                player.getInventory().armor.get(2).getItem() instanceof ArmorFSB armorFSB) {
            armorFSB.handleTick(event);
        }

        if (event.phase == TickEvent.Phase.START) {
            int x = Mth.floor(player.getX());
            int y = Mth.floor(player.getY() - player.getMyRidingOffset() - 0.01); // yOffset заменён
            int z = Mth.floor(player.getZ());
            BlockState state = player.level().getBlockState(new BlockPos(x, y, z));
            Block b = state.getBlock();

            if (b instanceof IStepTickReceiver step && !player.getAbilities().flying) {
                step.onPlayerStep(player.level(), new BlockPos(x, y, z), player);
            }
        }

        if (!player.level().isClientSide && event.phase == TickEvent.Phase.START) {

            /// GHOST FIX START ///
            if (!Float.isFinite(player.getHealth()) || !Float.isFinite(player.getAbsorptionAmount())) {
                player.sendSystemMessage(Component.literal("Your health has been restored!"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.SYRINGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                player.setHealth(player.getMaxHealth());
                player.setAbsorptionAmount(0);
            }
            /// GHOST FIX END ///

            /// BETA HEALTH START ///
            if (player.getInventory().hasAnyMatching(stack -> stack.is(ModItems.BETA.get()))) {

                if (player.getFoodData().getFoodLevel() > 10) {
                    player.heal(player.getFoodData().getFoodLevel() - 10);
                }

                if (player.getFoodData().getFoodLevel() != 10) {
                    // Устанавливаем уровень еды через отражение или через сеттер, если есть
                    // В 1.20.1 нет прямого сеттера, но можно использовать отражение
                    try {
                        Field food = FoodData.class.getDeclaredField("foodLevel");
                        food.setAccessible(true);
                        food.setInt(player.getFoodData(), 10);
                    } catch (Exception e) { }
                }
            }
            /// BETA HEALTH END ///


            /// NEW ITEM SYS START ///
            HazardSystem.updatePlayerInventory(player);
            /// NEW ITEM SYS END ///

            /// SYNC START ///
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDispatcher.sendPermaSync(serverPlayer);
            }
            /// SYNC END ///
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        // Для получения предыдущего слота руки используем LastActionTracker или храним в NBT
        // В 1.20.1 нет прямого аналога previousEquipment, поэтому упростим
        ItemStack heldItem = entity.getMainHandItem();

        if (entity instanceof Player player) {
            // Проверяем, изменился ли предмет в руке (упрощённо)
            ItemStack prevHeld = player.getPersistentData().contains("prevHeld") ?
                    ItemStack.of(player.getPersistentData().getCompound("prevHeld")) : ItemStack.EMPTY;

            if (!ItemStack.isSameItem(prevHeld, heldItem) && heldItem.getItem() instanceof IEquipReceiver receiver) {
                receiver.onEquip(player, heldItem);
            }

            // Сохраняем текущий предмет для следующего тика
            CompoundTag tag = new CompoundTag();
            heldItem.save(tag);
            player.getPersistentData().put("prevHeld", tag);
        }

        // Обработка брони (слоты 1-4: ноги, грудь, голова, ноги? в 1.20.1 порядок другой)
        EquipmentSlot[] armorSlots = {
                EquipmentSlot.FEET,
                EquipmentSlot.LEGS,
                EquipmentSlot.CHEST,
                EquipmentSlot.HEAD
        };

        for (EquipmentSlot slot : armorSlots) {
            ItemStack prev = entity.getPersistentData().contains("prev_" + slot.getName()) ?
                    ItemStack.of(entity.getPersistentData().getCompound("prev_" + slot.getName())) : ItemStack.EMPTY;
            ItemStack armor = entity.getItemBySlot(slot);

            boolean reapply = !ItemStack.isSameItem(prev, armor);

            if (reapply) {
                if (!prev.isEmpty() && ArmorModHandler.hasMods(prev)) {
                    for (ItemStack mod : ArmorModHandler.pryMods(prev)) {
                        if (mod != null && mod.getItem() instanceof ItemArmorMod armorMod) {
                            // Используем текущий слот (не создаём новую переменную)
                            if (slot != null) {
                                Multimap<Attribute, AttributeModifier> map = armorMod.getModifiers(slot, prev);
                                if (map != null && !map.isEmpty() && entity.getAttributes() != null) {
                                    entity.getAttributes().removeAttributeModifiers(map);
                                }
                            }
                        }
                    }
                }
            }

            if (!armor.isEmpty() && ArmorModHandler.hasMods(armor)) {
                for (ItemStack mod : ArmorModHandler.pryMods(armor)) {
                    if (mod != null && mod.getItem() instanceof ItemArmorMod armorMod) {
                        armorMod.modUpdate(entity, armor);
                        HazardSystem.applyHazards(mod, entity);

                        if (reapply) {
                            // Используем текущий слот
                            Multimap<Attribute, AttributeModifier> map = armorMod.getModifiers(slot, armor);
                            if (map != null && !map.isEmpty() && entity.getAttributes() != null) {
                                entity.getAttributes().addTransientAttributeModifiers(map);
                            }
                        }
                    }
                }
            }

            // Сохраняем текущий слот брони для следующего тика
            CompoundTag armorTag = new CompoundTag();
            armor.save(armorTag);
            entity.getPersistentData().put("prev_" + slot.getName(), armorTag);
        }

        // Вызываем обработчик эффектов
        EntityEffectHandler.onUpdate(entity);

        // Для не-игроков обновляем опасности инвентаря
        if (!entity.level().isClientSide && !(entity instanceof Player)) {
            HazardSystem.updateLivingInventory(entity);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        // Проверяем, что умерший — это наш заражённый крипер
        if (entity instanceof EntityCreeperTainted creeper && !entity.level().isClientSide) {
            // Проверяем правило моб-грифинга
            boolean griefing = entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            if (griefing) {
                spreadTaint(creeper);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Level level = entity.level();

        // Сброс радиации
        HbmLivingProps.setRadiation(entity, 0);

        if (level.isClientSide) return;

        // Катаклизм
        if (GeneralConfig.enableCataclysm.get()) {
            EntityBurningFOEQ foeq = new EntityBurningFOEQ(ModEntities.BURNING_FOEQ.get(), level);
            foeq.setPos(entity.getX(), 500, entity.getZ());
            level.addFreshEntity(foeq);
        }

        // Заражённый крипер убитый Boxcar
        if (entity instanceof EntityCreeperTainted && source.is(ModDamageSource.BOXCAR)) {
            AABB box = entity.getBoundingBox().inflate(50, 50, 50);
            List<Player> players = level.getEntitiesOfClass(Player.class, box);
            for (Player player : players) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.BOB_HIDDEN.trigger(sp);
                }
            }
        }

        // Дроп с мобов от игрока (не фейкового)
        if (source.getEntity() instanceof Player killer && !(killer instanceof FakePlayer)) {

            RandomSource rand = entity.getRandom();

            if (entity instanceof Spider && rand.nextInt(500) == 0) {
                entity.spawnAtLocation(ModItems.SPIDER_MILK.get());
            }

            if (entity instanceof CaveSpider && rand.nextInt(100) == 0) {
                entity.spawnAtLocation(ModItems.SERUM.get());
            }

            if (entity instanceof Animal && rand.nextInt(500) == 0) {
                entity.spawnAtLocation(ModItems.BANDAID.get());
            }

            if (entity instanceof Enemy) {
                if (rand.nextInt(1000) == 0) {
                    entity.spawnAtLocation(ModItems.HEART_PIECE.get());
                }
                if (rand.nextInt(250) == 0) {
                    entity.spawnAtLocation(ModItems.KEY_RED_CRACKED.get());
                }
                if (rand.nextInt(250) == 0) {
                    entity.spawnAtLocation(ModItems.LAUNCH_CODE_PIECE.get());
                }
            }

            if (entity instanceof EntityCyberCrab && rand.nextInt(500) == 0) {
                entity.spawnAtLocation(ModItems.WD40.get());
            }
        }
    }

    private static void spreadTaint(EntityCreeperTainted creeper) {
        RandomSource rand = creeper.getRandom();
        boolean trails = ServerConfig.TAINT_TRAILS; // используйте свой конфиг

        Level level = creeper.level();
        double x = creeper.getX();
        double y = creeper.getY();
        double z = creeper.getZ();

        if (creeper.isPowered()) {
            for (int i = 0; i < 255; i++) {
                int a = rand.nextInt(15) + (int) x - 7;
                int b = rand.nextInt(15) + (int) y - 7;
                int c = rand.nextInt(15) + (int) z - 7;
                BlockPos pos = new BlockPos(a, b, c);
                BlockState state = level.getBlockState(pos);

                if (!state.isAir() && state.isSolidRender(level, pos)) {
                    int meta = trails ? rand.nextInt(3) : rand.nextInt(3) + 5;
                    level.setBlock(pos, ModBlocks.TAINT.get().defaultBlockState().setValue(BlockTaint.META, meta), 2);
                }
            }
        } else {
            for (int i = 0; i < 85; i++) {
                int a = rand.nextInt(7) + (int) x - 3;
                int b = rand.nextInt(7) + (int) y - 3;
                int c = rand.nextInt(7) + (int) z - 3;
                BlockPos pos = new BlockPos(a, b, c);
                BlockState state = level.getBlockState(pos);

                if (!state.isAir() && state.isSolidRender(level, pos)) {
                    int meta = trails ? rand.nextInt(3) + 4 : rand.nextInt(6) + 10;
                    level.setBlock(pos, ModBlocks.TAINT.get().defaultBlockState().setValue(BlockTaint.META, meta), 2);
                }
            }
        }
    }
}