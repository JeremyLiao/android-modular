package com.jeremyliao.modular_base.inner.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaohailiang on 2018/9/3.
 */
public class ModuleInfo {

    public static ModuleInfo newInstance(String module) {
        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.serviceInfos = new ArrayList<>();
        moduleInfo.module = module;
        return moduleInfo;
    }

    private String module;
    private List<ModuleServiceInfo> serviceInfos;
    private ModuleEventsInfo eventsInfo;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<ModuleServiceInfo> getServiceInfos() {
        return serviceInfos;
    }

    public void setServiceInfos(List<ModuleServiceInfo> serviceInfos) {
        this.serviceInfos = serviceInfos;
    }

    public ModuleEventsInfo getEventsInfo() {
        return eventsInfo;
    }

    public void setEventsInfo(ModuleEventsInfo eventsInfo) {
        this.eventsInfo = eventsInfo;
    }
}
