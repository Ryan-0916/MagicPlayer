package com.magicrealms.magicplayer.velocity.player;

import com.magicrealms.magiclib.common.annotations.MongoField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ryan-0916
 * @Desc 玩家装备信息
 * 存储玩家装备相关信息 - 用于 Profile 使用
 * 此类用于存储玩家的装备信息，包括头盔、胸甲、护腿和鞋子等装备的 Base64 编码字符串。
 * 装备信息通常用于玩家的个人资料（Profile）显示或其他相关功能，数据存储为 Base64 字符串便于传输和存储。
 * @date 2025-05-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerArmor {
    /* 头盔 */
    @MongoField(name = "armor_helmet")
    private String helmet;
    /* 胸甲 */
    @MongoField(name = "armor_chestplate")
    private String chestplate;
    /* 护腿 */
    @MongoField(name = "armor_leggings")
    private String leggings;
    /* 鞋子 */
    @MongoField(name = "armor_boots")
    private String boots;
}
