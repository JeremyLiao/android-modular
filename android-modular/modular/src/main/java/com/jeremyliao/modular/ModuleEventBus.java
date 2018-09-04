package com.jeremyliao.modular;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jeremyliao.modular.liveevent.LiveEvent;
import com.jeremyliao.modular_base.inner.bean.ModuleEventsInfo;
import com.jeremyliao.modular_base.inner.bean.ModuleInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public final class ModuleEventBus {

    private final Map<String, Map<String, BusLiveEvent<Object>>> bus;

    private ModuleEventBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final ModuleEventBus DEFAULT_BUS = new ModuleEventBus();
    }

    public static ModuleEventBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    private final Observable empty = new EmptyObservable();

    void init(List<ModuleInfo> moduleInfos) {
        if (moduleInfos == null) {
            return;
        }
        if (moduleInfos.size() == 0) {
            return;
        }
        for (ModuleInfo moduleInfo : moduleInfos) {
            ModuleEventsInfo eventsInfo = moduleInfo.getEventsInfo();
            if (eventsInfo == null) {
                continue;
            }
            if (TextUtils.isEmpty(eventsInfo.getModule())) {
                continue;
            }
            if (eventsInfo.getEvents() == null) {
                continue;
            }
            if (!bus.containsKey(eventsInfo.getModule())) {
                bus.put(eventsInfo.getModule(), new HashMap<String, BusLiveEvent<Object>>());
            }
            Map<String, BusLiveEvent<Object>> eventMap = bus.get(eventsInfo.getModule());
            for (String key : eventsInfo.getEvents()) {
                if (!eventMap.containsKey(key)) {
                    eventMap.put(key, new BusLiveEvent<Object>());
                }
            }
        }
    }

    public synchronized <T> Observable<T> with(String module, String eventName, Class<T> type) {
        if (!bus.containsKey(module)) {
            //Module not defined
            return empty;
        }
        Map<String, BusLiveEvent<Object>> moduleMap = bus.get(module);
        if (!moduleMap.containsKey(eventName)) {
            //Event not defined
            return empty;
        }
        return (Observable<T>) moduleMap.get(eventName);
    }

    public Observable<Object> with(String module, String eventName) {
        return with(module, eventName, Object.class);
    }

    public interface Observable<T> {
        void setValue(T value);

        void postValue(T value);

        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        void observeForever(@NonNull Observer<T> observer);

        void observeStickyForever(@NonNull Observer<T> observer);

        void removeObserver(@NonNull Observer<T> observer);
    }

    private static class EmptyObservable<T> implements Observable<T> {
        @Override
        public void setValue(T value) {
            //Empty implement
        }

        @Override
        public void postValue(T value) {
            //Empty implement
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void observeForever(@NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void observeStickyForever(@NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            //Empty implement
        }
    }

    private static class BusLiveEvent<T> extends LiveEvent<T> implements Observable<T> {
        @Override
        protected Lifecycle.State observerActiveLevel() {
            return super.observerActiveLevel();
        }
    }

}
