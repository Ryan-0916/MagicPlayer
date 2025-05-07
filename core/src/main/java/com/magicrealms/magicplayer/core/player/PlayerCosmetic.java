package com.magicrealms.magicplayer.core.player;

import com.magicrealms.magiclib.core.adapt.ItemStackFieldAdapter;
import com.magicrealms.magiclib.common.annotations.MongoField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 存储玩家时装相关信息 - 用于 Profile 使用
 * @date 2025-05-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class PlayerCosmetic {
    /* 帽子 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "cosmetic_hat")
    private ItemStack hat;
    /* 背包 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "cosmetic_bag")
    private ItemStack bag;
    /* 权杖 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "cosmetic_walking_stick")
    private ItemStack walkingStick;
    /* 气球 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "cosmetic_balloon")
    private ItemStack balloon;
    /* 喷漆 */
    @MongoField(adapter = ItemStackFieldAdapter.class, name = "cosmetic_spray")
    private ItemStack spray;

    public PlayerCosmetic(Player player) {}
}
