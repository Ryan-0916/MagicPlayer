package com.magicrealms.magicplayer.core.listener;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.magicrealms.magicplayer.core.MagicPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Ryan-0916
 * @Desc 玩家相关事件
 * @date 2025-05-01
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        /* 初始化玩家的一些属性 */
        MagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryByPlayer(e.getPlayer());
    }

    /**
     * 玩家装备变更
     * 玩家装备变更时需要将玩家装备信息存入到
     * PlayerData 中
     * @param e 玩家装备变更事件
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent e) {
        Player player = e.getPlayer();
        MagicPlayer.getInstance().getPlayerDataRepository()
                .updateById(player.getName(), data -> {
            switch (e.getSlot()) {
                case HEAD:
                    data.getArmor().setHelmet(e.getNewItem());
                    break;
                case CHEST:
                    data.getArmor().setChestplate(e.getNewItem());
                    break;
                case LEGS:
                    data.getArmor().setLeggings(e.getNewItem());
                    break;
                case FEET:
                    data.getArmor().setBoots(e.getNewItem());
                    break;
            }
        });
    }

}
