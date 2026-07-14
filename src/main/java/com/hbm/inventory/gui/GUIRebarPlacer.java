package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerRebarPlacer;
import com.hbm.items.tool.ItemRebarPlacer;
import com.hbm.items.tool.ItemRebarPlacer.InventoryRebarPlacer;

import com.hbm.util.RefStrings;
import com.hbm.util.Tuple.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIRebarPlacer extends AbstractContainerScreen<ContainerRebarPlacer> {

    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/gui_rebar.png");
    private final InventoryRebarPlacer inventory;

    public GUIRebarPlacer(ContainerRebarPlacer container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        this.inventory = container.getInventory();
        this.imageWidth = 176;
        this.imageHeight = 182;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        // Тултип для слота с бетоном
        if (this.isHovering(53, 36, 18, 18, mouseX, mouseY) && !this.menu.getSlot(0).hasItem()) {
            List<ItemStack> list = new ArrayList<>();
            for (Pair<Block, Integer> conk : ItemRebarPlacer.acceptableConk) {
                ItemStack stack = new ItemStack(conk.key(), 1);
                stack.setDamageValue(conk.value());
                list.add(stack);
            }

            ItemStack selected = list.get(0);
            if (list.size() > 1) {
                int cycle = (int) ((System.currentTimeMillis() % (1000 * list.size())) / 1000);
                selected = list.get(cycle).copy();
                selected.setCount(0);
                list.set(cycle, selected);
            }

            List<Component> lines = new ArrayList<>();
            if (list.size() < 10) {
                for (ItemStack stack : list) {
                    lines.add(stack.getHoverName());
                }
            } else {
                // Просто показываем название выбранного
                lines.add(Component.literal(selected.getHoverName().getString()));
            }

            graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        String name = inventory.hasCustomName() ?
                inventory.target.getHoverName().getString() :
                inventory.getDisplayName().getString();

        graphics.drawString(font, name, this.imageWidth / 2 - font.width(name) / 2, 6, 0x404040, false);
        graphics.drawString(font, Component.translatable("container.inventory"), 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        ItemStack concrete = inventory.getItem(0);
        boolean hasValidConk = concrete != null && !concrete.isEmpty() &&
                ItemRebarPlacer.isValidConk(concrete.getItem(), concrete.getDamageValue());

        if (!hasValidConk) {
            graphics.blit(TEXTURE, leftPos + 87, topPos + 17, 176, 0, 56, 56);
        }
    }

    protected boolean isHovering(int x, int y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + w &&
                mouseY >= topPos + y && mouseY < topPos + y + h;
    }
}