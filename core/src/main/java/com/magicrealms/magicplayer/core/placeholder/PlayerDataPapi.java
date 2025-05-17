package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magiclib.core.holder.AbstractPaPiHolder;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 玩家数据相关变量
 * %playerdata_name_up% 大写格式的玩家名称
 * @date 2025-05-10
 */
@SuppressWarnings("unused")
public class PlayerDataPapi extends AbstractPaPiHolder {

    private static final String NAME_UP_PATTERN = "^name_up$";

    public PlayerDataPapi(BukkitMagicPlayer plugin) {
        super("PlayerData", "Ryan0916", "1.0");
        this.register();
    }

    private boolean isPlayerNameUp(String input) {
        return Pattern.compile(NAME_UP_PATTERN).matcher(input).matches();
    }

    @Override
    protected String onOffline(OfflinePlayer player, String params) {
        if (isPlayerNameUp(params)) {
            return StringUtils.upperCase(player.getName());
        }
        return null;
    }

    @Override
    protected String onOnline(Player player, String params)  {
        if (isPlayerNameUp(params)) {
            return StringUtils.upperCase(player.getName());
        }
        return null;
    }

}
