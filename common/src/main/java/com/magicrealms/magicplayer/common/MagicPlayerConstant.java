package com.magicrealms.magicplayer.common;

/**
 * @author Ryan-0916
 * @Desc 常量
 * @date 2025-05-01
 */
public final class MagicPlayerConstant {

    /* 插件名称 */
    public static final String PLUGIN_NAME = "MagicPlayer";
    /* 默认头像
     * 该文本表示 Base64 格式的默认玩家头像
     * 解析出来为 8 x 8 的史蒂夫头像
     */
    public static final String DEFAULT_AVATAR = "iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAYAAADED76LAAAA1ElEQVR4XmPQV+D9ry3H+19fnv+/hgz3fxUJjv9qUlxgMS1Z3v8MIAYMG6sI/nfSEvpvryX6X0eOD6wBrGBbZ87/vX1F/49Na/i/t7sIyC75v6Ys6r+JqtB/hlW1af+3tOT+B9FrG3L/zymK+b+7s+j/nEwfMGYASYJAkG3n/63V6WBJEBsEQJoY5iS7/d9cnfR/e1PR/ywHAzDe15Hzf1GW+/+GYJP/DBPinP9Pi3f4X+5kioL74+z+NwZb/mfId9X5n+usBeaAcJWf8f/mUGswBrEBqAeFqNKFJtgAAAAASUVORK5CYII=";

    /** 配置文件部分常量 */
    public static final String YML_CONFIG = "config";
    public static final String YML_REDIS = "redis";
    public static final String YML_LANGUAGE = "language";
    public static final String YML_MONGODB = "mongodb";
    public static final String YML_AVATAR = "model/avatar";
    public static final String YML_AVATAR_FRAME = "model/avatarFrame";
    public static final String YML_PLAYER_MENU = "menu/playerMenu";
    public static final String YML_PROFILE_MENU = "menu/profileMenu";
    public static final String YML_SETTING_MENU = "menu/settingMenu";
    public static final String YML_AVATAR_FRAME_MENU = "menu/avatarFrameMenu";

    /** Redis 相关 key */
    /* 跨服通讯频道 */
    public static final String BUNGEE_CHANNEL = "BUNGEE_CHANNEL_MAGIC_PLAYER";
    /* 玩家列表（当日） */
    public static final String DAILY_PLAYERS_HASH_KEY =  "MAGIC_PLAYER_DAILY_PLAYERS";
    /* 操作玩家列表（当日）时的分布式锁 */
    public static final String DAILY_PLAYER_LOCK =  "MAGIC_PLAYER_DAILY_PLAYER_LOCK_%s";
    /* 存储玩家头像的 Key */
    public static final String PLAYERS_AVATAR_LIKE = "MAGIC_PLAYER_PLAYERS_AVATAR";
    /* 存储玩家头像的 Key %s 代表的是头像 FormatId */
    public static final String PLAYERS_AVATAR =  "MAGIC_PLAYER_PLAYERS_AVATAR_%s";

    /** MongoDB部分常量 */
    /* MongoDB 玩家信息表 */
    public static final String MAGIC_PLAYERS_TABLE_NAME = "magic_players";
}
