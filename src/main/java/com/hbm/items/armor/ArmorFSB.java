package com.hbm.items.armor;

import com.hbm.handler.ArmorResistanceHandler;
import com.hbm.handler.ArmorModHandler;
import com.hbm.render.model.CompositeArmorModel;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ArmorFSB extends ArmorItem implements IArmorDisableModel, IHelmetOverlay{
    private List<MobEffectInstance> effects = new ArrayList<>();
    private HashSet<EnumPlayerPart> hidden = new HashSet<>();
    private boolean needsFullSet = false;
    private boolean noHelmet = false;
    private boolean vats = false;
    private boolean thermal = false;
    private boolean geigerSound = false;
    public boolean customGeiger = false;
    private boolean hardLanding = false;
    public int dashCount = 0;
    private int stepSize = 0;
    private SoundEvent step;
    private SoundEvent jump;
    private SoundEvent fall;
    private String overlayPath = null;

    public ArmorFSB(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    // Билдер методы как в оригинале

    public ArmorFSB addEffect(MobEffectInstance effect) {
        effects.add(effect);
        return this;
    }

    public ArmorFSB setNoHelmet(boolean noHelmet) {
        this.noHelmet = noHelmet;
        return this;
    }

    public ArmorFSB enableVATS(boolean vats) {
        this.vats = vats;
        return this;
    }

    public ArmorFSB enableThermalSight(boolean thermal) {
        this.thermal = thermal;
        return this;
    }

    public ArmorFSB setHasGeigerSound(boolean geiger) {
        this.geigerSound = geiger;
        return this;
    }

    public ArmorFSB setHasCustomGeiger(boolean geiger) {
        this.customGeiger = geiger;
        return this;
    }

    public ArmorFSB setHasHardLanding(boolean hardLanding) {
        this.hardLanding = hardLanding;
        return this;
    }

    public ArmorFSB setDashCount(int dashCount) {
        this.dashCount = dashCount;
        return this;
    }

    public ArmorFSB setStepSize(int stepSize) {
        this.stepSize = stepSize;
        return this;
    }

    public ArmorFSB setStep(SoundEvent step) {
        this.step = step;
        return this;
    }

    public ArmorFSB setJump(SoundEvent jump) {
        this.jump = jump;
        return this;
    }

    public ArmorFSB setFall(SoundEvent fall) {
        this.fall = fall;
        return this;
    }

    public ArmorFSB setOverlay(String path) {
        this.overlayPath = path;
        return this;
    }

    public ArmorFSB setFullSetForHide() {
        needsFullSet = true;
        return this;
    }

    public ArmorFSB cloneStats(ArmorFSB original) {
        this.effects = original.effects;
        this.noHelmet = original.noHelmet;
        this.vats = original.vats;
        this.thermal = original.thermal;
        this.geigerSound = original.geigerSound;
        this.customGeiger = original.customGeiger;
        this.hardLanding = original.hardLanding;
        this.dashCount = original.dashCount;
        this.stepSize = original.stepSize;
        this.step = original.step;
        this.jump = original.jump;
        this.fall = original.fall;
        // overlay не копируется, т.к. специфичен для шлема
        return this;
    }

    public void handleTick(TickEvent.PlayerTickEvent event) {

        Player player = event.player;
        boolean step = true;

        if (ArmorFSB.hasFSBArmor(player)) {

            ItemStack plate = player.getInventory().armor.get(2);
            if (plate.getItem() instanceof ArmorFSB chestplate) {

                if (!chestplate.effects.isEmpty()) {
                    for (MobEffectInstance effect : chestplate.effects) {
                        player.addEffect(new MobEffectInstance(
                                effect.getEffect(),
                                effect.getDuration(),
                                effect.getAmplifier(),
                                true,
                                true
                        ));
                    }
                }

                if (step && chestplate.step != null && player.level().isClientSide && player.onGround()) {
                    steppy(player, chestplate.step);
                }
            }
        }
    }

    public static void steppy(Player player, SoundEvent sound) {

        try {
            // В 1.20.1 эти поля могут быть private, используем отражение
            Field nextStepDistance = ObfuscationReflectionHelper.findField(Entity.class, "nextStepDistance");
            Field distanceWalkedOnStepModified = ObfuscationReflectionHelper.findField(Entity.class, "distanceWalkedOnStepModified");

            if (player.getPersistentData().getFloat("hfr_nextStepDistance") == 0) {
                player.getPersistentData().putFloat("hfr_nextStepDistance", nextStepDistance.getFloat(player));
            }

            int px = Mth.floor(player.getX());
            int py = Mth.floor(player.getY() - 0.2D - player.getMyRidingOffset());
            int pz = Mth.floor(player.getZ());
            BlockState state = player.level().getBlockState(new BlockPos(px, py, pz));
            Block block = state.getBlock();

            if (!state.isAir() &&
                    player.getPersistentData().getFloat("hfr_nextStepDistance") <= distanceWalkedOnStepModified.getFloat(player)) {
                player.playSound(sound, 1.0F, 1.0F);
            }

            player.getPersistentData().putFloat("hfr_nextStepDistance", nextStepDistance.getFloat(player));

        } catch (Exception ignored) {
        }
    }

    // Геттеры

    public List<MobEffectInstance> getEffects() { return effects; }
    public boolean hasNoHelmet() { return noHelmet; }
    public boolean hasVATS() { return vats; }
    public boolean hasThermal() { return thermal; }
    public boolean hasGeigerSound() { return geigerSound; }
    public boolean hasCustomGeiger() { return customGeiger; }
    public boolean hasHardLanding() { return hardLanding; }
    public int getDashCount() { return dashCount; }
    public int getStepSize() { return stepSize; }
    public SoundEvent getStepSound() { return step; }
    public SoundEvent getJumpSound() { return jump; }
    public SoundEvent getFallSound() { return fall; }
    public String getOverlayPath() { return overlayPath; }
    public boolean isArmorEnabled(ItemStack stack) { return true; }
    @OnlyIn(Dist.CLIENT)
    public void handleOverlay(RenderGuiOverlayEvent.Pre event, Player player) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(GuiGraphics guiGraphics, ItemStack stack, Player player,
                                    int width, int height, float partialTicks) {
        if (overlayPath == null) return;

        // Просто рендерим текстуру на весь экран
        guiGraphics.blit(ResLocation(RefStrings.MODID, overlayPath), 0, 0, 0, 0, width, height, width, height);
    }

    public ArmorFSB hides(EnumPlayerPart... parts) {
        Collections.addAll(hidden, parts);
        return this;
    }

    @Override
    public boolean disablesPart(Player player, ItemStack stack, EnumPlayerPart part) {
        return hidden.contains(part) && (!needsFullSet || hasFSBArmorIgnoreCharge(player));
    }

    public static boolean hasFSBArmorIgnoreCharge(Player player) {
        ItemStack chestplate = player.getInventory().armor.get(2); // CHEST slot

        if (!chestplate.isEmpty() && chestplate.getItem() instanceof ArmorFSB chestItem) {
            boolean noHelmet = chestItem.hasNoHelmet();

            int armorCount = noHelmet ? 3 : 4;
            for (int i = 0; i < armorCount; i++) {
                ItemStack armor = player.getInventory().armor.get(i);

                if (armor.isEmpty() || !(armor.getItem() instanceof ArmorFSB armorItem))
                    return false;

                if (armorItem.getMaterial() != chestItem.getMaterial())
                    return false;
            }

            return true;
        }

        return false;
    }

    public static boolean hasFSBArmor(Player player) {

        ItemStack plate = player.getInventory().armor.get(2); // CHEST slot

        if (!plate.isEmpty() && plate.getItem() instanceof ArmorFSB chestplate) {

            boolean noHelmet = chestplate.noHelmet;

            int armorCount = noHelmet ? 3 : 4;
            for (int i = 0; i < armorCount; i++) {

                ItemStack armor = player.getInventory().armor.get(i);

                if (armor.isEmpty() || !(armor.getItem() instanceof ArmorFSB armorItem))
                    return false;

                if (armorItem.getMaterial() != chestplate.getMaterial())
                    return false;

                if (!armorItem.isArmorEnabled(armor))
                    return false;
            }

            return true;
        }

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        ArmorResistanceHandler.ResistanceStats setStats = ArmorResistanceHandler.getSetResistance(stack);
        boolean hasSetBonus = setStats.hasResistance();

        boolean hasSpecialAbilities = vats || thermal || geigerSound || customGeiger ||
                hardLanding || dashCount > 0 || stepSize > 0 || !effects.isEmpty();

        if (hasSetBonus || hasSpecialAbilities) {
            tooltip.add(Component.literal(""));

            boolean showDetails = flag.isAdvanced() || Screen.hasShiftDown();

            if (showDetails) {
                tooltip.add(Component.translatable("armor.tooltip.fullSetBonus")
                        .withStyle(ChatFormatting.GOLD));

                if (hasSetBonus) {
                    tooltip.add(Component.translatable("damage.set.bonus").withStyle(ChatFormatting.DARK_GREEN));
                    tooltip.add(Component.translatable("damage.dt.format", String.format("    " + "%.1f", setStats.threshold)));

                    if (setStats.reduction > 0) {
                        float drPercent = setStats.reduction * 100;
                        tooltip.add(Component.translatable("damage.dr.format", String.format("    " + "%.0f%%", drPercent)));
                    }
                }

                if (!effects.isEmpty()) {
                    tooltip.add(Component.translatable("armor.tooltip.effects")
                            .withStyle(ChatFormatting.DARK_PURPLE));
                    for (MobEffectInstance effect : effects) {
                        tooltip.add(Component.literal("    " + ChatFormatting.WHITE +
                                Component.translatable(effect.getDescriptionId()).getString() +
                                " " + (effect.getAmplifier() + 1)));
                    }
                }

                if (vats) {
                    tooltip.add(Component.translatable("armor.tooltip.vats")
                            .withStyle(ChatFormatting.AQUA));
                }

                if (geigerSound) {
                    tooltip.add(Component.translatable("armor.tooltip.geigerSound")
                            .withStyle(ChatFormatting.GREEN));
                }

                if (thermal) {
                    tooltip.add(Component.translatable("armor.tooltip.thermal")
                            .withStyle(ChatFormatting.RED));
                }

                if (hardLanding) {
                    tooltip.add(Component.translatable("armor.tooltip.hardLanding")
                            .withStyle(ChatFormatting.RED));
                }

                if (dashCount > 0) {
                    tooltip.add(Component.translatable("armor.tooltip.dash", dashCount)
                            .withStyle(ChatFormatting.AQUA));
                }

                if (stepSize > 0) {
                    tooltip.add(Component.translatable("armor.tooltip.stepHeight", stepSize)
                            .withStyle(ChatFormatting.AQUA));
                }

            } else {
                tooltip.add(Component.translatable("armor.tooltip.hasSetBonus")
                        .withStyle(ChatFormatting.GOLD));

                ArmorResistanceHandler.addSetInfo(stack, tooltip);

                tooltip.add(Component.translatable("armor.tooltip.showDetails")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (slot == EquipmentSlot.HEAD) {
                    HumanoidModel<?> baseModel = defaultModel;
                    CompositeArmorModel compositeModel = new CompositeArmorModel(baseModel);

                    // Проверяем все слоты модов на наличие IRenderableMod
                    for (int i = 0; i < ArmorModHandler.MOD_SLOTS; i++) {
                        ItemStack mod = ArmorModHandler.pryMod(stack, i);
                        if (!mod.isEmpty() && mod.getItem() instanceof IRenderableMod renderableMod) {
                            HumanoidModel<?> modModel = renderableMod.getRenderModel(mod, living);
                            ResourceLocation texture = renderableMod.getRenderTexture(mod, living);
                            if (modModel != null && texture != null) {
                                compositeModel.addLayer(modModel, texture);
                            }
                        }
                    }

                    return compositeModel;
                }
                return defaultModel;
            }
        });
    }
}