package com.magicrealms.magicplayer.core.menu.sort;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 相框排序
 * 定义了四种不同的排序方式，并支持循环切换
 * @date 2025-05-16
 */
@Getter
public enum FrameSort {

    /* 按出版时间从旧到新排序 (值: 0) */
    PUBLISH_TIME_OLDEST(0, "OldestDisplay"),

    /* 按出版时间从新到旧排序 (值: 1) */
    PUBLISH_TIME_NEWEST(1, "NewestDisplay"),

    /* 按已解锁状态排序 (值: 2) */
    UNLOCKED(2, "UnlockDisplay"),

    /* 按未解锁状态排序 (值: 3) */
    LOCKED(3, "LockDisplay");

    /* 枚举值对应的整数值 */
    private final int value;

    /* Menu YML 中对应的 Path */
    private final String path;

    FrameSort(int value, String path) {
        this.value = value;
        this.path = path;
    }

    /**
     * 获取下一个排序方式（循环切换）
     * @return 下一个排序方式枚举值
     */
    public FrameSort next() {
        FrameSort[] values = FrameSort.values();
        int nextOrdinal = (this.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }

    public ItemStack getItemSlot(ConfigManager configManager,
                                 String configPath,
                                 char key) {
        String path = String.format("Icons.%s.%s", key, this.path);
        return ItemUtil.getItemStackByConfig(configManager, configPath, path);
    }
}
