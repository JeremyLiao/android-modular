package com.jeremyliao.modulea.export;


import android.content.Context;

import com.jeremyliao.base.base.IInterface;

/**
 * Created by liaohailiang on 2018/8/19.
 */
public interface ModuleAInterface extends IInterface {
    String getUserName();

    void launchModuleAMainPage(Context context);
}
