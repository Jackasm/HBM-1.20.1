package com.hbm.potion;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.PotionConfig;
import com.hbm.config.ServerConfig;

import com.hbm.entity.mob.EntityCreeperTainted;
import com.hbm.entity.mob.EntityTaintCrab;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.ModItems;

import com.hbm.sound.ModSounds;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.ModDamageSource;
import com.hbm.util.RefStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.hbm.util.ResLocation.ResLocation;

public class HbmPotion extends MobEffect {
    private ResourceLocation iconTexture;

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RefStrings.MODID);

    public static RegistryObject<MobEffect> TAINT = registerEffect("taint",
            () -> new HbmPotion(MobEffectCategory.HARMFUL, 0x800080));
    public static RegistryObject<MobEffect> RADIATION = registerEffect("radiation",
            () -> new HbmPotion(MobEffectCategory.HARMFUL, 0x84C128)
                    .setIcon(ResLocation(RefStrings.MODID, "textures/potion/radiation.png")));
    public static RegistryObject<MobEffect> BANG = registerEffect("bang",
            () -> new HbmPotion(MobEffectCategory.HARMFUL, 0x111111));
    public static RegistryObject<MobEffect> MUTATION = registerEffect("mutation",
            () -> new HbmPotion(MobEffectCategory.BENEFICIAL, 0x800080));
    public static RegistryObject<MobEffect> RADX = registerEffect("radx",
            () -> new HbmPotion(MobEffectCategory.BENEFICIAL, 0xBB4B00)
                    .setIcon(ResLocation(RefStrings.MODID, "textures/potion/radx.png")));
    public static RegistryObject<MobEffect> LEAD = registerEffect("lead",
            () -> new HbmPotion(MobEffectCategory.HARMFUL, 0x767682));
    public static RegistryObject<MobEffect> RADAWAY = registerEffect("radaway",
            () -> new HbmPotion(MobEffectCategory.BENEFICIAL, 0xBB4B00)
                    .setIcon(ResLocation(RefStrings.MODID, "textures/potion/radaway.png")));
    public static RegistryObject<MobEffect> PHOSPHORUS = registerEffect("phosphorus",
            () -> new HbmPotion(MobEffectCategory.HARMFUL, 0xFFFF00));
    public static RegistryObject<MobEffect> STABILITY = registerEffect("stability",
            () -> new HbmPotion(MobEffectCategory.BENEFICIAL, 0xD0D0D0));
    public static RegistryObject<MobEffect> POTIONSICKNESS = registerEffect("potionsickness",
            () -> new HbmPotion(MobEffectCategory.NEUTRAL, 0xff8080)
                    .setIcon(ResLocation(RefStrings.MODID, "textures/potion/potionsickness.png")));
    public static RegistryObject<MobEffect> DEATH = registerEffect("death",
            () -> new HbmPotion(MobEffectCategory.NEUTRAL, 0x111111));



    private static RegistryObject<MobEffect> registerEffect(String name, Supplier<MobEffect> supplier) {
        return EFFECTS.register(name, supplier);
    }

    public HbmPotion(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level world = entity.level();

        if (world.isClientSide) return;

        // Проверка на таинственный эффект
        if (this == TAINT.get()) {

            if (!(entity instanceof EntityCreeperTainted) &&
                    !(entity instanceof EntityTaintCrab) &&
                    world.random.nextInt(40) == 0) {
                entity.hurt(ModDamageSource.taint(entity), (amplifier + 1));
            }

            if (ServerConfig.TAINT_TRAILS) {
                BlockPos pos = entity.blockPosition().below();
                BlockPos entityPos = entity.blockPosition();

                if (entityPos.getY() > 1) {
                    BlockState state = world.getBlockState(pos);

                    if (state.isRedstoneConductor(world, pos)) {
                        world.setBlock(pos, ModBlocks.TAINT.get().defaultBlockState(), 2);
                    }


                }
            }
        }

        // Проверка на радиацию
        if (this == RADIATION.get()) {
            ContaminationUtil.contaminate(entity, HazardType.RADIATION,
                    ContaminationType.CREATIVE, (float)(amplifier + 1F) * 0.05F);
        }

        // Проверка на радавей
        if (this == RADAWAY.get()) {
            HbmLivingProps.incrementRadiation(entity, -(amplifier + 1));
        }

        // Проверка на взрыв
        if (this == BANG.get()) {
            entity.hurt(ModDamageSource.causeExplosiveDamage(entity), 1000);
            entity.setHealth(0.0F);

            if (!(entity instanceof Player)) {
                entity.remove(Entity.RemovalReason.KILLED);
            }

            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.getCachedSound("weapon.laserBang"),
                    entity.getSoundSource(), 100.0F, 1.0F);

            ExplosionLarge.spawnParticles(world, entity.getX(), entity.getY(), entity.getZ(), 10);

            if (entity instanceof Cow cow) {
                int toDrop = cow.isBaby() ? 10 : 3;
                cow.spawnAtLocation(new ItemStack(ModItems.CHEESE.get(), toDrop), 1.0F);
            }
        }

        // Проверка на свинец
        if (this == LEAD.get()) {
            entity.hurt(ModDamageSource.causeLeadDamage(entity), (amplifier + 1));
        }

        // Проверка на фосфор
        if (this == PHOSPHORUS.get()) {
            entity.setSecondsOnFire(1);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Таинственный эффект
        if (this == TAINT.get()) {
            return duration % 2 == 0;
        }

        if (this == RADAWAY.get()) {
            int tickRate = 20; // раз в секунду
            return duration % tickRate == 0;
        }

        // Радиация, радавей, фосфор
        if (this == RADIATION.get() || this == PHOSPHORUS.get()) {
            return true;
        }

        // Взрыв
        if (this == BANG.get()) {
            return duration <= 10;
        }

        // Свинец
        if (this == LEAD.get()) {
            int k = 60;
            return k > 0 ? duration % k == 0 : true;
        }

        return false;
    }

    // Метод для удобного создания инстансов эффектов
    public static MobEffectInstance createEffectInstance(RegistryObject<MobEffect> effect, int duration, int amplifier) {
        return new MobEffectInstance(effect.get(), duration, amplifier);
    }

    // Метод для проверки типа эффекта (вредный/полезный)
    public static boolean isBadEffect(MobEffect effect) {
        return effect.getCategory() == MobEffectCategory.HARMFUL;
    }

    public HbmPotion setIcon(ResourceLocation icon) {
        this.iconTexture = icon;
        return this;
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInInventory(MobEffectInstance instance) {
                return true;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) {
                return true;
            }

            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance,
                                               net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen<?> screen,
                                               net.minecraft.client.gui.GuiGraphics guiGraphics,
                                               int x, int y, int blitOffset) {
                if (iconTexture != null) {
                    guiGraphics.blit(iconTexture, x, y + 7, blitOffset, 0, 0, 18, 18, 18, 18);
                    return true;
                }
                return false;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance,
                                         net.minecraft.client.gui.Gui gui,
                                         net.minecraft.client.gui.GuiGraphics guiGraphics,
                                         int x, int y, float z, float alpha) {
                if (iconTexture != null) {
                    guiGraphics.blit(iconTexture, x + 3, y + 3, 0, 0, 0, 18, 18, 18, 18);
                    return true;
                }
                return false;
            }
        });
    }

}