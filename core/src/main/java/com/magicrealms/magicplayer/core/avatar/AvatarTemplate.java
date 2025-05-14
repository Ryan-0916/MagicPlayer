package com.magicrealms.magicplayer.core.avatar;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


/**
 * @author Ryan-0916
 * @Desc 头像模板
 * @date 2025-05-02
 */
@Builder
@Getter
public class AvatarTemplate {
    /* id */
    private int id;
    /* 默认 */
    private boolean isDefault;
    /* 偏移量 */
    private int offset;
    /* 字体 */
    private String font;
    /* 格式 */
    private List<String> format;


}
