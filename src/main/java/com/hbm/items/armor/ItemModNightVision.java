package com.hbm.items.armor;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemModNightVision extends ItemArmorMod {

    private static final String NIGHT_VISION_ACTIVE_NBT_KEY = "ITEM_MOD_NV_ACTIVE";

    public ItemModNightVision(Properties properties) {
        super(ArmorModHandler.HELMET_ONLY, true, false, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.night_vision.description.item").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.translatable("item.night_vision.description.in_armor", stack.getHoverName())
                .withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide && entity instanceof Player player &&
                armor.getItem() instanceof ArmorFSBPowered && ArmorFSB.hasFSBArmor(player)) {

            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(player);
            if (props != null && props.isHudEnabled()) {
                // 15 seconds to make less flickering if the client lags
                entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0));

                if (!armor.hasTag()) {
                    armor.getOrCreateTag();
                }
                if (!armor.getOrCreateTag().contains(NIGHT_VISION_ACTIVE_NBT_KEY)) {
                    armor.getOrCreateTag().putBoolean(NIGHT_VISION_ACTIVE_NBT_KEY, true);
                }

                if (entity.getRandom().nextInt(100) == 0) {
                    armor.hurtAndBreak(1, entity, e -> {});
                }
            } else if (armor.hasTag() && Objects.requireNonNull(armor.getTag()).contains(NIGHT_VISION_ACTIVE_NBT_KEY)) {
                // Disable night vision if it was the armor mod that applied it
                entity.removeEffect(MobEffects.NIGHT_VISION);
                armor.getTag().remove(NIGHT_VISION_ACTIVE_NBT_KEY);
            }
        }
    }
}