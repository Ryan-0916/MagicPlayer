package com.magicrealms.magicplayer.core.avatar;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

/**
 * @author Ryan-0916
 * @Desc 玩家头像格式化配置类
 * 记录头像格式化相关配置
 * @date 2025-05-02
 */
@Builder
@Getter
public class AvatarFormat {
    private int id;
    private String font;
    private String offsetChar;
    private ArrayList<String> format;
}
