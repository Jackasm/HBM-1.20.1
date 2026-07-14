package com.hbm.api.rebar;

import com.hbm.api.fluid.IFluidProvider;
import com.hbm.api.fluid.IFluidReceiver;
import com.hbm.tileentity.block.TileEntityRebar.RebarNode;
import com.hbm.uninos.NodeNet;

public class RebarNetwork extends NodeNet<IFluidReceiver, IFluidProvider, RebarNode> {
    @Override
    public void tick() {
        // если нужна логика обновления сети
    }
}
