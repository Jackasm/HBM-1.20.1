package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.entity.IRadarDetectableNT;
import com.hbm.api.entity.RadarEntry;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.interfaces.IControlReceiver;
import com.hbm.items.ISatChip;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemCoordinateBase;
import com.hbm.saveddata.SatelliteSavedData;
import com.hbm.saveddata.satellites.Satellite;
import com.hbm.saveddata.satellites.SatelliteHorizons;
import com.hbm.saveddata.satellites.SatelliteLaser;
import com.hbm.saveddata.satellites.SatelliteResonator;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.*;
import com.hbm.util.Library;
import com.hbm.util.Library.PosDir;
import com.hbm.util.Tuple.Triplet;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TileEntityMachineRadarNT extends TileEntityMachineBase implements
        IEnergyReceiver, MenuProvider, IConfigurableMachine, IControlReceiver {

    // Инвентарь: 10 слотов (0-8 для предметов, 9 для батареи)
    private final ItemStackHandler inventory = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public boolean scanMissiles = true;
    public boolean scanShells = true;
    public boolean scanPlayers = true;
    public boolean smartMode = true;
    public boolean redMode = true;
    public boolean showMap = false;
    public boolean jammed = false;

    public float prevRotation;
    public float rotation;
    public long power = 0;

    protected int pingTimer = 0;
    protected int lastPower;
    protected final static int maxTimer = 80;

    public static int maxPower = 100_000;
    public static int consumption = 500;
    public static int radarRange = 1_000;
    public static int radarBuffer = 30;
    public static int radarAltitude = 55;
    public static int chunkLoadCap = 10;
    public static boolean generateChunks = false;

    public byte[] map = new byte[40_000];
    public boolean clearFlag = false;
    public List<RadarEntry> entries = new ArrayList<>();

    public TileEntityMachineRadarNT(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADAR_NT.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.radar");
    }

    @Override
    public String getConfigName() {
        return "radar";
    }

    @Override
    public void readIfPresent(com.google.gson.JsonObject obj) {
        maxPower = IConfigurableMachine.grab(obj, "L:powerCap", maxPower);
        consumption = IConfigurableMachine.grab(obj, "L:consumption", consumption);
        radarRange = IConfigurableMachine.grab(obj, "I:radarRange", radarRange);
        radarBuffer = IConfigurableMachine.grab(obj, "I:radarBuffer", radarBuffer);
        radarAltitude = IConfigurableMachine.grab(obj, "I:radarAltitude", radarAltitude);
        chunkLoadCap = IConfigurableMachine.grab(obj, "I:chunkLoadCap", chunkLoadCap);
        generateChunks = IConfigurableMachine.grab(obj, "B:generateChunks", generateChunks);
    }

    @Override
    public void writeConfig(com.google.gson.stream.JsonWriter writer) throws IOException {
        writer.name("L:powerCap").value(maxPower);
        writer.name("L:consumption").value(consumption);
        writer.name("I:radarRange").value(radarRange);
        writer.name("I:radarBuffer").value(radarBuffer);
        writer.name("I:radarAltitude").value(radarAltitude);
        writer.name("B:generateChunks").value(generateChunks);
    }

    public int getRange() {
        return radarRange;
    }

    public void tick() {
        if (level == null) return;

        if (this.map == null || this.map.length != 40_000) {
            this.map = new byte[40_000];
        }

        if (!level.isClientSide) {
            this.power = Library.chargeTEFromItems(inventory, 9, power, maxPower);

            if (level.getGameTime() % 20 == 0) {
                for (PosDir pos : getConPos()) {
                    this.trySubscribe(level, pos.pos(), pos.dir());
                }
            }

            this.power = Library.chargeTEFromItems(inventory, 0, power, maxPower);
            this.jammed = false;
            allocateTargets();

            if (this.lastPower != getRedPower()) {
                this.setChanged();
                for (PosDir pos : getConPos()) {
                    updateRedstoneConnection(pos);
                }
            }
            lastPower = getRedPower();

            if (!this.muffled) {
                pingTimer++;
                if (power > 0 && pingTimer >= maxTimer) {
                    level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                            ModSounds.SONAR_PING.get(), net.minecraft.sounds.SoundSource.BLOCKS,
                            5.0F, 1.0F);
                    pingTimer = 0;
                }
            }

            if (this.showMap) {
                int chunkLoads = 0;
                for (int i = 0; i < 100; i++) {
                    int index = (int) (level.getGameTime() % 400) * 100 + i;
                    int iX = (index % 200) * getRange() * 2 / 200;
                    int iZ = index / 200 * getRange() * 2 / 200;

                    int x = worldPosition.getX() - getRange() + iX;
                    int z = worldPosition.getZ() - getRange() + iZ;

                    if (level.hasChunk(x >> 4, z >> 4)) {
                        this.map[index] = (byte) MathHelper.clamp(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), 50, 128);
                    } else {
                        if (this.map[index] == 0 && chunkLoads < chunkLoadCap) {
                            if (generateChunks) {
                                level.getChunk(x >> 4, z >> 4);
                                this.map[index] = (byte) MathHelper.clamp(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), 50, 128);
                                chunkLoads++;
                            } else {
                                if (level.hasChunk(x >> 4, z >> 4)) {
                                    this.map[index] = (byte) MathHelper.clamp(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), 50, 128);
                                    chunkLoads++;
                                }
                            }
                        }
                    }
                }
            }

            if (inventory.getStackInSlot(8).getItem() == ModItems.RADAR_LINKER.get()) {
                BlockPos pos = ItemCoordinateBase.getPosition(inventory.getStackInSlot(8));
                if (pos != null) {
                    BlockEntity te = level.getBlockEntity(pos);
                    if (te instanceof TileEntityMachineRadarScreen screen) {
                        screen.entries.clear();
                        screen.entries.addAll(this.entries);
                        screen.refX = worldPosition.getX();
                        screen.refY = worldPosition.getY();
                        screen.refZ = worldPosition.getZ();
                        screen.range = this.getRange();
                        screen.linked = true;
                        screen.setChanged();
                    }
                }
            }

            this.setChanged();
            if (this.clearFlag) {
                this.map = new byte[40_000];
                this.clearFlag = false;
            }
        } else {
            prevRotation = rotation;
            if (power > 0) rotation += 5F;
            if (rotation >= 360) {
                rotation -= 360F;
                prevRotation -= 360F;
            }
        }
    }

    public PosDir[] getConPos() {
        return new PosDir[]{
                new PosDir(worldPosition.offset(1, 0, 0), Direction.EAST),
                new PosDir(worldPosition.offset(-1, 0, 0), Direction.WEST),
                new PosDir(worldPosition.offset(0, 0, 1), Direction.SOUTH),
                new PosDir(worldPosition.offset(0, 0, -1), Direction.NORTH),
        };
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        FriendlyByteBuf fBuf = new FriendlyByteBuf(buf);
        fBuf.writeLong(this.power);
        fBuf.writeBoolean(this.scanMissiles);
        fBuf.writeBoolean(this.scanShells);
        fBuf.writeBoolean(this.scanPlayers);
        fBuf.writeBoolean(this.smartMode);
        fBuf.writeBoolean(this.redMode);
        fBuf.writeBoolean(this.showMap);
        fBuf.writeBoolean(this.jammed);
        fBuf.writeInt(entries.size());
        for (RadarEntry entry : entries) entry.toBytes(fBuf);
        if (this.clearFlag) {
            fBuf.writeBoolean(true);
        } else {
            fBuf.writeBoolean(false);
            if (this.showMap) {
                fBuf.writeBoolean(true);
                short index = (short) (Objects.requireNonNull(level).getGameTime() % 400);
                fBuf.writeShort(index);
                for (int i = index * 100; i < (index + 1) * 100; i++) {
                    fBuf.writeByte(this.map[i]);
                }
            } else {
                fBuf.writeBoolean(false);
            }
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        FriendlyByteBuf fBuf = new FriendlyByteBuf(buf);
        this.power = fBuf.readLong();
        this.scanMissiles = fBuf.readBoolean();
        this.scanShells = fBuf.readBoolean();
        this.scanPlayers = fBuf.readBoolean();
        this.smartMode = fBuf.readBoolean();
        this.redMode = fBuf.readBoolean();
        this.showMap = fBuf.readBoolean();
        this.jammed = fBuf.readBoolean();
        int count = fBuf.readInt();
        this.entries.clear();
        for (int i = 0; i < count; i++) {
            RadarEntry entry = new RadarEntry();
            entry.fromBytes(fBuf);
            this.entries.add(entry);
        }
        if (fBuf.readBoolean()) {
            this.map = new byte[40_000];
        } else {
            if (fBuf.readBoolean()) {
                int index = fBuf.readShort();
                for (int i = index * 100; i < (index + 1) * 100; i++) {
                    this.map[i] = fBuf.readByte();
                }
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.power = nbt.getLong("power");
        this.scanMissiles = nbt.getBoolean("scanMissiles");
        this.scanShells = nbt.getBoolean("scanShells");
        this.scanPlayers = nbt.getBoolean("scanPlayers");
        this.smartMode = nbt.getBoolean("smartMode");
        this.redMode = nbt.getBoolean("redMode");
        this.showMap = nbt.getBoolean("showMap");
        if (nbt.contains("map")) this.map = nbt.getByteArray("map");
        // Загружаем инвентарь
        if (nbt.contains("inventory")) {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("power", power);
        nbt.putBoolean("scanMissiles", scanMissiles);
        nbt.putBoolean("scanShells", scanShells);
        nbt.putBoolean("scanPlayers", scanPlayers);
        nbt.putBoolean("smartMode", smartMode);
        nbt.putBoolean("redMode", redMode);
        nbt.putBoolean("showMap", showMap);
        nbt.putByteArray("map", map);
        // Сохраняем инвентарь
        nbt.put("inventory", inventory.serializeNBT());
    }

    protected void allocateTargets() {
        this.entries.clear();

        if (worldPosition.getY() < radarAltitude) return;
        if (this.power < consumption) {
            this.power = 0;
            return;
        }
        this.power -= consumption;

        int scan = this.getRange();

        IRadarDetectableNT.RadarScanParams params = new IRadarDetectableNT.RadarScanParams(this.scanMissiles, this.scanShells, this.scanPlayers, this.smartMode);

        for (Entity e : matchingEntities) {
            if (e.level().dimension() == Objects.requireNonNull(level).dimension() &&
                    Math.abs(e.getX() - (worldPosition.getX() + 0.5)) <= scan &&
                    Math.abs(e.getZ() - (worldPosition.getZ() + 0.5)) <= scan &&
                    e.getY() - worldPosition.getY() > radarBuffer) {

                if (e instanceof LivingEntity && HbmLivingProps.getDigamma((LivingEntity) e) > 0.001) {
                    this.jammed = true;
                    entries.clear();
                    return;
                }

                // Используем только IRadarDetectableNT
                if (e instanceof IRadarDetectableNT detectable) {
                    if (detectable.canBeSeenBy(this) && detectable.paramsApplicable(params)) {
                        RadarEntry entry = new RadarEntry(detectable, e, detectable.suppliesRedstone(params));
                        this.entries.add(entry);
                        continue;
                    }
                }

                // Players (отдельно, так как Player не реализует IRadarDetectableNT)
                if (params.scanPlayers && e instanceof Player) {
                    RadarEntry entry = new RadarEntry((Player) e);
                    this.entries.add(entry);
                }
            }
        }
    }

    public int getRedPower() {
        if (!entries.isEmpty()) {
            if (redMode) {
                double maxRange = this.getRange() * Math.sqrt(2D);
                int power = 0;
                for (RadarEntry e : entries) {
                    if (!e.redstone) continue;
                    double dist = Math.sqrt(Math.pow(e.posX - worldPosition.getX(), 2) + Math.pow(e.posZ - worldPosition.getZ(), 2));
                    int p = 15 - (int) Math.floor(dist / maxRange * 15);
                    if (p > power) power = p;
                }
                return power;
            } else {
                int power = 0;
                for (RadarEntry e : entries) {
                    if (!e.redstone) continue;
                    if (e.blipLevel + 1 > power) {
                        power = e.blipLevel + 1;
                    }
                }
                return power;
            }
        }
        return 0;
    }

    @Override
    public void setPower(long i) {
        power = i;
    }

    @Override
    public long getPower() {
        return power;
    }

    @Override
    public long getMaxPower() {
        return maxPower;
    }

    @Override
    public boolean canConnect(Direction dir) {
        return dir != Direction.UP && dir != Direction.DOWN;
    }

    @Override
    public boolean hasPermission(Player player) {
        return this.isUsableByPlayer(player);
    }

    @Override
    public void receiveControl(CompoundTag data) {
    }

    @Override
    public void receiveControl(Player player, CompoundTag data) {
        if (data.contains("missiles")) this.scanMissiles = !this.scanMissiles;
        if (data.contains("shells")) this.scanShells = !this.scanShells;
        if (data.contains("players")) this.scanPlayers = !this.scanPlayers;
        if (data.contains("smart")) this.smartMode = !this.smartMode;
        if (data.contains("red")) this.redMode = !this.redMode;
        if (data.contains("map")) this.showMap = !this.showMap;
        if (data.contains("clear")) this.clearFlag = true;

        if (data.contains("link")) {
            int id = data.getInt("link");
            ItemStack link = inventory.getStackInSlot(id);

            if (link.getItem() == ModItems.SAT_RELAY.get()) {
                Satellite sat = Objects.requireNonNull(SatelliteSavedData.getData(Objects.requireNonNull(level))).getSatFromFreq(ISatChip.getFreq(link));
                if (sat instanceof SatelliteLaser) {
                    if (data.contains("launchPosX")) {
                        int x = data.getInt("launchPosX");
                        int z = data.getInt("launchPosZ");
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                        sat.onClick(level, x, z);
                    }
                }
                if (sat instanceof SatelliteHorizons) {
                    if (data.contains("launchPosX")) {
                        int x = data.getInt("launchPosX");
                        int z = data.getInt("launchPosZ");
                        int y = 60;
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                        sat.onCoordAction(level, player, x, y, z);
                    }
                }
                if (sat instanceof SatelliteResonator) {
                    if (data.contains("launchPosX")) {
                        int x = data.getInt("launchPosX");
                        int z = data.getInt("launchPosZ");
                        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                        sat.onCoordAction(level, player, x, y, z);
                    }
                }
            }
            if (link.getItem() == ModItems.RADAR_LINKER.get()) {
                BlockPos pos = ItemCoordinateBase.getPosition(link);
                if (pos != null) {
                    BlockEntity te = Objects.requireNonNull(level).getBlockEntity(pos);
                    if (te instanceof IRadarCommandReceiver rec) {
                        if (data.contains("launchEntity")) {
                            Entity entity = level.getEntity(data.getInt("launchEntity"));
                            if (entity != null) {
                                if (rec.sendCommandEntity(entity)) {
                                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                            ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                                }
                            }
                        } else if (data.contains("launchPosX")) {
                            int x = data.getInt("launchPosX");
                            int z = data.getInt("launchPosZ");
                            if (rec.sendCommandPosition(x, worldPosition.getY(), z)) {
                                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                        ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    private AABB renderBoundingBox = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBoundingBox == null) {
            renderBoundingBox = new AABB(
                    worldPosition.getX() - 1,
                    worldPosition.getY(),
                    worldPosition.getZ() - 1,
                    worldPosition.getX() + 2,
                    worldPosition.getY() + 3,
                    worldPosition.getZ() + 2
            );
        }
        return renderBoundingBox;
    }

    @Override
    public boolean isUsableByPlayer(Player player) {
        return Objects.requireNonNull(level).getBlockEntity(worldPosition) == this;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        // Это для ID = 1 (слоты)
        return null; // Будет обработано через provideContainer
    }

    // Static fields for entity detection system
    public static List<Function<Triplet<Entity, Object, IRadarDetectableNT.RadarScanParams>, RadarEntry>> converters = new ArrayList<>();
    public static List<Class<?>> classes = new ArrayList<>();
    public static List<Entity> matchingEntities = new ArrayList<>();

    public static void updateSystem() {
        matchingEntities.clear();
        // В 1.20.1 нужно использовать ServerLevel или другой подход
        // Временно оставляем пустым
    }

    public static void registerEntityClasses() {
        classes.add(IRadarDetectableNT.class);
        classes.add(Player.class);
    }

    public static void registerConverters() {
        converters.add(x -> {
            Entity e = x.getX();
            if (e instanceof IRadarDetectableNT detectable) {
                if (detectable.canBeSeenBy(x.getY()) && detectable.paramsApplicable(x.getZ())) {
                    return new RadarEntry(detectable, e, detectable.suppliesRedstone(x.getZ()));
                }
            }
            return null;
        });

        converters.add(x -> {
            Entity e = x.getX();
            IRadarDetectableNT.RadarScanParams params = x.getZ();
            if (e instanceof IRadarDetectableNT detectable && params.scanMissiles) {
                return new RadarEntry(detectable, e, detectable.suppliesRedstone(params));
            }
            return null;
        });

        converters.add(x -> {
            if (x.getX() instanceof Player && x.getZ().scanPlayers) {
                return new RadarEntry((Player) x.getX());
            }
            return null;
        });
    }

    private static class MathHelper {
        public static int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }

        public static int clamp(double value, int min, int max) {
            return (int) Math.max(min, Math.min(max, value));
        }
    }
}