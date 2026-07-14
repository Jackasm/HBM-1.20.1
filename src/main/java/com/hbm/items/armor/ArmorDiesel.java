package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModArmorItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.render.item.armor.ArmorDieselItemsRenderer;
import com.hbm.render.model.ModelArmorDiesel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ArmorDiesel extends ArmorFSBFueled {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorDiesel[] models;

    public ArmorDiesel(ArmorMaterial material, Type type, Properties properties,
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

            multimap.put(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(uuid, "Armor modifier", 0.25D, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        ItemStack legSlot = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!level.isClientSide && legSlot.getItem() == ModArmorItems.DIESELSUIT_LEGGINGS.get() && hasFSBArmor(player) && level.getGameTime() % 3 == 0) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "diesel_suit");
            data.putInt("player", player.getId());
            PacketDispatcher.sendAuxParticleNT(data, player.getX(), player.getY(), player.getZ(), player);
        }
    }

    @Override
    public boolean acceptsFluid(FluidTypeHBM type, ItemStack stack) {
        return type == Fluids.DIESEL.get() || type == Fluids.DIESEL_CRACK.get();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorDiesel[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorDiesel.DIESEL_LAYERS[i]);
                        models[i] = new ModelArmorDiesel(root, i);
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
                return new ArmorDieselItemsRenderer();
            }
        });
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        return "hbm:textures/models/thegadget3.png";
    }

}