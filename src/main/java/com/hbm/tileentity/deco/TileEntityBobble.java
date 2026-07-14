package com.hbm.tileentity.deco;

import com.hbm.datagen.worldgen.nbt.INBTTileEntityTransformable;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.HBMEnums.ScrapType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityBobble extends BlockEntity implements IForgeBlockEntity, INBTTileEntityTransformable, MenuProvider {

    public BobbleType type = BobbleType.NONE;

    public TileEntityBobble(BlockPos pos, BlockState state) {
        super(ModTileEntity.BOBBLE.get(), pos, state);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        int typeOrdinal = nbt.getByte("type");
        if (typeOrdinal >= 0 && typeOrdinal < BobbleType.values().length) {
            this.type = BobbleType.values()[typeOrdinal];
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putByte("type", (byte) type.ordinal());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        nbt.putByte("type", (byte) type.ordinal());
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Override
    public void transformTE(Level world, int coordBaseMode) {
        type = BobbleType.values()[world.random.nextInt(BobbleType.values().length - 1) + 1];
        setChanged();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.@NotNull Inventory inv, @NotNull Player player) {
        return null;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.bobble");
    }

    public static enum BobbleType {
        NONE("null", "null", null, null, false, ScrapType.BOARD_BLANK),
        STRENGTH("Strength", "Strength", null, "It's essential to give your arguments impact.", false, ScrapType.BRIDGE_BIOS),
        PERCEPTION("Perception", "Perception", null, "Only through observation will you perceive weakness.", false, ScrapType.BRIDGE_NORTH),
        ENDURANCE("Endurance", "Endurance", null, "Always be ready to take one for the team.", false, ScrapType.BRIDGE_SOUTH),
        CHARISMA("Charisma", "Charisma", null, "Nothing says pizzaz like a winning smile.", false, ScrapType.BRIDGE_IO),
        INTELLIGENCE("Intelligence", "Intelligence", null, "It takes the smartest individuals to realize there's always more to learn.", false, ScrapType.BRIDGE_BUS),
        AGILITY("Agility", "Agility", null, "Never be afraid to dodge the sensitive issues.", false, ScrapType.BRIDGE_CHIPSET),
        LUCK("Luck", "Luck", null, "There's only one way to give 110%.", false, ScrapType.BRIDGE_CMOS),
        BOB("Robert \"The Bobcat\" Katzinsky", "HbMinecraft", "Hbm's Nuclear Tech Mod", "I know where you live, " + System.getProperty("user.name"), false, ScrapType.CPU_SOCKET),
        FRIZZLE("Frooz", "Frooz", "Weapon models", "BLOOD IS FUEL", true, ScrapType.CPU_CLOCK),
        PU238("Pu-238", "Pu-238", "Improved Tom impact mechanics", null, false, ScrapType.CPU_REGISTER),
        VT("VT-6/24", "VT-6/24", "Balefire warhead model and general texturework", "You cannot unfuck a horse.", true, ScrapType.CPU_EXT),
        DOC("The Doctor", "Doctor17PH", "Russian localization, lunar miner", "Perhaps the moon rocks were too expensive", true, ScrapType.CPU_CACHE),
        BLUEHAT("The Blue Hat", "The Blue Hat", "Textures", "payday 2's deagle freeaim champ of the year 2022", true, ScrapType.MEM_16K_A),
        PHEO("Pheo", "Pheonix", "Deuterium machines, tantalium textures, Reliant Rocket", "RUN TO THE BEDROOM, ON THE SUITCASE ON THE LEFT, YOU'LL FIND MY FAVORITE AXE", true, ScrapType.MEM_16K_B),
        ADAM29("Adam29", "Adam29", "Ethanol, liquid petroleum gas", "You know, nukes are really quite beautiful. It's like watching a star be born for a split second.", true, ScrapType.MEM_16K_C),
        UFFR("UFFR", "UFFR", "All sorts of things from his PR", "fried shrimp", false, ScrapType.MEM_SOCKET),
        VAER("vaer", "vaer", "ZIRNOX", "taken de family out to the weekend cigarette festival", true, ScrapType.MEM_16K_D),
        NOS("Dr Nostalgia", "Dr Nostalgia", "SSG and Vortex models", "Take a picture, I'ma pose, paparazzi I've been drinking, moving like a zombie", true, ScrapType.BOARD_TRANSISTOR),
        DRILLGON("Drillgon200", "Drillgon200", "1.12 Port", null, false, ScrapType.CPU_LOGIC),
        CIRNO("Cirno", "Cirno", "the only multi layered skin i had", "No brain. Head empty.", true, ScrapType.BOARD_BLANK),
        MICROWAVE("Microwave", "Microwave", "OC Compatibility and massive RBMK/packet optimizations", "they call me the food heater john optimization", true, ScrapType.BOARD_CONVERTER),
        PEEP("Peep", "LePeeperSauvage", "Coilgun, Leadburster and Congo Lake models, BDCL QC", "Fluffy ears can't hide in ash, nor snow.", true, ScrapType.CARD_BOARD),
        MELLOW("MELLOWARPEGGIATION", "Mellow", "NBT Structures, industrial lighting, animation tools", "Make something cool now, ask for permission later.", true, ScrapType.CARD_PROCESSOR),
        ABEL("Abel1502", "Abel1502", "Abilities GUI, optimizations and many QoL improvements", "NANTO SUBARASHII", true, ScrapType.CPU_REGISTER);

        public final String name;
        public final String label;
        public final String contribution;
        public final String inscription;
        public final boolean skinLayers;
        public final ScrapType scrap;

        BobbleType(String name, String label, String contribution, String inscription, boolean layers, ScrapType scrap) {
            this.name = name;
            this.label = label;
            this.contribution = contribution;
            this.inscription = inscription;
            this.skinLayers = layers;
            this.scrap = scrap;
        }
    }
}
