package com.magicrealms.magicplayer.core.placeholder;

import com.magicrealms.magiclib.core.holder.AbstractPaPiHolder;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 抽象框变量
 * @date 2025-05-16
 */
public abstract class AbstractFramePapi extends AbstractPaPiHolder {

    private static final String SETTING_PATTERN = "^setting$";
    private static final String PROFILE_PATTERN = "^profile$";
    private static final String DEFAULT_PATTERN = "^default$";
    protected final BukkitMagicPlayer PLUGIN;

    public AbstractFramePapi(BukkitMagicPlayer plugin, String identifier, String author, String version) {
        super(identifier, author, version);
        this.PLUGIN = plugin;
        this.register();
    }

    protected abstract String getSettingFrame(PlayerData data);
    protected abstract String getDefaultFrame(PlayerData data);
    protected abstract String getProfileFrame(PlayerData data);

    private String handFrame(@Nullable String name, String params) {
        boolean isSetting = isSetting(params)
                , isProfile = isProfile(params)
                , isDefault = isDefault(params);

        if (!isSetting && !isProfile && !isDefault) {
            return null;
        }

        PlayerData data = PLUGIN.getPlayerDataRepository().queryById(name);
        if (data == null || data.getAvatarFrameId() == null) {
            return StringUtils.EMPTY;
        }

        return isSetting ? getSettingFrame(data)
                : isProfile ? getProfileFrame(data)
                : getDefaultFrame(data);
    }

    @Override
    protected String onOffline(OfflinePlayer player, String params) {
        return handFrame(player.getName(), params);
    }

    @Override
    protected String onOnline(Player player, String params){
        return handFrame(player.getName(), params);
    }

    private boolean isSetting(String input) {
        return Pattern.compile(SETTING_PATTERN).matcher(input).matches();
    }

    private boolean isProfile(String input) {
        return Pattern.compile(PROFILE_PATTERN).matcher(input).matches();
    }

    private boolean isDefault(String input) {
        return Pattern.compile(DEFAULT_PATTERN).matcher(input).matches();
    }
}

