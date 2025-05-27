package com.magicrealms.magicplayer.velocity.player;

import com.magicrealms.magiclib.common.annotations.MongoField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ryan-0916
 * @Desc 存储玩家时装相关信息 - 用于 Profile 使用
 * @date 2025-05-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerCosmetic {
    /* 帽子 */
    @MongoField(name = "cosmetic_hat")
    private String hat;
    /* 背包 */
    @MongoField(name = "cosmetic_bag")
    private String bag;
    /* 权杖 */
    @MongoField(name = "cosmetic_walking_stick")
    private String walkingStick;
    /* 气球 */
    @MongoField(name = "cosmetic_balloon")
    private String balloon;
    /* 喷漆 */
    @MongoField(name = "cosmetic_spray")
    private String spray;
}
