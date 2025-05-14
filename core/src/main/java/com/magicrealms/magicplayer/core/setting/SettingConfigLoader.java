package com.magicrealms.magicplayer.core.setting;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.utils.EnumUtil;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

/**
 * @author Ryan-0916
 * @Desc 设置配置文件加载器
 * @date 2025-05-10
 */
public class SettingConfigLoader {

    private final ConfigManager configManager;

    @Getter
    private final List<Setting> settings = new ArrayList<>();

    public SettingConfigLoader(BukkitMagicPlayer plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllSettingConfig();
    }

    private void loadAllSettingConfig() {
        configManager
                .getYmlSubKeys(YML_SETTING, "Setting", false)
                .ifPresent(keys -> keys.forEach(this::loadSettingConfig));
        settings.sort(Comparator
                .comparingInt(Setting::getWeight));
    }

    private void loadSettingConfig(String configKey) {
        EnumUtil.getMatchingEnum(SettingType.class, configKey)
                .ifPresent( type -> {
                            String basePath = "Setting." + configKey;
                            String weightPath = basePath + ".Weight";
                            String titlePath = basePath + ".Title";
                            String namePath = basePath + ".Name";
                            String descPath = basePath + ".Desc";
                            settings.add(Setting.builder()
                                    .type(type)
                                    .name(configManager.getYmlValue(YML_SETTING, namePath))
                                    .title(configManager.getYmlValue(YML_SETTING, titlePath))
                                    .description(configManager.getYmlValue(YML_SETTING, descPath))
                                    .weight(configManager.getYmlValue(YML_SETTING, weightPath, 0, ParseType.INTEGER))
                                    .build());
                        }
                );
    }
}
