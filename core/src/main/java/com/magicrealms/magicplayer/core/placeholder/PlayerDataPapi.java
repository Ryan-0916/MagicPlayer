package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magiclib.core.holder.AbstractPaPiHolder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc Todo: 简介
 * @date 2025-05-10
 */
public class PlayerDataPapi extends AbstractPaPiHolder {

    private static final String NAME_UP_PATTERN = "^name_up$";

    public PlayerDataPapi() {
        super("PlayerData", "Ryan0916", "1.0");
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || params.isEmpty()) {
            return null;
        }
        try {
            if (isPlayerNameUp(params)) {
                return StringUtils.upperCase(player.getName());
            }
        } catch (Exception e) {
            System.err.println("Error processing avatar request: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null || params.isEmpty()) {
            return null;
        }
        try {
            if (isPlayerNameUp(params)) {
                return StringUtils.upperCase(player.getName());
            }
        } catch (Exception e) {
            System.err.println("Error processing avatar request: " + e.getMessage());
        }
        return null;
    }

    private boolean isPlayerNameUp(String input) {
        return Pattern.compile(NAME_UP_PATTERN).matcher(input).matches();
    }
}
