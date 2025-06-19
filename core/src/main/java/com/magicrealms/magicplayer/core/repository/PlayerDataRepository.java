package com.magicrealms.magicplayer.core.repository;

import com.magicrealms.magiclib.common.repository.BaseRepository;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.common.utils.MongoDBUtil;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.api.repository.IPlayerDataRepository;
import com.mongodb.client.MongoCursor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 玩家数据存储类
 * @date 2025-05-06
 */
public class PlayerDataRepository extends BaseRepository<PlayerData> implements IPlayerDataRepository {

    public PlayerDataRepository(MongoDBStore mongoDBStore,
                                String tableName,
                                @Nullable RedisStore redisStore,
                                long cacheExpire) {
        super(mongoDBStore, tableName, redisStore, true, cacheExpire, PlayerData.class);
    }

    public PlayerData queryByPlayer(Player player) {
        String id = StringUtils.upperCase(player.getName());
        Optional<PlayerData> cachedData = redisStore.
                hGetObject(cacheHkey, id, PlayerData.class);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }
        try (MongoCursor<Document> cursor = mongoDBStore
                .find(tableName, getIdFilter(id))) {
            PlayerData data;
            if (cursor.hasNext()) {
                data = MongoDBUtil.toObject(cursor.next(), PlayerData.class);
            } else {
                data = new PlayerData(player);
                insert(data);
            }
            cacheEntity(id, data);
            return data;
        }
    }

    public void asyncUpdateByPlayer(Player player, Consumer<PlayerData> consumer) {
        asyncUpdateById(queryByPlayer(player)
                .getName(), consumer);
    }

    public void updateByPlayer(Player player, Consumer<PlayerData> consumer) {
        updateById(queryByPlayer(player)
                .getName(), consumer);
    }

}
