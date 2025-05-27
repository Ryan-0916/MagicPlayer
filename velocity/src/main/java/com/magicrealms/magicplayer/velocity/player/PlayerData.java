package com.magicrealms.magicplayer.velocity.player;

import com.magicrealms.magiclib.common.adapt.UUIDFieldAdapter;
import com.magicrealms.magiclib.common.annotations.FieldId;
import com.magicrealms.magiclib.common.annotations.MongoField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 玩家基本信息
 * 包含玩家装备、时装、游玩时长等基础信息
 * 该类用于存储玩家的基本信息，包括玩家的 UUID、用户名、头像、时装、装备、电子邮件、好友数等。
 * 这些信息主要用于玩家个人资料展示、玩家互动、以及玩家在游戏中的记录和统计。
 * @date 2025-05-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    /* id */
    @MongoField(adapter = UUIDFieldAdapter.class, name = "uuid")
    private UUID uniqueId;
    /* 名称 */
    @MongoField(id = @FieldId(enable = true, ignoreCase = true))
    private String name;
    /* 头颅 */
    @MongoField
    private String headStack;
    /* 邮箱 */
    @MongoField
    private String email;
    /* 材质 */
    @MongoField
    private String textures;
    /* 皮肤 */
    @MongoField
    private String skin;
    /* 头像 */
    @MongoField
    private String avatar;
    /* 好友数量 */
    @MongoField
    private int friendNumber;
    /* 注册时间 */
    @MongoField
    private long registerTime;
    /* 离线时间 */
    @MongoField
    private long offTime;
    /* 游玩时间 */
    @MongoField
    private long playtime;
    /* 装备 */
    @MongoField(recursive = true)
    private PlayerArmor armor;
    /* 时装 */
    @MongoField(recursive = true)
    private PlayerCosmetic cosmetic;
    /* 头像框 */
    @MongoField
    private Integer avatarFrameId;
    /* 背景框 */
    @MongoField
    private Integer backgroundFrameId;
}
