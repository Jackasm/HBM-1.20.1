package com.hbm.items.armor;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.network.PacketDispatcher;

import com.hbm.render.item.armor.ArmorBJItemsRenderer;
import com.hbm.render.model.ModelArmorBJ;
import com.hbm.sound.ModSounds;
import com.hbm.util.ArmorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ArmorBJJetpack extends ArmorBJ {

    @OnlyIn(Dist.CLIENT)
    private ModelArmorBJ model;

    public ArmorBJJetpack(ArmorMaterial material, Type type, Properties properties,
                          long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties, maxPower, chargeRate, consumption, drain);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (slot == EquipmentSlot.CHEST) {
                    if (model == null) {
                        Minecraft mc = Minecraft.getInstance();
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelArmorBJ.BJ_LAYERS[1]);
                        model = new ModelArmorBJ(root, 5); // type 5 = с джетпаком
                    }
                    model.young = living.isBaby();
                    model.setEntity(living);
                    return model;
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
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        // Проверяем, что предмет в слоте нагрудника
        boolean isInChestSlot = slotIndex == 38;
        if (!isInChestSlot) return;

        HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
        if (props == null) return;

        // Частицы на сервере
        if (!level.isClientSide && ArmorFSB.hasFSBArmor(player) && props.isJetpackActive()) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "jetpack_bj");
            data.putInt("player", player.getId());

            // Используем существующий метод отправки пакета
            PacketDispatcher.sendAuxParticleNT(data, player.getX(), player.getY(), player.getZ(), player);
        }

        // Логика полёта на клиенте и сервере
        if (ArmorFSB.hasFSBArmor(player)) {
            ArmorUtil.resetFlightTime(player);

            if (props.isJetpackActive()) {
                // Активный джетпак
                if (player.getDeltaMovement().y < 0.4D) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, 0.1D, 0));
                }
                player.fallDistance = 0;

                if (level.isClientSide) {
                    level.playSound(player, player.getX(), player.getY(), player.getZ(),
                            ModSounds.IMMOLATOR_SHOOT.get(), player.getSoundSource(), 0.125F, 1.5F);
                }
            } else if (player.isCrouching()) {
                // Режим планирования (при приседании)
                if (player.getDeltaMovement().y < -0.08) {
                    double mo = player.getDeltaMovement().y * -0.4;
                    player.setDeltaMovement(player.getDeltaMovement().add(0, mo, 0));

                    Vec3 look = player.getLookAngle();
                    player.setDeltaMovement(player.getDeltaMovement().add(
                            look.x * mo,
                            look.y * mo,
                            look.z * mo
                    ));
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.translatable("armor.electricJetpack")
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("armor.glider")
                .withStyle(ChatFormatting.GRAY));
    }
}