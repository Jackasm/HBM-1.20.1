package com.hbm.quests;

import com.hbm.main.MainRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestDefinition {
    private final String id;
    private final String titleKey;
    private final String descriptionKey;
    private final QuestType type;
    private final List<ItemStack> requiredItems;
    private final List<ItemStack> requiredAnyOf;
    private final ResourceLocation requiredAchievement;
    private final CustomQuestCondition customCondition;
    private final List<ItemStack> rewards;
    private final int expReward;
    private final int timeLimit;
    private final int cooldown;
    private final boolean oneTimeOnly;

    private QuestDefinition(Builder builder) {
        this.id = builder.id;
        this.titleKey = builder.titleKey;
        this.descriptionKey = builder.descriptionKey;
        this.type = builder.type;
        this.requiredItems = builder.requiredItems;
        this.requiredAnyOf = builder.requiredAnyOf;
        this.requiredAchievement = builder.requiredAchievement;
        this.customCondition = builder.customCondition;
        this.rewards = builder.rewards;
        this.expReward = builder.expReward;
        this.timeLimit = builder.timeLimit;
        this.cooldown = builder.cooldown;
        this.oneTimeOnly = builder.oneTimeOnly;
    }

    // Getters
    public String getId() { return id; }
    public String getTitleKey() { return titleKey; }
    public String getDescriptionKey() { return descriptionKey; }
    public QuestType getType() { return type; }
    public List<ItemStack> getRequiredItems() { return requiredItems; }
    public List<ItemStack> getRequiredAnyOf() { return requiredAnyOf; }
    public ResourceLocation getRequiredAchievement() { return requiredAchievement; }
    public List<ItemStack> getRewards() { return rewards; }
    public int getExpReward() { return expReward; }
    public int getTimeLimit() { return timeLimit; }
    public int getCooldown() { return cooldown; }
    public boolean isOneTimeOnly() { return oneTimeOnly; }

    public boolean checkCompletion(Player player, QuestInstance instance) {
        if (instance == null) return false;

        return switch (type) {
            case CRAFT, ITEM_CHECK -> checkItems(player, instance);
            case ACHIEVEMENT -> checkAchievement(player);
            case CUSTOM -> customCondition != null && customCondition.test(player);
        };
    }

    private boolean checkItems(Player player, QuestInstance instance) {
        boolean allRequiredMet = true;
        boolean anyOfMet = false;
        ItemStack foundAnyStack = null;

        // Проверка requiredItems (должны быть все)
        if (!requiredItems.isEmpty()) {
            List<ItemStack> remaining = new ArrayList<>();
            for (ItemStack req : requiredItems) remaining.add(req.copy());

            for (ItemStack stack : player.getInventory().items) {
                if (stack.isEmpty()) continue;
                for (int i = 0; i < remaining.size(); i++) {
                    ItemStack req = remaining.get(i);
                    if (ItemStack.isSameItem(stack, req)) {
                        int take = Math.min(stack.getCount(), req.getCount());
                        req.shrink(take);
                        if (req.getCount() <= 0) {
                            remaining.remove(i);
                            i--;
                        }
                        break;
                    }
                }
                if (remaining.isEmpty()) break;
            }
            allRequiredMet = remaining.isEmpty();
        }

        // Проверка requiredAnyOf (хотя бы один)
        if (!requiredAnyOf.isEmpty()) {
            for (ItemStack stack : player.getInventory().items) {
                if (stack.isEmpty()) continue;
                for (ItemStack anyReq : requiredAnyOf) {
                    if (ItemStack.isSameItem(stack, anyReq) && stack.getCount() >= anyReq.getCount()) {
                        anyOfMet = true;
                        foundAnyStack = anyReq.copy();
                        break;
                    }
                }
                if (anyOfMet) break;
            }
        } else {
            // Если список пуст, то условие "хотя бы один" считается выполненным
            anyOfMet = true;
        }

        // Если оба условия выполнены
        if (allRequiredMet && anyOfMet) {
            // Забираем предметы только для ITEM_CHECK (для CRAFT не забираем)
            if (type == QuestType.ITEM_CHECK) {
                // Забираем requiredItems
                for (ItemStack req : requiredItems) {
                    int amount = req.getCount();
                    for (int i = 0; i < player.getInventory().getContainerSize() && amount > 0; i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (ItemStack.isSameItem(stack, req)) {
                            int remove = Math.min(amount, stack.getCount());
                            stack.shrink(remove);
                            amount -= remove;
                            if (stack.isEmpty()) player.getInventory().setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
                // Забираем один из requiredAnyOf (тот, который нашли)
                if (foundAnyStack != null) {
                    int amount = foundAnyStack.getCount();
                    for (int i = 0; i < player.getInventory().getContainerSize() && amount > 0; i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (ItemStack.isSameItem(stack, foundAnyStack)) {
                            int remove = Math.min(amount, stack.getCount());
                            stack.shrink(remove);
                            amount -= remove;
                            if (stack.isEmpty()) player.getInventory().setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkAchievement(Player player) {
        if (requiredAchievement == null) return false;
        return player.getPersistentData().getBoolean("achievement_" + requiredAchievement.toString().replace(":", "_"));
    }

    public void giveRewards(Player player) {
        // Выдача предметов
        for (ItemStack reward : rewards) {
            if (!player.getInventory().add(reward.copy())) {
                player.drop(reward.copy(), false);
            }
        }
        if (expReward > 0) {
            player.giveExperiencePoints(expReward);
        }

        // Отправляем сообщение в чат
        sendCompletionMessage(player);

        // Проигрываем звук завершения квеста
        player.level().playSound(MainRegistry.proxy.me(), player.blockPosition(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                net.minecraft.sounds.SoundSource.PLAYERS,
                1.0F, 1.0F);
    }

    public static class Builder {
        private final String id;
        private String titleKey;
        private String descriptionKey;
        private QuestType type = QuestType.CUSTOM;
        private List<ItemStack> requiredItems = new ArrayList<>();
        private List<ItemStack> requiredAnyOf = new ArrayList<>();
        private ResourceLocation requiredAchievement;
        private CustomQuestCondition customCondition;
        private List<ItemStack> rewards = new ArrayList<>();
        private int expReward = 0;
        private int timeLimit = 0;
        private int cooldown = 0;
        private boolean oneTimeOnly = true;

        public Builder(String id) {
            this.id = id;
        }

        public Builder titleKey(String key) { this.titleKey = key; return this; }
        public Builder descriptionKey(String key) { this.descriptionKey = key; return this; }
        public Builder type(QuestType type) { this.type = type; return this; }
        public Builder requiredItem(ItemStack item) { this.requiredItems.add(item); return this; }
        public Builder requiredItems(List<ItemStack> items) { this.requiredItems.addAll(items); return this; }
        public Builder requiredAnyOf(ItemStack... items) { this.requiredAnyOf = Arrays.asList(items); return this; }
        public Builder requiredAchievement(ResourceLocation achievement) { this.requiredAchievement = achievement; return this; }
        public Builder customCondition(CustomQuestCondition condition) { this.customCondition = condition; return this; }
        public Builder reward(ItemStack reward) { this.rewards.add(reward); return this; }
        public Builder rewards(List<ItemStack> rewards) { this.rewards.addAll(rewards); return this; }
        public Builder expReward(int exp) { this.expReward = exp; return this; }
        public Builder timeLimit(int seconds) { this.timeLimit = seconds; return this; }
        public Builder cooldown(int seconds) { this.cooldown = seconds; return this; }
        public Builder oneTimeOnly(boolean oneTime) { this.oneTimeOnly = oneTime; return this; }

        public QuestDefinition build() {
            return new QuestDefinition(this);
        }
    }

    private void sendCompletionMessage(Player player) {
        // Сообщение о полученных наградах
        StringBuilder rewarded = new StringBuilder();
        if (!rewards.isEmpty()) {
            for (int i = 0; i < rewards.size(); i++) {
                ItemStack stack = rewards.get(i);
                rewarded.append(stack.getCount()).append("x ").append(stack.getHoverName().getString());
                if (i < rewards.size() - 1) rewarded.append(", ");
            }
        }
        if (expReward > 0) {
            if (!rewarded.isEmpty()) rewarded.append(", ");
            rewarded.append(expReward).append(" XP");
        }

        // Строим сообщение
        MutableComponent message = Component.literal("§6[Quest] §r")
                .append(Component.translatable(titleKey).withStyle(ChatFormatting.YELLOW));

        // Добавляем информацию о потраченных предметах, только если они были
        if (type == QuestType.ITEM_CHECK && !requiredItems.isEmpty()) {
            StringBuilder consumed = new StringBuilder();
            for (int i = 0; i < requiredItems.size(); i++) {
                ItemStack stack = requiredItems.get(i);
                consumed.append(stack.getCount()).append("x ").append(stack.getHoverName().getString());
                if (i < requiredItems.size() - 1) consumed.append(", ");
            }
            message = message.append(Component.literal(" §7- consumed: §c" + consumed));
        }

        message = message.append(Component.literal(" §7- received: §a" + rewarded));

        player.sendSystemMessage(message);
    }
}