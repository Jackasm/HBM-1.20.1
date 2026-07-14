package com.hbm.entity.mob;

import com.hbm.entity.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityDuck extends Chicken {

    public EntityDuck(EntityType<? extends EntityDuck> type, Level world) {
        super(type, world);
    }

    public EntityDuck(Level world) {
        this(ModEntities.DUCK.get(), world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Дополнительные цели можно добавить здесь если нужно
    }

    @Override
    public EntityDuck getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return new EntityDuck(ModEntities.DUCK.get(), level);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        // Отправляем сообщение в чат при смерти
        if (!this.level().isClientSide) {
            String deathMessage = this.getCombatTracker().getDeathMessage().getString();
            if (this.level() instanceof ServerLevel serverLevel) {
                MinecraftServer server = serverLevel.getServer();
                if (server != null) {
                    server.getPlayerList().broadcastSystemMessage(
                            Component.literal(deathMessage),
                            false
                    );
                }
            }
        }
        super.die(source);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Утки едят семена (как курицы)
        return stack.is(Items.WHEAT_SEEDS) ||
                stack.is(Items.MELON_SEEDS) ||
                stack.is(Items.PUMPKIN_SEEDS) ||
                stack.is(Items.BEETROOT_SEEDS);
    }
}