package com.jeremyliao.modular.exception;

/**
 * Created by liaohailiang on 2018/8/24.
 */
public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String module) {
        super("ModuleNotFoundException: " + module);
    }
}
