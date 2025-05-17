package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magiclib.core.holder.AbstractPaPiHolder;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.avatar.AvatarTemplate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 玩家数据部分变量
 * %avatar_id% 查看自己的头像 id 为头像模板编号 {@link AvatarTemplate#getId()}
 * %avatar_playerName_id% 查看其余玩家的头像 playerName 为该玩家名称，id 为头像模板编号
 * @date 2025-05-10
 */
@SuppressWarnings("unused")
public class AvatarPapi extends AbstractPaPiHolder {
    private static final String ID_PATTERN = "^\\d+$";
    private static final String PLAYER_ID_PATTERN = "^(\\S+)_(\\d+)$";

    public AvatarPapi(BukkitMagicPlayer plugin) {
        super("Avatar", "Ryan0916", "1.0");
        this.register();
    }

    private boolean isSelf(String input) {
        return Pattern.compile(ID_PATTERN).matcher(input).matches();
    }

    private boolean isOther(String input) { return Pattern.compile(PLAYER_ID_PATTERN).matcher(input).matches(); }

    @Override
    protected String onOffline(OfflinePlayer player, String params) throws NumberFormatException {
        return isSelf(params) ? handleSelfAvatarRequest(player.getName(), params) : null;
    }

    @Override
    protected String onOnline(Player player, String params) throws NumberFormatException {
        return isSelf(params) ? handleSelfAvatarRequest(player.getName(), params) :
                isOther(params) ? handleOtherAvatarRequest(params) : null;
    }

    private boolean isPlayerIdFormat(String input) { return Pattern.compile(PLAYER_ID_PATTERN).matcher(input).matches(); }

    private String handleSelfAvatarRequest(String name, String params) throws NumberFormatException {
        int layoutId = Integer.parseInt(params);
        return BukkitMagicPlayer.getInstance().getAvatarManager()
                .getAvatar(layoutId, name);
    }

    private String handleOtherAvatarRequest(String params) throws NumberFormatException {
        String[] parts = params.split("_");
        if (parts.length != 2) { return null; }
        String targetPlayer = parts[0];
        int layoutId = Integer.parseInt(parts[1]);
        return BukkitMagicPlayer.getInstance().getAvatarManager()
                .getAvatar(layoutId, targetPlayer);
    }
}