package com.magicrealms.magicplayer.core;

import com.magicrealms.magiclib.bukkit.manage.BungeeMessageManager;
import com.magicrealms.magiclib.bukkit.manage.CommandManager;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.bukkit.manage.PacketManager;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magicplayer.api.MagicPlayer;
import com.magicrealms.magicplayer.api.MagicPlayerAPI;
import com.magicrealms.magicplayer.core.avatar.AvatarManager;
import com.magicrealms.magicplayer.core.avatar.frame.AvatarFrameManager;
import com.magicrealms.magicplayer.core.listener.PlayerListener;
import com.magicrealms.magicplayer.core.placeholder.AvatarPapi;
import com.magicrealms.magicplayer.core.placeholder.PlayerDataPapi;
import com.magicrealms.magicplayer.api.player.repository.PlayerDataRepository;
import com.magicrealms.magicplayer.core.setting.SettingManager;
import com.magicrealms.magicplayer.core.skin.SkinManager;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Optional;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

public class BukkitMagicPlayer extends MagicPlayer {

    @Getter
    private static BukkitMagicPlayer instance;

    @Getter
    private BungeeMessageManager bungeeMessageManager;

    @Getter
    private SettingManager settingManager;

    @Getter
    private AvatarFrameManager frameManager;

    public BukkitMagicPlayer() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        dependenciesCheck(() -> {
            /* 加载配置文件 */
            loadConfig(configManager);
            /* 注册指令 */
            registerCommand(commandManager);
            /* 注册数据包监听器 */
            registerPacketListener(packetManager);
            /* 初始化 Redis */
            setupRedisStore();
            /* 初始化 MongoDB */
            setupMongoDB();
            /* 初始化头像 */
            setupAvatar();
            /* 初始化皮肤 */
            setupSkin();


//            setupSetting();
//            setupFrame();
            /* 初始化玩家数据持久层 */
            setupPlayerDataRepository();
            /* 初始化 API */
            setupApi();



            /* 变量部分注册 */
            if (dependenciesCheck("PlaceholderAPI")) {
                new AvatarPapi().register();
                new PlayerDataPapi().register();
            }
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        }, "SkinsRestorer");
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

    public void setupAvatar() { this.avatarManager = new AvatarManager(this); }

    private void setupSkin() { this.skinManager = new SkinManager(this); }

    public void setupPlayerDataRepository() {
        this.playerDataRepository = new PlayerDataRepository(mongoDBStore,
                MAGIC_PLAYERS_TABLE_NAME, redisStore, configManager.getYmlValue(YML_CONFIG, "Cache.PlayerData", 3600L, ParseType.LONG));
    }

    private void setupApi() { this.api = new MagicPlayerAPI(this); }



    public void setupSetting() { settingManager = new SettingManager(this); }

    public void setupFrame() { frameManager = new AvatarFrameManager(this); }




    private void unsubscribe() {
        Optional.ofNullable(bungeeMessageManager)
                .ifPresent(BungeeMessageManager::unsubscribe);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        unsubscribe();
        mongoDBStore.destroy();
    }

    @Override
    protected void loadConfig(ConfigManager configManager) {
        configManager.loadConfig(YML_CONFIG,
                YML_LANGUAGE,
                YML_REDIS,
                YML_MONGODB,
                YML_AVATAR,
                YML_SETTING,
                YML_AVATAR_FRAME,
                YML_PLAYER_MENU,
                YML_PROFILE_MENU,
                YML_SETTING_MENU,
                YML_AVATAR_FRAME_MENU
        );
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
