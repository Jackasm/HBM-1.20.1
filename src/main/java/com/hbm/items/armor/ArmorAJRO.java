package com.hbm.items.armor;

import com.hbm.render.item.armor.ArmorAJROItemsRenderer;
import com.hbm.render.model.ModelArmorAJRO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ArmorAJRO extends ArmorFSBPowered {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorAJRO[] models;

    public ArmorAJRO(ArmorMaterial material, Type type, Properties properties,
                     long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties, maxPower, chargeRate, consumption, drain);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorAJRO[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorAJRO.AJRO_LAYERS[i]);
                        models[i] = new ModelArmorAJRO(root, i);
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
                return new ArmorAJROItemsRenderer();
            }
        });
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        //Заглушка
        return "hbm:textures/models/thegadget3.png";
    }
}