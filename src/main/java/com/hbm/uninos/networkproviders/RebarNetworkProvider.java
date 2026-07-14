package com.hbm.uninos.networkproviders;

import com.hbm.api.rebar.RebarNetwork;
import com.hbm.uninos.INetworkProvider;

public class RebarNetworkProvider implements INetworkProvider<RebarNetwork> {
    public static final RebarNetworkProvider INSTANCE = new RebarNetworkProvider();

    @Override
    public RebarNetwork provideNetwork() {
        return new RebarNetwork();
    }
}