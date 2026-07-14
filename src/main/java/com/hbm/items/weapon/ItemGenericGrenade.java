package com.hbm.items.weapon;

import com.hbm.entity.grenade.EntityGrenadeBouncyGeneric;
import com.hbm.entity.grenade.EntityGrenadeImpactGeneric;
import com.hbm.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemGenericGrenade extends Item {

    protected int fuse;

    public ItemGenericGrenade(Properties properties, int fuse) {
        super(properties);
        this.fuse = fuse;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.GRENADE.get(), SoundSource.PLAYERS,
                0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            if (fuse == -1) {
                // Взрыв при ударе (impact)
                EntityGrenadeImpactGeneric entity = new EntityGrenadeImpactGeneric(level, player);
                entity.setType(this);
                level.addFreshEntity(entity);
            } else {
                // Взрыв по таймеру (bouncy)
                EntityGrenadeBouncyGeneric entity = new EntityGrenadeBouncyGeneric(level, player);
                entity.setType(this);
                level.addFreshEntity(entity);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public void explode(Entity grenade, LivingEntity thrower, Level level, double x, double y, double z) {
        // Переопределяется в дочерних классах
    }

    public int getMaxTimer() {
        return this.fuse * 20; // 20 тиков = 1 секунда
    }

    public double getBounceMod() {
        return 0.5D;
    }

    // Фабричный метод для свойств
    public static Properties createProperties() {
        return new Properties()
                .stacksTo(16);
    }
}