package com.hbm.items.armor;

import com.hbm.items.ModArmorItems;

import com.hbm.render.model.ModelCloak;
import com.hbm.render.model.ModelGoggles;
import com.hbm.render.model.ModelHat;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ArmorModel extends ArmorItem implements IHelmetOverlay {

    @OnlyIn(Dist.CLIENT)
    private ResourceLocation[] gogglesBlurs;

    public ArmorModel(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ModArmorItems.GOGGLES.get()) {
            return RefStrings.MODID + ":textures/models/armor/goggles.png";
        }
        if (stack.getItem() == ModArmorItems.CAPE_RADIATION.get()) {
            return RefStrings.MODID + ":textures/models/armor/capes/cape_radiation.png";
        }
        if (stack.getItem() == ModArmorItems.CAPE_GASMASK.get()) {
            return RefStrings.MODID + ":textures/models/armor/capes/cape_gas_mask.png";
        }
        if (stack.getItem() == ModArmorItems.CAPE_SCHRABIDIUM.get()) {
            return RefStrings.MODID + ":textures/models/armor/capes/cape_schrabidium.png";
        }
        if (stack.getItem() == ModArmorItems.CAPE_HIDDEN.get()) {
            return RefStrings.MODID + ":textures/models/armor/capes/cape_hidden.png";
        }
        return RefStrings.MODID + ":textures/models/armor/capes/cape_unknown.png";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(GuiGraphics guiGraphics, ItemStack stack, Player player, int width, int height, float partialTicks) {
        if (this != ModArmorItems.GOGGLES.get() &&
                this != ModArmorItems.HAZMAT_HELMET_RED.get() &&
                this != ModArmorItems.HAZMAT_HELMET_GREY.get()) {
            return;
        }

        if (gogglesBlurs == null) {
            gogglesBlurs = new ResourceLocation[6];
            for (int i = 0; i < 6; i++) {
                gogglesBlurs[i] = ResLocation(RefStrings.MODID, "textures/misc/overlay_goggles_" + i + ".png");
            }
        }

        int blurTextureIndex = (int) ((double) stack.getDamageValue() / (double) stack.getMaxDamage() * 6D);
        if (blurTextureIndex < 0 || blurTextureIndex > 5) {
            blurTextureIndex = 5;
        }

        // Рендерим оверлей
        guiGraphics.blit(gogglesBlurs[blurTextureIndex], 0, 0, 0, 0, width, height, width, height);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (this == ModArmorItems.CAPE_RADIATION.get() ||
                this == ModArmorItems.CAPE_GASMASK.get() ||
                this == ModArmorItems.CAPE_SCHRABIDIUM.get()) {
            tooltip.add(Component.translatable("item.hbm.cape.available").withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                Minecraft mc = Minecraft.getInstance();

                if (stack.getItem() == ModArmorItems.GOGGLES.get() && slot == EquipmentSlot.HEAD) {
                    ModelPart root = mc.getEntityModels().bakeLayer(ModelGoggles.GOGGLES);
                    ModelGoggles model = new ModelGoggles(root);
                    model.setEntity(living);
                    model.young = living.isBaby();
                    return model;
                }

                if (stack.getItem() == ModArmorItems.HAT.get() && slot == EquipmentSlot.HEAD) {
                    ModelPart root = mc.getEntityModels().bakeLayer(ModelHat.HAT);
                    ModelHat model = new ModelHat(root);
                    model.setEntity(living);
                    model.young = living.isBaby();
                    return model;
                }

                if ((stack.getItem() == ModArmorItems.CAPE_RADIATION.get() ||
                        stack.getItem() == ModArmorItems.CAPE_GASMASK.get() ||
                        stack.getItem() == ModArmorItems.CAPE_SCHRABIDIUM.get() ||
                        stack.getItem() == ModArmorItems.CAPE_HIDDEN.get()) &&
                        slot == EquipmentSlot.CHEST) {
                    ModelPart root = mc.getEntityModels().bakeLayer(ModelCloak.CLOAK);
                    ModelCloak model = new ModelCloak(root);
                    model.young = living.isBaby();
                    model.setEntity(living); // Устанавливаем сущность
                    return model;
                }

                return defaultModel;
            }
        });
    }
}