package com.magicrealms.magicplayer.api.skin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 用于处理皮肤的接口类
 * @date 2025-05-14
 */
public interface ISkinManager {

    /**
     * 获取玩家 Base64格式 皮肤材质
     * @param player 玩家
     * @return Base64格式玩家皮肤材质
     */
    String getTextures(Player player);

    /**
     * 解析 Base64 皮肤材质，获取 Base64格式 皮肤
     * @param base64Texture Base64 皮肤材质
     * @return Base64格式皮肤
     */
    String getSkin(String base64Texture);

    /**
     * 解析 Base64 皮肤，获取 Base64格式 头像
     * 获取一个 8 * 8 像素的皮肤头像
     * @param base64Skin Base64 皮肤
     * @return Base64格式头像
     */
    String getAvatar(String base64Skin);

    /**
     * 解析 Base64 皮肤材质，获取该皮肤的头颅
     * @param base64Texture Base64 皮肤材质
     * @return 该皮肤的头颅
     */
    ItemStack getSkull(String base64Texture);
}
