package com.hbm.tileentity;

import com.hbm.util.Library;
import com.hbm.tileentity.TilePort.PortDef;
import net.minecraft.core.Direction;

public class TilePortShapes {

    /** Все 6 сторон. */
    public static PortDef[] around(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x, y, z, Direction.values())
        };
    }

    public static PortDef[] mixer(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x, y, z, Library.POS_X, Library.NEG_X, Library.POS_Z, Library.NEG_Z, Library.NEG_Y),
                PortDef.make(x, y + 2, z, Library.POS_Y),
        };
    }

    public static PortDef[] horizontal(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x, y, z, Library.POS_X, Library.NEG_X, Library.POS_Z, Library.NEG_Z, Library.NEG_Y),
        };
    }

    public static PortDef[] assembler(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x - 1, y, z - 1, Library.NEG_X, Library.NEG_Z),
                PortDef.make(x, y, z - 1, Library.NEG_Z),
                PortDef.make(x + 1, y, z - 1, Library.POS_X, Library.NEG_Z),
                PortDef.make(x + 1, y, z, Library.POS_X),
                PortDef.make(x + 1, y, z + 1, Library.POS_X, Library.POS_Z),
                PortDef.make(x, y, z + 1, Library.POS_Z),
                PortDef.make(x - 1, y, z + 1, Library.NEG_X, Library.POS_Z),
                PortDef.make(x - 1, y, z, Library.NEG_X),
        };
    }

    public static PortDef[] purex(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x + 2, y, z - 1, Library.POS_X),
                PortDef.make(x + 2, y, z, Library.POS_X),
                PortDef.make(x + 2, y, z + 1, Library.POS_X),
                PortDef.make(x + 2, y, z + 2, Library.POS_X, Library.POS_Z),
                PortDef.make(x + 1, y, z + 2, Library.POS_Z),
                PortDef.make(x, y, z + 2, Library.POS_Z),
                PortDef.make(x - 1, y, z + 2, Library.POS_Z),
                PortDef.make(x - 2, y, z + 2, Library.NEG_X, Library.POS_Z),
                PortDef.make(x - 2, y, z + 1, Library.NEG_X),
                PortDef.make(x - 2, y, z, Library.NEG_X),
                PortDef.make(x - 2, y, z - 1, Library.NEG_X),
                PortDef.make(x - 2, y, z - 2, Library.NEG_X, Library.NEG_Z),
                PortDef.make(x - 1, y, z - 2, Library.NEG_Z),
                PortDef.make(x, y, z - 2, Library.NEG_Z),
                PortDef.make(x + 1, y, z - 2, Library.NEG_Z),
                PortDef.make(x + 2, y, z - 2, Library.POS_X, Library.NEG_Z),
        };
    }

    public static PortDef[] comboven(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x - 1, y, z - 1, Library.NEG_X, Library.NEG_Z),
                PortDef.make(x, y, z - 1, Library.NEG_Z),
                PortDef.make(x + 1, y, z - 1, Library.POS_X, Library.NEG_Z),
                PortDef.make(x + 1, y, z, Library.POS_X),
                PortDef.make(x + 1, y, z + 1, Library.POS_X, Library.POS_Z),
                PortDef.make(x, y, z + 1, Library.POS_Z),
                PortDef.make(x - 1, y, z + 1, Library.NEG_X, Library.POS_Z),
                PortDef.make(x - 1, y, z, Library.NEG_X),
                PortDef.make(x - 1, y + 1, z - 1, Library.NEG_X, Library.NEG_Z),
                PortDef.make(x, y + 1, z - 1, Library.NEG_Z),
                PortDef.make(x + 1, y + 1, z - 1, Library.POS_X, Library.NEG_Z),
                PortDef.make(x + 1, y + 1, z, Library.POS_X),
                PortDef.make(x + 1, y + 1, z + 1, Library.POS_X, Library.POS_Z),
                PortDef.make(x, y + 1, z + 1, Library.POS_Z),
                PortDef.make(x - 1, y + 1, z + 1, Library.NEG_X, Library.POS_Z),
                PortDef.make(x - 1, y + 1, z, Library.NEG_X),
        };
    }

    public static PortDef[] refinery(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x - 1, y, z - 1, Library.NEG_X, Library.NEG_Z),
                PortDef.make(x + 1, y, z - 1, Library.POS_X, Library.NEG_Z),
                PortDef.make(x + 1, y, z + 1, Library.POS_X, Library.POS_Z),
                PortDef.make(x - 1, y, z + 1, Library.NEG_X, Library.POS_Z),
        };
    }

    public static PortDef[] flare(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x, y, z - 1, Library.NEG_Z),
                PortDef.make(x + 1, y, z, Library.POS_X),
                PortDef.make(x, y, z + 1, Library.POS_Z),
                PortDef.make(x - 1, y, z, Library.NEG_X),
        };
    }

    public static PortDef[] solderer(int x, int y, int z, int meta) {
        Direction dir = Direction.from3DDataValue(meta - 10);
        Direction rot = dir.getClockWise();

        return new PortDef[] {
                PortDef.make(x, y, z, dir, rot.getOpposite()),
                PortDef.make(x + rot.getStepX(), y, z + rot.getStepZ(), dir, rot),
                PortDef.make(x - dir.getStepX(), y, z - dir.getStepZ(), dir.getOpposite(), rot.getOpposite()),
                PortDef.make(x - dir.getStepX() + rot.getStepX(), y, z - dir.getStepZ() + rot.getStepZ(),
                        dir.getOpposite(), rot),
        };
    }

    public static PortDef[] condenser(int x, int y, int z, int meta) {
        Direction dir = Direction.from3DDataValue(meta - 10);
        Direction rot = dir.getClockWise();

        return new PortDef[] {
                PortDef.make(x + rot.getStepX() * 3, y + 1, z + rot.getStepZ() * 3, rot),
                PortDef.make(x - rot.getStepX() * 3, y + 1, z - rot.getStepZ() * 3, rot.getOpposite()),
                PortDef.make(x + dir.getStepX() - rot.getStepX(), y + 1, z + dir.getStepZ() - rot.getStepZ(), dir),
                PortDef.make(x + dir.getStepX() + rot.getStepX(), y + 1, z + dir.getStepZ() + rot.getStepZ(), dir),
                PortDef.make(x - dir.getStepX() - rot.getStepX(), y + 1, z - dir.getStepZ() - rot.getStepZ(), dir.getOpposite()),
                PortDef.make(x - dir.getStepX() + rot.getStepX(), y + 1, z - dir.getStepZ() + rot.getStepZ(), dir.getOpposite()),
        };
    }

    public static PortDef[] auxTower(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x, y, z - 2, Library.NEG_Z),
                PortDef.make(x + 2, y, z, Library.POS_X),
                PortDef.make(x, y, z + 2, Library.POS_Z),
                PortDef.make(x - 2, y, z, Library.NEG_X),
        };
    }

    public static PortDef[] bigTower(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x - 3, y, z - 4, Library.NEG_Z),
                PortDef.make(x, y, z - 4, Library.NEG_Z),
                PortDef.make(x + 3, y, z - 4, Library.NEG_Z),
                PortDef.make(x + 4, y, z - 3, Library.POS_X),
                PortDef.make(x + 4, y, z, Library.POS_X),
                PortDef.make(x + 4, y, z + 3, Library.POS_X),
                PortDef.make(x - 3, y, z + 4, Library.POS_Z),
                PortDef.make(x, y, z + 4, Library.POS_Z),
                PortDef.make(x + 3, y, z + 4, Library.POS_Z),
                PortDef.make(x - 4, y, z - 3, Library.NEG_X),
                PortDef.make(x - 4, y, z, Library.NEG_X),
                PortDef.make(x - 4, y, z + 3, Library.NEG_X),
        };
    }

    public static PortDef[] liquefactor(int x, int y, int z) {
        return new PortDef[] {
                PortDef.make(x, y + 3, z, Library.POS_Y),
                PortDef.make(x, y, z, Library.NEG_Y),
                PortDef.make(x + 1, y + 1, z, Library.POS_X),
                PortDef.make(x - 1, y + 1, z, Library.NEG_X),
                PortDef.make(x, y + 1, z + 1, Library.POS_Z),
                PortDef.make(x, y + 1, z - 1, Library.NEG_Z),
        };
    }
}