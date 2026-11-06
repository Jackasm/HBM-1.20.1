package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.network.CableSwitch;
import com.hbm.tileentity.network.TileEntityCableBaseNT;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Проверяет, что сетевые блоки действительно создают узлы и соединяются:
 * кабель, покрытый провод, диод, выключенный и включённый рубильник, батарея.
 * Это регрессия на #105 и #114 — без тикера узла не появлялось.
 */
public class NetworksBlocksTestRunner extends TestRunner {

    private static final BlockPos CABLE = new BlockPos(320, 120, 320);
    private static final BlockPos WIRE = new BlockPos(321, 120, 320);
    private static final BlockPos DIODE = new BlockPos(322, 120, 320);
    private static final BlockPos SWITCH = new BlockPos(323, 120, 320);
    private static final BlockPos BATTERY = new BlockPos(320, 120, 322);
    /** Детектор стендом не проверяется (карточка #180): метрика, а не проверка. */
    private static final BlockPos DETECTOR = new BlockPos(324, 120, 320);

    private boolean placed = false;
    private boolean switchOn = false;
    private boolean ok = true;

    public NetworksBlocksTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    private void expect(String name, boolean condition) {
        check(name, condition);
        if (!condition) ok = false;
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;

        if (!placed) {
            placed = true;
            progress("placing_blocks");
            // Мир стенда переживает прогоны: сначала сносим старые блоки и их BE.
            TestChunks.force(level, 20, 20);
            for (BlockPos pos : new BlockPos[]{CABLE, WIRE, DIODE, SWITCH, BATTERY, DETECTOR}) {
                level.removeBlock(pos, false);
            }
            level.setBlockAndUpdate(CABLE, ModBlocks.RED_CABLE.get().defaultBlockState());
            level.setBlockAndUpdate(WIRE, ModBlocks.RED_WIRE_COATED.get().defaultBlockState());
            level.setBlockAndUpdate(DIODE, ModBlocks.CABLE_DIODE.get().defaultBlockState());
            level.setBlockAndUpdate(SWITCH, ModBlocks.CABLE_SWITCH.get().defaultBlockState());
            level.setBlockAndUpdate(BATTERY, ModBlocks.MACHINE_BATTERY.get().defaultBlockState());
            level.setBlockAndUpdate(DETECTOR, ModBlocks.CABLE_DETECTOR.get().defaultBlockState());
            return false;
        }

        if (!switchOn && tickCount >= 10) {
            switchOn = true;
            progress("switch_on");
            BlockState on = ModBlocks.CABLE_SWITCH.get().defaultBlockState()
                    .setValue(CableSwitch.POWERED, true);
            level.setBlockAndUpdate(SWITCH, on);
            // Детектор по эталону — клон рубильника: без питания узла быть не должно,
            // под питанием — должен появиться (#180).
            metric("detector_node_unpowered", EnergyNodespace.getNode(level, DETECTOR) != null);
            level.setBlockAndUpdate(DETECTOR, ModBlocks.CABLE_DETECTOR.get().defaultBlockState()
                    .setValue(com.hbm.blocks.network.CableDetector.POWERED, true));
            return false;
        }

        if (tickCount < 40) return false;

        EnergyNode cable = EnergyNodespace.getNode(level, CABLE);
        EnergyNode wire = EnergyNodespace.getNode(level, WIRE);
        EnergyNode diode = EnergyNodespace.getNode(level, DIODE);
        EnergyNode battery = EnergyNodespace.getNode(level, BATTERY);
        EnergyNode switchNode = EnergyNodespace.getNode(level, SWITCH);

        expect("cable_node", cable != null && cable.hasValidNet());
        expect("wire_node", wire != null && wire.hasValidNet());
        expect("diode_node", diode != null && diode.hasValidNet());
        expect("battery_node", battery != null && battery.hasValidNet());
        expect("switch_node_exists", switchNode != null);
        expect("switch_node_net", switchNode != null && switchNode.hasValidNet());
        expect("cable_wire_same_net",
                cable != null && wire != null && cable.getNet() != null && cable.getNet() == wire.getNet());
        // Сеть проходит сквозь диод: кабель и диод не соседи, они связаны через провод,
        // поэтому общая сеть доказывает, что диод в цепи, а не просто «узел есть».
        expect("diode_net_shared",
                diode != null && cable != null && diode.getNet() != null && diode.getNet() == cable.getNet());
        expect("battery_type", level.getBlockEntity(BATTERY) instanceof com.hbm.tileentity.storage.TileEntityMachineBattery);
        expect("cable_type", level.getBlockEntity(CABLE) instanceof TileEntityCableBaseNT);

        // Пять узлов цепи (кабель, провод, диод, рубильник, детектор) должны сойтись в одну сеть.
        int links = cable != null && cable.getNet() != null ? cable.getNet().getLinks().size() : -1;
        metric("net_links", links);
        expect("net_links_five", links == 5);

        info("switch_powered_state", level.getBlockState(SWITCH).getValue(CableSwitch.POWERED));
        metric("ticks", tickCount);
        metric("game_time", level.getGameTime());
        metric("cable_node_null", cable == null);
        metric("cable_net_null", cable == null ? null : cable.getNet() == null);

        // Детектор — клон рубильника по эталону: под питанием узел есть и в общей сети (#180).
        EnergyNode detector = EnergyNodespace.getNode(level, DETECTOR);
        expect("detector_node", detector != null && detector.hasValidNet());
        expect("detector_net_shared",
                detector != null && cable != null && detector.getNet() != null && detector.getNet() == cable.getNet());
        metric("detector_be", level.getBlockEntity(DETECTOR) != null);
        metric("detector_node_null", detector == null);
        metric("detector_powered", level.getBlockState(DETECTOR)
                .getValue(com.hbm.blocks.network.CableDetector.POWERED));

        metric("switch_on", switchOn);
        finish(ok);
        return true;
    }
}
