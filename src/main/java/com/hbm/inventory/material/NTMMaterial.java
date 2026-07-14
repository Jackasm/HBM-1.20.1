package com.hbm.inventory.material;

import com.hbm.items.DictFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class NTMMaterial {

    public final int id;
    public String[] names;
    public Set<MaterialShapes> autogen = new HashSet<>();
    public Set<MatTraits> traits = new HashSet<>();
    public SmeltingBehavior smeltable = SmeltingBehavior.NOT_SMELTABLE;
    public int solidColorLight = 0xFF4A00;
    public int solidColorDark = 0x802000;
    public int moltenColor = 0xFF4A00;

    public NTMMaterial smeltsInto;
    public int convIn;
    public int convOut;

    public NTMMaterial(int id, DictFrame dict) {
        this.names = dict.mats;
        this.id = id;

        this.smeltsInto = this;
        this.convIn = 1;
        this.convOut = 1;

        for (String name : dict.mats) {
            Mats.matByName.put(name, this);
        }

        Mats.orderedList.add(this);
        Mats.matById.put(id, this);
    }

    public String getUnlocalizedName() {
        return "hbmmat." + this.names[0].toLowerCase(Locale.US);
    }

    public NTMMaterial setConversion(NTMMaterial mat, int in, int out) {
        this.smeltsInto = mat;
        this.convIn = in;
        this.convOut = out;
        return this;
    }

    public NTMMaterial setAutogen(MaterialShapes... shapes) {
        Collections.addAll(this.autogen, shapes);
        return this;
    }

    public NTMMaterial setTraits(MatTraits... traits) {
        Collections.addAll(this.traits, traits);
        return this;
    }

    public NTMMaterial m() { this.traits.add(MatTraits.METAL); return this; }
    public NTMMaterial n() { this.traits.add(MatTraits.NONMETAL); return this; }

    public NTMMaterial smeltable(SmeltingBehavior behavior) {
        this.smeltable = behavior;
        return this;
    }

    public NTMMaterial setSolidColor(int colorLight, int colorDark) {
        this.solidColorLight = colorLight;
        this.solidColorDark = colorDark;
        return this;
    }

    public NTMMaterial setMoltenColor(int color) {
        this.moltenColor = color;
        return this;
    }

    public ItemStack make(Item item, int amount) {
        return new ItemStack(item, amount);
    }

    public ItemStack make(Item item) {
        return make(item, 1);
    }

    public enum SmeltingBehavior {
        NOT_SMELTABLE,
        VAPORIZES,
        BREAKS,
        SMELTABLE,
        ADDITIVE
    }

    public enum MatTraits {
        METAL,
        NONMETAL
    }
}