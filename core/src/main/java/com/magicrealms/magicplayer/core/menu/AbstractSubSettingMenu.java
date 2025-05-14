package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.core.holder.PageMenuHolder;
import com.magicrealms.magicplayer.core.setting.Setting;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 抽象子设置菜单
 * @date 2025-05-12
 */
@SuppressWarnings("unused")
public abstract class AbstractSubSettingMenu extends PageMenuHolder {

    /* 设置列表 */
    private final List<Setting> SETTINGS;
    /* 每页的设置数量 */
    private final int SETTING_PAGE_COUNT;
    /* 设置页的当前页数 */
    private final int SETTING_PAGE;
    /* 选中的设置下标 */
    private final int SELECT_SETTING_INDEX;

    public AbstractSubSettingMenu(MagicRealmsPlugin plugin,
                                  Player player,
                                  String configPath,
                                  String defLayout,
                                  @Nullable Runnable backRunnable,
                                  List<Setting> settings,
                                  int settingPage,
                                  int settingPageCount,
                                  int selectSettingIndex) {
        super(plugin, player, configPath, defLayout, backRunnable);
        this.SETTINGS = settings;
        this.SETTING_PAGE_COUNT = settingPageCount;
        this.SETTING_PAGE = settingPage;
        this.SELECT_SETTING_INDEX = selectSettingIndex;
    }

    protected Map<String, String> createPlaceholders() {
        Map<String, String> map = new HashMap<>();
        /* 设置的 Title */
        final String SETTING_TITLE = "setting_title_%s";
        /* 是否拥有设置 */
        final String HAS_SETTING = "has_setting_%s";
        /* 是否选中设置 */
        final String SELECT_SETTING = "selected_setting_%s";
        /* 拥有设置的 YML 自定义 Papi Path */
        final String CUSTOM_PAPI_HAS_SETTING_PATH = "CustomPapi" +
                ".HasSetting_%s.%s";
        /* 选中设置的 YML 自定义 Papi Path */
        final String CUSTOM_PAPI_SELECT_SETTING_PATH = "CustomPapi" +
                ".SelectedSetting_%s.%s";

        int pageOffset = (SETTING_PAGE - 1) * SETTING_PAGE_COUNT;
        for (int i = 0; i < SETTING_PAGE_COUNT; i++) {
            /* 设置的下标 */
            int index = i + pageOffset;
            /* 文件地址 */
            String configPath = getConfigPath();

            /* setting_title 部分变量 */
            boolean hasSetting =
                    index < SETTINGS.size();

            /* 变量部分 */
            String papiTitle = String.format(SETTING_TITLE, (i + 1));
            String papiHas = String.format(HAS_SETTING, (i + 1));
            String papiSelected = String.format(SELECT_SETTING, (i + 1));

            if (!hasSetting) {
                map.put(papiTitle, StringUtils.EMPTY);
                map.put(papiHas, getPlugin().getConfigManager().getYmlValue(configPath, String.format(CUSTOM_PAPI_HAS_SETTING_PATH, (i + 1), "UnEnable")));
                map.put(papiSelected, StringUtils.EMPTY);
                continue;
            }

            map.put(papiTitle, SETTINGS.get(index).getTitle());
            map.put(papiHas, getPlugin().getConfigManager().getYmlValue(configPath, String.format(CUSTOM_PAPI_HAS_SETTING_PATH, (i + 1), "Enable")));
            map.put(papiSelected, getPlugin().getConfigManager().getYmlValue(configPath,
                    String.format(CUSTOM_PAPI_SELECT_SETTING_PATH, (i + 1), index == SELECT_SETTING_INDEX ? "Enable": "UnEnable")));
        }
        return map;
    }



}
