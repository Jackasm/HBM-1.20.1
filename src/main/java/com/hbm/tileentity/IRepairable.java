package com.hbm.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.items.tool.ItemBlowtorch;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

import java.util.ArrayList;
import java.util.List;

public interface IRepairable {

    boolean isDamaged();

    List<AStack> getRepairMaterials();

    void repair();

    static List<AStack> getRepairMaterials(Level level, BlockPos pos, BlockDummyable multiBlock, Player player) {

        ItemStack held = player.getMainHandItem();

        if (held.isEmpty() || !(held.getItem() instanceof ItemBlowtorch)) return null;

        BlockEntity core = level.getBlockEntity(pos);
        if (!(core instanceof IRepairable)) return null;

        IRepairable repairable = (IRepairable) core;

        if (!repairable.isDamaged()) return null;
        return repairable.getRepairMaterials();
    }

    static boolean tryRepairMultiblock(Level level, BlockPos pos, BlockDummyable dummy, Player player) {

        BlockEntity core = level.getBlockEntity(pos);
        if (!(core instanceof IRepairable)) return false;

        IRepairable repairable = (IRepairable) core;

        if (!repairable.isDamaged()) return false;

        List<AStack> list = repairable.getRepairMaterials();
        if (list == null || list.isEmpty() || InventoryUtil.doesPlayerHaveAStacks(player, list, true)) {
            if (!level.isClientSide) repairable.repair();
            return true;
        }

        return false;
    }

    @OnlyIn(Dist.CLIENT)
    static void addGenericOverlay(RenderGuiOverlayEvent.Pre event, Level level, BlockPos pos, BlockDummyable multiBlock) {

        List<AStack> materials = IRepairable.getRepairMaterials(level, pos, multiBlock, Minecraft.getInstance().player);

        if (materials == null) return;

        List<Component> text = new ArrayList<>();
        text.add(Component.literal(ChatFormatting.GOLD + "Repair with:"));

        for (AStack stack : materials) {
            try {
                ItemStack display = stack.extractForCyclingDisplay(20);
                text.add(Component.literal("- ").append(display.getHoverName())
                        .append(Component.literal(" x" + display.getCount())));
            } catch (Exception ex) {
                text.add(Component.literal(ChatFormatting.RED + "- ERROR"));
            }
        }

        ILookOverlay.printGeneric(event, Component.translatable(multiBlock.getDescriptionId()).getString(),
                0xffff00, 0x404000, text);
    }

    @OnlyIn(Dist.CLIENT)
    default boolean shouldRenderRepairOverlay(Level level, BlockPos pos, Player player) {
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || !(held.getItem() instanceof ItemBlowtorch)) return false;

        BlockEntity core = level.getBlockEntity(pos);
        if (!(core instanceof IRepairable repairable)) return false;

        return repairable.isDamaged() && repairable.getRepairMaterials() != null;
    }

    @OnlyIn(Dist.CLIENT)
    default void addOverlaySections(List<OverlaySection> sections, Level level, BlockPos pos, BlockDummyable multiBlock) {
        List<Component> text = new ArrayList<>();
        List<AStack> materials = IRepairable.getRepairMaterials(level, pos, multiBlock, Minecraft.getInstance().player);

        if (materials != null) {
            text.add(Component.literal(ChatFormatting.GOLD + "Repair with:"));
            for (AStack stack : materials) {
                try {
                    ItemStack display = stack.extractForCyclingDisplay(20);
                    text.add(Component.literal("- ").append(display.getHoverName())
                            .append(Component.literal(" x" + display.getCount())));
                } catch (Exception ex) {
                    text.add(Component.literal(ChatFormatting.RED + "- ERROR"));
                }
            }

            OverlaySection section = new OverlaySection(OverlaySection.Type.TILE_ENTITY);
            section.setIcon(new ItemStack(multiBlock));
            for (Component line : text) {
                section.addLine(line);
            }
            sections.add(section);
        }
    }

    void tryExtinguish(Level level, BlockPos pos, EnumExtinguishType type);

    enum EnumExtinguishType {
        WATER,
        FOAM,
        SAND,
        CO2
    }
}