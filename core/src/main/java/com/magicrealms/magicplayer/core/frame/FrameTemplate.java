package com.magicrealms.magicplayer.core.frame;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 头像框模板
 * @date 2025-05-12
 */
@Getter
@Builder
public class FrameTemplate {
    /* id */
    private int id;
    /* 头像框 */
    private String frame;
    /* 明信片模块头像框 */
    private String profileFrame;
    /* 设置模块头像框 */
    private String settingFrame;
    /* 拥有该权限的玩家才可以解锁 */
    private String permission;
    /* 解锁后菜单中显示的物品 */
    private ItemStack unlockItem;
    /* 未解锁时菜单中显示的物品 */
    private ItemStack lockItem;

    /**
     * 玩家是否解锁该头像框
     * @param player 玩家
     * @return 是否解锁该头像框
     */
    public boolean isUnlocked(Player player) {
        return StringUtils.isBlank(permission) || player.hasPermission(permission);
    }
}
