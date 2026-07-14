package com.hbm.uninos.networkproviders;

import com.hbm.api.energy.EnergyNet;
import com.hbm.uninos.INetworkProvider;

public class EnergyNetProvider implements INetworkProvider<EnergyNet> {

    public static final EnergyNetProvider INSTANCE = new EnergyNetProvider();

    private EnergyNetProvider() {}

    @Override
    public EnergyNet provideNetwork() {
        return new EnergyNet();
    }
}