package com.magicrealms.magicplayer.core.avatar;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magicplayer.core.MagicPlayer;
import lombok.Getter;

import java.util.*;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_AVATAR;

/**
 * @author Ryan-0916
 * @Desc 头像配置文件加载器
 * 用于加载并存储玩家头像格式
 * @date 2025-05-02
 */
public class AvatarConfigLoader {

    private final ConfigManager configManager;

    @Getter
    private final Map<Integer, AvatarFormat> formats = new HashMap<>();

    public AvatarConfigLoader(MagicPlayer plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllFormatConfig();
    }

    private void loadAllFormatConfig() {
        configManager
                .getYmlSubKeys(YML_AVATAR, "Layout", false)
                .ifPresent(keys -> keys.forEach(this::loadFormatConfig));
    }

    private void loadFormatConfig(String configKey) {
        String basePath = "Layout." + configKey;
        String layOutPath = basePath + ".Layout";
        String idPath = basePath + ".Id";
        String fontPath = basePath + ".Font";
        String offsetCharPath = basePath + ".OffsetChar";
        if (configManager.containsYmlKey(YML_AVATAR, layOutPath) &&
                configManager.containsYmlKey(YML_AVATAR, idPath)) {
            int id = configManager.getYmlValue(YML_AVATAR, idPath, -1, ParseType.INTEGER);
            if (id < 0) { return; }
            configManager.getYmlListValue(YML_AVATAR, layOutPath).ifPresent(
                    layout -> formats.put(id, AvatarFormat.builder()
                                    .id(id)
                                    .format(new ArrayList<>(layout))
                                    .offsetChar(configManager.getYmlValue(YML_AVATAR, offsetCharPath))
                                    .font(configManager.getYmlValue(YML_AVATAR, fontPath, "minecraft:default", ParseType.STRING))
                            .build())
            );
        }
    }
    
}
