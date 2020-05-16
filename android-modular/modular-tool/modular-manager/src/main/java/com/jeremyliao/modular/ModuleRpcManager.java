package com.jeremyliao.modular;

import com.google.gson.reflect.TypeToken;
import com.jeremyliao.base.base.IInterface;
import com.jeremyliao.base.inner.bean.ModuleServiceInfo;
import com.jeremyliao.base.inner.utils.GsonUtil;
import com.jeremyliao.modular.exception.InterfaceNotFoundException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    private Map<String, ModuleServiceWrapper> serviceMap = new HashMap<>();

    private ModuleRpcManager() {
        init();
    }

    private void init() {
        List<ModuleServiceInfo> serviceInfos = getModuleInfo();
        if (serviceInfos == null || serviceInfos.size() == 0) {
            return;
        }
        for (ModuleServiceInfo serviceInfo : serviceInfos) {
            serviceMap.put(serviceInfo.getInterfaceClassName(), new ModuleServiceWrapper(serviceInfo));
        }
    }

    private static String getModuleJson() {
        return null;
    }

    private List<ModuleServiceInfo> getModuleInfo() {
        String json = getModuleJson();
        if (json == null) {
            return null;
        }
        Type type = new TypeToken<ArrayList<ModuleServiceInfo>>() {
        }.getType();
        return GsonUtil.fromJson(json, type);
    }

    public <T extends IInterface> T call(Class<T> interfaceType) throws
            InterfaceNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String interfaceName = interfaceType.getCanonicalName();
        if (!serviceMap.containsKey(interfaceName)) {
            throw new InterfaceNotFoundException(interfaceType);
        }
        ModuleServiceWrapper serviceWrapper = serviceMap.get(interfaceName);
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

        RpcProxyHandler(Object target) {
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

        ModuleServiceWrapper(ModuleServiceInfo moduleServiceInfo) {
            this.moduleServiceInfo = moduleServiceInfo;
        }
    }
}
