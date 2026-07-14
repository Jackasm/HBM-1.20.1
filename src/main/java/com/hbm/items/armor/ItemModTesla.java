package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import com.hbm.render.item.armor.BackTeslaItemRenderer;
import com.hbm.render.model.ModelBackTesla;
import com.hbm.tileentity.machine.TileEntityTesla;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemModTesla extends ItemArmorMod {

    private List<double[]> targets = new ArrayList<>();

    @OnlyIn(Dist.CLIENT)
    private ModelBackTesla modelTesla;

    public ItemModTesla(Properties properties) {
        super(ArmorModHandler.PLATE_ONLY, false, true, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Zaps nearby entities (requires full electric set)"));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Player player)) return;
        if (!ArmorFSB.hasFSBArmorIgnoreCharge(player)) return;
        if (!(armor.getItem() instanceof ArmorFSBPowered)) return;

        double x = entity.getX();
        double y = entity.getY() + 1.25;
        double z = entity.getZ();

        this.targets = TileEntityTesla.zap(entity.level(), x, y, z, 5, entity);

        if (this.targets != null && !this.targets.isEmpty() && entity.getRandom().nextInt(5) == 0) {
            armor.hurtAndBreak(1, entity, p -> {});
        }
    }

    public List<double[]> getTargets() {
        return targets;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                if (slot == EquipmentSlot.CHEST) {
                    return getArmorModel(living);
                }
                return defaultModel;
            }

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new BackTeslaItemRenderer();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public ModelBackTesla getArmorModel(LivingEntity entity) {
        if (modelTesla == null) {
            Minecraft mc = Minecraft.getInstance();
            ModelPart root = mc.getEntityModels().bakeLayer(ModelBackTesla.LAYER_LOCATION);
            modelTesla = new ModelBackTesla(root);
        }
        modelTesla.setEntity(entity);
        modelTesla.young = entity.isBaby();
        return modelTesla;
    }
}