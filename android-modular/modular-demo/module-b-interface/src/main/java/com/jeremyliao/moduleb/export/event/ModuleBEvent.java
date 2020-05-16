package com.jeremyliao.moduleb.export.event;

import com.jeremyliao.android.modular.bus.IModularEvent;

/**
 * Created by liaohailiang on 2019-08-30.
 */
public class ModuleBEvent implements IModularEvent {
    final public String content;

    public ModuleBEvent(String content) {
        this.content = content;
    }
}
