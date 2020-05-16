package com.jeremyliao.android.modular.bus;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jeremyliao.liveeventbus.core.Observable;

/**
 * Created by liaohailiang on 2020-05-15.
 */
public class ModularBus {

    public static <T extends IModularEvent> Observable<T> toObservable(final Class<T> eventType) {
        return LiveEventBus.get(eventType.getCanonicalName(), eventType);
    }
}
