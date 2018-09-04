package com.jeremyliao.modular_base.anotation;

import com.jeremyliao.modular_base.base.IInterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liaohailiang on 2018/8/24.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ModuleService {
    Class<? extends IInterface> interfaceDefine();

    String module();

    boolean singleton() default false;
}
