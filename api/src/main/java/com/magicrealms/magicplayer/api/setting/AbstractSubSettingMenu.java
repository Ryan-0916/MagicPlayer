package com.magicrealms.magicplayer.api.setting;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.core.holder.PageMenuHolder;
import com.magicrealms.magicplayer.api.MagicPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_SETTING_MENU;

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
                                  String configPath,
                                  String defLayout,
                                  SettingParam param) {
        super(plugin, param.player(), configPath, defLayout, param.backRunnable());
        this.SETTINGS = param.settings();
        this.SETTING_PAGE_COUNT = param.settingCount();
        this.SETTING_PAGE = param.page();
        this.SELECT_SETTING_INDEX = param.selectedIndex();
    }

    @SuppressWarnings("DuplicatedCode")
    protected Map<String, String> createPlaceholders() {
        Map<String, String> map = new HashMap<>();
        /* 变量部分处理 */
        final String TITLE = "setting_title_%s",  // 设置标题
                HAS = "has_setting_%s", // 存在设置
                SELECTED = "selected_setting_%s", // 选中设置
                TITLE_FORMAT = "title_format_%s"; // 设置标题格式化
        /* 自定义 Papi Path */
        final String CUSTOM_PAPI_HAS = "CustomPapi.HasSetting_%s.%s", // 存在设置,
                CUSTOM_PAPI_SELECTED = "CustomPapi.SelectedSetting_%s.%s", // 选中设置
                CUSTOM_PAPI_TITLE_FORMAT = "CustomPapi.TitleFormat_%s.%s"; // 设置标题格式化
        int pageOffset = (SETTING_PAGE - 1) * SETTING_PAGE_COUNT;
        for (int i = 0; i < SETTING_PAGE_COUNT; i++) {
            int index = i + pageOffset // 设置的下标
                    , settingSort = i + 1; // 设置的顺序;
            /* 文件管理器 */
            ConfigManager configManager = MagicPlayer.getInstance().getConfigManager();
            /* 文件地址 */
            String configPath = YML_SETTING_MENU;
            /* 是否存在此下标的邮件 */
            boolean hasSetting = index < SETTINGS.size();
            /* 变量部分处理 */
            String papiTitle = String.format(TITLE, settingSort),
                    papiHas = String.format(HAS, settingSort),
                    papiSelected = String.format(SELECTED, settingSort),
                    papiTitleFormat = String.format(TITLE_FORMAT, settingSort);
            /* 启用变量 */
            String enablePath = hasSetting ? "Enable" : "UnEnable";
            /* 是否存在变量 */
            map.put(papiHas, configManager.getYmlValue(configPath,
                    String.format(CUSTOM_PAPI_HAS, settingSort, enablePath)));
            /* Title格式化变量 */
            map.put(papiTitleFormat, configManager.getYmlValue(configPath,
                    String.format(CUSTOM_PAPI_TITLE_FORMAT, settingSort, enablePath)));
            /* Title部分变量 */
            map.put(papiTitle, hasSetting ? SETTINGS.get(index).getTitle() : StringUtils.EMPTY);
            /* 是否选中部分变量 */
            map.put(papiSelected, configManager.getYmlValue(configPath, String.format(CUSTOM_PAPI_SELECTED, settingSort, index == SELECT_SETTING_INDEX ? "Enable": "UnEnable")));
        }
        return map;
    }

}
