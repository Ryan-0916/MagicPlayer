package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.frame.FrameTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc 背景框部分变量
 * @date 2025-05-16
 */
public class BackgroundFramePapi extends AbstractFramePapi{

    public BackgroundFramePapi(BukkitMagicPlayer plugin) {
        super(plugin, "bgFrame", "Ryan0916", "1.0");
    }

    private Optional<FrameTemplate> getFrameTemplate(int frameId) {
        return PLUGIN.getBackgroundFrameManager()
                .getFrames().stream().filter(e -> e.getId() == frameId)
                .findFirst();
    }

    @Override
    protected String getSettingFrame(PlayerData data) {
        if (data.getBackgroundFrameId() == null) {
            return StringUtils.EMPTY;
        }
        Optional<FrameTemplate> template = getFrameTemplate(data.getBackgroundFrameId());
        if (template.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return template.get().getSettingFrame();
    }

    @Override
    protected String getDefaultFrame(PlayerData data) {
        if (data.getBackgroundFrameId() == null) {
            return StringUtils.EMPTY;
        }
        Optional<FrameTemplate> template = getFrameTemplate(data.getBackgroundFrameId());
        if (template.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return template.get().getFrame();
    }

    @Override
    protected String getProfileFrame(PlayerData data) {
        if (data.getBackgroundFrameId() == null) {
            return StringUtils.EMPTY;
        }
        Optional<FrameTemplate> template = getFrameTemplate(data.getBackgroundFrameId());
        if (template.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return template.get().getProfileFrame();
    }

}
