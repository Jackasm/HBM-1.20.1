package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerTurretSentry;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.server.AuxButtonPacket;
import com.hbm.network.server.NBTControlPacket;
import com.hbm.tileentity.turret.TileEntityTurretSentry;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class GUITurretSentry extends AbstractContainerScreen<ContainerTurretSentry> {

    private static final ResourceLocation TEXTURE_BASE = ResLocation(RefStrings.MODID, "textures/gui/turrets/gui_turret_base.png");
    private static final ResourceLocation TEXTURE = ResLocation(RefStrings.MODID, "textures/gui/turrets/gui_turret_sentry.png");

    private final TileEntityTurretSentry turret;
    private EditBox nameField;
    private int whitelistIndex = 0;

    public GUITurretSentry(ContainerTurretSentry container, Inventory inv, Component title) {
        super(container, inv, title);
        this.turret = container.turret;
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    public void init() {
        super.init();
        this.nameField = new EditBox(this.font, leftPos + 10, topPos + 65, 50, 14, Component.literal(""));
        this.nameField.setTextColor(-1);
        this.nameField.setBordered(false);
        this.nameField.setMaxLength(25);
        this.setFocused(this.nameField);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        String text = this.nameField != null ? this.nameField.getValue() : "";
        this.init(minecraft, width, height);
        if (this.nameField != null) {
            this.nameField.setValue(text);
        }
    }

    @Override
    protected void containerTick() {
        if (this.nameField != null) {
            this.nameField.tick();
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

        if (this.nameField != null) {
            this.nameField.render(graphics, mouseX, mouseY, partialTicks);
        }

        // Энергия
        if (mouseX >= leftPos + 152 && mouseX <= leftPos + 168 && mouseY >= topPos + 45 && mouseY <= topPos + 97) {
            graphics.renderTooltip(font, Component.literal(turret.power + " / " + turret.getMaxPower() + " HE"), mouseX, mouseY);
        }

        // Кнопки настроек
        String on = Component.translatable("turret.on").getString();
        String off = Component.translatable("turret.off").getString();
        renderButtonTooltip(graphics, mouseX, mouseY, leftPos + 8, topPos + 30, 10, 10, "turret.players", turret.targetPlayers ? on : off);
        renderButtonTooltip(graphics, mouseX, mouseY, leftPos + 22, topPos + 30, 10, 10, "turret.animals", turret.targetAnimals ? on : off);
        renderButtonTooltip(graphics, mouseX, mouseY, leftPos + 36, topPos + 30, 10, 10, "turret.mobs", turret.targetMobs ? on : off);
        renderButtonTooltip(graphics, mouseX, mouseY, leftPos + 50, topPos + 30, 10, 10, "turret.machines", turret.targetMachines ? on : off);

        // Информация о патронах
        if (Objects.requireNonNull(Minecraft.getInstance().player).containerMenu.getCarried().isEmpty() &&
                leftPos + 79 <= mouseX && leftPos + 79 + 54 > mouseX &&
                topPos + 62 < mouseY && topPos + 62 + 54 >= mouseY) {

            boolean draw = true;
            for (int i = 1; i <= 9; i++) {
                if (isHoveringSlot(i, mouseX, mouseY) && menu.getSlot(i).hasItem()) {
                    draw = false;
                    break;
                }
            }

            if (draw) {
                List<ItemStack> ammoTypes = getAmmoTypes();
                if (!ammoTypes.isEmpty()) {
                    List<List<ItemStack>> lines = new ArrayList<>();
                    ItemStack selected = ammoTypes.get(0);

                    if (ammoTypes.size() > 1) {
                        int cycle = (int) ((System.currentTimeMillis() % (1000L * ammoTypes.size())) / 1000);
                        selected = ammoTypes.get(cycle).copy();
                        selected.setCount(0);
                        ammoTypes.set(cycle, selected);
                    }

                    if (ammoTypes.size() < 10) {
                        lines.add(ammoTypes);
                    } else if (ammoTypes.size() < 24) {
                        lines.add(ammoTypes.subList(0, ammoTypes.size() / 2));
                        lines.add(ammoTypes.subList(ammoTypes.size() / 2, ammoTypes.size()));
                    } else {
                        int bound0 = (int) Math.ceil(ammoTypes.size() / 3D);
                        int bound1 = (int) Math.ceil(ammoTypes.size() / 3D * 2D);
                        lines.add(ammoTypes.subList(0, bound0));
                        lines.add(ammoTypes.subList(bound0, bound1));
                        lines.add(ammoTypes.subList(bound1, ammoTypes.size()));
                    }

                    renderAmmoTooltip(graphics, lines, selected, mouseX, mouseY);
                }
            }
        }
    }

    private boolean isHoveringSlot(int slotIndex, int mouseX, int mouseY) {
        net.minecraft.world.inventory.Slot slot = menu.getSlot(slotIndex);
        return isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    public boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= leftPos + x && mouseX < leftPos + x + width &&
                mouseY >= topPos + y && mouseY < topPos + y + height;
    }

    private List<ItemStack> getAmmoTypes() {
        List<ItemStack> list = new ArrayList<>();
        for (Integer id : turret.getAmmoList()) {
            BulletConfig cfg = BulletConfig.configs.get(id);
            if (cfg != null && cfg.ammo != null) {
                list.add(cfg.ammo.copy());
            }
        }
        return list;
    }

    private void renderButtonTooltip(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int w, int h, String key, String value) {
        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            graphics.renderTooltip(font, Component.translatable(key, value), mouseX, mouseY);
        }
    }

    private void renderAmmoTooltip(GuiGraphics graphics, List<List<ItemStack>> lines, ItemStack selected, int mouseX, int mouseY) {
        if (!lines.isEmpty()) {
            graphics.renderTooltip(font, List.of(selected.getHoverName()), selected.getTooltipImage(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.nameField != null && this.nameField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // Кнопка вкл/выкл
        if (leftPos + 115 <= mouseX && leftPos + 115 + 18 > mouseX && topPos + 26 < mouseY && topPos + 26 + 18 >= mouseY) {
            playButtonSound();
            PacketDispatcher.sendToServer(new AuxButtonPacket(turret.getBlockPos(), 0, 0));
            return true;
        }

        // Кнопки настроек целей
        if (leftPos + 8 <= mouseX && leftPos + 8 + 10 > mouseX && topPos + 30 < mouseY && topPos + 30 + 10 >= mouseY) {
            playButtonSound();
            PacketDispatcher.sendToServer(new AuxButtonPacket(turret.getBlockPos(), 0, 1));
            return true;
        }
        if (leftPos + 22 <= mouseX && leftPos + 22 + 10 > mouseX && topPos + 30 < mouseY && topPos + 30 + 10 >= mouseY) {
            playButtonSound();
            PacketDispatcher.sendToServer(new AuxButtonPacket(turret.getBlockPos(), 0, 2));
            return true;
        }
        if (leftPos + 36 <= mouseX && leftPos + 36 + 10 > mouseX && topPos + 30 < mouseY && topPos + 30 + 10 >= mouseY) {
            playButtonSound();
            PacketDispatcher.sendToServer(new AuxButtonPacket(turret.getBlockPos(), 0, 3));
            return true;
        }
        if (leftPos + 50 <= mouseX && leftPos + 50 + 10 > mouseX && topPos + 30 < mouseY && topPos + 30 + 10 >= mouseY) {
            playButtonSound();
            PacketDispatcher.sendToServer(new AuxButtonPacket(turret.getBlockPos(), 0, 4));
            return true;
        }

        int count = getWhitelistCount();
        if (count > 0) {
            if (leftPos + 7 <= mouseX && leftPos + 7 + 18 > mouseX && topPos + 80 < mouseY && topPos + 80 + 18 >= mouseY) {
                whitelistIndex--;
                if (whitelistIndex < 0) whitelistIndex = count - 1;
                playButtonSound();
                return true;
            }
            if (leftPos + 43 <= mouseX && leftPos + 43 + 18 > mouseX && topPos + 80 < mouseY && topPos + 80 + 18 >= mouseY) {
                whitelistIndex++;
                whitelistIndex %= count;
                playButtonSound();
                return true;
            }
        }

        // Добавить имя
        if (leftPos + 7 <= mouseX && leftPos + 7 + 18 > mouseX && topPos + 98 < mouseY && topPos + 98 + 18 >= mouseY) {
            playButtonSound();
            if (this.nameField != null && !nameField.getValue().isEmpty()) {
                net.minecraft.nbt.CompoundTag data = new net.minecraft.nbt.CompoundTag();
                data.putString("name", nameField.getValue());
                PacketDispatcher.sendToServer(new NBTControlPacket(data, turret.getBlockPos()));
                nameField.setValue("");
            }
            return true;
        }

        // Удалить имя
        if (leftPos + 43 <= mouseX && leftPos + 43 + 18 > mouseX && topPos + 98 < mouseY && topPos + 98 + 18 >= mouseY) {
            playButtonSound();
            net.minecraft.nbt.CompoundTag data = new net.minecraft.nbt.CompoundTag();
            data.putInt("del", whitelistIndex);
            PacketDispatcher.sendToServer(new NBTControlPacket(data, turret.getBlockPos()));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playButtonSound() {
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F)
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.nameField != null && this.nameField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Рисуем базовую текстуру
        graphics.blit(TEXTURE_BASE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Рисуем текстуру сентея (поверх)
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Кнопка вкл/выкл
        if (turret.isOn) {
            graphics.blit(TEXTURE_BASE, leftPos + 115, topPos + 26, 176, 40, 18, 18);
        }

        // Иконки настроек
        if (turret.targetPlayers) graphics.blit(TEXTURE_BASE, leftPos + 8, topPos + 30, 176, 0, 10, 10);
        if (turret.targetAnimals) graphics.blit(TEXTURE_BASE, leftPos + 22, topPos + 30, 176, 10, 10, 10);
        if (turret.targetMobs) graphics.blit(TEXTURE_BASE, leftPos + 36, topPos + 30, 176, 20, 10, 10);
        if (turret.targetMachines) graphics.blit(TEXTURE_BASE, leftPos + 50, topPos + 30, 176, 30, 10, 10);

        // Энергия
        int powerScaled = (int) (turret.power * 52 / turret.getMaxPower());
        graphics.blit(TEXTURE_BASE, leftPos + 152, topPos + 97 - powerScaled, 194, 52 - powerScaled, 16, powerScaled);

        // Ховеры кнопок
        if (leftPos + 7 <= mouseX && leftPos + 7 + 18 > mouseX && topPos + 80 < mouseY && topPos + 80 + 18 >= mouseY) {
            graphics.blit(TEXTURE_BASE, leftPos + 7, topPos + 80, 176, 58, 18, 18);
        }
        if (leftPos + 43 <= mouseX && leftPos + 43 + 18 > mouseX && topPos + 80 < mouseY && topPos + 80 + 18 >= mouseY) {
            graphics.blit(TEXTURE_BASE, leftPos + 43, topPos + 80, 194, 58, 18, 18);
        }
        if (leftPos + 7 <= mouseX && leftPos + 7 + 18 > mouseX && topPos + 98 < mouseY && topPos + 98 + 18 >= mouseY) {
            graphics.blit(TEXTURE_BASE, leftPos + 7, topPos + 98, 176, 76, 18, 18);
        }
        if (leftPos + 43 <= mouseX && leftPos + 43 + 18 > mouseX && topPos + 98 < mouseY && topPos + 98 + 18 >= mouseY) {
            graphics.blit(TEXTURE_BASE, leftPos + 43, topPos + 98, 194, 76, 18, 18);
        }

        // Счётчик убийств
        int tallies = turret.stattrak;
        if (tallies >= 36) {
            graphics.blit(TEXTURE_BASE, leftPos + 77, topPos + 50, 176, 120, 63, 6);
        } else {
            int steps = (int) Math.ceil(tallies / 5D);
            for (int s = 0; s < steps; s++) {
                int m = tallies % 5;
                if (s < steps - 1 || m == 0) {
                    graphics.blit(TEXTURE_BASE, leftPos + 77 + 9 * s, topPos + 50, 194, 94, 9, 6);
                } else {
                    graphics.blit(TEXTURE_BASE, leftPos + 77 + 9 * s, topPos + 50, 176, 94, m * 2, 6);
                }
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        String name = turret.getDisplayName().getString();
        graphics.drawString(font, name, imageWidth / 2 - font.width(name) / 2, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040, false);

        List<String> whitelist = turret.getWhitelist();
        String displayName = Component.translatable("turret.none").getString();

        int count = getWhitelistCount();
        if (count > 0) {
            while (whitelistIndex >= count) {
                whitelistIndex = 0;
            }
        }

        if (whitelist != null && !whitelist.isEmpty()) {
            displayName = whitelist.get(whitelistIndex);
        }

        String text = nameField != null ? nameField.getValue() : "";
        String cursor = (System.currentTimeMillis() % 1000 < 500) ? " " : "|";
        if (nameField != null && nameField.isFocused()) {
            int cursorPos = nameField.getCursorPosition();
            text = text.substring(0, cursorPos) + cursor + text.substring(cursorPos);
        }

        graphics.drawString(font, displayName, 12, 51, 0x00ff00, false);
        graphics.drawString(font, text, 12, 69, 0x00ff00, false);
    }

    private int getWhitelistCount() {
        List<String> names = turret.getWhitelist();
        return names == null ? 0 : names.size();
    }
}