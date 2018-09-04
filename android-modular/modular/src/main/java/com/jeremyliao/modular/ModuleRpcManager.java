package com.jeremyliao.modular;

import android.text.TextUtils;

import com.jeremyliao.modular.exception.InterfaceNotFoundException;
import com.jeremyliao.modular.exception.ModuleNotFoundException;
import com.jeremyliao.modular_base.base.IInterface;
import com.jeremyliao.modular_base.inner.bean.ModuleInfo;
import com.jeremyliao.modular_base.inner.bean.ModuleServiceInfo;

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

    private Map<String, Map<String, ModuleServiceWrapper>> rpcMap = new HashMap<>();

    private ModuleRpcManager() {
    }

    void init(List<ModuleInfo> moduleInfos) {
        if (moduleInfos == null) {
            return;
        }
        if (moduleInfos.size() == 0) {
            return;
        }
        for (ModuleInfo moduleInfo : moduleInfos) {
            if (TextUtils.isEmpty(moduleInfo.getModule())) {
                continue;
            }
            if (moduleInfo.getServiceInfos() == null || moduleInfo.getServiceInfos().size() == 0) {
                continue;
            }
            if (!rpcMap.containsKey(moduleInfo.getModule())) {
                rpcMap.put(moduleInfo.getModule(), new HashMap<String, ModuleServiceWrapper>());
            }
            Map<String, ModuleServiceWrapper> serviceWrapperMap = rpcMap.get(moduleInfo.getModule());
            for (ModuleServiceInfo serviceInfo : moduleInfo.getServiceInfos()) {
                if (!serviceWrapperMap.containsKey(serviceInfo.getInterfaceClassName())) {
                    serviceWrapperMap.put(serviceInfo.getInterfaceClassName(), new ModuleServiceWrapper(serviceInfo));
                }
            }
        }
    }

    public <T extends IInterface> T call(String moduleName, Class<T> interfaceType) throws
            ModuleNotFoundException, InterfaceNotFoundException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        if (!rpcMap.containsKey(moduleName)) {
            throw new ModuleNotFoundException(moduleName);
        }
        Map<String, ModuleServiceWrapper> serviceWrapperMap = rpcMap.get(moduleName);
        if (!serviceWrapperMap.containsKey(interfaceType.getCanonicalName())) {
            throw new InterfaceNotFoundException(interfaceType);
        }
        ModuleServiceWrapper serviceWrapper = serviceWrapperMap.get(interfaceType.getCanonicalName());
        Object target = null;
        if (serviceWrapper.moduleServiceInfo.isSingleton()) {
            if (serviceWrapper.target == null) {
                serviceWrapper.target = Class.forName(serviceWrapper.moduleServiceInfo.getImplementClassName()).newInstance();
            }
            target = serviceWrapper.target;
        } else {
            target = Class.forName(serviceWrapper.moduleServiceInfo.getImplementClassName()).newInstance();
        }
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{interfaceType}, new RpcProxyHandler(target));
    }

    private class RpcProxyHandler implements InvocationHandler {

        private Object target;

        public RpcProxyHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return method.invoke(target, objects);
        }
    }

    private static class ModuleServiceWrapper {
        private ModuleServiceInfo moduleServiceInfo;
        private Object target;

        public ModuleServiceWrapper(ModuleServiceInfo moduleServiceInfo) {
            this.moduleServiceInfo = moduleServiceInfo;
        }
    }
}
