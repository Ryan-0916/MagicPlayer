package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.annotations.TabComplete;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magicplayer.common.storage.PlayerSessionStorage;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Ryan-0916
 * @Desc 头像部分命令
 * @date 2025-05-08
 */
@CommandListener
@SuppressWarnings("unused")
public class AvatarTabController {

    @TabComplete(text = "^\\s?$", permissionType = PermissionType.OP, label = "^avatar$")
    public List<String> first(CommandSender sender, String[] args) {
        return Stream.concat(Stream.of("id"),
                PlayerSessionStorage.getOnlinePlayerNames(BukkitMagicPlayer
                                .getInstance()
                                .getRedisStore()).stream())
                .toList();
    }

    @TabComplete(text = "^\\S+$", permissionType = PermissionType.OP, label = "^avatar$")
    public List<String> firstTab(CommandSender sender, String[] args) {
        return Stream.concat(Stream.of("id"),
                        PlayerSessionStorage.getOnlinePlayerNames(BukkitMagicPlayer
                                .getInstance()
                                .getRedisStore()).stream())
                .filter(e ->
                        StringUtils.startsWithIgnoreCase(e, args[0]))
                .toList();
    }

    @TabComplete(text = "^\\S+\\s$", permissionType = PermissionType.OP, label = "^avatar$")
    public List<String> second(CommandSender sender, String[] args) {
        return Stream.of("id")
                .toList();
    }

    @TabComplete(text = "^\\S+\\s\\S+$", permissionType = PermissionType.OP, label = "^avatar$")
    public List<String> secondTab(CommandSender sender, String[] args) {
        return Stream.of("id")
                .filter(e ->
                        StringUtils.startsWithIgnoreCase(e, args[1]))
                .toList();
    }

}
