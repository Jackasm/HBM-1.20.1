package com.hbm.inventory.fluid.trait;

import net.minecraft.network.chat.Component;

import java.util.List;

public class FluidTraitsSimple {


    public static class FT_NoID extends FluidTrait {
        @Override public void addInfoHidden(List<Component> info) {

        }
    }

}