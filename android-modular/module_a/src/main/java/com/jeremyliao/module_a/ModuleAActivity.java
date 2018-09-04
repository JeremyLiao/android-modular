package com.jeremyliao.module_a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chenenyu.router.annotation.Route;
import com.jeremyliao.modular.ModuleEventBus;
import com.jeremyliao.module_a_export.ModuleAEvents;
import com.jeremyliao.module_a_export.ModuleARouteConst;

@Route(ModuleARouteConst.MODULE_A_MAIN)
public class ModuleAActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_a);
    }

    public void showToast(View v) {
        Toast.makeText(this, "I am ModuleA", Toast.LENGTH_SHORT).show();
        ModuleEventBus.get()
                .with("module_a", ModuleAEvents.SHOW_TOAST)
                .postValue("I am ModuleA");
    }
}
