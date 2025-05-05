package com.magicrealms.magicplayer.core.store;

import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.utils.RedissonUtil;
import com.magicrealms.magicplayer.core.MagicPlayer;
import com.magicrealms.magicplayer.core.player.PlayerData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

/**
 * @author Ryan-0916
 * @Desc 玩家信息存储类
 * @date 2025-05-01
 */
@SuppressWarnings("unused")
public class PlayerDataStorage {

    private static volatile PlayerDataStorage instance;

    private PlayerDataStorage() {}

    public static PlayerDataStorage getInstance() {
        if (instance == null) {
            synchronized (PlayerDataStorage.class) {
                if (instance == null) {
                    instance = new PlayerDataStorage();
                }
            }
        }
        return instance;
    }

    public void updatePlayerData(Player player,
                                 Consumer<PlayerData> updateConsumer) {
        updateAndCachePlayerData(player.getName(),
                getPlayerData(player),
                updateConsumer);
    }

    /* 修改玩家数据 */
    public void updatePlayerData(String playerName,
                                 Consumer<PlayerData> updateConsumer) {
        updateAndCachePlayerData(playerName,
                getPlayerData(playerName),
                updateConsumer);
    }


    private void updateAndCachePlayerData(String playerName,
                                          PlayerData cacheData,
                                          Consumer<PlayerData> updateConsumer) {
        MagicPlayer plugin = MagicPlayer.getInstance();
        String subKey = StringUtils.upperCase(playerName);
        RedissonUtil.doAsyncWithLock(MagicPlayer.getInstance().getRedisStore(),
                String.format(PLAYERS_DATA_LOCK, subKey),
                subKey,
                5000, () -> {
                    /* 修改表字段 */
                    PlayerData update = new PlayerData();
                    updateConsumer.accept(update);
                    if (!plugin.getMagicPlayerStore().updateData(subKey, update)) {
                        return;
                    }
                    /* 修改 Redis 缓存 */
                    if (cacheData == null) {
                        return;
                    }
                    updateConsumer.accept(cacheData);
                    cachePlayerData(subKey, cacheData);
                });
    }

    @NotNull
    public PlayerData getPlayerData(Player player) {
        String playerName = StringUtils.upperCase(player.getName());
        PlayerData data = getPlayerDataFromCacheOrStore(playerName);
        return data != null ? data : getPlayerDataFromStoreAndCache(player);
    }

    @Nullable
    public PlayerData getPlayerData(String name) {
        String playerName = StringUtils.upperCase(name);
        return getPlayerDataFromCacheOrStore(playerName);
    }

    @Nullable
    private PlayerData getPlayerDataFromCacheOrStore(String playerName) {
        MagicPlayer plugin = MagicPlayer.getInstance();
        return plugin.getRedisStore()
                .hGetObject(PLAYERS_DATA_HASH_KEY, playerName, PlayerData.class)
                .orElseGet(() -> {
                    PlayerData data = plugin.getMagicPlayerStore().getData(playerName);
                    if (data != null) {
                        cachePlayerData(playerName, data);
                    }
                    return data;
                });
    }

    @NotNull
    private PlayerData getPlayerDataFromStoreAndCache(Player player) {
        MagicPlayer plugin = MagicPlayer.getInstance();
        PlayerData data = plugin.getMagicPlayerStore().getData(player);
        cachePlayerData(player.getName(), data);
        return data;
    }

    private void cachePlayerData(String playerName, PlayerData data) {
        MagicPlayer.getInstance()
                .getRedisStore()
                .hSetObject(PLAYERS_DATA_HASH_KEY,
                        StringUtils.upperCase(playerName),
                        data,
                        MagicPlayer.getInstance()
                        .getConfigManager()
                        .getYmlValue(YML_CONFIG, "Cache.PlayerData", 3600, ParseType.INTEGER));
    }
}
