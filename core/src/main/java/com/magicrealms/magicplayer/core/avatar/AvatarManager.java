package com.magicrealms.magicplayer.core.avatar;

import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.utils.Base64Util;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magicplayer.core.MagicPlayer;
import com.magicrealms.magicplayer.core.player.PlayerData;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

/**
 * @author Ryan-0916
 * @Desc 头像管理器，管理玩家头像相关信息
 * 提供获取玩家头像等方法
 * @date 2025-05-02
 */
public class AvatarManager {

    private final AvatarConfigLoader configLoader;


    public AvatarManager(MagicPlayer plugin) {
        /* 移除 Redis 头像相关缓存 */
        plugin.getRedisStore()
                .removeKeyByPrefix(PLAYERS_AVATAR_LIKE);
        /* 加载头像相关的配置 */
        this.configLoader = new AvatarConfigLoader(plugin);
    }

    public String getPlayerAvatar(String name, int id) {
        String key =StringUtils.upperCase(name);
        /* 从缓存中寻找 */
        String avatarCatch = MagicPlayer.getInstance().getRedisStore()
                        .hGetValue(String.format(PLAYERS_AVATAR, id),
                                key).orElse(null);
        if (StringUtils.isNotBlank(avatarCatch)) {
            return avatarCatch;
        }
        List<String> layout = configLoader.getFormats().get(id).getFormat();
        String font = configLoader.getFormats().get(id).getFont();
        String offsetChar = configLoader.getFormats().get(id).getOffsetChar();
        if (layout == null) {
            return null;
        }
        /* 查找玩家的头像 Base64 编码并转换成 BufferedImage */
        PlayerData data = MagicPlayer.getInstance().getPlayerDataRepository().queryById(name);
        try {
            BufferedImage avatar = Base64Util.base64ToImage(data != null && data.getAvatar() != null ? data.getAvatar() : DEFAULT_AVATAR);
            /* 拼接 Layout 的每个字并等待处理 */
            StringBuilder builder = layout.stream()
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append),
                    avatarBuilder = new StringBuilder();
            /* 确认每个点的 16 进制颜色值*/
            for (int i = 0; i < Math.min(64, builder.length()); i++) {
                int x = i % 8, y = i / 8;
                Color color = new Color(avatar.getRGB(x, y));
                String hexColor = String.format("%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                avatarBuilder.append("<reset>")
                        .append(i != 0 && x == 0 ? offsetChar
                                : StringUtil.EMPTY)
                        .append("<reset>")
                        .append(String.format("<#%s>", hexColor))
                        .append(String.format("<font:%s>", font))
                        .append(builder.charAt(i))
                        .append("</reset>");
            }
            /* 最终处理的结果 */
            String avatarStr = avatarBuilder.toString();
            MagicPlayer.getInstance().getRedisStore()
                    .hSetValue(String.format(PLAYERS_AVATAR, id),
                            key,
                            avatarStr,
                            MagicPlayer.getInstance().getConfigManager().getYmlValue(YML_CONFIG, "Cache.Avatar", 3600, ParseType.INTEGER)
                    );
            return avatarStr;
        } catch (Exception e) {
            MagicPlayer.getInstance()
                    .getLoggerManager()
                    .warning("获取玩家头像时出现未知异常头像ID：", id);
        }
        return null;
    }

}
