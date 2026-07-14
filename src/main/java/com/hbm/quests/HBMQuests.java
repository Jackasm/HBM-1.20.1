package com.hbm.quests;

import com.hbm.blocks.ModBlocks;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class HBMQuests {
    private static final Map<String, QuestDefinition> QUESTS = new LinkedHashMap<>();

    public static void registerQuests() {
        // Одноразовые
        registerQuest(new QuestDefinition.Builder("hbm.quest.craft_nuke")
                .titleKey("hbm.quest.craft_nuke.title")
                .descriptionKey("hbm.quest.craft_nuke.desc")
                .type(QuestType.CRAFT)
                .requiredItem(new ItemStack(ModItems.MINE_FAT.get(), 1))
                .reward(new ItemStack(ModItems.INGOT_SCHRABIDIUM.get(), 5))
                .expReward(100)
                .oneTimeOnly(true)
                .build());

        registerQuest(new QuestDefinition.Builder("hbm.quest.collect_uranium")
                .titleKey("hbm.quest.collect_uranium.title")
                .descriptionKey("hbm.quest.collect_uranium.desc")
                .type(QuestType.ITEM_CHECK)
                .requiredItem(new ItemStack(ModItems.INGOT_URANIUM.get(), 10))
                .reward(new ItemStack(ModItems.INGOT_PLUTONIUM.get(), 2))
                .expReward(50)
                .oneTimeOnly(true)
                .build());

        registerQuest(new QuestDefinition.Builder("hbm.quest.achievement_researcher")
                .titleKey("hbm.quest.achievement_researcher.title")
                .descriptionKey("hbm.quest.achievement_researcher.desc")
                .type(QuestType.ACHIEVEMENT)
                .requiredAchievement(ResLocation(RefStrings.MODID, "researcher"))
                .reward(new ItemStack(ModItems.CHEESE.get(), 1))
                .expReward(200)
                .oneTimeOnly(true)
                .build());

        registerQuest(new QuestDefinition.Builder("hbm.quest.time_challenge")
                .titleKey("hbm.quest.time_challenge.title")
                .descriptionKey("hbm.quest.time_challenge.desc")
                .type(QuestType.CRAFT)
                .requiredItem(new ItemStack(ModItems.PLATE_ADVANCED_ALLOY.get(), 32))
                .reward(new ItemStack(ModItems.INGOT_TITANIUM.get(), 10))
                .expReward(75)
                .timeLimit(300)
                .oneTimeOnly(true)
                .build());

        // Повторяемые
        registerQuest(new QuestDefinition.Builder("hbm.quest.repeatable_iron")
                .titleKey("hbm.quest.repeatable_iron.title")
                .descriptionKey("hbm.quest.repeatable_iron.desc")
                .type(QuestType.ITEM_CHECK)
                .requiredItem(new ItemStack(Blocks.IRON_ORE, 32))
                .reward(new ItemStack(ModItems.INGOT_STEEL.get(), 5))
                .expReward(25)
                .cooldown(3600)
                .oneTimeOnly(false)
                .build());

        registerQuest(new QuestDefinition.Builder("hbm.quest.repeatable_dynamite")
                .titleKey("hbm.quest.repeatable_dynamite.title")
                .descriptionKey("hbm.quest.repeatable_dynamite.desc")
                .type(QuestType.CRAFT)
                .requiredItem(new ItemStack(ModItems.STICK_DYNAMITE.get(), 16))
                .reward(new ItemStack(ModItems.GRENADE_NUKE.get(), 32))
                .expReward(15)
                .oneTimeOnly(true)
                .build());

        // Кастомные
        registerQuest(new QuestDefinition.Builder("hbm.quest.custom_radiation")
                .titleKey("hbm.quest.custom_radiation.title")
                .descriptionKey("hbm.quest.custom_radiation.desc")
                .type(QuestType.CUSTOM)
                .customCondition(player -> {
                    float rad = HbmLivingProps.getRadiation(player);
                    return rad > 50;
                })
                .reward(new ItemStack(ModItems.RADX.get(), 1))
                .expReward(50)
                .oneTimeOnly(true)
                .build());

        registerQuest(new QuestDefinition.Builder("hbm.quest.use_rocket_launcher")
                .titleKey("hbm.quest.use_rocket_launcher.title")
                .descriptionKey("hbm.quest.use_rocket_launcher.desc")
                .type(QuestType.CUSTOM)
                .customCondition(player -> player.getPersistentData().getBoolean("used_rocket_launcher"))
                .reward(new ItemStack(ModGunItems.GUN_STINGER.get(), 1))
                .expReward(150)
                .oneTimeOnly(true)
                .build());

        registerQuest(new QuestDefinition.Builder("hbm.quest.repeatable_iron")
                .titleKey("hbm.quest.repeatable_iron.title")
                .descriptionKey("hbm.quest.repeatable_iron.desc")
                .type(QuestType.ITEM_CHECK)
                .requiredItem(new ItemStack(Blocks.IRON_ORE, 32))
                .reward(new ItemStack(ModItems.INGOT_STEEL.get(), 5))
                .expReward(25)
                .cooldown(3600)
                .oneTimeOnly(false)
                .build());

        // Квест на прочтение книги (завершается автоматически при пролистывании до конца)
        registerQuest(new QuestDefinition.Builder("hbm.quest.survival_guide")
                .titleKey("hbm.quest.survival_guide.title")
                .descriptionKey("hbm.quest.survival_guide.desc")
                .type(QuestType.CUSTOM)
                .customCondition(player -> {
                    // Проверяем флаг в persistent data
                    return player.getPersistentData().getBoolean("survival_guide_completed");
                })
                .expReward(100)
                .oneTimeOnly(true)
                .build());

        // Квест на крафт железной печи
        registerQuest(new QuestDefinition.Builder("hbm.quest.craft_iron_furnace")
                .titleKey("hbm.quest.craft_iron_furnace.title")
                .descriptionKey("hbm.quest.craft_iron_furnace.desc")
                .type(QuestType.CRAFT)
                .requiredItem(new ItemStack(ModBlocks.FURNACE_IRON.get(), 1))
                .reward(new ItemStack(Items.COAL, 64))
                .expReward(50)
                .oneTimeOnly(true)
                .build());

        // Квест на крафт наковальни (железная ИЛИ свинцовая)
        registerQuest(new QuestDefinition.Builder("hbm.quest.craft_anvil")
                .titleKey("hbm.quest.craft_anvil.title")
                .descriptionKey("hbm.quest.craft_anvil.desc")
                .type(QuestType.CRAFT)
                .requiredAnyOf(new ItemStack(ModItems.ANVIL_IRON.get(), 1),
                        new ItemStack(ModItems.ANVIL_LEAD.get(), 1))
                .reward(new ItemStack(ModGunItems.GUN_FLAREGUN.get(), 1))
                .reward(new ItemStack(ModAmmoItems.AMMO_G26_FLARE_WEAPON.get(), 1))
                .expReward(30)
                .oneTimeOnly(true)
                .build());

        // Квест на крафт доменной печи (в наковальне)
        registerQuest(new QuestDefinition.Builder("hbm.quest.craft_blast_furnace")
                .titleKey("hbm.quest.craft_blast_furnace.title")
                .descriptionKey("hbm.quest.craft_blast_furnace.desc")
                .type(QuestType.CRAFT)
                .requiredItem(new ItemStack(ModBlocks.MACHINE_BLAST_FURNACE.get(), 1))
                .reward(new ItemStack(ModAmmoItems.AMMO_G26_FLARE_SUPPLY.get(), 1))
                .expReward(50)
                .oneTimeOnly(true)
                .build());
    }

    private static void registerQuest(QuestDefinition quest) {
        QUESTS.put(quest.getId(), quest);
    }

    public static QuestDefinition getQuest(String id) {
        return QUESTS.get(id);
    }

    public static Collection<QuestDefinition> getAllQuests() {
        return QUESTS.values();
    }
}