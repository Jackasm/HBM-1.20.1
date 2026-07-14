package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.render.item.armor.ArmorDeshItemsRenderer;
import com.hbm.render.model.ModelArmorDesh;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArmorDesh extends ArmorFSBFueled {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorDesh[] models;

    public ArmorDesh(ArmorMaterial material, Type type, Properties properties,
                     Supplier<FluidTypeHBM> fuelType, int maxFuel, int fillRate, int consumption, int drain) {
        super(material, type, properties, fuelType, maxFuel, fillRate, consumption, drain);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        multimap.putAll(super.getAttributeModifiers(slot, stack));

        if (slot == this.getType().getSlot()) {
            int slotIndex = this.getType().getSlot().getIndex();
            UUID uuid = ArmorModHandler.fixedUUIDs[slotIndex];

            multimap.put(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(uuid, "Armor modifier", -0.025D, AttributeModifier.Operation.MULTIPLY_BASE));
        }

        return multimap;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorDesh[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorDesh.DESH_LAYERS[i]);
                        models[i] = new ModelArmorDesh(root, i);
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
                return new ArmorDeshItemsRenderer();
            }
        });
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        //Заглушка
        return "hbm:textures/models/thegadget3.png";
    }
}