package com.magicrealms.magicplayer.core.avatar.frame;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.*;

/**
 * @author Ryan-0916
 * @Desc 头像框加载器
 * @date 2025-05-12
 */
public class AvararFrameConfigLoader {

    private final ConfigManager configManager;

    @Getter
    private final List<AvatarFrame> frames = new ArrayList<>();

    public AvararFrameConfigLoader(BukkitMagicPlayer plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllFrameConfig();
    }

    private void loadAllFrameConfig() {
        configManager
                .getYmlSubKeys(YML_AVATAR_FRAME, "Frame", false)
                .ifPresent(keys -> keys.forEach(this::loadFormatConfig));
        frames.sort(Comparator
                .comparingInt(AvatarFrame::getWeight));
    }

    private void loadFormatConfig(String configKey) {
        String basePath = "Frame." + configKey;
        String idPath = basePath + ".Id";
        String profilePath = basePath + ".Profile";
        String settingPath = basePath + ".Setting";
        String framePath = basePath + ".Frame";
        String weightPath = basePath + ".Weight";
        String activeDisplayPath = basePath + ".ActiveDisplay";
        String disabledDisplayPath = basePath + ".DisabledDisplay";
        if (configManager.containsYmlKey(YML_AVATAR_FRAME, idPath)) {
            int id = configManager
                    .getYmlValue(YML_AVATAR_FRAME, idPath, -1, ParseType.INTEGER);
            if (id < 0) { return; }
            frames.add(AvatarFrame.builder()
                    .id(id)
                    .profileFrame(configManager.getYmlValue(YML_AVATAR_FRAME, profilePath))
                    .settingFrame(configManager.getYmlValue(YML_AVATAR_FRAME, settingPath))
                    .frame(configManager.getYmlValue(YML_AVATAR_FRAME, framePath))
                    .weight(configManager.getYmlValue(YML_SETTING, weightPath, 0, ParseType.INTEGER))
                    .activeItem(ItemUtil.getItemStackByConfig(configManager, YML_AVATAR_FRAME, activeDisplayPath))
                    .disabledItem(ItemUtil.getItemStackByConfig(configManager, YML_AVATAR_FRAME, disabledDisplayPath))
                    .build());
        }
    }
}
