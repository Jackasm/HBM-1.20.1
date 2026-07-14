package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModArmorItems;
import com.hbm.render.model.ModelM65Small;
import com.hbm.util.RefStrings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ArmorLiquidator extends ArmorFSB {

    private ResourceLocation hazmatBlur = ResLocation(RefStrings.MODID, "textures/misc/overlay_dark.png");

    @OnlyIn(Dist.CLIENT)
    private ModelM65Small model;

    public ArmorLiquidator(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot == this.getType().getSlot()) {
            multimap.put(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(ArmorModHandler.fixedUUIDs[this.getType().getSlot().getIndex()],
                            "Armor modifier", 100D, AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(ArmorModHandler.fixedUUIDs[this.getType().getSlot().getIndex()],
                            "Armor modifier", -0.1D, AttributeModifier.Operation.MULTIPLY_BASE));
        }

        return multimap;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (stack.getItem() == ModArmorItems.LIQUIDATOR_HELMET.get() && slot == EquipmentSlot.HEAD) {
                    if (model == null) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelM65Small.M65Small);
                        model = new ModelM65Small(root);
                    }
                    model.young = living.isBaby();
                    model.setEntity(living);
                    return model;
                }
                return defaultModel;
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(GuiGraphics guiGraphics, ItemStack stack, Player player,
                                    int width, int height, float partialTicks) {

        if (this.getType() == Type.HELMET) {
            guiGraphics.blit(hazmatBlur, 0, 0, 0, 0, width, height, width, height);
        }
    }
}