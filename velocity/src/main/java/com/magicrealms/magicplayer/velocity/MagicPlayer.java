package com.magicrealms.magicplayer.velocity;

import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magicplayer.velocity.listener.DailyPlayerListener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author Ryan-0916
 * @Desc Velocity-MagicPlayer
 * @date 2025-04-29
 */
@Plugin(id = "magicplayer",
        name = "MagicPlayer", version = "1.0",
        description = "魔法领域-玩家", authors = {"Ryan0916"},
        dependencies = {
            @Dependency(id = "magiclib")
        }
)
@SuppressWarnings("unused")
public class MagicPlayer {

    @Getter
    private static MagicPlayer INSTANCE;
    @Inject @Getter
    private Logger logger;
    @Getter
    private final ProxyServer server;
    @Getter
    private final RedisStore redisStore;

    @Inject
    public MagicPlayer(ProxyServer server, Logger logger) {
        INSTANCE = this;
        this.server = server;
        this.logger = logger;
        /* TODO: 根据配置文件读取 Redis 地址 */
        this.redisStore = new RedisStore("127.0.0.1", 6379, "1qaZxsw@");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getEventManager().register(this, new DailyPlayerListener());
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        server.getEventManager().unregisterListeners(this);
    }

}
