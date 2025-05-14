package com.magicrealms.magicplayer.core.avatar.frame;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magicplayer.api.setting.Setting;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.core.menu.AvatarFrameMenu;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

/**
 * @author Ryan-0916
 * @Desc 头像框加载器
 * @date 2025-05-12
 */
public class AvatarFrameLoader {

    private final ConfigManager configManager;

    private final Setting avatarFrameSetting;

    @Getter
    private final List<AvatarFrameTemplate> frames = new ArrayList<>();

    public AvatarFrameLoader(BukkitMagicPlayer plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllTemplate();
        if (!configManager.getYmlValue(YML_AVATAR_FRAME, "Setting.Enable", false, ParseType.BOOLEAN)) {
            avatarFrameSetting = null;
            return;
        }
        avatarFrameSetting = Setting.builder().weight(configManager.getYmlValue(YML_AVATAR_FRAME, "Setting.Weight", 0, ParseType.INTEGER))
                .title(configManager.getYmlValue(YML_AVATAR_FRAME, "Setting.Title"))
                .name(configManager.getYmlValue(YML_AVATAR_FRAME, "Setting.Name"))
                .description(configManager.getYmlValue(YML_AVATAR_FRAME, "Setting.Desc"))
                .clickAction(AvatarFrameMenu::new)
                .build();
    }

    private void loadAllTemplate() {
        configManager.getYmlSubKeys(YML_AVATAR_FRAME, "Template", false)
                .ifPresent(keys -> keys.forEach(this::loadTemplate));
        frames.sort(Comparator.comparingInt(AvatarFrameTemplate::getWeight));
    }

    private void loadTemplate(String configKey) {
        String basePath = "Template." + configKey;
        String idPath = basePath + ".Id";
        String profilePath = basePath + ".Profile";
        String settingPath = basePath + ".Setting";
        String framePath = basePath + ".Frame";
        String weightPath = basePath + ".Weight";
        String activeDisplayPath = basePath + ".ActiveDisplay";
        String disabledDisplayPath = basePath + ".DisabledDisplay";
        if (configManager.containsYmlKey(YML_AVATAR_FRAME, idPath)) {
            int id = configManager.getYmlValue(YML_AVATAR_FRAME, idPath, -1, ParseType.INTEGER);
            if (id < 0) { return; }
            frames.add(AvatarFrameTemplate.builder()
                    .id(id)
                    .profileFrame(configManager.getYmlValue(YML_AVATAR_FRAME, profilePath))
                    .settingFrame(configManager.getYmlValue(YML_AVATAR_FRAME, settingPath))
                    .frame(configManager.getYmlValue(YML_AVATAR_FRAME, framePath))
                    .weight(configManager.getYmlValue(YML_AVATAR_FRAME, weightPath, 0, ParseType.INTEGER))
                    .activeItem(ItemUtil.getItemStackByConfig(configManager, YML_AVATAR_FRAME, activeDisplayPath))
                    .disabledItem(ItemUtil.getItemStackByConfig(configManager, YML_AVATAR_FRAME, disabledDisplayPath))
                    .build());
        }
    }

    public Optional<Setting> getAvatarFrameSetting() {
        return Optional.ofNullable(avatarFrameSetting);
    }
}
