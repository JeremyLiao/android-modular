# android-modular
一个组件化的实施方案和组件化基础设施框架
## 总体框架
![image](https://github.com/JeremyLiao/android-modular/blob/master/imgs/img1.png)
## 设计目标
1. 支持组件模式和集成模式
2. 组件模式下每个组件能够单独运行调试
3. 组件之前完全隔离
4. 支持路由跳转
5. 支持总线消息框架
6. 支持组件间接口调用

## 组件化基础设施框架
- 组件消息总线：ModuleEventBus
- 组件间rpc框架：ModuleRpcManager

#### 引入
- Maven
```
<dependency>
  <groupId>com.jeremyliao</groupId>
  <artifactId>modular</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```
- Gradle
```
compile 'com.jeremyliao:modular:0.0.1'
```

#### 组件消息总线
消息必须在每个组件的export-module中预先定义，避免了消息滥发
#### 组件间rpc框架
接口必须在每个组件的export-module中预先定义
#### router框架
route path在每个组件的export-module中预先定义，Demo中使用的router框架是[chenenyu/Router](https://github.com/chenenyu/Router)，可替换成其他框架

## 组件化实施方案
#### 集成模式和组件模式切换
在gradle.properties中定义

```
IS_MODULE_MODE=false
```
将 IS_MODULE_MODE改为你需要的开发模式（true/false）， 然后点击 "Sync Project" 按钮同步项目
#### 创建一个组件
一个组件包含两个module：
- module_x_export 组件对外暴露的接口
- module_x 组件本身的实现

##### module_x module的实现
###### 在组件的build.gradle中apply：

```
apply from: "${project.rootDir}/module_config.gradle"
```
这个module_config.gradle定义在rootDir下：

```
if (IS_MODULE_MODE.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}

android {
    sourceSets {
        main {
            if (IS_MODULE_MODE.toBoolean()) {
                manifest.srcFile 'src/main/module/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/main/AndroidManifest.xml'
                java {
                    exclude 'module/**'
                }
            }
        }
    }
}
```
###### 定义两个AndroidManifest.xml：
1. src/main/module/AndroidManifest.xml
2. src/main/AndroidManifest.xml

##### module_x_export 对外暴露的接口module的实现
###### 配置组件
1. module config 组件配置
2. 组件消息总线消息定义
3. 组件接口定义
4. 组件路由表定义

###### 配置文件定义
在assets/module_config下面生成一个以配置文件classname为文件名的文件
![image](https://github.com/JeremyLiao/android-modular/blob/master/imgs/img2.png)

### 相关资料
参考了GitHub上其他几个组件化框架，在此表示感谢：
1. [luckybilly/CC](https://github.com/luckybilly/CC)
2. [guiying712/AndroidModulePattern](https://github.com/guiying712/AndroidModulePattern)
3. [LiushuiXiaoxia/AndroidModular](https://github.com/LiushuiXiaoxia/AndroidModular)
