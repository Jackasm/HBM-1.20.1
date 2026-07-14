// IBaseAbility.java
package com.hbm.handler.ability;

import net.minecraft.network.chat.Component;

public interface IBaseAbility extends Comparable<IBaseAbility> {
    String getName();

    default String getExtension(int level) {
        return "";
    }

    default Component getFullName(int level) {
        return Component.translatable(getName())
                .append(Component.literal(getExtension(level)));
    }

    default boolean isAllowed() {
        return true;
    }

    default int levels() {
        return 1;
    }

    default int sortOrder() {
        return hashCode();
    }

    @Override
    default int compareTo(IBaseAbility o) {
        return sortOrder() - o.sortOrder();
    }
}