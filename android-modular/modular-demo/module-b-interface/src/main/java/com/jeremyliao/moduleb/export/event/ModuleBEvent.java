package com.jeremyliao.moduleb.export.event;

import com.jeremyliao.liveeventbus.core.LiveEvent;

/**
 * Created by liaohailiang on 2019-08-30.
 */
public class ModuleBEvent implements LiveEvent {
    public final String content;

    public ModuleBEvent(String content) {
        this.content = content;
    }
}
