package com.hbm.world.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.WorldConfig;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Meteorite {

    public static boolean safeMode = false;

    public void generate(Level world, RandomSource rand, int x, int y, int z, boolean safe, boolean allowSpecials, boolean damagingImpact) {
        safeMode = safe;

        if (replacables.isEmpty()) {
            generateReplacables();
        }

        if (damagingImpact) {
            AABB box = new AABB(
                    x - 7.5, y - 7.5, z - 7.5,
                    x + 7.5, y + 7.5, z + 7.5
            );
            List<Entity> list = world.getEntities(null, box);

            for (Entity e : list) {
                e.hurt(ModDamageSource.causeMeteoriteDamage(e.level()), 1000);
            }
        }

        if (WorldConfig.ENABLE_SPECIAL_METEOR && allowSpecials) {
            switch (rand.nextInt(300)) {
                case 0:
                    // Meteor-only tiny meteorite
                    List<ItemStack> list0 = new ArrayList<>();
                    list0.add(new ItemStack(ModBlocks.BLOCK_METEOR.get()));
                    generateBox(world, rand, x, y, z, list0);
                    return;
                case 1:
                    // Large ore-only meteorite
                    List<ItemStack> list1 = new ArrayList<>(this.getRandomOre(rand));
                    int i = list1.size();
                    for (int j = 0; j < i; j++)
                        list1.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                    generateSphere7x7(world, rand, x, y, z, list1);
                    return;
                case 2:
                    // Medium ore-only meteorite
                    List<ItemStack> list2 = new ArrayList<>(this.getRandomOre(rand));
                    int k = list2.size() / 2;
                    for (int j = 0; j < k; j++)
                        list2.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                    generateSphere5x5(world, rand, x, y, z, list2);
                    return;
                case 3:
                    // Small pure ore meteorite
                    List<ItemStack> list3 = new ArrayList<>(this.getRandomOre(rand));
                    generateBox(world, rand, x, y, z, list3);
                    return;
                case 4:
                    // Bamboozle
                    world.explode(null, x + 0.5, y + 0.5, z + 0.5, 15F, !safe, Level.ExplosionInteraction.TNT);
                    ExplosionLarge.spawnRubble(world, x, y, z, 25);
                    return;
                case 5:
                    // Large treasure-only meteorite
                    List<ItemStack> list4 = new ArrayList<>();
                    list4.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                    list4.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                    generateSphere7x7(world, rand, x, y, z, list4);
                    return;
                case 6:
                    // Medium treasure-only meteorite
                    List<ItemStack> list5 = new ArrayList<>();
                    list5.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                    list5.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                    list5.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                    generateSphere5x5(world, rand, x, y, z, list5);
                    return;
                case 7:
                    // Small pure treasure meteorite
                    List<ItemStack> list6 = new ArrayList<>();
                    list6.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                    generateBox(world, rand, x, y, z, list6);
                    return;
                case 8:
                    // Large nuclear meteorite
                    List<ItemStack> list7 = new ArrayList<>();
                    list7.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                    List<ItemStack> list8 = new ArrayList<>();
                    list8.add(new ItemStack(ModBlocks.TOXIC_BLOCK.get()));
                    generateSphere7x7(world, rand, x, y, z, list7);
                    generateSphere5x5(world, rand, x, y, z, list8);
                    return;
                case 9:
                    // Giant ore meteorite
                    List<ItemStack> list9 = new ArrayList<>();
                    list9.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                    generateSphere9x9(world, rand, x, y, z, list9);
                    generateSphere7x7(world, rand, x, y, z, this.getRandomOre(rand));
                    return;
                case 10:
                    // Tainted Meteorite
                    List<ItemStack> list10 = new ArrayList<>();
                    list10.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                    generateSphere5x5(world, rand, x, y, z, list10);
                    setBlock(world, x, y, z, ModBlocks.TAINT.get(), 9, 2);
                    return;
            }
        }

        switch (rand.nextInt(3)) {
            case 0:
                generateLarge(world, rand, x, y, z);
                break;
            case 1:
                generateMedium(world, rand, x, y, z);
                break;
            case 2:
                generateSmall(world, rand, x, y, z);
                break;
        }
    }

    public void generateLarge(Level world, RandomSource rand, int x, int y, int z) {
        // 0 - Molten
        // 1 - Cobble
        // 2 - Broken
        // 3 - Mix
        int hull = rand.nextInt(4);

        // 0 - Cobble
        // 1 - Broken
        // 2 - Mix
        int outerPadding = 0;

        if(hull == 2)
            outerPadding = 1 + rand.nextInt(2);
        else if(hull == 3)
            outerPadding = 2;

        // 0 - Broken
        // 1 - Stone
        // 2 - Netherrack
        int innerPadding = rand.nextInt(hull == 0 ? 3 : 2);

        // 0 - Meteor
        // 1 - Treasure
        // 2 - Ore
        int core = rand.nextInt(2);
        if(innerPadding > 0)
            core = 2;

        List<ItemStack> hullL = new ArrayList<>();
        switch(hull) {
            case 0:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_MOLTEN.get()));
                break;
            case 1:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                break;
            case 2:
                for(int i = 0; i < 99; i++)
                    hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 3:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_MOLTEN.get()));
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                break;
        }

        List<ItemStack> opL = new ArrayList<>();
        switch(outerPadding) {
            case 0:
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                break;
            case 1:
                for(int i = 0; i < 99; i++)
                    opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 2:
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                break;
        }

        List<ItemStack> ipL = new ArrayList<>();
        switch(innerPadding) {
            case 0:
                for(int i = 0; i < 99; i++)
                    ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 1:
                ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                break;
            case 2:
                ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                break;
        }

        List<ItemStack> coreL = new ArrayList<>();
        switch(core) {
            case 0:
                coreL.add(new ItemStack(ModBlocks.BLOCK_METEOR.get()));
                break;
            case 1:
                coreL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 2:
                coreL.addAll(this.getRandomOre(rand));
                break;
        }

        switch(rand.nextInt(5)) {
            case 0:
                genL1(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 1:
                genL2(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 2:
                genL3(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 3:
                genL4(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 4:
                genL5(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
        }
    }

    public void generateMedium(Level world, RandomSource rand, int x, int y, int z) {
        // 0 - Molten
        // 1 - Cobble
        // 2 - Broken
        // 3 - Mix
        int hull = rand.nextInt(4);

        // 0 - Cobble
        // 1 - Broken
        // 2 - Mix
        int outerPadding = 0;

        if(hull == 2)
            outerPadding = 1 + rand.nextInt(2);
        else if(hull == 3)
            outerPadding = 2;

        // 0 - Broken
        // 1 - Stone
        // 2 - Netherrack
        int innerPadding = rand.nextInt(hull == 0 ? 3 : 2);

        // 0 - Meteor
        // 1 - Treasure
        // 2 - Ore
        int core = rand.nextInt(2);
        if(innerPadding > 0)
            core = 2;

        List<ItemStack> hullL = new ArrayList<>();
        switch(hull) {
            case 0:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_MOLTEN.get()));
                break;
            case 1:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                break;
            case 2:
                for(int i = 0; i < 99; i++)
                    hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 3:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_MOLTEN.get()));
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                break;
        }

        List<ItemStack> opL = new ArrayList<>();
        switch(outerPadding) {
            case 0:
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                break;
            case 1:
                for(int i = 0; i < 99; i++)
                    opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 2:
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                opL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                break;
        }

        List<ItemStack> ipL = new ArrayList<>();
        switch(innerPadding) {
            case 0:
                for(int i = 0; i < 99; i++)
                    ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 1:
                ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                break;
            case 2:
                ipL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                break;
        }

        List<ItemStack> coreL = new ArrayList<>();
        switch(core) {
            case 0:
                coreL.add(new ItemStack(ModBlocks.BLOCK_METEOR.get()));
                break;
            case 1:
                coreL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 2:
                coreL.addAll(this.getRandomOre(rand));
                break;
        }

        List<ItemStack> sCore = new ArrayList<>();
        switch(core) {
            case 0:
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR.get()));
                break;
            case 1:
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 2:
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR.get()));
                break;
        }

        switch(rand.nextInt(6)) {
            case 0:
                genM1(world, rand, x, y, z, hullL, opL, ipL, sCore);
                break;
            case 1:
                genM2(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 2:
                genM3(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 3:
                genM4(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 4:
                genM5(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
            case 5:
                genM6(world, rand, x, y, z, hullL, opL, ipL, coreL);
                break;
        }
    }

    public void generateSmall(Level world, RandomSource rand, int x, int y, int z) {
        // 0 - Molten
        // 1 - Cobble
        // 2 - Broken
        // 3 - Mix
        int hull = rand.nextInt(4);

        // 0 - Meteor
        // 1 - Treasure
        // 2 - Ore
        int core = rand.nextInt(3);

        List<ItemStack> hullL = new ArrayList<>();
        switch(hull) {
            case 0:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_MOLTEN.get()));
                break;
            case 1:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_COBBLE.get()));
                break;
            case 2:
                for(int i = 0; i < 99; i++)
                    hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 3:
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_MOLTEN.get()));
                hullL.add(new ItemStack(ModBlocks.BLOCK_METEOR_BROKEN.get()));
                break;
        }

        List<ItemStack> sCore = new ArrayList<>();
        switch(core) {
            case 0:
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR.get()));
                break;
            case 1:
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                break;
            case 2:
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR_TREASURE.get()));
                sCore.add(new ItemStack(ModBlocks.BLOCK_METEOR.get()));
                break;
        }

        generateBox(world, rand, x, y, z, hullL);
        ItemStack stack = sCore.get(rand.nextInt(sCore.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genL1(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere7x7(world, rand, x, y, z, hull);
        generateStar5x5(world, rand, x, y, z, op);
        generateStar3x3(world, rand, x, y, z, ip);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genL2(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere7x7(world, rand, x, y, z, hull);
        generateSphere5x5(world, rand, x, y, z, op);
        generateStar3x3(world, rand, x, y, z, ip);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genL3(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere7x7(world, rand, x, y, z, hull);
        generateSphere5x5(world, rand, x, y, z, op);
        generateBox(world, rand, x, y, z, ip);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genL4(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere7x7(world, rand, x, y, z, hull);
        generateSphere5x5(world, rand, x, y, z, op);
        generateBox(world, rand, x, y, z, ip);
        generateStar3x3(world, rand, x, y, z, this.getRandomOre(rand));
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genL5(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere7x7(world, rand, x, y, z, hull);
        generateSphere5x5(world, rand, x, y, z, op);
        generateStar5x5(world, rand, x, y, z, ip);
        generateStar3x3(world, rand, x, y, z, this.getRandomOre(rand));
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genM1(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere5x5(world, rand, x, y, z, hull);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genM2(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere5x5(world, rand, x, y, z, hull);
        generateStar3x3(world, rand, x, y, z, op);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genM3(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere5x5(world, rand, x, y, z, hull);
        generateBox(world, rand, x, y, z, op);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genM4(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere5x5(world, rand, x, y, z, hull);
        generateBox(world, rand, x, y, z, op);
        generateStar3x3(world, rand, x, y, z, ip);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genM5(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere5x5(world, rand, x, y, z, hull);
        generateBox(world, rand, x, y, z, ip);
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void genM6(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> hull, List<ItemStack> op, List<ItemStack> ip, List<ItemStack> core) {
        generateSphere5x5(world, rand, x, y, z, hull);
        generateBox(world, rand, x, y, z, ip);
        generateStar3x3(world, rand, x, y, z, this.getRandomOre(rand));
        ItemStack stack = core.get(rand.nextInt(core.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void generateSphere7x7(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> set) {
        for(int a = -3; a < 4; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -1; a < 2; a++)
            for(int b = -3; b < 4; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -1; a < 2; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -3; c < 4; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }

        for(int a = -2; a < 3; a++)
            for(int b = -2; b < 3; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -1; a < 2; a++)
            for(int b = -2; b < 3; b++)
                for(int c = -2; c < 3; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -2; a < 3; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -2; c < 3; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
    }

    public void generateSphere5x5(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> set) {
        for(int a = -2; a < 3; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -1; a < 2; a++)
            for(int b = -2; b < 3; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -1; a < 2; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -2; c < 3; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
    }

    public void generateSphere9x9(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> set) {
        for(int a = -4; a < 5; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -1; a < 2; a++)
            for(int b = -4; b < 5; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -1; a < 2; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -4; c < 5; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }

        for(int a = -1; a < 2; a++)
            for(int b = -3; b < 4; b++)
                for(int c = -3; c < 4; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -3; a < 4; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -3; c < 4; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -3; a < 4; a++)
            for(int b = -3; b < 4; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }

        for(int a = -3; a < 4; a++)
            for(int b = -2; b < 3; b++)
                for(int c = -2; c < 3; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -2; a < 3; a++)
            for(int b = -3; b < 4; b++)
                for(int c = -2; c < 3; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
        for(int a = -2; a < 3; a++)
            for(int b = -2; b < 3; b++)
                for(int c = -3; c < 4; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
    }

    public void generateBox(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> set) {
        for(int a = -1; a < 2; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }
    }

    public void generateStar5x5(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> set) {
        for(int a = -1; a < 2; a++)
            for(int b = -1; b < 2; b++)
                for(int c = -1; c < 2; c++) {
                    ItemStack stack = set.get(rand.nextInt(set.size()));
                    setBlock(world, x + a, y + b, z + c, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
                }

        ItemStack stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x + 2, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x - 2, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y + 2, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y - 2, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y, z + 2, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y, z - 2, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    public void generateStar3x3(Level world, RandomSource rand, int x, int y, int z, List<ItemStack> set) {

        ItemStack stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x + 1, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x - 1, y, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y + 1, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y - 1, z, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y, z + 1, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
        stack = set.get(rand.nextInt(set.size()));
        setBlock(world, x, y, z - 1, Block.byItem(stack.getItem()), stack.getDamageValue(), 2);
    }

    private void setBlock(Level world, int x, int y, int z, Block b, int meta, int flag) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState targetState = world.getBlockState(pos);
        Block target = targetState.getBlock();

        if (safeMode) {
            if (!targetState.canBeReplaced() && !replacables.contains(target)) return;
        }

        float hardness = targetState.getDestroySpeed(world, pos);
        if (hardness != -1 && hardness < 10000) {
            world.setBlock(pos, b.defaultBlockState(), flag);
        }
    }

    public static HashSet<Block> replacables = new HashSet<>();

    public static void generateReplacables() {
        replacables.add(ModBlocks.BLOCK_METEOR.get());
        replacables.add(ModBlocks.BLOCK_METEOR_BROKEN.get());
        replacables.add(ModBlocks.BLOCK_METEOR_COBBLE.get());
        replacables.add(ModBlocks.BLOCK_METEOR_MOLTEN.get());
        replacables.add(ModBlocks.BLOCK_METEOR_TREASURE.get());
        replacables.add(ModBlocks.ORE_METEOR_IRON.get());
        replacables.add(ModBlocks.ORE_METEOR_COPPER.get());
        replacables.add(ModBlocks.ORE_METEOR_ALUMINIUM.get());
        replacables.add(ModBlocks.ORE_METEOR_RARE_EARTH.get());
        replacables.add(ModBlocks.ORE_METEOR_COBALT.get());
    }

    public List<ItemStack> getRandomOre(RandomSource rand) {
        List<ItemStack> ores = new ArrayList<>();
        // Собираем все метеорные руды
        ores.add(new ItemStack(ModBlocks.ORE_METEOR_IRON.get()));
        ores.add(new ItemStack(ModBlocks.ORE_METEOR_COPPER.get()));
        ores.add(new ItemStack(ModBlocks.ORE_METEOR_ALUMINIUM.get()));
        ores.add(new ItemStack(ModBlocks.ORE_METEOR_RARE_EARTH.get()));
        ores.add(new ItemStack(ModBlocks.ORE_METEOR_COBALT.get()));
        return ores;
    }
}