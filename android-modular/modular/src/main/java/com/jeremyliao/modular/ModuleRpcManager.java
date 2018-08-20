package com.jeremyliao.modular;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liaohailiang on 2018/8/19.
 */
public class ModuleRpcManager {

    private static class SingletonHolder {
        private static final ModuleRpcManager INSTANCE = new ModuleRpcManager();
    }

    public static ModuleRpcManager get() {
        return ModuleRpcManager.SingletonHolder.INSTANCE;
    }

    private Map<Class<? extends IInterface>, Object> rpcMap = new HashMap<>();

    private ModuleRpcManager() {
    }

    void init(List<IModuleConfig> moduleConfigs) {
        if (moduleConfigs == null) {
            return;
        }
        if (moduleConfigs.size() == 0) {
            return;
        }
        for (IModuleConfig config : moduleConfigs) {
            Class<? extends IInterface> type = config.getRpcInterfaceClass();
            String implementClassName = config.getRpcImplementClassName();
            if (type == null) {
                break;
            }
            if (implementClassName == null) {
                break;
            }
            Object impl = null;
            try {
                impl = Class.forName(implementClassName).newInstance();
            } catch (Exception e) {
                impl = null;
            }
            if (impl == null) {
                break;
            }
            if (!type.isInterface()) {
                break;
            }
            if (!type.isInstance(impl)) {
                break;
            }
            rpcMap.put(type, impl);
        }
    }

    public <T extends IInterface> T call(Class<T> interfaceType) throws Throwable {
        T instance = (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{interfaceType}, new RpcProxyHandler(interfaceType));
        return instance;
    }

    private class RpcProxyHandler implements InvocationHandler {

        private Class<? extends IInterface> interfaceType;

        public RpcProxyHandler(Class<? extends IInterface> interfaceType) {
            this.interfaceType = interfaceType;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (interfaceType == null) {
                throw new NullPointerException();
            }
            if (!rpcMap.containsKey(interfaceType)) {
                throw new InterfaceNotFoundException(interfaceType);
            }
            return method.invoke(rpcMap.get(interfaceType), objects);
        }
    }

    public static class InterfaceNotFoundException extends RuntimeException {
        public InterfaceNotFoundException(Class type) {
            super("InterfaceNotFoundException: " + type);
        }
    }
}
