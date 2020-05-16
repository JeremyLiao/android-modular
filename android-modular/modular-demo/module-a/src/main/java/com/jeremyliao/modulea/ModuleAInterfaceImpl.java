package com.jeremyliao.modulea;

import android.content.Context;
import android.content.Intent;

import com.jeremyliao.base.anotation.ModuleService;
import com.jeremyliao.modulea.export.ModuleAInterface;

/**
 * Created by liaohailiang on 2018/8/19.
 */
@ModuleService(interfaceDefine = ModuleAInterface.class)
public class ModuleAInterfaceImpl implements ModuleAInterface {
    @Override
    public String getUserName() {
        return "Jeremy Liao";
    }

    @Override
    public void launchModuleAMainPage(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, ModuleAActivity.class));
    }
}
