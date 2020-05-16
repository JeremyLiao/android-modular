package com.jeremyliao.android.modular.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jeremyliao.modular.ModuleRpcManager;
import com.jeremyliao.modulea.export.ModuleAInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchModuleA();
        finish();
    }

    private void launchModuleA() {
        try {
            ModuleRpcManager.get().call(ModuleAInterface.class).launchModuleAMainPage(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
