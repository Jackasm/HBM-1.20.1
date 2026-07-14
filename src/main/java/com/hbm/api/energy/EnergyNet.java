package com.hbm.api.energy;

import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.NodeNet;

import java.util.*;
import java.util.Map.Entry;

public class EnergyNet extends NodeNet<IEnergyReceiver, IEnergyProvider, EnergyNode> {

    private record ProviderEntry(IEnergyProvider provider, long available) {}
    private record ReceiverEntry(IEnergyReceiver receiver, long demand) {}

    public long energyTracker = 0L;
    protected static final int TIMEOUT = 3000; // мс, после которых провайдер/потребитель считается мёртвым

    @Override
    public void tick() {
        // Очистка мёртвых ссылок перед каждым тиком
        cleanup();

        Map<IEnergyProvider, Long> providersMap = getProviders();
        Map<IEnergyReceiver, Long> receiversMap = getReceivers();

        if (providersMap.isEmpty() || receiversMap.isEmpty()) return;

        long timestamp = System.currentTimeMillis();
        List<ProviderEntry> activeProviders = new ArrayList<>();
        long totalAvailable = 0;

        // Сбор активных провайдеров
        List<IEnergyProvider> toRemoveProviders = new ArrayList<>();
        for (Entry<IEnergyProvider, Long> entry : providersMap.entrySet()) {
            IEnergyProvider provider = entry.getKey();
            if (timestamp - entry.getValue() > TIMEOUT || NodeNet.isInvalid(provider)) {
                toRemoveProviders.add(provider);
                continue;
            }
            long available = Math.min(provider.getPower(), provider.getProviderSpeed());
            if (available > 0) {
                activeProviders.add(new ProviderEntry(provider, available));
                totalAvailable += available;
            }
        }
        for (IEnergyProvider provider : toRemoveProviders) {
            removeProvider(provider);
        }

        // Сбор потребителей по приоритетам
        IEnergyReceiver.ConnectionPriority[] priorities = IEnergyReceiver.ConnectionPriority.values();
        List<ReceiverEntry>[] receiversByPriority = new ArrayList[priorities.length];
        for (int i = 0; i < receiversByPriority.length; i++) {
            receiversByPriority[i] = new ArrayList<>();
        }
        long[] demandByPriority = new long[priorities.length];
        long totalDemand = 0;

        List<IEnergyReceiver> toRemoveReceivers = new ArrayList<>();
        for (Entry<IEnergyReceiver, Long> entry : receiversMap.entrySet()) {
            IEnergyReceiver receiver = entry.getKey();
            if (timestamp - entry.getValue() > TIMEOUT || NodeNet.isInvalid(receiver)) {
                toRemoveReceivers.add(receiver);
                continue;
            }
            long demand = Math.min(receiver.getMaxPower() - receiver.getPower(), receiver.getReceiverSpeed());
            if (demand > 0) {
                int priorityOrd = receiver.getPriority().ordinal();
                receiversByPriority[priorityOrd].add(new ReceiverEntry(receiver, demand));
                demandByPriority[priorityOrd] += demand;
                totalDemand += demand;
            }
        }
        for (IEnergyReceiver receiver : toRemoveReceivers) {
            removeReceiver(receiver);
        }

        long toTransfer = Math.min(totalAvailable, totalDemand);
        long energyUsed = 0;

        // Распределение: от HIGHEST к LOWEST
        for (int i = priorities.length - 1; i >= 0; i--) {
            long priorityDemand = demandByPriority[i];
            if (priorityDemand == 0) continue;
            for (ReceiverEntry entry : receiversByPriority[i]) {
                double weight = (double) entry.demand / (double) priorityDemand;
                long toSend = (long) Math.max(toTransfer * weight, 0D);
                long leftover = entry.receiver.transferPower(toSend);
                energyUsed += (toSend - leftover);
            }
            toTransfer = Math.max(0, toTransfer - energyUsed);
        }

        this.energyTracker += energyUsed;
        long leftoverEnergy = energyUsed;

        // Списание энергии с провайдеров пропорционально их вкладу
        if (totalAvailable > 0) {
            for (ProviderEntry entry : activeProviders) {
                double weight = (double) entry.available / (double) totalAvailable;
                long toUse = (long) Math.max(energyUsed * weight, 0D);
                entry.provider.usePower(toUse);
                leftoverEnergy -= toUse;
            }
        }

        // Компенсация ошибок округления – остаток списываем со случайного провайдера
        int safeGuard = 100;
        while (safeGuard-- > 0 && leftoverEnergy > 0 && !activeProviders.isEmpty()) {
            ProviderEntry victim = activeProviders.get(RANDOM.nextInt(activeProviders.size()));
            long toUse = Math.min(leftoverEnergy, victim.provider.getPower());
            victim.provider.usePower(toUse);
            leftoverEnergy -= toUse;
        }
    }

    /**
     * Особый режим для диодов – передаёт энергию в сеть без провайдеров (сам выступает источником).
     * @param power количество энергии для передачи
     * @return невостребованный остаток
     */
    public long sendPowerDiode(long power) {
        cleanup();

        Map<IEnergyReceiver, Long> receiversMap = getReceivers();
        if (receiversMap.isEmpty()) return power;

        long timestamp = System.currentTimeMillis();
        IEnergyReceiver.ConnectionPriority[] priorities = IEnergyReceiver.ConnectionPriority.values();
        List<ReceiverEntry>[] receiversByPriority = new ArrayList[priorities.length];
        for (int i = 0; i < receiversByPriority.length; i++) {
            receiversByPriority[i] = new ArrayList<>();
        }
        long[] demandByPriority = new long[priorities.length];
        long totalDemand = 0;

        Iterator<Entry<IEnergyReceiver, Long>> recIt = receiversMap.entrySet().iterator();
        while (recIt.hasNext()) {
            Entry<IEnergyReceiver, Long> entry = recIt.next();
            IEnergyReceiver receiver = entry.getKey();
            if (timestamp - entry.getValue() > TIMEOUT || NodeNet.isInvalid(receiver)) {
                recIt.remove();
                removeReceiver(receiver);
                continue;
            }
            long demand = Math.min(receiver.getMaxPower() - receiver.getPower(), receiver.getReceiverSpeed());
            if (demand > 0) {
                int p = receiver.getPriority().ordinal();
                receiversByPriority[p].add(new ReceiverEntry(receiver, demand));
                demandByPriority[p] += demand;
                totalDemand += demand;
            }
        }

        long toTransfer = Math.min(power, totalDemand);
        long energyUsed = 0;

        for (int i = priorities.length - 1; i >= 0; i--) {
            long priorityDemand = demandByPriority[i];
            if (priorityDemand == 0) continue;
            for (ReceiverEntry entry : receiversByPriority[i]) {
                double weight = (double) entry.demand / (double) priorityDemand;
                long toSend = (long) Math.max(toTransfer * weight, 0D);
                long leftover = entry.receiver.transferPower(toSend);
                energyUsed += (toSend - leftover);
            }
            toTransfer = Math.max(0, toTransfer - energyUsed);
        }

        this.energyTracker += energyUsed;
        return power - energyUsed;
    }
}