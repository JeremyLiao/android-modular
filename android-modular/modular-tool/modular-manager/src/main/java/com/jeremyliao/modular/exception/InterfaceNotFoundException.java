package com.jeremyliao.modular.exception;

/**
 * Created by liaohailiang on 2018/8/24.
 */
public class InterfaceNotFoundException extends RuntimeException {
    public InterfaceNotFoundException(Class type) {
        super("InterfaceNotFoundException: " + type);
    }
}
