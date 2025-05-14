package com.magicrealms.magicplayer.core.setting;

import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import java.util.List;

/**
 * @author Ryan-0916
 * @Desc  设置管理器
 * @date 2025-05-10
 */
public class SettingManager {

    private final SettingConfigLoader configLoader;

    public SettingManager(BukkitMagicPlayer plugin) {
        /* 加载头像相关的配置 */
        this.configLoader = new SettingConfigLoader(plugin);
    }

    public List<Setting> getSettings() {
        return configLoader.getSettings();
    }

}
