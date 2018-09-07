package com.jeremyliao.modular;

import android.content.Context;
import android.content.res.AssetManager;

import com.jeremyliao.modular.utils.AppUtils;
import com.jeremyliao.modular_base.inner.bean.ModuleInfo;
import com.jeremyliao.modular_base.inner.utils.GsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public final class ModuleRpcInitHelper {

    private static final String ASSET_PATH = "modules/module_info";

    private ModuleRpcInitHelper() {
    }

    private Context getContext() {
        return AppUtils.getApplicationContext();
    }

    public static void init(Context context) {
        if (context == null) {
            context = AppUtils.getApplicationContext();
        }
        AssetManager asset = context.getAssets();
        try {
            List<ModuleInfo> moduleInfos = new ArrayList<>();
            InputStream inputStream = asset.open(ASSET_PATH);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                ModuleInfo moduleInfo = GsonUtil.fromJson(line, ModuleInfo.class);
                moduleInfos.add(moduleInfo);
            }
            ModuleRpcManager.get().init(moduleInfos);
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
