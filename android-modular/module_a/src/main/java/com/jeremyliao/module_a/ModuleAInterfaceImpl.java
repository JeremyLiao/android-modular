package com.jeremyliao.module_a;

import com.jeremyliao.modular_base.anotation.ModuleService;
import com.jeremyliao.module_a_export.ModuleAInterface;

/**
 * Created by liaohailiang on 2018/8/19.
 */
@ModuleService(interfaceDefine = ModuleAInterface.class, module = "module_a")
public class ModuleAInterfaceImpl implements ModuleAInterface {
    @Override
    public String getUserName() {
        return "Hello world";
    }
}
