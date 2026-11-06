package com.hbm.items.special;

import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScramblingName {

    public String previous;
    public String next;
    public String[] previousFrags;
    public String[] nextFrags;
    public String[] frags;
    public int[] mask;
    public int age = 0;

    private static final Random RAND = new Random();

    public ScramblingName(String init) {
        previous = next = init;
        frags = init.split("");
        mask = new int[frags.length];
        previousFrags = chop(previous, frags.length);
        nextFrags = chop(next, frags.length);
    }

    public String getResult() {
        return String.join("", frags);
    }

    public void updateTick(String[] nextNames) {
        age++;
        try {
            if (age % 200 == 0) nextName(nextNames);
            if (age % 5 == 0) scramble();
        } catch (Exception ignored) {}
    }

    public void nextName(String[] nextNames) {
        if (nextNames.length < 2) return;

        this.previous = this.next;

        String initial = next;
        while (initial.equals(next)) {
            next = nextNames[RAND.nextInt(nextNames.length)];
        }

        int length = Math.min(previous.length(), next.length());
        this.previousFrags = chop(previous, length);
        this.frags = chop(previous, length);
        this.nextFrags = chop(next, length);
        mask = new int[length];
    }

    public void scramble() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < mask.length; i++) {
            int m = mask[i];
            if (m == 0) indices.add(i);
            if (m > 0 && m <= 5) mask[i]++;
            if (m > 5) frags[i] = nextFrags[i];
        }
        if (!indices.isEmpty()) {
            int toSwitch = indices.get(RAND.nextInt(indices.size()));
            mask[toSwitch] = 1;
            frags[toSwitch] = ChatFormatting.OBFUSCATED + previousFrags[toSwitch] + ChatFormatting.RESET;
        }
    }

    public String[] chop(String name, int parts) {
        if (parts == name.length()) return name.split("");
        double index = 0;
        double incrementPerStep = (double) name.length() / (double) parts;
        List<String> slices = new ArrayList<>();
        for (int i = 0; i < parts; i++) {
            int end = (i == parts - 1) ? name.length() : (int) (index + incrementPerStep);
            slices.add(name.substring((int) index, end));
            index += incrementPerStep;
        }
        return slices.toArray(new String[parts]);
    }
}