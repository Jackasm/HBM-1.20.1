package com.hbm.inventory.fluid.tank;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.util.BobMathUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluidTankHBM implements IFluidTank, IFluidHandler {

    public static final List<FluidLoadingHandler> loadingHandlers = new ArrayList<>();

    private FluidTypeHBM type;
    private int fluid;
    private int capacity;
    private int pressure;

    static {
        loadingHandlers.add(new FluidLoaderStandard());
        loadingHandlers.add(new FluidLoaderFillableItem());
        loadingHandlers.add(new FluidLoaderInfinite());
    }

    public FluidTankHBM(FluidTypeHBM type, int capacity) {
        this.type = type;
        this.capacity = capacity;
        this.fluid = 0;
        this.pressure = 0;
    }

    public int changeTankSize(int size) {
        capacity = size;

        if(fluid > capacity) {
            int dif = fluid - capacity;
            fluid = capacity;
            return dif;
        }
        return 0;
    }

    public void setFill(int fill) {
        this.fluid = Math.min(fill, capacity);
    }

    public void setType(FluidTypeHBM type) {
        if (this.type != type) {
            this.type = type;
            this.fluid = 0;
        }
    }

    public boolean setType(int in, int out, ItemStack[] slots, ItemStackHandler handler) {

        if(!slots[in].isEmpty() && slots[in].getItem() instanceof IItemFluidIdentifier id) {

            if(in == out) {
                FluidTypeHBM newType = id.getType(null, BlockPos.ZERO, slots[in]);

                if(type != newType) {
                    type = newType;
                    fluid = 0;
                    return true;
                }

            } else if(slots[out].isEmpty()) {
                FluidTypeHBM newType = id.getType(null, BlockPos.ZERO,  slots[in]);
                if(type != newType) {
                    type = newType;
                    handler.setStackInSlot(out, slots[in].copy());
                    handler.setStackInSlot(in, ItemStack.EMPTY);
                    fluid = 0;
                    return true;
                }
            }
        }

        return false;
    }

    public int getPressure()
    {
        return pressure;
    }

    public FluidTypeHBM getTankType() {
        return type;
    }

    public int getFill() {
        return fluid;
    }

    public int getMaxFill() {
        return capacity;
    }


    // IFluidTank implementation
    @Nonnull
    @Override
    public FluidStack getFluid() {
        // Пока возвращаем пустой стек, позже реализуем конвертацию
        return FluidStack.EMPTY;
    }

    @Override
    public int getFluidAmount() {
        return fluid;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        // Позже добавим проверку по типу
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }

        int filled = Math.min(capacity - fluid, resource.getAmount());
        if (action.execute() && filled > 0) {
            fluid += filled;
        }
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int drained = Math.min(fluid, maxDrain);
        if (action.execute() && drained > 0) {
            fluid -= drained;
        }
        // Позже реализуем создание правильного FluidStack
        return new FluidStack(net.minecraft.world.level.material.Fluids.WATER, drained);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || resource.getAmount() <= 0) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    public boolean unloadTank(int in, int out, ItemStack[] slots, ItemStackHandler itemStackHandler) {
        if(slots[in] == null) return false;

        int prev = this.getFill();

        for(FluidLoadingHandler handler : loadingHandlers) {
            if(handler.fillItem(itemStackHandler, in, out, this)) {
                break;
            }
        }

        return this.getFill() < prev;
    }

    // NBT methods
    public void writeToNBT(CompoundTag nbt, String prefix) {
        nbt.putInt(prefix + "_fill", fluid);
        nbt.putInt(prefix + "_capacity", capacity);
        nbt.putString(prefix + "_type", type.getName());
        nbt.putInt(prefix + "_pressure", pressure);
    }

    public void readFromNBT(CompoundTag nbt, String prefix) {
        fluid = nbt.getInt(prefix + "_fill");
        capacity = nbt.getInt(prefix + "_capacity");
        type = Fluids.fromName(nbt.getString(prefix + "_type"));
        pressure = nbt.getInt(prefix + "_pressure");

        // Ensure fluid doesn't exceed capacity
        fluid = Math.min(fluid, capacity);
    }

    // IFluidHandler implementation
    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == 0 ? getFluid() : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? capacity : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank == 0 && isFluidValid(stack);
    }

    public boolean loadTank(int in, int out, ItemStack[] slots, ItemStackHandler itemStackHandler) {
        if(slots[in] == null) return false;

        boolean isInfiniteBarrel = slots[in].getItem() == ModItems.FLUID_BARREL_INFINITE.get();
        if(!isInfiniteBarrel && pressure != 0) return false;

        int prev = this.getFill();

        for(FluidLoadingHandler handler : loadingHandlers) {
            if(handler.emptyItem(itemStackHandler, in, out, this)) {
                break;
            }
        }

        return this.getFill() > prev;
    }

    /**
     * Renders the fluid texture into a GUI, with the height based on the fill state
     * @param graphics GuiGraphics для рендера
     * @param x the tank's left side
     * @param y the tank's bottom side (convention from the old system)
     * @param width tank width
     * @param height tank height
     */
    @OnlyIn(Dist.CLIENT)
    public void renderTank(GuiGraphics graphics, int x, int y, int width, int height) {
        renderTank(graphics, x, y, width, height, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTank(GuiGraphics graphics, int x, int y, int width, int height, int orientation) {

        int color = type.getTint();
        float r = ((color >> 16) & 0xFF) / 255F;
        float g = ((color >> 8) & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;

        graphics.setColor(r, g, b, 1.0F);

        y -= height;

        ResourceLocation texture = type.getTexture();

        int fillHeight = (fluid * height) / capacity;

        if (orientation == 0) {
            // Вертикальный бак (снизу вверх)
            graphics.blit(texture,
                    x, y + height - fillHeight,  // x, y
                    0, 0,                         // u, v
                    width, fillHeight,             // width, height
                    width, height                   // textureWidth, textureHeight
            );
        } else if (orientation == 1) {
            // Горизонтальный бак (слева направо)
            int fillWidth = (fluid * width) / capacity;
            graphics.blit(texture,
                    x, y,                          // x, y
                    0, 0,                         // u, v
                    fillWidth, height,              // width, height
                    width, height                   // textureWidth, textureHeight
            );
        }

        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public void renderTankInfo(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {

            List<Component> list = new ArrayList<>();
            list.add(Component.translatable(type.getDescriptionId()));
            list.add(Component.literal(fluid + "/" + capacity + "mB"));

            if (this.pressure != 0) {
                list.add(Component.literal("§cPressure: " + this.pressure + " PU"));
                list.add(Component.literal((BobMathUtil.getBlink() ? "§c" : "§4") + "Pressurized, use compressor!"));
            }

            type.addInfo(list);

            // Рендерим тултип напрямую через GuiGraphics
            graphics.renderComponentTooltip(Minecraft.getInstance().font, list, mouseX, mouseY);
        }
    }

    public void serialize(ByteBuf buf) {
        buf.writeInt(fluid);
        buf.writeInt(capacity);
        buf.writeInt(Fluids.getID(type));
        buf.writeShort((short) pressure);
    }

    public void deserialize(ByteBuf buf) {
        this.fluid = buf.readInt();
        this.capacity = buf.readInt();
        this.type = Fluids.fromID(buf.readInt());
        this.pressure = buf.readShort();
    }
}