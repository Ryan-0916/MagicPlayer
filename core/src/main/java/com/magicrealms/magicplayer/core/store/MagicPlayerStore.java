package com.magicrealms.magicplayer.core.store;

import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.utils.DocumentUtil;
import com.magicrealms.magicplayer.core.player.PlayerData;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.MAGIC_PLAYERS_TABLE_NAME;

@SuppressWarnings("unused")
public class MagicPlayerStore extends MongoDBStore {

    public MagicPlayerStore(String host, int port, String database) {
        super(host, port, database);
        super.createTable(MAGIC_PLAYERS_TABLE_NAME);
    }

    @NotNull
    public PlayerData getData(Player player) {
        PlayerData data = getData(player.getName());
        if (data != null) {
            return data;
        }
        data = new PlayerData(player);
        super.insertOne(MAGIC_PLAYERS_TABLE_NAME, DocumentUtil.toDocument(data));
        return data;
    }

    @Nullable
    public PlayerData getData(String name) {
        try (MongoCursor<Document> iterator = super
                .select(MAGIC_PLAYERS_TABLE_NAME, Filters.regex("name", "^"+name+"$", "i"))) {
            if (iterator.hasNext()) {
                return DocumentUtil.toObject(iterator.next(), PlayerData.class);
            }
        } finally {
            super.close();
        }
        return null;
    }

    public boolean updateData(String name, PlayerData data) {
        return super.updateOne(MAGIC_PLAYERS_TABLE_NAME
                , Filters.regex("name", "^" + name + "$", "i")
                , new Document("$set", DocumentUtil.toDocument(data)));
    }

}