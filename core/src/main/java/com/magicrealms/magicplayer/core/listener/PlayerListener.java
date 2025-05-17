package com.magicrealms.magicplayer.core.listener;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.menu.ProfileMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Ryan-0916
 * @Desc 玩家相关事件
 * @date 2025-05-01
 */
public class PlayerListener implements Listener {

    private int i = 0;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        /* 初始化玩家的一些属性 */
        BukkitMagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryByPlayer(e.getPlayer());
    }

    /**
     * 玩家装备变更
     * 玩家装备变更时需要将玩家装备信息存入到 PlayerData 中
     * @param e 玩家装备变更事件
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerArmorChangeEvent(PlayerArmorChangeEvent e) {
        Player player = e.getPlayer();
        BukkitMagicPlayer.getInstance().getPlayerDataRepository()
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


    /**
     * 玩家点击实体事件
     * Shift + 右键 打开玩家明信片
     * @param e 玩家点击实体事件
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        if (i++ % 2 != 0) { return; }
        if (e.getRightClicked() instanceof Player clicked
                && !clicked.hasMetadata("NPC")
                && e.getPlayer().isSneaking()
                && e.getPlayer().hasPermission("magic.command.magicplayer.profile.see")) {
            PlayerData data = BukkitMagicPlayer.getInstance()
                    .getPlayerDataRepository()
                    .queryByPlayer(clicked);
            new ProfileMenu(player, data);
        }
    }

}
