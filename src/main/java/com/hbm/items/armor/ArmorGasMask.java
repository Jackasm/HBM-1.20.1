package com.hbm.items.armor;

import com.hbm.api.item.IGasMask;
import com.hbm.items.ModArmorItems;

import com.hbm.render.model.ModelGasMask;
import com.hbm.render.model.ModelM65Small;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ArmorRegistry.HazardClass;

import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ArmorGasMask extends ArmorItem implements IGasMask, IHelmetOverlay {

    private final ResourceLocation[] googleBlur = new ResourceLocation[]{
            ResLocation(RefStrings.MODID, "textures/misc/overlay_goggles_0.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_goggles_1.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_goggles_2.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_goggles_3.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_goggles_4.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_goggles_5.png")
    };

    private final ResourceLocation[] maskBlur = new ResourceLocation[]{
            ResLocation(RefStrings.MODID, "textures/misc/overlay_gasmask_0.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_gasmask_1.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_gasmask_2.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_gasmask_3.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_gasmask_4.png"),
            ResLocation(RefStrings.MODID, "textures/misc/overlay_gasmask_5.png")
    };

    public ArmorGasMask(Properties properties) {
        super(ArmorMaterials.IRON, Type.HELMET, properties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ModArmorItems.GAS_MASK.get()) {
            return RefStrings.MODID + ":textures/models/armor/gas_mask.png";
        }
        if (stack.getItem() == ModArmorItems.GAS_MASK_M65.get()) {
            return RefStrings.MODID + ":textures/models/armor/model_m65.png";
        }
        if (stack.getItem() == ModArmorItems.GAS_MASK_OLDE.get()) {
            return RefStrings.MODID + ":textures/models/armor/mask_olde.png";
        }
        if (stack.getItem() == ModArmorItems.GAS_MASK_MONO.get()) {
            return RefStrings.MODID + ":textures/models/armor/model_m65_mono.png";
        }
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(GuiGraphics guiGraphics, ItemStack stack, Player player, int width, int height, float partialTicks) {
        ResourceLocation tex = null;

        if (this == ModArmorItems.GOGGLES.get() || this == ModArmorItems.GAS_MASK_M65.get()) {
            int index = (int) ((double) stack.getDamageValue() / (double) stack.getMaxDamage() * 6D);
            tex = this.googleBlur[Math.min(index, 5)];
        }

        if (this == ModArmorItems.GAS_MASK.get()) {
            int index = (int) ((double) stack.getDamageValue() / (double) stack.getMaxDamage() * 6D);
            tex = this.maskBlur[Math.min(index, 5)];
        }

        if (tex == null) return;

        // Рендерим оверлей
        guiGraphics.blit(tex, 0, 0, 0, 0, width, height, width, height);
    }

    @Override
    public ArrayList<ArmorRegistry.HazardClass> getBlacklist(ItemStack stack, LivingEntity entity) {
        if (this == ModArmorItems.GAS_MASK_MONO.get()) {
            return new ArrayList<>(Arrays.asList(
                    HazardClass.GAS_LUNG,
                    HazardClass.GAS_BLISTERING,
                    HazardClass.BACTERIA
            ));
        } else {
            return new ArrayList<>(Arrays.asList(HazardClass.GAS_BLISTERING));
        }
    }

    @Override
    public ItemStack getFilter(ItemStack stack, LivingEntity entity) {
        return ArmorUtil.getGasMaskFilter(stack);
    }

    @Override
    public void installFilter(ItemStack stack, LivingEntity entity, ItemStack filter) {
        ArmorUtil.installGasMaskFilter(stack, filter);
    }

    @Override
    public void damageFilter(ItemStack stack, LivingEntity entity, int damage) {
        ArmorUtil.damageGasMaskFilter(stack, damage);
    }

    @Override
    public boolean isFilterApplicable(ItemStack stack, LivingEntity entity, ItemStack filter) {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            ItemStack filter = this.getFilter(stack, player);

            if (!filter.isEmpty()) {
                ArmorUtil.removeFilter(stack);

                if (!player.getInventory().add(filter)) {
                    player.drop(filter, true);
                }

                return InteractionResultHolder.success(stack);
            }
        }

        return super.use(level, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (level != null && level.isClientSide) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ArmorUtil.addGasMaskTooltip(stack, player, tooltip, flag.isAdvanced());
            }
        }

        List<HazardClass> haz = getBlacklist(stack, null);

        if (!haz.isEmpty()) {
            tooltip.add(Component.translatable("hazard.neverProtects")
                    .withStyle(ChatFormatting.RED));

            for (HazardClass clazz : haz) {
                tooltip.add(Component.literal(" -")
                        .append(Component.translatable("hazard." + clazz.name().toLowerCase()))
                        .withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (slot == EquipmentSlot.HEAD) {
                    Minecraft mc = Minecraft.getInstance();

                    // Определяем какая модель нужна
                    if (stack.getItem() == ModArmorItems.GAS_MASK.get()) {
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelGasMask.GAS_MASK);
                        ModelGasMask model = new ModelGasMask(root);
                        model.young = living.isBaby();
                        model.setEntity(living);
                        return model;
                    }
                    else if (stack.getItem() == ModArmorItems.GAS_MASK_M65.get() ||
                            stack.getItem() == ModArmorItems.GAS_MASK_OLDE.get() ||
                            stack.getItem() == ModArmorItems.GAS_MASK_MONO.get()) {
                        ModelPart root = mc.getEntityModels().bakeLayer(ModelM65Small.M65Small);
                        ModelM65Small model = new ModelM65Small(root);
                        model.young = living.isBaby();
                        model.setEntity(living);
                        return model;
                    }
                }
                return defaultModel;
            }
        });
    }
}