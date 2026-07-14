package com.hbm.items.armor;

import com.hbm.items.ModArmorItems;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

public class ArmorHazmat extends ArmorFSB {

    private String overlayPath;

    public ArmorHazmat(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    public ArmorHazmat setOverlay(String path) {
        this.overlayPath = path;
        return this;
    }

    public String getOverlayPath() {
        return overlayPath;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHelmetOverlay(GuiGraphics guiGraphics, ItemStack stack, Player player,
                                    int width, int height, float partialTicks) {
        if (overlayPath != null && this.getType() == Type.HELMET) {
            ResourceLocation tex = ResLocation(overlayPath);
            guiGraphics.blit(tex, 0, 0, 0, 0, width, height, width, height);
        }
    }

    @Override
    public @NotNull String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        // Определяем цвет
        String color = "";
        Item item = stack.getItem();
        if (item == ModArmorItems.HAZMAT_HELMET_RED.get() ||
                item == ModArmorItems.HAZMAT_CHESTPLATE_RED.get() ||
                item == ModArmorItems.HAZMAT_LEGGINGS_RED.get() ||
                item == ModArmorItems.HAZMAT_BOOTS_RED.get()) {
            color = "_red";
        } else if (item == ModArmorItems.HAZMAT_HELMET_GREY.get() ||
                item == ModArmorItems.HAZMAT_CHESTPLATE_GREY.get() ||
                item == ModArmorItems.HAZMAT_LEGGINGS_GREY.get() ||
                item == ModArmorItems.HAZMAT_BOOTS_GREY.get()) {
            color = "_grey";
        }

        // Определяем слой текстуры
        String layer = (this.getType() == Type.LEGGINGS) ? "_2.png" : "_1.png";

        return RefStrings.MODID + ":textures/models/armor/hazmat" + color + "_layer" + layer;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
    }
}