package com.magicrealms.magicplayer.core.avatar.frame;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 头像框
 * @date 2025-05-12
 */
@Getter
@Builder
public class AvatarFrame {
    private int id;
    private String frame;
    private String profileFrame;
    private String settingFrame;
    private int weight;
    private ItemStack activeItem;
    private ItemStack disabledItem;
}
