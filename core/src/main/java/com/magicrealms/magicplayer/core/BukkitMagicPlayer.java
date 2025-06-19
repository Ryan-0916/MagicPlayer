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
import com.magicrealms.magicplayer.core.frame.FrameManager;
import com.magicrealms.magicplayer.core.listener.PlayerListener;
import com.magicrealms.magicplayer.core.menu.AvatarFrameMenu;
import com.magicrealms.magicplayer.core.menu.BackgroundFrameMenu;
import com.magicrealms.magicplayer.core.placeholder.AvatarFramePapi;
import com.magicrealms.magicplayer.core.placeholder.AvatarPapi;
import com.magicrealms.magicplayer.core.placeholder.BackgroundFramePapi;
import com.magicrealms.magicplayer.core.placeholder.PlayerDataPapi;
import com.magicrealms.magicplayer.core.repository.PlayerDataRepository;
import com.magicrealms.magicplayer.core.setting.SettingRegistry;
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
    private FrameManager avatarFrameManager;

    @Getter
    private FrameManager backgroundFrameManager;

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
            /* 初始化设置 */
            setupSetting();
            /* 初始化头像框 */
            setupAvatarFrame();
            /* 初始化背景框 */
            setupBackgroundFrame();
            /* 初始化玩家数据持久层 */
            setupPlayerDataRepository();
            /* 初始化 API */
            setupApi();
            /* 变量部分 */
            setupPapi();
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        }, "SkinsRestorer");
    }

    public void setupPapi() {
        if (dependenciesCheck("PlaceholderAPI")) {
            new AvatarPapi(this);
            new PlayerDataPapi(this);
            new AvatarFramePapi(this);
            new BackgroundFramePapi(this);
        }
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

    private void setupSetting() { this.settingRegistry = new SettingRegistry(); }

    public void setupAvatarFrame() {
        this.avatarFrameManager = new FrameManager(this, YML_AVATAR_FRAME, AvatarFrameMenu::new);
        this.avatarFrameManager.registrySetting();
    }

    public void setupBackgroundFrame() {
        this.backgroundFrameManager = new FrameManager(this, YML_BACKGROUND_FRAME, BackgroundFrameMenu::new);
        this.backgroundFrameManager.registrySetting();
    }

    public void destroyAvatarFrame() {
        this.avatarFrameManager.destroySetting();
    }

    public void destroyBackgroundFrame() {
        this.backgroundFrameManager.destroySetting();
    }

    public void setupPlayerDataRepository() {
        this.playerDataRepository = new PlayerDataRepository(mongoDBStore,
                MAGIC_PLAYERS_TABLE_NAME, redisStore, configManager.getYmlValue(YML_CONFIG, "Cache.PlayerData", 3600L, ParseType.LONG));
    }

    private void setupApi() { this.api = new MagicPlayerAPI(this); }

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
                YML_AVATAR_FRAME,
                YML_BACKGROUND_FRAME,
                YML_PLAYER_MENU,
                YML_PROFILE_MENU,
                YML_SETTING_MENU,
                YML_AVATAR_FRAME_MENU,
                YML_BACKGROUND_FRAME_MENU
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
    protected void registerPacketListener(PacketManager packetManager) {}

}
