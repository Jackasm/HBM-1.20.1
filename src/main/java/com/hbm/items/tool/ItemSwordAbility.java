package com.hbm.items.tool;

import com.hbm.handler.ability.AvailableAbilities;
import com.hbm.handler.ability.IWeaponAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSwordAbility extends SwordItem {

    protected final float attackDamage;
    protected final float attackSpeed;
    protected final AvailableAbilities availableAbilities;

    public ItemSwordAbility(Properties properties, Tier tier, float attackDamage, float attackSpeed) {
        super(tier, 0, 0, properties);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.availableAbilities = new AvailableAbilities();
    }

    public ItemSwordAbility addWeaponAbility(IWeaponAbility ability, int level) {
        this.availableAbilities.addAbility(ability, level);
        return this;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player && canOperate(stack)) {
            this.availableAbilities.getWeaponAbilities().forEach((ability, level) ->
                    ability.onHit(level, attacker.level(), (Player) attacker, target, this));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        availableAbilities.addInformation(tooltip);
        super.appendHoverText(stack, level, tooltip, flag);
    }

    protected boolean canOperate(ItemStack stack) {
        return true;
    }
}