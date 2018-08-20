package com.jeremyliao.modular;

import android.content.Context;
import android.content.res.AssetManager;

import com.jeremyliao.modular.utils.AppUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public final class ModuleManager {

    private static class SingletonHolder {
        private static final ModuleManager INSTANCE = new ModuleManager();
    }

    public static ModuleManager get() {
        return SingletonHolder.INSTANCE;
    }

    private ModuleManager() {
        init();
    }

    private Context getContext() {
        return AppUtils.getApplicationContext();
    }

    private void init() {
        AssetManager asset = getContext().getAssets();
        try {
            String[] moduleConfigs = asset.list("module_config");
            if (moduleConfigs == null || moduleConfigs.length == 0) {
                return;
            }
            List<IModuleConfig> moduleConfigList = new ArrayList<>();
            for (String cln : moduleConfigs) {
                try {
                    Class<?> type = Class.forName(cln);
                    Object instance = type.newInstance();
                    if (instance instanceof IModuleConfig) {
                        moduleConfigList.add((IModuleConfig) instance);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ModuleEventBus.get().init(moduleConfigList);
            ModuleRpcManager.get().init(moduleConfigList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
