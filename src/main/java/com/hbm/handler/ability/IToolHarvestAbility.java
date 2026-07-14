package com.hbm.handler.ability;

import com.hbm.config.ToolConfig;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemToolAbility;
import com.hbm.util.EnchantmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public interface IToolHarvestAbility extends IBaseAbility {

    default void preHarvestAll(int level, Level world, Player player) { }
    default void postHarvestAll(int level, Level world, Player player) { }

    default void onHarvestBlock(int level, Level world, BlockPos pos, Player player, BlockState state) {
        harvestBlock(false, world, pos, player);
    }

    static void harvestBlock(boolean skipDefaultDrops, Level world, BlockPos pos, Player player) {
        if(skipDefaultDrops) {
            world.destroyBlock(pos, false);
            ItemStack stack = player.getMainHandItem();
            if(!stack.isEmpty()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
            }
        } else if(player instanceof ServerPlayer) {
            BlockState blockState = world.getBlockState(pos);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            // Создаем контекст для лута
            LootParams.Builder builder = new LootParams.Builder((ServerLevel) world)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.TOOL, player.getMainHandItem())
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.BLOCK_STATE, blockState);

            if(blockEntity != null) {
                builder.withParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
            }

            List<ItemStack> drops = blockState.getDrops(builder);

            world.destroyBlock(pos, false);

            for(ItemStack drop : drops) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), drop);
            }

            ItemStack stack = player.getMainHandItem();
            if(!stack.isEmpty()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
            }
        }
    }

    int SORT_ORDER_BASE = 100;

    default void onToolSelected(ItemStack stack, Player player, boolean selected) {
        // Вызывается когда способность выбрана/отменена
    }

    // region implementations
    IToolHarvestAbility NONE = new IToolHarvestAbility() {
        @Override
        public String getName() {
            return "";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE;
        }
    };

    IToolHarvestAbility SILK = new IToolHarvestAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.silktouch";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_SILK;
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 1;
        }



        @Override
        public void preHarvestAll(int level, Level world, Player player) {
            // Не добавляем зачарование, а обрабатываем дроп сами
        }

        @Override
        public void postHarvestAll(int level, Level world, Player player) {
            // Не убираем зачарование
        }

        @Override
        public void onHarvestBlock(int level, Level world, BlockPos pos, Player player, BlockState state) {
            if (world.isClientSide()) return;

            Block block = state.getBlock();
            Item item = block.asItem();

            if (item != Items.AIR) {
                // Удаляем блок
                world.removeBlock(pos, false);

                // Создаем и выбрасываем предмет блока
                ItemStack blockItem = new ItemStack(item, 1);
                Block.popResource(world, pos, blockItem);

                // Износ
                ItemStack tool = player.getMainHandItem();
                if (!player.isCreative() && !tool.isEmpty()) {
                    tool.hurtAndBreak(1, player,
                            p -> p.broadcastBreakEvent(p.getUsedItemHand()));
                }
            } else {
                world.destroyBlock(pos, true, player);
            }
        }
    };

    IToolHarvestAbility LUCK = new IToolHarvestAbility() {
        private final int[] powerAtLevel = {1, 2, 3, 4, 5, 9};
        private final Map<UUID, Integer> playerLevels = new HashMap<>();

        @Override
        public String getName() {
            return "item.hbm.ability.luck";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_LUCK;
        }

        @Override
        public int levels() {
            return powerAtLevel.length;
        }

        @Override
        public String getExtension(int level) {
            return " (" + powerAtLevel[level] + ")";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 2;
        }

        public void onToolSelected(ItemStack stack, Player player, boolean selected) {
            if (selected) {
                // Добавляем Fortune при включении
                int level = getCurrentLevel(stack); // Нужно получить текущий уровень
                EnchantmentUtil.addEnchantment(stack, Enchantments.BLOCK_FORTUNE, powerAtLevel[level]);
                playerLevels.put(player.getUUID(), level);
            } else {
                // Убираем при выключении
                EnchantmentUtil.removeEnchantment(stack, Enchantments.BLOCK_FORTUNE);
                playerLevels.remove(player.getUUID());
            }
        }

        private int getCurrentLevel(ItemStack stack) {
            // Получаем AvailableAbilities из инструмента
            if (stack.getItem() instanceof ItemToolAbility tool) {
                AvailableAbilities abilities = tool.availableAbilities;

                // Получаем уровень LUCK способности
                return abilities.maxLevel(IToolHarvestAbility.LUCK);
            }
            return 0;
        }
    };

    IToolHarvestAbility SMELTER = new IToolHarvestAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.smelter";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 2; // После SHREDDER
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_SMELTER;
        }



        @Override
        public void onHarvestBlock(int level, Level world, BlockPos pos, Player player, BlockState state) {
            if (world.isClientSide()) return;

            Block block = state.getBlock();
            Item oreItem = block.asItem();
            ItemStack heldItem = player.getMainHandItem();

            if (oreItem != Items.AIR) {
                // Удаляем блок
                world.removeBlock(pos, false);

                List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos,
                        world.getBlockEntity(pos), player, heldItem);

                // Переплавляем каждый предмет из дропа
                for (ItemStack drop : drops) {
                    ItemStack smeltedDrop = smeltOre(drop, world);

                    if (smeltedDrop.getItem() != drop.getItem()) {
                        // Если предмет изменился после плавки
                        Block.popResource(world, pos, smeltedDrop);
                    } else {
                        // Если плавка не произошла или результат тот же
                        Block.popResource(world, pos, drop);
                    }
                }
                // Износ
                ItemStack tool = player.getMainHandItem();
                if (!player.isCreative() && !tool.isEmpty()) {
                    tool.hurtAndBreak(1, player,
                            p -> p.broadcastBreakEvent(p.getUsedItemHand()));
                }
            } else {
                world.destroyBlock(pos, true, player);
            }
        }

        private ItemStack smeltOre(ItemStack oreStack, Level world) {
            if (world.isClientSide()) {
                return oreStack;
            }

            SimpleContainer container = new SimpleContainer(oreStack);

            Optional<SmeltingRecipe> recipe = world.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, container, world);

            if (recipe.isPresent()) {
                SmeltingRecipe smeltingRecipe = recipe.get();

                ItemStack result = smeltingRecipe.assemble(container, world.registryAccess());

                if (!result.isEmpty()) {
                    return new ItemStack(result.getItem(), oreStack.getCount());
                }
            }

            return oreStack;
        }
    };

    IToolHarvestAbility SHREDDER = new IToolHarvestAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.shredder";
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 1; // После SILK
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_SHREDDER;
        }

        @Override
        public void preHarvestAll(int level, Level world, Player player) {
            // Ничего не делает перед добычей
        }

        @Override
        public void postHarvestAll(int level, Level world, Player player) {
            // Обрабатываем дроп после добычи
            List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class,
                    player.getBoundingBox().inflate(10.0D));

            for (ItemEntity entity : items) {
                ItemStack stack = entity.getItem();
                // Проверяем, является ли это рудой
                ItemStack powder = convertToPowder(stack);
                if (!powder.isEmpty()) {
                    entity.setItem(powder);
                }
            }
        }

        private ItemStack convertToPowder(ItemStack oreStack) {
            // Карта конвертации руда -> порошок
            Map<Item, Item> oreToPowder = new HashMap<>();

            // Добавьте свои соответствия
            // oreToPowder.put(ModBlocks.ORE_TITANIUM.asItem(), ModItems.POWDER_TITANIUM);
            // oreToPowder.put(ModBlocks.ORE_URANIUM.asItem(), ModItems.POWDER_URANIUM);
            // и т.д.

            Item powder = oreToPowder.get(oreStack.getItem());
            if (powder != null) {
                return new ItemStack(powder, oreStack.getCount());
            }

            return oreStack; // Если не нашли соответствие, возвращаем исходный предмет
        }
    };


    IToolHarvestAbility CENTRIFUGE = new IToolHarvestAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.centrifuge";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_CENTRIFUGE;
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 5;
        }

        @Override
        public void onHarvestBlock(int level, Level world, BlockPos pos, Player player, BlockState state) {
            Block block = state.getBlock();
            if(block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
                ItemStack[] result = getCentrifugeOutput(new ItemStack(block));

                boolean doesCentrifuge = result != null;

                harvestBlock(doesCentrifuge, world, pos, player);

                if(doesCentrifuge) {
                    for(ItemStack stack : result) {
                        if(!stack.isEmpty()) {
                            world.addFreshEntity(new ItemEntity(world,
                                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    stack.copy()));
                        }
                    }
                }
            } else {
                harvestBlock(false, world, pos, player);
            }
        }

        private ItemStack[] getCentrifugeOutput(ItemStack input) {
            if (input == null || input.isEmpty()) {
                return null;
            }

            ItemStack[] result = CentrifugeRecipes.getOutput(input);

            if (result == null) {
                return null;
            }

            ItemStack[] output = new ItemStack[result.length];
            for (int i = 0; i < result.length; i++) {
                if (result[i] != null && !result[i].isEmpty()) {
                    output[i] = result[i].copy();
                    output[i].setCount(output[i].getCount() * input.getCount());
                }
            }

            return output;
        }
    };

    IToolHarvestAbility CRYSTALLIZER = new IToolHarvestAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.crystallizer";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_CRYSTALLIZER;
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 6;
        }

        @Override
        public void onHarvestBlock(int level, Level world, BlockPos pos, Player player, BlockState state) {
            harvestBlock(false, world, pos, player);
        }
    };

    IToolHarvestAbility MERCURY = new IToolHarvestAbility() {
        @Override
        public String getName() {
            return "item.hbm.ability.mercury";
        }

        @Override
        public boolean isAllowed() {
            return ToolConfig.ABILITY_MERCURY;
        }

        @Override
        public int sortOrder() {
            return SORT_ORDER_BASE + 7;
        }

        @Override
        public void onHarvestBlock(int level, Level world, BlockPos pos, Player player, BlockState state) {
            Block block = state.getBlock();
            int mercury = 0;

            if(block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
                mercury = player.getRandom().nextInt(5) + 4;
            } else if(block == Blocks.REDSTONE_BLOCK) {
                mercury = player.getRandom().nextInt(7) + 8;
            }

            boolean doesConvert = mercury > 0;

            harvestBlock(doesConvert, world, pos, player);

            if(doesConvert) {
                world.addFreshEntity(new ItemEntity(world,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        new ItemStack(ModItems.NUGGET_MERCURY.get(), mercury)));
            }
        }
    };
    // endregion

    IToolHarvestAbility[] ABILITIES = {NONE, SILK, LUCK, SMELTER, SHREDDER, CENTRIFUGE, CRYSTALLIZER, MERCURY};

    static IToolHarvestAbility getByName(String name) {
        for(IToolHarvestAbility ability : ABILITIES) {
            if(ability.getName().equals(name)) {
                return ability;
            }
        }
        return NONE;
    }



}