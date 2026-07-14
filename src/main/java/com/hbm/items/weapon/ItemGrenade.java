package com.hbm.items.weapon;

import com.hbm.entity.ModEntities;
import com.hbm.entity.grenade.*;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemGrenade extends Item {

    public int fuse = 4;

    public ItemGrenade(Properties properties, int fuse) {
        super(properties.stacksTo(16));
        this.fuse = fuse;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, player.getSoundSource(), 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            spawnGrenade(level, player, stack.getItem());
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void spawnGrenade(Level level, Player player, Item item) {
        if (item == ModItems.GRENADE_GENERIC.get()) {
            level.addFreshEntity(new EntityGrenadeGeneric(ModEntities.GRENADE_GENERIC.get(), level, player));
        }
        if (item == ModItems.GRENADE_KYIV.get()) {
            level.addFreshEntity(new EntityGrenadeGeneric(ModEntities.GRENADE_KYIV.get(), level, player));
        }
        else if (item == ModItems.GRENADE_STRONG.get()) {
            level.addFreshEntity(new EntityGrenadeStrong(ModEntities.GRENADE_STRONG.get(), level, player));
        }
        else if (item == ModItems.GRENADE_FRAG.get()) {
            level.addFreshEntity(new EntityGrenadeFrag(ModEntities.GRENADE_FRAG.get(), level, player));
        }
        else if (item == ModItems.GRENADE_FIRE.get()) {
            level.addFreshEntity(new EntityGrenadeFire(ModEntities.GRENADE_FIRE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_CLUSTER.get()) {
            level.addFreshEntity(new EntityGrenadeCluster(ModEntities.GRENADE_CLUSTER.get(), level, player));
        }
        else if (item == ModItems.GRENADE_FLARE.get()) {
            level.addFreshEntity(new EntityGrenadeFlare(ModEntities.GRENADE_FLARE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_ELECTRIC.get()) {
            level.addFreshEntity(new EntityGrenadeElectric(ModEntities.GRENADE_ELECTRIC.get(), level, player));
        }
        else if (item == ModItems.GRENADE_POISON.get()) {
            level.addFreshEntity(new EntityGrenadePoison(ModEntities.GRENADE_POISON.get(), level, player));
        }
        else if (item == ModItems.GRENADE_GAS.get()) {
            level.addFreshEntity(new EntityGrenadeGas(ModEntities.GRENADE_GAS.get(), level, player));
        }
        else if (item == ModItems.GRENADE_SCHRABIDIUM.get()) {
            level.addFreshEntity(new EntityGrenadeSchrabidium(ModEntities.GRENADE_SCHRABIDIUM.get(), level, player));
        }
        else if (item == ModItems.GRENADE_NUKE.get()) {
            level.addFreshEntity(new EntityGrenadeNuke(ModEntities.GRENADE_NUKE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_NUCLEAR.get()) {
            level.addFreshEntity(new EntityGrenadeNuclear(ModEntities.GRENADE_NUCLEAR.get(), level, player));
        }
        else if (item == ModItems.GRENADE_PULSE.get()) {
            level.addFreshEntity(new EntityGrenadePulse(ModEntities.GRENADE_PULSE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_PLASMA.get()) {
            level.addFreshEntity(new EntityGrenadePlasma(ModEntities.GRENADE_PLASMA.get(), level, player));
        }
        else if (item == ModItems.GRENADE_TAU.get()) {
            level.addFreshEntity(new EntityGrenadeTau(ModEntities.GRENADE_TAU.get(), level, player));
        }
        else if (item == ModItems.GRENADE_LEMON.get()) {
            level.addFreshEntity(new EntityGrenadeLemon(ModEntities.GRENADE_LEMON.get(), level, player));
        }
        else if (item == ModItems.GRENADE_MK2.get()) {
            level.addFreshEntity(new EntityGrenadeMk2(ModEntities.GRENADE_MK2.get(), level, player));
        }
        else if (item == ModItems.GRENADE_ASCHRAB.get()) {
            level.addFreshEntity(new EntityGrenadeASchrab(ModEntities.GRENADE_ASCHRAB.get(), level, player));
        }
        else if (item == ModItems.GRENADE_ZOMG.get()) {
            level.addFreshEntity(new EntityGrenadeZOMG(ModEntities.GRENADE_ZOMG.get(), level, player));
        }
        else if (item == ModItems.GRENADE_SHRAPNEL.get()) {
            level.addFreshEntity(new EntityGrenadeShrapnel(ModEntities.GRENADE_SHRAPNEL.get(), level, player));
        }
        else if (item == ModItems.GRENADE_BLACK_HOLE.get()) {
            level.addFreshEntity(new EntityGrenadeBlackHole(ModEntities.GRENADE_BLACK_HOLE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_GASCAN.get()) {
            level.addFreshEntity(new EntityGrenadeGascan(ModEntities.GRENADE_GASCAN.get(), level, player));
        }
        else if (item == ModItems.GRENADE_CLOUD.get()) {
            level.addFreshEntity(new EntityGrenadeCloud(ModEntities.GRENADE_CLOUD.get(), level, player));
        }
        else if (item == ModItems.GRENADE_PINK_CLOUD.get()) {
            level.addFreshEntity(new EntityGrenadePC(ModEntities.GRENADE_PC.get(), level, player));
        }
        else if (item == ModItems.GRENADE_SMART.get()) {
            level.addFreshEntity(new EntityGrenadeSmart(ModEntities.GRENADE_SMART.get(), level, player));
        }
        else if (item == ModItems.GRENADE_MIRV.get()) {
            level.addFreshEntity(new EntityGrenadeMIRV(ModEntities.GRENADE_MIRV.get(), level, player));
        }
        else if (item == ModItems.GRENADE_BREACH.get()) {
            level.addFreshEntity(new EntityGrenadeBreach(ModEntities.GRENADE_BREACH.get(), level, player));
        }
        else if (item == ModItems.GRENADE_BURST.get()) {
            level.addFreshEntity(new EntityGrenadeBurst(ModEntities.GRENADE_BURST.get(), level, player));
        }
        // IF series
        else if (item == ModItems.GRENADE_IF_GENERIC.get()) {
            level.addFreshEntity(new EntityGrenadeIFGeneric(ModEntities.GRENADE_IF_GENERIC.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_HE.get()) {
            level.addFreshEntity(new EntityGrenadeIFHE(ModEntities.GRENADE_IF_HE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_BOUNCY.get()) {
            level.addFreshEntity(new EntityGrenadeIFBouncy(ModEntities.GRENADE_IF_BOUNCY.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_STICKY.get()) {
            level.addFreshEntity(new EntityGrenadeIFSticky(ModEntities.GRENADE_IF_STICKY.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_IMPACT.get()) {
            level.addFreshEntity(new EntityGrenadeIFImpact(ModEntities.GRENADE_IF_IMPACT.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_INCENDIARY.get()) {
            level.addFreshEntity(new EntityGrenadeIFIncendiary(ModEntities.GRENADE_IF_INCENDIARY.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_TOXIC.get()) {
            level.addFreshEntity(new EntityGrenadeIFToxic(ModEntities.GRENADE_IF_TOXIC.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_CONCUSSION.get()) {
            level.addFreshEntity(new EntityGrenadeIFConcussion(ModEntities.GRENADE_IF_CONCUSSION.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_BRIMSTONE.get()) {
            level.addFreshEntity(new EntityGrenadeIFBrimstone(ModEntities.GRENADE_IF_BRIMSTONE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_MYSTERY.get()) {
            level.addFreshEntity(new EntityGrenadeIFMystery(ModEntities.GRENADE_IF_MYSTERY.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_SPARK.get()) {
            level.addFreshEntity(new EntityGrenadeIFSpark(ModEntities.GRENADE_IF_SPARK.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_HOPWIRE.get()) {
            level.addFreshEntity(new EntityGrenadeIFHopwire(ModEntities.GRENADE_IF_HOPWIRE.get(), level, player));
        }
        else if (item == ModItems.GRENADE_IF_NULL.get()) {
            level.addFreshEntity(new EntityGrenadeIFNull(ModEntities.GRENADE_IF_NULL.get(), level, player));
        }
        else if (item == ModItems.NUCLEAR_WASTE_PEARL.get()) {
            level.addFreshEntity(new EntityWastePearl(ModEntities.WASTE_PEARL.get(), level, player));
        }
        else if (item == ModItems.STICK_DYNAMITE.get()) {
            level.addFreshEntity(new EntityGrenadeDynamite(ModEntities.GRENADE_DYNAMITE.get(), level, player));
        }
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        Item item = stack.getItem();

        if (item == ModItems.GRENADE_SCHRABIDIUM.get() ||
                item == ModItems.GRENADE_ASCHRAB.get() ||
                item == ModItems.GRENADE_CLOUD.get()) {
            return Rarity.RARE;
        }

        if (item == ModItems.GRENADE_PLASMA.get() ||
                item == ModItems.GRENADE_ZOMG.get() ||
                item == ModItems.GRENADE_BLACK_HOLE.get() ||
                item == ModItems.GRENADE_PINK_CLOUD.get()) {
            return Rarity.EPIC;
        }

        if (item == ModItems.GRENADE_NUKE.get() ||
                item == ModItems.GRENADE_NUCLEAR.get() ||
                item == ModItems.GRENADE_TAU.get() ||
                item == ModItems.GRENADE_LEMON.get() ||
                item == ModItems.GRENADE_MK2.get() ||
                item == ModItems.GRENADE_PULSE.get() ||
                item == ModItems.GRENADE_GASCAN.get()) {
            return Rarity.UNCOMMON;
        }

        return Rarity.COMMON;
    }

    private String translateFuse() {
        if (fuse == -1) return "Impact";
        if (fuse == 0) return "Instant";
        return fuse + "s";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Fuse: " + translateFuse()));

        Item item = stack.getItem();

        if (item == ModItems.GRENADE_SMART.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"Why did it not blow up????\""));
            tooltip.add(Component.literal("If it didn't blow up it means it worked.").withStyle(ChatFormatting.ITALIC));
        }

        if (item == ModItems.GRENADE_IF_GENERIC.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"How do you like them apples?\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_HE.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"You better run, you better take cover!\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_BOUNCY.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"Boing!\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_STICKY.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"This one is the booger grenade.\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_IMPACT.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"Tossable boom.\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_INCENDIARY.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"Flaming wheel of destruction!\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_TOXIC.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"TOXIC SHOCK\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_CONCUSSION.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"Oof ouch owie, my bones!\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_BRIMSTONE.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"Zoop!\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_MYSTERY.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"It's a mystery!\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_SPARK.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"We can't rewind, we've gone too far.\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_HOPWIRE.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("\"All I ever wished for was a bike that didn't fall over.\"").withStyle(ChatFormatting.ITALIC));
        }
        if (item == ModItems.GRENADE_IF_NULL.get()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("java.lang.NullPointerException").withStyle(ChatFormatting.ITALIC));
        }
    }

    public static int getFuseTicks(ItemStack stack) {
        if (stack.getItem() instanceof ItemGrenade grenade) {
            return grenade.fuse * 20;
        }
        return 0;
    }
    public static int getFuseTicks(Item item) {
        if (item instanceof ItemGrenade grenade) {
            return grenade.fuse * 20;
        }
        return 0;
    }
}