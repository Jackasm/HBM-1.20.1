package com.hbm.items.armor;

import com.hbm.api.item.IGasMask;
import com.hbm.render.model.ModelHazmatMask;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.ArmorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArmorHazmatMask extends ArmorHazmat implements IGasMask {

    public ArmorHazmatMask(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (slot == EquipmentSlot.HEAD) {
                    Minecraft mc = Minecraft.getInstance();
                    ModelPart root = mc.getEntityModels().bakeLayer(ModelHazmatMask.HAZMAT_MASK);
                    ModelHazmatMask model = new ModelHazmatMask(root);
                    model.young = living.isBaby();
                    model.setEntity(living);
                    return model;
                }
                return defaultModel;
            }
        });
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    public ArrayList<HazardClass> getBlacklist(ItemStack stack, LivingEntity entity) {
        return new ArrayList<>(); // full hood has no restrictions
    }

    @Override
    public ItemStack getFilter(ItemStack stack, LivingEntity entity) {
        return ArmorUtil.getGasMaskFilter(stack);
    }

    @Override
    public void installFilter(ItemStack stack, LivingEntity entity, ItemStack filter) {
        ArmorUtil.installGasMaskFilter(stack, filter);
    }

    @Override
    public void damageFilter(ItemStack stack, LivingEntity entity, int damage) {
        ArmorUtil.damageGasMaskFilter(stack, damage);
    }

    @Override
    public boolean isFilterApplicable(ItemStack stack, LivingEntity entity, ItemStack filter) {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            ItemStack filter = this.getFilter(stack, player);

            if (!filter.isEmpty()) {
                ArmorUtil.removeFilter(stack);

                if (!player.getInventory().add(filter)) {
                    player.drop(filter, true);
                }

                return InteractionResultHolder.success(stack);
            }
        }

        return super.use(level, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Player player = Minecraft.getInstance().player;
        ArmorUtil.addGasMaskTooltip(stack, player, tooltip, flag.isAdvanced());
        super.appendHoverText(stack, level, tooltip, flag);
    }
}