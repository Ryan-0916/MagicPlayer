package com.magicrealms.magicplayer.core.avatar.frame;

import com.magicrealms.magicplayer.core.BukkitMagicPlayer;

import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 头像框管理器
 * @date 2025-05-12
 */
public class AvatarFrameManager {

    private final AvararFrameConfigLoader configLoader;

    public AvatarFrameManager(BukkitMagicPlayer plugin) {
        /* 加载头像相关的配置 */
        this.configLoader = new AvararFrameConfigLoader(plugin);
    }

    public List<AvatarFrame> getFrames() {
        return configLoader.getFrames();
    }
}
