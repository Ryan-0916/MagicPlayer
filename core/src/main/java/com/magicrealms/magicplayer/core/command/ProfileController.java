package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.menu.ProfileMenu;
import org.bukkit.entity.Player;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;

/**
 * @author Ryan-0916
 * @Desc 资料卡部分相关命令
 * @date 2025-05-09
 */
@CommandListener
@SuppressWarnings("unused")
public class ProfileController {
    @Command(text = "^\\s?$", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.profile", label = "^profile$")
    public void profile(Player sender, String[] args) {
        new ProfileMenu(sender, BukkitMagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryByPlayer(sender));
    }

    @Command(text = "^\\S+$", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.profile.see", label = "^profile$")
    public void profileSee(Player sender, String[] args) {
        PlayerData profileData = BukkitMagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryById(args[0]);
        if (profileData == null) {
            MessageDispatcher.getInstance().sendMessage(BukkitMagicPlayer.getInstance()
                    , sender, BukkitMagicPlayer.getInstance().getConfigManager().getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Error.NoAnyPlayer"));
            return;
        }
        new ProfileMenu(sender, profileData);
    }
}
