package com.hbm.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class BobHiddenTrigger extends SimpleCriterionTrigger<BobHiddenTrigger.Instance> {

    private static final ResourceLocation ID = ResLocation(MODID, "bob_hidden");

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    protected @NotNull Instance createInstance(@NotNull JsonObject json, @NotNull ContextAwarePredicate player, @NotNull DeserializationContext context) {
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