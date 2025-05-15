package com.magicrealms.magicplayer.core.frame;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magicplayer.api.setting.Setting;
import com.magicrealms.magicplayer.api.setting.SettingParam;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 头像框加载器
 * @date 2025-05-12
 */
public class FrameLoader {

    private final ConfigManager configManager;

    private final Setting avatarFrameSetting;

    @Getter
    private final List<FrameTemplate> frames = new ArrayList<>();

    private final String configPath;

    public FrameLoader(BukkitMagicPlayer plugin, String configPath, Consumer<SettingParam> clickAction) {
        this.configManager = plugin.getConfigManager();
        this.configPath = configPath;
        this.loadAllTemplate();


        /* 注册 Setting */
        if (!configManager.getYmlValue(configPath, "Setting.Enable", false, ParseType.BOOLEAN)) {
            avatarFrameSetting = null;
            return;
        }
        avatarFrameSetting = Setting.builder().weight(configManager.getYmlValue(configPath, "Setting.Weight", 0, ParseType.INTEGER))
                .title(configManager.getYmlValue(configPath, "Setting.Title"))
                .name(configManager.getYmlValue(configPath, "Setting.Name"))
                .description(configManager.getYmlValue(configPath, "Setting.Desc"))
                .clickAction(clickAction)
                .build();
    }

    private void loadAllTemplate() {
        configManager.getYmlSubKeys(configPath, "Template", false)
                .ifPresent(keys -> keys.forEach(this::loadTemplate));
        frames.sort(Comparator.comparingInt(FrameTemplate::getWeight));
    }

    private void loadTemplate(String configKey) {
        String basePath = "Template." + configKey;
        String idPath = basePath + ".Id";
        String profilePath = basePath + ".Profile";
        String settingPath = basePath + ".Setting";
        String framePath = basePath + ".Frame";
        String weightPath = basePath + ".Weight";
        String permission = basePath + ".Permission";
        String unlockDisplayPath = basePath + ".UnlockDisplay";
        String lockDisplayPath = basePath + ".LockDisplay";
        if (configManager.containsYmlKey(configPath, idPath)) {
            int id = configManager.getYmlValue(configPath, idPath, -1, ParseType.INTEGER);
            if (id < 0) { return; }
            frames.add(FrameTemplate.builder()
                    .id(id)
                    .profileFrame(configManager.getYmlValue(configPath, profilePath))
                    .settingFrame(configManager.getYmlValue(configPath, settingPath))
                    .frame(configManager.getYmlValue(configPath, framePath))
                    .weight(configManager.getYmlValue(configPath, weightPath, 0, ParseType.INTEGER))
                    .permission(configManager.getYmlValue(configPath, permission))
                    .unlockItem(ItemUtil.getItemStackByConfig(configManager, configPath, unlockDisplayPath))
                    .lockItem(ItemUtil.getItemStackByConfig(configManager, configPath, lockDisplayPath))
                    .build());
        }
    }

    public Optional<Setting> getAvatarFrameSetting() {
        return Optional.ofNullable(avatarFrameSetting);
    }
}
