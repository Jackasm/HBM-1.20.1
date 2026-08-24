package com.hbm.tileentity.machine;

import com.hbm.inventory.container.ContainerMachineAmmoPress;
import com.hbm.inventory.recipes.AmmoPressRecipes;
import com.hbm.inventory.recipes.AmmoPressRecipes.AmmoPressRecipe;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.interfaces.IControlReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TileEntityMachineAmmoPress extends TileEntityMachineBase implements IControlReceiver, MenuProvider {

    public int selectedRecipe = -1;
    public int playAnimation = 0;
    public float prevLift = 0F;
    public float lift = 0F;
    public float prevPress = 0F;
    public float press = 0F;

    public enum AnimationState {
        LIFTING, PRESSING, RETRACTING, LOWERING
    }

    public AnimationState animState = AnimationState.LIFTING;

    private final ItemStackHandler inventory = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot >= 9) return false;
            if (selectedRecipe < 0 || selectedRecipe >= AmmoPressRecipes.recipes.size()) return false;

            AmmoPressRecipe recipe = AmmoPressRecipes.recipes.get(selectedRecipe);
            if (recipe.input[slot] == null) return false;
            return recipe.input[slot].matchesRecipe(stack, true);
        }
    };

    private final AABB renderBoundingBox;

    public TileEntityMachineAmmoPress(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_AMMO_PRESS.get(), pos, state);
        this.renderBoundingBox = new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1,
                pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (playAnimation > 0) playAnimation--;
            performRecipe();
            this.networkPackNT(25);
        } else {
            this.prevLift = this.lift;
            this.prevPress = this.press;

            if (playAnimation > 0 || lift > 0) {
                switch (animState) {
                    case LIFTING:
                        lift += 1F / 40F;
                        if (lift >= 1F) {
                            lift = 1F;
                            animState = AnimationState.PRESSING;
                        }
                        break;
                    case PRESSING:
                        press += 1F / 20F;
                        if (press >= 1F) {
                            press = 1F;
                            animState = AnimationState.RETRACTING;
                        }
                        break;
                    case RETRACTING:
                        press -= 1F / 20F;
                        if (press <= 0F) {
                            press = 0F;
                            animState = AnimationState.LOWERING;
                        }
                        break;
                    case LOWERING:
                        lift -= 1F / 40F;
                        if (lift <= 0F) {
                            lift = 0F;
                            animState = AnimationState.LIFTING;
                        }
                        break;
                }
            }
        }
    }

    public void performRecipe() {
        if (selectedRecipe < 0 || selectedRecipe >= AmmoPressRecipes.recipes.size()) return;

        AmmoPressRecipe recipe = AmmoPressRecipes.recipes.get(selectedRecipe);

        ItemStack output = inventory.getStackInSlot(9);
        if (!output.isEmpty()) {
            if (output.getItem() != recipe.output.getItem()) return;
            if (output.getDamageValue() != recipe.output.getDamageValue()) return;
            if (output.getCount() + recipe.output.getCount() > output.getMaxStackSize()) return;
        }

        if (hasIngredients(recipe)) {
            produceAmmo(recipe);
            performRecipe();
        }
    }

    public boolean hasIngredients(AmmoPressRecipe recipe) {
        for (int i = 0; i < 9; i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (recipe.input[i] == null && slotStack.isEmpty()) continue;
            if (recipe.input[i] != null && slotStack.isEmpty()) return false;
            if (recipe.input[i] == null && !slotStack.isEmpty()) return false;
            if (!recipe.input[i].matchesRecipe(slotStack, false)) return false;
        }
        return true;
    }

    protected void produceAmmo(AmmoPressRecipe recipe) {
        for (int i = 0; i < 9; i++) {
            if (recipe.input[i] != null) {
                inventory.extractItem(i, recipe.input[i].getStackSize(), false);
            }
        }

        ItemStack output = inventory.getStackInSlot(9);
        if (output.isEmpty()) {
            inventory.setStackInSlot(9, recipe.output.copy());
        } else {
            output.grow(recipe.output.getCount());
        }

        playAnimation = 40;
    }

    public int[] access = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public boolean canExtractItem(int i, ItemStack stack, int j) {
        return i == 9;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(selectedRecipe);
        buf.writeInt(playAnimation);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        selectedRecipe = buf.readInt();
        playAnimation = buf.readInt();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("recipe", selectedRecipe);
        nbt.put("inventory", inventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        selectedRecipe = nbt.getInt("recipe");
        inventory.deserializeNBT(nbt.getCompound("inventory"));
    }

    @Override
    public boolean hasPermission(Player player) {
        return isUsableByPlayer(player);
    }

    @Override
    public void receiveControl(CompoundTag data) {
        int newRecipe = data.getInt("selection");
        if (newRecipe == selectedRecipe) {
            selectedRecipe = -1;
        } else {
            selectedRecipe = newRecipe;
        }
        setChanged();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return renderBoundingBox;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerMachineAmmoPress(windowId, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.machineAmmoPress");
    }
}