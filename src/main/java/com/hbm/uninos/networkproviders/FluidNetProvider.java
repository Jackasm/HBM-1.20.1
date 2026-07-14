package com.hbm.uninos.networkproviders;

import com.hbm.api.fluid.FluidNet;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.uninos.INetworkProvider;

public class FluidNetProvider implements INetworkProvider<FluidNet> {

    protected FluidTypeHBM type;

    public FluidNetProvider(FluidTypeHBM type) {
        this.type = type;
    }

    @Override
    public FluidNet provideNetwork() {
        return new FluidNet(type);
    }
}
