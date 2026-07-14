package com.hbm.items.armor;

import com.hbm.items.ModArmorItems;
import com.hbm.render.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ArmorT45 extends ArmorFSBPowered {

    @OnlyIn(Dist.CLIENT)
    private ModelT45Helmet helmet;
    @OnlyIn(Dist.CLIENT)
    private ModelT45Chest plate;
    @OnlyIn(Dist.CLIENT)
    private ModelT45Legs legs;
    @OnlyIn(Dist.CLIENT)
    private ModelT45Boots boots;

    public ArmorT45(ArmorMaterial material, ArmorItem.Type type, Properties properties,
                    long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties, maxPower, chargeRate, consumption, drain);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (stack.getItem() == ModArmorItems.T45_HELMET.get() && slot == EquipmentSlot.HEAD) {
                    if (helmet == null) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelT45Helmet.T45_HELMET);
                        helmet = new ModelT45Helmet(root);
                    }
                    helmet.young = living.isBaby();
                    helmet.setEntity(living);
                    return helmet;
                }
                if (stack.getItem() == ModArmorItems.T45_CHESTPLATE.get() && slot == EquipmentSlot.CHEST) {
                    if (plate == null) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelT45Chest.T45_CHEST);
                        plate = new ModelT45Chest(root);
                    }
                    plate.young = living.isBaby();
                    plate.setEntity(living);
                    return plate;
                }
                if (stack.getItem() == ModArmorItems.T45_LEGGINGS.get() && slot == EquipmentSlot.LEGS) {
                    if (legs == null) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelT45Legs.T45_LEGS);
                        legs = new ModelT45Legs(root);
                    }
                    legs.young = living.isBaby();
                    legs.setEntity(living);
                    return legs;
                }
                if (stack.getItem() == ModArmorItems.T45_BOOTS.get() && slot == EquipmentSlot.FEET) {
                    if (boots == null) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelT45Boots.T45_BOOTS);
                        boots = new ModelT45Boots(root);
                    }
                    boots.young = living.isBaby();
                    boots.setEntity(living);
                    return boots;
                }
                return defaultModel;
            }
        });
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ModArmorItems.T45_HELMET.get()) {
            return "hbm:textures/models/armor/t45_helmet.png";
        }
        if (stack.getItem() == ModArmorItems.T45_CHESTPLATE.get()) {
            return "hbm:textures/models/armor/t45_chest.png";
        }
        if (stack.getItem() == ModArmorItems.T45_LEGGINGS.get()) {
            return "hbm:textures/models/armor/t45_legs.png";
        }
        if (stack.getItem() == ModArmorItems.T45_BOOTS.get()) {
            return "hbm:textures/models/armor/t45_boots.png";
        }
        return null;
    }
}