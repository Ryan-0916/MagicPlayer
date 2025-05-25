package com.magicrealms.magicplayer.velocity.player.repository;

import com.magicrealms.magiclib.common.repository.BaseRepository;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magicplayer.velocity.player.PlayerData;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 玩家数据存储类
 * @date 2025-05-06
 */
public class PlayerDataRepository extends BaseRepository<PlayerData> {

    public PlayerDataRepository(MongoDBStore mongoDBStore,
                                String tableName,
                                @Nullable RedisStore redisStore,
                                long cacheExpire) {
        super(mongoDBStore, tableName, redisStore, true, cacheExpire, PlayerData.class);
    }

}
