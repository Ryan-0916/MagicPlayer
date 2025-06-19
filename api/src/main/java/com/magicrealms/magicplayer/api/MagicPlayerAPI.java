package com.magicrealms.magicplayer.api;

import com.magicrealms.magicplayer.api.avatar.IAvatarManager;
import com.magicrealms.magicplayer.api.exception.UnknownAvatarTemplate;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.api.repository.IPlayerDataRepository;
import com.magicrealms.magicplayer.api.setting.ISettingRegistry;
import com.magicrealms.magicplayer.api.setting.Setting;
import com.magicrealms.magicplayer.api.skin.ISkinManager;
import com.magicrealms.magicplayer.common.storage.PlayerSessionStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Ryan-0916
 * @Desc MagicPlayer API
 * @date 2025-05-13
 */
@SuppressWarnings("unused")
public record MagicPlayerAPI(MagicPlayer plugin) {

    private static MagicPlayerAPI instance;

    public MagicPlayerAPI(MagicPlayer plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static MagicPlayerAPI getInstance() {
        if (instance == null) {
            throw new RuntimeException("MagicPlayer API 未被初始化");
        }
        return instance;
    }

    /**
     * Get player avatar by default template
     * {@link IAvatarManager#getAvatar(String)}
     */
    public String getPlayerAvatar(String playerName) throws UnknownAvatarTemplate {
        return plugin.getAvatarManager().getAvatar(playerName);
    }

    /**
     * Get player avatar by template id
     * {@link IAvatarManager#getAvatar(int, String)}
     */
    public String getPlayerAvatar(int templateId, String playerName) throws UnknownAvatarTemplate {
        return plugin.getAvatarManager().getAvatar(templateId, playerName);
    }

    /**
     * Get textures by player
     * {@link ISkinManager#getTextures(Player)}
     */
    public String getTextures(Player player) {
        return plugin.getSkinManager().getTextures(player);
    }

    /**
     * Get skin by textures
     * {@link ISkinManager#getSkin(String)}
     */
    public String getSkin(String base64Texture) {
        return plugin.getSkinManager().getSkin(base64Texture);
    }

    /**
     * Get avatar by skin
     * {@link ISkinManager#getAvatar(String)}
     */
    public String getAvatar(String base64Skin) {
        return plugin.getSkinManager().getAvatar(base64Skin);
    }

    /**
     * Get skull by textures
     * {@link ISkinManager#getSkin(String)}
     */
    public ItemStack getSkull(String base64Texture) {
        return plugin.getSkinManager().getSkull(base64Texture);
    }

    /**
     * Registry a custom setting model
     * {@link ISettingRegistry#registry(Setting)}
     */
    public void registerSetting(Setting setting) {
        plugin.getSettingRegistry().registry(setting);
    }

    /**
     * Destroy a custom setting model
     * {@link ISettingRegistry#destroy(Setting)}
     */
    public void destroySetting(Setting setting) {
        plugin.getSettingRegistry().destroy(setting);
    }

    /**
     * query player data
     * {@link IPlayerDataRepository#queryByPlayer(Player)}
     */
    public PlayerData queryPlayerData(Player player) {
        return plugin.getPlayerDataRepository().queryByPlayer(player);
    }

    /**
     * query player data
     * {@link IPlayerDataRepository#queryById(Object)}
     */
    public @Nullable PlayerData queryPlayerData(String playerName) {
        return plugin.getPlayerDataRepository().queryById(playerName);
    }

    /**
     * query all OnlinePlayer Names
     * {@link IPlayerDataRepository#queryById(Object)}
     */
    public List<String> getOnlinePlayerNames() {
        return PlayerSessionStorage.getOnlinePlayerNames(MagicPlayer.getInstance().getRedisStore());
    }

}
