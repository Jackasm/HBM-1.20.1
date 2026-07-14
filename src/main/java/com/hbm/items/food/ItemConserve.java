package com.hbm.items.food;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.items.ItemEnumMulti;
import com.hbm.items.ModItems;
import com.hbm.util.EnumUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class ItemConserve extends ItemEnumMulti<ItemConserve.EnumFoodType> {

    public ItemConserve(Properties properties) {
        super(properties, EnumFoodType.class, true);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        EnumFoodType num = (EnumFoodType) getType(stack);

        if (entity instanceof Player player) {
            stack.shrink(1);
            player.getFoodData().eat(num.foodLevel, num.saturation);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            this.onFoodEaten(stack, level, player);
        }

        return stack;
    }

    protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        player.getInventory().add(new ItemStack(ModItems.CAN_KEY.get()));
        EnumFoodType num = (EnumFoodType) getType(stack);

        if (num == EnumFoodType.BHOLE && !level.isClientSide) {
            EntityVortex vortex = new EntityVortex(ModEntities.VORTEX.get(), level);
            vortex.setPos(player.getX(), player.getY(), player.getZ());
            level.addFreshEntity(vortex);

        } else if (num == EnumFoodType.RECURSION && level.random.nextInt(10) > 0) {
            player.getInventory().add(stackFromEnum(EnumFoodType.RECURSION));
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.canEat(false)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {

        String unloc = this.getDescriptionId(stack) + ".desc";
        MutableComponent loc = Component.translatable(unloc);

        String locString = loc.getString();
        if (!unloc.equals(locString)) {
            String[] locs = locString.split("\\$");
            for (String s : locs) {
                tooltip.add(Component.literal(s));
            }
        }
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        Enum num = getType(stack);
        return "item.canned_" + num.name().toLowerCase(Locale.US);
    }

    public enum EnumFoodType {
        BEEF(8, 0.75F),
        TUNA(4, 0.75F),
        MYSTERY(6, 0.5F),
        PASHTET(4, 0.5F),
        CHEESE(3, 1F),
        JIZZ(15, 5F), // :3
        MILK(5, 0.25F),
        ASS(6, 0.75F), // :3
        PIZZA(8, 075F),
        TUBE(2, 0.25F),
        TOMATO(4, 0.5F),
        ASBESTOS(7, 1F),
        BHOLE(10, 1F),
        HOTDOGS(5, 0.75F),
        LEFTOVERS(1, 0.1F),
        YOGURT(3, 0.5F),
        STEW(5, 0.5F),
        CHINESE(6, 0.1F),
        OIL(3, 1F),
        FIST(6, 0.75F),
        SPAM(8, 1F),
        FRIED(10, 0.75F),
        NAPALM(6, 1F),
        DIESEL(6, 1F),
        KEROSENE(6, 1F),
        RECURSION(1, 1F),
        BARK(2, 1F);

        protected final int foodLevel;
        protected final float saturation;

        EnumFoodType(int level, float sat) {
            this.foodLevel = level;
            this.saturation = sat;
        }
    }
}