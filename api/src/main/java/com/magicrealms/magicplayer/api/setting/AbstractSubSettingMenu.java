package com.magicrealms.magicplayer.api.setting;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.core.holder.PageMenuHolder;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 抽象子设置菜单
 * @date 2025-05-12
 */
@SuppressWarnings("unused")
public abstract class AbstractSubSettingMenu extends PageMenuHolder {

    /* 父菜单 PAPI */
    private final Map<String, String> PARENT_PAPI;


    public AbstractSubSettingMenu(MagicRealmsPlugin plugin,
                                  String configPath,
                                  String defLayout,
                                  SettingParam param) {
        super(plugin, param.player(), configPath, defLayout, param.backRunnable());
        this.PARENT_PAPI = param.titlePapi();
    }

    protected Map<String, String> createPlaceholders() {
        return PARENT_PAPI;
    }

}
