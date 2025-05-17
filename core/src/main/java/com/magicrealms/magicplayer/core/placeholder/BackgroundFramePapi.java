package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.frame.FrameTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc 背景框部分变量
 * %bgframe_setting% 设置菜单部分支持的背景框尺寸
 * %bgframe_profile% 明信片菜单部分支持的背景框尺寸
 * %bgframe_default% 默认支持的背景框尺寸
 * @date 2025-05-16
 */
public class BackgroundFramePapi extends AbstractFramePapi{

    public BackgroundFramePapi(BukkitMagicPlayer plugin) {
        super(plugin, "BgFrame", "Ryan0916", "1.0");
    }

    private Optional<FrameTemplate> getFrameTemplate(int frameId) {
        return PLUGIN.getBackgroundFrameManager()
                .getFrames().stream().filter(e -> e.getId() == frameId)
                .findFirst();
    }

    @Override
    protected String getSettingFrame(PlayerData data) {
        if (data.getBackgroundFrameId() == null) { return StringUtils.EMPTY; }
        return getFrameTemplate(data.getBackgroundFrameId())
                .map(FrameTemplate::getSettingFrame)
                .orElse(StringUtils.EMPTY);
    }

    @Override
    protected String getDefaultFrame(PlayerData data) {
        if (data.getBackgroundFrameId() == null) { return StringUtils.EMPTY; }
        return getFrameTemplate(data.getBackgroundFrameId())
                .map(FrameTemplate::getFrame)
                .orElse(StringUtils.EMPTY);
    }

    @Override
    protected String getProfileFrame(PlayerData data) {
        if (data.getBackgroundFrameId() == null) { return StringUtils.EMPTY; }
        return getFrameTemplate(data.getBackgroundFrameId())
                .map(FrameTemplate::getProfileFrame)
                .orElse(StringUtils.EMPTY);
    }

}
