package com.magicrealms.magicplayer.core.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.magicrealms.magiclib.common.utils.Base64Util;
import com.magicrealms.magiclib.common.utils.GsonUtil;
import com.magicrealms.magicplayer.core.MagicPlayer;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.DEFAULT_AVATAR;

/**
 * @author Ryan-0916
 * @Desc 玩家数据工具类
 * @date 2025-05-02
 */
@SuppressWarnings("unused")
public final class SkinUtil {

    private static final int SIZE = 8;

    private SkinUtil() {}

    /**
     * 获取玩家皮肤材质
     * 通过插件 SkinsRestorer 获取玩家皮肤材质
     * @param player 玩家
     * @return 玩家皮肤材质 Base64
     */
    public static String getPlayerSkin(Player player) {
        if (MagicPlayer.getInstance()
                .getSkinsRestorer() == null) {
            return null;
        }
        try {
            SkinProperty skinProperty = MagicPlayer.getInstance()
                    .getSkinsRestorer().getPlayerStorage()
                    .getSkinForPlayer(player.getUniqueId(),
                            player.getName())
                    .orElse(null);
            if (skinProperty == null) {
                return null;
            }
            /* 将皮肤数据从 Base64 转换成 Json */
            JsonElement json = GsonUtil.jsonToElement(Base64Util.base64ToString(skinProperty.getValue()));
            if (json == null) {
                return null;
            }
            if (json instanceof JsonObject object
                    && object.get("textures") instanceof JsonObject textures
                    && textures.get("SKIN") instanceof JsonObject skinUrl) {
                return Base64Util.imageUrlToBase64(skinUrl.get("url").getAsString());
            }
        } catch (Exception e) {
            MagicPlayer.getInstance().getLoggerManager().warning("获取玩家皮肤时出现了未知的错误");
        }
        return null;
    }

    /**
     * 根据皮肤材质绘制玩家头
     * 注意此处 Skin 为 Base64 格式
     * @param skinBase64 玩家皮肤 Base64 格式
     * @return 玩家头像
     */
    public static String getPlayerAvatar(String skinBase64) {
        try {
            return drawPlayerAvatar(skinBase64);
        } catch (Exception exception) {
            MagicPlayer.getInstance().getLoggerManager().warning("获取玩家头像时出现了未知的错误");
        }
        /* 默认史蒂夫头像 */
        return DEFAULT_AVATAR;
    }

    /**
     * 绘制玩家头像
     * 该头像是由玩家皮肤纹理绘制而来
     * 8x8像素组成的一个 Base64 头像
     * @param skinBase64 皮肤纹理
     * @return 玩家头像 Base64 编码
     */
    private static String drawPlayerAvatar(String skinBase64) throws IOException {
        if (skinBase64 == null) {
            return DEFAULT_AVATAR;
        }
        BufferedImage skin = Base64Util.base64ToImage(skinBase64);
        if (skin == null) {
            return DEFAULT_AVATAR;
        }
        BufferedImage head = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = head.createGraphics();
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHints(renderingHints);
        fillRectAvatar(skin, 8, 16, graphics);
        fillRectAvatar(skin, 40, 48, graphics);
        graphics.dispose();
        return Base64Util.imageToBase64(head);
    }

    /**
     * 将图片的区域填充进行的图片中
     * @param skin     原图
     * @param startX   起始 x 坐标
     * @param endX     终点 x 坐标
     * @param graphics Graphics2D
     */
    private static void fillRectAvatar(BufferedImage skin, int startX, int endX, Graphics2D graphics) {
        for (int i = startX; i <= endX; i++) {
            for (int j = 8; j <= 16; j++) {
                int color = skin.getRGB(i, j);
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = color & 0x000000ff;
                int alpha = (color >> 24) & 0xff;
                graphics.setColor(new Color(red, green, blue, alpha));
                graphics.fillRect(i - startX, j - 8, 1, 1);
            }
        }
    }

}
