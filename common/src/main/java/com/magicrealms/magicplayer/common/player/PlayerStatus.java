package com.magicrealms.magicplayer.common.player;

import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc 玩家状态
 * @date 2025-04-27
 */
@Getter
public enum PlayerStatus {
    /* 离线 */
    OFFLINE(0),
    /* 在线 */
    ONLINE(1),
    /* 挂机 */
    AFK(2),
    /* 隐身 */
    HIDDEN(3);

    private final int code;

    PlayerStatus(int code) {
        this.code = code;
    }
}
