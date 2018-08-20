package com.jeremyliao.modular;

/**
 * Created by liaohailiang on 2018/8/17.
 */
public interface IModuleConfig {

    /**
     * Name of module
     *
     * @return
     */
    String getModuleName();

    /**
     * getEventDefineClass
     *
     * @return
     */
    Class<? extends IEvent> getEventDefineClass();

    /**
     * getRpcInterfaceClass
     *
     * @return
     */
    Class<? extends IInterface> getRpcInterfaceClass();

    /**
     * getRpcImplement
     *
     * @return
     */
    String getRpcImplementClassName();
}
