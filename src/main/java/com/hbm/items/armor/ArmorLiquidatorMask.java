package com.hbm.items.armor;

import com.hbm.api.item.IGasMask;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.ArmorUtil;
import net.minecraft.client.Minecraft;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArmorLiquidatorMask extends ArmorLiquidator implements IGasMask {

    public ArmorLiquidatorMask(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
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
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        Player player = Minecraft.getInstance().player;
        ArmorUtil.addGasMaskTooltip(stack, player, tooltip, flag.isAdvanced());
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

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        return "hbm:textures/models/armor/liquidator_helmet.png";
    }
}