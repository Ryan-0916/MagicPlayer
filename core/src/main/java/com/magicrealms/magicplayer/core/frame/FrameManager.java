package com.magicrealms.magicplayer.core.frame;

import com.magicrealms.magicplayer.api.setting.SettingParam;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 头像框管理器
 * @date 2025-05-12
 */
public class FrameManager {

    private final BukkitMagicPlayer plugin;

    private final FrameLoader loader;

    public FrameManager(BukkitMagicPlayer plugin, String configPath, Consumer<SettingParam> clickAction) {
        this.plugin = plugin;
        /* 加载头像相关的配置 */
        this.loader = new FrameLoader(plugin, configPath, clickAction);
    }

    public void registrySetting() {
        loader.getAvatarFrameSetting().ifPresent(
                setting -> plugin.getSettingRegistry().registry(setting)
        );
    }

    public void destroySetting() {
        loader.getAvatarFrameSetting().ifPresent(
                setting -> plugin.getSettingRegistry().destroy(setting)
        );
    }

    public List<FrameTemplate> getFrames() {
        return loader.getFrames();
    }
}
