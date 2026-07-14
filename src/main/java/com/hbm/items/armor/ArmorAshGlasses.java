package com.hbm.items.armor;

import com.hbm.render.model.ModelGlasses;
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

import java.util.function.Consumer;

public class ArmorAshGlasses extends ArmorItem {

    @OnlyIn(Dist.CLIENT)
    private ModelGlasses model;

    public ArmorAshGlasses(ArmorMaterial material, Properties properties) {
        super(material, Type.HELMET, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack,
                                                                   EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                if (model == null) {
                    model = new ModelGlasses(0);  // прямой конструктор, без ModelLayer
                }

                model.copyPropertiesFromHumanoidModel(_default);
                model.setupAnim(entityLiving, 0, 0, 0, 0, 0);

                return model;
            }
        });
    }
}