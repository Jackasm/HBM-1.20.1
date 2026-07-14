package com.hbm.items.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ability.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemToolAbility extends DiggerItem {

    protected final float attackDamage;
    protected final float attackSpeed;
    public AvailableAbilities availableAbilities;
    protected boolean rockBreaker = false;
    public boolean isShears = false;
    private final TagKey<Block> effectiveTag;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ItemToolAbility(float attackDamage, float attackSpeed, Tier tier,
                           TagKey<Block> effectiveBlocksTag,
                           Properties properties,
                           AvailableAbilities abilities) {
        super(attackDamage, attackSpeed, tier, effectiveBlocksTag, properties);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.effectiveTag = effectiveBlocksTag;
        this.availableAbilities = abilities != null ? abilities : new AvailableAbilities().addToolAbilities();

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Tool modifier",
                this.attackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Tool modifier",
                this.attackSpeed, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    // Билдер для удобного создания инструментов
    public static Builder builder(Tier tier, TagKey<Block> effectiveBlocksTag) {
        return new Builder(tier, effectiveBlocksTag);
    }

    public static class Builder {
        private final Tier tier;
        private final TagKey<Block> effectiveBlocksTag;
        private float attackDamage = 1.0F;
        private float attackSpeed = -2.4F;
        private Rarity rarity = Rarity.COMMON;
        private final AvailableAbilities abilities = new AvailableAbilities().addToolAbilities();
        private boolean rockBreaker = false;

        public Builder(Tier tier, TagKey<Block> effectiveBlocksTag) {
            this.tier = tier;
            this.effectiveBlocksTag = effectiveBlocksTag;
        }

        // Добавляем методы настройки как в ItemSwordAbility
        public Builder attackDamage(float damage) {
            this.attackDamage = damage;
            return this;
        }

        public Builder attackSpeed(float speed) {
            this.attackSpeed = speed;
            return this;
        }

        public Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder addAbility(IBaseAbility ability, int level) {
            this.abilities.addAbility(ability, level);
            return this;
        }

        public Builder rockBreaker() {
            this.rockBreaker = true;
            return this;
        }


        public ItemToolAbility build() {
            Properties props = new Properties()
                    .durability(tier.getUses())
                    .rarity(rarity);

            ItemToolAbility tool = new ItemToolAbility(
                    attackDamage, attackSpeed, tier,
                    effectiveBlocksTag,
                    props, abilities);
            tool.rockBreaker = this.rockBreaker;
            tool.isShears = false;

            return tool;
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player.isShiftKeyDown()) {
            // Shift + ПКМ: выключить способности
            disableAbilities(player, stack);
            return InteractionResultHolder.success(stack);
        } else if (!level.isClientSide()) {
            // ПКМ: циклическое переключение способностей
            cycleAbilities(player, stack);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player && canOperate(stack)) {
            this.availableAbilities.getWeaponAbilities().forEach((ability, level) ->
                    ability.onHit(level, attacker.level(), (Player) attacker, target, this));
        }
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level,
                             @NotNull BlockState state, @NotNull BlockPos pos,
                             @NotNull LivingEntity entity) {

        // Износ инструмента ДО разрушения
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, entity,
                    livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        return super.mineBlock(stack, level, state, pos, entity);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (!canOperate(stack)) return 1.0F;

        if (state.is(effectiveTag)) { // нужно сохранить effectiveTag в поле
            return this.speed; // скорость из Tier
        }
        return 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (!canOperate(stack)) return false;

        Configuration config = getConfiguration(stack);
        if (config.getActivePreset().harvestAbility == IToolHarvestAbility.SILK)
            return true;

        return super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ToolAction toolAction) {
        if (isShears && toolAction == ToolActions.SHEARS_DIG) {
            return true;
        }
        return super.canPerformAction(stack, toolAction);
    }

    // Система конфигурации способностей
    public static class Configuration {
        public List<ToolPreset> presets;
        public int currentPreset;

        public Configuration() {
            this.presets = null;
            this.currentPreset = 0;
        }

        public Configuration(List<ToolPreset> presets, int currentPreset) {
            this.presets = presets;
            this.currentPreset = currentPreset;
        }

        public void writeToNBT(CompoundTag nbt) {
            nbt.putInt("ability", currentPreset);

            ListTag nbtPresets = new ListTag();
            for (ToolPreset preset : presets) {
                CompoundTag nbtPreset = new CompoundTag();
                preset.writeToNBT(nbtPreset);
                nbtPresets.add(nbtPreset);
            }
            nbt.put("abilityPresets", nbtPresets);
        }

        public void readFromNBT(CompoundTag nbt) {
            currentPreset = nbt.getInt("ability");

            ListTag nbtPresets = nbt.getList("abilityPresets", 10);
            int numPresets = Math.min(nbtPresets.size(), 99);

            presets = new ArrayList<>(numPresets);
            for (int i = 0; i < numPresets; i++) {
                CompoundTag nbtPreset = nbtPresets.getCompound(i);
                ToolPreset preset = new ToolPreset();
                preset.readFromNBT(nbtPreset);
                presets.add(preset);
            }
            currentPreset = Math.max(0, Math.min(currentPreset, presets.size() - 1));
        }

        public void reset(AvailableAbilities availableAbilities) {
            currentPreset = 0;
            presets = new ArrayList<>();

            // Всегда добавляем пресет "NONE" первым
            presets.add(new ToolPreset());

            // Добавляем только area abilities
            availableAbilities.getToolAreaAbilities().forEach((ability, level) -> {
                if (ability != IToolAreaAbility.NONE) {
                    presets.add(new ToolPreset(ability, level, IToolHarvestAbility.NONE, 0));
                }
            });

            // Добавляем только harvest abilities
            availableAbilities.getToolHarvestAbilities().forEach((ability, level) -> {
                if (ability != IToolHarvestAbility.NONE) {
                    presets.add(new ToolPreset(IToolAreaAbility.NONE, 0, ability, level));
                }
            });

            // Добавляем комбинации area + harvest (где разрешено)
            availableAbilities.getToolAreaAbilities().forEach((areaAbility, areaLevel) -> {
                if (areaAbility != IToolAreaAbility.NONE && areaAbility.allowsHarvest(areaLevel)) {
                    availableAbilities.getToolHarvestAbilities().forEach((harvestAbility, harvestLevel) -> {
                        if (harvestAbility != IToolHarvestAbility.NONE) {
                            presets.add(new ToolPreset(areaAbility, areaLevel, harvestAbility, harvestLevel));
                        }
                    });
                }
            });

            // Сортируем для удобного переключения
            presets.sort(Comparator
                    .comparing((ToolPreset p) -> p.harvestAbility == IToolHarvestAbility.NONE ? 0 : 1)
                    .thenComparing((ToolPreset p) -> p.areaAbility == IToolAreaAbility.NONE ? 0 : 1)
                    .thenComparing(p -> p.areaAbility.getName())
                    .thenComparingInt(p -> p.areaAbilityLevel)
                    .thenComparing(p -> p.harvestAbility.getName())
                    .thenComparingInt(p -> p.harvestAbilityLevel));
        }

        public ToolPreset getActivePreset() {
            if (presets == null || presets.isEmpty() || currentPreset < 0 || currentPreset >= presets.size()) {
                return new ToolPreset(); // Возвращаем пустой пресет по умолчанию
            }
            return presets.get(currentPreset);
        }
    }

    public Configuration getConfiguration(ItemStack stack) {
        Configuration config = new Configuration();
        if (!stack.hasTag() || !Objects.requireNonNull(stack.getTag()).contains("ability")) {
            config.reset(availableAbilities);
            return config;
        }

        config.readFromNBT(stack.getTag());
        return config;
    }

    public void setConfiguration(ItemStack stack, Configuration config) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        config.writeToNBT(Objects.requireNonNull(stack.getTag()));
    }

    public boolean canOperate(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // Получаем активный пресет для отображения стрелочки
        Configuration config = getConfiguration(stack);
        availableAbilities.addInformation(tooltip, config.getActivePreset());

        if (rockBreaker) {
            tooltip.add(Component.literal("")
                    .append(Component.translatable("tooltip.hbm.rock_breaker")
                            .withStyle(net.minecraft.ChatFormatting.RED)));
        }
    }


    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.isEnchanted() || !getConfiguration(stack).getActivePreset().isNone();
    }

    private void cycleAbilities(Player player, ItemStack stack) {
        Configuration config = getConfiguration(stack);
        ToolPreset oldPreset = config.getActivePreset();

        // Убираем зачарования со старого пресета
        if (oldPreset.harvestAbility != IToolHarvestAbility.NONE) {
            oldPreset.harvestAbility.onToolSelected(stack, player, false);
        }

        // Переключаем на следующий пресет
        config.currentPreset = (config.currentPreset + 1) % config.presets.size();

        // Сохраняем изменения
        setConfiguration(stack, config);

        // Добавляем зачарования для нового пресета
        ToolPreset newPreset = config.getActivePreset();
        if (newPreset.harvestAbility != IToolHarvestAbility.NONE) {
            newPreset.harvestAbility.onToolSelected(stack, player, true);
        }

        // Показываем сообщение игроку
        player.displayClientMessage(newPreset.getMessage(), true);
    }

    private void disableAbilities(Player player, ItemStack stack) {
        Configuration config = getConfiguration(stack);

        // Ищем пресет "NONE" или создаем его
        int noneIndex = -1;
        for (int i = 0; i < config.presets.size(); i++) {
            if (config.presets.get(i).isNone()) {
                noneIndex = i;
                break;
            }
        }

        // Если не нашли пресет "NONE", создаем его
        if (noneIndex == -1) {
            config.presets.add(0, new ToolPreset());
            noneIndex = 0;
        }

        // Устанавливаем пресет "NONE"
        config.currentPreset = noneIndex;

        // Сохраняем изменения
        setConfiguration(stack, config);

        // Показываем сообщение игроку
        player.displayClientMessage(
                Component.translatable("item.hbm.ability.none")
                        .withStyle(style -> style.withColor(0xFFAA00)),
                true
        );
    }


    public void breakExtraBlock(Level world, BlockPos pos, Player player, boolean dropLoot) {
        BlockState state = world.getBlockState(pos);
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.isEmpty() || !state.canHarvestBlock(world, pos, player)) {
            return;
        }

        // Проверяем, может ли инструмент добывать этот блок
        if (!isCorrectToolForDrops(heldItem, state)) {
            return;
        }

        // Добываем блок с дропом лута
        state.getBlock().playerWillDestroy(world, pos, state, player);

        if (world.removeBlock(pos, false)) {
            state.getBlock().destroy(world, pos, state);

            // Дропаем лут!
            if (dropLoot) {
                // Получаем BlockEntity для корректного дропа
                BlockEntity blockEntity = world.getBlockEntity(pos);

                ItemStack stack = player.getMainHandItem();
                ItemToolAbility tool = (ItemToolAbility) stack.getItem();
                ItemToolAbility.Configuration config = tool.getConfiguration(stack);
                ToolPreset preset = config.getActivePreset();

                // Дропаем предметы из блока
                if (preset.harvestAbility == IToolHarvestAbility.SILK || preset.harvestAbility == IToolHarvestAbility.SMELTER)
                {
                    preset.harvestAbility.onHarvestBlock(
                            preset.harvestAbilityLevel, world, pos, player, state);
                } else Block.dropResources(state, world, pos, blockEntity, player, heldItem);

                // Дропаем опыт, если есть (только на сервере)
                if (!world.isClientSide) {
                    int exp = state.getExpDrop(world, world.random, pos, 0, 0);
                    if (exp > 0) {
                        state.getBlock().popExperience((ServerLevel) world, pos, exp);
                    }
                }
            }


            // Применяем износ инструмента
            heldItem.hurtAndBreak(1, player,
                    (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

            // Вызываем событие разрушения блока для клиента
            world.levelEvent(2001, pos, Block.getId(state));
        }
    }
}