package com.magicrealms.magicplayer.api.setting;

import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 抽象设置模块
 * @date 2025-05-10
 */
@Getter
@Builder
public class Setting {
    /* 权重 */
    protected int weight;
    /* 标题 - 显示在 */
    protected String title;
    /* 名称 */
    protected String name;
    /* 介绍 */
    protected String description;
    /* 点击后执行的操作 SettingPrams 为 Setting 菜单的一些属性 */
    protected Consumer<SettingPrams> clickAction;
}
