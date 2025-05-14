package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magicplayer.core.menu.SettingMenu;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 设置部分相关命令
 * @date 2025-05-10
 */
@CommandListener
@SuppressWarnings("unused")
public class SettingController {
    @Command(text = "^\\s?$", permissionType = PermissionType.PLAYER,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.setting", label = "^setting$")
    public void online(Player sender, String[] args) {
        new SettingMenu(sender, null);
    }
}
