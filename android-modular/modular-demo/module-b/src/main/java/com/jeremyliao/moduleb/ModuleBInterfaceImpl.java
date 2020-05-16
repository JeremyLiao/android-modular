package com.jeremyliao.moduleb;

import android.content.Context;
import android.content.Intent;

import com.jeremyliao.base.anotation.ModuleService;
import com.jeremyliao.moduleb.export.ModuleBInterface;

/**
 * Created by liaohailiang on 2018/8/19.
 */
@ModuleService(interfaceDefine = ModuleBInterface.class)
public class ModuleBInterfaceImpl implements ModuleBInterface {
    @Override
    public void launchModuleBMainPage(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, ModuleBActivity.class));
    }
}
