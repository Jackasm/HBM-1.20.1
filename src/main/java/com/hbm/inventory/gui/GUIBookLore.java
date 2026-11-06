package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerBookLore;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIBookLore extends AbstractContainerScreen<ContainerBookLore> {

    private static final ResourceLocation TEXTURE =
            ResLocation(RefStrings.MODID, "textures/gui/book/book_lore.png");

    protected static final int SIZE_X = 272;
    protected static final int SIZE_Y = 182;

    protected String key;
    protected CompoundTag tag;
    protected int color;
    protected int page;
    protected int maxPage;

    public GUIBookLore(ContainerBookLore container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = SIZE_X;
        this.imageHeight = SIZE_Y;

        ItemStack stack = container.getBook();
        CompoundTag tag = stack.getTag();
        if (tag == null) return;
        this.tag = tag;
        this.key = tag.getString("k");
        if (key.isEmpty()) return;

        this.color = tag.getInt("cov_col");
        if (color <= 0) color = 0x303030;
        this.maxPage = (int) Math.ceil(tag.getInt("p") / 2.0) - 1;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (key == null || key.isEmpty()) return;

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();

        float r = (color >> 16 & 255) / 255F;
        float g = (color >> 8 & 255) / 255F;
        float b = (color & 255) / 255F;
        RenderSystem.setShaderColor(r, g, b, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, SIZE_X, SIZE_Y, 512, 512);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.blit(TEXTURE, leftPos + 7, topPos + 7, 0, 182, 258, 165, 512, 512);

        RenderSystem.disableBlend();

        boolean overY = mouseY >= topPos + 155 && mouseY < topPos + 165;

        if (page > 0) {
            int u = (overY && mouseX >= leftPos + 24 && mouseX <= leftPos + 42) ? 295 : 272;
            graphics.blit(TEXTURE, leftPos + 24, topPos + 155, u, 13, 18, 10, 512, 512);
        }
        if (page < maxPage) {
            int u = (overY && mouseX >= leftPos + 230 && mouseX <= leftPos + 248) ? 295 : 272;
            graphics.blit(TEXTURE, leftPos + 230, topPos + 155, u, 0, 18, 10, 512, 512);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        if (tag == null || key == null || key.isEmpty()) return;

        String k = "book_lore." + key + ".page.";

        for (int i = 0; i < 2; i++) {
            int defacto = this.page * 2 + i;
            if (defacto >= tag.getInt("p")) continue;

            Component text;
            CompoundTag argTag = tag.getCompound("p" + defacto);

            if (argTag.isEmpty()) {
                text = Component.translatable(k + defacto);
            } else {
                List<Object> args = new ArrayList<>();
                int index = 1;
                String arg = argTag.getString("a1");
                while (!arg.isEmpty()) {
                    args.add(arg);
                    index++;
                    arg = argTag.getString("a" + index);
                }
                text = Component.translatable(k + defacto, args.toArray());
            }

            List<String> lines = wrap(text.getString(), 100, " ", "$");
            for (int l = 0; l < lines.size(); l++) {
                graphics.drawString(this.font, lines.get(l),
                        leftPos + 20 + i * 130,
                        topPos + 20 + 9 * l,
                        0x0F0F0F, false);
            }
        }
    }

    private List<String> wrap(String text, int maxWidth, String space, String newline) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(java.util.regex.Pattern.quote(space));
        if (words.length == 0) return lines;

        lines.add(words[0]);
        int indent = this.font.width(words[0]);

        for (int w = 1; w < words.length; w++) {
            if (words[w].equals(newline)) {
                if (w + 1 < words.length && !words[w + 1].equals(newline)) {
                    lines.add(words[++w]);
                    indent = this.font.width(words[w]);
                } else {
                    lines.add("");
                }
                continue;
            }
            int wordWidth = this.font.width(space + words[w]);
            if (indent + wordWidth <= maxWidth) {
                int last = lines.size() - 1;
                lines.set(last, lines.get(last) + space + words[w]);
                indent += wordWidth;
            } else {
                lines.add(words[w]);
                indent = this.font.width(words[w]);
            }
        }
        return lines;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (key != null && !key.isEmpty()) {
            if (mouseY >= topPos + 155 && mouseY < topPos + 165) {
                if (page > 0 && mouseX >= leftPos + 24 && mouseX <= leftPos + 42) {
                    page--;
                    Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
                if (page < maxPage && mouseX >= leftPos + 230 && mouseX <= leftPos + 248) {
                    page++;
                    Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 /* ESC */ ||
                (this.minecraft != null && this.minecraft.options.keyInventory.matches(keyCode, scanCode))) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}