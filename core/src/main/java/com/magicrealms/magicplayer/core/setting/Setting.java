package com.magicrealms.magicplayer.core.setting;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc Todo: 简介
 * @date 2025-05-10
 */
@Getter
@Builder
public class Setting {
    private SettingType type;
    private int weight;
    private String title;
    private String name;
    private String description;
}
