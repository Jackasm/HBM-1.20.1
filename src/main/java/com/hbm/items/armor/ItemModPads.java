package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModPads extends ItemArmorMod {

    private final float damageMod;
    private final boolean isStatic;

    public ItemModPads(Properties properties, float damageMod, boolean isStatic) {
        super(ArmorModHandler.BOOTS_ONLY, false, false, false, true, properties);
        this.damageMod = damageMod;
        this.isStatic = isStatic;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (damageMod != 1F) {
            tooltip.add(Component.literal(ChatFormatting.RED + "-" + Math.round((1F - damageMod) * 100) + "% fall damage"));
        }

        if (isStatic) {
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + "Passively charges electric armor when walking"));
        }

        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (event.getSource().is(DamageTypeTags.IS_FALL)) {
            event.setAmount(event.getAmount() * damageMod);
        }
    }

    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide && isStatic && entity instanceof Player player) {
            if (player.walkDist != player.walkDistO) {
                if (ArmorFSB.hasFSBArmorIgnoreCharge(player)) {
                    for (int i = 0; i < 4; i++) {
                        ItemStack stack = player.getInventory().armor.get(i);
                        if (stack != null && stack.getItem() instanceof ArmorFSBPowered powered) {
                            long charge = powered.drain / 2;
                            if (charge == 0) charge = powered.consumption / 40;
                            long power = Math.min(powered.getMaxCharge(stack), powered.getCharge(stack) + charge);
                            powered.setCharge(stack, power);
                        }
                    }
                }
            }
        }
    }
}