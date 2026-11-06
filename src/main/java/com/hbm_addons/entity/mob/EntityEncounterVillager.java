package com.hbm_addons.entity.mob;

import com.hbm.entity.mob.ai.EntityAIFireGun;
import com.hbm.entity.mob.ai.EntityAINearestAttackableTargetNT;
import com.hbm_addons.entity.ai.EncounterGoal;
import com.hbm_addons.inventory.container.EncounterTradeMenu;
import com.hbm_addons.handler.EncounterReputation;
import com.hbm_addons.util.EncounterPools;
import com.hbm.itempool.ItemPool;
import com.hbm.items.weapon.sedna.GunItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityEncounterVillager extends Villager {

    public static final EntityDataAccessor<Byte> DW_FACTION =
            SynchedEntityData.defineId(EntityEncounterVillager.class, EntityDataSerializers.BYTE);

    public static final EntityDataAccessor<Byte> DW_GOAL =
            SynchedEntityData.defineId(EntityEncounterVillager.class, EntityDataSerializers.BYTE);

    public static final byte FACTION_FRIENDLY = 0;
    public static final byte FACTION_HOSTILE = 1;

    /** Как в ванильном NearestAttackableTargetGoal — общий интервал для всех мобов. */
    private static final int TARGET_INTERVAL = 10;

    private final SimpleContainer lootInventory = new SimpleContainer(27);
    public int attackTicks = 0;

    public EntityEncounterVillager(EntityType<? extends EntityEncounterVillager> type, Level level) {
        super(type, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Villager.createAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DW_FACTION, FACTION_FRIENDLY);
        this.entityData.define(DW_GOAL, (byte) EncounterGoal.TRAVELER.ordinal());
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (this.isHostile()) {
            return super.mobInteract(player, hand);
        }
        if (!this.level().isClientSide) {
            EncounterTradeMenu.openFor(player, this);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    public byte getFaction() { return this.entityData.get(DW_FACTION); }
    public boolean isHostile() { return getFaction() == FACTION_HOSTILE; }

    public EncounterGoal getGoal() {
        int ord = this.entityData.get(DW_GOAL);
        EncounterGoal[] all = EncounterGoal.values();
        return ord >= 0 && ord < all.length ? all[ord] : EncounterGoal.TRAVELER;
    }

    public void setGoal(EncounterGoal goal) {
        this.entityData.set(DW_GOAL, (byte) goal.ordinal());
    }

    public void setFaction(byte faction) {
        this.entityData.set(DW_FACTION, faction);
        if (!this.level().isClientSide) {
            this.rebuildGoals();
            this.applyFactionAppearance();
        }
    }

    public SimpleContainer getLootInventory() {
        return this.lootInventory;
    }

    private void applyFactionAppearance() {
        if (isHostile()) {
            this.setVillagerData(this.getVillagerData()
                    .setProfession(VillagerProfession.NITWIT)
                    .setLevel(1));
        } else {
            VillagerProfession[] profs = {
                    VillagerProfession.FARMER, VillagerProfession.TOOLSMITH,
                    VillagerProfession.WEAPONSMITH, VillagerProfession.CLERIC,
                    VillagerProfession.CARTOGRAPHER
            };
            this.setVillagerData(this.getVillagerData()
                    .setProfession(profs[this.random.nextInt(profs.length)])
                    .setLevel(1 + this.random.nextInt(3)));
        }
    }

    @Override
    protected void registerGoals() {
        // Пусто — цели собираются в rebuildGoals()
    }

    public void rebuildGoals() {
        this.goalSelector.removeAllGoals(g -> true);
        this.targetSelector.removeAllGoals(g -> true);

        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Стоячие цели не должны бродить
        boolean shouldWander = !(isHostile() && getGoal() == EncounterGoal.AMBUSHER);
        if (shouldWander) {
            this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        }

        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        if (isHostile()) {
            if (this.getMainHandItem().getItem() instanceof GunItem) {
                this.goalSelector.addGoal(2, new EntityAIFireGun(this));
            } else {
                this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
            }

            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

            double detectRange = switch (getGoal()) {
                case RAIDER -> 48.0D;
                case SCOUT -> 32.0D;
                case AMBUSHER -> 16.0D;
                default -> 32.0D;
            };

            // Игроки — без селектора, короткая сигнатура
            this.targetSelector.addGoal(2, new EntityAINearestAttackableTargetNT<>(
                    this, Player.class, true, false, detectRange));

            // Жители — с селектором, длинная сигнатура (interval + селектор)
            this.targetSelector.addGoal(3, new EntityAINearestAttackableTargetNT<>(
                    this, Villager.class,
                    TARGET_INTERVAL, true, false,
                    e -> !(e instanceof EntityEncounterVillager),
                    detectRange * 0.75));

        } else {
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        }
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        if (!isHostile()) return null;
        return super.getTarget();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("EncounterFaction", getFaction());
        tag.putByte("EncounterGoal", (byte) getGoal().ordinal());
        tag.put("EncounterLoot", lootInventory.createTag());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("EncounterFaction")) {
            this.entityData.set(DW_FACTION, tag.getByte("EncounterFaction"));
        }
        if (tag.contains("EncounterGoal")) {
            this.entityData.set(DW_GOAL, tag.getByte("EncounterGoal"));
        }
        if (tag.contains("EncounterLoot")) {
            lootInventory.fromTag(tag.getList("EncounterLoot", 10));
        }
        if (!this.level().isClientSide) {
            this.rebuildGoals();
            this.applyFactionAppearance();
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level,
                                        @NotNull DifficultyInstance difficulty,
                                        @NotNull MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag tag) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);
        this.equipForSpawn();
        return spawnData;
    }

    /** Назначает экипировку, оружие и лут. Вызывается из finalizeSpawn и вручную. */
    public void equipForSpawn() {
        boolean hostile = isHostile();

        var armorPool = hostile
                ? EncounterPools.ENCOUNTER_HOSTILE_ARMOR_POOLS
                : EncounterPools.ENCOUNTER_FRIENDLY_ARMOR_POOLS;
        EncounterPools.assignItemsToEntity(this, armorPool, this.random);

        if (this.getMainHandItem().isEmpty()) {
            ItemPool gunPool = hostile
                    ? EncounterPools.ENCOUNTER_HOSTILE_GUN_POOLS
                    : EncounterPools.ENCOUNTER_FRIENDLY_GUN_POOLS;
            ItemStack gun = gunPool.generateOne(this.random);
            if (gun != null && !gun.isEmpty()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, gun);
                if (gun.getItem() instanceof GunItem) {
                    gun.getOrCreateTag().putBoolean("infiniteMag", true);
                }
            }
        }

        int items = hostile ? 2 + this.random.nextInt(4) : 1 + this.random.nextInt(3);
        for (int i = 0; i < items; i++) {
            ItemStack loot = EncounterPools.ENCOUNTER_LOOT_POOLS.generateOne(this.random);
            if (loot != null && !loot.isEmpty()) {
                lootInventory.setItem(this.random.nextInt(lootInventory.getContainerSize()), loot);
            }
        }

        this.rebuildGoals();
        this.applyFactionAppearance();
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        for (int i = 0; i < lootInventory.getContainerSize(); i++) {
            ItemStack stack = lootInventory.getItem(i);
            if (!stack.isEmpty()) this.spawnAtLocation(stack);
        }
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.MAINHAND, EquipmentSlot.HEAD,
                EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = this.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack);
                this.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);
        if (this.level().isClientSide || this.isHostile()) return;
        if (source.getEntity() instanceof Player killer) {
            EncounterReputation.onFriendlyKilled(killer);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (attackTicks > 0) attackTicks--;
    }

    public void triggerAttackAnimation() {
        this.attackTicks = 20;
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor level, @NotNull MobSpawnType type) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && level.isUnobstructed(this)
                && !level.containsAnyLiquid(this.getBoundingBox());
    }

}
