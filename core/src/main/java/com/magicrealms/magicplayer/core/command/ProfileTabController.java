package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.annotations.TabComplete;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magicplayer.common.storage.PlayerSessionStorage;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.List;


/**
 * @author Ryan-0916
 * @Desc 资料卡部分命令补全
 * @date 2025-05-09
 */
@CommandListener
@SuppressWarnings("unused")
public class ProfileTabController {

    @TabComplete(text = "^\\s?$", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.profile.see",
            label = "^profile$")
    public List<String> first(CommandSender sender, String[] args) {
        return PlayerSessionStorage.getOnlinePlayerNames(BukkitMagicPlayer
                .getInstance()
                .getRedisStore());
    }

    @TabComplete(text = "^\\S+$", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.profile.see", label = "^profile$")
    public List<String> firstTab(CommandSender sender, String[] args) {
        return PlayerSessionStorage.getOnlinePlayerNames(BukkitMagicPlayer
                        .getInstance()
                        .getRedisStore())
                .stream()
                .filter(e ->
                        StringUtils.startsWithIgnoreCase(e, args[0]))
                .toList();
    }

}
