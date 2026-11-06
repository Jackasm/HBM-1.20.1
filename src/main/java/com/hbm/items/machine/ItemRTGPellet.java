package com.hbm.items.machine;

import com.hbm.config.VersatileConfig;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPelletDepleted.DepletedRTGMaterial;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ItemRTGPellet extends Item implements ITooltipProvider {

    private final short heat;
    private boolean doesDecay = false;
    private Supplier<ItemStack> decaySupplier = null;
    private long lifespan = 0;

    public static final List<ItemRTGPellet> pelletList = new ArrayList<>();

    public ItemRTGPellet(int heatIn, Properties properties) {
        super(properties.stacksTo(1));
        this.heat = (short) heatIn;
        pelletList.add(this);
    }

    public ItemRTGPellet setDecays(DepletedRTGMaterial mat, long life) {
        this.doesDecay = true;
        this.decaySupplier = () -> {
            ItemStack stack = new ItemStack(ModItems.PELLET_RTG_DEPLETED.get(), 1);
            stack.getOrCreateTag().putInt("CustomModelData", mat.ordinal());
            return stack;
        };
        this.lifespan = life;
        return this;
    }

    public long getMaxLifespan() { return lifespan; }
    public short getHeat() { return heat; }

    @Nullable
    public ItemStack getDecayItem() {
        return decaySupplier == null ? null : decaySupplier.get();
    }

    public boolean getDoesDecay() { return doesDecay; }

    public static ItemStack handleDecay(ItemStack stack, ItemRTGPellet instance) {
        if (instance.getDoesDecay() && VersatileConfig.rtgDecay()) {
            if (instance.getLifespan(stack) <= 0) return instance.getDecayItem();
            else instance.decay(stack);
        }
        return stack;
    }

    public void decay(ItemStack stack) {
        if (stack.getItem() instanceof ItemRTGPellet pellet) {
            if (!pellet.getDoesDecay()) return;
            CompoundTag tag = stack.getOrCreateTag();
            tag.putLong("PELLET_DEPLETION", getLifespan(stack) - 1);
        }
    }

    public long getLifespan(ItemStack stack) {
        if (stack.getItem() instanceof ItemRTGPellet) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("PELLET_DEPLETION")) {
                tag.putLong("PELLET_DEPLETION", getMaxLifespan());
            }
            return tag.getLong("PELLET_DEPLETION");
        }
        return 0;
    }

    public static short getScaledPower(ItemRTGPellet fuel, ItemStack stack) {
        return (short) Math.ceil(fuel.getHeat() * ((double) fuel.getLifespan(stack) / (double) fuel.getMaxLifespan()));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && this == ModItems.PELLET_RTG.get()) {
            String fact = level.random.nextInt(RTG_FACTS.length) == 0 ? "" : RTG_FACTS[level.random.nextInt(RTG_FACTS.length)];
            player.displayClientMessage(
                    Component.translatable(RTG_FACTS[level.random.nextInt(RTG_FACTS.length)])
                            .withStyle(ChatFormatting.YELLOW), false);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getDoesDecay() && getLifespan(stack) != getMaxLifespan();
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13.0F * ((float) getLifespan(stack) / (float) getMaxLifespan()));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00FF00;
    }

    // ITooltipProvider

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        List<String> keys = new ArrayList<>();
        String base = stack.getItem().getDescriptionId();
        keys.add(base + ".desc");

        boolean scaled = getDoesDecay() && VersatileConfig.scaleRTGPower();
        keys.add("desc.item.rtgHeat=" + (scaled ? getScaledPower(this, stack) : getHeat()));

        if (getDoesDecay()) {
            ItemStack decay = getDecayItem();
            if (decay != null) {
                keys.add("desc.item.rtgDecay=" + Component.translatable(decay.getDescriptionId()).getString()
                        + ":" + decay.getCount());
            }
            keys.add("desc.item.rtgLifespan=" + BobMathUtil.toPercentage(getLifespan(stack), getMaxLifespan()));
        }
        return keys.toArray(new String[0]);
    }

    public String getData() {
        return String.format(Locale.US, "%s (%s HE/t)%s",
                Component.translatable(getDescriptionId() + ".name").getString(),
                getHeat(),
                getDoesDecay() ? " (decays)" : "");
    }

    public static HashMap<ItemStack, ItemStack> getRecipeMap() {
        HashMap<ItemStack, ItemStack> map = new HashMap<>();
        for (ItemRTGPellet pellet : pelletList) {
            map.put(new ItemStack(pellet), pellet.getDecayItem());
        }
        return map;
    }

    @Override
    public void addTooltip(ItemStack stack, List<Component> tooltip, boolean showDetails) {
        tooltip.add(Component.translatable(getDescriptionId() + ".desc"));

        boolean scaled = getDoesDecay() && VersatileConfig.scaleRTGPower();
        tooltip.add(Component.translatable("desc.item.rtgHeat",
                scaled ? getScaledPower(this, stack) : getHeat()).withStyle(ChatFormatting.YELLOW));

        if (getDoesDecay()) {
            ItemStack decay = getDecayItem();
            if (decay != null) {
                tooltip.add(Component.translatable("desc.item.rtgDecay",
                        Component.translatable(decay.getDescriptionId()),
                        decay.getCount()).withStyle(ChatFormatting.GRAY));
            }
            tooltip.add(Component.literal(BobMathUtil.toPercentage(getLifespan(stack), getMaxLifespan()))
                    .withStyle(ChatFormatting.GRAY));

            if (showDetails) {
                tooltip.add(Component.translatable("desc.item.rtgExtended").withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.literal(String.format(Locale.US, "%s / %s ticks", getLifespan(stack), getMaxLifespan()))
                        .withStyle(ChatFormatting.DARK_GRAY));

                String[] timeLeft = BobMathUtil.ticksToDate(getLifespan(stack));
                String[] maxLife = BobMathUtil.ticksToDate(getMaxLifespan());
                tooltip.add(Component.translatable("desc.item.rtgTimeRemaining",
                        timeLeft[0], timeLeft[1], timeLeft[2]).withStyle(ChatFormatting.DARK_GRAY));
                tooltip.add(Component.translatable("desc.item.rtgMaxLife",
                        maxLife[0], maxLife[1], maxLife[2]).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    // Список фактов (ключи локализации)
    private static final String[] RTG_FACTS = new String[]{
            "rtg.fact.1", "rtg.fact.2", "rtg.fact.3", "rtg.fact.4", "rtg.fact.5",
            "rtg.fact.6", "rtg.fact.7", "rtg.fact.8", "rtg.fact.9", "rtg.fact.10",
            "rtg.fact.11", "rtg.fact.12", "rtg.fact.13", "rtg.fact.14", "rtg.fact.15",
            "rtg.fact.16"
    };
}