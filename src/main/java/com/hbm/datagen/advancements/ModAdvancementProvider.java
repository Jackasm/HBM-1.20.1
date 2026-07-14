package com.hbm.datagen.advancements;

import com.hbm.advancements.*;
import com.hbm.entity.ModEntities;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class ModAdvancementProvider extends ForgeAdvancementProvider {

    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                  ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new ModAdvancementGenerator()));
    }

    public static class ModAdvancementGenerator implements AdvancementGenerator {

        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<Advancement> writer,
                             @NotNull ExistingFileHelper existingFileHelper) {

            Advancement root = Advancement.Builder.advancement()
                    .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                    .save(writer, ResLocation(MODID, "root"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.COIN_CREEPER.get().getDefaultInstance(),
                            Component.translatable("achievement.bossCreeper"),
                            Component.translatable("achievement.bossCreeper.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("kill_nuclear_creeper",
                            KilledTrigger.TriggerInstance.playerKilledEntity(
                                            EntityPredicate.Builder.entity().of(ModEntities.CREEPER_NUCLEAR.get())))
                    .save(writer, ResLocation(MODID, "kill_nuclear_creeper"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.NUCLEAR_WASTE.get().getDefaultInstance(),
                            Component.translatable("achievement.manhattan"),
                            Component.translatable("achievement.manhattan.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("nuclear_explosion", new NuclearExplosionTrigger.Instance(ContextAwarePredicate.ANY))
                    .save(writer, ResLocation(MODID, "manhattan"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.COIN_MASKMAN.get().getDefaultInstance(),
                            Component.translatable("achievement.bossMaskman"),
                            Component.translatable("achievement.bossMaskman.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("kill_maskman", KillMaskmanTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "boss_maskman"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.COIN_RADIATION.get().getDefaultInstance(),
                            Component.translatable("achievement.bossMeltdown"),
                            Component.translatable("achievement.bossMeltdown.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("boss_meltdown", BossMeltdownTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "boss_meltdown"), existingFileHelper);

            Advancement fiend = Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModToolItems.SHIMMER_SLEDGE.get().getDefaultInstance(),
                            Component.translatable("achievement.fiend"),
                            Component.translatable("achievement.fiend.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("fiend", FiendTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "fiend"), existingFileHelper);

            Advancement fiend2 = Advancement.Builder.advancement()
                    .parent(fiend)
                    .display(ModToolItems.SHIMMER_AXE.get().getDefaultInstance(),
                            Component.translatable("achievement.fiend2"),
                            Component.translatable("achievement.fiend2.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("fiend2", Fiend2Trigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "fiend2"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.COIN_UFO.get().getDefaultInstance(),
                            Component.translatable("achievement.bossUFO"),
                            Component.translatable("achievement.bossUFO.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("boss_ufo", BossUFOTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "boss_ufo"), existingFileHelper);

            Advancement radDeath = Advancement.Builder.advancement()
                    .parent(root)
                    .display(Items.SKELETON_SKULL.getDefaultInstance(),
                            Component.translatable("achievement.radDeath"),
                            Component.translatable("achievement.radDeath.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("rad_death", RadDeathTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "rad_death"), existingFileHelper);

            Advancement radPoison = Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.GEIGER_COUNTER.get().getDefaultInstance(),
                            Component.translatable("achievement.radPoison"),
                            Component.translatable("achievement.radPoison.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("rad_poison", RadPoisonTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "rad_poison"), existingFileHelper);

            Advancement horizonsStart = Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.SAT_GERALD.get().getDefaultInstance(),
                            Component.translatable("achievement.horizonsStart"),
                            Component.translatable("achievement.horizonsStart.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("horizons_start", HorizonsStartTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "horizons_start"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(horizonsStart)
                    .display(ModItems.SAT_GERALD.get().getDefaultInstance(),
                            Component.translatable("achievement.horizonsEnd"),
                            Component.translatable("achievement.horizonsEnd.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("horizons_end", HorizonsEndTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "horizons_end"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.SAT_FOEQ.get().getDefaultInstance(),
                            Component.translatable("achievement.FOEQ"),
                            Component.translatable("achievement.FOEQ.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("foeq", FOEQTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "foeq"), existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.BOXCAR_ITEM.get().getDefaultInstance(),
                            Component.translatable("achievement.bobHidden"),
                            Component.translatable("achievement.bobHidden.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("bob_hidden", BobHiddenTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "bob_hidden"), existingFileHelper);

            Advancement no9 = Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModArmorItems.NO9.get().getDefaultInstance(),
                            Component.translatable("achievement.no9"),
                            Component.translatable("achievement.no9.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("no9", No9Trigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "no9"), existingFileHelper);

            Advancement someWounds = Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.INJECTOR_KNIFE.get().getDefaultInstance(),
                            Component.translatable("achievement.someWounds"),
                            Component.translatable("achievement.someWounds.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("some_wounds", SomeWoundsTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "some_wounds"), existingFileHelper);

            Advancement goFish = Advancement.Builder.advancement()
                    .parent(root)
                    .display(Items.COD.getDefaultInstance(),
                            Component.translatable("achievement.goFish"),
                            Component.translatable("achievement.goFish.desc"),
                            null,
                            FrameType.TASK,
                            true, true, false)
                    .addCriterion("go_fish", GoFishTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "go_fish"), existingFileHelper);

            Advancement blastFurnace = Advancement.Builder.advancement()
                    .parent(root)
                    .display(new ItemStack(ModItems.NOTHING.get()),
                            Component.translatable("achievement.blastFurnace"),
                            Component.translatable("achievement.blastFurnace.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("blast_furnace", BlastFurnaceTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "blast_furnace"), existingFileHelper);

            Advancement assembly = Advancement.Builder.advancement()
                    .parent(blastFurnace)
                    .display(new ItemStack(ModItems.NOTHING.get()),
                            Component.translatable("achievement.assembly"),
                            Component.translatable("achievement.assembly.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("assembly", AssemblyTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "assembly"), existingFileHelper);

            Advancement chemplant = Advancement.Builder.advancement()
                    .parent(assembly)
                    .display(new ItemStack(ModItems.NOTHING.get()),
                            Component.translatable("achievement.chemplant"),
                            Component.translatable("achievement.chemplant.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("chemplant", ChemplantTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "chemplant"), existingFileHelper);

            Advancement desh = Advancement.Builder.advancement()
                    .parent(chemplant)
                    .display(new ItemStack(ModItems.INGOT_DESH.get()),
                            Component.translatable("achievement.desh"),
                            Component.translatable("achievement.desh.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("desh", DeshTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "desh"), existingFileHelper);

            Advancement technetium = Advancement.Builder.advancement()
                    .parent(desh)
                    .display(new ItemStack(ModItems.INGOT_TCALLOY.get()),
                            Component.translatable("achievement.technetium"),
                            Component.translatable("achievement.technetium.desc"),
                            null,
                            FrameType.GOAL,
                            true, true, false)
                    .addCriterion("technetium", TechnetiumTrigger.Instance.instance())
                    .save(writer, ResLocation(MODID, "technetium"), existingFileHelper);

        }
    }
}