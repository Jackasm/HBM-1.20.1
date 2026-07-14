package com.hbm.explosion;

import com.hbm.interfaces.IExplosionRay;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ExplosionNukeRayBatched implements IExplosionRay {

    public HashMap<ChunkPos, List<FloatTriplet>> perChunk = new HashMap<>();
    public List<ChunkPos> orderedChunks = new ArrayList<>();
    private final CoordComparator comparator = new CoordComparator();

    int posX;
    int posY;
    int posZ;
    Level level;

    int strength;
    int length;
    int speed;
    int gspNumMax;
    int gspNum;
    double gspX;
    double gspY;

    public boolean isAusf3Complete = false;

    // Добавляем список неуничтожимых блоков
    private static final Set<Block> UNBREAKABLE_BLOCKS = Set.of(
            Blocks.BEDROCK,
            Blocks.BARRIER,
            Blocks.END_PORTAL_FRAME,
            Blocks.END_PORTAL,
            Blocks.NETHER_PORTAL,
            Blocks.COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.STRUCTURE_BLOCK,
            Blocks.STRUCTURE_VOID
    );

    public ExplosionNukeRayBatched(Level level, int x, int y, int z, int strength, int speed, int length) {
        this.level = level;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.strength = strength;
        this.speed = speed;
        this.length = length;
        this.gspNumMax = (int) (2.5 * Math.PI * Math.pow(this.strength, 2));
        this.gspNum = 1;
        this.gspX = Math.PI;
        this.gspY = 0.0;
    }

    // Метод для проверки, можно ли уничтожить блок
    private boolean canDestroyBlock(Block block) {
        if (UNBREAKABLE_BLOCKS.contains(block)) {
            return false;
        }
        // Бедрок имеет сопротивление 3600000
        return !(block.getExplosionResistance() >= 3600000.0F);
    }

    private void generateGspUp() {
        if (this.gspNum < this.gspNumMax) {
            int k = this.gspNum + 1;
            double hk = -1.0 + 2.0 * (k - 1.0) / (this.gspNumMax - 1.0);
            this.gspX = Math.acos(hk);

            double prev_lon = this.gspY;
            double lon = prev_lon + 3.6 / Math.sqrt(this.gspNumMax) / Math.sqrt(1.0 - hk * hk);
            this.gspY = lon % (Math.PI * 2);
        } else {
            this.gspX = 0.0;
            this.gspY = 0.0;
        }
        this.gspNum++;
    }

    private Vec3 getSpherical2cartesian() {
        double dx = Math.sin(this.gspX) * Math.cos(this.gspY);
        double dz = Math.sin(this.gspX) * Math.sin(this.gspY);
        double dy = Math.cos(this.gspX);
        return new Vec3(dx, dy, dz);
    }

    public void collectTip(int count) {
        int amountProcessed = 0;

        while (this.gspNumMax >= this.gspNum) {
            Vec3 vec = this.getSpherical2cartesian();

            int length = (int) Math.ceil(strength);
            float res = strength;

            FloatTriplet lastPos = null;
            HashSet<ChunkPos> chunkCoords = new HashSet<>();

            for (int i = 0; i < length; i++) {
                if (i > this.length) break;

                float x0 = (float) (posX + (vec.x * i));
                float y0 = (float) (posY + (vec.y * i));
                float z0 = (float) (posZ + (vec.z * i));

                int iX = (int) Math.floor(x0);
                int iY = (int) Math.floor(y0);
                int iZ = (int) Math.floor(z0);

                double fac = 100 - ((double) i) / ((double) length) * 100;
                fac *= 0.07D;

                BlockPos pos = new BlockPos(iX, iY, iZ);
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();

                // Проверка на неуничтожимый блок
                if (!canDestroyBlock(block)) {
                    break; // Прекращаем этот луч при встрече с бедроком
                }

                if (block != Blocks.AIR) {
                    res -= (float) Math.pow(masqueradeResistance(block), 7.5D - fac);
                }

                if (res > 0 && block != Blocks.AIR) {
                    lastPos = new FloatTriplet(x0, y0, z0);
                    ChunkPos chunkPos = new ChunkPos(iX >> 4, iZ >> 4);
                    chunkCoords.add(chunkPos);
                }

                if (res <= 0 || i + 1 >= this.length || i == length - 1) {
                    break;
                }
            }

            for (ChunkPos pos : chunkCoords) {
                List<FloatTriplet> triplets = perChunk.computeIfAbsent(pos, k -> new ArrayList<>());
                triplets.add(lastPos);
            }

            this.generateGspUp();
            amountProcessed++;
            if (amountProcessed >= count) {
                return;
            }
        }

        orderedChunks.addAll(perChunk.keySet());
        orderedChunks.sort(comparator);
        isAusf3Complete = true;
    }

    public static float masqueradeResistance(Block block) {
        if (block == Blocks.SANDSTONE) return Blocks.STONE.getExplosionResistance();
        if (block == Blocks.OBSIDIAN) return Blocks.STONE.getExplosionResistance() * 3;
        return block.getExplosionResistance();
    }

    public class CoordComparator implements Comparator<ChunkPos> {
        @Override
        public int compare(ChunkPos o1, ChunkPos o2) {
            int chunkX = ExplosionNukeRayBatched.this.posX >> 4;
            int chunkZ = ExplosionNukeRayBatched.this.posZ >> 4;
            int diff1 = Math.abs((chunkX - o1.x)) + Math.abs((chunkZ - o1.z));
            int diff2 = Math.abs((chunkX - o2.x)) + Math.abs((chunkZ - o2.z));
            return Integer.compare(diff1, diff2);
        }
    }

    public void processChunk() {
        if (this.perChunk.isEmpty()) return;

        ChunkPos coord = orderedChunks.get(0);
        List<FloatTriplet> list = perChunk.get(coord);
        HashSet<BlockPos> toRem = new HashSet<>();
        HashSet<BlockPos> toRemTips = new HashSet<>();

        int chunkX = coord.x;
        int chunkZ = coord.z;

        int enter = Math.min(
                Math.abs(posX - (chunkX << 4)),
                Math.abs(posZ - (chunkZ << 4))) - 16;
        enter = Math.max(enter, 0);

        for (FloatTriplet triplet : list) {
            float x = triplet.xCoord;
            float y = triplet.yCoord;
            float z = triplet.zCoord;
            Vec3 vec = new Vec3(x - this.posX, y - this.posY, z - this.posZ);
            double pX = vec.x / vec.length();
            double pY = vec.y / vec.length();
            double pZ = vec.z / vec.length();

            int tipX = (int) Math.floor(x);
            int tipY = (int) Math.floor(y);
            int tipZ = (int) Math.floor(z);

            boolean inChunk = false;
            for (int i = enter; i < vec.length(); i++) {
                int x0 = (int) Math.floor(posX + pX * i);
                int y0 = (int) Math.floor(posY + pY * i);
                int z0 = (int) Math.floor(posZ + pZ * i);

                if ((x0 >> 4) != chunkX || (z0 >> 4) != chunkZ) {
                    if (inChunk) {
                        break;
                    } else {
                        continue;
                    }
                }

                inChunk = true;
                BlockPos pos = new BlockPos(x0, y0, z0);
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();

                // Проверка на неуничтожимый блок
                if (!canDestroyBlock(block)) {
                    break; // Прекращаем при встрече с бедроком
                }

                if (!state.isAir() || !state.getFluidState().isEmpty()) {
                    if (x0 == tipX && y0 == tipY && z0 == tipZ) {
                        toRemTips.add(pos);
                    }
                    toRem.add(pos);
                }
            }
        }

        for (BlockPos pos : toRem) {
            if (toRemTips.contains(pos)) {
                this.handleTip(pos);
            } else {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }

        perChunk.remove(coord);
        orderedChunks.remove(0);
    }

    protected void handleTip(BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public boolean isComplete() {
        return isAusf3Complete && perChunk.isEmpty();
    }

    @Override
    public void cacheChunksTick(int time) {
        if (!isAusf3Complete) {
            collectTip(speed * 10);
        }
    }

    @Override
    public void destructionTick(int time) {
        if (!isAusf3Complete) return;
        long start = System.currentTimeMillis();
        while (!perChunk.isEmpty() && System.currentTimeMillis() < start + time) {
            processChunk();
        }
    }

    @Override
    public void cancel() {
        isAusf3Complete = true;
        if (perChunk != null) perChunk.clear();
        if (orderedChunks != null) orderedChunks.clear();
    }

    public static class FloatTriplet {
        public float xCoord;
        public float yCoord;
        public float zCoord;

        public FloatTriplet(float x, float y, float z) {
            xCoord = x;
            yCoord = y;
            zCoord = z;
        }
    }
}