package com.hbm.render.overlay;


import com.hbm.api.fluid.IFluidContainer;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class TileEntityOverlayProvider implements IOverlayProvider {

    @Override
    public boolean shouldRender(OverlayContext context) {
        if (context.mc().hitResult == null ||
                context.mc().hitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        BlockPos pos = ((BlockHitResult) context.mc().hitResult).getBlockPos();
        Level level = context.mc().level;

        assert level != null;
        return level.getBlockEntity(pos) != null;
    }

    @Override
    public List<OverlaySection> getSections(OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        assert context.mc().hitResult != null;
        BlockPos pos = ((BlockHitResult) context.mc().hitResult).getBlockPos();
        Level level = context.mc().level;
        assert level != null;
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if (tileEntity != null) {
            OverlaySection section = new OverlaySection(OverlaySection.Type.TILE_ENTITY);

            // Пример для энергетических систем
            if (tileEntity instanceof IEnergyStorage energy) {
                int stored = energy.getEnergyStored();
                int capacity = energy.getMaxEnergyStored();
                float percentage = capacity > 0 ? (stored * 100f / capacity) : 0;

                section.addLine(Component.literal("⚡ Энергия: " +
                        formatNumber(stored) + "/" + formatNumber(capacity) + " RF"));

                String energyBar = createBar(percentage, 20);
                section.addLine(Component.literal(energyBar)
                        .withStyle(style -> style.withColor(getEnergyColor(percentage))));
            }

            // Пример для инвентарей
            if (tileEntity instanceof IItemHandler itemHandler) {
                int slots = itemHandler.getSlots();
                int filled = 0;
                for (int i = 0; i < slots; i++) {
                    if (!itemHandler.getStackInSlot(i).isEmpty()) filled++;
                }

                section.addLine(Component.literal("📦 Инвентарь: " + filled + "/" + slots + " слотов"));
            }

            // Пример для жидкостей
            if (tileEntity instanceof IFluidContainer fluidContainer) {
                FluidTypeHBM fluid = fluidContainer.getFluidType();
                if (fluid != Fluids.NONE.get()) {

                    long amount = fluidContainer.getFluidAmount(fluid);
                    long capacity = fluidContainer.getCapacity(fluid);
                    float percentage = capacity > 0 ? (amount * 100f / capacity) : 0;

                    section.addLine(Component.literal("💧 " + fluid.getLocalizedName() +
                            ": " + formatNumber((int)amount) + "/" + formatNumber((int)capacity) + " мБ"));

                    String fluidBar = createBar(percentage, 15);
                    section.addLine(Component.literal(fluidBar)
                            .withStyle(style -> style.withColor(0x1E90FF)));
                }
            }

            if (!section.getLines().isEmpty()) {
                sections.add(section);
            }
        }

        return sections;
    }

    private String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fk", number / 1000.0);
        }
        return String.valueOf(number);
    }

    private String createBar(float percentage, int length) {
        int filledChars = (int) (percentage / 100 * length);
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < length; i++) {
            bar.append(i < filledChars ? "█" : "░");
        }

        return String.format("%s %.1f%%", bar, percentage);
    }

    private int getEnergyColor(float percentage) {
        if (percentage > 75) return 0x00FF00;
        if (percentage > 50) return 0xFFFF00;
        if (percentage > 25) return 0xFFA500;
        return 0xFF4500;
    }
}