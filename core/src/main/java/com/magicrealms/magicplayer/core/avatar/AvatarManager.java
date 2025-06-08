package com.magicrealms.magicplayer.core.avatar;

import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.utils.Base64Util;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magicplayer.api.avatar.IAvatarManager;
import com.magicrealms.magicplayer.api.exception.UnknownAvatarTemplate;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.api.player.PlayerData;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

/**
 * @author Ryan-0916
 * @Desc 提供头像等相关操作类
 * @date 2025-05-02
 */
public class AvatarManager implements IAvatarManager {

    private final AvatarLoader loader;

    public AvatarManager(BukkitMagicPlayer plugin) {
        /* 移除 Redis 头像相关缓存 */
        plugin.getRedisStore().removeKeyByPrefix(PLAYERS_AVATAR_LIKE);
        this.loader = new AvatarLoader(plugin);
    }

    @Override
    public String getAvatar(String playerName) throws UnknownAvatarTemplate {
        int defaultTemplateId = loader.getTemplates().values().
                stream().filter(AvatarTemplate::isDefault).findFirst()
                .map(AvatarTemplate::getId).orElse(-1);

        if (defaultTemplateId == -1) {
            throw new UnknownAvatarTemplate("绘制玩家" + playerName + "头像失败，" +
                    "原因：找不到默认头像模板");
        }

        return getAvatar(defaultTemplateId, playerName);
    }

    @Override
    public String getAvatar(int templateId, String playerName) throws UnknownAvatarTemplate {
        AvatarTemplate template = loader
                .getTemplates().get(templateId);
        if (template == null) {
            throw new UnknownAvatarTemplate("绘制玩家" + playerName + "头像失败，" +
                    "原因：找不到头像模板编号为：" + templateId + "的模板");
        }
        return getAvatar(template, playerName);
    }

    public String getAvatar(AvatarTemplate template, String playerName) {
        Objects.requireNonNull(template, "AvatarTemplate cannot be null");
        Objects.requireNonNull(playerName, "Player name cannot be null");
        BukkitMagicPlayer plugin = BukkitMagicPlayer.getInstance();
        String cacheKey = String.format(PLAYERS_AVATAR, template.getId());
        String subKey = StringUtils.upperCase(playerName);
        /* 尝试从缓存获取 */
        Optional<String> cachedAvatar = plugin.getRedisStore().hGetValue(cacheKey, subKey);
        if (cachedAvatar.isPresent() && StringUtils.isNotBlank(cachedAvatar.get())) {
            return cachedAvatar.get();
        }
        try {
            PlayerData data = plugin.getPlayerDataRepository().queryById(playerName);
            String avatarBase64 = (data != null && data.getAvatar() != null) ? data.getAvatar() : DEFAULT_AVATAR;
            BufferedImage avatar = Base64Util.base64ToImage(avatarBase64);
            String processedAvatar = processAvatarLayout(template, avatar);
            long cacheTime = plugin.getConfigManager().getYmlValue(YML_CONFIG, "Cache.Avatar", 3600L, ParseType.LONG);
            plugin.getRedisStore().hSetValue(cacheKey, subKey, processedAvatar, cacheTime);
            return processedAvatar;
        } catch (Exception e) {
            plugin.getLoggerManager().warning(
                    String.format("绘制玩家头像时出现异常 [模板ID: %s, 玩家: %s]: %s",
                            template.getId(), playerName, e.getMessage())
            );
            return null;
        }
    }

    private String processAvatarLayout(AvatarTemplate template, BufferedImage avatar) {
        StringBuilder avatarBuilder = new StringBuilder();
        String layout = StringUtils.join(template.getFormat(), "");
        MagicLib magicLib = MagicLib.getInstance();
        for (int i = 0; i < Math.min(64, layout.length()); i++) {
            int x = i % 8;
            int y = i / 8;
            /* 获取像素颜色 */
            Color color = new Color(avatar.getRGB(x, y));
            String hexColor = String.format("%02x%02x%02x",
                    color.getRed(), color.getGreen(), color.getBlue());
            if (i != 0 && x == 0) {
                avatarBuilder.append(magicLib.getOffsetManager()
                        .format(template.getOffset(), StringUtils.EMPTY));
            }
            /* 添加样式和字符 */
            avatarBuilder.append(String.format("<#%s>", hexColor)).append(layout.charAt(i));
        }
        String AVATAR_FORMAT = "<reset><font:%s><shadow:#00000000>%s</shadow></font></reset>";
        return String.format(AVATAR_FORMAT, template.getFont(), avatarBuilder);
    }

}
