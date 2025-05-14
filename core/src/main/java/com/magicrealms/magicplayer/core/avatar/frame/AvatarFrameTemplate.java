package com.magicrealms.magicplayer.core.avatar.frame;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 头像框模板
 * @date 2025-05-12
 */
@Getter
@Builder
public class AvatarFrameTemplate {
    /* id */
    private int id;
    /* 头像框 */
    private String frame;
    /* 明信片模块头像框 */
    private String profileFrame;
    /* 设置模块头像框 */
    private String settingFrame;
    /* 权重 */
    private int weight;
    /* Todo：待优化 */
    private ItemStack activeItem;
    /* Todo：待优化 */
    private ItemStack disabledItem;
}
