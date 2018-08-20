package com.jeremyliao.module_b;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.chenenyu.router.annotation.Route;
import com.jeremyliao.modular.ModuleEventBus;
import com.jeremyliao.modular.ModuleRpcManager;
import com.jeremyliao.module_a_export.ModuleAEvents;
import com.jeremyliao.module_a_export.ModuleAInterface;
import com.jeremyliao.module_a_export.ModuleAModuleConfig;
import com.jeremyliao.module_b_export.ModuleBRouteConst;

@Route(ModuleBRouteConst.MODULE_B_MAIN)
public class ModuleBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_b);
        ModuleEventBus.get()
                .with(ModuleAModuleConfig.MODULE_NAME, ModuleAEvents.SHOW_TOAST, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(ModuleBActivity.this, "Msg from module a: " + s,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void callModuleAGetUserName(View v) {
        String userName = null;
        try {
            userName = ModuleRpcManager.get()
                    .call(ModuleAInterface.class)
                    .getUserName();
        } catch (Throwable throwable) {
        }
        Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();
    }
}
