package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.annotations.TabComplete;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magicplayer.common.util.PlayerSessionUtil;
import com.magicrealms.magicplayer.core.MagicPlayer;
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

    @TabComplete(text = "^\\s?\\S+$", permissionType = PermissionType.PLAYER,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.profile.see",
            label = "^profile$")
    public List<String> firstTab(CommandSender sender, String[] args) {
        return PlayerSessionUtil.getOnlinePlayerNames(MagicPlayer
                .getInstance()
                .getRedisStore());
    }


}
