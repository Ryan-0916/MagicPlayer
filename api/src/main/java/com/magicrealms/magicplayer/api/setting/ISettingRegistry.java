package com.magicrealms.magicplayer.api.setting;

import java.util.List;

/**
* @author Ryan-0916
* @Desc 用于设置模块的接口类
* @date 2025-05-14
*/
public interface ISettingRegistry {

    /**
     * 注册自定义设置模块 - 它将在 SettingMenu 中展示
     * @param setting 自定义设置模块
     */
    void registry(Setting setting);

    /**
     * 销毁自定义设置模块 - 它将从 SettingMenu 中移除
     * @param setting 自定义设置模块
     */
    void destroy(Setting setting);

    /**
     * 获取全部自定义设置模块
     * @return 所有已经注册了的自定义设置模块
     */
    List<Setting> getSettings();

}
