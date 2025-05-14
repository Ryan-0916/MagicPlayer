package com.magicrealms.magicplayer.core.menu.click;

import com.magicrealms.magicplayer.api.player.PlayerData;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 点击的记录
 * @date 2025-05-06
 */
public record ClickHandler(Player clicker, PlayerData clickData) {

    public static ClickHandler of(Player clicker, PlayerData clickData) {
        return new ClickHandler(clicker, clickData);
    }

}
