package com.hbm.tileentity.network;

import com.hbm.interfaces.IControlReceiver;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.ItemStackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileEntityRadioTelex extends TileEntityLoadedBase implements IControlReceiver, MenuProvider {

    public static final int lineWidth = 33;
    public String txChannel = "";
    public String rxChannel = "";
    public String[] txBuffer = new String[]{"", "", "", "", ""};
    public String[] rxBuffer = new String[]{"", "", "", "", ""};
    public int sendingLine = 0;
    public int sendingIndex = 0;
    public boolean isSending = false;
    public int sendingWait = 0;
    public int writingLine = 0;
    public boolean printAfterRx = false;
    public boolean deleteOnReceive = true;
    public char sendingChar = ' ';

    public static final char eol = '\n';
    public static final char eot = '\u0004';
    public static final char bell = '\u0007';
    public static final char print = '\u000c';
    public static final char pause = '\u0016';
    public static final char clear = '\u007f';

    public TileEntityRadioTelex(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADIO_TELEX.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        this.sendingChar = ' ';

        if (this.isSending && this.txChannel.isEmpty()) this.isSending = false;

        if (this.isSending) {
            if (sendingWait > 0) {
                sendingWait--;
            } else {
                String line = txBuffer[sendingLine];

                if (line.length() > sendingIndex) {
                    char c = line.charAt(sendingIndex);
                    sendingIndex++;
                    if (c == pause) {
                        sendingWait = 20;
                    } else {
                        RTTYSystem.broadcast(level, this.txChannel, c);
                        this.sendingChar = c;
                    }
                } else {
                    if (sendingLine >= 4) {
                        this.isSending = false;
                        RTTYSystem.broadcast(level, this.txChannel, eot);
                        this.sendingLine = 0;
                        this.sendingIndex = 0;
                    } else {
                        RTTYSystem.broadcast(level, this.txChannel, eol);
                        this.sendingLine++;
                        this.sendingIndex = 0;
                    }
                }
            }
        }

        if (!this.rxChannel.isEmpty()) {
            RTTYSystem.RTTYChannel chan = RTTYSystem.listen(level, this.rxChannel);

            if (chan != null && chan.signal instanceof Character && (chan.timeStamp > level.getGameTime() - 2 && chan.timeStamp != -1)) {
                char c = (char) chan.signal;

                if (this.deleteOnReceive) {
                    this.deleteOnReceive = false;
                    for (int i = 0; i < 5; i++) this.rxBuffer[i] = "";
                    this.writingLine = 0;
                }

                if (c == eot) {
                    if (this.printAfterRx) {
                        this.printAfterRx = false;
                        this.print();
                    }
                    this.deleteOnReceive = true;
                } else if (c == eol) {
                    if (this.writingLine < 4) this.writingLine++;
                    setChanged();
                } else if (c == bell) {
                    level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 2F, 0.5F);
                } else if (c == print) {
                    this.printAfterRx = true;
                } else if (c == clear) {
                    for (int i = 0; i < 5; i++) this.rxBuffer[i] = "";
                    this.writingLine = 0;
                } else {
                    this.rxBuffer[this.writingLine] += c;
                    setChanged();
                }
            }
        }

        networkPackNT(16);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        for (int i = 0; i < 5; i++) {
            writeString(buf, txBuffer[i]);
            writeString(buf, rxBuffer[i]);
        }
        writeString(buf, this.txChannel);
        writeString(buf, this.rxChannel);
        buf.writeChar(this.sendingChar);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        for (int i = 0; i < 5; i++) {
            txBuffer[i] = readString(buf);
            rxBuffer[i] = readString(buf);
        }
        this.txChannel = readString(buf);
        this.rxChannel = readString(buf);
        this.sendingChar = buf.readChar();
    }

    private void writeString(ByteBuf buf, String s) {
        buf.writeInt(s.length());
        for (char c : s.toCharArray()) buf.writeChar(c);
    }

    private String readString(ByteBuf buf) {
        int len = buf.readInt();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(buf.readChar());
        return sb.toString();
    }

    @Override
    public void receiveControl(CompoundTag data) {
        for (int i = 0; i < 5; i++) {
            if (data.contains("tx" + i)) this.txBuffer[i] = data.getString("tx" + i);
        }

        String cmd = data.getString("cmd");

        if ("snd".equals(cmd) && !this.isSending) {
            this.isSending = true;
            this.sendingLine = 0;
            this.sendingIndex = 0;
        }

        if ("rxprt".equals(cmd)) print();

        if ("rxcls".equals(cmd)) {
            for (int i = 0; i < 5; i++) this.rxBuffer[i] = "";
            this.writingLine = 0;
        }

        if ("sve".equals(cmd)) {
            this.txChannel = data.getString("txChan");
            this.rxChannel = data.getString("rxChan");
            setChanged();
        }
    }

    public void print() {
        ItemStack stack = new ItemStack(Items.PAPER);
        List<String> text = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (!rxBuffer[i].isEmpty()) text.add(rxBuffer[i]);
        }
        ItemStackUtil.addTooltipToStack(stack, text.toArray(new String[0]));
        stack.setHoverName(net.minecraft.network.chat.Component.literal("Message"));
        if (level != null) {
            level.addFreshEntity(new ItemEntity(level,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, stack));
        }
    }

    @Override
    public boolean hasPermission(Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 16 * 16;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        for (int i = 0; i < 5; i++) {
            nbt.putString("tx" + i, txBuffer[i]);
            nbt.putString("rx" + i, rxBuffer[i]);
        }
        nbt.putString("txChan", txChannel);
        nbt.putString("rxChan", rxChannel);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        for (int i = 0; i < 5; i++) {
            txBuffer[i] = nbt.getString("tx" + i);
            rxBuffer[i] = nbt.getString("rx" + i);
        }
        this.txChannel = nbt.getString("txChan");
        this.rxChannel = nbt.getString("rxChan");
    }

    private AABB renderBox;

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        if (renderBox == null) {
            renderBox = new AABB(
                    worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1,
                    worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2
            );
        }
        return renderBox;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.radio_telex");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new com.hbm.inventory.container.ContainerRadioTelex(id, inv, this);
    }
}