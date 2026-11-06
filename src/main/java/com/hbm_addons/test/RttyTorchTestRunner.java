package com.hbm_addons.test;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.tileentity.storage.TileEntityMachineFluidTank;
import com.hbm.tileentity.network.RTTYSystem;
import com.hbm.tileentity.network.TileEntityRadioTorchController;
import com.hbm.tileentity.network.TileEntityRadioTorchCounter;
import com.hbm.tileentity.network.TileEntityRadioTorchLogic;
import com.hbm.tileentity.network.TileEntityRadioTorchReader;
import com.hbm.tileentity.network.TileEntityRadioTorchReceiver;
import com.hbm.tileentity.network.TileEntityRadioTorchSender;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

/**
 * Стенд RTTY-факелов: проверяет не постановку (её закрывает {@code rtty_place}),
 * а передачу по каналу для всех шести блоков.
 *
 * <p>Почему появился: до этой правки {@code RTTYSystem.updateBroadcastQueue()}
 * не вызывался вообще, а TE факелов не реализовывали {@code IControlReceiver} —
 * то есть ни сигнал не ходил, ни кнопка «Save Settings» ничего не сохраняла.
 * Стенд проходит по обеим дырам: настройки задаются тем же
 * {@code receiveControl}, что и из GUI, а значения читаются из
 * {@code RTTYSystem.listen} после тика.</p>
 *
 * <p>Контроллер стоит на заглушке мультиблока (жидкостный бак): ядро окружено
 * заглушками, свободной грани у него нет — значит ROR обязан пробрасываться
 * через {@code TileEntityProxyCombo}, как в эталоне.</p>
 */
public class RttyTorchTestRunner extends TestRunner {

    private static final int BASE_X = 12200;
    private static final int BASE_Y = 100;
    private static final int BASE_Z = 12200;

    private static final int SETTLE_TICKS = 12;

    private final BlockPos chestPos = new BlockPos(BASE_X, BASE_Y, BASE_Z);
    private final BlockPos counterPos = chestPos.above();

    private final BlockPos senderSupport = new BlockPos(BASE_X + 4, BASE_Y, BASE_Z);
    private final BlockPos senderPos = senderSupport.above();

    private final BlockPos receiverPos = new BlockPos(BASE_X + 8, BASE_Y, BASE_Z);

    private final BlockPos readerSupport = new BlockPos(BASE_X + 12, BASE_Y, BASE_Z);
    private final BlockPos readerPos = readerSupport.above();

    private final BlockPos tankPos = new BlockPos(BASE_X + 20, BASE_Y, BASE_Z);

    private final BlockPos logicSupport = new BlockPos(BASE_X + 14, BASE_Y, BASE_Z);
    private final BlockPos logicPos = logicSupport.above();

    /** Место контроллера не фиксировано: оно зависит от того, где встал бак. */
    private BlockPos controllerPos;
    private BlockPos controllerSupport;
    private Direction controllerFace = Direction.UP;
    private BlockPos tankCorePos;

    private int stage = 0;
    private int wait = 0;

    public RttyTorchTestRunner(net.minecraft.server.level.ServerLevel level, String scenarioName) {
        super(level, scenarioName);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        switch (stage) {
            case 0 -> {
                build();
                stage = 1;
                wait = 0;
                return false;
            }
            case 1 -> {
                if (++wait < SETTLE_TICKS) return false;
                progress("verify_chains");
                verifyChains();

                // Вторая фаза: контроллеру нужна свежая команда в канале (он
                // реагирует только на сообщение не старше тика), а логике —
                // сигнал, который больше не подходит под условие «равно 5».
                progress("live_commands");
                RTTYSystem.broadcast(level, "cmd", "setmode!2");
                RTTYSystem.broadcast(level, "lg", "6");
                stage = 2;
                wait = 0;
                return false;
            }
            case 2 -> {
                if (++wait < SETTLE_TICKS) return false;

                TileEntityMachineFluidTank tank = tank();
                int mode = tank == null ? -1 : tank.mode;
                info("tank_mode", mode);
                check("controller_runs_command", mode == 2);

                TileEntityRadioTorchLogic logic = logic();
                int state = logic == null ? -1 : logic.lastState;
                info("logic_state_after_miss", state);
                check("logic_no_match", state == 0);

                releaseChunks();
                finish(allChecksPassed());
                return true;
            }
            default -> {
                return true;
            }
        }
    }

    private void build() {
        progress("build");

        // Без форсирования чанк не тикает: TE простоят молча, и сигнал не пойдёт.
        forceChunks();

        // --- счётчик на сундуке: 3 железных слитка и 2 золотых ---
        clear(chestPos, 1);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(chestPos) instanceof Container chest) {
            chest.setItem(0, new ItemStack(Items.IRON_INGOT, 3));
            chest.setItem(1, new ItemStack(Items.GOLD_INGOT, 2));
        }
        check("counter_placed", placeTorch(ModBlocks.RADIO_TORCH_COUNTER.get(), chestPos));

        TileEntityRadioTorchCounter counter = counter();
        if (counter != null) {
            // Образец — железный слиток; режим EXACT выставит сам слот.
            counter.setItem(0, new ItemStack(Items.IRON_INGOT));
            CompoundTag tag = new CompoundTag();
            tag.putString("c0", "cnt");
            counter.receiveControl(tag);
        }

        // --- передатчик: опора запитана красным блоком сбоку ---
        clear(senderSupport, 1);
        level.setBlock(senderSupport, Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(senderSupport.east(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        check("sender_placed", placeTorch(ModBlocks.RADIO_TORCH_SENDER.get(), senderSupport));

        setChannel(sender(), "sig");

        // --- приёмник ---
        clear(receiverPos.below(), 1);
        level.setBlock(receiverPos.below(), Blocks.STONE.defaultBlockState(), 3);
        check("receiver_placed", placeTorch(ModBlocks.RADIO_TORCH_RECEIVER.get(), receiverPos.below()));
        setChannel(receiver(), "sig");

        // --- читатель на конденсаторе: читает VAL:fill ---
        clear(readerSupport, 1);
        level.setBlock(readerSupport, ModBlocks.CAPACITOR_COPPER.get().defaultBlockState(), 3);
        check("reader_placed", placeTorch(ModBlocks.RADIO_TORCH_READER.get(), readerSupport));

        if (reader() != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("c0", "read");
            tag.putString("n0", "fill");
            reader().receiveControl(tag);
        }

        // --- контроллер на заглушке жидкостного бака ---
        // Бак ставится последним: его мультиблок занимает 5×3×3 и стирает всё,
        // что оказалось рядом.
        clear(tankPos, 4);
        boolean tankPlaced = placeMachine(ModBlocks.MACHINE_FLUIDTANK.get(), tankPos);
        check("tank_placed", tankPlaced);

        BlockPos proxy = findProxyWithAirAbove();
        info("controller_proxy", proxy == null ? "нет" : proxy.toShortString());
        if (proxy == null) info("nearby_te", describeNearbyBlockEntities());

        boolean controllerPlaced = proxy != null
                && placeTorch(ModBlocks.RADIO_TORCH_CONTROLLER.get(), controllerSupport, controllerFace);
        if (controllerPlaced) this.controllerPos = proxy;
        check("controller_placed", controllerPlaced);

        if (controller() != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("c", "cmd");
            controller().receiveControl(tag);
        }
        RTTYSystem.broadcast(level, "cmd", "setmode!2");

        // --- логический факел: строка 1 «== 5» ---
        clear(logicSupport, 1);
        level.setBlock(logicSupport, Blocks.STONE.defaultBlockState(), 3);
        check("logic_placed", placeTorch(ModBlocks.RADIO_TORCH_LOGIC.get(), logicSupport));

        if (logic() != null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("c", "lg");
            tag.putString("m1", "5");
            tag.putInt("k1", 4); // «равно»
            logic().receiveControl(tag);
        }
        RTTYSystem.broadcast(level, "lg", "5");
    }

    private void verifyChains() {
        // 1) счётчик: 3 железных слитка в сундуке → канал cnt
        TileEntityRadioTorchCounter counter = counter();
        int count = counter == null ? -1 : counter.lastCount[0];
        info("counter_count", count);
        check("counter_counts_iron", count == 3);

        RTTYSystem.RTTYChannel cntChannel = RTTYSystem.listen(level, "cnt");
        String cntSignal = cntChannel == null || cntChannel.signal == null ? "нет" : "" + cntChannel.signal;
        info("cnt_signal", cntSignal);
        check("counter_broadcast", "3".equals(cntSignal));

        // 2) передатчик → приёмник по каналу sig
        TileEntityRadioTorchReceiver receiver = receiver();
        int recvState = receiver == null ? -1 : receiver.lastState;
        info("receiver_state", recvState);
        check("sender_to_receiver", recvState == 15);

        // Клиент выбирает текстуру on/off по lastState, и приезжает он блок-пакетом.
        // Пустой getUpdateTag() (по умолчанию у BlockEntity) означал, что факел
        // на клиенте всегда «выключен» — проверяем, что состояние уходит.
        CompoundTag updateTag = receiver == null ? new CompoundTag() : receiver.getUpdateTag();
        info("update_tag_state", updateTag.getInt("l"));
        check("update_tag_has_state", updateTag.contains("l"));
        check("update_tag_state_value", updateTag.getInt("l") == 15);

        // 3) читатель: VAL:fill конденсатора → «0» в канал read
        RTTYSystem.RTTYChannel readChannel = RTTYSystem.listen(level, "read");
        String readSignal = readChannel == null || readChannel.signal == null ? "нет" : "" + readChannel.signal;
        info("read_signal", readSignal);
        check("reader_broadcasts_fill", "0".equals(readSignal));

        // 4) логика: строка 1 подошла под «== 5»
        TileEntityRadioTorchLogic logic = logic();
        int state = logic == null ? -1 : logic.lastState;
        info("logic_state", state);
        check("logic_condition_match", state == 1);

        // 5) контроллер проверяется во второй фазе: команда отправляется заново,
        // чтобы она была свежей (контроллер реагирует на сообщение не старше тика).
    }

    private boolean allChecksPassed() {
        for (CheckResult c : checks) if (!c.pass) return false;
        metric("checks", checks.size());
        return !checks.isEmpty();
    }

    /** Форсирует чанки стенда: нефорсированный чанк TE не тикает. */
    private void forceChunks() {
        forEachChunk(true);
    }

    /** Снимает форсирование: иначе следующий сценарий получит чужую нагрузку. */
    private void releaseChunks() {
        forEachChunk(false);
    }

    private void forEachChunk(boolean forced) {
        int cx = BASE_X >> 4;
        int cz = BASE_Z >> 4;
        for (int x = cx - 1; x <= cx + 3; x++) {
            for (int z = cz - 1; z <= cz + 3; z++) {
                level.setChunkForced(x, z, forced);
            }
        }
    }

    /** Очищает площадку: факел не встанет, если цель занята рельефом. */
    private void clear(BlockPos pos, int radius) {        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = 0; dy <= radius; dy++) {
                    level.setBlock(pos.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private boolean placeTorch(Block block, BlockPos support) {
        return placeTorch(block, support, Direction.UP);
    }

    private boolean placeTorch(Block block, BlockPos support, Direction face) {
        FakePlayer player = FakePlayerFactory.getMinecraft(level);
        ItemStack stack = new ItemStack(block);

        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(context(player, stack, support, face));

        BlockPos target = support.relative(face);
        BlockState placed = level.getBlockState(target);
        boolean ok = placed.getBlock() == block && placed.getValue(RadioTorchBase.FACING) == face.getOpposite();
        if (!ok) {
            info("torch_not_placed_at", target.toShortString()
                    + " block=" + placed.getBlock().getDescriptionId()
                    + " item=" + stack.getItem().getDescriptionId());
        }
        return ok;
    }

    /** Постановка обычного блока-машины (мультиблок сам разложит заглушки). */
    private boolean placeMachine(Block block, BlockPos target) {
        FakePlayer player = FakePlayerFactory.getMinecraft(level);
        ItemStack stack = new ItemStack(block);

        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(context(player, stack, target.below(), Direction.UP));

        boolean ok = level.getBlockState(target).getBlock() == block
                || level.getBlockState(target.above()).getBlock() == block;
        if (!ok) {
            info("machine_not_placed_at", target.toShortString()
                    + " block=" + level.getBlockState(target).getBlock().getDescriptionId()
                    + " item=" + stack.getItem().getDescriptionId());
        }
        return ok;
    }

    private BlockPlaceContext context(FakePlayer player, ItemStack stack, BlockPos support, Direction face) {
        Vec3 hit = Vec3.atCenterOf(support).add(
                face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
        return new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack,
                new BlockHitResult(hit, face, support, false));
    }

    /** Ищет заглушку бака со свободным местом сверху — туда встанет контроллер. */
    /** Диагностика: что вообще стоит вокруг места бака и с какими TE. */
    private String describeNearbyBlockEntities() {
        StringBuilder sb = new StringBuilder();
        int found = 0;
        for (int dy = -2; dy <= 4 && found < 10; dy++) {
            for (int dx = -6; dx <= 6 && found < 10; dx++) {
                for (int dz = -6; dz <= 6 && found < 10; dz++) {
                    BlockPos p = tankPos.offset(dx, dy, dz);
                    BlockEntity be = level.getBlockEntity(p);
                    if (be == null) continue;
                    sb.append(p.toShortString()).append('=').append(be.getClass().getSimpleName()).append(';');
                    found++;
                }
            }
        }
        return sb.length() == 0 ? "пусто" : sb.toString();
    }

    /** Ищет заглушку бака со свободной гранью — туда встанет контроллер. */
    private BlockPos findProxyWithAirAbove() {
        for (int dy = -2; dy <= 4; dy++) {
            for (int dx = -6; dx <= 6; dx++) {
                for (int dz = -6; dz <= 6; dz++) {
                    BlockPos p = tankPos.offset(dx, dy, dz);
                    if (!(level.getBlockEntity(p) instanceof com.hbm.tileentity.TileEntityProxyCombo)) continue;

                    // Верх бака занят его же корпусом без собственного TE,
                    // поэтому свободная грань ищется по всем шести сторонам.
                    for (Direction dir : Direction.values()) {
                        BlockPos air = p.relative(dir);
                        if (!level.isEmptyBlock(air)) continue;

                        if (level.getBlockState(p).getBlock() instanceof BlockDummyable dummy) {
                            this.tankCorePos = dummy.findCore(level, p);
                        }
                        this.controllerSupport = p;
                        this.controllerFace = dir;
                        return air;
                    }
                }
            }
        }
        return null;
    }

    private void setChannel(com.hbm.tileentity.network.TileEntityRadioTorchBase te, String channel) {
        if (te == null) return;
        CompoundTag tag = new CompoundTag();
        tag.putString("c", channel);
        // Тем же путём, которым ходит кнопка «Save Settings» из GUI.
        te.receiveControl(tag);
    }

    private TileEntityRadioTorchCounter counter() {
        return level.getBlockEntity(counterPos) instanceof TileEntityRadioTorchCounter counter ? counter : null;
    }

    private TileEntityRadioTorchSender sender() {
        return level.getBlockEntity(senderPos) instanceof TileEntityRadioTorchSender sender ? sender : null;
    }

    private TileEntityRadioTorchReceiver receiver() {
        return level.getBlockEntity(receiverPos) instanceof TileEntityRadioTorchReceiver receiver ? receiver : null;
    }

    private TileEntityRadioTorchReader reader() {
        return level.getBlockEntity(readerPos) instanceof TileEntityRadioTorchReader reader ? reader : null;
    }

    private TileEntityRadioTorchController controller() {
        if (controllerPos == null) return null;
        return level.getBlockEntity(controllerPos) instanceof TileEntityRadioTorchController controller ? controller : null;
    }

    private TileEntityRadioTorchLogic logic() {
        return level.getBlockEntity(logicPos) instanceof TileEntityRadioTorchLogic logic ? logic : null;
    }

    private TileEntityMachineFluidTank tank() {
        if (tankCorePos == null) return null;
        BlockEntity be = level.getBlockEntity(tankCorePos);
        return be instanceof TileEntityMachineFluidTank tank ? tank : null;
    }
}
