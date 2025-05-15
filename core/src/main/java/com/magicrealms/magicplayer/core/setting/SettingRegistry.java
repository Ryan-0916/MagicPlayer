package com.magicrealms.magicplayer.core.setting;

import com.magicrealms.magicplayer.api.setting.Setting;
import com.magicrealms.magicplayer.api.setting.ISettingRegistry;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 设置模块注册器
 * @date 2025-05-14
 */
@Getter
public class SettingRegistry implements ISettingRegistry {

    private final List<Setting> settings = new ArrayList<>();

    @Override
    public void registry(Setting setting) {
        settings.add(setting);
        sort();
    }

    @Override
    public void destroy(Setting setting) {
        settings.remove(setting);
        sort();
    }

    public void sort() {
        settings.sort(Comparator.comparingInt(Setting::getWeight));
    }

}
