package com.hbm.tileentity.network;

import com.hbm.inventory.material.NTMMaterial;

public interface IRenderFoundry {

    /** Returns whether a molten metal layer should be rendered in the TESR */
    boolean shouldRender();
    /** Returns the Y-offset of the molten metal layer */
    double getFillLevel();
    /** Returns the NTM Mat used, mainly for the color */
    NTMMaterial getMat();

    /* Return size constraints for the rectangle */
    double minX();
    double maxX();
    double minZ();
    double maxZ();
    double moldHeight();
    double outHeight();
}
