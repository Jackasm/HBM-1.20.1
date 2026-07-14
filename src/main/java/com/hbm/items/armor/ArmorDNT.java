package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.items.ModArmorItems;
import com.hbm.network.PacketDispatcher;

import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.render.item.armor.ArmorDNTItemsRenderer;
import com.hbm.render.model.ModelArmorDNT;
import com.hbm.sound.ModSounds;
import com.hbm.util.ArmorUtil;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class ArmorDNT extends ArmorFSBPowered implements IAttackHandler, IDamageHandler {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorDNT[] models;

    private static final UUID SPEED_UUID = UUID.fromString("6ab858ba-d712-485c-bae9-e5e765fc555a");

    public ArmorDNT(ArmorMaterial material, Type type, Properties properties,
                    long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties, maxPower, chargeRate, consumption, drain);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorDNT[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorDNT.DNT_LAYERS[i]);
                        models[i] = new ModelArmorDNT(root, i);
                        models[i].setEntity(living);
                        models[i].young = living.isBaby();
                    }
                }

                int slotIndex = slot.getIndex();
                if (slotIndex >= 0 && slotIndex < 4) {
                    return models[slotIndex];
                }
                return defaultModel;
            }
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new ArmorDNTItemsRenderer();
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot == EquipmentSlot.CHEST && this == ModArmorItems.DNT_CHESTPLATE.get()) {
            multimap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_UUID, "DNT SPEED", 0.25, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        boolean isInArmorSlot = slotIndex >= 36 && slotIndex <= 39;
        if (!isInArmorSlot) return;

        if (this != ModArmorItems.DNT_CHESTPLATE.get()) return;

        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
        if (props == null) return;

        // Скорость при спринте
        if (player.isSprinting()) {
            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addTransientModifier(
                    new AttributeModifier(SPEED_UUID, "DNT SPEED", 0.25, AttributeModifier.Operation.ADDITION));
        } else {
            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(SPEED_UUID);
        }

        // Эффекты полёта
        if (ArmorFSB.hasFSBArmor(player)) {
            ArmorUtil.resetFlightTime(player);

            boolean jetpackActive = props.isJetpackActive();
            boolean enableBackpack = props.isBackpackEnabled();

            if (jetpackActive) {
                if (player.getDeltaMovement().y < 0.6D) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, 0.2D, 0));
                }
                player.fallDistance = 0;

                if (level.isClientSide) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(),
                            ModSounds.IMMOLATOR_SHOOT.get(), player.getSoundSource(), 0.125F, 1.5F);
                }
            } else if (!player.isCrouching() && !player.onGround() && enableBackpack) {
                player.fallDistance = 0;

                Vec3 motion = player.getDeltaMovement();
                if (motion.y < -1) {
                    player.setDeltaMovement(motion.add(0, 0.4D, 0));
                } else if (motion.y < -0.1) {
                    player.setDeltaMovement(motion.add(0, 0.2D, 0));
                } else if (motion.y < 0) {
                    player.setDeltaMovement(motion.multiply(1, 0, 1));
                }

                player.setDeltaMovement(motion.x * 1.05D, motion.y, motion.z * 1.05D);

                if (player.zza != 0) {
                    Vec3 look = player.getLookAngle();
                    player.setDeltaMovement(player.getDeltaMovement().add(look.x * 0.25 * player.zza, 0, look.z * 0.25 * player.zza));
                }

                if (level.isClientSide) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(),
                            ModSounds.IMMOLATOR_SHOOT.get(), player.getSoundSource(), 0.125F, 1.5F);
                }
            }

            if (player.isCrouching() && !player.onGround()) {
                player.setDeltaMovement(player.getDeltaMovement().add(0, -0.1D, 0));
            }

            // Частицы
            if (!level.isClientSide && (jetpackActive || (!player.onGround() && enableBackpack))) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "jetpack_dns");
                data.putInt("player", player.getId());
                PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data, player.getX(), player.getY(), player.getZ()),
                        level, player.getOnPos(), 100);
            }
        }
    }

    @Override
    public void handleAttack(LivingAttackEvent event, ItemStack stack) {
        if (event.getEntity() instanceof Player player && ArmorFSB.hasFSBArmor(player)) {
            if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
                return;
            }
            HbmPlayerProps.plink(player, "random.break", 0.5F, 1.0F + player.getRandom().nextFloat() * 0.5F);
            event.setCanceled(true);
        }
    }

    @Override
    public void handleDamage(LivingHurtEvent event, ItemStack stack) {
        if (event.getEntity() instanceof Player player && ArmorFSB.hasFSBArmor(player)) {
            if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
                event.setAmount(event.getAmount() * 0.001F);
                return;
            }
            event.setAmount(0);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("Charge: " + BobMathUtil.getShortNumber(getCharge(stack)) + " / " +
                BobMathUtil.getShortNumber(getMaxCharge(stack))).withStyle(ChatFormatting.GRAY));
        // Эффекты
        tooltip.add(Component.translatable("armor.rocketBoots").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("armor.fastFall").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("armor.sprintBoost").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        //Заглушка
        return "hbm:textures/models/thegadget3.png";
    }
}