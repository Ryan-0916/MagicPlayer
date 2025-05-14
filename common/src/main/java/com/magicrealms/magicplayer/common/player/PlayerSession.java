package com.magicrealms.magicplayer.common.player;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


/**
 * @author Ryan-0916
 * @Desc 缓存玩家相关信息
 * @date 2025-04-27
 */
@Data
@Builder
@SuppressWarnings("unused")
public class PlayerSession {
    /* 玩家ID */
    private UUID uuid;
    /* 玩家名称 */
    private String name;
    /* 所在子服名称 */
    private String serverName;
    /* 玩家状态 */
    private PlayerStatus status;
    /* 上线时间 */
    private long upTime;
    /* 离线时间 */
    private long offTime;
    /* 开始挂机时间 */
    private long startAfkTime;
    /* 今日游玩时间 */
    private long playTime;

    public boolean isOnline() {
        return status != PlayerStatus.OFFLINE && status != PlayerStatus.HIDDEN;
    }

    public boolean isAfk() {
        return status == PlayerStatus.AFK;
    }

}
