package com.hbm.items.armor;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.ModArmorItems;
import com.hbm.render.item.armor.ArmorHEVItemsRenderer;
import com.hbm.render.model.ModelArmorHEV;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ArmorHEV extends ArmorFSBPowered {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorHEV[] models;

    private static long lastSurvey;
    private static float prevResult;
    private static float lastResult;

    public ArmorHEV(ArmorMaterial material, Type type, Properties properties,
                    long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties, maxPower, chargeRate, consumption, drain);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorHEV[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorHEV.HEV_LAYERS[i]);
                        models[i] = new ModelArmorHEV(root, i);
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
                return new ArmorHEVItemsRenderer();
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(GuiGraphics guiGraphics, ItemStack stack, Player player,
                                    int width, int height, float partialTicks) {
        // Проверяем, что надет полный сет HEV
        if (!hasFSBArmorIgnoreCharge(player)) return;

        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty() || chestplate.getItem() != ModArmorItems.HEV_CHESTPLATE.get()) return;

        float radiation = HbmLivingProps.getRadiation(player);

        // Обновляем замер радиации
        if (System.currentTimeMillis() >= lastSurvey + 1000) {
            lastSurvey = System.currentTimeMillis();
            prevResult = lastResult;
            lastResult = radiation;
        }

        float radDelta =  lastResult - prevResult;

        Font font = Minecraft.getInstance().font;

        // Масштабирование
        double scale = 2D;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale((float) scale, (float) scale, (float) scale);

        // Здоровье
        int health = (int) (player.getHealth() * 5);
        int healthColor = health > 15 ? 0xff8000 : 0xff0000;
        int hX = (int) (8 / scale);
        int hY = (int) ((height - 18 - 2) / scale);
        guiGraphics.drawString(font, "+" + health, hX, hY, healthColor);

        // Заряд брони
        double totalCharge = 0;
        for (int i = 0; i < 4; i++) {
            ItemStack armor = player.getInventory().armor.get(i);
            if (armor.getItem() instanceof ArmorFSBPowered powered) {
                totalCharge += (double) powered.getCharge(armor) / (double) powered.getMaxCharge(armor);
            }
        }

        int armorValue = (int) (totalCharge * 25);
        int armorColor = armorValue > 15 ? 0xff8000 : 0xff0000;
        int aX = (int) (70 / scale);
        int aY = (int) ((height - 18 - 2) / scale);
        guiGraphics.drawString(font, "||" + armorValue, aX, aY, armorColor);

        // Радиация
        StringBuilder radBar = new StringBuilder("☢ [");
        for (int i = 0; i < 10; i++) {
            if (radiation / 100 > i) {
                int mid = (int) (radiation - i * 100);
                if (mid < 33) radBar.append("..");
                else if (mid < 67) radBar.append("|.");
                else radBar.append("||");
            } else {
                radBar.append(" ");
            }
        }
        radBar.append("]");

        int radColor = radiation < 800 ? 0xff8000 : 0xff0000;
        int rX = (int) (8 / scale);
        int rY = (int) ((height - 40) / scale);
        guiGraphics.drawString(font, radBar.toString(), rX, rY, radColor);

        guiGraphics.pose().popPose();

        // Дельта радиации (без масштабирования)
        if (radDelta > 0) {
            String delta;
            if (radDelta > 1000) delta = ">1000 RAD/s";
            else if (radDelta < 1) delta = "<1 RAD/s";
            else delta = Math.round(radDelta) + " RAD/s";

            int dX = 32;
            int dY = height - 55;
            guiGraphics.drawString(font, delta, dX, dY, 0xFF0000);
        }
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        //Заглушка
        return "hbm:textures/models/thegadget3.png";
    }
}