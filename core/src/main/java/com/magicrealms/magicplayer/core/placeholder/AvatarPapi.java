package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magiclib.core.holder.AbstractPaPiHolder;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 玩家数据部分变量
 * @date 2025-05-10
 */
@SuppressWarnings("unused")
public class AvatarPapi extends AbstractPaPiHolder {
    private static final String ID_PATTERN = "^\\d+$";
    private static final String PLAYER_ID_PATTERN = "^(\\S+)_(\\d+)$";

    public AvatarPapi(BukkitMagicPlayer plugin) {
        super("avatar", "Ryan0916", "1.0");
        this.register();
    }

    @Override
    protected String onOffline(OfflinePlayer player, String params) {
        if (isNumericId(params)) {
            return handleSelfAvatarRequest(player.getName(), params);
        }
        return null;
    }

    @Override
    protected String onOnline(Player player, String params) {
        if (isNumericId(params)) {
            return handleSelfAvatarRequest(player.getName(), params);
        } else if (isPlayerIdFormat(params)) {
            return handleOtherPlayerAvatarRequest(params);
        }
        return null;
    }

    private boolean isNumericId(String input) {
        return Pattern.compile(ID_PATTERN).matcher(input).matches();
    }

    private boolean isPlayerIdFormat(String input) {
        return Pattern.compile(PLAYER_ID_PATTERN).matcher(input).matches();
    }

    private String handleSelfAvatarRequest(String name, String params) {
        int layoutId = Integer.parseInt(params);
        return BukkitMagicPlayer.getInstance().getAvatarManager()
                .getAvatar(layoutId, name);
    }

    private String handleOtherPlayerAvatarRequest(String params) {
        String[] parts = params.split("_");
        if (parts.length != 2) {
            return null;
        }
        String targetPlayer = parts[0];
        int layoutId = Integer.parseInt(parts[1]);
        return BukkitMagicPlayer.getInstance().getAvatarManager()
                .getAvatar(layoutId, targetPlayer);
    }
}