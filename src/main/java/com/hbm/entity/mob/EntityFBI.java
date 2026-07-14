package com.hbm.entity.mob;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.MobConfig;
import com.hbm.entity.mob.ai.EntityAIBreaking;

import com.hbm.items.ModArmorItems;
import com.hbm.items.ModGunItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityFBI extends Monster implements RangedAttackMob {

    private static final Set<Block> CAN_DESTROY = new HashSet<>();

    static {
        CAN_DESTROY.add(Blocks.OAK_DOOR);
        CAN_DESTROY.add(Blocks.IRON_DOOR);
        CAN_DESTROY.add(Blocks.OAK_TRAPDOOR);
        CAN_DESTROY.add(ModBlocks.MACHINE_PRESS.get());
        CAN_DESTROY.add(ModBlocks.CRATE_IRON.get());
        CAN_DESTROY.add(ModBlocks.CRATE_STEEL.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_DIESEL.get());
        CAN_DESTROY.add(Blocks.CHEST);
        CAN_DESTROY.add(Blocks.TRAPPED_CHEST);
        //TODO портировать машины
        /*
        CAN_DESTROY.add(ModBlocks.MACHINE_EPRESS.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_CHEMPLANT.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_CRYSTALLIZER.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_TURBINE.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_LARGE_TURBINE.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_RTG_GREY.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_MINIRTG.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_POWERTG.get());
        CAN_DESTROY.add(ModBlocks.MACHINE_CYCLOTRON.get());

         */
    }

    public EntityFBI(EntityType<? extends EntityFBI> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EntityAIBreaking(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 20, 25, 15.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 128.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof EntityFBI) {
            return false;
        }

        if (this.getItemBySlot(EquipmentSlot.HEAD) != null &&
                this.getItemBySlot(EquipmentSlot.HEAD).getItem() == Item.byBlock(Blocks.GLASS)) {
            if (source.is(DamageTypes.DROWN)) return false;
            if (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) return false;
        }

        return super.hurt(source, amount);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty,
                                        @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag compound) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, compound);

        int equip = this.random.nextInt(2);
        switch (equip) {
            case 0 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModGunItems.GUN_HEAVY_REVOLVER.get()));
            case 1 -> this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModGunItems.GUN_SPAS12.get()));
        }

        if (this.random.nextInt(5) == 0) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModArmorItems.SECURITY_HELMET.get()));
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModArmorItems.SECURITY_CHESTPLATE.get()));
            this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModArmorItems.SECURITY_LEGGINGS.get()));
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModArmorItems.SECURITY_BOOTS.get()));
        }

        if (this.level() != null && this.level().dimension() != Level.OVERWORLD) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.GLASS));
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModArmorItems.PAA_CHESTPLATE.get()));
            this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModArmorItems.PAA_LEGGINGS.get()));
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModArmorItems.PAA_BOOTS.get()));
        }

        return spawnData;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.getTarget() == null) {
            Player nearest = this.level().getNearestPlayer(this, 128.0D);
            if (nearest != null) {
                this.setTarget(nearest);
            }
        }

        // Используем PathFinderUtils для поиска пути
        if (this.getTarget() != null) {
            // Упрощённый вариант - стандартный навигатор
            this.getNavigation().moveTo(this.getTarget(), 1.0D);
        }
    }

    @Override
    public int getArmorValue() {
        return 20; // Полный комплект алмазной брони
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        // Реализация стрельбы
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide || this.getHealth() <= 0) {
            return;
        }

        if (this.tickCount % MobConfig.RAID_ATTACK_DELAY.get() == 0) {
            Vec3 vec = new Vec3(MobConfig.RAID_ATTACK_REACH.get(), 0, 0);
            vec = vec.yRot((float) (Math.PI * 2) * this.random.nextFloat());

            Vec3 start = new Vec3(this.getX(), this.getY() + 0.5 + this.random.nextFloat(), this.getZ());
            Vec3 end = start.add(vec);

            BlockHitResult hit = this.level().clip(new net.minecraft.world.level.ClipContext(
                    start, end,
                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                    this
            ));

            if (hit.getType() != HitResult.Type.MISS) {
                BlockPos pos = hit.getBlockPos();
                BlockState state = this.level().getBlockState(pos);
                if (CAN_DESTROY.contains(state.getBlock())) {
                    this.level().destroyBlock(pos, false);
                }
            }
        }

        double range = 1.5;
        AABB aabb = new AABB(this.getX(), this.getY(), this.getZ(),
                this.getX(), this.getY(), this.getZ()).inflate(range);

        List<ItemEntity> items = this.level().getEntitiesOfClass(ItemEntity.class, aabb);

        for (ItemEntity item : items) {
            item.setSecondsOnFire(10);
        }
    }

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effect) {
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModArmorItems.GAS_MASK_M65.get()));
        }
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // Сохраняем броню
        tag.put("HeadArmor", this.getItemBySlot(EquipmentSlot.HEAD).save(new CompoundTag()));
        tag.put("ChestArmor", this.getItemBySlot(EquipmentSlot.CHEST).save(new CompoundTag()));
        tag.put("LegsArmor", this.getItemBySlot(EquipmentSlot.LEGS).save(new CompoundTag()));
        tag.put("FeetArmor", this.getItemBySlot(EquipmentSlot.FEET).save(new CompoundTag()));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Восстанавливаем броню
        if (tag.contains("HeadArmor")) {
            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.of(tag.getCompound("HeadArmor")));
        }
        if (tag.contains("ChestArmor")) {
            this.setItemSlot(EquipmentSlot.CHEST, ItemStack.of(tag.getCompound("ChestArmor")));
        }
        if (tag.contains("LegsArmor")) {
            this.setItemSlot(EquipmentSlot.LEGS, ItemStack.of(tag.getCompound("LegsArmor")));
        }
        if (tag.contains("FeetArmor")) {
            this.setItemSlot(EquipmentSlot.FEET, ItemStack.of(tag.getCompound("FeetArmor")));
        }
    }
}