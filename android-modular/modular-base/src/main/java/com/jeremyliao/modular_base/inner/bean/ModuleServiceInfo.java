package com.jeremyliao.modular_base.inner.bean;

import com.jeremyliao.modular_base.inner.utils.GsonUtil;

/**
 * Created by liaohailiang on 2018/9/3.
 */
public class ModuleServiceInfo {

    private String module;
    private String interfaceClassName;
    private String implementClassName;
    private boolean singleton;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getInterfaceClassName() {
        return interfaceClassName;
    }

    public void setInterfaceClassName(String interfaceClassName) {
        this.interfaceClassName = interfaceClassName;
    }

    public String getImplementClassName() {
        return implementClassName;
    }

    public void setImplementClassName(String implementClassName) {
        this.implementClassName = implementClassName;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this);
    }
}
