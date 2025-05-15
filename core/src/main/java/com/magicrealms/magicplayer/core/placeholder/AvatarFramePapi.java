package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.frame.FrameTemplate;
import org.apache.commons.lang3.StringUtils;
import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc 头像框部分变量
 * @date 2025-05-15
 */
public class AvatarFramePapi extends AbstractFramePapi {

    public AvatarFramePapi(BukkitMagicPlayer plugin) {
        super(plugin, "avatarFrame", "Ryan0916", "1.0");
    }

    private Optional<FrameTemplate> getFrameTemplate(int frameId) {
        return PLUGIN.getAvatarFrameManager()
                .getFrames().stream().filter(e -> e.getId() == frameId)
                .findFirst();
    }

    @Override
    protected String getSettingFrame(PlayerData data) {
        if (data.getAvatarFrameId() == null) {
            return StringUtils.EMPTY;
        }
        Optional<FrameTemplate> template = getFrameTemplate(data.getAvatarFrameId());
        if (template.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return template.get().getSettingFrame();
    }

    @Override
    protected String getDefaultFrame(PlayerData data) {
        if (data.getAvatarFrameId() == null) {
            return StringUtils.EMPTY;
        }
        Optional<FrameTemplate> template = getFrameTemplate(data.getAvatarFrameId());
        if (template.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return template.get().getFrame();
    }

    @Override
    protected String getProfileFrame(PlayerData data) {
        if (data.getAvatarFrameId() == null) {
            return StringUtils.EMPTY;
        }
        Optional<FrameTemplate> template = getFrameTemplate(data.getAvatarFrameId());
        if (template.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return template.get().getProfileFrame();
    }
}
