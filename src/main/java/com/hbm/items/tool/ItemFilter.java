package com.hbm.items.tool;

import com.hbm.api.item.IGasMask;
import com.hbm.handler.ArmorModHandler;
import com.hbm.sound.ModSounds;
import com.hbm.util.ArmorUtil;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemFilter extends Item {

    public ItemFilter() {
        super(new Properties()
                .durability(20000));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        if (helmet.isEmpty()) {
            return InteractionResultHolder.pass(stack);
        }

        if (!(helmet.getItem() instanceof IGasMask)) {
            if (ArmorModHandler.hasMods(helmet)) {
                ItemStack[] mods = ArmorModHandler.pryMods(helmet);
                if (mods[ArmorModHandler.HELMET_ONLY] != null) {
                    ItemStack mask = mods[ArmorModHandler.HELMET_ONLY];
                    ItemStack ret = installFilterOn(mask, stack, level, player);
                    ArmorModHandler.applyMod(helmet, mask);
                    return InteractionResultHolder.success(ret);
                }
            }
        }

        ItemStack ret = installFilterOn(helmet, stack, level, player);
        return InteractionResultHolder.success(ret);
    }

    private ItemStack installFilterOn(ItemStack helmet, ItemStack filter, Level level, Player player) {
        if (!(helmet.getItem() instanceof IGasMask mask)) {
            return filter;
        }

        if (!mask.isFilterApplicable(helmet, player, filter)) {
            return filter;
        }

        ItemStack copy = filter.copy();
        ItemStack current = ArmorUtil.getGasMaskFilter(helmet);

        if (!current.isEmpty()) {
            filter = current;
        } else {
            filter.shrink(filter.getCount());
        }

        ArmorUtil.installGasMaskFilter(helmet, copy);

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.GASMASK_SCREW.get(), SoundSource.PLAYERS,
                    1.0F, 1.0F);
        }

        return filter;
    }
}