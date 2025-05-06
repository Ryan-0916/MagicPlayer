package com.magicrealms.magicplayer.core.repository;

import com.magicrealms.magiclib.common.repository.BaseRepository;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.common.utils.MongoDBUtil;
import com.magicrealms.magicplayer.core.player.PlayerData;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc TODO：一段话介绍该类对象
 * 例如我现在要创建一个 MongoDB 的对象，比如玩家对象，第一步创建实体类
 *
 *
 * @date 2025-05-06
 */
public class PlayerDataRepository extends BaseRepository<PlayerData> {

    public PlayerDataRepository(MongoDBStore mongoDBStore,
                                String tableName,
                                @Nullable RedisStore redisStore,
                                long cacheExpire) {
        super(mongoDBStore, tableName, redisStore, cacheExpire, PlayerData.class);
    }

    public PlayerData queryByPlayer(Player player) {
        String subKey = player.getName();
        Optional<PlayerData> redisData = Optional.ofNullable(super.getRedisStore())
                .flatMap((r) -> r.hGetObject(super.getCacheHkey(), subKey, PlayerData.class));
        if (redisData.isPresent()) {
            return redisData.get();
        }
        try (MongoCursor<Document> iterator = super.getMongoDBStore()
                .select(super.getTableName(), Filters.eq("name", subKey))) {
            PlayerData data;
            if (iterator.hasNext()) {
                data = MongoDBUtil.toObject(iterator.next(), PlayerData.class);
                Optional.ofNullable(super.getRedisStore())
                        .ifPresent(r -> r.hSetObject(super.getCacheHkey(), subKey, data, super.getCacheExpire()));
            } else {
                data = new PlayerData(player);
                insert(data);
                Optional.ofNullable(super.getRedisStore())
                        .ifPresent(r -> r.hSetObject(super.getCacheHkey(), subKey, data, super.getCacheExpire()));
            }
            return data;
        } finally {
            super.getMongoDBStore().close();
        }
    }

    public void updateByPlayer(Player player, Consumer<PlayerData> consumer) {
        updateById(queryByPlayer(player)
                .getName(), consumer);
    }

}
