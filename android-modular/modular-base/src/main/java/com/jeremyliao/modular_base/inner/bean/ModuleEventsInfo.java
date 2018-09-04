package com.jeremyliao.modular_base.inner.bean;

import java.util.List;

/**
 * Created by liaohailiang on 2018/9/3.
 */
public class ModuleEventsInfo {

    private String module;
    private List<String> events;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }
}
