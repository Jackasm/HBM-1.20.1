package com.hbm.tileentity.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.NukeCustom;
import com.hbm.inventory.container.ContainerNukeCustom;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.BufPacket;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.ModTileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class TileEntityNukeCustom extends BlockEntity implements MenuProvider, IBufPacketReceiver {

    public float tnt;
    public float nuke;
    public float hydro;
    public float amat;
    public float dirty;
    public float schrab;
    public float euph;

    private Component customName;
    private boolean needsUpdate = true;

    private ByteBuf lastPackedBuf = null;
    private int updateCounter = 0;

    private final ItemStackHandler inventory = new ItemStackHandler(27) {
        @Override
        protected void onContentsChanged(int slot) {
            needsUpdate = true;
            setChanged();
            syncToClient();
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityNukeCustom(BlockPos pos, BlockState state) {
        super(ModTileEntity.NUKE_CUSTOM.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return customName != null ? customName : Component.translatable("container.nukeCustom");
    }

    public void setCustomName(Component name) {
        this.customName = name;
        setChanged();
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            if (needsUpdate) {
                recalcValues();
                needsUpdate = false;
            }

            if (++updateCounter >= 10) {
                updateCounter = 0;
                networkPackNT(50);
            }
        }
    }

    private void recalcValues() {
        float tnt = 0F, tntMod = 1F;
        float nuke = 0F, nukeMod = 1F;
        float hydro = 0F, hydroMod = 1F;
        float amat = 0F, amatMod = 1F;
        float dirty = 0F, dirtyMod = 1F;
        float schrab = 0F, schrabMod = 1F;
        float euph = 0F;

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            ComparableStack comp = new ComparableStack(stack).makeSingular();
            CustomNukeEntry ent = entries.get(comp);

            if (ent == null) continue;

            if (ent.entry == EnumEntryType.ADD) {
                switch (ent.type) {
                    case TNT -> tnt += ent.value * stack.getCount();
                    case NUKE -> nuke += ent.value * stack.getCount();
                    case HYDRO -> hydro += ent.value * stack.getCount();
                    case AMAT -> amat += ent.value * stack.getCount();
                    case DIRTY -> dirty += ent.value * stack.getCount();
                    case SCHRAB -> schrab += ent.value * stack.getCount();
                    case EUPH -> euph += ent.value * stack.getCount();
                }
            } else if (ent.entry == EnumEntryType.MULT) {
                switch (ent.type) {
                    case TNT -> tntMod *= (float) Math.pow(ent.value, stack.getCount());
                    case NUKE -> nukeMod *= (float) Math.pow(ent.value, stack.getCount());
                    case HYDRO -> hydroMod *= (float) Math.pow(ent.value, stack.getCount());
                    case AMAT -> amatMod *= (float) Math.pow(ent.value, stack.getCount());
                    case DIRTY -> dirtyMod *= (float) Math.pow(ent.value, stack.getCount());
                    case SCHRAB -> schrabMod *= (float) Math.pow(ent.value, stack.getCount());
                    default -> {}
                }
            }
        }

        tnt *= tntMod;
        nuke *= nukeMod;
        hydro *= hydroMod;
        amat *= amatMod;
        dirty *= dirtyMod;
        schrab *= schrabMod;

        if (tnt < 16) nuke = 0;
        if (nuke < 100) hydro = 0;
        if (nuke < 50) amat = 0;
        if (nuke < 50) schrab = 0;
        if (schrab == 0) euph = 0;

        this.tnt = tnt;
        this.nuke = nuke;
        this.hydro = hydro;
        this.amat = amat;
        this.dirty = dirty;
        this.schrab = schrab;
        this.euph = euph;
    }

    public float getNukeAdj() {
        if (nuke == 0) return 0;
        return Math.min(nuke + tnt / 2, NukeCustom.maxNuke);
    }

    public float getHydroAdj() {
        if (hydro == 0) return 0;
        return Math.min(hydro + nuke / 2 + tnt / 4, NukeCustom.maxHydro);
    }

    public float getAmatAdj() {
        if (amat == 0) return 0;
        return Math.min(amat + hydro / 2 + nuke / 4 + tnt / 8, NukeCustom.maxAmat);
    }

    public float getSchrabAdj() {
        if (schrab == 0) return 0;
        return Math.min(schrab + amat / 2 + hydro / 4 + nuke / 8 + tnt / 16, NukeCustom.maxSchrab);
    }

    public boolean isFalling() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.CUSTOM_FALL.get())
                return true;
        }
        return false;
    }

    // ============ NBT ============

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));

        this.tnt = nbt.getFloat("tnt");
        this.nuke = nbt.getFloat("nuke");
        this.hydro = nbt.getFloat("hydro");
        this.amat = nbt.getFloat("amat");
        this.dirty = nbt.getFloat("dirty");
        this.schrab = nbt.getFloat("schrab");
        this.euph = nbt.getFloat("euph");

        if (nbt.contains("name", Tag.TAG_STRING)) {
            customName = Component.Serializer.fromJson(nbt.getString("name"));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());

        nbt.putFloat("tnt", tnt);
        nbt.putFloat("nuke", nuke);
        nbt.putFloat("hydro", hydro);
        nbt.putFloat("amat", amat);
        nbt.putFloat("dirty", dirty);
        nbt.putFloat("schrab", schrab);
        nbt.putFloat("euph", euph);

        if (customName != null) {
            nbt.putString("name", Component.Serializer.toJson(customName));
        }
    }

    // ============ CAPABILITIES ============

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
    }

    // ============ GUI ============

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerNukeCustom(windowId, inv, this);
    }

    // ============ RENDER ============

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    // ============ BOMB REGISTRATION ============

    public static final HashMap<ComparableStack, CustomNukeEntry> entries = new HashMap<>();

    public static void registerBombItems() {
        entries.put(new ComparableStack(Items.GUNPOWDER), new CustomNukeEntry(EnumBombType.TNT, 0.8F));
        entries.put(new ComparableStack(Blocks.TNT), new CustomNukeEntry(EnumBombType.TNT, 4F));
        entries.put(new ComparableStack(ModBlocks.DET_CORD.get()), new CustomNukeEntry(EnumBombType.TNT, 1.5F));
        entries.put(new ComparableStack(ModItems.INGOT_SEMTEX.get()), new CustomNukeEntry(EnumBombType.TNT, 8F));
        entries.put(new ComparableStack(ModBlocks.DET_CHARGE.get()), new CustomNukeEntry(EnumBombType.TNT, 15F));
        entries.put(new ComparableStack(ModBlocks.RED_BARREL.get()), new CustomNukeEntry(EnumBombType.TNT, 2.5F));
        entries.put(new ComparableStack(ModBlocks.PINK_BARREL.get()), new CustomNukeEntry(EnumBombType.TNT, 4F));
        entries.put(new ComparableStack(ModItems.CUSTOM_TNT.get()), new CustomNukeEntry(EnumBombType.TNT, 10F));

        entries.put(new ComparableStack(ModItems.INGOT_U235.get()), new CustomNukeEntry(EnumBombType.NUKE, 15F));
        entries.put(new ComparableStack(ModItems.INGOT_PU239.get()), new CustomNukeEntry(EnumBombType.NUKE, 25F));
        entries.put(new ComparableStack(ModItems.INGOT_NEPTUNIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 30F));
        entries.put(new ComparableStack(ModItems.NUGGET_U235.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.5F));
        entries.put(new ComparableStack(ModItems.NUGGET_PU239.get()), new CustomNukeEntry(EnumBombType.NUKE, 2.5F));
        entries.put(new ComparableStack(ModItems.NUGGET_NEPTUNIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 3.0F));
        entries.put(new ComparableStack(ModItems.POWDER_NEPTUNIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 30F));
        entries.put(new ComparableStack(ModItems.CUSTOM_NUKE.get()), new CustomNukeEntry(EnumBombType.NUKE, 30F));

        entries.put(new ComparableStack(ModItems.CELL_DEUTERIUM.get()), new CustomNukeEntry(EnumBombType.HYDRO, 20F));
        entries.put(new ComparableStack(ModItems.CELL_TRITIUM.get()), new CustomNukeEntry(EnumBombType.HYDRO, 30F));
        entries.put(new ComparableStack(ModItems.LITHIUM.get()), new CustomNukeEntry(EnumBombType.HYDRO, 20F));
        entries.put(new ComparableStack(ModItems.TRITIUM_DEUTERIUM_CAKE.get()), new CustomNukeEntry(EnumBombType.HYDRO, 200F));
        entries.put(new ComparableStack(ModItems.CUSTOM_HYDRO.get()), new CustomNukeEntry(EnumBombType.HYDRO, 30F));

        entries.put(new ComparableStack(ModItems.CELL_ANTIMATTER.get()), new CustomNukeEntry(EnumBombType.AMAT, 5F));
        entries.put(new ComparableStack(ModItems.CUSTOM_AMAT.get()), new CustomNukeEntry(EnumBombType.AMAT, 15F));
        entries.put(new ComparableStack(ModItems.EGG_BALEFIRE_SHARD.get()), new CustomNukeEntry(EnumBombType.AMAT, 15F));
        entries.put(new ComparableStack(ModItems.EGG_BALEFIRE.get()), new CustomNukeEntry(EnumBombType.AMAT, 150F));

        entries.put(new ComparableStack(ModItems.INGOT_TUNGSTEN.get()), new CustomNukeEntry(EnumBombType.DIRTY, 1F));
        entries.put(new ComparableStack(ModItems.CUSTOM_DIRTY.get()), new CustomNukeEntry(EnumBombType.DIRTY, 10F));

        entries.put(new ComparableStack(ModItems.INGOT_SCHRABIDIUM.get()), new CustomNukeEntry(EnumBombType.SCHRAB, 5F));
        entries.put(new ComparableStack(ModBlocks.BLOCK_SCHRABIDIUM.get()), new CustomNukeEntry(EnumBombType.SCHRAB, 50F));
        entries.put(new ComparableStack(ModItems.NUGGET_SCHRABIDIUM.get()), new CustomNukeEntry(EnumBombType.SCHRAB, 0.5F));
        entries.put(new ComparableStack(ModItems.POWDER_SCHRABIDIUM.get()), new CustomNukeEntry(EnumBombType.SCHRAB, 5F));
        entries.put(new ComparableStack(ModItems.CELL_SAS3.get()), new CustomNukeEntry(EnumBombType.SCHRAB, 7.5F));
        entries.put(new ComparableStack(ModItems.CELL_ANTI_SCHRABIDIUM.get()), new CustomNukeEntry(EnumBombType.SCHRAB, 15F));
        entries.put(new ComparableStack(ModItems.CUSTOM_SCHRAB.get()), new CustomNukeEntry(EnumBombType.SCHRAB, 15F));

        entries.put(new ComparableStack(ModItems.NUGGET_EUPHEMIUM.get()), new CustomNukeEntry(EnumBombType.EUPH, 1F));
        entries.put(new ComparableStack(ModItems.INGOT_EUPHEMIUM.get()), new CustomNukeEntry(EnumBombType.EUPH, 1F));

        entries.put(new ComparableStack(Items.REDSTONE), new CustomNukeEntry(EnumBombType.TNT, 1.05F, EnumEntryType.MULT));
        entries.put(new ComparableStack(Blocks.REDSTONE_BLOCK), new CustomNukeEntry(EnumBombType.TNT, 1.5F, EnumEntryType.MULT));

        entries.put(new ComparableStack(ModItems.INGOT_URANIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.05F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.INGOT_PLUTONIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.15F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.INGOT_U238.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.1F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.INGOT_PU238.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.15F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.NUGGET_URANIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.005F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.NUGGET_PLUTONIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.015F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.NUGGET_U238.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.01F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.NUGGET_PU238.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.015F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.POWDER_URANIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.05F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.POWDER_PLUTONIUM.get()), new CustomNukeEntry(EnumBombType.NUKE, 1.15F, EnumEntryType.MULT));

        entries.put(new ComparableStack(ModItems.INGOT_PU240.get()), new CustomNukeEntry(EnumBombType.DIRTY, 1.05F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModItems.NUCLEAR_WASTE.get()), new CustomNukeEntry(EnumBombType.DIRTY, 1.025F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModBlocks.BLOCK_WASTE.get()), new CustomNukeEntry(EnumBombType.DIRTY, 1.25F, EnumEntryType.MULT));
        entries.put(new ComparableStack(ModBlocks.YELLOW_BARREL.get()), new CustomNukeEntry(EnumBombType.DIRTY, 1.2F, EnumEntryType.MULT));
    }

    public void networkPackNT(int range) {
        if (level == null || level.isClientSide) return;

        ByteBuf buf = Unpooled.buffer();
        this.serialize(buf);

        boolean shouldSend = true;

        if (lastPackedBuf != null && buf.equals(lastPackedBuf) && level.getGameTime() % 20 != 0) {
            shouldSend = false;
        }

        if (shouldSend) {
            this.lastPackedBuf = buf.copy();

            BufPacket packet = new BufPacket(worldPosition, this);
            PacketDispatcher.sendToAllAround(packet, level, worldPosition, range);
        }
    }

    public void serialize(ByteBuf buf) {
        // Поля бомбы
        buf.writeFloat(this.tnt);
        buf.writeFloat(this.nuke);
        buf.writeFloat(this.hydro);
        buf.writeFloat(this.amat);
        buf.writeFloat(this.dirty);
        buf.writeFloat(this.schrab);
        buf.writeFloat(this.euph);
    }

    public void deserialize(ByteBuf buf) {
        this.tnt = buf.readFloat();
        this.nuke = buf.readFloat();
        this.hydro = buf.readFloat();
        this.amat = buf.readFloat();
        this.dirty = buf.readFloat();
        this.schrab = buf.readFloat();
        this.euph = buf.readFloat();

        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ============ ENUMS ============

    public enum EnumBombType {
        TNT("TNT"),
        NUKE("Nuclear"),
        HYDRO("Hydrogen"),
        AMAT("Antimatter"),
        DIRTY("Salted"),
        SCHRAB("Schrabidium"),
        EUPH("Anti Mass");

        final String name;

        EnumBombType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum EnumEntryType {
        ADD,
        MULT
    }

    public static class CustomNukeEntry {
        public final EnumBombType type;
        public EnumEntryType entry;
        public final float value;

        public CustomNukeEntry(EnumBombType type, float value) {
            this.type = type;
            this.entry = EnumEntryType.ADD;
            this.value = value;
        }

        public CustomNukeEntry(EnumBombType type, float value, EnumEntryType entry) {
            this(type, value);
            this.entry = entry;
        }
    }
}