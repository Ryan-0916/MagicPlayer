 package com.magicrealms.magicplayer.velocity.listener;

import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.common.utils.RedissonUtil;
import com.magicrealms.magicplayer.common.player.PlayerSession;
import com.magicrealms.magicplayer.common.player.PlayerStatus;
import com.magicrealms.magicplayer.common.storage.PlayerSessionStorage;
import com.magicrealms.magicplayer.velocity.MagicPlayer;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

 /**
 * @author Ryan-0916
 * @Desc 当日玩家相关事件监听
 * 主要负责处理当日玩家的相关数据
 * 如：在线状态，所在子服，登录时间等等
 * @date 2025-04-29
 */
@SuppressWarnings("unused")
public class DailyPlayerListener {

    private final static long EXPIRE = 24 * 60 * 60;
    private static final long LOCK_TIMEOUT = 5000;

    public void addDailyPlayerSession(Player player) {
        String subKey = StringUtils.upperCase(player.getUsername());
        long time = System.currentTimeMillis();
        MagicPlayer.getINSTANCE().getRedisStore().hSetObject(DAILY_PLAYERS_HASH_KEY, subKey,
                PlayerSession.builder()
                        .uuid(player.getUniqueId())
                        .name(player.getUsername())
                        .upTime(time)
                        .status(PlayerStatus.ONLINE)
                        .build(),
                EXPIRE);
    }


    private void updatePlayerSession(Player player, Consumer<PlayerSession> updater) {
        RedisStore store = MagicPlayer.getINSTANCE().getRedisStore();
        String subKey = StringUtils.upperCase(player.getUsername());
        RedissonUtil.doAsyncWithLock(store,
                String.format(DAILY_PLAYER_LOCK, subKey),
                subKey,
                LOCK_TIMEOUT,
                () -> {
                    Optional<PlayerSession> optionalDailyPlayer = PlayerSessionStorage.getPlayerSession(store, subKey);
                    if (optionalDailyPlayer.isEmpty()) {
                        addDailyPlayerSession(player);
                        return;
                    }
                    PlayerSession dailyPlayer = optionalDailyPlayer.get();
                    updater.accept(dailyPlayer);
                    store.hSetObject(DAILY_PLAYERS_HASH_KEY, subKey, dailyPlayer, EXPIRE);
                });
    }

    private void updateLoginSession(PlayerSession session) {
        session.setStatus(PlayerStatus.ONLINE);
        session.setStartAfkTime(0);
        session.setUpTime(System.currentTimeMillis());
    }

    @Subscribe()
    public EventTask onLoginEvent(LoginEvent event) {
        return EventTask.async(() -> {
            Player player = event.getPlayer();
            if (MagicPlayer.getINSTANCE()
                    .getRedisStore()
                    .hExists(DAILY_PLAYERS_HASH_KEY, String.valueOf(player.getUniqueId()))) {
                updatePlayerSession(player, this::updateLoginSession);
            } else {
                addDailyPlayerSession(player);
            }
        });
    }

    private void updateServerConnectedSession(PlayerSession session,
                                              ServerConnectedEvent event) {
        if (session.getStatus() != PlayerStatus.ONLINE &&
                session.getStatus() != PlayerStatus.HIDDEN) {
            session.setStatus(PlayerStatus.HIDDEN);
        }
        session.setStartAfkTime(0);
        session.setUpTime(System.currentTimeMillis());
        session.setServerName(event.getServer().getServerInfo().getName());
    }

    @Subscribe()
    public EventTask onServerConnectedEvent(ServerConnectedEvent event) {
        return EventTask.async(() -> {
            Player player = event.getPlayer();
            if (MagicPlayer.getINSTANCE().getRedisStore().hExists(DAILY_PLAYERS_HASH_KEY, String.valueOf(player.getUniqueId()))) {
                updatePlayerSession(player, session -> updateServerConnectedSession(session, event));
            } else {
                addDailyPlayerSession(player);
            }
        });
    }

    @Subscribe()
    public EventTask onDisconnectEvent(DisconnectEvent event) {
        return EventTask.async(() -> {
            Player player = event.getPlayer();
            String subKey = StringUtils.upperCase(player.getUsername());
            RedisStore store = MagicPlayer.getINSTANCE().getRedisStore();
            long offTime = System.currentTimeMillis();
            Optional<PlayerSession> playerSession
                    = PlayerSessionStorage.getPlayerSession(store, subKey);
            if (playerSession.isPresent()) {
                updatePlayerSession(player, session -> {
                    session.setStatus(PlayerStatus.OFFLINE);
                    session.setServerName(null);
                    session.setStartAfkTime(0);
                    session.setOffTime(offTime);
                    session.setPlayTime(session.getPlayTime() + (offTime - session.getUpTime()));
                });
                MagicPlayer.getINSTANCE().getPlayerDataRepository().updateById(
                        player.getUsername(), e -> {
                            e.setOffTime(offTime);
                            e.setPlaytime(e.getPlaytime() + offTime -
                                    playerSession.get().getUpTime());
                        }
                );
            } else {
                MagicPlayer.getINSTANCE().getPlayerDataRepository().updateById(
                        player.getUsername(), e -> e.setOffTime(offTime)
                );
            }
        });
    }

}
