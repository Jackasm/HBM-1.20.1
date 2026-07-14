package com.hbm.items.armor;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.render.item.armor.ArmorTrenchmasterItemsRenderer;
import com.hbm.render.model.ModelArmorTrenchmaster;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ArmorTrenchmaster extends ArmorFSB implements IAttackHandler, IDamageHandler {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorTrenchmaster[] models;

    public ArmorTrenchmaster(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (models == null) {
                    models = new ModelArmorTrenchmaster[4];
                    for (int i = 0; i < 4; i++) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorTrenchmaster.TRENCH_LAYERS[i]);
                        models[i] = new ModelArmorTrenchmaster(root, i);
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
                return new ArmorTrenchmasterItemsRenderer();
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("armor.moreAmmo").withStyle(ChatFormatting.RED));
    }

    @Override
    public void handleDamage(LivingHurtEvent event, ItemStack stack) {

        if (event.getEntity() instanceof Player player && ArmorFSB.hasFSBArmor(player)) {
            if (event.getSource().is(DamageTypeTags.IS_EXPLOSION) && event.getSource().getEntity() == player) {
                event.setAmount(0);
            }
        }
    }

    @Override
    public void handleAttack(LivingAttackEvent event, ItemStack stack) {

        if (event.getEntity() instanceof Player player && ArmorFSB.hasFSBArmor(player)) {
            if (player.getRandom().nextInt(3) == 0) {
                HbmPlayerProps.plink(player, "random.break", 0.5F, 1.0F + player.getRandom().nextFloat() * 0.5F);
                event.setCanceled(true);
            }
        }
    }

    public static boolean isTrenchMaster(Player player) {
        if (player == null) return false;
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        return !chestplate.isEmpty() && chestplate.getItem() == ModArmorItems.TRENCHMASTER_CHESTPLATE.get() && ArmorFSB.hasFSBArmor(player);
    }

    public static boolean hasAoS(Player player) {
        if (player == null) return false;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!helmet.isEmpty()) {
            ItemStack[] mods = ArmorModHandler.pryMods(helmet);
            ItemStack helmetMod = mods[ArmorModHandler.HELMET_ONLY];
            return !helmetMod.isEmpty() && helmetMod.getItem() == ModItems.CARD_AOS.get();
        }
        return false;
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        //Заглушка
        return "hbm:textures/models/thegadget3.png";
    }
}