package com.hbm.items.tool;

import com.hbm.api.fluid.IFillableItem;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemPipette extends Item implements IFillableItem {

    public ItemPipette(Properties properties) {
        super(properties);
    }

    public short getMaxFill(ItemStack stack) {
        if (this == ModItems.PIPETTE_LABORATORY.get()) return 50;
        else return 1_000;
    }

    public void initNBT(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putShort("type", (short) Fluids.getID(Fluids.NONE.get()));
        tag.putShort("fill", (short) 0);
        tag.putShort("capacity", getMaxFill(stack));
        stack.setTag(tag);
    }

    public FluidTypeHBM getType(ItemStack stack) {
        if (!stack.hasTag()) {
            initNBT(stack);
        }
        short id = Objects.requireNonNull(stack.getTag()).getShort("type");
        return Fluids.fromID(id);
    }

    public short getCapacity(ItemStack stack) {
        if (!stack.hasTag()) {
            initNBT(stack);
        }
        return Objects.requireNonNull(stack.getTag()).getShort("capacity");
    }

    public void setFill(ItemStack stack, FluidTypeHBM type, short fill) {
        if (!stack.hasTag()) {
            initNBT(stack);
        }
        CompoundTag tag = stack.getTag();
        Objects.requireNonNull(tag).putShort("type", (short) Fluids.getID(type));
        tag.putShort("fill", fill);
    }

    @Override
    public int getFill(ItemStack stack) {
        if (!stack.hasTag()) {
            initNBT(stack);
        }
        return Objects.requireNonNull(stack.getTag()).getShort("fill");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.hasTag()) {
            initNBT(stack);
        }

        if (!level.isClientSide) {
            if (this.getFill(stack) == 0) {
                short a;
                if (this == ModItems.PIPETTE_LABORATORY.get()) {
                    a = !player.isShiftKeyDown() ? (short) Math.min(this.getCapacity(stack) + 1, 50) : (short) Math.max(this.getCapacity(stack) - 1, 1);
                } else {
                    a = !player.isShiftKeyDown() ? (short) Math.min(this.getCapacity(stack) + 50, 1_000) : (short) Math.max(this.getCapacity(stack) - 50, 50);
                }
                Objects.requireNonNull(stack.getTag()).putShort("capacity", a);
                player.displayClientMessage(Component.literal(a + "/" + this.getMaxFill(stack) + "mB"), false);
            } else {
                player.displayClientMessage(Component.translatable("desc.item.pipette.noEmpty"), false);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.PIPETTE_LABORATORY.get()) {
            tooltip.add(Component.translatable("desc.item.pipette.corrosive"));
            tooltip.add(Component.translatable("desc.item.pipette.laboratory"));
        }
        if (this == ModItems.PIPETTE_BORON.get()) {
            tooltip.add(Component.translatable("desc.item.pipette.corrosive"));
        }
        if (this == ModItems.PIPETTE.get()) {
            tooltip.add(Component.translatable("desc.item.pipette.noCorrosive"));
        }
        tooltip.add(Component.literal("Fluid: " + this.getType(stack).getLocalizedName()));
        tooltip.add(Component.literal("Amount: " + this.getFill(stack) + "/" + this.getCapacity(stack) + "mB (" + this.getMaxFill(stack) + "mB)"));
    }

    @Override
    public boolean acceptsFluid(FluidTypeHBM type, ItemStack stack) {
        return (type == this.getType(stack) || this.getFill(stack) == 0) && !type.isAntimatter();
    }

    @Override
    public int tryFill(FluidTypeHBM type, int amount, ItemStack stack) {
        if (!acceptsFluid(type, stack)) return amount;

        if (this.getFill(stack) == 0) {
            this.setFill(stack, type, (short) 0);
        }

        int req = this.getCapacity(stack) - this.getFill(stack);
        int toFill = Math.min(req, amount);
        this.setFill(stack, type, (short) (this.getFill(stack) + toFill));

        if (this.getFill(stack) > 0 && willFizzle(type)) {
            stack.shrink(1);
        }

        return amount - toFill;
    }

    public boolean willFizzle(FluidTypeHBM type) {
        if (this != ModItems.PIPETTE.get()) return false;
        return type.isCorrosive() && type != Fluids.PEROXIDE.get();
    }

    @Override
    public boolean providesFluid(FluidTypeHBM type, ItemStack stack) {
        return this.getType(stack) == type;
    }

    @Override
    public int tryEmpty(FluidTypeHBM type, int amount, ItemStack stack) {
        if (providesFluid(type, stack)) {
            int toUnload = Math.min(amount, this.getFill(stack));
            this.setFill(stack, type, (short) (this.getFill(stack) - toUnload));
            if (this.getFill(stack) == 0) {
                this.setFill(stack, Fluids.NONE.get(), (short) 0);
            }
            return toUnload;
        }
        return amount;
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return this.getFill(stack) == this.getCapacity(stack);
    }

    @Override
    public FluidTypeHBM getFirstFluidType(ItemStack stack) {
        return this.getType(stack);
    }
}