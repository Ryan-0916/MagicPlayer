package com.magicrealms.magicplayer.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magicplayer.common.player.PlayerSession;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.api.player.click.ClickAction;
import com.magicrealms.magicplayer.api.player.PlayerMenu;
import com.magicrealms.magicplayer.api.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.DAILY_PLAYERS_HASH_KEY;

/**
 * @author Ryan-0916
 * @Desc 在线玩家部分相关命令
 * @date 2025-05-02
 */
@CommandListener
@SuppressWarnings("unused")
public class OnlineController {

    @Command(text = "^\\s?$", permissionType = PermissionType.PERMISSION,
            permission = "magic.command.magicplayer.all||magic.command.magicplayer.online", label = "^online$")
    public void online(Player sender, String[] args) {
        Optional<List<PlayerData>> data = BukkitMagicPlayer.getInstance().getRedisStore()
                .hGetAllObject(DAILY_PLAYERS_HASH_KEY, PlayerSession.class)
                .map(e -> e.stream()
                        .filter(PlayerSession::isOnline)
                        .map(s -> BukkitMagicPlayer.getInstance().getPlayerDataRepository()
                                .queryById(s.getName())).collect(Collectors.toList()));
        new PlayerMenu.Builder()
                .leftAction(ClickAction.of("左键做鸡", e -> e.clicker().sendMessage("你做了" + e.clickData().getName() + "的鸡")))
                .rightAction(ClickAction.of("右键吃鸡", e -> e.clicker().sendMessage("你吃了" + e.clickData().getName() + "的鸡")))
                .data(data.orElse(new ArrayList<>()))
                .player(sender)
                .open();
    }

}
