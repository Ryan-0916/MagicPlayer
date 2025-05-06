package com.magicrealms.magicplayer.core.menu;

import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 点击信息
 * @date 2025-05-06
 */
public record ClickAction(String describe, Consumer<ClickHandler> handler) {

    public static ClickAction of(String describe, Consumer<ClickHandler> handler) {
        return new ClickAction(describe, handler);
    }

}
