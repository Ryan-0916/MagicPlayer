package com.magicrealms.magicplayer.api.repository;

import com.magicrealms.magiclib.common.repository.IBaseRepository;
import com.magicrealms.magicplayer.api.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 玩家数据存储类
 * @date 2025-06-08
 */
public interface IPlayerDataRepository extends IBaseRepository<PlayerData> {

    PlayerData queryByPlayer(Player player);

    void asyncUpdateByPlayer(Player player, Consumer<PlayerData> consumer);

    void updateByPlayer(Player player, Consumer<PlayerData> consumer);
}
