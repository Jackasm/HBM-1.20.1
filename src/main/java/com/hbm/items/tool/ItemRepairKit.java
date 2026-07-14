package com.hbm.items.tool;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemRepairKit extends Item {

    public ItemRepairKit(int dura) {
        super(new Properties().stacksTo(1).durability(dura - 1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        boolean didSomething = false;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (!item.isEmpty() && item.getItem() instanceof GunItem gun) {
                int configs = gun.getConfigCount();

                for (int j = 0; j < configs; j++) {
                    GunConfig cfg = gun.getConfig(item, j);
                    float maxDura = cfg.getDurability(item);
                    float wear = Math.min(gun.getWear(item, j), maxDura);
                    if (wear > 0) {
                        gun.setWear(item, j, Math.max(0F, gun.getWear(item, j) - maxDura * 0.25F));
                        didSomething = true;
                    }
                }
            }
        }

        if (didSomething) {
            if (this == ModItems.GUN_KIT_1.get()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.SPRAY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            if (this == ModItems.GUN_KIT_2.get()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.REPAIR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return stack.getMaxDamage() > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        if (stack.getMaxDamage() == 0) return 13;
        float damage = stack.getDamageValue();
        float maxDamage = stack.getMaxDamage();
        float condition = (maxDamage - damage) / maxDamage;
        return Math.round(13.0F * condition);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        if (stack.getMaxDamage() == 0) return 0x00FF00;
        float damage = stack.getDamageValue();
        float maxDamage = stack.getMaxDamage();
        float condition = (maxDamage - damage) / maxDamage;

        if (condition > 0.66f) {
            return 0x00FF00;
        } else if (condition > 0.33f) {
            return 0xFFAA00;
        } else {
            return 0xFF0000;
        }
    }
}