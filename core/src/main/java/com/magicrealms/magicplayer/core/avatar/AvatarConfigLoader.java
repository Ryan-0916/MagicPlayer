package com.magicrealms.magicplayer.core.avatar;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import lombok.Getter;

import java.util.*;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_AVATAR;

/**
 * @author Ryan-0916
 * @Desc 头像配置文件加载器
 * @date 2025-05-02
 */
public class AvatarConfigLoader {

    @Getter
    private final Map<Integer, AvatarTemplate> templates = new HashMap<>();

    private final ConfigManager configManager;

    public AvatarConfigLoader(BukkitMagicPlayer plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllTemplate();
    }

    private void loadAllTemplate() {
        configManager
                .getYmlSubKeys(YML_AVATAR, "Template", false)
                .ifPresent(keys -> keys.forEach(this::loadTemplate));
    }

    private void loadTemplate(String configKey) {
        String basePath = "Template." + configKey;
        String idPath = basePath + ".Id";
        String offsetPath = basePath + ".Offset";
        String isDefaultPath = basePath + ".IsDefault";
        String fontPath = basePath + ".Font";
        String layOutPath = basePath + ".Layout";
        if (!configManager.containsYmlKey(YML_AVATAR,
                idPath)) {
            return;
        }
        int id = configManager.getYmlValue(YML_AVATAR, idPath, -1, ParseType.INTEGER);
        if (id < 0) {
            return;
        }
        configManager.getYmlListValue(YML_AVATAR, layOutPath).ifPresent(
                layout -> templates.put(id, AvatarTemplate.builder()
                        .id(id)
                        .offset(configManager.getYmlValue(YML_AVATAR, offsetPath, 0, ParseType.INTEGER))
                        .isDefault(configManager.getYmlValue(YML_AVATAR, isDefaultPath, false, ParseType.BOOLEAN))
                        .font(configManager.getYmlValue(YML_AVATAR, fontPath))
                        .format(layout)
                        .build())
        );
    }
    
}
