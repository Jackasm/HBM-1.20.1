package com.hbm.pawn;

import com.hbm.entity.mob.PawnEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.UUID;

public class VanillaFurnaceAdapter implements IPawnServicable {
    private final AbstractFurnaceBlockEntity furnace;
    private final BlockPos pos;
    private final Level level;
    private final IItemHandler inventory;

    public VanillaFurnaceAdapter(AbstractFurnaceBlockEntity furnace) {
        this.furnace = furnace;
        this.pos = furnace.getBlockPos();
        this.level = furnace.getLevel();
        this.inventory = new InvWrapper(furnace);
    }

    @Override
    public ServicableType getServicableType() {
        return ServicableType.FURNACE;
    }

    @Override
    public boolean needsService() {
        ItemStack fuel = inventory.getStackInSlot(1);
        return fuel.isEmpty() || fuel.getCount() < 4;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public Level getServiceLevel() {
        return level;
    }

    @Override
    public boolean executeService(PawnEntity pawn) {
        // Логика заправки будет в PawnWorkGoal
        return true;
    }

    @Override
    public UUID getOwnerUUID() {
        // Для ванильных печей – получаем владельца из реестра
        return BlockOwnershipRegistry.get(level).getOwner(pos);
    }

    @Override
    public void setOwnerUUID(UUID owner) {
        BlockOwnershipRegistry.get(level).setOwner(pos, owner);
    }
}