package com.magicrealms.magicplayer.api.setting;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 子菜单所需参数
 * @date 2025-05-14
 */
public record SettingParam(Player player, Map<String, String> titlePapi, Runnable backRunnable) {
    public static SettingParam of(Player player,
                                  Map<String, String> titlePapi,
                                  Runnable backRunnable) {
        return new SettingParam(player, titlePapi, backRunnable);
    }
}
