package com.magicrealms.magicplayer.core.menu;

import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.holder.PageMenuHolder;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magicplayer.core.BukkitMagicPlayer;
import com.magicrealms.magicplayer.api.player.PlayerData;
import com.magicrealms.magicplayer.api.setting.Setting;
import com.magicrealms.magicplayer.api.setting.SettingParam;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_LANGUAGE;
import static com.magicrealms.magicplayer.common.MagicPlayerConstant.YML_SETTING_MENU;

/**
 * @author Ryan-0916
 * @Desc 设置菜单
 * @date 2025-05-10
 */
public class SettingMenu extends PageMenuHolder {

    private final List<Setting> SETTINGS;

    private final PlayerData HOLDER_DATA;

    private final int PAGE_COUNT;

    private final String ICON_DISPLAY = "Icons.%s.Display";

    public SettingMenu(Player player, @Nullable Runnable backRunnable) {
        super(BukkitMagicPlayer.getInstance(), player, YML_SETTING_MENU,
                "A########BC##HJKKKCC###JKKKDEFG#JKKK####IJKKK", backRunnable);
        this.SETTINGS = BukkitMagicPlayer.getInstance()
                .getSettingRegistry().getSettings();
        /* 获取菜单布局中每页显示的设置数量 */
        this.PAGE_COUNT = StringUtils
                .countMatches(super.getLayout(), "J");
        /* 玩家的个人信息 */
        this.HOLDER_DATA = BukkitMagicPlayer.getInstance()
                .getPlayerDataRepository()
                .queryByPlayer(player);
        super.setMaxPage(PAGE_COUNT <= 0 ? 1 :
                this.SETTINGS.size() % PAGE_COUNT == 0 ?
                        this.SETTINGS.size() / PAGE_COUNT : this.SETTINGS.size() / PAGE_COUNT + 1);
        asyncOpenMenu();
    }

    @Override
    protected void handleMenuUnCache(String layout) {
        int size =  layout.length();
        /* 当前显示的下标 */
        int appearIndex = ((super.getPage() - 1) * PAGE_COUNT) - 1;
        for (int i = 0; i < size; i++){
            switch (layout.charAt(i)) {
                case 'A' -> super.setCheckBoxSlot(i, super.getBackMenuRunnable() != null);
                case 'J' -> {
                    if (SETTINGS.size() > ++appearIndex) {
                        setSetting(i, layout.charAt(i), SETTINGS.get(appearIndex));
                    } else {
                        super.setItemSlot(i, ItemUtil.AIR);
                    }
                }
                case 'K' -> {
                    if (SETTINGS.size() > appearIndex) {
                        setSetting(i, layout.charAt(i), SETTINGS.get(appearIndex));
                    } else {
                        super.setItemSlot(i, ItemUtil.AIR);
                    }
                }
                case 'B', 'C' -> setHead(i, layout.charAt(i));
                case 'H' -> super.setButtonSlot(i, !(super.getPage() > 1));
                case 'I' -> super.setButtonSlot(i, !(super.getPage() < super.getMaxPage()));
                default -> super.setItemSlot(i);
            }
        }
    }

    private void setHead(int slot, Character key) {
        CompletableFuture.runAsync(() -> {
            String path = String.format(ICON_DISPLAY, key);
            ItemStack itemStack = key.equals('B') ?
                    ItemUtil.setItemStackByConfig(HOLDER_DATA.getHeadStack().clone(),
                            super.getPlugin().getConfigManager(),
                            super.getConfigPath(), path
                            , getPlayer()) :
                    ItemUtil.getItemStackByConfig(super.getPlugin().getConfigManager(),
                            super.getConfigPath(), path
                            , getPlayer());
            setItemSlot(slot, itemStack);
        });
    }

    private void setSetting(int i, char c, Setting setting) {
        super.setItemSlot(i, ItemUtil.getItemStackByConfig(getPlugin().getConfigManager()
            , getConfigPath(), String.format(ICON_DISPLAY, c), Map.of(
                        "setting_name", setting.getName(),
                        "setting_desc", setting.getDescription())
        ));
    }

    @Override
    protected LinkedHashMap<String, String> processHandTitle(LinkedHashMap<String, String> title) {
        return title
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, (entry)
                        -> StringUtil.replacePlaceholders(entry.getValue(), createPlaceholders()), (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }

    @SuppressWarnings("DuplicatedCode")
    private Map<String, String> createPlaceholders() {
        Map<String, String> map = new HashMap<>(PAGE_COUNT * 2);
        /* 设置的 Title */
        final String SETTING_TITLE = "setting_title_%s";
        /* 是否拥有设置 */
        final String HAS_SETTING = "has_setting_%s";
        /* 设置 Format */
        final String SETTING_Format = "setting_format_%s";
        /* 拥有设置的 YML 自定义 Papi Path */
        final String CUSTOM_PAPI_HAS_SETTING_PATH = "CustomPapi" +
                ".HasSetting_%s.%s";
        /* 设置的 Format YML 自定义 Papi Path */
        final String CUSTOM_PAPI_SETTING_FORMAT_PATH = "CustomPapi" +
                ".SettingFormat_%s.%s";
        int pageOffset = (super.getPage() - 1) * PAGE_COUNT;
        for (int i = 0; i < PAGE_COUNT; i++) {
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
            String papiFormat = String.format(SETTING_Format, (i + 1));
            if (!hasSetting) {
                map.put(papiTitle, StringUtils.EMPTY);
                map.put(papiHas, getPlugin().getConfigManager().getYmlValue(configPath, String.format(CUSTOM_PAPI_HAS_SETTING_PATH, (i + 1), "UnEnable")));
                map.put(papiFormat, getPlugin().getConfigManager().getYmlValue(configPath, String.format(CUSTOM_PAPI_SETTING_FORMAT_PATH, (i + 1), "UnEnable")));
                continue;
            }
            map.put(papiTitle, SETTINGS.get(index).getTitle());
            map.put(papiHas, getPlugin().getConfigManager().getYmlValue(configPath, String.format(CUSTOM_PAPI_HAS_SETTING_PATH, (i + 1), "Enable")));
            map.put(papiFormat, getPlugin().getConfigManager().getYmlValue(configPath, String.format(CUSTOM_PAPI_SETTING_FORMAT_PATH, (i + 1), "Enable")));
        }
        return map;
    }

    @Override
    public void topInventoryClickEvent(InventoryClickEvent event, int slot) {
        if (!super.tryCooldown(slot, super.getPlugin().getConfigManager()
                .getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Error.ButtonCooldown"))) {
            return;
        }
        char c = super.getLayout().charAt(slot);
        asyncPlaySound("Icons." + c + ".Display.Sound");
        switch (c) {
            case 'A'-> super.backMenu();
            case 'H' -> super.changePage(- 1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                super.handleMenu(super.getLayout());
                super.asyncUpdateTitle();
            });
            case 'I' -> super.changePage(1, b -> {
                asyncPlaySound(b ? "Icons." + c + ".ActiveDisplay.Sound" : "Icons." + c + ".DisabledDisplay.Sound");
                if (!b) return;
                super.handleMenu(super.getLayout());
                super.asyncUpdateTitle();
            });
            case 'J', 'K' -> clickSetting(slot, c);
        }
    }

    private void clickSetting(int slot, Character key) {
        int index = StringUtils
                .countMatches(super.getLayout().substring(0, slot), "J");
        if (key.equals('K')) {
            index--;
        }
        if (index < 0) return;
        int selectedIndex = (super.getPage() - 1) * PAGE_COUNT + index;
        SETTINGS.get(selectedIndex).getClickAction().accept(
                SettingParam.of(super.getPlayer(),
                        SETTINGS,
                super.getPage(),
                PAGE_COUNT,
                selectedIndex, this::asyncOpenMenu));
    }
}
