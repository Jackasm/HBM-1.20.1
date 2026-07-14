package com.hbm.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteriaTriggers {
    public static final NuclearCreeperDeathTrigger NUCLEAR_CREEPER_DEATH = new NuclearCreeperDeathTrigger();
    public static final NuclearExplosionTrigger NUCLEAR_EXPLOSION = new NuclearExplosionTrigger();
    public static final KillMaskmanTrigger KILL_MASKMAN = new KillMaskmanTrigger();
    public static final BossMeltdownTrigger BOSS_MELTDOWN = new BossMeltdownTrigger();
    public static final FiendTrigger FIEND = new FiendTrigger();
    public static final Fiend2Trigger FIEND2 = new Fiend2Trigger();
    public static final BossUFOTrigger BOSS_UFO = new BossUFOTrigger();
    public static final RadDeathTrigger RAD_DEATH = new RadDeathTrigger();
    public static final RadPoisonTrigger RAD_POISON = new RadPoisonTrigger();
    public static final HorizonsStartTrigger HORIZONS_START = new HorizonsStartTrigger();
    public static final HorizonsEndTrigger HORIZONS_END = new HorizonsEndTrigger();
    public static final FOEQTrigger FOEQ = new FOEQTrigger();

    public static final No9Trigger NO9 = new No9Trigger();
    public static final SomeWoundsTrigger SOME_WOUNDS = new SomeWoundsTrigger();
    public static final GoFishTrigger GO_FISH = new GoFishTrigger();
    public static final BlastFurnaceTrigger BLAST_FURNACE = new BlastFurnaceTrigger();
    public static final AssemblyTrigger ASSEMBLY = new AssemblyTrigger();
    public static final ChemplantTrigger CHEMPLANT = new ChemplantTrigger();
    public static final DeshTrigger DESH = new DeshTrigger();
    public static final TechnetiumTrigger TECHNETIUM = new TechnetiumTrigger();
    public static final BobHiddenTrigger BOB_HIDDEN = new BobHiddenTrigger();

    public static void register() {
        CriteriaTriggers.register(NUCLEAR_CREEPER_DEATH);
        CriteriaTriggers.register(NUCLEAR_EXPLOSION);
        CriteriaTriggers.register(KILL_MASKMAN);
        CriteriaTriggers.register(BOSS_MELTDOWN);
        CriteriaTriggers.register(FIEND);
        CriteriaTriggers.register(FIEND2);
        CriteriaTriggers.register(BOSS_UFO);
        CriteriaTriggers.register(RAD_DEATH);
        CriteriaTriggers.register(RAD_POISON);
        CriteriaTriggers.register(HORIZONS_START);
        CriteriaTriggers.register(HORIZONS_END);
        CriteriaTriggers.register(FOEQ);
        CriteriaTriggers.register(NO9);
        CriteriaTriggers.register(SOME_WOUNDS);
        CriteriaTriggers.register(GO_FISH);

        CriteriaTriggers.register(BLAST_FURNACE);
        CriteriaTriggers.register(ASSEMBLY);
        CriteriaTriggers.register(CHEMPLANT);
        CriteriaTriggers.register(DESH);
        CriteriaTriggers.register(TECHNETIUM);
        CriteriaTriggers.register(BOB_HIDDEN);
    }
}