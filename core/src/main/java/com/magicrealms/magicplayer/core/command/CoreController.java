package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magicplayer.core.MagicPlayer;
import org.bukkit.command.CommandSender;

import java.util.Locale;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

/**
 * @author Ryan-0916
 * @Desc 核心部分命令
 * @date 2025-05-02
 */
@CommandListener
@SuppressWarnings("unused")
public class CoreController {

    @Command(text = "^Reload$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload", label = "^magicPlayer$")
    public void reload(CommandSender sender, String[] args){
        MagicPlayer.getInstance().getConfigManager()
                .reloadConfig(YML_REDIS, YML_MONGODB);
        /* 重置 Avatar 部分 */
        MagicPlayer.getInstance().setupAvatar();
        MessageDispatcher.getInstance()
                .sendMessage(MagicPlayer.getInstance(), sender,
                        MagicPlayer.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\sAll$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload", label = "^magicPlayer$")
    public void reloadAll(CommandSender sender, String[] args){
        MagicPlayer.getInstance().getConfigManager().reloadAllConfig();
        /* 重置 Redis 部分 */
        MagicPlayer.getInstance().setupRedisStore();
        /* 重置 MongoDB 部分 */
        MagicPlayer.getInstance().setupMongoDB();
        /* 重置 Avatar 部分 */
        MagicPlayer.getInstance().setupAvatar();
        MessageDispatcher.getInstance()
                .sendMessage(MagicPlayer.getInstance(), sender,
                        MagicPlayer.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\s(?!all\\b)\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload", label = "^magicPlayer$")
    public void reloadBy(CommandSender sender, String[] args){
        MagicPlayer.getInstance().getConfigManager()
                .reloadConfig(args[1], e -> {
            if (!e) {
                MessageDispatcher.getInstance().sendMessage(MagicPlayer.getInstance(), sender,
                        MagicPlayer.getInstance().getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.ReloadFile"));
                return;
            }
            switch (args[1].toLowerCase(Locale.ROOT)) {
                case "redis" ->  MagicPlayer.getInstance().setupRedisStore();
                case "mongodb" -> MagicPlayer.getInstance().setupMongoDB();
                case "avatar" ->  MagicPlayer.getInstance().setupAvatar();
            }
            MessageDispatcher.getInstance().sendMessage(MagicPlayer.getInstance(), sender,
                    MagicPlayer.getInstance().getConfigManager()
                            .getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Success.ReloadFile"));
        });
    }
}
