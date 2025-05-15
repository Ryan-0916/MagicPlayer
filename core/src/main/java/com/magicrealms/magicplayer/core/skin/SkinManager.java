package com.magicrealms.magicplayer.core.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.magicrealms.magiclib.common.utils.Base64Util;
import com.magicrealms.magiclib.common.utils.GsonUtil;
import com.magicrealms.magicplayer.api.skin.ISkinManager;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.saicone.rtag.util.SkullTexture;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.event.SkinApplyEvent;
import net.skinsrestorer.api.property.SkinProperty;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.DEFAULT_AVATAR;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.PLAYERS_AVATAR_LIKE;

/**
 * @author Ryan-0916
 * @Desc 提供皮肤等相关操作类
 * @date 2025-05-14
 */
public class SkinManager implements ISkinManager {

    private static final int SIZE = 8;

    private static final String DEF_TEXTURES = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ==";

    private static final String DEFAULT_SKIN = "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";

    private final SkinsRestorer skinsRestorer;

    public SkinManager(BukkitMagicPlayer plugin) {
        this.skinsRestorer = SkinsRestorerProvider.get();
        this.skinsRestorer.getEventBus().subscribe(plugin, SkinApplyEvent.class, event -> {
            Player player = event.getPlayer(Player.class);
            BukkitMagicPlayer.getInstance().getPlayerDataRepository().asyncUpdateByPlayer(event.getPlayer(Player.class), data -> {
                data.setTextures(getTextures(player));
                data.setHeadStack(getSkull(data.getTextures()));
                data.setSkin(getSkin(data.getTextures()));
                data.setAvatar(getAvatar(data.getSkin()));
            });
            String subKey = StringUtils.upperCase(player.getName());
            /* 清理掉该玩家头像相关的缓存 */
            plugin.getRedisStore().getKeyByPrefix(PLAYERS_AVATAR_LIKE)
                    .forEach(
                            key -> plugin.getRedisStore().removeHkey(key, subKey)
                    );
        });
    }

    @Override
    public String getTextures(Player player) {
        try {
            if (skinsRestorer == null) {
                return DEF_TEXTURES;
            }
            return skinsRestorer.getPlayerStorage()
                    .getSkinForPlayer(player.getUniqueId(), player.getName())
                    .map(SkinProperty::getValue)
                    .orElse(DEF_TEXTURES);
        } catch (Exception e) {
            BukkitMagicPlayer.getInstance().getLoggerManager().warning(
                    "获取玩家" + player.getName() + "皮肤材质时出现错误: " + e.getMessage());
        }
        return DEF_TEXTURES;
    }

    @Override
    public String getSkin(String base64Texture) {
        try {
            JsonElement json = GsonUtil.jsonToElement(Base64Util.base64ToString(base64Texture));
            if (!(json instanceof JsonObject object)) {
                return DEFAULT_SKIN;
            }
            JsonObject textures = object.getAsJsonObject("textures");
            JsonObject skin = textures != null ? textures.getAsJsonObject("SKIN") : null;
            if (skin != null && skin.has("url")) {
                return Base64Util.imageUrlToBase64(skin.get("url").getAsString());
            }
        } catch (Exception e) {
            BukkitMagicPlayer.getInstance().getLoggerManager().warning(
                    "获取玩家皮肤时出现错误: " + e.getMessage());
        }
        return DEFAULT_SKIN;
    }

    @Override
    public String getAvatar(String base64Skin) {
        try {
            return drawAvatar(base64Skin);
        } catch (Exception exception) {
            BukkitMagicPlayer.getInstance().getLoggerManager()
                    .warning("获取玩家头像时出现了未知的错误");
        }
        return DEFAULT_AVATAR;
    }

    @Override
    public ItemStack getSkull(String base64Texture) {
        return SkullTexture.getTexturedHead(base64Texture);
    }

    private String drawAvatar(String base64Skin) throws IOException {
        if (base64Skin == null) {
            return DEFAULT_AVATAR;
        }
        BufferedImage skin = Base64Util.base64ToImage(base64Skin);
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
    private void fillRectAvatar(BufferedImage skin, int startX, int endX, Graphics2D graphics) {
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
