package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModArmorItems;
import com.hbm.render.item.armor.ArmorEnvsuitItemsRenderer;
import com.hbm.render.model.ModelArmorEnvsuit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class ArmorEnvsuit extends ArmorFSBPowered {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorEnvsuit[] models;

    private static final UUID SPEED_UUID = UUID.fromString("6ab858ba-d712-485c-bae9-e5e765fc555a");

    public ArmorEnvsuit(ArmorMaterial material, Type type, Properties properties,
                        long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties, maxPower, chargeRate, consumption, drain);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorEnvsuit[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorEnvsuit.ENV_LAYERS[i]);
                        models[i] = new ModelArmorEnvsuit(root, i);
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
                return new ArmorEnvsuitItemsRenderer();
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot == EquipmentSlot.CHEST && this == ModArmorItems.ENVSUIT_CHESTPLATE.get()) {
            multimap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_UUID, "SQUIRREL SPEED", 0.1, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        boolean isInArmorSlot = slotIndex >= 36 && slotIndex <= 39;
        if (!isInArmorSlot) return;

        if (this != ModArmorItems.ENVSUIT_CHESTPLATE.get()) return;

        // Скорость при спринте
        if (ArmorFSB.hasFSBArmor(player)) {
            if (player.isSprinting()) {
                Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addTransientModifier(
                        new AttributeModifier(SPEED_UUID, "SQUIRREL SPEED", 0.1, AttributeModifier.Operation.ADDITION));
            } else {
                Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(SPEED_UUID);
            }

            // Под водой
            if (player.isInWater()) {
                if (!level.isClientSide) {
                    player.setAirSupply(300);
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0));
                }

                double mo = 0.1 * player.zza;
                Vec3 look = player.getLookAngle();
                player.setDeltaMovement(player.getDeltaMovement().add(
                        look.x * mo,
                        look.y * mo,
                        look.z * mo
                ));
            } else {
                boolean canRemoveNightVision = true;
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                ItemStack helmetMod = ArmorModHandler.pryMod(helmet, ArmorModHandler.HELMET_ONLY);
                if (!helmetMod.isEmpty() && helmetMod.getItem() instanceof ItemModNightVision) {
                    canRemoveNightVision = false;
                }

                if (!level.isClientSide && canRemoveNightVision) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            }
        }
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        //Заглушка
        return "hbm:textures/models/thegadget3.png";
    }
}