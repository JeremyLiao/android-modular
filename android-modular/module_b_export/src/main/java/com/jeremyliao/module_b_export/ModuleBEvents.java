package com.jeremyliao.module_b_export;


import com.jeremyliao.modular_base.anotation.ModuleEvents;
import com.jeremyliao.modular_base.base.IEvent;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@ModuleEvents(module = "module_b")
public class ModuleBEvents implements IEvent {

    public static final String SAY_HELLO = "say_hello";
}
