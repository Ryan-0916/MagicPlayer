package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.frame.FrameTemplate;
import org.apache.commons.lang3.StringUtils;
import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc 头像框部分变量
 * %avatarframe_setting% 设置菜单部分支持的头像框尺寸
 * %avatarframe_profile% 明信片菜单部分支持的头像框尺寸
 * %avatarframe_default% 默认支持的头像框尺寸
 * @date 2025-05-15
 */
public class AvatarFramePapi extends AbstractFramePapi {

    public AvatarFramePapi(BukkitMagicPlayer plugin) {
        super(plugin, "AvatarFrame", "Ryan0916", "1.0");
    }

    private Optional<FrameTemplate> getFrameTemplate(int frameId) {
        return PLUGIN.getAvatarFrameManager()
                .getFrames().stream().filter(e -> e.getId() == frameId)
                .findFirst();
    }

    @Override
    protected String getSettingFrame(PlayerData data) {
        if (data.getAvatarFrameId() == null) { return StringUtils.EMPTY; }
        return getFrameTemplate(data.getAvatarFrameId())
                .map(FrameTemplate::getSettingFrame)
                .orElse(StringUtils.EMPTY);
    }

    @Override
    protected String getDefaultFrame(PlayerData data) {
        if (data.getAvatarFrameId() == null) { return StringUtils.EMPTY; }
        return getFrameTemplate(data.getAvatarFrameId())
                .map(FrameTemplate::getFrame)
                .orElse(StringUtils.EMPTY);
    }

    @Override
    protected String getProfileFrame(PlayerData data) {
        if (data.getAvatarFrameId() == null) { return StringUtils.EMPTY; }
        return getFrameTemplate(data.getAvatarFrameId())
                .map(FrameTemplate::getProfileFrame)
                .orElse(StringUtils.EMPTY);
    }
}
