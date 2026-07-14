package com.hbm.render.tileentity;

import com.hbm.blocks.network.FluidDuctStandard;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.conduit.ConduitChannel;
import com.hbm.tileentity.conduit.ConduitTileEntity;
import com.hbm.tileentity.conduit.EnergyChannel;
import com.hbm.tileentity.conduit.FluidChannel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class ConduitRenderer implements BlockEntityRenderer<ConduitTileEntity> {

    public ConduitRenderer(BlockEntityRendererProvider.Context ignoredCtx) {}

    private static final ResourceLocation[] SLOT_OVERLAYS = {
            ResLocation("hbm", "textures/block/storage/conduit_overlay_0.png"),
            ResLocation("hbm", "textures/block/storage/conduit_overlay_1.png"),
            ResLocation("hbm", "textures/block/storage/conduit_overlay_2.png")
    };

    private static final ResourceLocation CABLE_OVERLAY =
            ResLocation("hbm", "textures/block/storage/conduit_cable_overlay.png");

    private static final double[][] OFFSETS = {
            {-0.2, -0.2},
            {-0.2,  0.2},
            { 0.2, -0.2},
            { 0.2,  0.2}
    };

    @Override
    public void render(ConduitTileEntity conduit, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer, int light, int overlay) {
        Level level = conduit.getLevel();

        if (level == null) return;

        EnumSet<Direction> allConnected = EnumSet.noneOf(Direction.class);
        for (ConduitChannel channel : conduit.getChannels()) {
            allConnected.addAll(channel.getConnections());
        }

        boolean straight = false;
        if (allConnected.size() == 1) {
            straight = true;
        } else if (allConnected.size() == 2) {
            Iterator<Direction> it = allConnected.iterator();
            Direction a = it.next();
            Direction b = it.next();
            if (a.getOpposite() == b) straight = true;
        }

        ResourceLocation BOX_TEX = ResLocation("hbm", "textures/block/storage/conduit.png");
        ResourceLocation CABLE_TEX = HBMResourceManager.red_cable_tex;

        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);

        if (!straight) {
            renderBox(stack, buffer, allConnected, conduit, BOX_TEX, light, overlay);
        } else {
            List<ConduitChannel> sorted = getSortedChannels(conduit);

            Direction.Axis axis = allConnected.iterator().next().getAxis();
            int idx = 0;
            for (ConduitChannel channel : sorted) {
                stack.pushPose();

                double[] base = OFFSETS[idx % OFFSETS.length];
                double offX = 0, offY = 0, offZ = 0;

                if (axis == Direction.Axis.X) {
                    offY = base[0];
                    offZ = base[1];
                } else if (axis == Direction.Axis.Z) {
                    offX = base[0];
                    offY = base[1];
                } else { // Y
                    offX = base[0];
                    offZ = base[1];
                }
                stack.translate(offX, offY, offZ);

                if (channel instanceof EnergyChannel) {
                    renderCable(stack, buffer, allConnected, CABLE_TEX, light, overlay);
                } else if (channel instanceof FluidChannel) {
                    renderFluidDuct(stack, buffer, allConnected, (FluidChannel) channel, light, overlay);
                }
                stack.popPose();
                idx++;
            }
        }

        stack.popPose();
    }

    private void renderBox(PoseStack stack, MultiBufferSource buffer, EnumSet<Direction> connected,
                           ConduitTileEntity conduit, ResourceLocation tex, int light, int overlay) {
        stack.pushPose();
        float scale = 2.665f;

        boolean pX = connected.contains(Direction.EAST);
        boolean nX = connected.contains(Direction.WEST);
        boolean pY = connected.contains(Direction.UP);
        boolean nY = connected.contains(Direction.DOWN);
        boolean pZ = connected.contains(Direction.SOUTH);
        boolean nZ = connected.contains(Direction.NORTH);

        int mask = (pX ? 32 : 0) | (nX ? 16 : 0) | (pY ? 8 : 0) | (nY ? 4 : 0) | (pZ ? 2 : 0) | (nZ ? 1 : 0);

        List<Integer> colors = collectChannelColors(conduit);
        boolean hasCable = hasEnergyChannel(conduit);

        if (mask == 0) {
            for (Direction dir : Direction.values()) {
                String part = getPartName(dir);
                renderBoxPart(stack, buffer, part, tex, colors, hasCable, light, overlay);
            }
        } else {
            stack.pushPose();
            stack.scale(1f, scale, scale);
            if (pX) renderBoxPart(stack, buffer, "pX", tex, colors, hasCable, light, overlay);
            if (nX) renderBoxPart(stack, buffer, "nX", tex, colors, hasCable, light, overlay);
            stack.popPose();

            stack.pushPose();
            stack.scale(scale, 1f, scale);
            if (pY) renderBoxPart(stack, buffer, "pY", tex, colors, hasCable, light, overlay);
            if (nY) renderBoxPart(stack, buffer, "nY", tex, colors, hasCable, light, overlay);
            stack.popPose();

            stack.pushPose();
            stack.scale(scale, scale, 1f);
            if (pZ) renderBoxPart(stack, buffer, "nZ", tex, colors, hasCable, light, overlay);
            if (nZ) renderBoxPart(stack, buffer, "pZ", tex, colors, hasCable, light, overlay);
            stack.popPose();

            // Заглушки
            stack.scale(scale, scale, scale);
            if (!pX && !pY && !pZ) renderBoxPart(stack, buffer, "ppn", tex, colors, hasCable, light, overlay);
            if (!pX && !pY && !nZ) renderBoxPart(stack, buffer, "ppp", tex, colors, hasCable, light, overlay);
            if (!nX && !pY && !pZ) renderBoxPart(stack, buffer, "npn", tex, colors, hasCable, light, overlay);
            if (!nX && !pY && !nZ) renderBoxPart(stack, buffer, "npp", tex, colors, hasCable, light, overlay);
            if (!pX && !nY && !pZ) renderBoxPart(stack, buffer, "pnn", tex, colors, hasCable, light, overlay);
            if (!pX && !nY && !nZ) renderBoxPart(stack, buffer, "pnp", tex, colors, hasCable, light, overlay);
            if (!nX && !nY && !pZ) renderBoxPart(stack, buffer, "nnn", tex, colors, hasCable, light, overlay);
            if (!nX && !nY && !nZ) renderBoxPart(stack, buffer, "nnp", tex, colors, hasCable, light, overlay);
        }
        stack.popPose();
    }

    private void renderFluidDuct(PoseStack stack, MultiBufferSource buffer, EnumSet<Direction> directions,
                                 FluidChannel channel, int light, int overlay) {
        int pipeType = channel.getEntry().pipeType;
        FluidTypeHBM fluid = channel.getEntry().fluid;
        int color = fluid != null ? fluid.getColor() : 0xff00ff;

        ResourceLocation tex = FluidDuctStandard.TEXTURES.get(pipeType);
        ResourceLocation over = FluidDuctStandard.OVERLAYS.get(pipeType);

        for (Direction dir : directions) {
            String part = getPartName(dir);
            renderDuctPart(stack, buffer, part, tex, over, color, light, overlay);
            String oppositePart = getPartName(dir.getOpposite());
            renderDuctPart(stack, buffer, oppositePart, tex, over, color, light, overlay);
        }
        renderDuctPart(stack, buffer, "Core", tex, over, color, light, overlay);
    }

    private void renderBoxPart(PoseStack stack, MultiBufferSource buffer, String part,
                               ResourceLocation tex, List<Integer> colors,
                               boolean hasCable, int light, int overlay) {
        // Базовая текстура
        HBMResourceManager.fluid_duct.renderPart(stack, buffer, part, tex, light, overlay);

        // Оверлеи для труб (цветные)
        for (int i = 0; i < colors.size(); i++) {
            int color = colors.get(i);
            ResourceLocation slotOverlay = SLOT_OVERLAYS[i];

            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            HBMResourceManager.fluid_duct.renderPartColored(stack, buffer, part, slotOverlay, light, overlay, r, g, b, 0.4f);
        }

        if (hasCable) {
            HBMResourceManager.fluid_duct.renderPart(stack, buffer, part, CABLE_OVERLAY, light, overlay);
        }
    }

    private void renderDuctPart(PoseStack stack, MultiBufferSource buffer, String part,
                                ResourceLocation tex, ResourceLocation overlay, int color, int light, int overlayVal) {
        stack.pushPose();
        HBMResourceManager.fluid_duct.renderPart(stack, buffer, part, tex, light, overlayVal);
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        HBMResourceManager.fluid_duct.renderPartColored(stack, buffer, part, overlay, light, overlayVal, r, g, b, 0.4f);
        stack.popPose();
    }

    private void renderCable(PoseStack stack, MultiBufferSource buffer, EnumSet<Direction> directions,
                             ResourceLocation tex, int light, int overlay) {
        for (Direction dir : directions) {
            String part = getCablePartName(dir);
            HBMResourceManager.cable_neo.renderPart(stack, buffer, part, tex, light, overlay);
        }
        if (!directions.isEmpty())
            HBMResourceManager.cable_neo.renderPart(stack, buffer, "Core", tex, light, overlay);
    }

    private List<Integer> collectChannelColors(ConduitTileEntity conduit) {
        List<Integer> colors = new ArrayList<>();
        ConduitChannel[] slots = new ConduitChannel[4];

        List<FluidChannel> initTubes = new ArrayList<>();
        FluidChannel noneTube = null;
        EnergyChannel energy = null;

        for (ConduitChannel ch : conduit.getChannels()) {
            if (ch instanceof FluidChannel fc) {
                if (fc.getEntry().fluid == com.hbm.inventory.fluid.Fluids.NONE.get()) {
                    noneTube = fc;
                } else {
                    initTubes.add(fc);
                }
            } else if (ch instanceof EnergyChannel ec) {
                energy = ec;
            }
        }

        initTubes.sort(Comparator.comparing(fc -> Objects.requireNonNull(fc.getEntry().fluid).getLocalizedName()));

        int idx = 0;
        for (FluidChannel fc : initTubes) {
            if (idx < 3) slots[idx++] = fc;
        }
        if (energy != null) slots[3] = energy;
        if (noneTube != null) {
            if (slots[2] == null) slots[2] = noneTube;
            else if (slots[3] == null) slots[3] = noneTube;
        }

        for (int i = 0; i < 4; i++) {
            if (slots[i] instanceof FluidChannel fc) {
                FluidTypeHBM fluid = fc.getEntry().fluid;
                colors.add(fluid != null ? fluid.getColor() : 0xffffff);
            }
        }
        return colors;
    }

    private boolean hasEnergyChannel(ConduitTileEntity conduit) {
        for (ConduitChannel channel : conduit.getChannels()) {
            if (channel instanceof EnergyChannel) return true;
        }
        return false;
    }

    private List<ConduitChannel> getSortedChannels(ConduitTileEntity conduit) {
        ConduitChannel[] slots = new ConduitChannel[4];
        List<FluidChannel> initTubes = new ArrayList<>();
        FluidChannel noneTube = null;
        EnergyChannel energy = null;

        for (ConduitChannel ch : conduit.getChannels()) {
            if (ch instanceof FluidChannel fc) {
                if (fc.getEntry().fluid == com.hbm.inventory.fluid.Fluids.NONE.get()) {
                    noneTube = fc;
                } else {
                    initTubes.add(fc);
                }
            } else if (ch instanceof EnergyChannel ec) {
                energy = ec;
            }
        }

        // Сортируем инициализированные трубы по алфавиту
        initTubes.sort(Comparator.comparing(fc -> Objects.requireNonNull(fc.getEntry().fluid).getLocalizedName()));

        // Инициализированные трубы заполняют слоты 0, 1, 2
        int idx = 0;
        for (FluidChannel fc : initTubes) {
            if (idx < 3) {
                slots[idx++] = fc;
            }
        }

        // Кабель всегда в слот 3
        if (energy != null) {
            slots[3] = energy;
        }

        // Неинициализированная труба — в первый свободный слот 2 или 3
        if (noneTube != null) {
            if (slots[2] == null) {
                slots[2] = noneTube;
            } else if (slots[3] == null) {
                slots[3] = noneTube;
            }
        }

        return Arrays.asList(slots);
    }

    private String getPartName(Direction dir) {
        return switch (dir) {
            case EAST -> "pX";
            case WEST -> "nX";
            case UP -> "pY";
            case DOWN -> "nY";
            case SOUTH -> "nZ";
            case NORTH -> "pZ";
        };
    }

    private String getCablePartName(Direction dir) {
        return switch (dir) {
            case EAST -> "posX";
            case WEST -> "negX";
            case UP -> "posY";
            case DOWN -> "negY";
            case SOUTH -> "negZ";
            case NORTH -> "posZ";
        };
    }
}