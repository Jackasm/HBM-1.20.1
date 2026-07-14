package com.hbm.api.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.uninos.NodeNet;
import com.hbm.util.HBMEnums;
import com.hbm.util.Tuple.Pair;

import java.util.*;

public class FluidNet extends NodeNet<IFluidReceiver, IFluidProvider, FluidNode> {

    public long fluidTracker = 0L;

    protected static final int TIMEOUT = 3000;
    protected static long currentTime = 0;
    protected FluidTypeHBM type;

    // Provider storage by pressure
    public long[] fluidAvailable = new long[IFluidUser.HIGHEST_VALID_PRESSURE + 1];
    public List<Pair<IFluidProvider, Long>>[] providers = new ArrayList[IFluidUser.HIGHEST_VALID_PRESSURE + 1];

    // Receiver storage by pressure and priority
    public long[][] fluidDemand = new long[IFluidUser.HIGHEST_VALID_PRESSURE + 1][HBMEnums.ConnectionPriority.values().length];
    public List<Pair<IFluidReceiver, Long>>[][] receivers = new ArrayList[IFluidUser.HIGHEST_VALID_PRESSURE + 1][HBMEnums.ConnectionPriority.values().length];
    public long[] transferred = new long[IFluidUser.HIGHEST_VALID_PRESSURE + 1];

    public FluidNet(FluidTypeHBM type) {
        this.type = type;

        // Initialize arrays
        for (int i = 0; i <= IFluidUser.HIGHEST_VALID_PRESSURE; i++) {
            providers[i] = new ArrayList<>();
            for (int j = 0; j < HBMEnums.ConnectionPriority.values().length; j++) {
                receivers[i][j] = new ArrayList<>();
            }
        }
    }

    @Override
    public void tick() {
        if (getReceivers().isEmpty() || getProviders().isEmpty()) return;

        currentTime = System.currentTimeMillis();

        setupProviders();
        setupReceivers();
        transferFluid();

        cleanup();
    }

    /**
     * Collects all providers and calculates available fluid
     */
    private void setupProviders() {
        // Сначала собираем провайдеров для удаления
        List<IFluidProvider> toRemove = new ArrayList<>();

        for (Map.Entry<IFluidProvider, Long> entry : getProviders().entrySet()) {
            if (currentTime - entry.getValue() > TIMEOUT || isInvalid(entry.getKey())) {
                toRemove.add(entry.getKey());
            }
        }

        // Удаляем через публичный метод
        for (IFluidProvider provider : toRemove) {
            removeProvider(provider);
        }

        // Теперь работаем с оставшимися провайдерами
        for (Map.Entry<IFluidProvider, Long> entry : getProviders().entrySet()) {
            IFluidProvider provider = entry.getKey();
            int[] pressureRange = provider.getProvidingPressureRange(type);

            for (int p = pressureRange[0]; p <= pressureRange[1]; p++) {
                long available = Math.min(provider.getFluidAvailable(type, p), provider.getProviderSpeed(type, p));
                providers[p].add(new Pair<>(provider, available));
                fluidAvailable[p] += available;
            }
        }
    }

    /**
     * Collects all receivers and calculates demand
     */
    private void setupReceivers() {
        // Собираем получателей для удаления
        List<IFluidReceiver> toRemove = new ArrayList<>();

        for (Map.Entry<IFluidReceiver, Long> entry : getReceivers().entrySet()) {
            if (currentTime - entry.getValue() > TIMEOUT || isInvalid(entry.getKey())) {
                toRemove.add(entry.getKey());
            }
        }

        // Удаляем через публичный метод
        for (IFluidReceiver receiver : toRemove) {
            removeReceiver(receiver);
        }

        // Работаем с оставшимися
        for (Map.Entry<IFluidReceiver, Long> entry : getReceivers().entrySet()) {
            IFluidReceiver receiver = entry.getKey();
            int[] pressureRange = receiver.getReceivingPressureRange(type);

            for (int p = pressureRange[0]; p <= pressureRange[1]; p++) {
                long required = Math.min(receiver.getDemand(type, p), receiver.getReceiverSpeed(type, p));
                int priority = receiver.getFluidPriority().ordinal();
                receivers[p][priority].add(new Pair<>(receiver, required));
                fluidDemand[p][priority] += required;
            }
        }
    }

    /**
     * Distributes fluid from providers to receivers
     */
    private void transferFluid() {
        long[] received = new long[IFluidUser.HIGHEST_VALID_PRESSURE + 1];
        long[] unaccounted = new long[IFluidUser.HIGHEST_VALID_PRESSURE + 1];

        for (int p = 0; p <= IFluidUser.HIGHEST_VALID_PRESSURE; p++) {
            long totalAvailable = fluidAvailable[p];

            // Process priorities from highest to lowest
            for (int priority = HBMEnums.ConnectionPriority.values().length - 1; priority >= 0; priority--) {
                long demand = fluidDemand[p][priority];
                if (demand <= 0) continue;

                long toTransfer = Math.min(demand, totalAvailable);
                if (toTransfer <= 0) continue;

                long priorityReceived = 0;

                // Distribute weighted by demand
                for (Pair<IFluidReceiver, Long> entry : receivers[p][priority]) {
                    double weight = (double) entry.value() / (double) demand;
                    long toSend = (long) Math.max(toTransfer * weight, 0);

                    toSend -= entry.key().transferFluid(type, p, toSend);
                    priorityReceived += toSend;
                    fluidTracker += toSend;
                }

                received[p] += priorityReceived;
                totalAvailable -= priorityReceived;
            }

            unaccounted[p] = received[p];
        }

        // Deduct from providers proportionally
        for (int p = 0; p <= IFluidUser.HIGHEST_VALID_PRESSURE; p++) {
            if (fluidAvailable[p] == 0 || providers[p].isEmpty()) continue;

            for (Pair<IFluidProvider, Long> entry : providers[p]) {
                double weight = (double) entry.value() / (double) fluidAvailable[p];
                long toUse = (long) Math.max(received[p] * weight, 0);
                entry.key().useUpFluid(type, p, toUse);
                unaccounted[p] -= toUse;
            }
        }

        // Handle rounding errors by randomly assigning remaining fluid
        for (int p = 0; p <= IFluidUser.HIGHEST_VALID_PRESSURE; p++) {
            int iterations = 100;
            while (iterations-- > 0 && unaccounted[p] > 0 && !providers[p].isEmpty()) {
                Pair<IFluidProvider, Long> selected = providers[p].get(RANDOM.nextInt(providers[p].size()));
                IFluidProvider provider = selected.key();

                long toUse = Math.min(unaccounted[p], provider.getFluidAvailable(type, p));
                provider.useUpFluid(type, p, toUse);
                unaccounted[p] -= toUse;
            }
        }
    }

    /**
     * Clears temporary storage for the next tick
     */
    public void cleanup() {
        for (int p = 0; p <= IFluidUser.HIGHEST_VALID_PRESSURE; p++) {
            fluidAvailable[p] = 0;
            providers[p].clear();
            transferred[p] = 0;

            for (int priority = 0; priority < HBMEnums.ConnectionPriority.values().length; priority++) {
                fluidDemand[p][priority] = 0;
                receivers[p][priority].clear();
            }
        }
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void destroy() {
        super.destroy();
        // Additional cleanup if needed
    }
}