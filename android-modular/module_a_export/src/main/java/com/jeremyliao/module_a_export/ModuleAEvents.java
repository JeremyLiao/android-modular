package com.jeremyliao.module_a_export;


import com.jeremyliao.modular_base.anotation.ModuleEvents;
import com.jeremyliao.modular_base.base.IEvent;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@ModuleEvents(module = "module_a")
public class ModuleAEvents implements IEvent {

    public static final String SHOW_TOAST = "show_toast";
}
