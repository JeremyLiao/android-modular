package com.jeremyliao.android_modular;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenenyu.router.Router;
import com.jeremyliao.module_a_export.ModuleARouteConst;
import com.jeremyliao.module_b_export.ModuleBRouteConst;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startModuleA(View v) {
        Router.build(ModuleARouteConst.MODULE_A_MAIN).go(this);
    }

    public void startModuleB(View v) {
        Router.build(ModuleBRouteConst.MODULE_B_MAIN).go(this);
    }

    public void startModuleAB(View v) {
        Router.build(ModuleBRouteConst.MODULE_B_MAIN).go(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Router.build(ModuleARouteConst.MODULE_A_MAIN).go(MainActivity.this);
            }
        }, 500);
    }
}
