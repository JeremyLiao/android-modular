package com.jeremyliao.modular;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jeremyliao.modular.liveevent.LiveEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    void init(List<IModuleConfig> moduleConfigs) {
        if (moduleConfigs == null) {
            return;
        }
        if (moduleConfigs.size() == 0) {
            return;
        }
        for (IModuleConfig config : moduleConfigs) {
            HashMap<String, BusLiveEvent<Object>> moduleMap = new HashMap<>();
            Class<? extends IEvent> type = config.getEventDefineClass();
            if (type != null) {
                Field[] fields = type.getDeclaredFields();
                for (Field field : fields) {
                    int modifiers = field.getModifiers();

                    if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                        try {
                            Object value = field.get(type);
                            if (value instanceof String) {
                                moduleMap.put((String) value, new BusLiveEvent<>());
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            bus.put(config.getModuleName(), moduleMap);
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
