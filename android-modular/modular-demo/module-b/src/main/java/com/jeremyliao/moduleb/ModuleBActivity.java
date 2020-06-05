package com.jeremyliao.moduleb;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jeremyliao.modular.ModuleRpcManager;
import com.jeremyliao.modulea.export.ModuleAInterface;
import com.jeremyliao.moduleb.databinding.ActivityModuleBBinding;
import com.jeremyliao.moduleb.export.event.ModuleBEvent;

public class ModuleBActivity extends AppCompatActivity {

    private ActivityModuleBBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_module_b);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
    }

    public void callModuleAGetUserName() {
        String userName = null;
        try {
            userName = ModuleRpcManager.get()
                    .call(ModuleAInterface.class)
                    .getUserName();
        } catch (Throwable throwable) {
        }
        Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();
    }

    public void sendMsg() {
        LiveEventBus.get(ModuleBEvent.class).post(new ModuleBEvent("hello world"));
    }
}
