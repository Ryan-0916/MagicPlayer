package com.magicrealms.magicplayer.core.avatar.frame;

import com.magicrealms.magicplayer.core.BukkitMagicPlayer;

import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 头像框管理器
 * @date 2025-05-12
 */
public class AvatarFrameManager {

    private final BukkitMagicPlayer plugin;

    private final AvatarFrameConfigLoader configLoader;

    public AvatarFrameManager(BukkitMagicPlayer plugin) {
        this.plugin = plugin;
        /* 加载头像相关的配置 */
        this.configLoader = new AvatarFrameConfigLoader(plugin);
    }

    public void registrySetting() {
        configLoader.getAvatarFrameSetting().ifPresent(
                setting -> plugin.getSettingRegistry().registry(setting)
        );
    }

    public void destroySetting() {
        configLoader.getAvatarFrameSetting().ifPresent(
                setting -> plugin.getSettingRegistry().destroy(setting)
        );
    }

    public List<AvatarFrameTemplate> getFrames() {
        return configLoader.getFrames();
    }
}
