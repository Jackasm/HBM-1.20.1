package com.hbm.pawn;

import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.storage.TileEntityFileCabinet;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BlockClassifier {

    // Склады по классам
    public static boolean isStorage(BlockEntity be) {
        if (be instanceof TileEntityFileCabinet) return true;
        return be instanceof ChestBlockEntity ||
                be instanceof BarrelBlockEntity ||
                be instanceof TileEntityAshpit ||
                be.getClass().getSimpleName().contains("Crate") ||
                be.getClass().getSimpleName().contains("Tank") ||
                be.getClass().getSimpleName().contains("Cabinet");
    }

    public static StorageType getStorageType(BlockEntity be) {
        if (be instanceof ChestBlockEntity) return StorageType.CHEST;
        if (be instanceof BarrelBlockEntity) return StorageType.BARREL;
        if (be instanceof TileEntityFileCabinet) return StorageType.CABINET;
        String name = be.getClass().getSimpleName().toLowerCase();
        if (name.contains("tank")) return StorageType.TANK;
        if (name.contains("crate")) return StorageType.CRATE;
        if (name.contains("cabinet")) return StorageType.CABINET;
        if (name.contains("drawer")) return StorageType.DRAWER;
        return StorageType.OTHER;
    }

    // Потребители — блоки, имеющие инвентарь (через Container или IItemHandler)
    public static boolean isConsumer(BlockEntity be) {
        if (be == null) return false;
        // Если есть capability IItemHandler — это потребитель
        if (be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            return !isStorage(be);
        }
        // Если реализует Container — тоже потребитель
        return be instanceof Container && !isStorage(be);
    }

    public static ConsumerType getConsumerType(BlockEntity be) {
        // Ванильные печи
        if (be instanceof AbstractFurnaceBlockEntity) return ConsumerType.FURNACE;
        // Ваши печи (явные классы)
        if (be instanceof TileEntityFurnaceIron) return ConsumerType.FURNACE;
        if (be instanceof TileEntityHeaterOven) return ConsumerType.FURNACE;
        if (be instanceof TileEntityBlastFurnace) return ConsumerType.FURNACE;
        if (be instanceof TileEntityHeaterFirebox) return ConsumerType.FURNACE;
        if (be instanceof TileEntityMachinePress) return ConsumerType.FURNACE;
        if (be instanceof TileEntitySawmill) return ConsumerType.CRAFTER;
        if (be instanceof TileEntityFurnaceSteel) return ConsumerType.CRAFTER;
        if (be instanceof TileEntityHeaterOilburner) return ConsumerType.MACHINE;

        if (be.getClass().getSimpleName().contains("Smelter")) return ConsumerType.SMELTER;
        if (be.getClass().getSimpleName().equals("CrafterPlannerBlockEntity")) return ConsumerType.PLANNER;

        // Любой другой блок с IItemHandler — GENERIC
        if (be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
            return ConsumerType.GENERIC;
        }
        return ConsumerType.GENERIC;
    }
}