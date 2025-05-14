package com.magicrealms.magicplayer.api.player;

import com.magicrealms.magiclib.core.adapt.ItemStackFieldAdapter;
import com.magicrealms.magiclib.common.annotations.MongoField;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "armor_helmet")
    private ItemStack helmet;
    /* 胸甲 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "armor_chestplate")
    private ItemStack chestplate;
    /* 护腿 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "armor_leggings")
    private ItemStack leggings;
    /* 鞋子 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "armor_boots")
    private ItemStack boots;

    public PlayerArmor(Player player) {
        this.helmet = player.getInventory().getHelmet();
        this.chestplate = player.getInventory().getChestplate();
        this.leggings = player.getInventory().getLeggings();
        this.boots = player.getInventory().getBoots();
    }

    public boolean isEquippedHelmet() {
        return ItemUtil.isNotAirOrNull(helmet);
    }

    public boolean isEquippedChestplate() {
        return ItemUtil.isNotAirOrNull(chestplate);
    }

    public boolean isEquippedLeggings() {
        return ItemUtil.isNotAirOrNull(leggings);
    }

    public boolean isEquippedBoots() {
        return ItemUtil.isNotAirOrNull(boots);
    }
}
