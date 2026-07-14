package com.hbm.items.armor;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ISatChip;
import com.hbm.network.PacketDispatcher;

import com.hbm.saveddata.SatelliteSavedData;
import com.hbm.saveddata.satellites.Satellite;
import com.hbm.saveddata.satellites.SatelliteScanner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemModLens extends ItemArmorMod implements ISatChip {

    public ItemModLens(Properties properties) {
        super(ArmorModHandler.EXTRA, true, false, false, false, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.AQUA + "Satellite Frequency: " + ISatChip.getFreq(stack)));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        Level level = entity.level();
        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        ItemStack lens = ArmorModHandler.pryMods(armor)[ArmorModHandler.EXTRA];
        if (lens == null) return;

        int freq = ISatChip.getFreq(lens);
        Satellite sat = Objects.requireNonNull(SatelliteSavedData.getData(level)).getSatFromFreq(freq);
        if (!(sat instanceof SatelliteScanner)) return;

        int x = (int) Math.floor(player.getX());
        int y = (int) Math.floor(player.getY());
        int z = (int) Math.floor(player.getZ());
        int range = 3;

        int cX = x >> 4;
        int cZ = z >> 4;

        int height = Math.min(Math.max(y + 10, 64), 255);
        int seg = (int) (level.getGameTime() % height);

        int hits = 0;

        for (int chunkX = cX - range; chunkX <= cX + range; chunkX++) {
            for (int chunkZ = cZ - range; chunkZ <= cZ + range; chunkZ++) {
                ChunkAccess chunk = level.getChunk(chunkX, chunkZ);

                for (int ix = 0; ix < 16; ix++) {
                    for (int iz = 0; iz < 16; iz++) {
                        BlockPos pos = new BlockPos((chunkX << 4) + ix, seg, (chunkZ << 4) + iz);
                        Block b = level.getBlockState(pos).getBlock();
                        int aX = pos.getX();
                        int aZ = pos.getZ();

                        if (addIf(ModBlocks.ORE_ALEXANDRITE.get(), b, 1, aX, seg, aZ, "Alexandrite", 0x00ffff, player)) hits++;
                        if (addIf(ModBlocks.ORE_OIL.get(), b, 300, aX, seg, aZ, "Oil", 0xa0a0a0, player)) hits++;
                        if (addIf(ModBlocks.ORE_BEDROCK_OIL.get(), b, 300, aX, seg, aZ, "Bedrock Oil", 0xa0a0a0, player)) hits++;
                        if (addIf(ModBlocks.ORE_COLTAN.get(), b, 5, aX, seg, aZ, "Coltan", 0xa0a000, player)) hits++;
                        if (addIf(ModBlocks.STONE_GNEISS.get(), b, 5000, aX, seg, aZ, "Schist", 0x8080ff, player)) hits++;
                        if (addIf(ModBlocks.ORE_AUSTRALIUM.get(), b, 1000, aX, seg, aZ, "Australium", 0xffff00, player)) hits++;
                        if (addIf(Blocks.END_PORTAL_FRAME, b, 1, aX, seg, aZ, "End Portal", 0x40b080, player)) hits++;
                        if (addIf(ModBlocks.VOLCANO_CORE.get(), b, 1, aX, seg, aZ, "Volcano Core", 0xff4000, player)) hits++;
                        if (addIf(ModBlocks.PINK_LOG.get(), b, 1, aX, seg, aZ, "Pink Log", 0xff00ff, player)) hits++;
                        if (addIf(ModBlocks.BOBBLEHEAD.get(), b, 1, aX, seg, aZ, "A Treasure!", 0xff0000, player)) hits++;
                        if (addIf(ModBlocks.DECO_LOOT.get(), b, 1, aX, seg, aZ, null, 0x800000, player)) hits++;
                        if (addIf(ModBlocks.CRATE_AMMO.get(), b, 1, aX, seg, aZ, null, 0x800000, player)) hits++;
                        if (addIf(ModBlocks.CRATE_CAN.get(), b, 1, aX, seg, aZ, null, 0x800000, player)) hits++;
                        if (addIf(ModBlocks.ORE_BEDROCK.get(), b, 1, aX, seg, aZ, "Bedrock Ore", 0xff0000, player)) hits++;

                        if (hits > 100) return;
                    }
                }
            }
        }
    }

    private boolean addIf(Block target, Block b, int chance, int x, int y, int z, String label, int color, ServerPlayer player) {
        if (target == b && player.getRandom().nextInt(chance) == 0) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "marker");
            data.putInt("color", color);
            data.putInt("expires", 15000);
            data.putDouble("dist", 300D);
            if (label != null) data.putString("label", label);
            PacketDispatcher.sendAuxParticleNT(data, x, y, z, player);
            return true;
        }
        return false;
    }
}