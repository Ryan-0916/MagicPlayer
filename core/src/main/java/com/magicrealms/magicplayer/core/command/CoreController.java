package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
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

    private void setupCommon() {
        /* 重置 Avatar 部分 */
        BukkitMagicPlayer.getInstance().setupAvatar();
        /* 重置 头像框 部分 */
        BukkitMagicPlayer.getInstance().destroyAvatarFrame();
        BukkitMagicPlayer.getInstance().setupAvatarFrame();
        /* 重置 背景框 部分 */
        BukkitMagicPlayer.getInstance().destroyBackgroundFrame();
        BukkitMagicPlayer.getInstance().setupBackgroundFrame();
        BukkitMagicPlayer.getInstance().setupPlayerDataRepository();
    }

    @Command(text = "^Reload$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.reload", label = "^magicPlayer$")
    public void reload(CommandSender sender, String[] args){
        BukkitMagicPlayer.getInstance().getConfigManager()
                .reloadConfig(YML_REDIS, YML_MONGODB);
        setupCommon();
        MessageDispatcher.getInstance()
                .sendMessage(BukkitMagicPlayer.getInstance(), sender,
                        BukkitMagicPlayer.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\sAll$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.reload", label = "^magicPlayer$")
    public void reloadAll(CommandSender sender, String[] args){
        BukkitMagicPlayer.getInstance().getConfigManager().reloadAllConfig();
        /* 重置 Redis 部分 */
        BukkitMagicPlayer.getInstance().setupRedisStore();
        /* 重置 MongoDB 部分 */
        BukkitMagicPlayer.getInstance().setupMongoDB();
        setupCommon();
        MessageDispatcher.getInstance()
                .sendMessage(BukkitMagicPlayer.getInstance(), sender,
                        BukkitMagicPlayer.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\s(?!all\\b)\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.reload", label = "^magicPlayer$")
    public void reloadBy(CommandSender sender, String[] args){
        BukkitMagicPlayer.getInstance().getConfigManager()
                .reloadConfig(args[1], e -> {
            if (!e) {
                MessageDispatcher.getInstance().sendMessage(BukkitMagicPlayer.getInstance(), sender,
                        BukkitMagicPlayer.getInstance().getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.ReloadFile"));
                return;
            }
            switch (args[1].toLowerCase(Locale.ROOT)) {
                case "config" -> BukkitMagicPlayer.getInstance().setupPlayerDataRepository();
                case "redis" ->  {
                    BukkitMagicPlayer.getInstance().setupRedisStore();
                    BukkitMagicPlayer.getInstance().setupPlayerDataRepository();
                }
                case "mongodb" -> {
                    BukkitMagicPlayer.getInstance().setupMongoDB();
                    BukkitMagicPlayer.getInstance().setupPlayerDataRepository();
                }
                case "avatar" ->  BukkitMagicPlayer.getInstance().setupAvatar();
                case "avatarframe" -> {
                    BukkitMagicPlayer.getInstance().destroyAvatarFrame();
                    BukkitMagicPlayer.getInstance().setupAvatarFrame();
                }
                case "bgframe" -> {
                    /* 重置 背景框 部分 */
                    BukkitMagicPlayer.getInstance().destroyBackgroundFrame();
                    BukkitMagicPlayer.getInstance().setupBackgroundFrame();
                }
            }
            MessageDispatcher.getInstance().sendMessage(BukkitMagicPlayer.getInstance(), sender,
                    BukkitMagicPlayer.getInstance().getConfigManager()
                            .getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Success.ReloadFile"));
        });
    }
}
