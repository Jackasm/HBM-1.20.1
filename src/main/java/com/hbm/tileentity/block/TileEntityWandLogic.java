package com.hbm.tileentity.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockWandLogic;
import com.hbm.blocks.generic.LogicBlock;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class TileEntityWandLogic extends TileEntityLoadedBase {

    private boolean triggerReplace;

    public int placedRotation;

    public Block disguise;
    public int disguiseMeta = 0;

    public String actionID = "FODDER_WAVE";
    public String conditionID = "PLAYER_CUBE_5";
    public String interactionID;

    public TileEntityWandLogic(BlockPos pos, BlockState state) {
        super(ModTileEntity.WAND_LOGIC.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (triggerReplace) {
            replace();
        } else {
            networkPackNT(15);
        }
    }

    public void setTriggerReplace(boolean value) {
        this.triggerReplace = value;
        setChanged();
    }

    public void setDisguise(Block block, int meta) {
        this.disguise = block;
        this.disguiseMeta = meta;
        setChanged();
    }

    public void setAction(String id) { this.actionID = id; setChanged(); }
    public void setCondition(String id) { this.conditionID = id; setChanged(); }
    public void setInteraction(String id) { this.interactionID = id; setChanged(); }

    private void replace() {
        if (level == null) return;

        if (!(level.getBlockState(worldPosition).getBlock() instanceof BlockWandLogic)) {
            System.err.println("[HBM] TileEntityWandLogic at " + worldPosition + " but block isn't BlockWandLogic");
            return;
        }

        Block target = (disguise == null) ? ModBlocks.LOGIC_BLOCK_INVIS.get() : ModBlocks.LOGIC_BLOCK.get();
        level.setBlock(worldPosition, target.defaultBlockState(), 3);

        BlockEntity te = level.getBlockEntity(worldPosition);
        if (te instanceof LogicBlock.TileEntityLogicBlock logic) {
            logic.actionID = this.actionID;
            logic.conditionID = this.conditionID;
            logic.interactionID = this.interactionID;
            logic.direction = Direction.values()[placedRotation % 6];
            logic.disguise = this.disguise;
            logic.disguiseMeta = this.disguiseMeta;
            logic.setChanged();
        }
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putString("actionID", actionID);
        nbt.putString("conditionID", conditionID);
        if (interactionID != null) nbt.putString("interactionID", interactionID);
        nbt.putInt("rotation", placedRotation);
        if (disguise != null) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(disguise);
            if (id != null) nbt.putString("disguise", id.toString());
            nbt.putInt("disguiseMeta", disguiseMeta);
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        actionID = nbt.getString("actionID");
        conditionID = nbt.getString("conditionID");
        if (nbt.contains("interactionID")) interactionID = nbt.getString("interactionID");
        placedRotation = nbt.getInt("rotation");
        if (nbt.contains("disguise")) {
            disguise = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("disguise")));
            disguiseMeta = nbt.getInt("disguiseMeta");
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(placedRotation);
        writeString(buf, actionID);
        writeString(buf, conditionID);
        writeString(buf, interactionID == null ? "" : interactionID);
        buf.writeInt(Block.getId(disguise == null ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() : disguise.defaultBlockState()));
        buf.writeInt(disguiseMeta);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        placedRotation = buf.readInt();
        actionID = readString(buf);
        conditionID = readString(buf);
        interactionID = readString(buf);
        if (interactionID.isEmpty()) interactionID = null;
        disguise = Block.stateById(buf.readInt()).getBlock();
        disguiseMeta = buf.readInt();
    }

    private static void writeString(ByteBuf buf, String s) {
        buf.writeInt(s.length());
        for (char c : s.toCharArray()) buf.writeChar(c);
    }

    private static String readString(ByteBuf buf) {
        int len = buf.readInt();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(buf.readChar());
        return sb.toString();
    }
}