package com.magicrealms.magicplayer.common.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magicplayer.common.player.PlayerSession;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.DAILY_PLAYERS_HASH_KEY;

/**
 * @author Ryan-0916
 * @Desc 今日在线玩家缓存工具类
 * @date 2025-05-09
 */
@SuppressWarnings("unused")
public final class PlayerSessionStorage {

    private static final Cache<String, List<String>> ONLINE_CACHE =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build();

    private static final String CACHE_KEY = "ONLINE_PLAYERS";

    private PlayerSessionStorage() {}

    public static Optional<PlayerSession> getPlayerSession(RedisStore store, String name) {
        if (StringUtils.isBlank(name)) {
            return Optional.empty();
        }
        return store.hGetObject(DAILY_PLAYERS_HASH_KEY, StringUtils.upperCase(name), PlayerSession.class);
    }

    public static List<String> getOnlinePlayerNames(RedisStore store) {
        try {
            return ONLINE_CACHE.get(CACHE_KEY,
                    () -> queryOnlinePlayerNames(store));
        } catch (ExecutionException exception) {
            return queryOnlinePlayerNames(store);
        }
    }

    public static List<String> queryOnlinePlayerNames(RedisStore store) {
        return store.hGetAllObject(DAILY_PLAYERS_HASH_KEY, PlayerSession.class)
                .map(e -> e.stream()
                        .map(PlayerSession::getName)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

}
