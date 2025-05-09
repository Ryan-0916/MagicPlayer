package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magicplayer.core.MagicPlayer;
import org.bukkit.entity.Player;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;

/**
 * @author Ryan-0916
 * @Desc 头像部分命令
 * @date 2025-05-08
 */
@CommandListener
@SuppressWarnings("unused")
public class AvatarController {

    @Command(text = "^\\S+$", permissionType = PermissionType.OP, label = "^avatar$")
    public void avatar(Player sender, String[] args) {
        try {
            int i = Integer.parseInt(args[0]);
            MessageDispatcher.getInstance().sendMessage(
                    MagicPlayer.getInstance(),
                    sender,
                    MagicPlayer.getInstance().getAvatarManager()
                            .getPlayerAvatar(sender.getName(), i)
            );
        } catch (Exception exception) {
            MessageDispatcher.getInstance().sendMessage(
                    MagicPlayer.getInstance(),
                    sender,
                    MagicPlayer.getInstance().getConfigManager().getYmlValue(YML_LANGUAGE, "PlayerMessage.Error.UnKnowAvatarId")
            );
            MagicPlayer.getInstance().getLogger().warning("尝试获取头像失败，原因：未知的头像 ID " + args[0]);
        }
    }

}
