package com.jeremyliao.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by liaohailiang on 2018/12/26.
 */
class ModularPlugin implements Plugin<Project> {

    final String TAG = "[ModularPlugin]"

    @Override
    void apply(Project project) {
        System.out.println(TAG + project)
        addTransform(project)
    }

    private void addTransform(Project project) {
        def extension = project.extensions.findByType(AppExtension.class)
        System.out.println(TAG + extension)
        extension.registerTransform(new ModularTransform(project))
    }
}
