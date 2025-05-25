package com.magicrealms.magicplayer.velocity.player;

import com.magicrealms.magiclib.common.adapt.UUIDFieldAdapter;
import com.magicrealms.magiclib.common.annotations.FieldId;
import com.magicrealms.magiclib.common.annotations.MongoField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc Velocity 版 PlayerData
 * 包含玩家部分信息
 * 该信息主要用于更新操作
 * @date 2025-05-01
 */
@Data
@AllArgsConstructor
public class PlayerData {
    /* 名称 */
    @MongoField(id = @FieldId(enable = true, ignoreCase = true))
    private String name;
    /* 离线时间 */
    @MongoField
    private long offTime;
    /* 游玩时间 */
    @MongoField
    private long playtime;
}
