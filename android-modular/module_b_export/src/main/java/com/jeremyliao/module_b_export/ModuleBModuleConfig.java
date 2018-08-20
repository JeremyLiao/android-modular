package com.jeremyliao.module_b_export;

import com.jeremyliao.modular.IEvent;
import com.jeremyliao.modular.IInterface;
import com.jeremyliao.modular.IModuleConfig;

/**
 * Created by liaohailiang on 2018/8/17.
 */
public class ModuleBModuleConfig implements IModuleConfig {

    @Override
    public String getModuleName() {
        return "com.jeremyliao.module_b";
    }

    @Override
    public Class<? extends IEvent> getEventDefineClass() {
        return ModuleBEvents.class;
    }

    @Override
    public Class<? extends IInterface> getRpcInterfaceClass() {
        return null;
    }

    @Override
    public String getRpcImplementClassName() {
        return null;
    }
}
