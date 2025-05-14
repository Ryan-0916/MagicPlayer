package com.magicrealms.magicplayer.api.avatar;

import com.magicrealms.magicplayer.api.exception.UnknownAvatarTemplate;

/**
 * @author Ryan-0916
 * @Desc 用于处理头像的接口类
 * @date 2025-05-13
 */
public interface IAvatarManager {

    /**
     * 获取默认尺寸的玩家头像
     * @param playerName 玩家名称
     * @return 玩家头像
     * @throws UnknownAvatarTemplate 如果找不到默认模板则提示该异常
     */
    String getAvatar(String playerName) throws UnknownAvatarTemplate;

    /**
     * 根据模板获取自定义尺寸的玩家头像
     * @param templateId 模板编号
     * @param playerName 玩家名称
     * @return 玩家头像
     * @throws UnknownAvatarTemplate 如果找不到该模板则提示该异常
     */
    String getAvatar(int templateId, String playerName) throws UnknownAvatarTemplate;

}
