package com.hbm.inventory.gui;

import com.hbm.items.ModItems;

import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.ItemBobmazonPacket;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUIScreenBobmazon extends Screen {

    protected static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/book/gui_bobmazon.png");
    protected int imageWidth = 176 + 41;
    protected int imageHeight = 229;
    protected int leftPos;
    protected int topPos;
    int currentPage = 0;
    List<Offer> offers = new ArrayList<>();
    List<FolderButton> buttons = new ArrayList<>();
    private final Player player;

    public GUIScreenBobmazon(Player player, List<Offer> offers) {
        super(Component.translatable("gui.bobmazon"));
        this.player = player;
        this.offers = offers;
    }

    int getPageCount() {
        return (int) Math.ceil((offers.size() - 1) / 3);
    }

    @Override
    public void tick() {
        if (currentPage < 0) currentPage = 0;
        if (currentPage > getPageCount()) currentPage = getPageCount();

        if (this.player.getMainHandItem() != null && this.player.getMainHandItem().getItem() == ModItems.BOBMAZON_HIDDEN.get() &&
                this.player.getName().getString().equals("SolsticeUnlimitd")) {
            this.onClose();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        this.renderBg(graphics, partialTicks, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderLabels(graphics, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        updateButtons();
    }

    protected void updateButtons() {
        if (!buttons.isEmpty()) buttons.clear();

        for (int i = currentPage * 3; i < Math.min(currentPage * 3 + 3, offers.size()); i++) {
            buttons.add(new FolderButton(leftPos + 34, topPos + 35 + (54 * (int) Math.floor(i)) - currentPage * 3 * 54, offers.get(i)));
        }

        if (currentPage != 0) {
            buttons.add(new FolderButton(leftPos + 25 - 18, topPos + 26 + (27 * 3), 1, "Previous"));
        }
        if (currentPage != getPageCount()) {
            buttons.add(new FolderButton(leftPos + 25 + (27 * 4) + 18 + 41, topPos + 26 + (27 * 3), 2, "Next"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        try {
            for (FolderButton b : buttons) {
                if (b.isMouseOnButton((int) mouseX, (int) mouseY)) {
                    b.executeAction();
                    return true;
                }
            }
        } catch (Exception ex) {
            updateButtons();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String page = (currentPage + 1) + "/" + (getPageCount() + 1);
        graphics.drawString(font, page, leftPos + this.imageWidth / 2 - font.width(page) / 2, topPos + 205, 0x404040, false);

        for (FolderButton b : buttons) {
            if (b.isMouseOnButton(mouseX, mouseY)) {
                b.drawString(graphics, mouseX, mouseY);
            }
        }
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        for (FolderButton b : buttons) {
            b.drawButton(graphics, b.isMouseOnButton(mouseX, mouseY));
        }
        for (FolderButton b : buttons) {
            b.drawIcon(graphics, b.isMouseOnButton(mouseX, mouseY));
        }

        for (int d = currentPage * 3; d < Math.min(currentPage * 3 + 3, offers.size()); d++) {
            offers.get(d).drawRequirement(graphics, this, leftPos + 34, topPos + 53 + (54 * (int) Math.floor(d)) - currentPage * 3 * 54);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    class FolderButton {

        int xPos;
        int yPos;
        int type;
        String info;
        Offer offer;

        public FolderButton(int x, int y, int t, String i) {
            xPos = x;
            yPos = y;
            type = t;
            info = i;
        }

        public FolderButton(int x, int y, Offer offer) {
            xPos = x;
            yPos = y;
            type = 0;
            this.offer = offer;
        }

        public boolean isMouseOnButton(int mouseX, int mouseY) {
            return xPos <= mouseX && xPos + 18 > mouseX && yPos < mouseY && yPos + 18 >= mouseY;
        }

        public void drawButton(GuiGraphics graphics, boolean hovered) {
            graphics.blit(TEXTURE, xPos, yPos, hovered ? 176 + 41 + 18 : 176 + 41, type == 1 ? 18 : (type == 2 ? 36 : 0), 18, 18);
        }

        public void drawIcon(GuiGraphics graphics, boolean hovered) {
            if (offer != null) {
                graphics.renderFakeItem(offer.offer, xPos + 1, yPos + 1);
            }
        }

        public void drawString(GuiGraphics graphics, int x, int y) {
            if (info == null || info.isEmpty()) return;
            graphics.renderTooltip(font, Component.literal(info), x, y);
        }

        public void executeAction() {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (type == 0) {
                PacketDispatcher.sendToServer(new ItemBobmazonPacket(player, offer));
            } else if (type == 1) {
                if (currentPage > 0) currentPage--;
                updateButtons();
            } else if (type == 2) {
                if (currentPage < getPageCount()) currentPage++;
                updateButtons();
            }
        }
    }

    public static class Offer {

        public ItemStack offer;
        public Requirement requirement;
        public int cost;
        public int rating;
        public String comment;
        public String author;

        public Offer(ItemStack offer, Requirement requirement, int cost, int rating, String comment, String author) {
            this.offer = offer;
            this.requirement = requirement;
            this.cost = cost;
            this.rating = rating * 4 - 1;
            this.comment = comment;
            this.author = author;
        }

        public Offer(ItemStack offer, Requirement requirement, int cost) {
            this(offer, requirement, cost, 0);
        }

        public Offer(ItemStack offer, Requirement requirement, int cost, int rating) {
            this(offer, requirement, cost, rating, "No Ratings", "");
        }

        public void drawRequirement(GuiGraphics graphics, GUIScreenBobmazon gui, int x, int y) {
            graphics.blit(TEXTURE, x + 19, y - 4, 176 + 41, 62, 39, 8);
            graphics.blit(TEXTURE, x + 19, y - 4, 176 + 41, 54, rating, 8);

            String count = "";
            if (offer.getCount() > 1) count = " x" + offer.getCount();

            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            float scale = 0.5F;
            poseStack.scale(scale, scale, scale);
            graphics.drawString(gui.font, offer.getHoverName().getString() + count, (int) ((x + 20) / scale), (int) ((y - 12) / scale), 0x404040, false);
            poseStack.popPose();

            String price = cost + " Cap";
            if (cost != 1) price += "s";
            graphics.drawString(gui.font, price, x + 62, y - 3, 0x404040, false);

            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);

            if (!author.isEmpty()) {
                graphics.drawString(gui.font, "- " + author, (x + 20) * 2, (y + 18) * 2, 0x222222, false);
            }
            graphics.drawString(gui.font, comment, (x + 20) * 2, (y + 8) * 2, 0x222222, false);

            poseStack.popPose();

            if (offer != null) {
                graphics.renderFakeItem(requirement.icon, x + 1, y + 1);
            }
        }
    }

    public enum Requirement {
        NONE(null, Items.CHEST),
        STEEL("hbm:blast_furnace", ModItems.INGOT_STEEL.get()),
        ASSEMBLY("hbm:assembly", ModItems.CIRCUIT_ADVANCED.get()),
        CHEMICS("hbm:chemplant", ModItems.NOTHING.get()),
        OIL("hbm:desh", ModItems.NOTHING.get()),
        NUCLEAR("hbm:technetium", ModItems.INGOT_U235.get()),
        HIDDEN("hbm:bob_hidden", ModItems.NOTHING.get());

        public final String advancementId;
        public final ItemStack icon;

        Requirement(String advancementId, Item icon) {
            this.advancementId = advancementId;
            this.icon = new ItemStack(icon);
        }

        public boolean fulfills(ServerPlayer player) {
            if (this == NONE) return true;
            if (advancementId == null) return true;

            Advancement advancement = player.server.getAdvancements().getAdvancement(
                    ResourceLocation.parse(advancementId)
            );
            if (advancement == null) return false;

            return player.getAdvancements().getOrStartProgress(advancement).isDone();
        }
    }
}