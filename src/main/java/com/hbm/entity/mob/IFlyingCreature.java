package com.hbm.entity.mob;

/**
 * Существа с двумя режимами: ходьба и полёт. В 1.7.10 интерфейс держал только
 * состояние, а переключали его два ИИ-таска; здесь те же два таска живут
 * вложенными классами в {@link EntityPigeon}.
 */
public interface IFlyingCreature {

    int STATE_WALKING = 0;
    int STATE_FLYING = 1;

    int getFlyingState();

    void setFlyingState(int state);
}
