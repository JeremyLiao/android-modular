package com.jeremyliao.modulea;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jeremyliao.modular.ModuleRpcManager;
import com.jeremyliao.modulea.databinding.ActivityModuleABinding;
import com.jeremyliao.moduleb.export.ModuleBInterface;
import com.jeremyliao.moduleb.export.event.ModuleBEvent;

public class ModuleAActivity extends AppCompatActivity {

    private ActivityModuleABinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_module_a);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        LiveEventBus.get(ModuleBEvent.class)
                .observe(this, moduleBEvent ->
                        Toast.makeText(ModuleAActivity.this,
                                moduleBEvent != null ? moduleBEvent.content : "",
                                Toast.LENGTH_SHORT).show());
    }

    public void launchModuleBMainPage() {
        try {
            ModuleRpcManager.get()
                    .call(ModuleBInterface.class)
                    .launchModuleBMainPage(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
