package com.hbm.entity.missile;

import com.hbm.items.weapon.ItemCustomMissilePart;
import com.hbm.items.weapon.ItemCustomMissilePart.PartType;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hbm.util.ResLocation.ResLocation;

public class MissileStruct {

    public ItemCustomMissilePart warhead;
    public ItemCustomMissilePart fuselage;
    public ItemCustomMissilePart fins;
    public ItemCustomMissilePart thruster;

    public MissileStruct() { }

    public MissileStruct(ItemStack w, ItemStack f, ItemStack s, ItemStack t) {
        if (w != null && w.getItem() instanceof ItemCustomMissilePart part)
            warhead = part;
        if (f != null && f.getItem() instanceof ItemCustomMissilePart part)
            fuselage = part;
        if (s != null && s.getItem() instanceof ItemCustomMissilePart part)
            fins = part;
        if (t != null && t.getItem() instanceof ItemCustomMissilePart part)
            thruster = part;
    }

    public MissileStruct(Item w, Item f, Item s, Item t) {
        if (w instanceof ItemCustomMissilePart part)
            warhead = part;
        if (f instanceof ItemCustomMissilePart part)
            fuselage = part;
        if (s instanceof ItemCustomMissilePart part)
            fins = part;
        if (t instanceof ItemCustomMissilePart part)
            thruster = part;
    }

    public void writeToByteBuffer(ByteBuf buf) {
        writeStringToBuf(buf, warhead);
        writeStringToBuf(buf, fuselage);
        writeStringToBuf(buf, fins);
        writeStringToBuf(buf, thruster);
    }

    private void writeStringToBuf(ByteBuf buf, ItemCustomMissilePart part) {
        if (part != null) {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(part);
            String keyStr = key != null ? key.toString() : "";
            buf.writeInt(keyStr.length());
            if (!keyStr.isEmpty()) {
                buf.writeCharSequence(keyStr, java.nio.charset.StandardCharsets.UTF_8);
            }
        } else {
            buf.writeInt(0);
        }
    }

    public static MissileStruct readFromByteBuffer(ByteBuf buf) {
        MissileStruct multipart = new MissileStruct();

        multipart.warhead = readPartFromBuf(buf, PartType.WARHEAD);
        multipart.fuselage = readPartFromBuf(buf, PartType.FUSELAGE);
        multipart.fins = readPartFromBuf(buf, PartType.FINS);
        multipart.thruster = readPartFromBuf(buf, PartType.THRUSTER);

        return multipart;
    }

    private static ItemCustomMissilePart readPartFromBuf(ByteBuf buf, PartType expectedType) {
        int length = buf.readInt();
        if (length <= 0) return null;

        String keyStr = buf.readCharSequence(length, java.nio.charset.StandardCharsets.UTF_8).toString();
        Item item = ForgeRegistries.ITEMS.getValue(ResLocation(keyStr));

        if (item instanceof ItemCustomMissilePart part && part.type == expectedType) {
            return part;
        }
        return null;
    }
}
