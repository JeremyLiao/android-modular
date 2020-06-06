# android-modular
一套Android组件化的实施方案和支撑框架
![image](https://user-images.githubusercontent.com/23290617/82115483-98efe000-9795-11ea-959c-e4d9bbb6e03e.png)

## 组件通信框架
- 组件方法调用框架
    - 编译时组件
    - 插件
    - 运行时框架
- 组件消息总线框架

## 组件化构架
- 壳工程
- 组件层
    - 组件对外暴露层
        - 接口定义
        - 消息定义
    - 组件实现层
- 公共模块
![image](https://user-images.githubusercontent.com/23290617/82115811-b3c35400-9797-11ea-96ba-0a8155c42644.png)

## 组件结构
### 组件对外暴露层
![image](https://user-images.githubusercontent.com/23290617/82115963-bc685a00-9798-11ea-901e-b4aa0564c412.png)
#### 消息定义

```
public class ModuleBEvent implements IModularEvent {
    final public String content;

    public ModuleBEvent(String content) {
        this.content = content;
    }
}
```
#### 接口定义

```
public interface ModuleBInterface extends IInterface {
    void launchModuleBMainPage(Context context);
}
```
### 组件实现
用注解@ModuleService制定实现的接口

```
@ModuleService(interfaceDefine = ModuleBInterface.class)
public class ModuleBInterfaceImpl implements ModuleBInterface {
    @Override
    public void launchModuleBMainPage(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, ModuleBActivity.class));
    }
}
```
## 组件间通信
#### 组件间接口调用

```
userName = ModuleRpcManager.get()
        .call(ModuleAInterface.class)
        .getUserName();
```
#### 组件间消息
##### 监听消息

```
ModularBus.toObservable(ModuleBEvent.class)
        .observe(this, moduleBEvent ->
                Toast.makeText(ModuleAActivity.this,
                        moduleBEvent != null ? moduleBEvent.content : "",
                        Toast.LENGTH_SHORT).show());
```

##### 发送消息

```
ModularBus.toObservable(ModuleBEvent.class).post(new ModuleBEvent("hello world"));
```
## 使用组件通信框架
### 使用组件接口调用框架
##### 配置classpath
```
classpath "com.jeremyliao.modular-tools:plugin:0.0.1"
```
##### 在组件中处理注解
```
annotationProcessor "com.jeremyliao.modular-tools:processor:0.0.1"
```
##### 在壳工程中使用插件
```
apply plugin: 'modular-plugin'
```
##### 运行时使用

```
implementation "com.jeremyliao.modular-tools:manager:0.0.1"
```

### 使用组件消息总线框架
```
implementation 'com.jeremyliao:live-event-bus:1.7.2'
```
