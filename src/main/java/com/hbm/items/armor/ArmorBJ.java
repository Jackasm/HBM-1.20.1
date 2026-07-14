package com.hbm.items.armor;

import com.hbm.items.ModArmorItems;
import com.hbm.render.item.armor.ArmorBJItemsRenderer;
import com.hbm.render.model.ModelArmorBJ;
import com.hbm.util.ModDamageSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ArmorBJ extends ArmorFSBPowered {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorBJ[] models;

    public ArmorBJ(ArmorMaterial material, Type type, Properties properties,
                   long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties, maxPower, chargeRate, consumption, drain);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorBJ[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorBJ.BJ_LAYERS[i]);
                        models[i] = new ModelArmorBJ(root, i);
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
                return new ArmorBJItemsRenderer();
            }
        });
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        //Заглушка
        return "hbm:textures/models/thegadget3.png";
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        // Проверяем, что предмет в слоте шлема
        boolean isInHelmetSlot = slotIndex == 39;
        if (!isInHelmetSlot) return;

        if (this == ModArmorItems.BJ_HELMET.get() &&
                ArmorFSB.hasFSBArmorIgnoreCharge(player) &&
                !ArmorFSB.hasFSBArmor(player)) {

            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

            if (!player.getInventory().add(helmet)) {
                player.drop(helmet, false);
            }

            player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            DamageSource lunarDamage = ModDamageSource.createDamageSource(
                    ModDamageSource.LUNAR,
                    null,
                    null,
                    level
            );
            player.hurt(lunarDamage, 1000);
        }
    }
}