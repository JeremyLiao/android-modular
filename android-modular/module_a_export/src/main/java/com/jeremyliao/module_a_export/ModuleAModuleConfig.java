package com.jeremyliao.module_a_export;

import com.jeremyliao.modular.IInterface;
import com.jeremyliao.modular.IModuleConfig;
import com.jeremyliao.modular.IEvent;

/**
 * Created by liaohailiang on 2018/8/17.
 */
public class ModuleAModuleConfig implements IModuleConfig {

    public static final String MODULE_NAME = "com.jeremyliao.module_a";

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public Class<? extends IEvent> getEventDefineClass() {
        return ModuleAEvents.class;
    }

    @Override
    public Class<? extends IInterface> getRpcInterfaceClass() {
        return ModuleAInterface.class;
    }

    @Override
    public String getRpcImplementClassName() {
        return "com.jeremyliao.module_a.ModuleAInterfaceImpl";
    }
}
