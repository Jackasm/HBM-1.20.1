package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBobble.BobbleType;
import com.hbm.items.ModItems;
import com.hbm.items.deco.ItemBobble;
import com.hbm.tileentity.deco.TileEntityBobble;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bobblehead: ровно один дроп, и он с типом (хотфикс 0.1.1).
 *
 * <p>Блок хранит тип в ТЕ и выдаёт предмет сам в {@code playerWillDestroy}. Пока в
 * регистрации стоял {@code LootInfo.self()}, датаген делал ещё и стандартную таблицу,
 * и сломанный блок отдавал два предмета: типизированный и пустой «NONE».</p>
 *
 * <p>Проверяется обе половины: таблицы лута в ресурсах нет, а разбор блока игроком
 * (два ванильных шага — {@code playerWillDestroy} и {@code Block.dropResources}) даёт
 * один предмет, и его NBT несёт исходный тип.</p>
 */
public class BobbleLootTestRunner extends TestRunner {

    private static final BlockPos POS = new BlockPos(8, 250, 8);
    private static final BobbleType TYPE = BobbleType.BOB;

    public BobbleLootTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount++ > 0) return true;

        Block block = ModBlocks.BOBBLEHEAD.get();
        BlockState state = block.defaultBlockState();

        // Таблица лута не должна генерироваться: иначе к кастомному дропу добавится второй.
        boolean tableAbsent = server.getResourceManager()
                .getResource(new ResourceLocation("hbm", "loot_tables/blocks/deco/bobblehead.json"))
                .isEmpty();
        check("loot_table_absent", tableAbsent);

        // Чистая площадка: воздух, ноль предметов рядом.
        level.setBlockAndUpdate(POS, Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(POS, state);
        BlockEntity te = level.getBlockEntity(POS);
        boolean teOk = te instanceof TileEntityBobble;
        check("block_entity_present", teOk);
        if (teOk) {
            ((TileEntityBobble) te).type = TYPE;
            te.setChanged();
        }

        AABB area = new AABB(POS).inflate(3);
        List<ItemEntity> before = new ArrayList<>(level.getEntitiesOfClass(ItemEntity.class, area));

        FakePlayer player = new FakePlayer(level, new GameProfile(UUID.randomUUID(), "bobble_loot_test"));
        block.playerWillDestroy(level, POS, state, player);
        Block.dropResources(state, level, POS, level.getBlockEntity(POS), player, ItemStack.EMPTY);
        level.removeBlock(POS, false);

        List<ItemEntity> after = new ArrayList<>(level.getEntitiesOfClass(ItemEntity.class, area));
        after.removeAll(before);

        check("single_drop", after.size() == 1);
        metric("drops", after.size());
        if (after.size() == 1) {
            ItemStack stack = after.get(0).getItem();
            check("drop_is_bobblehead", stack.is(ModItems.BOBBLEHEAD.get()));
            check("drop_keeps_type", ItemBobble.getType(stack) == TYPE);
            metric("drop", stack.getItem() + " x" + stack.getCount() + " type=" + ItemBobble.getType(stack));
            check("drop_single_count", stack.getCount() == 1);
        }

        // Мир не мусорим: предметы и место убираем.
        for (ItemEntity item : after) item.discard();
        if (!level.getBlockState(POS).isAir()) level.setBlockAndUpdate(POS, Blocks.AIR.defaultBlockState());

        finish(checks.stream().allMatch(c -> c.pass));
        return true;
    }
}
