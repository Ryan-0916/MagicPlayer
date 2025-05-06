package com.magicrealms.magicplayer.core.repository;

import com.magicrealms.magiclib.common.repository.BaseRepository;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.common.utils.MongoDBUtil;
import com.magicrealms.magicplayer.core.player.PlayerData;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc TODO：一段话介绍该类对象
 * @date 2025-05-06
 */
public class PlayerDataRepository extends BaseRepository<PlayerData> {

    public PlayerDataRepository(MongoDBStore mongoDBStore,
                                String tableName,
                                @Nullable RedisStore redisStore,
                                long cacheExpire) {
        super(mongoDBStore, tableName, redisStore, true, cacheExpire, PlayerData.class);
    }

    public PlayerData queryByPlayer(Player player) {
        String id = player.getName();
        Optional<PlayerData> cachedData = super.getRedisStore().
                hGetObject(super.getCacheHkey(), id, PlayerData.class);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }
        try (MongoCursor<Document> cursor = super.getMongoDBStore()
                .find(super.getTableName(), super.getIdFilter(id))) {
            PlayerData data;
            if (cursor.hasNext()) {
                data = MongoDBUtil.toObject(cursor.next(), PlayerData.class);
            } else {
                data = new PlayerData(player);
                super.insert(data);
            }
            super.cacheEntity(id, data);
            return data;
        }
    }

    public void updateByPlayer(Player player, Consumer<PlayerData> consumer) {
        updateById(queryByPlayer(player)
                .getName(), consumer);
    }

}
