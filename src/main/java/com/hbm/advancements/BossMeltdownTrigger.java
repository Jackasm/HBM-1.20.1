package com.hbm.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class BossMeltdownTrigger extends SimpleCriterionTrigger<BossMeltdownTrigger.Instance> {

    private static final ResourceLocation ID = ResLocation(MODID, "boss_meltdown");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        return new Instance(player);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ContextAwarePredicate player) {
            super(ID, player);
        }

        public static Instance instance() {
            return new Instance(ContextAwarePredicate.ANY);
        }
    }
}