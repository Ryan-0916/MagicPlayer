package com.magicrealms.magicplayer.api.setting;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 子菜单所需参数
 * @date 2025-05-14
 */
public record SettingParam(Player player,
                           List<Setting> settings,
                           int page,
                           int settingCount,
                           int selectedIndex,
                           Runnable backRunnable) {
    public static SettingParam of(Player player,
                                  List<Setting> settings,
                                  int page,
                                  int settingCount,
                                  int selectedIndex,
                                  Runnable backRunnable) {
        return new SettingParam(player, settings, page, settingCount, selectedIndex, backRunnable);
    }
}
