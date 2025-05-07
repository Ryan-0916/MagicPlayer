package com.magicrealms.magicplayer.core;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.bukkit.manage.BungeeMessageManager;
import com.magicrealms.magiclib.bukkit.manage.CommandManager;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.bukkit.manage.PacketManager;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magicplayer.core.avatar.AvatarManager;
import com.magicrealms.magicplayer.core.listener.PlayerListener;
import com.magicrealms.magicplayer.core.repository.PlayerDataRepository;
import lombok.Getter;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import org.bukkit.Bukkit;

import java.util.Optional;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

public class MagicPlayer extends MagicRealmsPlugin {

    @Getter
    private static MagicPlayer instance;

    @Getter
    private RedisStore redisStore;

    @Getter
    private MongoDBStore mongoDBStore;

    @Getter
    PlayerDataRepository playerDataRepository;

    @Getter
    private BungeeMessageManager bungeeMessageManager;

    @Getter
    private SkinsRestorer skinsRestorer;

    @Getter
    private AvatarManager avatarManager;

    public MagicPlayer() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        dependenciesCheck(() -> {
            loadConfig(getConfigManager());
            registerCommand(commandManager);
            registerPacketListener(packetManager);
            setupRedisStore();
            setupMongoDB();
            setupAvatar();
            setupPlayerDataRepository();
            /* 皮肤插件前置 */
            skinsRestorer = SkinsRestorerProvider.get();
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        }, "SkinsRestorer");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        unsubscribe();
        mongoDBStore.destroy();
    }

    public void setupRedisStore() {
        String host = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Host"), password = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Password");
        int port = getConfigManager().getYmlValue(YML_REDIS, "DataSource.Port", 6379, ParseType.INTEGER);
        boolean redisPasswordModel = getConfigManager().getYmlValue(YML_REDIS, "DataSource.PasswordModel", false, ParseType.BOOLEAN);
        /* ItemUtil GSON 的目的是为了让 Gson 兼容 ItemStack 的转换 */
        this.redisStore = new RedisStore(ItemUtil.GSON, host, port, redisPasswordModel ? password : null);
        this.unsubscribe();
        this.bungeeMessageManager = new BungeeMessageManager.Builder().channel(BUNGEE_CHANNEL)
                .plugin(this)
                .host(host)
                .port(port)
                .passwordModel(redisPasswordModel)
                .password(password)
                .messageListener(e -> {
                    switch (e.getType()) {
                        case SERVER_MESSAGE
                                -> MessageDispatcher.getInstance().sendBroadcast(this, e.getMessage());
                        case PLAYER_MESSAGE
                                -> Optional.ofNullable(Bukkit.getPlayerExact(e.getRecipientName())).ifPresent(player -> MessageDispatcher.getInstance().sendMessage(this, player, e.getMessage()));
                    }
                }).build();
    }

    public void setupMongoDB() {
        String host = getConfigManager().getYmlValue(YML_MONGODB, "DataSource.Host")
                , database = getConfigManager().getYmlValue(YML_MONGODB, "DataSource.Database");
        int port = getConfigManager().getYmlValue(YML_MONGODB, "DataSource.Port", 27017, ParseType.INTEGER);
        this.mongoDBStore = new MongoDBStore(host, port, database);
    }

    public void setupPlayerDataRepository() {
        this.playerDataRepository = new PlayerDataRepository(mongoDBStore,
                MAGIC_PLAYERS_TABLE_NAME, redisStore, getConfigManager()
                .getYmlValue(YML_CONFIG, "Cache.PlayerData", 3600L, ParseType.LONG));
    }

    public void setupAvatar() {
        avatarManager = new AvatarManager(this);
    }

    private void unsubscribe() {
        Optional.ofNullable(bungeeMessageManager)
                .ifPresent(BungeeMessageManager::unsubscribe);
    }

    @Override
    protected void loadConfig(ConfigManager configManager) {
        configManager.loadConfig(YML_CONFIG,
                YML_LANGUAGE,
                YML_REDIS,
                YML_MONGODB,
                YML_AVATAR,
                YML_PLAYER_MENU);
    }

    @Override
    protected void registerCommand(CommandManager commandManager) {
        commandManager.registerCommand(PLUGIN_NAME, e -> {
            switch (e.cause()) {
                case NOT_PLAYER -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "ConsoleMessage.Error.NotPlayer"));
                case NOT_CONSOLE -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.NotConsole"));
                case UN_KNOWN_COMMAND -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.UnknownCommand"));
                case PERMISSION_DENIED -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.PermissionDenied"));
            }
        });
    }

    @Override
    protected void registerPacketListener(PacketManager packetManager) {
        packetManager.registerListeners();
    }

}
