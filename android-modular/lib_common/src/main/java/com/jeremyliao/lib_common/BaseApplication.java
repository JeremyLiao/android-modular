package com.jeremyliao.lib_common;

import android.app.Application;

import com.jeremyliao.modular.ModuleRpcInitHelper;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //ModuleRpcInitHelper 初始化
        ModuleRpcInitHelper.init(this);
    }
}
