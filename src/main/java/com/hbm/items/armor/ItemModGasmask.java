package com.hbm.items.armor;

import com.hbm.api.item.IGasMask;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModArmorItems;
import com.hbm.render.model.ModelGasMask;
import com.hbm.render.model.ModelM65Small;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ArmorRegistry.HazardClass;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

public class ItemModGasmask extends ItemArmorMod implements IGasMask, IRenderableMod {

    @OnlyIn(Dist.CLIENT)
    private ModelM65Small modelGasMask;
    private final ResourceLocation tex = ResLocation("hbm", "textures/models/armor/model_m65.png");
    private final ResourceLocation tex_mono = ResLocation("hbm", "textures/models/armor/model_m65_mono.png");

    public ItemModGasmask(Properties properties) {
        super(ArmorModHandler.HELMET_ONLY, true, false, false, false, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Gas protection").withStyle(ChatFormatting.GREEN));

        tooltip.add(Component.literal(""));
        super.appendHoverText(stack, level, tooltip, flag);

        Player player = Minecraft.getInstance().player;
        ArmorUtil.addGasMaskTooltip(stack, player, tooltip, flag.isAdvanced());

        List<HazardClass> haz = getBlacklist(stack, null);

        if (!haz.isEmpty()) {
            tooltip.add(Component.literal("Will never protect against:").withStyle(ChatFormatting.RED));

            for (HazardClass clazz : haz) {
                tooltip.add(Component.literal(" -")
                        .append(Component.translatable("hazard." + clazz.name().toLowerCase()))
                        .withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.literal("  " + stack.getHoverName().getString() + " (gas protection)")
                .withStyle(ChatFormatting.GREEN));
        Player player = Minecraft.getInstance().player;
        ArmorUtil.addGasMaskTooltip(stack, player, list, false);
    }


    private float wrapAngleTo180(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    @Override
    public ArrayList<HazardClass> getBlacklist(ItemStack stack, LivingEntity entity) {
        if (this == ModArmorItems.ATTACHMENT_MASK_MONO.get()) {
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel<?> getRenderModel(ItemStack mod, LivingEntity entity) {
        if (modelGasMask == null) {
            Minecraft mc = Minecraft.getInstance();
            ModelPart root = mc.getEntityModels().bakeLayer(ModelM65Small.M65Small);
            modelGasMask = new ModelM65Small(root);
        }
        modelGasMask.young = entity.isBaby();
        modelGasMask.setEntity(entity);
        return modelGasMask;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getRenderTexture(ItemStack mod, LivingEntity entity) {
        if (this == ModArmorItems.ATTACHMENT_MASK.get()) {
            return tex;
        } else if (this == ModArmorItems.ATTACHMENT_MASK_MONO.get()) {
            return tex_mono;
        }
        return null;
    }
}